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
 * Event class for bundle expiration.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class BundleExpiredEvent extends BundleEvent {
	public BundleExpiredEvent(Bundle bundle) {
		
		super(event_type_t.BUNDLE_EXPIRED);
		bundle_ = bundle;
	
	}

	/**
	 *  The expired bundle
	 */
	private Bundle bundle_;

	/**
	 * Accessor for the expired bundle
	 * @return the bundle_
	 */
	public Bundle bundle() {
		return bundle_;
	}

	/**
	 * Setter for the expired bundle
	 * @param bundle the bundle_ to set
	 */
	public void set_bundle(Bundle bundle) {
		bundle_ = bundle;
	}
};


