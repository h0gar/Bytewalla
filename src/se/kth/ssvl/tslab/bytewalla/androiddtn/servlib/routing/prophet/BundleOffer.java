package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.prophet;

import java.util.ArrayList;

public class BundleOffer {
	byte flags;
	public short length;
	public short offerCount;
	public short reserve;
	public ArrayList<BundleOfferEntry> entries = new ArrayList<BundleOfferEntry>();

	public String toString() {
		String st = String.format("Flags %x\n" + "Length %d\n"
				+ "OfferCount %d\n" + "Reserve %d\n", flags, length,
				offerCount, reserve);
		
		for(BundleOfferEntry e: entries)
			st += e.toString();

		return st;
	}
}
