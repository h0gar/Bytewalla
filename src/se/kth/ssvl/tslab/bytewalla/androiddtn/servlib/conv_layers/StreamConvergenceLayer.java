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

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import android.util.Log;

/**
 * "Another shared-implementation convergence layer class for use with reliable,
 * in-order delivery protocols (i.e. TCP, SCTP, and Bluetooth RFCOMM). The goal
 * is to share as much functionality as possible between protocols that have
 * in-order, reliable, delivery semantics.
 * 
 * For the protocol, bundles are broken up into configurable-sized segments that
 * are sent sequentially. Only a single bundle is inflight on the wire at one
 * time (i.e. we don't interleave segments from different bundles). When segment
 * acknowledgements are enabled (the default behavior), the receiving node sends
 * an acknowledgement for each segment of the bundle that was received.
 * 
 * Keepalive messages are sent back and forth to ensure that the connection
 * remains open. In the case of on demand links, a configurable idle timer is
 * maintained to close the link when no bundle traffic has been sent or
 * received. Links that are expected to be open but have broken due to
 * underlying network conditions (i.e. always on and on demand links) are
 * reopened by a timer that is managed by the contact manager.
 * 
 * Flow control is managed through the poll callbacks given by the base class
 * CLConnection. In send_pending_data, we check if there are any acks that need
 * to be sent, then check if there are bundle segments to be sent (i.e. acks are
 * given priority). The only exception to this is that the connection might be
 * write blocked in the middle of sending a data segment. In that case, we must
 * first finish transmitting the current segment before sending any other acks
 * (or the shutdown message), otherwise those messages will be consumed as part
 * of the payload.
 * 
 * To make sure that we don't deadlock with the other side, we always drain any
 * data that is ready on the channel. All incoming messages mark state in the
 * appropriate data structures (i.e. InFlightList and IncomingList), then rely
 * on send_pending_data to send the appropriate responses.
 * 
 * The InflightBundle is used to record state about bundle transmissions. To
 * record the segments that have been sent, we fill in the sent_data_ sparse
 * bitmap with the range of bytes as we send segments out. As acks arrive, we
 * extend the ack_data_ field to match. Once the whole bundle is acked, the
 * entry is removed from the InFlightList.
 * 
 * The IncomingBundle is used to record state about bundle reception. The
 * rcvd_data_ bitmap is extended contiguously with the amount of data that has
 * been received, including partially received segments. To track segments that
 * we have received but haven't yet acked, we set a single bit for the offset of
 * the end of the segment in the ack_data_ bitmap. We also separately record the
 * total range of acks that have been previously sent in acked_length_. As we
 * send acks out, we clear away the bits in ack_data_" [DTN2].
 * 
 * @author María José Peroza Marval (mjpm@kth.se)
 */

public abstract class StreamConvergenceLayer extends ConnectionConvergenceLayer {

	/**
	 * TAG for Android Logging mechanism
	 */
	private static final String TAG = "StreamConvergenceLayer";

	/**
	 * Constructor
	 */
	public StreamConvergenceLayer() {
		super();
	}

	/**
	 * Values for ContactHeader flags.
	 */

	protected enum contact_header_flags_t {
		SEGMENT_ACK_ENABLED(1 << 0), REACTIVE_FRAG_ENABLED(1 << 1), NEGATIVE_ACK_ENABLED(
				1 << 2);

		private static final Map<Integer, contact_header_flags_t> lookup = new HashMap<Integer, contact_header_flags_t>();

		static {
			for (contact_header_flags_t s : EnumSet
					.allOf(contact_header_flags_t.class))
				lookup.put(s.getCode(), s);
		}

		private int code;

		private contact_header_flags_t(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static contact_header_flags_t get(int code) {
			return lookup.get(code);
		}

	}

	/**
	 * "Valid type codes for the protocol messages, shifted into the high-order
	 * four bits of the byte. The lower four bits are used for per-message
	 * flags, defined below"[DTN2].
	 */

	public enum msg_type_t {
		DATA_SEGMENT(0x1 << 4), // /< a segment of bundle data (followed by a
								// SDNV segment length)
		ACK_SEGMENT(0x2 << 4), // /< acknowledgement of a segment (followed by a
								// SDNV ack length)
		REFUSE_BUNDLE(0x3 << 4), // /< reject reception of current bundle
		KEEPALIVE(0x4 << 4), // /< keepalive packet
		SHUTDOWN(0x5 << 4); // /< about to shutdown

		private static final Map<Integer, msg_type_t> lookup = new HashMap<Integer, msg_type_t>();

		static {
			for (msg_type_t s : EnumSet.allOf(msg_type_t.class))
				lookup.put(s.getCode(), s);
		}

		private int code;

		private msg_type_t(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static msg_type_t get(int code) {
			return lookup.get(code);
		}

	}

	/**
	 * Valid flags for the DATA_SEGMENT message.
	 */

	public enum data_segment_flags_t {
		BUNDLE_START(0x1 << 1), // /< First segment of a bundle
		BUNDLE_END(0x1); // /< Last segment of a bundle

		private static final Map<Integer, data_segment_flags_t> lookup = new HashMap<Integer, data_segment_flags_t>();

		static {
			for (data_segment_flags_t s : EnumSet
					.allOf(data_segment_flags_t.class))
				lookup.put(s.getCode(), s);
		}

		private int code;

		private data_segment_flags_t(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static data_segment_flags_t get(int code) {
			return lookup.get(code);
		}

	}

	/**
	 * Valid flags for the SHUTDOWN message.
	 */

	public enum shutdown_flags_t {
		SHUTDOWN_HAS_REASON(0x1 << 1), // /< Has reason code
		SHUTDOWN_HAS_DELAY(0x1 << 0); // /< Has reconnect delay

		private static final Map<Integer, shutdown_flags_t> lookup = new HashMap<Integer, shutdown_flags_t>();

		static {
			for (shutdown_flags_t s : EnumSet.allOf(shutdown_flags_t.class))
				lookup.put(s.getCode(), s);
		}

		private int code;

		private shutdown_flags_t(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static shutdown_flags_t get(int code) {
			return lookup.get(code);
		}

	}

	/**
	 * Values for the SHUTDOWN reason codes
	 */

	public enum shutdown_reason_t {
		SHUTDOWN_NO_REASON(0xff), // /< no reason code (never sent)
		SHUTDOWN_IDLE_TIMEOUT(0x0), // /< idle connection
		SHUTDOWN_VERSION_MISMATCH(0x1), // /< version mismatch
		SHUTDOWN_BUSY(0x2); // /< node is busy

		private static final Map<Integer, shutdown_reason_t> lookup = new HashMap<Integer, shutdown_reason_t>();

		static {
			for (shutdown_reason_t s : EnumSet.allOf(shutdown_reason_t.class))
				lookup.put(s.getCode(), s);
		}

		private int code;

		private shutdown_reason_t(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static shutdown_reason_t get(int code) {
			return lookup.get(code);
		}

	}

	/**
	 * Convert a reason code to a string.
	 **/
	public static String shutdown_reason_to_str(shutdown_reason_t reason) {
		switch (reason) {
		case SHUTDOWN_NO_REASON:
			return "no reason";
		case SHUTDOWN_IDLE_TIMEOUT:
			return "idle connection";
		case SHUTDOWN_VERSION_MISMATCH:
			return "version mismatch";
		case SHUTDOWN_BUSY:
			return "node is busy";
		}
		Log.d(TAG, "Not Reached");
		return "NOTREACHED";
	}

	/**
	 * Version of the actual CL protocol.
	 */
	byte cl_version_;

	@Override
	public void dump_link(Link link, StringBuffer buf) {

		assert (link != null) : "StreamConvergenceLayer : dump_link, link is null";
		assert (!link.isdeleted()) : "StreamConvergenceLayer : dump_link, link is deleted";
		assert (link.cl_info() != null) : "StreamConvergenceLayer : dump_link, cl_info is null";

		super.dump_link(link, buf);

		StreamLinkParams params = (StreamLinkParams) (link.cl_info());
		assert (params != null) : "StreamConvergenceLayer : dump_link, params are null";

		buf.append("segment_ack_enabled: "
				+ String.valueOf(params.segment_ack_enabled()) + "\n");
		buf.append("negative_ack_enabled: "
				+ String.valueOf(params.negative_ack_enabled()) + "\n");
		buf.append("keepalive_interval: "
				+ String.valueOf(params.keepalive_interval()) + "\n");
		buf.append("segment_length: " + String.valueOf(params.segment_length())
				+ "\n");

	}

	
	@Override
	public abstract CLConnection new_connection(Link link, LinkParams params);

	
	@Override
	public abstract LinkParams new_link_params();

	
	@Override
	public abstract boolean parse_nexthop(Link link, LinkParams params);

	@Override
	public boolean finish_init_link(Link link, LinkParams lparams) {

		StreamLinkParams params = (StreamLinkParams) (lparams);
		assert (params != null) : "StreamConvergenceLayer : finish_init_link, params are null";

		// make sure to set the reliability bit in the link structure
		if (params.segment_ack_enabled()) {
			link.set_reliable(true);
		}

		return true;

	}

}
