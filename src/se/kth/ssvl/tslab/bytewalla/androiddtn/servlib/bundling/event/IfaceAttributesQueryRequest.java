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

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.AttributeNameVector;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Interface;

/**
 * Event for querying Interface attribute
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class IfaceAttributesQueryRequest  extends CLAQueryReport {
	public IfaceAttributesQueryRequest(String query_id, Interface iface,
			AttributeNameVector attribute_names) {
		super(event_type_t.CLA_IFACE_ATTRIB_QUERY, query_id, true);
		iface_ = iface;
		attribute_names_ = attribute_names;

	}

	/**
	 *  Interface for which the given attributes are requested.
	 */
	private Interface iface_;

	/**
	 *  Link attributes requested by name.
	 */
	private AttributeNameVector attribute_names_;

	/**
	 * Getter for the Interface for which the given attributes are requested.
	 * @return the iface_
	 */
	public Interface iface() {
		return iface_;
	}

	/**
	 * Setter for the Interface for which the given attributes are requested.
	 * @param iface the iface_ to set
	 */
	public void set_iface(Interface iface) {
		iface_ = iface;
	}

	/**
	 * Getter for the Link attributes requested by name.
	 * @return the attribute_names_
	 */
	public AttributeNameVector attribute_names() {
		return attribute_names_;
	}

	/**
	 * Setter for the Link attributes requested by name.
	 * @param attributeNames the attribute_names_ to set
	 */
	public void set_attribute_names(AttributeNameVector attribute_names) {
		attribute_names_ = attribute_names;
	}
};

