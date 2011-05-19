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

/*
 * @Author Mahesh B S ( mabsp@kth.se )
 */
package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.prophet;

import java.util.Date;

public class ProphetNeighbor {
	public static enum ProphetNeighborRecvState {
		RIBDICTIONARY_ACK_SENT("RIBDICTIONARY_ACK,SENT"), RIBIB_ACK_SENT(
				"RIB_INFORMATION_ACK_SENT"), SYN_ACK_SENT("SYN_ACK_SENT"), UNDEFINED(
				"UNDEFINED"), BUNDLEOFFER_SENT("BUNDLEOFFER_SENT"), BUNDLERESPONSE_RECVD(
				"BUNDLERESPONSE_RECVD");

		private String caption;

		// /< All phases now complete, waiting for timer or ACK

		ProphetNeighborRecvState() {
			caption = "";
		}

		ProphetNeighborRecvState(String c) {
			caption = c;
		}

		public String getCaption() {
			return caption;
		}
	}

	public static enum ProphetNeighborSendState {
		RIBDICTIONARY_SENT("RIBDICTIONARY_SENT"), RIBIB_SENT("RIBIB_SENT"), SYN_ACK_ACK_SENT(
				"SYN_ACK_ACK_SENT"), SYN_SENT("SYNSENT"), SYNACK_RCVD(
				"SYNACK_RCVD"), UNDEFINED("UNDEFINED");

		private String caption;

		// /< All phases now complete, waiting for timer or ACK

		ProphetNeighborSendState() {
			caption = "";
		}

		ProphetNeighborSendState(String c) {
			caption = c;
		}

		public String getCaption() {
			return caption;
		}
	}

	public static final short P_SHORT = 1000;
	// private static final float P_first_threshold = 0.1f;
	// private static final float alpha = 0.5f;
	private static final float beta = 0.9f;
	private static final float delta = 0.01f;
	private static final float gamma = 0.999f;
	private static final float K_unit = 24 * 60 * 60 * 1000;
	/*
	 * +========================================+ | Parameter | Recommended
	 * value | +========================================+ | P_encounter | 0.5 |
	 * +----------------------------------------+ | P_encounter_first | 0.25 |
	 * +----------------------------------------+ | P_first_threshold | 0.1 |
	 * +----------------------------------------+ | alpha | 0.5 |
	 * +----------------------------------------+ | beta | 0.9 |
	 * +----------------------------------------+ | gamma | 0.999 |
	 * +----------------------------------------+ | delta | 0.01 |
	 * +========================================+
	 */
	private static final float P_encounter = 0.5f;

	private static final float P_encounter_first = 0.25f;

	private static int staticStringID = 1;

	protected static short next_instance_ = (short) ((short) Math.random() % 100);

	public static short getLongP(double d) {
		return (short) (d * ProphetNeighbor.P_SHORT);
	}

	public static float getPFloat(short s) {
		return (float) s / (float) P_SHORT;
	}

	protected static short getNextInstance() {
		return next_instance_++;
	}

	public RIBDictionary rIBDictionary;
	public RIBInformationBase rIBInformation;
	public int sendTransactionId = (int) Math.random();

	public long timestamp = 0;

	private long age = new Date().getTime();

	private float P_ = P_encounter_first;

	private int stringID = staticStringID++;

	protected short local_instance_ = getNextInstance();

	protected ProphetNeighborRecvState recvState_ = ProphetNeighborRecvState.UNDEFINED;
	protected int recvTansactionId = 0;
	protected short remote_instance_ = getNextInstance();
	protected String remoteEid;
	protected ProphetNeighborSendState sendState_ = ProphetNeighborSendState.UNDEFINED;
	public BundleOffer bundleOffer = null;
	public boolean acked = false;

	public ProphetNeighbor(String endpointID) {
		remoteEid = endpointID;
	}

	public ProphetNeighborRecvState getRecvState() {
		return recvState_;
	}

	public ProphetNeighborSendState getSendState() {
		return sendState_;
	}

	public float P_() {
		update_age();
		return P_;
	}

	public short P_short() {
		return getLongP(P_);
	}

	public String remote_eid() {
		return remoteEid.split("/prophet")[0];
	}

	public void setRecvState(ProphetNeighborRecvState synAckSent) {
		recvState_ = synAckSent;
	}

	public void setSendState(ProphetNeighborSendState synsent) {
		sendState_ = synsent;
	}

	public int stringID() {
		return stringID;
	}

	public void update_age() {
		/* K = (number of days */
		float k = (age - new Date().getTime()) / K_unit;
		P_ = (float) (P_ * Math.pow(gamma, k));
	}

	// P_(A,B) = P_(A,B)_old + ( 1 - delta - P_(A,B)_old ) * P_encounter (1)
	public void update_encounter() {
		update_age();
		P_ = P_ + (1 - delta - P_) * P_encounter;
		age = new Date().getTime();
	}

	public void update_transitivity(float pab, float pbc) {
		update_age();
		P_ = Math.max(P_, pab * pbc * beta);
		age = new Date().getTime();
	}
}
