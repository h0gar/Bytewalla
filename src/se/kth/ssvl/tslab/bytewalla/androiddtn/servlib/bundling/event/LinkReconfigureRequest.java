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

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.AttributeVector;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;


/**
 * Event class for reconfiguring an existing link.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class LinkReconfigureRequest extends BundleEvent {
	/**
	 * main constructor
	 * @param link
	 * @param parameters
	 */
	public LinkReconfigureRequest(Link link, AttributeVector parameters)

	{
		super(event_type_t.LINK_RECONFIGURE);
	    link_ = link;
	    parameters_ = parameters;
		
		
		daemon_only_ = true;
	}

	/**
	 * The link to be changed
	 */
	private Link link_;

	/**
	 *  Set of key, value pairs
	 */
	private AttributeVector parameters_;

	/**
	 * Getter for the link to be changed
	 * @return the link_
	 */
	public Link link() {
		return link_;
	}

	/**
	 * Setter for the link to be changed
	 * @param link the link_ to set
	 */
	public void set_link(Link link) {
		link_ = link;
	}

	/**
	 * Getter for the Set of key, value pairs
	 * @return the parameters_
	 */
	public AttributeVector parameters() {
		return parameters_;
	}

	/**
	 * Setter for the Set of key, value pairs
	 * @param parameters the parameters_ to set
	 */
	public void set_parameters(AttributeVector parameters) {
		parameters_ = parameters;
	}
};
