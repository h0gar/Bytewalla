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

import java.net.Socket;
import java.util.Iterator;

import se.kth.ssvl.tslab.bytewalla.androiddtn.DTNService;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BlockInfoVec;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.Bundle;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleDaemon;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundlePayload;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.CustodyTimerSpec;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.SDNV;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.ForwardingInfo.action_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleFreeEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleReceivedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleSendCancelledEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleTransmittedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ContactEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.event_source_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ContactEvent.reason_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.ContactManager;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.StreamConvergenceLayer.contact_header_flags_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.StreamConvergenceLayer.data_segment_flags_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.StreamConvergenceLayer.msg_type_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.StreamConvergenceLayer.shutdown_flags_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.StreamConvergenceLayer.shutdown_reason_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.BufferHelper;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;
import android.util.Log;

/**
 * Stream connection class.
 * 
 * @author María José Peroza Marval (mjpm@kth.se)
 */

public abstract class Connection extends CLConnection {

	/**
	 * Unique identifier according to Java Serializable specification
	 */
	private static final long serialVersionUID = -4367955899900761305L;

	/**
	 * TAG for Android Logging mechanism
	 */
	public static final String TAG = "Connection";

	/**
	 * Socket of the connection
	 */
	protected Socket socket_;

	/**
	 * Uploading and downloading flags
	 */
	private boolean uploading_ = false;
	private boolean downloading_ = false;

	/**
	 * Set the socket
	 */
	public void set_socket(Socket socket) {
		socket_ = socket;
	}

	/**
	 * Increasing the counter for uploading bundles
	 */
	protected void handle_bundle_begin_upload(Bundle bundle) {
		Log.d(TAG, "handle begin upload, uploading number is "
				+ ContactManager.getInstance().number_uploading_bundles());
		uploading_ = true;

		if (DTNService.is_test_data_logging())
			TestDataLogger.getInstance().log_bundle_upload_begin(this, bundle);
		ContactManager.getInstance().set_number_uploading_bundles(
				ContactManager.getInstance().number_uploading_bundles() + 1);
	}

	/**
	 * Decreasing the counter for uploading bundles if the bundle was not
	 * successfully uploaded
	 */
	protected void handle_bundle_uploading_terminated_unfinished(Bundle bundle) {

		Log.d(TAG,
				"handle uploading terminated unfinished, uploading number is "
						+ ContactManager.getInstance()
								.number_uploading_bundles());

		ContactManager.getInstance().set_number_uploading_bundles(
				ContactManager.getInstance().number_uploading_bundles() - 1);

		uploading_ = false;

		// requeue the bundle when uploading terminated unfinished
		BundleDaemon.getInstance().actions().queue_bundle(bundle,
				this.contact_.link(), action_t.FORWARD_ACTION,
				CustodyTimerSpec.getDefaultInstance());

	}

	/**
	 * Decreasing the counter for uploading bundles if the bundle was
	 * successfully uploaded
	 */
	protected void handle_bundle_end_upload(InFlightBundle inflight) {

		Log.d(TAG, "handle end upload, uploading number is "
				+ ContactManager.getInstance().number_uploading_bundles());

		if (DTNService.is_test_data_logging())
			TestDataLogger.getInstance().log_bundle_upload_end(this, inflight);
		ContactManager.getInstance().set_number_uploading_bundles(
				ContactManager.getInstance().number_uploading_bundles() - 1);

		uploading_ = false;

	}

	/**
	 * Increasing the counter for downloading bundles
	 */
	protected void handle_bundle_begin_download() {

		Log.d(TAG,
				"handle begin download, before handle downloading number is "
						+ ContactManager.getInstance()
								.number_downloading_bundles());

		downloading_ = true;
		if (DTNService.is_test_data_logging())
			TestDataLogger.getInstance().log_bundle_download_begin(this);

		ContactManager.getInstance().set_number_downloading_bundles(
				ContactManager.getInstance().number_downloading_bundles() + 1);
	}

	/**
	 * Decreasing the counter if the the bundle was not successfully downloaded
	 */
	protected void handle_bundle_downloading_terminated_unfinished(Bundle bundle) {

		Log
				.d(
						TAG,
						"handle downloading terminated unfinished, before handle  downloading number is "
								+ ContactManager.getInstance()
										.number_downloading_bundles());

		ContactManager.getInstance().set_number_downloading_bundles(
				ContactManager.getInstance().number_downloading_bundles() - 1);

		downloading_ = false;

		// the bundle will be re-received again in order to try again later
		BundleDaemon.getInstance().post(new BundleFreeEvent(bundle));
	}

	/**
	 * Decreasing the counter if the the bundle was successfully downloaded
	 */
	protected void handle_bundle_end_download(IncomingBundle incoming) {

		Log.d(TAG,
				"handle downloading end, before handle downloading number is "
						+ ContactManager.getInstance()
								.number_downloading_bundles());

		if (DTNService.is_test_data_logging())
			TestDataLogger.getInstance()
					.log_bundle_download_end(this, incoming);

		ContactManager.getInstance().set_number_downloading_bundles(
				ContactManager.getInstance().number_downloading_bundles() - 1);

		downloading_ = false;

	}

	/**
	 * Constructor.
	 */
	public Connection(StreamConvergenceLayer cl, StreamLinkParams params,
			boolean active_connector) throws OutOfMemoryError {

		super(cl, params, active_connector);

		current_inflight_ = null;
		send_segment_todo_ = 0;
		recv_segment_todo_ = 0;
		breaking_contact_ = false;
		contact_initiated_ = false;

	}

	@Override
	public boolean send_pending_data() {

		// if the outgoing data buffer is empty, we can't do anything until
		if (sendbuf_.remaining() == 0) {
			return false;
		}

		// "if we're in the middle of sending a segment, we need to continue
		// sending it. only if we completely send the segment do we fall
		// through to send acks, otherwise we return to try to finish it
		// again later"[DTN2].
		if (send_segment_todo_ != 0) {
			assert (current_inflight_ != null) : "Connection : send_pending_data, current_inflight is null";
			send_data_todo(current_inflight_);
		}

		// "see if we're broken or write blocked"[DTN2].
		if (contact_broken() || (send_segment_todo_ != 0)) {

			return false;
		}

		boolean sent_ack = false;
		// "now check if there are acks we need to send -- even if it
		// returns true (i.e. we sent an ack), we continue on and try to
		// send some real payload data, otherwise we could get starved by
		// arriving data and never send anything out"[DTN2].
		if (params_.segment_ack_enabled()) {
		
			sent_ack = send_pending_acks();

			// "if the connection failed during ack transmission, stop"[DTN2]
			if (contact_broken()) {
				return sent_ack;
			}
		}
		// "check if we need to start a new bundle. if we do, then
		// start_next_bundle handles the correct return code"[DTN2].
		boolean sent_data;
		if (current_inflight_ == null) {
			sent_data = start_next_bundle();
		} else {
			// "otherwise send the next segment of the current bundle" [DTN2]
			sent_data = send_next_segment(current_inflight_);
		}

		return sent_ack || sent_data;

	}

	@Override
	public void handle_bundles_queued() {

		// "since the main run loop checks the link queue to see if there
		// are bundles that should be put in flight, we simply log a debug
		// message here. the point of the message is to kick the thread
		// out of poll() which forces the main loop to check the queue" [DTN2].
		Log.d(TAG, "handle_bundles_queued: %d bundles on link queue"
				+ contact_.link().bundles_queued());

	}

	@Override
	public void handle_cancel_bundle(Bundle bundle) {

		// "if the bundle is already actually in flight (i.e. we've already
		// sent all or part of it), we can't currently cancel it. however,
		// in the case where it's not already in flight, we can cancel it
		// and accordingly signal with an event" [DTN2].
		Iterator<InFlightBundle> iter = inflight_.iterator();
		while (iter.hasNext()) {
			InFlightBundle inflight = iter.next();
			if (inflight.bundle() == bundle) {
				if (inflight.sent_data().isEmpty()) {
					// "this bundle might be current_inflight_ but with no
					// data sent yet; check for this case so we do not have
					// a dangling pointer"[DTN2].
					if (inflight == current_inflight_) {
						// "we may have sent a segment length without any bundle
						// data; if so we must send the segment so we can't
						// cancel the send now"[DTN2]
						if (send_segment_todo_ != 0) {
							String text = String
									.format(
											"handle_cancel_bundle: bundle %s already in flight, can't cancel send",
											bundle.bundleid());
							Log.d(TAG, text);
							return;
						}
						current_inflight_ = null;
					}

					String text = String
							.format(
									"handle_cancel_bundle: bundle %s not yet in flight, cancelling send",
									bundle.bundleid());
					Log.d(TAG, text);
					inflight_.remove(iter);
					BundleDaemon Daemon = BundleDaemon.getInstance();
					Daemon.post(new BundleSendCancelledEvent(bundle, contact_
							.link()));
					return;
				} else {

					String text = String
							.format(
									"handle_cancel_bundle: bundle %s already in flight, can't cancel send",
									bundle.bundleid());
					Log.d(TAG, text);
					return;
				}
			}
		}
/*		
		String text = String
				.format(
						"handle_cancel_bundle: can't find bundle %d in the in flight list",
						bundle.bundleid());
*/						
		Log.w(TAG, "handle_cancel_bundle: can't find bundle %d in the in flight list");
	}

	@Override
	public void break_contact(ContactEvent.reason_t reason) {

		Log.e(TAG, "Breaking contact with reason " + reason.toString());
		// "it's possible that we can end up calling break_contact multiple
		// times, if for example we have an error when sending out the
		// shutdown message below. we simply ignore the multiple calls" [DTN2].
		if (breaking_contact_) {
			return;
		}
		breaking_contact_ = true;

		// "we can only send a shutdown byte if we're not in the middle
		// of sending a segment, otherwise the shutdown byte could be
		// interpreted as a part of the payload" [DTN2].
		boolean send_shutdown = false;
		int shutdown_reason = shutdown_reason_t.SHUTDOWN_NO_REASON.getCode();

		switch (reason) {
		case USER:
			// "if the user is closing this link, we say that we're busy" [DTN2]
			send_shutdown = true;
			shutdown_reason = shutdown_reason_t.SHUTDOWN_BUSY.getCode();
			break;

		case IDLE:
			// "if we're idle, indicate as such" [DTN2]
			send_shutdown = true;
			shutdown_reason = shutdown_reason_t.SHUTDOWN_IDLE_TIMEOUT.getCode();
			break;

		case SHUTDOWN:
			// "if the other side shuts down first, we send the
			// corresponding SHUTDOWN byte for a clean handshake, but
			// don't give any more reason"[DTN2]
			send_shutdown = true;
			break;

		case BROKEN:
		case CL_ERROR:
			// no shutdown
			send_shutdown = false;
			break;

		case CL_VERSION:
			// version mismatch
			send_shutdown = true;
			shutdown_reason = shutdown_reason_t.SHUTDOWN_VERSION_MISMATCH
					.getCode();
			break;

		case INVALID:
		case NO_INFO:
		case RECONNECT:
		case TIMEOUT:
		case DISCOVERY:
			Log.d(TAG, "NOTREACHED");
			break;
		}

		if (send_shutdown && sendbuf_.position() == 0
				&& send_segment_todo_ == 0) {
			Log.d(TAG, "break_contact: sending shutdown");
			byte typecode = (byte) msg_type_t.SHUTDOWN.getCode();

			if (shutdown_reason != shutdown_reason_t.SHUTDOWN_NO_REASON
					.getCode()) {

				typecode |= shutdown_flags_t.SHUTDOWN_HAS_REASON.getCode();
			}

			Log.e(TAG, "Sending shutdown CL Msg");
			sendbuf_.put(typecode);

			send_data();
		}

		if (uploading_)
			handle_bundle_uploading_terminated_unfinished(inflight_.front()
					.bundle());

		if (downloading_)
			handle_bundle_downloading_terminated_unfinished(incoming_.back()
					.bundle());

		super.break_contact(reason);

	}

	/**
	 * Hook used to tell the derived CL class to drain data out of the send
	 * buffer.
	 */
	abstract void send_data();

	/**
	 * utility functions used by derived classes
	 */
	protected void initiate_contact() {

		Log.d(TAG, "initiate_contact called");

		// format the contact header
		ContactHeader contacthdr = new ContactHeader();
		contacthdr.magic = ConvergenceLayer.MAGIC;
		contacthdr.version = ((StreamConvergenceLayer) cl_).cl_version_;

		contacthdr.flags = 0;

		StreamLinkParams params = stream_lparams();

		if (params.segment_ack_enabled())
			contacthdr.flags |= contact_header_flags_t.SEGMENT_ACK_ENABLED
					.getCode();

		if (params.reactive_frag_enabled())
			contacthdr.flags |= contact_header_flags_t.REACTIVE_FRAG_ENABLED
					.getCode();

		if (params.negative_ack_enabled())
			contacthdr.flags |= contact_header_flags_t.NEGATIVE_ACK_ENABLED
					.getCode();

		contacthdr.keepalive_interval = (short) params.keepalive_interval();

		// copy the contact header into the send buffer
		assert (sendbuf_.position() == 0) : "Connection : initiate_contact, the position of the buffer is not zero";
		if (sendbuf_.remaining() < 8) {

			String text = String.format("send buffer too short: %s < needed 8",
					sendbuf_.remaining());
			Log.w(TAG, text);

			IByteBuffer reserved_sendbuffer = BufferHelper.reserve(sendbuf_,
					(sendbuf_.position() + 8));
			sendbuf_ = reserved_sendbuffer;

		}
		sendbuf_.putInt(contacthdr.magic);
		sendbuf_.put(contacthdr.version);
		sendbuf_.put(contacthdr.flags);
		sendbuf_.putShort(contacthdr.keepalive_interval);

		// follow up with the local endpoint id length + data
		BundleDaemon bd = BundleDaemon.getInstance();
		int local_eid_len = bd.local_eid().length();
		int sdnv_len = SDNV.encoding_len(local_eid_len);

		if (sendbuf_.remaining() < sdnv_len + local_eid_len) {

			String text = String.format(
					"send buffer too short: %s < needed %s", sendbuf_
							.remaining(), sdnv_len + local_eid_len);
			Log.w(TAG, text);

			IByteBuffer reserved_sendbuffer = BufferHelper.reserve(sendbuf_,
					(sendbuf_.position() + sdnv_len + local_eid_len));
			sendbuf_ = reserved_sendbuffer;
		}

		sdnv_len = SDNV.encode(local_eid_len, sendbuf_);

		sendbuf_.put(bd.local_eid().byte_array());

		// drain the send buffer
		note_data_sent();
		send_data();

		/*
		 * "Now we initialize the various timers that are used for keepalives /
		 * idle timeouts to make sure they're not used uninitialized" [DTN2].
		 */

		data_rcvd_ = System.currentTimeMillis();
		data_sent_ = System.currentTimeMillis();
		keepalive_sent_ = System.currentTimeMillis();
		contact_initiated_ = true;
		Link.set_link_counter(Link.link_counter() + 1);

	}

	protected void process_data() {

		if (recvbuf_.position() == 0) {
			return;
		}

		String text = String.format(
				"processing up to %s bytes from receive buffer", recvbuf_
						.position());
		Log.d(TAG, text);

		// "all data (keepalives included) should be noted since the last
		// reception time is used to determine when to generate new
		// keepalives"[DTN2].
		note_data_rcvd();

		// "the first thing we need to do is handle the contact initiation
		// sequence, i.e. the contact header and the announce bundle. we
		// know we need to do this if we haven't yet called contact_up()"
		// [DTN2].
		if (!contact_up_) {
			handle_contact_initiation();

		}

		// "if a data segment is bigger than the receive buffer. when
		// processing a data segment, we mark the unread amount in the
		// recv_segment_todo__ field,
		// so if that's not zero, we need to drain it
		// then fall through to handle the rest of the buffer"[DTN2].
		if (recv_segment_todo_ != 0) {

			Log.d(TAG, "there is some leftover segment to do with length "
					+ recv_segment_todo_);
			int last_position = recvbuf_.position();
			int[] handled_bytes = new int[1];
			recvbuf_.rewind();
			boolean ok = handle_data_todo(last_position, handled_bytes);

			if (!ok) {
				// revert the position back in case we're unable to handle data
				recvbuf_.position(last_position);
				return;
			} else {
				BufferHelper.move_data_back_to_beginning(recvbuf_,
						handled_bytes[0]);
				recvbuf_.position(last_position - handled_bytes[0]);

				// if there's still something left to process bail out to come
				// and process again
				if (recv_segment_todo_ != 0)
					return;

			}

		}

		// "now, drain cl messages from the receive buffer. we peek at the
		// first byte and dispatch to the correct handler routine
		// depending on the type of the CL message. we don't consume the
		// byte yet since there's a possibility that we need to read more
		// from the remote side to handle the whole message"[DTN2].

		Log.d(TAG, "falling down to recvbuf_ processing with position "
				+ recvbuf_.position());
		while (recvbuf_.position() != 0) {

			// remember the position before drain

			if (contact_broken())
				return;

			byte type = (byte) (recvbuf_.get(0) & 0xf0);
			byte flags = (byte) (recvbuf_.get(0) & 0x0f);

			String text1 = String
					.format(
							"recvbuf has %s full bytes, dispatching to handler routine",
							recvbuf_.position());
			Log.d(TAG, text1);
			boolean ok = false;

			msg_type_t msg_type = msg_type_t.get(type);

			if (msg_type != null) {
				switch (msg_type) {
				case DATA_SEGMENT:
					ok = handle_data_segment(flags);
					break;
				case ACK_SEGMENT:
					ok = handle_ack_segment(flags);
					break;
				case REFUSE_BUNDLE:
					ok = handle_refuse_bundle(flags);
					break;
				case KEEPALIVE:
					ok = handle_keepalive(flags);
					break;
				case SHUTDOWN:
					ok = handle_shutdown(flags);
					break;

				}
			} else {
				String text2 = String.format(
						"invalid CL message type code 0x%s (flags 0x%s)", type,
						flags);
				Log.e(TAG, text2);
				break_contact(ContactEvent.reason_t.CL_ERROR);
				return;
			}
			// if there's not enough data in the buffer to handle the
			// message, make sure there's space to receive more
			if (!ok) {

				Log
						.d(TAG,
								"try to process but the data is not enough or not possible to process");
				break_contact(reason_t.BROKEN);
				return;
			}

		}

	}

	/**
	 * Terminate the contact if we have not received 2*keepalive in seconds
	 */
	protected void check_timeout() {

		long now;
		long elapsed_received;

		StreamLinkParams params = (StreamLinkParams) (params_);
		assert (params != null) : "Connection : check_keepalive, params are null";

		now = System.currentTimeMillis();

		int timeout = 10;
		if (params.keepalive_interval() != 0)
			timeout = 2 * params.keepalive_interval();

		elapsed_received = (now - data_rcvd_);

		if (elapsed_received > timeout * 1000) {
			// it's possible that the link is blocked while in the
			// middle of a segment, triggering a poll timeout, so make
			// sure not to send a keepalive in this case
			if (send_segment_todo_ != 0) {
				Log.d(TAG, "break contact");
				return;
			}

			break_contact(reason_t.IDLE);
		}

	}

	protected void check_keepalive() {

		long now;
		long elapsed, elapsed2;

		StreamLinkParams params = (StreamLinkParams) (params_);
		assert (params != null) : "Connection : check_keepalive, params are null";

		now = System.currentTimeMillis();

		if (params.keepalive_interval() != 0) {
			elapsed = (now - data_sent_);
			elapsed2 = (now - keepalive_sent_);

			if (Math.min(elapsed, elapsed2) > ((params.keepalive_interval() * 1000) - 500)) {

				if (send_segment_todo_ != 0) {
					Log.d(TAG,
							"not issuing keepalive in the middle of a segment");
					return;
				}

				send_keepalive();
			}
		}

	}

	/**
	 * utility functions used internally in this class
	 */
	private void note_data_rcvd() {

		Log.d(TAG, "noting data_rcvd");
		data_rcvd_ = System.currentTimeMillis();

	}

	private void note_data_sent() {

		Log.d(TAG, "noting data_sent");
		data_sent_ = System.currentTimeMillis();

	}

	/**
	 * send acknowledgement according to the recv_data when data is received the
	 * bit in particular position in the DataBitmap will be set
	 */
	private boolean send_pending_acks() {

		if (contact_broken() || incoming_.isEmpty()) {
			return false; // nothing to do
		}

		IncomingBundle incoming = incoming_.get(0);

		boolean generated_ack = false;

		// try to go back immediately if we haven't get anything or not downloading
		if (incoming.rcvd_data().isEmpty() && !downloading_)
			return false;

		// next set bit position + 1 will be the size of the segment we should
		// send
		// if we couldn't find it means that we don't need to ack
		while (incoming.rcvd_data().nextSetBit(0) != -1) {
			int ack_len = incoming.rcvd_data().nextSetBit(0) + 1;

			// make sure we have space in the send buffer
			int encoding_len = 1 + SDNV.encoding_len(ack_len);
			if (encoding_len > sendbuf_.remaining()) {

				String text = String
						.format(
								"send_pending_acks: no space for ack in buffer (need %d, have %d)",
								encoding_len, sendbuf_.remaining());
				Log.d(TAG, text);
				
				// send the data for trying to clear space
				send_data();
				note_data_sent();
				
				break;
			}

			sendbuf_.put((byte) msg_type_t.ACK_SEGMENT.getCode());

			SDNV.encode(ack_len, sendbuf_);

			// after put it in the buffer clear out the bit
			// it have to be minus because position start with zero
			incoming.rcvd_data().clear(ack_len - 1);

			// sendbuf_.position(sendbuf_.position() + len);

			generated_ack = true;
			if (incoming.acked_length() < ack_len) {
				incoming.set_acked_length(ack_len);
			}

			if (generated_ack) {
				send_data();
				note_data_sent();
			}

		}

		// "now, check if a) we've gotten everything we're supposed to
		// (i.e. total_length_ isn't zero), and b) we're done with all the
		// acks we need to send" [DTN2]


		if ((incoming.total_length() != 0)
				&& (incoming.total_length() == incoming.acked_length())
				&& params_.segment_ack_enabled()) {
			String text = String.format(
					"send_pending_acks: acked all %d bytes of bundle %s",
					incoming.total_length(), incoming.bundle().bundleid());
			Log.d(TAG, text);

			
			BundleDaemon.getInstance().post(new BundleReceivedEvent(incoming.bundle(), event_source_t.EVENTSRC_PEER, incoming.total_length(), contact_
							.link().remote_eid(), contact_.link()));
			
			
			incoming_.remove(0);

		} else {
			Log
					.d(
							TAG,
							String
									.format(
											"send_pending_acks: still need to send acks or haven't get total length-- acked %d , total length %d",
											incoming.acked_length(), incoming
													.total_length()));
		}

		// return true if we've sent something
		return generated_ack;

	}

	private boolean start_next_bundle() {

		assert (current_inflight_ == null) : "Connection : start_next_bunble, current_inflight is not null";

		if (!contact_up_) {
			return false;
		}

		Link link = contact_.link();

		// try to pop the next bundle off the link queue and put it in flight
		Bundle bundle = link.queue().front();

		// if there is no bundle in the list or the system is downloading
		if (bundle == null || downloading_) {

			return false;
		}

		handle_bundle_begin_upload(bundle);

		InFlightBundle inflight = new InFlightBundle(bundle);
		String text = String.format(
				"trying to find xmit blocks for bundle id:%s on link %s",
				bundle.bundleid(), link.name());
		Log.d(TAG, text);

		sendbuf_.rewind();

		inflight.set_blocks(bundle.xmit_link_block_set().find_blocks(
				contact_.link()));

		assert (inflight.blocks() != null) : "Connection : start_next_bundle, inflight blocks are null";
		inflight.set_total_length(BundleProtocol
				.total_length(inflight.blocks()));
		inflight_.add(inflight);
		current_inflight_ = inflight;

		link.add_to_inflight(bundle, inflight.total_length());
		link.del_from_queue(bundle, inflight.total_length());

		// now send the first segment for the bundle
		return send_next_segment(current_inflight_);

	}

	private boolean send_next_segment(InFlightBundle inflight) {

		// have to space to send so we must bounce out
		if (sendbuf_.remaining() == 0) {
			return false;
		}

		assert (send_segment_todo_ == 0) : "Connection : send_next_segment, send_segment_todo_ is not zero";

		StreamLinkParams params = stream_lparams();

		int bytes_sent = inflight.sent_data().size();

		// check for Bundle finishing before trying to send more data
		if (bytes_sent == inflight.total_length()) {
			String text = String
					.format(
							"send_next_segment: already sent all %d bytes, finishing bundle",
							bytes_sent);
			Log.d(TAG, text);
			return finish_bundle(inflight);
		}

		byte flags = 0;
		int segment_len;

		if (bytes_sent == 0) {
			flags |= data_segment_flags_t.BUNDLE_START.getCode();
		}

		// check whether this is the case of last segment
		if (params.segment_length() >= inflight.total_length() - bytes_sent) {

			flags |= data_segment_flags_t.BUNDLE_END.getCode();
			segment_len = inflight.total_length() - bytes_sent;

			Log.d(TAG, "Sending last segment flag now is " + flags
					+ ", last segment len is " + segment_len);
		} else {
			segment_len = params.segment_length();
		}

		int sdnv_len = SDNV.encoding_len(segment_len);

		if (sendbuf_.remaining() < 1 + sdnv_len) {
			String text = String
					.format(
							"send_next_segment: not enough space for segment header [need %d, have %d]",
							1 + sdnv_len, sendbuf_.remaining());
			Log.d(TAG, text);
			return false;
		}

		String text = String
				.format(
						"send_next_segment: starting %d byte segment [block byte range %d..%d]",
						segment_len, bytes_sent, bytes_sent + segment_len);
		Log.d(TAG, text);

		byte bp = (byte) ((msg_type_t.DATA_SEGMENT.getCode() & 0xF0) | (flags & 0x0F));
		sendbuf_.put(bp);
		int cc = SDNV.encode(segment_len, sendbuf_, sendbuf_.remaining() - 1);
		assert (cc == sdnv_len);
		send_segment_todo_ = segment_len;

		// send_data_todo actually does the deed
		return send_data_todo(inflight);

	}

	private boolean send_data_todo(InFlightBundle inflight) {

		assert (send_segment_todo_ != 0) : "Connection : send_data_todo, send_segment_todo_ is equal to zero";

		// "loop since it may take multiple calls to send on the socket
		// before we can actually drain the todo amount"[DTN2].
		while ((send_segment_todo_ != 0) && (sendbuf_.remaining() != 0)) {

			int bytes_sent = inflight.sent_data().size();
			int send_len = Math.min(send_segment_todo_, sendbuf_.remaining());

			Bundle bundle = inflight.bundle();
			BlockInfoVec blocks = inflight.blocks();

			boolean[] last_block_value = new boolean[1];

			int ret = BundleProtocol.produce(bundle, blocks, sendbuf_,
					bytes_sent, send_len, last_block_value);

			inflight.set_send_complete(last_block_value[0]);
			assert (ret == send_len);

			if (ret != send_len) {
				Log.e(TAG, "produce error");
				return false;
			}
			sendbuf_.position(sendbuf_.position() + send_len);

			int sent_len = inflight.sent_data().size();
			inflight.sent_data().set(sent_len + send_len - 1);

			String text = String
					.format(
							"send_data_todo: sent %d/%d of current segment from block offset %d (%d todo), updated sent_data %d",
							send_len, send_segment_todo_, bytes_sent,
							send_segment_todo_ - send_len, inflight.sent_data()
									.size());
			Log.d(TAG, text);

			send_segment_todo_ -= send_len;

			note_data_sent();
			send_data();

			if (contact_broken())
				return true;

			// if test_write_delay is set, then we only send one segment
			// at a time before bouncing back to poll
			if (params_.test_write_delay() != 0) {
				String text1 = String
						.format(
								"send_data_todo done, returning more to send (send_segment_todo_==%s) since test_write_delay is non-zero",
								send_segment_todo_);
				Log.d(TAG, text1);
				return true;
			}
		}

		return (send_segment_todo_ == 0);

	}

	private boolean finish_bundle(InFlightBundle inflight) {

		assert (inflight.send_complete());
		assert (current_inflight_ == inflight) : "Connection : finish_bundle, inconsistency";
		current_inflight_ = null;

		check_completed(inflight);

		return true;

	}

	private void check_completed(InFlightBundle inflight) {

		// "we can pop the inflight bundle off of the queue and clean it up
		// only when both finish_bundle is called (so current_inflight_ no
		// longer points to the inflight bundle), and after the final ack
		// for the bundle has been received (determined by looking at
		// inflight->ack_data_)" [DTN2].

		if (current_inflight_ == inflight) {
			String text = String
					.format(
							"check_completed: bundle %s still waiting for finish_bundle",
							inflight.bundle().bundleid());
			Log.d(TAG, text);
			return;
		}

		if (params_.segment_ack_enabled()) {
			int acked_len = inflight.ack_data().size();
			if (acked_len != inflight.total_length()) {
				String text = String
						.format(
								"check_completed: bundle %d fail because only acked %d/%d",
								inflight.bundle().bundleid(), acked_len,
								inflight.total_length());
				Log.e(TAG, text);
				return;
			}
		}

		String text = String.format(
				"check_completed: bundle %d transmission complete", inflight
						.bundle().bundleid());
		Log.d(TAG, text);
		assert (inflight == inflight_.get(0));

		handle_bundle_end_upload(inflight);

		inflight_.remove(0);

		// if the ack is not enable add the BundleTransmittedEvent here in order
		// to remove bundle from the system
		if (!params_.segment_ack_enabled()) {

			inflight.set_transmit_event_posted(true);
			BundleTransmittedEvent event = new BundleTransmittedEvent(inflight
					.bundle(), contact_, contact_.link(), inflight.sent_data()
					.size(), 0);
			BundleDaemon.getInstance().post(event);

		}

	}

	private void send_keepalive() {

		// "there's no point in putting another byte in the buffer if
		// there's already data waiting to go out, since the arrival of
		// that data on the other end will do the same job as the
		// keepalive byte" [DTN2]
		if (sendbuf_.position() != 0) {
			String text = String
					.format(
							"send_keepalive: send buffer has %s bytes queued, suppressing keepalive",
							sendbuf_.position());
			Log.d(TAG, text);
			return;
		}
		assert (sendbuf_.remaining() > 0) : "Connection : send_keepalive, sendbuf_is full";

		// "similarly, we must not send a keepalive if send_segment_todo_ is
		// nonzero, because that would likely insert the keepalive in the middle
		// of a bundle currently being sent -- verified in check_keepalive"
		// [DTN2]
		assert (send_segment_todo_ == 0);

		keepalive_sent_ = System.currentTimeMillis();

		sendbuf_.put((byte) msg_type_t.KEEPALIVE.getCode());

		// "don't note_data_sent() here since keepalive messages shouldn't
		// be counted for keeping an idle link open" [DTN2]
		send_data();

	}

	/**
	 * Read the contact_initiation, process, and return the number of bytes
	 * handled
	 */
	private void handle_contact_initiation() {

		Log.d(TAG, "handle_contact_initiation: called");
		// the position of the buffer is the num bytes received now
		int received_length = recvbuf_.position();
		int last_position = recvbuf_.position();
		int handled_bytes = 0;

		try {
			assert (!contact_up_);

			/*
			 * First check for valid magic number.
			 */
			int magic = 0;
			int len_needed = 4;
			if (received_length < len_needed) {

				String text = String
						.format(
								"handle_contact_initiation: not enough data received (need > %s, got %s)",
								len_needed, received_length);
				Log.d(TAG, text);
				return;
			}

			magic = recvbuf_.getInt(0);

			if (magic != ConvergenceLayer.MAGIC) {
				String text = String
						.format(
								"remote sent magic number 0x%.8x, expected 0x%.8x  -- disconnecting.",
								magic, ConvergenceLayer.MAGIC);
				Log.w(TAG, text);
				break_contact(ContactEvent.reason_t.MAGIC_NUMBER);
				return;
			}

			// Now check that whether we got a full contact header ,
			// The magic plus another 4 bytes from version, flags, and
			// keepalive_interval

			len_needed = 8;
			if (received_length < len_needed) {
				String text = String
						.format(
								"handle_contact_initiation (missing for full header magic version, flags, keepalive_interval ): not enough data received (need > %s, got %s)",
								len_needed, recvbuf_.position());
				Log.d(TAG, text);
				return;
			}

			// move the buffer to the position to parse the contact header
			recvbuf_.position(8);
			int[] peer_eid_len = new int[1];
			peer_eid_len[0] = 0;
			int sdnv_len = SDNV.decode(recvbuf_, peer_eid_len);

			if (sdnv_len < 0) {
				String text = String
						.format(
								"handle_contact_initiation (missing for EID length field ): not enough data received (need > %s, got %s)",
								len_needed, recvbuf_.position());
				Log.d(TAG, text);
				return;
			}

			len_needed = 8 + sdnv_len + peer_eid_len[0];
			if (received_length < len_needed) {
				String text = String
						.format(
								"handle_contact_initiation ( missing for EID): not enough data received (need > %s, got %s)",
								len_needed, recvbuf_.position());
				Log.d(TAG, text);
				return;
			}

			// Ok, we have enough data, parse the contact header.
			//
			// Go back to the beginning position because we were moving position
			// to check the data above
			recvbuf_.rewind();

			ContactHeader contacthdr = new ContactHeader();
			contacthdr.magic = recvbuf_.getInt();
			contacthdr.version = recvbuf_.get();
			contacthdr.flags = recvbuf_.get();
			contacthdr.keepalive_interval = recvbuf_.getShort();
			handled_bytes += 8;

			/*
			 * "In this implementation, we can't handle other versions than our
			 * own, but if the other side presents a higher version, we allow it
			 * to go through and thereby allow them to downgrade to this
			 * version" [DTN2].
			 */
			byte cl_version = ((StreamConvergenceLayer) cl_).cl_version_;
			if (contacthdr.version < cl_version) {
				String text = String
						.format(
								"remote sent version %s, expected version %s -- disconnecting.",
								contacthdr.version, cl_version);
				Log.w(TAG, text);
				break_contact(ContactEvent.reason_t.CL_VERSION);
				return;
			}

			/*
			 * Now do parameter negotiation.
			 */
			StreamLinkParams params = stream_lparams();

			params.set_keepalive_interval(Math.min(params.keepalive_interval(),
					contacthdr.keepalive_interval));

			params
					.set_segment_ack_enabled(params.segment_ack_enabled()
							&& ((contacthdr.flags & contact_header_flags_t.SEGMENT_ACK_ENABLED
									.getCode()) > 0));

			params
					.set_reactive_frag_enabled(params.reactive_frag_enabled()
							&& ((contacthdr.flags & contact_header_flags_t.REACTIVE_FRAG_ENABLED
									.getCode()) > 0));

			params
					.set_negative_ack_enabled(params.negative_ack_enabled()
							&& ((contacthdr.flags & contact_header_flags_t.NEGATIVE_ACK_ENABLED
									.getCode()) > 0));

			/*
			 * "Make sure to readjust poll_timeout in case we have a smaller
			 * keepalive interval than data timeout"[DTN2]
			 */
			if (params.keepalive_interval() != 0
					&& (params.keepalive_interval() * 1000) < params
							.data_timeout()) {
				poll_timeout_ = params.keepalive_interval() * 1000;
			}

			/*
			 * "Now skip the sdnv that encodes the peer's eid length since we
			 * parsed it above"[DTN2].
			 */
			recvbuf_.position(recvbuf_.position() + sdnv_len);
			handled_bytes += sdnv_len;

			/*
			 * "Finally, parse the peer node's eid and give it to the base class
			 * to handle (i.e. by linking us to a Contact if we don't have
			 * one)"[DTN2].
			 */
			EndpointID peer_eid = new EndpointID();
			if (!peer_eid.assign(recvbuf_, peer_eid_len[0])) {
				String text = String.format(
						"protocol error: invalid endpoint id '%s' (len %s)",
						peer_eid, peer_eid_len);
				Log.e(TAG, text);
				break_contact(ContactEvent.reason_t.PEER_EID);
				return;
			}

			if (!find_contact(peer_eid)) {
				assert (contact_ == null);
				Log
						.d(
								TAG,
								String
										.format(
												"handle_contact_initiation: failed to find contact for peer eid %s ",
												peer_eid));
				break_contact(ContactEvent.reason_t.FIND_CONTACT);
				return;
			}
			recvbuf_.position(recvbuf_.position() + peer_eid_len[0]);
			handled_bytes += peer_eid_len[0];
			/*
			 * Make sure that the link's remote eid field is properly set.
			 */
			Link link = contact_.link();
			 
			if (link.remote_eid().equals(EndpointID.NULL_EID())) {
				link.set_remote_eid(peer_eid);
			}
			else if (!link.remote_eid().equals(peer_eid)) {
				String text = String
						.format(
								"handle_contact_initiation: remote eid mismatch: link remote eid was set to %s but peer eid is %s",
								link.remote_eid(), peer_eid);
				link.set_remote_eid(peer_eid);
				Log.w(TAG, text);
			}

			/*
			 * Finally, we note that the contact is now up.
			 */
			contact_up();

		} finally {
			BufferHelper.move_data_back_to_beginning(recvbuf_, handled_bytes);
			recvbuf_.position(last_position - handled_bytes);
 		}

	}

	private boolean handle_data_segment(byte flags) {
		// remember position before handling
		int last_position = recvbuf_.position();
		int consumed_len = 0;
		recvbuf_.rewind();

		boolean handle_todo_result = false;
		try {
			IncomingBundle incoming = null;
			if ((flags & data_segment_flags_t.BUNDLE_START.getCode()) > 0) {
				// "make sure we're done with the last bundle if we got a new
				// BUNDLE_START flag... note that we need to be careful in
				// case there's not enough data to decode the length of the
				// segment, since we'll be called again"[DTN2].
				boolean create_new_incoming = true;
				if (!incoming_.isEmpty()) {
					incoming = incoming_.back();

					if (incoming.rcvd_data().isEmpty()) {
						Log.d(TAG,
								"found empty incoming bundle for BUNDLE_START");
						create_new_incoming = false;
					} else if (incoming.total_length() == 0) {
						Log
								.e(TAG,
										"protocol error: got BUNDLE_START before bundle completed");
						break_contact(ContactEvent.reason_t.CL_ERROR);
						return false;
					}
				}

				if (create_new_incoming) {
					Log
							.d(TAG,
									"got BUNDLE_START segment, creating new IncomingBundle");
					IncomingBundle incoming2 = new IncomingBundle(new Bundle(
							BundlePayload.location_t.DISK));
					incoming_.add(incoming2);

				}

				if (uploading_)
					return false;

				handle_bundle_begin_download();

			} else if (incoming_.isEmpty()) {
				Log
						.e(TAG,
								"protocol error: first data segment doesn't have BUNDLE_START flag set");
				break_contact(ContactEvent.reason_t.CL_ERROR);
				return false;
			}

			// "Note that there may be more than one incoming bundle on the
			// IncomingList, but it's the one at the back that we're reading
			// in data for. Others are waiting for acks to be sent"[DTN2].
			incoming = incoming_.back();

			// move pass the CLMessage type and flag byte
			byte bp = recvbuf_.get();
			// always increment the data consumed
			consumed_len++;

			// Decode the segment length and then call handle_data_todo
			int[] segment_len = new int[1];
			int sdnv_len = SDNV.decode(recvbuf_, bp, segment_len);
			if (sdnv_len < 0) {
				Log.d(TAG,
						"handle_data_segment: too few bytes in buffer for sdnv "
								+ last_position);
				return false;
			}

			// always increment the data consumed
			consumed_len += sdnv_len;

			if (segment_len[0] == 0) {
				Log.e(TAG, "protocol error -- zero length segment");
				break_contact(ContactEvent.reason_t.CL_ERROR);
				return false;
			}

			int segment_offset = incoming.rcvd_data().size();
			String text = String
					.format(
							"handle_data_segment: got segment of length %d at offset %d ",
							segment_len[0], segment_offset);
			Log.d(TAG, text);

			// "if this is the last segment for the bundle, we calculate and
			// store the total length in the IncomingBundle structure so
			// send_pending_acks knows when we're done"[DTN2].
			if ((flags & (byte) data_segment_flags_t.BUNDLE_END.getCode()) > 0) {
				incoming.set_total_length(incoming.rcvd_data().size()
						+ segment_len[0]);

				Log.d(TAG, "got BUNDLE_END: total length "
						+ incoming.total_length());
			}

			recv_segment_todo_ = segment_len[0];

			int[] todo_handled_bytes = new int[1];
			handle_todo_result = handle_data_todo(last_position - consumed_len,
					todo_handled_bytes);

			consumed_len += todo_handled_bytes[0];

			return handle_todo_result;

		} finally {
			if (consumed_len > 0 && handle_todo_result) {
				BufferHelper
						.move_data_back_to_beginning(recvbuf_, consumed_len);
				recvbuf_.position(last_position - consumed_len);
			} else {
				// revert the data back in case we're unable to handle data
				recvbuf_.position(last_position);
			}
		}
	}

	private boolean handle_data_todo(int recv_len, int[] handled_bytes) {
		// remember position before handling
		int last_position = recvbuf_.position();
		handled_bytes[0] = 0;
		try {
			// "We shouldn't get ourselves here unless there's something
			// incoming and there's something left to read"[DTN2]
			assert (!incoming_.isEmpty()) : "Connection : handle_data_todo, incoming bundle list is empty";
			assert (recv_segment_todo_ != 0);

			// "Note that there may be more than one incoming bundle on the
			// IncomingList. There's always only one (at the back) that we're
			// reading in data for, the rest are waiting for acks to go
			// out"[DTN2]
			IncomingBundle incoming = incoming_.back();

			int chunk_len = Math.min(recv_len, recv_segment_todo_);

			if (recv_len == 0) {
				return false; // nothing to do
			}

			int rcvd_offset = incoming.rcvd_data().size();
			String text = String
					.format(
							"handle_data_todo: reading todo segment %s/%s at offset %s",
							chunk_len, recv_segment_todo_, rcvd_offset);
			Log.d(TAG, text);

			boolean[] last = new boolean[1];

			int cc = BundleProtocol.consume(incoming.bundle(), recvbuf_,
					chunk_len, last);
			if (cc < 0 || cc != chunk_len) {
				Log.e(TAG, "protocol error parsing bundle data segment");
				break_contact(ContactEvent.reason_t.CL_ERROR);
				return false;
			}

			recv_segment_todo_ -= cc;
			handled_bytes[0] += cc;

			int old_recv_size = incoming.rcvd_data().size();
			incoming.rcvd_data().set(old_recv_size + chunk_len - 1);

			if (recv_segment_todo_ == 0) {
				check_completed(incoming);

			}

			return true;
		} finally {
			recvbuf_.position(last_position);
		}
	}

	private boolean handle_ack_segment(byte flags) {

		Log.d(TAG, "handling ack segment");

		int last_position = recvbuf_.position();
		int consumed_len = 0;
		recvbuf_.rewind();

		try {
			int[] acked_len = new int[1];

			// get pass the type byte & flag
			recvbuf_.get();
			consumed_len += 1;

			int sdnv_len = SDNV.decode(recvbuf_, acked_len);

			if (sdnv_len < 0) {
				Log.d(TAG, "handle_ack_segment: too few bytes for sdnv "
						+ recvbuf_.position());

				// minus the one already pass
				consumed_len -= 1;
				return false;

			}

			consumed_len += sdnv_len;

			// recvbuf_.position(1 + sdnv_len);

			if (inflight_.isEmpty()) {
				Log
						.e(TAG,
								"protocol error: got ack segment with no inflight bundle");
				break_contact(ContactEvent.reason_t.CL_ERROR);
				return false;
			}

			InFlightBundle inflight = inflight_.get(0);

			int ack_begin = inflight.ack_data().size();

			Log.d(TAG, "received ack segment with ack_len " + acked_len[0]
					+ ", ack begin is " + ack_begin + ", sent data now is "
					+ inflight.sent_data().size());

			inflight.ack_data().set(acked_len[0] - 1);

			Log.d(TAG, String.format(
					"receving ACK for bundle %d until byte %d", inflight
							.bundle().bundleid(), inflight.ack_data().size()));
			// "now check if this was the last ack for the bundle, in which
			// case we can pop it off the list and post a
			// BundleTransmittedEvent"[DTN2]
			if (acked_len[0] == inflight.total_length()
					&& params_.segment_ack_enabled()) {
				String text = String
						.format(
								"handle_ack_segment: got final ack for %d byte range -- acked_len %d, ack_data %d",
								acked_len[0] - ack_begin, acked_len[0],
								inflight.ack_data().size());
				Log.d(TAG, text);

				inflight.set_transmit_event_posted(true);

				BundleDaemon Daemon = BundleDaemon.getInstance();
				Daemon.post(new BundleTransmittedEvent(inflight.bundle(),
						contact_, contact_.link(), inflight.sent_data().size(),
						inflight.ack_data().size()));

				// might delete inflight
				check_completed(inflight);

			} else {
				String text = String
						.format(
								"handle_ack_segment: got acked_len %d (%d byte range) -- ack_data %d",
								acked_len[0], acked_len[0] - ack_begin,
								inflight.ack_data().size());
				Log.d(TAG, text);
			}

			return true;

		} finally {
			if (consumed_len > 0) {
				BufferHelper
						.move_data_back_to_beginning(recvbuf_, consumed_len);
				recvbuf_.position(last_position - consumed_len);
			} else {
				recvbuf_.position(last_position);
			}
		}
	}

	private boolean handle_refuse_bundle(byte flags) {

		Log.d(TAG, "got refuse_bundle message");
		Log.e(TAG, "REFUSE_BUNDLE not implemented");
		break_contact(ContactEvent.reason_t.CL_ERROR);
		return true;
	}

	private boolean handle_keepalive(byte flags) {

		Log.d(TAG, "got keepalive message");

		BufferHelper.move_data_back_to_beginning(recvbuf_, 1);
		recvbuf_.rewind();
		return true;

	}

	private boolean handle_shutdown(byte flags) {

		int recv_len = recvbuf_.position();
		int last_position = recvbuf_.position();

		Log.d(TAG, "got SHUTDOWN byte");
		int shutdown_len = 1;
		int handled_len = 0;
		boolean has_reason = false;
		boolean has_delay = false;
		if ((flags & (byte) shutdown_flags_t.SHUTDOWN_HAS_REASON.getCode()) > 0) {
			shutdown_len += 1;
			has_reason = true;
		}

		if ((flags & (byte) shutdown_flags_t.SHUTDOWN_HAS_DELAY.getCode()) > 0) {

			// "check whether they have length for SDNV at least 1 to prevent
			// buffer overflow" [DTN2]
			if (recv_len < shutdown_len + 1)
				return false;

			int old_position = recvbuf_.position();
			try {
				// "check here in case of invalid SDNV by moving to the supposed
				// to
				// be delay length"[DTN2]
				recvbuf_.position(shutdown_len);
				int delay_SDNV_len = SDNV.len(recvbuf_);

				if (delay_SDNV_len < 0)
					return false;

				shutdown_len += 1;
				has_delay = true;
			} finally {
				recvbuf_.position(old_position);
			}

		}

		if (recv_len < shutdown_len) {
			// "rare case where there's not enough data in the buffer
			// to handle the shutdown message data"[DTN2]
			String text = String.format(
					"got %s/%s bytes for shutdown data... waiting for more",
					recvbuf_.position(), shutdown_len);
			Log.d(TAG, text);
			return false;
		}

		// now handle the message, first skipping the typecode byte
		recvbuf_.position(1);
		handled_len += 1;
		shutdown_reason_t reason = shutdown_reason_t.SHUTDOWN_NO_REASON;
		if (has_reason)

		{
			byte type_reason = recvbuf_.get();
			handled_len += 1;

			switch (shutdown_reason_t.get(type_reason)) {
			case SHUTDOWN_NO_REASON:
				reason = shutdown_reason_t.SHUTDOWN_NO_REASON;
				break;
			case SHUTDOWN_IDLE_TIMEOUT:
				reason = shutdown_reason_t.SHUTDOWN_IDLE_TIMEOUT;
				break;
			case SHUTDOWN_VERSION_MISMATCH:
				reason = shutdown_reason_t.SHUTDOWN_VERSION_MISMATCH;
				break;
			case SHUTDOWN_BUSY:
				reason = shutdown_reason_t.SHUTDOWN_BUSY;
				break;
			default:
				Log.e(TAG, "invalid shutdown reason code 0x" + recvbuf_.get(0));
			}

		}

		int delay[] = new int[1];
		if (has_delay) {
			int sdnv_len = SDNV.len(recvbuf_);
			if (sdnv_len < 0)
				return false;

			SDNV.decode(recvbuf_, delay);
			handled_len += sdnv_len;
		}

		String text = String.format(
				"got SHUTDOWN (%s)  [has reason %s , has delay %s with %s]",
				StreamConvergenceLayer.shutdown_reason_to_str(reason),
				has_reason, has_delay,

				delay[0]);
		Log.i(TAG, text);

		break_contact(ContactEvent.reason_t.SHUTDOWN);

		BufferHelper.move_data_back_to_beginning(recvbuf_, shutdown_len);
		recvbuf_.position(last_position - shutdown_len);

		return true;

	}

	private void check_completed(IncomingBundle incoming) {

		int rcvd_len = incoming.rcvd_data().size();

		// "if we don't know the total length yet, we haven't seen the
		// BUNDLE_END message"[DTN2]
		if (incoming.total_length() == 0) {
			return;
		}

		int formatted_len = BundleProtocol.total_length(incoming.bundle()
				.recv_blocks());

		String text = String.format(
				"check_completed: rcvd %s / %s (formatted length %s)",
				rcvd_len, incoming.total_length(), formatted_len);
		Log.d(TAG, text);

		if (rcvd_len < incoming.total_length()) {
			return;
		}

		if (rcvd_len > incoming.total_length()) {

			String text1 = String
					.format(
							"protocol error: received too much data -- got %s, total length %s",
							rcvd_len, incoming.total_length());
			Log.e(TAG, text1);

			// "we pretend that we got nothing so the cleanup code in
			// ConnectionCL::close_contact doesn't try to post a received
			// event for the bundle"[DTN2]
			incoming.rcvd_data().clear();
			break_contact(ContactEvent.reason_t.CL_ERROR);
			return;
		}

		// "validate that the total length as conveyed by the convergence
		// layer matches the length according to the bundle protocol"[DTN2]
		if (incoming.total_length() != formatted_len) {
			String text3 = String
					.format(
							"protocol error: CL total length %s doesn't match bundle protocol total %s",
							incoming.total_length(), formatted_len);
			Log.e(TAG, text3);

			incoming.rcvd_data().clear();
			break_contact(ContactEvent.reason_t.CL_ERROR);
			return;
		}

		handle_bundle_end_download(incoming);

		
		// if the acknowledgement is not enable, sent the Bundle to the Daemon here, otherwise, send it after the acknowledgement have been sent
		if (!params_.segment_ack_enabled())
		{
		BundleDaemon Daemon = BundleDaemon.getInstance();
		
		Daemon.post(new BundleReceivedEvent(incoming.bundle(),
				event_source_t.EVENTSRC_PEER, incoming.total_length(), contact_
						.link().remote_eid(), contact_.link()));
		}

	}

	/**
	 * Utility function to downcast the params_ pointer that's stored in the
	 * CLConnection parent class.
	 */
	StreamLinkParams stream_lparams() {
		StreamLinkParams ret = (StreamLinkParams) params_;
		assert (ret != null) : "Connection : stream_lparams, ret is null";
		return ret;
	}

	protected InFlightBundle current_inflight_; // /< Current bundle that's in
												// flight
	protected int send_segment_todo_; // /< Bytes left to send of current
										// segment
	protected int recv_segment_todo_; // /< Bytes left to recv of current
										// segment
	protected long data_rcvd_; // /< Timestamp for idle/keepalive timer
	protected long data_sent_; // /< Timestamp for idle timer
	protected long keepalive_sent_; // /< Timestamp for keepalive timer
	protected boolean breaking_contact_; // /< Bit to catch multiple calls to
											// break_contact
	protected boolean contact_initiated_; // < bit to prevent certain actions
											// before contact is initiated

}
