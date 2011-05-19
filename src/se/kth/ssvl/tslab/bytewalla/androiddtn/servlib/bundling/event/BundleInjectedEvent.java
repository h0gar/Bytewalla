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


/**
 * Event class after Bundle is injected
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class BundleInjectedEvent  extends BundleEvent {
	/**
	 * main Constructor
	 * @param bundle
	 * @param request_id
	 */
	public BundleInjectedEvent(Bundle bundle, String request_id) {
		super(event_type_t.BUNDLE_INJECTED);
		bundle_ = bundle;
		request_id_ = request_id;
		
		
	}

	/**
	 *  The injected bundle
	 */
	private Bundle bundle_;

	/**
	 *  Request ID from the inject request
	 */
	private String request_id_;

	/**
	 * Accessor for the injected bundle
	 * @return the bundle_
	 */
	public Bundle bundle() {
		return bundle_;
	}

	/**
	 * Setter for the injected bundle
	 * @param bundle the bundle_ to set
	 */
	public void set_bundle(Bundle bundle) {
		bundle_ = bundle;
	}

	/**
	 * Accessor for the Request ID from the inject request
	 * @return the request_id_
	 */
	public String request_id() {
		return request_id_;
	}

	/**
	 * Setter for the Request ID from the inject request
	 * @param requestId the request_id_ to set
	 */
	public void set_request_id(String request_id) {
		request_id_ = request_id;
	}
};
