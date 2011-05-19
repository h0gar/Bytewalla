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

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Interface;
/**
 * Class for notifying EndpointID reachability
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class EIDReachableQueryRequest extends CLAQueryReport {
	/**
	 * constructor
	 * @param query_id
	 * @param iface
	 * @param endpoint
	 */
	public EIDReachableQueryRequest(String query_id, Interface iface,
			String endpoint)

	{
		super(event_type_t.CLA_EID_REACHABLE_QUERY, query_id, true);
		iface_ = iface;
		endpoint_ = endpoint;

	}

	/**
	 *  Interface on which to check if the given endpoint is reachable.
	 */
	private Interface iface_;

	/**
	 *  Endpoint ID to be checked for reachable status.
	 */
	private String endpoint_;

	/**
	 * Getter for the Interface on which to check if the given endpoint is reachable.
	 * @return the iface_
	 */
	public Interface iface() {
		return iface_;
	}

	/**
	 * Setter for the Interface on which to check if the given endpoint is reachable.
	 * @param iface the iface_ to set
	 */
	public void set_iface(Interface iface) {
		iface_ = iface;
	}

	/**
	 * Getter for the Endpoint ID to be checked for reachable status.
	 * @return the endpoint_
	 */
	public String endpoint() {
		return endpoint_;
	}

	/**
	 * Setter for the Endpoint ID to be checked for reachable status.
	 * @param endpoint the endpoint_ to set
	 */
	public void set_endpoint(String endpoint) {
		endpoint_ = endpoint;
	}
};