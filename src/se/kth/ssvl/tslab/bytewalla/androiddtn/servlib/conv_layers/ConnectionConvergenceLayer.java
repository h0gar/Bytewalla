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

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.Bundle;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleDaemon;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleReceivedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleSendCancelledEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleTransmittedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.event_source_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Contact;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.BufferHelper;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;
import android.util.Log;

/**
 * "All convergence layers that maintain a connection (i.e. a TCP socket) to
 * next hop peers derive from this base class. As such, it manages all
 * communication to/from the main bundle daemon thread, handles the main thread
 * of control for each connection, and dispatches to the specific CL
 * implementation to handle the actual wire protocol.
 * 
 * The design is as follows:
 * 
 * Open links contain a Connection class stored in the Contact's cl_info slot.
 * The lifetime of this object is one-to-one with the duration of the Contact
 * object. The Connection class is a thread that contains a MsgQueue for
 * commands to be sent from the bundle daemon. The commands are SEND_BUNDLE,
 * CANCEL_BUNDLE, and BREAK_CONTACT. When in an idle state, the thread blocks on
 * this queue as well as the socket or other connection object so it can be
 * notified of events coming from either the daemon or the peer node.
 * 
 * To enable backpressure, each connection has a maximum queue depth for bundles
 * that have been pushed onto the queue but have not yet been sent or registered
 * as in-flight by the CL. The state of the link is set to BUSY when this limit
 * is reached, but is re-set to AVAILABLE if By default, there is no hard limit
 * on the number of bundles that can be in-flight, instead the limit is
 * determined by the capacity of the underlying link.
 * 
 * The hardest case to handle is how to close a contact, as there is a race
 * condition between the underlying connection breaking and the higher layers
 * determining that the link should be closed. If the underlying link breaks due
 * to a timeout or goes idle for an on demand link, a ContactDownEvent is posted
 * and the thread terminates, setting the is_stopped() bit in the thread class.
 * In response to this event, the daemon will call the close_contact method. In
 * this case, the connection thread has already terminated so it is cleaned up
 * when the Contact object goes away.
 * 
 * If the link is closed by the daemon thread due to user configuration or a
 * scheduled link's open time elapsing, then close_contact will be called while
 * the connection is still open. The connection thread is informed by sending it
 * a BREAK_CONTACT command. Reception of this command closes the connection and
 * terminates, setting the is_stopped() bit when it is done. All this logic is
 * handled by the break_contact method in the Connection class.
 * 
 * Finally, for bidirectional protocols, opportunistic links can be created in
 * response to new connections arriving from a peer node" [DTN2].
 * 
 * @author María José Peroza Marval (mjpm@kth.se)
 */

public abstract class ConnectionConvergenceLayer extends ConvergenceLayer {

	/**
	 * TAG for Android Logging mechanism
	 */
	private static final String TAG = "ConnectionConvergenceLayer";

	@Override
	public boolean init_link(Link link) {

		assert (link != null);
		assert (!link.isdeleted());
		assert (link.cl_info() == null);

		String text = String.format("adding %s link %s", link.type_str(), link
				.nexthop());
		Log.d(TAG, text);

		// "Create a new parameters structure, parse the options, and store
		// them in the link's cl info slot" [DTN2].
		LinkParams params = new_link_params();

		// "Try to parse the link's next hop, but continue on even if the
		// parse fails since the hostname may not be resolvable when we
		// initialize the link. Each subclass is responsible for
		// re-checking when opening the link"[DTN2].
		parse_nexthop(link, params);

		if (!finish_init_link(link, params)) {
			Log.e(TAG, "error in finish_init_link");
			return false;
		}

		link.set_cl_info(params);

		return true;

	}

	@Override
	public void delete_link(Link link) {

		assert (link != null) : "ConnectionConvergenceLayer : delete_link, link is null";
		assert (!link.isdeleted());

		Log.d(TAG, "deleting link " + link.name());

		if (link.isopen() || link.isopening()) {
			String text = String.format(
					"link %s open, deleting link state when contact closed",
					link.name());
			Log.d(TAG, text);
			return;
		}

		assert (link.contact() == null) : "ConnectionConvergenceLayer : delete_link, contact is not null";
		assert (link.cl_info() != null) : "";

		link.set_cl_info(null);

	}

	@Override
	public void dump_link(Link link, StringBuffer buf) {

		assert (link != null) : "ConnectionConvergenceLayer : dump_link, link is null";
		assert (!link.isdeleted()) : "ConnectionConvergenceLayer : dump_link, link is deleted";
		assert (link.cl_info() != null) : "ConnectionConvergenceLayer : dump_link, cl_info is null";
		;

		LinkParams params = (LinkParams) (link.cl_info());
		assert (params != null);

		buf.append("reactive_frag_enabled: %s\n"
				+ params.reactive_frag_enabled());
		buf.append("sendbuf_len: %s\n" + params.sendbuf_len());
		buf.append("recvbuf_len: %d\n" + params.recvbuf_len());
		buf.append("data_timeout: %d\n" + params.data_timeout());
		buf.append("test_read_delay: %d\n" + params.test_read_delay());
		buf.append("test_write_delay: %d\n" + params.test_write_delay());
		buf.append("test_recv_delay: %d\n" + params.test_recv_delay());

	}

	public boolean reconfigure_link(Link link) {

		assert (link != null);
		assert (!link.isdeleted());
		assert (link.cl_info() != null);

		LinkParams params = (LinkParams) link.cl_info();
		assert (params != null);

		if (link.isopen()) {

			CLConnection conn = (CLConnection) (link.contact().cl_info());
			assert (conn != null) : "ConnectionConvergenceLayer: reconfigure_link, conn is null";

			if ((params.sendbuf_len() != conn.sendbuf_.capacity())
					&& (params.sendbuf_len() >= conn.sendbuf_.capacity())) {
				String text = String.format(
						"resizing link %s send buffer from %s -> %s", link,
						conn.sendbuf_.capacity(), params.sendbuf_len());
				Log.i(TAG, text);

				IByteBuffer reserved_sendbuf = BufferHelper.reserve(
						conn.sendbuf_, params.sendbuf_len());
				conn.sendbuf_ = reserved_sendbuf;

			}

			if ((params.recvbuf_len() != conn.recvbuf_.capacity())
					&& (params.recvbuf_len() >= conn.recvbuf_.capacity())) {
				String text = String.format(
						"resizing link %s recv buffer from %s -> %s", link,
						conn.recvbuf_.capacity(), params.recvbuf_len());
				Log.i(TAG, text);

				IByteBuffer reserved_recvbuf = BufferHelper.reserve(
						conn.recvbuf_, params.recvbuf_len());
				conn.recvbuf_ = reserved_recvbuf;
			}
		}

		return true;

	}

	@Override
	public boolean open_contact(Contact contact) {

		Link link = contact.link();
		assert (link != null) : "ConnectionConvergenceLayer: open_contact, link is null";
		assert (!link.isdeleted()) : "ConnectionConvergenceLayer: open_contact, link is deleted";
		assert (link.cl_info() != null) : "ConnectionConvergenceLayer: open_contact, cl_info is null";

		Log.d(TAG, "opening contact on link " + link);

		LinkParams params = (LinkParams) link.cl_info();
		assert (params != null);

		// "create a new connection for the contact, set up to use the
		// link's configured parameters"[DTN2]
		CLConnection conn;
		try {
			conn = new_connection(link, params);
		} catch (OutOfMemoryError e) {
			Log.d(TAG, "Not Enough resources");
			return false;
		}
		conn.set_contact(contact);
		contact.set_cl_info(conn);
		conn.start();

		return true;
	}

	@Override
	public boolean close_contact(Contact contact) {

		Log.i(TAG, "close_contact " + contact);

		Link link = contact.link();
		assert (link != null) : "ConnectionConvergenceLayer : close_contact, link is null";

		CLConnection conn = (CLConnection) (contact.cl_info());
		assert (conn != null) : "ConnectionConvergenceLayer : close_contact, conn is null";

		// "if the connection isn't already broken, then we need to tell it
		// to do so" [DTN2]
		if (!conn.contact_broken()) {

			try {
				conn.cmdqueue().put(
						conn.new CLMsg(CLConnection.clmsg_t.CLMSG_BREAK_CONTACT));
			} catch (InterruptedException e) {
				Log.e(TAG, "InteruptedException in close_contact command");
			}
		}

		while (conn.isAlive()) {
			Log.d(TAG, "waiting for connection thread to stop...");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Log.d(TAG, "InterruptedException");
			}

		}

		// "now that the connection thread is stopped, clean up the in
		// flight and incoming bundles" [DTN2]
		LinkParams params = (LinkParams) (link.cl_info());
		assert (params != null) : "ConnectionConvergenceLayer : close_contact, params are null";

		while (!conn.inflight().isEmpty()) {

			InFlightBundle inflight = conn.inflight().get(0);
			int sent_bytes = inflight.sent_data().size();

			// "every time we sent we set that particular bit , and we clear out when we ack hence"[DTN2]
			int acked_bytes = inflight.ack_data().size();
			if ((!params.reactive_frag_enabled()) || (sent_bytes == 0)
					|| (link.is_reliable() && acked_bytes == 0)) {
				// "if we've started the bundle but not gotten anything
				// out, we need to push the bundle back onto the link
				// queue so it's there when the link re-opens"[DTN2]
				if (!link.del_from_inflight(inflight.bundle(), inflight
						.total_length())
						|| !link.add_to_queue(inflight.bundle(), inflight
								.total_length())) {
					Log.w(TAG, "inflight queue mismatch for bundle "
							+ inflight.bundle().bundleid());
				}

			} else {
				// "otherwise, if part of the bundle has been transmitted,
				// then post the event so that the core system can do
				// reactive fragmentation"[DTN2]
				if (!inflight.transmit_event_posted()
						&& params.reactive_frag_enabled()) {
					BundleDaemon Daemon = BundleDaemon.getInstance();
					Daemon.post(new BundleTransmittedEvent(inflight.bundle(),
							contact, link, sent_bytes, acked_bytes));
				}
			}

			conn.inflight().remove(0);

		}

		// "check the tail of the incoming queue to see if there's a
		// partially-received bundle that we need to post a received event
		// for (if reactive fragmentation is enabled)"[DTN2]
		if (!conn.incoming().isEmpty()) {
			IncomingBundle incoming = conn.incoming().get(
					conn.incoming().size() - 1);
			if (!incoming.rcvd_data().isEmpty()) {
				int rcvd_len = incoming.rcvd_data().size();

				int header_block_length = BundleProtocol
						.payload_offset(incoming.bundle().recv_blocks());

				if ((incoming.total_length() == 0)
						&& params.reactive_frag_enabled()
						&& (rcvd_len > header_block_length)) {
					String text = String
							.format(
									"partial arrival of bundle: got %d bytes [hdr %d payload %s]",
									rcvd_len, header_block_length, incoming
											.bundle().payload().length());
					Log.d(TAG, text);

					BundleDaemon Daemon = BundleDaemon.getInstance();
					Daemon.post(new BundleReceivedEvent(incoming.bundle(),
							event_source_t.EVENTSRC_PEER, rcvd_len, contact
									.link().remote_eid(), contact.link()));
				}
			}
		}

		// drain the CLConnection incoming queue
		conn.incoming().clear();

		// clear out the connection message queue
		conn.cmdqueue().clear();
		contact.set_cl_info(null);

		if (link.isdeleted()) {
			assert (link.cl_info() != null);
			link.set_cl_info(null);
		}

		Link.set_link_counter(Link.link_counter() - 1);
		return true;

	}

	@Override
	public void bundle_queued(Link link, Bundle bundle) {

		String text = String.format("bundle_queued: queued %s on %s", bundle,
				link);
		Log.d(TAG, text);

		if (!link.isopen()) {
			return;
		}

		assert (!link.isdeleted()) : "ConnectionConvergenceLayer : bundle_queued, link is deleted";

		Contact contact = link.contact();
		assert (contact != null) : "ConnectionConvergenceLayer : bundle_queued, contact is null";

		CLConnection conn = (CLConnection) (contact.cl_info());
		assert (conn != null) : "ConnectionConvergenceLayer : bundle_queued, conn is null";

		// "the bundle was previously put on the link queue, so we just
		// kick the connection thread in case it's idle.
		//
		// note that it's possible the bundle was already picked up and
		// taken off the link queue by the connection thread, so don't
		// assert here"[DTN2].
		try {
			conn.cmdqueue().put(
					conn.new CLMsg(CLConnection.clmsg_t.CLMSG_BUNDLES_QUEUED));
			
		} catch (InterruptedException e) {
			Log.e(TAG, "Interupt in bundle queue command");
		}

	}

	@Override
	public void cancel_bundle(Link link, Bundle bundle) {
		
		if(bundle==null){
			Log.e(TAG, "bundle is null when cancel_bundle is called");
			
		}

		Log.d(TAG, "cancel_bundle, bundle is " + bundle);
		
		assert (!link.isdeleted()) : "ConnectionConvergenceLayer : cancel_bundle, link is deleted";

		// "the bundle should be on the inflight queue for cancel_bundle to
		// be called"[DTN2]
		if (!bundle.is_queued_on(link.inflight())) {
			String text = String.format(
					"cancel_bundle %s is not on link %s inflight queue",
					bundle, link);
			Log.w(TAG, text);
			return;
		}

		if (!link.isopen()) {
			/*
			 * (Taken from jmmikkel checkin comment on BBN source tree)
			 * 
			 * The dtn2 internal convergence layer complains and does nothing if
			 * you try to cancel a bundle after the link has closed instead of
			 * just considering the send cancelled. I believe that posting a
			 * BundleCancelledEvent before returning is the correct way to make
			 * the cancel actually happen in this situation, as the bundle is
			 * removed from the link queue in that event's handler.
			 */
			String text = String.format(
					"cancel_bundle %s but link %s isn't open!!", bundle, link);
			Log.w(TAG, text);
			BundleDaemon Daemon = BundleDaemon.getInstance();
			Daemon.post(new BundleSendCancelledEvent(bundle, link));
			return;
		}

		Contact contact = link.contact();
		CLConnection conn = (CLConnection) (contact.cl_info());
		assert (conn != null) : "ConnectionConvergenceLayer : cancel_bundle, conn is null";

		assert (contact.link() == link);
		String text = String.format("cancel_bundle: cancelling %s on %s",
				bundle, link);
		Log.d(TAG, text);

		
		try {
			conn.cmdqueue()
					.put(
							conn.new CLMsg(
									CLConnection.clmsg_t.CLMSG_CANCEL_BUNDLE,
									bundle));
		} catch (InterruptedException e) {

			Log.e(TAG, "Cancel bundle command");
		}

	}

	/**
	 * Parse and validate the nexthop address for the given link.
	 */

	@Override
	public abstract boolean parse_nexthop(Link link, LinkParams params);

	/**
	 * Create a new LinkParams structure.
	 */
	public abstract LinkParams new_link_params();

	/**
	 * After the link parameters are parsed, do any initialization of the link
	 * that's necessary before starting up a connection.
	 */
	public boolean finish_init_link(Link link, LinkParams params) {
		return true;

	}

	/**
	 * Create a new CL-specific connection object.
	 */
	public abstract CLConnection new_connection(Link link, LinkParams params);

}
