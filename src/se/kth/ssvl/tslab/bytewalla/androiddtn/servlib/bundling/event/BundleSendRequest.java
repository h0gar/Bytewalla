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
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.ForwardingInfo.action_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;


/**
 * BundleEvent for requesting Bundle sending
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class BundleSendRequest extends BundleEvent {
	/**
	 * Empty constructor
	 */
	public BundleSendRequest() {
		super(event_type_t.BUNDLE_SEND);
		// should be processed only by the daemon
		daemon_only_ = true;

	}

	/**
	 * Constructor specifying Bundle, link, and action
	 * @param bundle
	 * @param link
	 * @param action
	 */
	public BundleSendRequest(Bundle bundle, Link link, action_t action) {
		super(event_type_t.BUNDLE_SEND);
		bundle_ = bundle;
		link_   = link;
		action_ = action;
		// should be processed only by the daemon
		daemon_only_ = true;
	}

	/**
	 *  Bundle to be sent
	 */
	private Bundle bundle_;

	/**
	 *  Link on which to send the bundle
	 */
	private Link link_;

	/**
	 *  Forwarding action to use when sending bundle
	 */
	private action_t action_;

	/**
	 * Getter for the Bundle to be sent
	 * @return the bundle_
	 */
	public Bundle bundle() {
		return bundle_;
	}

	/**
	 * Setter for the Bundle to be sent
	 * @param bundle the bundle_ to set
	 */
	public void set_bundle(Bundle bundle) {
		bundle_ = bundle;
	}

	/**
	 * Getter for the Link on which to send the bundle
	 * @return the link_
	 */
	public Link link() {
		return link_;
	}

	/**
	 * Setter for the Link on which to send the bundle
	 * @param link the link_ to set
	 */
	public void set_link(Link link) {
		link_ = link;
	}

	/**
	 * Getter for the Forwarding action to use when sending bundle
	 * @return the action_
	 */
	public action_t action() {
		return action_;
	}

	/**
	 * Setter for the Forwarding action to use when sending bundle
	 * @param action the action_ to set
	 */
	public void set_action(action_t action) {
		action_ = action;
	}
};