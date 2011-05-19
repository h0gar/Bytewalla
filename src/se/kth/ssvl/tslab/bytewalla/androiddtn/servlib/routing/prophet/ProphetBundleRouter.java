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
package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.prophet;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

import se.kth.ssvl.tslab.bytewalla.androiddtn.DTNManager;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.Bundle;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleDaemon;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.SDNV;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.Bundle.priority_values_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundlePayload.location_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleDeleteRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ContactUpEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointIDPattern;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.RouteEntry;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.TableBasedRouter;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.prophet.BaseTLV.TLVType;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.prophet.HelloTLV.HelloFunctionType;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.prophet.ProphetNeighbor.ProphetNeighborRecvState;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.prophet.ProphetNeighbor.ProphetNeighborSendState;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.SerializableByteBuffer;
import android.util.Log;

/**
 * This is a non-abstract version of TableBasedRouter.
 * 
 * @author Mahesh Bogadi Shankar Prasad (mabsp@kth.se)
 */

public class ProphetBundleRouter extends TableBasedRouter {
	public static enum prophet_header_result {

		AckAll("AckAll", (byte) 0x01), Failure("Failure", (byte) 0x03), NoSuccessAck(
				"NoSuccessAck", (byte) 0x00), ReturnReceipt("ReturnReceipt",
				(byte) 0x04), Success("Success", (byte) 0x02);

		private static final Map<String, prophet_header_result> caption_map = new HashMap<String, prophet_header_result>();
		private static final Map<Byte, prophet_header_result> lookup = new HashMap<Byte, prophet_header_result>();

		static {
			for (prophet_header_result s : EnumSet
					.allOf(prophet_header_result.class)) {
				lookup.put(s.getCode(), s);
				caption_map.put(s.getCaption(), s);
			}

		}

		public static prophet_header_result get(byte code) {
			return lookup.get(code);
		}

		private String caption_;

		private byte code_;

		private prophet_header_result(String caption, byte code) {
			this.caption_ = caption;
			this.code_ = code;
		}

		public String getCaption() {
			return caption_;
		}

		public byte getCode() {
			return code_;
		}

	}

	private static ProphetBundleRouter instance;

	/*
	 * NoSuccessAck: Result = 1 AckAll: Result = 2 Success: Result = 3 Failure:
	 * Result = 4 ReturnReceipt Result = 5
	 */

	private final static String TAG = "ProphetBundleRouter";

	public static ProphetBundleRouter getInstance() {
		return instance;
	}

	private static HashMap<String, ProphetNeighbor> neighbors = new HashMap<String, ProphetNeighbor>();

	private ProphetRegistration registration = new ProphetRegistration(this);

	/*
	 * 0 1 2 3 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * Protocol |Version| Flags | Result | Code |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * Receiver Instance | Sender Instance |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * Transaction Identifier |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |S|
	 * SubMessage Number | Length (SDNV) |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ | | ~
	 * Message Body ~ | |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 */

	public ProphetBundleRouter() {
		super();
		instance = this;
	}

	@Override
	public ProphetRegistration getProphetRegistration() {
		return registration;
	}

	public String localEid() {
		return BundleDaemon.getInstance().local_eid().str();
	}

	public byte SDNVByteDecode(IByteBuffer buf) {
		int[] val = new int[1];
		SDNV.decode(buf, 1, val);
		return (byte) val[0];
	}

	public short SDNVShortDecode(IByteBuffer buf) {
		int[] val = new int[1];
		SDNV.decode(buf, 2, val);
		return (short) val[0];
	}

	private byte[] adjustLenAndReturnArray(IByteBuffer buf) {
		int len = buf.position();
		buf.position(14);
		buf.putShort((short) len);
		byte array[] = new byte[len];

		for (int i = 0; i < len; i++)
			array[i] = buf.array()[i];
		return array;
	}

	private IByteBuffer createProphetBundle(ProphetNeighbor pn) {
		IByteBuffer prophetBuffer = new SerializableByteBuffer(1000);
		prophetBuffer.rewind();
		// Protocol
		prophetBuffer.put((byte) 140);
		// Version | Flags
		prophetBuffer.put((byte) (0x01 << 4));
		// result
		prophetBuffer.put(prophet_header_result.AckAll.getCode());
		// code
		prophetBuffer.put((byte) 0);

		// Receiver Instance
		prophetBuffer.putShort(pn.remote_instance_);
		// Sender Instance
		prophetBuffer.putShort(pn.local_instance_);
		// Transaction Identifier
		prophetBuffer.putInt(pn.sendTransactionId);
		// submessage number
		prophetBuffer.putShort((short) 0);

		// length
		prophetBuffer.putShort((short) 0);
		return prophetBuffer;
	}

	/**
	 * @param buf
	 * @param hello
	 */
	private String getString(IByteBuffer buf, int length) {
		byte[] b = new byte[length];
		buf.get(b);
		return new String(b);
	}

	public void deliver_bundle(Bundle bundle) {
		IByteBuffer buf = new SerializableByteBuffer(1000);

		// Prophet Control bundle
		if (!bundle.payload().read_data(0, bundle.payload().length(), buf)) {
			Log.e(TAG, "Erruor reading prophet bundle");
			return;
		}

		// Log.d(TAG, toString(buf.array()));
		Log.d(TAG, "Bundle with length " + bundle.payload().length());

		ProphetBundle hdr = parseProphetBundle(buf);
		ProphetNeighbor pn;

		String remote_eid = bundle.source().str().split("/prophet")[0];
		if (remote_eid == null) {
			Log.e("TAG", "Bundle recv : remote_eid == null");
			return;
		}

		BundleDaemon
				.getInstance()
				.post_at_head(
						new BundleDeleteRequest(
								bundle,
								BundleProtocol.status_report_reason_t.REASON_NO_ADDTL_INFO));

		if ((pn = neighbors.get(remote_eid)) == null) {
			pn = new ProphetNeighbor(remote_eid);
			neighbors.put(remote_eid, pn);
		}
		pn.recvTansactionId = hdr.trans_id;
		Log.d(TAG, hdr.toString());

		/* First byte is type */
		switch (hdr.type) {
		case HELLO:
			handleHello(buf, pn, hdr);
			break;
		case ERROR:
			handleError(buf, pn, hdr);
			break;
		case RIBDICTIONARY:
			handleRIBDictionary(pn, hdr);
			break;
		case RIBINFORMATIONBASE:
			handleRIBInformationBase(pn, hdr);
			break;
		case BUNDLEOFFER:
			handleBundleOffer(buf, pn, hdr);
			break;
		case BUNDLERESPONSE:
			handleBundleResponse(buf, pn, hdr);
			break;
		default:
			Log.e(TAG, String.format("Unknown Prophet control(%x) from %s",
					hdr.type, pn.remote_eid()));
		}

		/* received both */
		if (hdr.type == TLVType.RIBDICTIONARY
				|| hdr.type == TLVType.RIBINFORMATIONBASE
				|| (hdr.type == TLVType.HELLO && hdr.hello.function == HelloFunctionType.ACK_SUP_RECALC)) {
			if (pn.rIBDictionary != null && pn.rIBInformation != null
					&& pn.acked) {
				updateNeighborP_(pn);
//				sendBundleOffer(pn);
				pn.setRecvState(ProphetNeighborRecvState.BUNDLEOFFER_SENT);
				reroute_all_bundles();
			}
		}
	}

	private void handleError(IByteBuffer buf, ProphetNeighbor controller,
			ProphetBundle hdr) {
		Log.d(TAG, "Received ERROR from " + controller.remote_eid());
	}

	private void handleHello(IByteBuffer buf, ProphetNeighbor pn,
			ProphetBundle hdr) {
		Log.d(TAG, String.format("Received HELLO(%s) from %s ",
				hdr.hello.function.getCaption(), pn.remote_eid()));
		notify(String.format("Received HELLO(%s)", hdr.hello.function
				.getCaption()), String.format("From %s", pn.remote_eid()));

		switch (hdr.hello.function) {
		case SYN:
			sendHello(pn, HelloFunctionType.SYNACK_SUP_RECALC);
			pn.setRecvState(ProphetNeighborRecvState.SYN_ACK_SENT);

			/* new database is arriving */
			pn.rIBDictionary = null;
			pn.rIBInformation = null;
			pn.acked = false;
			break;
		case SYNACK_EXE_RECALC:
		case SYNACK_SUP_RECALC:
			if (pn.getSendState() == ProphetNeighborSendState.SYN_SENT) {
				pn.setSendState(ProphetNeighborSendState.SYNACK_RCVD);
				sendNext(pn);
			}
			break;
		case ACK_EXE_RECALC:
		case ACK_SUP_RECALC:
			pn.acked = true;
			break;
		case RSTACK:
			break;
		}
	}

	private void handleRIBDictionary(ProphetNeighbor pn, ProphetBundle hdr) {
		Log.d(TAG, "Received RIBDictionary from " + pn.remote_eid());
		notify("Received RIBDictionary", String.format("From %s", pn
				.remote_eid()));
		pn.rIBDictionary = hdr.rIBDictionary;
	}

	private void handleRIBInformationBase(ProphetNeighbor pn, ProphetBundle hdr) {
		Log.d(TAG, "Received RIBInformationBase from " + pn.remote_eid());
		notify("Received RIBInformationBase", String.format("From %s", pn
				.remote_eid()));
		pn.rIBInformation = hdr.rIBInformationBase;
	}

	/**
	 * @param pn
	 */
	private void updateNeighborP_(ProphetNeighbor pn) {
		Iterator<Short> sid = pn.rIBInformation.entries.keySet().iterator();
		EndpointIDPattern pneid = new EndpointIDPattern(pn.remote_eid() + "/*");

		while (sid.hasNext()) {
			Short ssid = sid.next();
			String neid = pn.rIBDictionary.entries.get(ssid).eid;
			if (neid.equals(localEid()))
				continue;

			if (neid.equals(pn.remote_eid())) {
				continue;
			}

			float P = pn.rIBInformation.entries.get(ssid).pValue;
			ProphetNeighbor p = neighbors.get(neid);
			if (p == null) {
				p = new ProphetNeighbor(neid);
			}

			/*
			 * pn.P_() is P_(A-B) P is P_(B-C)
			 */
			p.update_transitivity(pn.P_(), P);
			Log.i(TAG, "transitivity updated " +  p.P_() + " from " + p);
			
			/* my propbability is less than this */
			if (p.P_() <= P) {
				EndpointIDPattern nepnp = new EndpointIDPattern(neid + "/*");

				route_table_.add_entry(new RouteEntry(nepnp, pneid));
				Log.i(TAG, "Added route " + pn.remote_eid() + " - " + p.P_()
						+ " " + neid + " - " + P);
			} else {
				Log.i(TAG, "Skipped route " + pn.remote_eid() + " - " + p.P_()
						+ " " + neid + " - " + P);
			}
		}

		pn.rIBDictionary = null;
		pn.rIBInformation = null;
		pn.acked = false;
	}

	private void sendBundleOffer(ProphetNeighbor pn) {
		IByteBuffer buf = createProphetBundle(pn);
		createBundleOfferTLV(buf, pn);
		sendMsg(adjustLenAndReturnArray(buf), pn);
		Log.d(TAG, String.format("send Bundle offer %s", pn.remote_eid()));
		notify(String.format("send Bundle offer"), String.format("To %s", pn
				.remote_eid()));
	}

	private void notify(String s1, String s2) {
		DTNManager.getInstance().notify_user(s1, s2);
	}

	private void handleBundleOffer(IByteBuffer buf, ProphetNeighbor pn,
			ProphetBundle hdr) {
		Log.d(TAG, "Received BundleOffer from " + pn.remote_eid());
		pn.bundleOffer = hdr.bundleOffer;
		sendBundleResponse(pn);
	}

	private void sendBundleResponse(ProphetNeighbor pn) {
		if (pn.bundleOffer == null)
			return;

		IByteBuffer buf = createProphetBundle(pn);

		BundleResponseTLV.createTLV(buf, pn.bundleOffer.entries);

		sendMsg(adjustLenAndReturnArray(buf), pn);
		Log.d(TAG, String.format("send Bundle Response %s", pn.remote_eid()));
		notify(String.format("send Bundle Response"), String.format("To %s", pn
				.remote_eid()));
		pn.bundleOffer = null;
	}

	private ProphetBundle parseProphetBundle(IByteBuffer buf) {
		ProphetBundle bundle = new ProphetBundle();
		buf.rewind();

		// Protocol
		bundle.protocol = buf.get();

		// Version | Flags
		bundle.versionFlags = buf.get();

		// result
		bundle.result = buf.get();

		// code
		bundle.code = buf.get();

		// Receiver Instance
		bundle.receiver = buf.getShort();

		// Sender Instance
		bundle.sender = buf.getShort();

		// Transaction Identifier
		bundle.trans_id = buf.getInt();

		// submessage number
		bundle.submessage = buf.getShort();

		// length
		bundle.length = buf.getShort();

		// type
		bundle.type = TLVType.get(buf.get());

		switch (bundle.type) {
		case HELLO:
			Hello hello = bundle.hello;
			hello.function = HelloFunctionType.get(buf.get());
			hello.length = SDNVShortDecode(buf);
			hello.timer = SDNVByteDecode(buf);
			hello.eid_length = SDNVByteDecode(buf);
			hello.eid = getString(buf, hello.eid_length);
			break;
		case RIBDICTIONARY:
			RIBDictionary d = bundle.rIBDictionary;
			d.flags = buf.get();
			d.length = SDNVShortDecode(buf);
			d.rIBDEntryCount = SDNVShortDecode(buf);
			d.reserved = buf.getShort();

			for (int i = 0; i++ < d.rIBDEntryCount;) {
				RIBDictionaryEntry e = new RIBDictionaryEntry();
				e.stringID = SDNVShortDecode(buf);
				e.length = SDNVByteDecode(buf);
				e.reserved = buf.get();
				e.eid = getString(buf, e.length);
				d.entries.put(e.stringID, e);
			}
			break;
		case RIBINFORMATIONBASE:
			RIBInformationBase d1 = bundle.rIBInformationBase;
			d1.flags = buf.get();
			d1.length = SDNVShortDecode(buf);
			d1.rIBStringCount = SDNVShortDecode(buf);
			d1.reserved = buf.getShort();

			for (int i = 0; i++ < d1.rIBStringCount;) {
				RIBInformationBaseEntry e = new RIBInformationBaseEntry();
				e.stringID = SDNVShortDecode(buf);
				e.pValue = ProphetNeighbor.getPFloat(SDNVShortDecode(buf));
				e.flags = buf.get();
				d1.entries.put(e.stringID, e);
			}
			break;
		case BUNDLEOFFER:
			BundleOffer bo = bundle.bundleOffer;
			// flags
			bo.flags = buf.get();
			// length
			bo.length = SDNVShortDecode(buf);
			// offer count
			bo.offerCount = SDNVShortDecode(buf);

			// reserve
			bo.reserve = buf.getShort();

			for (int i = 0; i++ < bo.offerCount;) {
				BundleOfferEntry boe = new BundleOfferEntry();
				// ID
				boe.id = SDNVShortDecode(buf);

				// B_flags
				boe.flags = buf.get();
				boe.reserve = buf.get();

				// Creation Timestamp time
				boe.creationTime = SDNVShortDecode(buf);
				// Creation Timestamp sequence number
				boe.seqNo = SDNVShortDecode(buf);

				bo.entries.add(boe);
			}

			break;

		case BUNDLERESPONSE:
			BundleResponse br = bundle.bundleResponse;
			// flags
			br.flags = buf.get();
			// length
			br.length = SDNVShortDecode(buf);
			// offer count
			br.offerCount = SDNVShortDecode(buf);

			// reserve
			br.reserve = buf.getShort();

			for (int i = 0; i++ < br.offerCount;) {
				BundleResponseEntry bre = new BundleResponseEntry();
				// ID
				bre.id = SDNVShortDecode(buf);

				// B_flags
				bre.flags = buf.get();
				bre.reserve = buf.get();

				// Creation Timestamp time
				bre.creationTime = SDNVShortDecode(buf);
				// Creation Timestamp sequence number
				bre.seqNo = SDNVShortDecode(buf);
				br.entries.add(bre);
			}

			break;
		}

		return bundle;
	}

	/*
	 * private int SDNVIntDecode(IByteBuffer buf) { int[] val = new int[1];
	 * SDNV.decode(buf, 4, val); return (short) val[0]; }
	 */
	private void handleBundleResponse(IByteBuffer buf, ProphetNeighbor pn,
			ProphetBundle hdr) {
		Log.d(TAG, "Received Bundle Response from " + pn.remote_eid());
		reroute_all_bundles();
	}

	/* send the error */
	@SuppressWarnings("unused")
	private void sendError(ProphetNeighbor pn) {
		IByteBuffer buf = createProphetBundle(pn);
		ErrorTLV.createTLV(buf, new byte[2]);
		sendMsg(adjustLenAndReturnArray(buf), pn);
		Log.d(TAG, "send Error " + pn.remote_eid());
	}

	/* send the hello */
	private void sendHello(ProphetNeighbor pn, HelloFunctionType helloFun) {
		IByteBuffer buf = createProphetBundle(pn);
		HelloTLV.createTLV(buf, helloFun);

		sendMsg(adjustLenAndReturnArray(buf), pn);
		Log.d(TAG, String.format("send Hello(%s) %s", helloFun.getCaption(), pn
				.remote_eid()));
		notify(String.format("send Hello(%s)", helloFun.getCaption()), String
				.format("To %s", pn.remote_eid()));
	}

	private void sendMsg(final byte[] payload, ProphetNeighbor pn) {
		Bundle bundle = new Bundle(location_t.MEMORY);
		bundle.set_dest(new EndpointID(pn.remote_eid() + "/prophet"));
		bundle.set_source(new EndpointID(BundleDaemon.getInstance().local_eid()
				.str()
				+ "/prophet"));
		bundle.set_prevhop(BundleDaemon.getInstance().local_eid());
		bundle.set_custodian(EndpointID.NULL_EID());
		bundle.set_replyto(new EndpointID(BundleDaemon.getInstance()
				.local_eid().str()
				+ "/prophet"));
		bundle.set_singleton_dest(true);
		bundle.set_expiration(10000);
		bundle.set_priority(priority_values_t.COS_EXPEDITED);
		bundle.payload().set_data(payload);

		Log.d(TAG, toString(payload));
		// BundleDaemon.getInstance().post_at_head(new
		// BundleReceivedEvent(bundle, event_source_t.EVENTSRC_ADMIN));
		route_bundle(bundle);
		pn.timestamp = new Date().getTime();
	}

	private void sendNext(ProphetNeighbor pn) {
		switch (pn.getSendState()) {
		case RIBIB_SENT:
			if (pn.timestamp + 5 * 60 * 1000 < new Date().getTime()) {
				pn.setSendState(ProphetNeighborSendState.UNDEFINED);
				sendNext(pn);
			}
			break;
		case UNDEFINED:
			sendHello(pn, HelloFunctionType.SYN);
			pn.setSendState(ProphetNeighborSendState.SYN_SENT);
			pn.update_encounter();
			break;
		case SYN_SENT:
			break;
		case SYNACK_RCVD:
			sendHello(pn, HelloFunctionType.ACK_SUP_RECALC);
			pn.setSendState(ProphetNeighborSendState.SYN_ACK_ACK_SENT);
			sendNext(pn);
			break;
		case SYN_ACK_ACK_SENT:
			sendRIBDictionary(pn);
			pn.setSendState(ProphetNeighborSendState.RIBDICTIONARY_SENT);
			sendNext(pn);
			break;
		case RIBDICTIONARY_SENT:
			sendRIBInformation(pn);
			pn.setSendState(ProphetNeighborSendState.RIBIB_SENT);
			sendNext(pn);
			break;
		}
	}

	private void sendRIBDictionary(ProphetNeighbor pn) {
		IByteBuffer buf = createProphetBundle(pn);
		RIBDictionaryTLV.createTLV(buf, neighbors);
		sendMsg(adjustLenAndReturnArray(buf), pn);
		Log.d(TAG, "Send RIBDictionary " + pn.remote_eid());
		notify("send RIBDictionary", String.format("To %s", pn.remote_eid()));
	}

	private void sendRIBInformation(ProphetNeighbor pn) {
		RIBInformationBaseTLV ribInfo = new RIBInformationBaseTLV();
		IByteBuffer buf = createProphetBundle(pn);
		ribInfo.createTLV(buf, neighbors);
		sendMsg(adjustLenAndReturnArray(buf), pn);
		Log.d(TAG, "send RIBInformation " + pn.remote_eid());
		notify("send RIBInformationBase", String.format("To %s", pn
				.remote_eid()));
	}

	private String toString(byte[] br) {
		String st = "";
		for (byte b : br) {
			st += String.format("%x", b);
		}
		return st;
	}

	// protected void handle_bundle_transmitted(BundleTransmittedEvent event) {
	// String eid = event.bundle().dest().str();
	// super.handle_bundle_transmitted(event);
	//
	// if (eid.endsWith("/prophet")) {
	// Log.i(TAG, "########Prophet Bundle Transmitted");
	// ProphetNeighbor pn = neighbors.get(eid.split("/prophet")[0]);
	// sendNext(pn);
	// }
	// }

	/* Contact up */
	@Override
	protected void handle_contact_up(ContactUpEvent event) {
		super.handle_contact_up(event);

		ProphetNeighbor pn = null;
		String remote_eid = event.contact().link().remote_eid().str();

		if (remote_eid.equals(localEid())) {
			Log.d(TAG, "Link ID is equal self");
			return;
		}
		// Add new contact.
		if ((pn = neighbors.get(remote_eid)) == null) {
			pn = new ProphetNeighbor(remote_eid);
			neighbors.put(pn.remote_eid(), pn);
			Log.d(TAG, "New neighbor " + pn.remote_eid());
		}

		sendNext(pn);
	}

	// TLV Type
	// The TLV Type for a Bundle Offer is 0xA2. The TLV Type for a
	// Bundle Response is 0xA3.

	/*
	 * 0 1 2 3 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ | TLV
	 * Type | Flags | Length |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * Bundle Offer Count | Reserved |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * Bundle Dest String Id 1 (SDNV)| B_flags | resv |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * Bundle 1 Creation Timestamp time | | (variable length SDNV) |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * Bundle 1 Creation Timestamp sequence number | | (variable length SDNV) |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ ~ . ~ ~
	 * . ~ ~ . ~
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * Bundle Dest String Id n (SDNV)| B_flags | resv |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * Bundle n Creation Timestamp time | | (variable length SDNV) |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * Bundle n Creation Timestamp sequence number | | (variable length SDNV) |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 */

	public void createBundleOfferTLV(IByteBuffer buf, ProphetNeighbor pn) {
		try {
			pending_bundles_.get_lock().lock();
			ListIterator<Bundle> bundles = pending_bundles_.begin();

			buf.put(BaseTLV.TLVType.BUNDLEOFFER.getCode());
			int start = buf.position();
			buf.put((byte) 0);
			// length
			SDNV.encode(4, buf, 2);
			// offer count
			SDNV.encode(0, buf, 2);
			// reserve
			buf.putShort((short) 0);

			int offerCount = 0;
			Log.d(TAG, "No of pending bundles " + pending_bundles_.size());
			while (bundles.hasNext()) {
				Bundle b = bundles.next();

				if (b.dest().str().endsWith("/prophet")) {
					continue;
				}

				Log.d(TAG, b.bundleid() + " is offered to " + b.dest());
				// ID
				SDNV.encode(b.bundleid(), buf, 2);
				// B_flags
				buf.putShort((byte) 0);

				// Creation Timestamp time
				SDNV.encode(b.creation_ts().seconds(), buf, 2);
				// Creation Timestamp sequence number
				SDNV.encode(b.creation_ts().seqno(), buf, 2);
				offerCount++;
			}
			int end = buf.position();

			/* fill the header */
			buf.position(start);
			buf.put((byte) 0);
			// length
			SDNV.encode(end - start, buf, 2);
			// offer count
			SDNV.encode(offerCount, buf, 2);

			buf.position(end);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
