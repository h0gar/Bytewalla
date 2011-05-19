package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.prophet;

public class BundleResponseEntry {

	public short id;
	public byte flags;
	public byte reserve;
	public int creationTime;
	public int seqNo;
	public String toString() {
		return String.format("ID %d/" + "Flags %d/" + "Reserve %d/"
				+ "Creationtime %d/" + "Seqno %d\n", id, flags, reserve,
				creationTime, seqNo);
	}
}
