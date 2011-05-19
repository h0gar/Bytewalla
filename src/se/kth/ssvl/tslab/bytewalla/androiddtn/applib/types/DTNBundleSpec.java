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



import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPICode.dtn_bundle_priority_t;



/**
 * The actual DTNBundle description according to the Bundle Protocol.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public class DTNBundleSpec{
	/**
	 * Source EndpointID of the Bundle
	 */
	private DTNEndpointID source_;
	/**
	 * Destination EndpointID of the Bundle
	 */
	private DTNEndpointID dest_;
	
	/**
	 * Reply to EndpointID of the Bundle
	 */
	private DTNEndpointID replyto_;
	
	/**
	 * Priority of the Bundle
	 */
	private dtn_bundle_priority_t priority_;
	
	/**
	 * Delivery options
	 */
	private int dopts_;
	
	/**
	 * Expiration time in seconds
	 */
	private int expiration_;
	
	/**
	 * Bundle Creation Timestamp
	 */
	private DTNBundleTimestamp creation_ts_;
	
	/**
	 * The delivery registration ID of the Bundle
	 */
	private int delivery_regid_;
	
	/**
	 * Flag to indicate whether this Bundle is an admin Bundle
	 */
	private boolean is_admin_;
	
	
	/**
	 * The constructor of DTNBundleSpec	
	 */
	public DTNBundleSpec()
	{
		// set default values
		source_   = new DTNEndpointID();
		dest_     = new DTNEndpointID();
		
		priority_ = dtn_bundle_priority_t.COS_NORMAL;
		dopts_    = 0;
		expiration_ = -1;
		creation_ts_ = new DTNBundleTimestamp();
		delivery_regid_ = -1;
		
	}
	
	/**
	 * Getter of the source EndpointID of this Bundle
	 * @return the source_
	 */
	public DTNEndpointID source() {
		return source_;
	}
	/**
	 * Setter of the source EndpointID of this Bundle
	 * @param source the source_ to set
	 */
	public void set_source(DTNEndpointID source) {
		source_ = source;
	}
	/**
	 * Getter of the destination EndpointID of this Bundle
	 * @return the dest_
	 */
	public DTNEndpointID dest() {
		return dest_;
	}
	/**
	 * Setter of the destination EndpointID of this Bundle
	 * @param dest the dest_ to set
	 */
	public void set_dest(DTNEndpointID dest) {
		dest_ = dest;
	}
	/**
	 * Getter of the reply to EndpointID of this Bundle
	 * @return the replyto_
	 */
	public DTNEndpointID replyto() {
		return replyto_;
	}
	/**
	 * Setter of the reply to EndpointID of this Bundle
	 * @param replyto the replyto_ to set
	 */
	public void set_replyto(DTNEndpointID replyto) {
		replyto_ = replyto;
	}
	/**
	 * Getter of the priority of this Bundle
	 * @return the priority_
	 */
	public dtn_bundle_priority_t priority() {
		return priority_;
	}
	/**
	 * Setter of the priority of this Bundle
	 * @param priority the priority_ to set
	 */
	public void set_priority(dtn_bundle_priority_t priority) {
		priority_ = priority;
	}
	/**
	 * Getter of the delivery options of this Bundle
	 * @return the dopts_
	 */
	public int dopts() {
		return dopts_;
	}
	/**
	 * Setter of the delivery options of this Bundle
	 * @param dopts the dopts_ to set
	 */
	public void set_dopts(int dopts) {
		dopts_ = dopts;
	}
	/**
	 * Getter of the expiration time of this Bundle in seconds
	 * @return the expiration_
	 */
	public int expiration() {
		return expiration_;
	}
	/**
	 * Setter of the the expiration time of this Bundle in seconds
	 * @param expiration the expiration_ to set
	 */
	public void set_expiration(int expiration) {
		expiration_ = expiration;
	}
	/**
	 * Getter of the Bundle Creation Timestamp of this Bundle according to the protocol
	 * @return the creation_ts_ the creation timestamp of this Bundle as DTNBundleTimestamp object
	 * @see DTNBundleTimestamp
	 */
	public DTNBundleTimestamp creation_ts() {
		return creation_ts_;
	}
	/**
	 * Setter of the Bundle Creation Timestamp of this Bundle according to the protocol
	 * @param creationTs the creation_ts_ to set
	 */
	public void set_creation_ts(DTNBundleTimestamp creation_ts) {
		creation_ts_ = creation_ts;
	}
	/**
	 * Getter of the delivery registration ID
	 * @return the delivery_regid_
	 */
	public int delivery_regid() {
		return delivery_regid_;
	}
	/**
	 * Setter of the delivery registration ID
	 * @param deliveryRegid the delivery_regid_ to set
	 */
	public void set_delivery_regid(int delivery_regid) {
		delivery_regid_ = delivery_regid;
	}

	/**
	 * Getter of the is_admin flag to indicate whether this Bundle is admin Bundle
	 * @return the is_admin_
	 */
	public boolean is_admin() {
		return is_admin_;
	}

	/**
	 * Setter of the is_admin flag to indicate whether this Bundle is admin Bundle
	 * @param isAdmin the is_admin_ to set
	 */
	public void set_is_admin(boolean is_admin) {
		is_admin_ = is_admin;
	}
	
	

	
}
