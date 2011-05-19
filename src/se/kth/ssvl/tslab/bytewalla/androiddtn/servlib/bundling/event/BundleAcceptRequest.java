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
 * Event to check whether the Bundle would be accepted by the Bundle Daemon or not
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class BundleAcceptRequest  extends BundleEvent {
	/**
	 * Constructor
	 * @param bundle
	 * @param source
	 * @param result
	 * @param reason
	 */
	public BundleAcceptRequest(final Bundle bundle, event_source_t source,
			boolean[] result, BundleProtocol.status_report_reason_t[] reason) {
		super(event_type_t.BUNDLE_ACCEPT_REQUEST);
		bundle_ = bundle;
		source_ = source;
		result_ = result;
		reason_ = reason;
	}

	/**
	 *  The bundle to be checked
	 */
	private Bundle bundle_;

	/**
	 * The source of the event
	 */
	private event_source_t source_;

	/**
	 * The pointer to the result of Bundle Acceptance
	 */
	private boolean[] result_;

	/**
	 * The pointer to the accepted or rejected reason 
	 */
	private BundleProtocol.status_report_reason_t[] reason_;

	/**
	 * Accessor for the bundle to be checked
	 * @return the bundle_
	 */
	public Bundle bundle() {
		return bundle_;
	}

	/**
	 * Setter for the bundle to be checked
	 * @param bundle the bundle_ to set
	 */
	public void set_bundle(Bundle bundle) {
		bundle_ = bundle;
	}

	/**
	 * Accessor for the source of the event
	 * @return the source_
	 */
	public event_source_t source() {
		return source_;
	}

	/**
	 * Setter for the source of the event
	 * @param source the source_ to set
	 */
	public void set_source(event_source_t source) {
		source_ = source;
	}

	/**
	 * Accessor for the result of Bundle Acceptance
	 * @return the result_
	 */
	public boolean[] result() {
		return result_;
	}

	/**
	 * Setter for the result of Bundle Acceptance
	 * @param result the result_ to set
	 */
	public void set_result(boolean[] result) {
		result_ = result;
	}

	/**
	 * Accessor for the accepted or rejected reason 
	 * @return the reason_
	 */
	public BundleProtocol.status_report_reason_t[] reason() {
		return reason_;
	}

	/**
	 * Setter for the accepted or rejected reason 
	 * @param reason the reason_ to set
	 */
	public void set_reason(BundleProtocol.status_report_reason_t[] reason) {
		reason_ = reason;
	}
};
