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

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;


/**
 * Event class for link unavailable events
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */

public class LinkUnavailableEvent extends ContactEvent {
	public LinkUnavailableEvent(final Link link, ContactEvent.reason_t reason) {
	
		super(event_type_t.LINK_UNAVAILABLE, reason);
		link_ = link;
		reason_ = reason;
	}

	/**
	 *  The link that is unavailable
	 */
	private Link link_;

	/**
	 * Getter for the link that is unavailable
	 * @return the link_
	 */
	public Link link() {
		return link_;
	}

	/**
	 * Setter for the that is unavailable
	 * @param link the link_ to set
	 */
	public void set_link(Link link) {
		link_ = link;
	}
};
