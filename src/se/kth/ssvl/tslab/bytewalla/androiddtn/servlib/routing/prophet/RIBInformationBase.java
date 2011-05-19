package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.prophet;

import java.util.HashMap;
import java.util.Iterator;

public class RIBInformationBase {
	HashMap<Short, RIBInformationBaseEntry> entries = new HashMap<Short, RIBInformationBaseEntry>();
	byte flags;
	short length;
	short reserved;
	short rIBStringCount;

	@Override
	public String toString() {
		String st = String.format("Flags %d\n" + "Length %d\n"
				+ "RIBStringCount %d\n" + "Reserved %d\n", flags, length,
				rIBStringCount, reserved);
		Iterator<Short> sid = entries.keySet().iterator();
		while (sid.hasNext()) {
			st += entries.get(sid.next());
		}
		return st;
	}
}
