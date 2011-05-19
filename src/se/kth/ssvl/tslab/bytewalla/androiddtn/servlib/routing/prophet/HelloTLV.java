package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.prophet;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleDaemon;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.SDNV;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;

public class HelloTLV extends BaseTLV{
	
	public static enum HelloFunctionType {
		
		//           Requesting execution of recalculation phase:
//                 Hello Function = 3
//           Requesting suppression of recalculation phase:
//                 Hello Function = 131
        ACK_EXE_RECALC("ACK_EXE_RECALC", (byte)3),
ACK_SUP_RECALC("ACK", (byte)131),
RSTACK("RSTACK", (byte)4),

SYN("SYN", (byte)1),
        //		Requesting execution of recalculation phase:
//            Hello Function = 2
//      
        SYNACK_EXE_RECALC("SYNACK_EX_RECALC", (byte)2 ),
        //           Requesting suppression of recalculation phase:
//                 Hello Function = 130
        SYNACK_SUP_RECALC("SYNACK", (byte)130);	
		
		private static final Map<String, HelloFunctionType> caption_map = new HashMap<String, HelloFunctionType>();
		private static final Map<Byte, HelloFunctionType> lookup = new HashMap<Byte, HelloFunctionType>();

		static {
			for (HelloFunctionType s : EnumSet.allOf(HelloFunctionType.class)) {
				lookup.put(s.getCode(), s);
				caption_map.put(s.getCaption(), s);
			}

		}

		public static HelloFunctionType get(byte code) {
			return lookup.get(code);
		}
		private String caption_;

		private byte code_;

		private HelloFunctionType(String caption, byte code) {
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

	/*
	0                   1                   2                   3
    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   | TLV Type=0x01 | Hello Function|          TLV Length           |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   | Timer (SDNV)  |EID Length,SDNV|  Sender EID (variable length) |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   */

	public static void createTLV(IByteBuffer tlvBuf, HelloFunctionType func) {
		tlvBuf.put(TLVType.HELLO.getCode());
		tlvBuf.put(func.getCode());
		String eid = BundleDaemon.getInstance().local_eid().toString();
		SDNV.encode(eid.length() + 6, tlvBuf, 2);
		SDNV.encode(10, tlvBuf, 1);
		SDNV.encode(eid.length(), tlvBuf, 1);
		tlvBuf.put(eid.getBytes());
	}
}
