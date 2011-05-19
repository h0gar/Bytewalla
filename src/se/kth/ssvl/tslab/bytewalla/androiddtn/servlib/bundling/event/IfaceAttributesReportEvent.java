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

/**
 * Class for reporting Interface attributes
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class IfaceAttributesReportEvent extends CLAQueryReport {
	public IfaceAttributesReportEvent(String query_id,
			AttributeVector attributes) {
		super(event_type_t.CLA_IFACE_ATTRIB_QUERY, query_id);
		attributes_ = attributes;

	}

	/**
	 *  Interface attribute values by name.
	 */
	private AttributeVector attributes_;

	/**
	 * Getter for the Interface attribute values by name.
	 * @return the attributes_
	 */
	public AttributeVector attributes() {
		return attributes_;
	}

	/**
	 * @param attributes the attributes_ to set
	 */
	public void set_attributes(AttributeVector attributes) {
		attributes_ = attributes;
	}
};