package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.prophet;

import java.util.HashMap;
import java.util.Iterator;

public class RIBDictionary {

	HashMap<Short, RIBDictionaryEntry> entries = new HashMap<Short, RIBDictionaryEntry>();
	byte flags;
	short length;
	short reserved;
	short rIBDEntryCount;

	@Override
	public String toString() {
		String st = String.format("Flags %x\n" + "Length %d\n"
				+ "RIBEntryCount %d\n" + "Reserved %d\n", flags, length,
				rIBDEntryCount, reserved);

		Iterator<Short> sid = entries.keySet().iterator();
		while(sid.hasNext()) {
			st += entries.get(sid.next()).toString();
		}

		return st;
	}
}

