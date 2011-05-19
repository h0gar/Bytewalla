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
package se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types;

import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPICode.dtn_status_report_reason_t;

/**
 * Class to represent BundleStatusReport to be accessed by the API
 * 
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class DTNBundleStatusReport {
	/**
	 * Bundle ID of this status report
	 */
	private int bundle_id_;

	/**
	 * Status report reason of this status report
	 */
	private dtn_status_report_reason_t reason_;

	/**
	 * Status report flag
	 */
	private byte flags_;

	/**
	 * Time of receipt
	 */
	private DTNTime receipt_dt_;

	/**
	 * Time of custody
	 */
	private DTNTime custody_dt_;

	/**
	 * Time of forwarding
	 */
	private DTNTime forwarding_dt_;

	/**
	 * Time of delivery
	 */
	private DTNTime delivery_dt_;

	/**
	 * Time of deletion
	 */
	private DTNTime deletion_dt_;

	/**
	 * Time of application acknowledgement
	 */
	private DTNTime ack_by_app_dt_;

	/**
	 * Getter function for BundleID
	 * 
	 * @return the bundle_id_
	 */
	public int bundle_id() {
		return bundle_id_;
	}

	/**
	 * Setter function for BundleID
	 * 
	 * @param bundleId
	 *            the bundle_id_ to set
	 */
	public void set_bundle_id(int bundleId) {
		bundle_id_ = bundleId;
	}

	/**
	 * Getter function for status report reason
	 * 
	 * @return the reason_
	 */
	public dtn_status_report_reason_t reason() {
		return reason_;
	}

	/**
	 * Setter function for status report reason
	 * 
	 * @param reason
	 *            the reason_ to set
	 */
	public void set_reason(dtn_status_report_reason_t reason) {
		reason_ = reason;
	}

	/**
	 * Getter function for flags of this status report
	 * 
	 * @return the flags_
	 */
	public byte flags() {
		return flags_;
	}

	/**
	 * Setter function for flags of this status report
	 * 
	 * @param flags
	 *            the flags_ to set
	 */
	public void set_flags(byte flags) {
		flags_ = flags;
	}

	/**
	 * Getter function for Time of Receipt
	 * 
	 * @return the receipt_dt_
	 */
	public DTNTime receipt_dt() {
		return receipt_dt_;
	}

	/**
	 * Setter function for Time of Receipt
	 * 
	 * @param receiptTs
	 *            the receipt_dt_ to set
	 */
	public void set_receipt_dt(DTNTime receipt_dt) {
		receipt_dt_ = receipt_dt;
	}

	/**
	 * Getter function for Time of Custody
	 * 
	 * @return the custody_dt_
	 */
	public DTNTime custody_dt() {
		return custody_dt_;
	}

	/**
	 * Setter function for Time of Custody
	 * 
	 * @param custodyTs
	 *            the custody_dt_ to set
	 */
	public void set_custody_dt(DTNTime custody_dt) {
		custody_dt_ = custody_dt;
	}

	/**
	 * Getter function for Time of forwarding
	 * 
	 * @return the forwarding_dt_
	 */
	public DTNTime forwarding_dt() {
		return forwarding_dt_;
	}

	/**
	 * Setter function for Time of forwarding
	 * 
	 * @param forwardingTs
	 *            the forwarding_dt_ to set
	 */
	public void set_forwarding_dt(DTNTime forwarding_dt) {
		forwarding_dt_ = forwarding_dt;
	}

	/**
	 * Getter function for delivery receipt
	 * 
	 * @return the delivery_dt_
	 */
	public DTNTime delivery_dt() {
		return delivery_dt_;
	}

	/**
	 * Setter function for delivery receipt
	 * 
	 * @param deliveryTs
	 *            the delivery_dt_ to set
	 */
	public void set_delivery_dt(DTNTime delivery_dt) {
		delivery_dt_ = delivery_dt;
	}

	/**
	 * Getter function for deletion receipt
	 * 
	 * @return the deletion_dt_
	 */
	public DTNTime deletion_dt() {
		return deletion_dt_;
	}

	/**
	 * Setter function for deletion receipt
	 * 
	 * @param deletionTs
	 *            the deletion_dt_ to set
	 */
	public void set_deletion_dt(DTNTime deletion_dt) {
		deletion_dt_ = deletion_dt;
	}

	/**
	 * @return the ack_by_app_dt_
	 */
	public DTNTime ack_by_app_dt() {
		return ack_by_app_dt_;
	}

	/**
	 * @param ackByAppTs
	 *            the ack_by_app_dt_ to set
	 */
	public void set_ack_by_app_dt(DTNTime ack_by_app_dt) {
		ack_by_app_dt_ = ack_by_app_dt;

	}

}