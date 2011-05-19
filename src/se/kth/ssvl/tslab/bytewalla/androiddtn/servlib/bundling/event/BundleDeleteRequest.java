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
package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.Bundle;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol;


/**
 * Bundle Event for deleting a Bundle
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class BundleDeleteRequest extends BundleEvent {
	/**
	 * Constructor
	 */
	public BundleDeleteRequest() {
		super(event_type_t.BUNDLE_DELETE);
		// should be processed only by the daemon
		daemon_only_ = true;

	}

	/**
	 * Constructor with DTNBundle specified
	 * @param bundle
	 * @param reason
	 */
	public BundleDeleteRequest(Bundle bundle,
			BundleProtocol.status_report_reason_t reason)

	{
		super(event_type_t.BUNDLE_DELETE);
		bundle_ = bundle;
		reason_ = reason;
		daemon_only_ = true;
	}


	/**
	 *  Bundle to be deleted
	 */
	private Bundle bundle_;

	/**
	 *  The reason code
	 */
	private BundleProtocol.status_report_reason_t reason_;

	/**
	 * Accessor for the Bundle to be deleted
	 * @return the bundle_
	 */
	public Bundle bundle() {
		return bundle_;
	}

	/**
	 * Setter for the Bundle to be deleted
	 * @param bundle the bundle_ to set
	 */
	public void set_bundle(Bundle bundle) {
		bundle_ = bundle;
	}

	/**
	 * Accessor for the reason code
	 * @return the reason_
	 */
	public BundleProtocol.status_report_reason_t reason() {
		return reason_;
	}

	/**
	 * Setter for the reason code
	 * @param reason the reason_ to set
	 */
	public void set_reason(BundleProtocol.status_report_reason_t reason) {
		reason_ = reason;
	}
};
