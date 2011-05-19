package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.prophet;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.SDNV;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;

public class ErrorTLV extends BaseTLV{
/*
	 0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    | TLV type=0x02 |     Flags     |          TLV Length           |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |                                                               |
    ~                               Data                            ~
    |                                                               |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    */
	public static void createTLV(IByteBuffer tlv, byte[] data) {
		tlv.put((byte)0x02);
		tlv.put((byte)0);
		SDNV.encode((4 + data.length), tlv,2);
		tlv.put(data);
	}
}
