package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.prophet;

import java.util.HashMap;
import java.util.Iterator;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.SDNV;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;

public class RIBInformationBaseTLV extends BaseTLV {
	
	/*
	 * 0                   1                   2                   3
       0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      | TLV Type=0xA1 |   Flags       |           Length              |
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      |     RIB String Count (SDNV)   |           Reserved            |
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      |     RIBD String ID 1 (SDNV)   |            P-Value            |
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      |  RIB Flags 1  |               .                               ~
      +-+-+-+-+-+-+-+-+               .                               ~
      ~                               .                               ~
      ~                               .                               ~
      ~                               .                               ~
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      |     RIBD String ID n (SDNV)   |            P-Value            |
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      |  RIB Flags n  |
      +-+-+-+-+-+-+-+-+
	 */
	public void createTLV(IByteBuffer buf, HashMap<String, ProphetNeighbor> neighbors) {
		buf.put(TLVType.RIBINFORMATIONBASE.getCode());
		int start = buf.position();
		buf.put((byte)0);
		int lenpos = buf.position();
		SDNV.encode(4,buf,2);
		
		SDNV.encode(neighbors.keySet().size()+1, buf, 2);
		buf.putShort((short)0);
				
		SDNV.encode(0, buf, 2);
		//P Value
		SDNV.encode(ProphetNeighbor.getLongP(1.0), buf, 2);
		//Flag
		buf.put((byte)0);
		
		Iterator<String> neids = neighbors.keySet().iterator();
		while( neids.hasNext() )
		{
			String eid = neids.next();
			ProphetNeighbor pn = neighbors.get(eid);
			//String id
			SDNV.encode(pn.stringID(), buf, 2);
			//P Value
			SDNV.encode(pn.P_short(), buf, 2);
			//Flag
			buf.put((byte)0);
		}
		int end = buf.position();
		buf.position(lenpos);
		SDNV.encode(end-start, buf, 2);
		buf.position(end);
	}
}
