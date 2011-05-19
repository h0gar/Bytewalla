package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.prophet;

import java.util.ArrayList;

public class BundleResponse {

	public byte flags;
	public short length;
	public short offerCount;
	public short reserve;
	public ArrayList<BundleResponseEntry> entries = new ArrayList<BundleResponseEntry>();
	public String toString() {
		String st = String.format("Flags %x\n" + "Lenght %d\n"
				+ "OfferCount %d\n" + "Reserve %d\n", flags, length,
				offerCount, reserve);
		
		for(BundleResponseEntry e: entries)
			st += e.toString();

		return st;
	}

}
