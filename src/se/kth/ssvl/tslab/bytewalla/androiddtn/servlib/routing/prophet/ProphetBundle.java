package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.prophet;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.prophet.BaseTLV.TLVType;

public class ProphetBundle {
	// private static final String TAG = "ProphetBundle";

	byte code;
	Hello hello = new Hello();
	short length;
	byte protocol;
	short receiver;
	byte result;
	RIBDictionary rIBDictionary = new RIBDictionary();
	RIBInformationBase rIBInformationBase = new RIBInformationBase();
	BundleOffer bundleOffer = new BundleOffer();
	BundleResponse bundleResponse = new BundleResponse();
	short sender;
	short submessage;

	int trans_id;
	TLVType type;
	byte versionFlags;

	@Override
	public String toString() {
		String st = String.format("Protocol %x\n" + "VersionFlags %x\n"
				+ "Result %x\n" + "Code %x\n" + "Receiver %d\n" + "Sender %d\n"
				+ "Transaction ID %d\n" + "Submessage %x\n" + "Length %d\n"
				+ "Type %s\n", protocol, versionFlags, result, code, receiver,
				sender, trans_id, submessage, length, type.getCaption());

		switch (type) {
		case HELLO:
			st += hello.toString();
			break;
		case ERROR:
			break;

		case RIBDICTIONARY:
			st += rIBDictionary.toString();
			break;

		case RIBINFORMATIONBASE:
			st += rIBInformationBase.toString();
			break;
		case BUNDLEOFFER:
			st += bundleOffer;
			break;
		case BUNDLERESPONSE:
			st += bundleResponse;
			break;
		}
		return st;
	}
}
