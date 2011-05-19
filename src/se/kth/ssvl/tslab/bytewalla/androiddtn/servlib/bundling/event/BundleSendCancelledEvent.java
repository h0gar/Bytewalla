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
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;

/**
 * Event generated after the Bundle was canceled successfully
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class BundleSendCancelledEvent extends BundleEvent {
	/**
	 * Constructor
	 * @param bundle
	 * @param link
	 */
	public BundleSendCancelledEvent(Bundle bundle, final Link link) {
		super(event_type_t.BUNDLE_CANCELLED);
		bundle_ = bundle;
		link_   = link;
	}

	/**
	 *  The canceled bundle
	 */
	private Bundle bundle_;

	/**
	 *  The link the Bundle was queued on
	 */
	private Link link_;

	/**
	 * Accessor for the canceled bundle
	 * @return the bundle_
	 */
	public Bundle bundle() {
		return bundle_;
	}

	/**
	 * Setter for the canceled bundle
	 * @param bundle the bundle_ to set
	 */
	public void set_bundle(Bundle bundle) {
		bundle_ = bundle;
	}

	/**
	 * Accessor for the link the Bundle was queued on
	 * @return the link_
	 */
	public Link link() {
		return link_;
	}

	/**
	 * Setter for the link the Bundle was queued on
	 * @param link the link_ to set
	 */
	public void set_link(Link link) {
		link_ = link;
	}
};