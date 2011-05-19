/*
 *	  This file is part of the Bytewalla Project
 *    More information can be found at "http://www.tslab.ssvl.kth.se/csd/projects/092106/".
 *    
 *    Copyright 2009 Telecommunication Systems Laboratory (TSLab), Royal Institute of Technology, Sweden.
 *    
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *    
 */

package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.Bundle;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleDaemon;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ContactEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ContactUpEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkStateChangeRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Contact;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.ContactManager;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.TCPConvergenceLayer.TCPLinkParams;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.MsgBlockingQueue;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.List;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.SerializableByteBuffer;
import android.util.Log;

/**
 * "Helper class (and thread) that manages an established connection with a peer
 * daemon" [DTN2].
 * 
 * @author María José Peroza Marval (mjpm@kth.se)
 */

public abstract class CLConnection extends CLInfo implements Runnable {

	/**
	 * Unique identifier according to Java Serializable specification
	 */
	private static final long serialVersionUID = -5438596101732561849L;

	/**
	 * Internal thread
	 */
	private Thread thread_;

	/**
	 * TAG for Android Logging mechanism
	 */
	private static final String TAG = "CLConnection";

	/**
	 * Default size for the block buffer
	 */
	// public static int DEFAULT_BLOCK_BUFFER_SIZE = 4096;
	public static int DEFAULT_BLOCK_BUFFER_SIZE = 16384;
	// public static int DEFAULT_BLOCK_BUFFER_SIZE = 32768;

	/**
	 * Default length of the TCP segment
	 */
	// public static int DEFAULT_SEGMENT_LENGTH = 1024;
	// public static int DEFAULT_SEGMENT_LENGTH = 4096;
	public static int DEFAULT_SEGMENT_LENGTH = 16384;

	/**
	 * Default size for the receiving and sending buffer
	 */
	public static int DEFAULT_SEND_RECEIVE_BUFFER_SIZE = 32768;

	/**
	 * Constructor.
	 */
	public CLConnection(ConnectionConvergenceLayer cl, LinkParams params,
			boolean active_connector) throws OutOfMemoryError {

		thread_ = new Thread(this);

		
		/*Link generated_link = new Link(cl);
		contact_ = new Contact(generated_link);
		generated_link.set_contact(contact_);
		generated_link.set_cl_info(params);
		contact_.set_cl_info(this);*/
	    contact_ = null;
		contact_up_ = false;
		cmdqueue_ = new MsgBlockingQueue<CLMsg>(20);
		cl_ = cl;
		params_ = params; 
		active_connector_ = active_connector;
		poll_timeout_ = -1;
		contact_broken_ = false;

		incoming_ = new List<IncomingBundle>();
		inflight_ = new List<InFlightBundle>();

		try {
			sendbuf_ = new SerializableByteBuffer(params_.sendbuf_len());
			recvbuf_ = new SerializableByteBuffer(params_.recvbuf_len());
		} catch (OutOfMemoryError e) {
			thread_ = null;
			contact_ = null;
			contact_up_ = false;
			cmdqueue_ = null;
			cl_ = null;
			params_ = null;
			active_connector_ = active_connector;
			contact_broken_ = true;
			incoming_ = null;
			inflight_ = null;

		}

	}

	/**
	 * Attach to the given contact.
	 */
	void set_contact(Contact contact) {
		contact_ = contact;
	}

	/**
	 * Main run loop.
	 */
	public void run() {

		// There are 2 cases when we get here
		// 1. The TCPServer creates the socket and gives the socket here
		// 2. The Contact manager opencontact() to the available link

		TCPLinkParams params = (TCPLinkParams) (params_);
		assert (params != null) : "CLConnection : run, params are null";

		poll_timeout_ = params.data_timeout();

		if (params.keepalive_interval() != 0
				&& (params.keepalive_interval() * 1000) < params.data_timeout()) {
			poll_timeout_ = 2 * params.keepalive_interval() * 1000;
		}
		if (contact_broken_) {
			Log.d(TAG, "contact_broken set during initialization");
			return;
		}

		if (active_connector_) {
			Log.d(TAG, "trying to connect");
			try {
				connect();
			} catch (ConnectionException e) {
				String text = String.format(
						"connection attempt to %s:%s failed...",
						params.remote_addr_, params.remote_port_);
				Log.i(TAG, text);
				break_contact(ContactEvent.reason_t.BROKEN);
				return;
			}
		}

		while (true) {
			if (contact_broken_) {
				Log.d(TAG, "contact_broken set, exiting main loop");
				return;
			}

//			Log.d(TAG, "CLConnection is still running in the main loop, cmdqueue_ size is " + cmdqueue_.size());
			// "check the command queue coming in from the bundle daemon
			// if any arrive, we continue to the top of the loop to check
			// contact_broken and then process any other commands before
			// checking for data to/from the remote side" [DTN2].
			if (cmdqueue_.size() != 0) {
				process_command();
				continue;
			}

			int timeout = 10;

			if (contact_up_) {
				// "send any data there is to send. if something was sent
				// out and there's still more to go, we'll call poll() with a
				// zero timeout so we can read any data there is to
				// consume, then return to send another chunk" [DTN2].
				boolean more_to_send = send_pending_data();
				timeout = more_to_send ? 0 : poll_timeout_;
			}

			// "check again here for contact broken since we don't want to
			// poll if the socket's been closed" [DTN2]
			if (contact_broken_) {
				Log.d(TAG, "contact_broken set, exiting main loop");
				return;
			}

			handle_poll_activity(timeout);
		}
	}

	/*
	 * Utility functions, all virtual so subclasses could override them
	 */

	/**
	 * Post a ContactUpEvent in the Daemon
	 */
	public void contact_up() {

		Log.d(TAG, "contact_up");
		assert (contact_ != null) : "CLConnection : contact_up, contact_ is null";

		assert (!contact_up_) : "CLConnection : contact_up, contact_up is already true";
		contact_up_ = true;

		BundleDaemon Daemon = BundleDaemon.getInstance();
		Daemon.post(new ContactUpEvent(contact_));

	}

	/**
	 * Break a contact. Part of the termination of a connection.
	 */
	public void break_contact(ContactEvent.reason_t reason) {

		contact_broken_ = true;

		Log.d(TAG, "break_contact: " + reason.getCaption());

		if (reason != ContactEvent.reason_t.BROKEN) {
			disconnect();
		}

		// "if the connection isn't being closed by the user, we need to
		// notify the daemon that either the contact ended or the link
		// became unavailable before a contact began.
		//
		// we need to check that there is in fact a contact, since a
		// connection may be accepted and then break before establishing a
		// contact" [DTN2]
		if ((reason != ContactEvent.reason_t.USER) && (contact_ != null)) {
			BundleDaemon Daemon = BundleDaemon.getInstance();
			Daemon.post(new LinkStateChangeRequest(contact_.link(),
					Link.state_t.CLOSED, reason));
		}
	}

	/**
	 * Check for the type of message received from the Bundle Daemon and decide
	 * what to do next.
	 */
	void process_command() {

		Log.e(TAG, "receiving command from Bundle Daemon");
		CLMsg msg;
		try {
			msg = cmdqueue_.take();
	
	
		switch (msg.type_) {
		case CLMSG_BUNDLES_QUEUED:
			Log.d(TAG, "processing CLMSG_BUNDLES_QUEUED");
			handle_bundles_queued();
			break;

		case CLMSG_CANCEL_BUNDLE:
			Log.d(TAG, "processing CLMSG_CANCEL_BUNDLE");
			handle_cancel_bundle(msg.bundle_);
			break;

		case CLMSG_BREAK_CONTACT:
			Log.d(TAG, "processing CLMSG_BREAK_CONTACT");
			break_contact(ContactEvent.reason_t.USER);
			break;
		default:
			Log.d(TAG, "invalid CLMsg typecode " + msg.type_);
		}

		} catch (InterruptedException e) {
			Log.e(TAG, "Interupt Exception in processs_command");
		}
	}

	/**
	 * Start the CLConnection Thread.
	 */
	public void start() {
		thread_.start();
	}

	/**
	 * Stop the CLConnection Thread.
	 */
	public void stop() {
		Log.d(TAG, "stopping thread in CLConnection");
		if (thread_ != null) {
			Thread moribund = thread_;
			thread_ = null;
			moribund.interrupt();
		}
	}

	/**
	 * Check if the thread is still running.
	 */
	public boolean isAlive() {

		if (thread_ != null)
			return thread_.isAlive();
		else
			return false;

	}

	/**
	 * Find or create an opportunistic link for a contact.
	 */
	boolean find_contact(EndpointID peer_eid) {

		if (contact_ != null) {
			Log.d(TAG, "CLConnection.find_contact: contact already exists");
			return true;
		}

		/*
		 * "Now we may need to find or create an appropriate opportunistic link
		 * for the connection.
		 * 
		 * First, we check if there's an idle (i.e. UNAVAILABLE) link to the
		 * remote eid. We explicitly ignore the nexthop address, since that can
		 * change (due to things like TCP/UDP port number assignment), but we
		 * pass in the remote eid to match for a link.
		 * 
		 * If we can't find one, then we create a new opportunistic link for the
		 * connection" [DTN2].
		 */
		assert (nexthop_ != "") : "CLConnection : find_contact, nexthop_ is empty"; // the
		// derived
		// class
		// must
		// have
		// set
		// the
		// nexthop
		// in
		// the
		// constructor
		BundleDaemon Daemon = BundleDaemon.getInstance();
		ContactManager cm = Daemon.contactmgr();

		boolean new_link = false;
		Link link = cm.find_link_to(peer_eid);

		if (link == null || link.contact() != null) {
			if (link != null) {
				Log.w(TAG, "in-use opportunistic link " + link);
			}

			link = cm.new_opportunistic_link(cl_, nexthop_, peer_eid);
			if (link == null) {
				Log.d(TAG, "failed to create opportunistic link");
				return false;
			}

			new_link = true;
			Log.d(TAG, "created new opportunistic link " + link);
		}

		assert (link != null);

		link.lock().lock();
		try {
			if (!new_link) {
				assert (link.contact() == null);
				link.set_nexthop(nexthop_);
				Log.d(TAG, "found idle opportunistic link " + link);
			}

			// The link should not be marked for deletion because the
			// ContactManager is locked.
			assert (!link.isdeleted()) : "CLConnection : find_contact, link is deleted";

			assert (link.cl_info() != null);
			assert (!link.isopen());

			contact_ = new Contact(link);
			contact_.set_cl_info(this);
			link.set_contact(contact_);

			/*
			 * "Now that the connection is established, we swing the params_
			 * pointer to those of the link, since there's a chance they've been
			 * modified by the user in the past" [DTN2].
			 */
			LinkParams lparams = (LinkParams) link.cl_info();
			assert (lparams != null) : "CLConnection : find_contact, LinkParameters are null";
			params_ = lparams;
		} finally {
			link.lock().unlock();
		}
		return true;
	}

	/**
	 * Assignment function for the nexthop identifier
	 */
	public void set_nexthop(String nexthop) {
		nexthop_ = nexthop;
	}

	/**
	 * Initiate a connection to the remote side.
	 */
	abstract void connect() throws ConnectionException;

	/**
	 * "Accept a connection from the remote side. For variants that don't
	 * implement interfaces, but require a link to be configured on both ends
	 * (e.g. serial), this will never be called, so the base class simple
	 * asserts NOTREACHED"[DTN2].
	 */
	void accept() { /* NOTREACHED; */
	}

	/**
	 * Shutdown the connection
	 */
	abstract void disconnect();

	/**
	 * Handle notification that bundle(s) may be queued on the link.
	 */
	abstract void handle_bundles_queued();

	/**
	 * Handle a cancel bundle request
	 */
	abstract void handle_cancel_bundle(Bundle b);

	/**
	 * "Start or continue transmission of bundle data or cl acks. This is called
	 * each time through the main run loop. Note that in general, this function
	 * should send one "unit" of data, i.e. a chunk of bundle data, a packet,
	 * etc.
	 * 
	 * @returns true if some data was sent, which will trigger another call, or
	 *          false if the main loop should poll() on the socket before
	 *          calling again" [DTN2].
	 */
	abstract boolean send_pending_data();

	/**
	 * Handle network activity from the remote side.
	 */
	abstract void handle_poll_activity(int timeout);

	/**
	 * Enum for messages from the daemon thread to the connection thread.
	 */
	public enum clmsg_t {
		CLMSG_INVALID(0), CLMSG_BUNDLES_QUEUED(1), CLMSG_CANCEL_BUNDLE(2), CLMSG_BREAK_CONTACT(
				3);

		private static final Map<Integer, clmsg_t> lookup = new HashMap<Integer, clmsg_t>();

		static {
			for (clmsg_t s : EnumSet.allOf(clmsg_t.class))
				lookup.put(s.getCode(), s);
		}

		private int code;

		private clmsg_t(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static clmsg_t get(int code) {
			return lookup.get(code);
		}
	}

	/**
	 * Message to string conversion.
	 */
	String clmsg_to_str(clmsg_t type) {
		switch (type) {
		case CLMSG_INVALID:
			return "CLMSG_INVALID";
		case CLMSG_BUNDLES_QUEUED:
			return "CLMSG_BUNDLES_QUEUED";
		case CLMSG_CANCEL_BUNDLE:
			return "CLMSG_CANCEL_BUNDLE";
		case CLMSG_BREAK_CONTACT:
			return "CLMSG_BREAK_CONTACT";
		default:
			return "Problem";// PANIC("bogus clmsg_t");
		}
	}

	/**
	 * Class used for messages going from the daemon thread to the connection
	 * thread.
	 */
	public class CLMsg {
		public CLMsg() {
			type_ = clmsg_t.CLMSG_INVALID;

		}

		public CLMsg(clmsg_t type) {
			type_ = type;

		}

		public CLMsg(clmsg_t type, Bundle bundle) {
			type_ = type;

		}

		protected clmsg_t type_;
		protected Bundle bundle_;
	}

	protected Contact contact_; // /< Ref to the Contact
	protected boolean contact_up_; // /< Has contact_up been called
	private MsgBlockingQueue<CLMsg> cmdqueue_; // /< Daemon/CLConnection command

	// queue

	public MsgBlockingQueue<CLMsg> cmdqueue() {
		return cmdqueue_;
	}

	public void set_cmdqueue(MsgBlockingQueue<CLMsg> cmdqueue) {
		cmdqueue_ = cmdqueue;
	}

	protected ConnectionConvergenceLayer cl_; // /< ConvergenceLayer

	protected LinkParams params_; // /< Link parameters, or defaults until Link
	// is bound
	protected boolean active_connector_; // /< Should we connect() or accept()
	protected String nexthop_; // /< Nexthop identifier set by CL

	protected int poll_timeout_; // /< Timeout to wait for poll data
	protected IByteBuffer sendbuf_; // /< Buffer for outgoing data
	protected IByteBuffer recvbuf_; // /< Buffer for incoming data
	protected List<InFlightBundle> inflight_; // /< Bundles going out the wire

	public List<InFlightBundle> inflight() {
		return inflight_;
	}

	protected List<IncomingBundle> incoming_; // /< Bundles arriving on the wire

	public List<IncomingBundle> incoming() {
		return incoming_;
	}

	protected boolean contact_broken_; // /< Contact has been broken

	public boolean contact_broken() {
		return contact_broken_;
	}

	public void set_contact_broken(boolean contactBroken) {
		contact_broken_ = contactBroken;
	}

	protected AtomicInteger num_pending_; // /< Bundles pending transmission

}
