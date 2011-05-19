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
package se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types;


/**
 * Registration state of the DTN API Bundle
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public class DTNRegistrationInfo  {

	/**
	 * EndpointID particular to this Registration State
	 */
	private DTNEndpointID endpoint_;
	
	/**
	 * Registration flags
	 */
	private int flags_ = 0;
	
	/**
	 * Expiration time in seconds
	 */
	private int expiration_ = 0;
	
	/**
	 * Whether the status of the Registration begins with passive
	 */
	private boolean init_passive_ = false;
	
	/**
	 * Main Constructor
	 * @param endpoint
	 * @param flags
	 * @param expiration
	 * @param init_passive
	 */
	public DTNRegistrationInfo(DTNEndpointID endpoint, int flags , int expiration, boolean init_passive)
	{
		endpoint_ = new DTNEndpointID(endpoint.uri());
		flags_    = flags;
		expiration_ = expiration;
		init_passive_ = init_passive;
		
	}
	
	
	/**
	 * Getter function for EndpointID particular to this Registration State
	 * @return the endpoint_
	 */
	public DTNEndpointID endpoint() {
		return endpoint_;
	}
	/**
	 * Setter function for EndpointID particular to this Registration State
	 * @param endpoint the endpoint_ to set
	 */
	public void set_endpoint(DTNEndpointID endpoint) {
		endpoint_ = endpoint;
	}
	
	/**
	 * Getter function for Registration flags
	 * @return the flags_
	 */
	public int flags() {
		return flags_;
	}
	/**
	 * Setter function for Registration flags
	 * @param flags the flags_ to set
	 */
	public void set_flags(int flags) {
		flags_ = flags;
	}
	/**
	 * Getter function for expiration time of this Registration in seconds
	 * @return the expiration_
	 */
	public int expiration() {
		return expiration_;
	}
	/**
	 * Setter function for expiration time of this Registration in seconds
	 * @param expiration the expiration_ to set
	 */
	public void set_expiration(int expiration) {
		expiration_ = expiration;
	}
	/**
	 * Getter function for whether the status of the Registration begins with passive
	 * @return the init_passive_
	 */
	public boolean init_passive() {
		return init_passive_;
	}
	/**
	 * Setter function for whether the status of the Registration begins with passive
	 * @param initPassive the init_passive_ to set
	 */
	public void set_init_passive(boolean init_passive) {
		init_passive_ = init_passive;
	}


	
	
	
	
}
