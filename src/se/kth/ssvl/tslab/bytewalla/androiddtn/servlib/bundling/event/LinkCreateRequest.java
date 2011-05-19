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
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.ConvergenceLayer;


/**
 * Event class for creating and opening a link
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class LinkCreateRequest  extends BundleEvent {
	/**
	 * main constructor
	 * @param name
	 * @param link_type
	 * @param endpoint
	 * @param cla
	 * @param parameters
	 */
	public LinkCreateRequest(final String name, Link.link_type_t link_type,
			final String endpoint, ConvergenceLayer cla,
			AttributeVector parameters)

	{
		super(event_type_t.LINK_CREATE);
		name_        = name;
		link_type_   = link_type;
		endpoint_    = endpoint;
		cla_         = cla;
		parameters_  = parameters;
		daemon_only_ = true;
	}

	/**
	 *  Identifier for the link
	 */
	private String name_;

	/**
	 *  Next hop EID
	 */
	private String endpoint_;

	/**
	 *  Type of link
	 */
	private Link.link_type_t link_type_;

	/**
	 *  CL to use
	 */
	private ConvergenceLayer cla_;

	/**
	 *  An optional set of key, value pairs
	 */
	private AttributeVector parameters_;

	/**
	 * Getter for the Identifier for the link
	 * @return the name_
	 */
	public String name() {
		return name_;
	}

	/**
	 * Setter for the Identifier for the link
	 * @param name the name_ to set
	 */
	public void set_name(String name) {
		name_ = name;
	}

	/**
	 * Getter for the Next hop EID
	 * @return the endpoint_
	 */
	public String endpoint() {
		return endpoint_;
	}

	/**
	 * Setter for the Next hop EID
	 * @param endpoint the endpoint_ to set
	 */
	public void set_endpoint(String endpoint) {
		endpoint_ = endpoint;
	}

	/**
	 * Getter for the type of link
	 * @return the link_type_
	 */
	public Link.link_type_t link_type() {
		return link_type_;
	}

	/**
	 * Setter for the type of link
	 * @param linkType the link_type_ to set
	 */
	public void set_link_type(Link.link_type_t link_type) {
		link_type_ = link_type;
	}

	/**
	 * Getter for the CL to use
	 * @return the cla_
	 */
	public ConvergenceLayer cla() {
		return cla_;
	}

	/**
	 * Setter for the CL to use
	 * @param cla the cla_ to set
	 */
	public void set_cla(ConvergenceLayer cla) {
		cla_ = cla;
	}

	/**
	 * Getter for the optional set of key, value pairs
	 * @return the parameters_
	 */
	public AttributeVector parameters() {
		return parameters_;
	}

	/**
	 * Setter for the optional set of key, value pairs
	 * @param parameters the parameters_ to set
	 */
	public void set_parameters(AttributeVector parameters) {
		parameters_ = parameters;
	}
};
