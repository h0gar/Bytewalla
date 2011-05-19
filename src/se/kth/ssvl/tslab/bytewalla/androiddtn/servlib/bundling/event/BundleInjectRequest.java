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

import java.io.File;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.Bundle.priority_values_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.ForwardingInfo.action_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;

/**
 * BundleEvent for injecting the Bundle into DTN System. 
 * Any components that want to inject the Bundle can do so by posting this event to the BundleDaemon.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class BundleInjectRequest extends BundleEvent {
	public BundleInjectRequest() {
		super(event_type_t.BUNDLE_INJECT);
		// should be processed only by the daemon
		daemon_only_ = true;

	}

	// Bundle properties
	
	/**
	 * Source EndpointID of the Bundle
	 */
	private EndpointID src_;
	
	/**
	 * Destination EndpointID of the Bundle
	 */
	private EndpointID dest_;
	
	/**
	 * Reply-to EndpointID of the Bundle
	 */
	private EndpointID replyto_;
	
	/**
	 * Custodian EndpointID of the Bundle
	 */
	private EndpointID custodian_;
	
	/**
	 * Priority for this Bundle Delivery according to the Protocol
	 */
	private priority_values_t priority_values_;
	
	/**
	 * Expiration time in seconds
	 */
	private int expiration_;
	
	/**
	 * Payload file for this InjectedBundle
	 */
	private File payload_file_;

	/**
	 *  Outgoing link
	 */
	private Link link_;

	/**
	 *  Forwarding action
	 */
	private action_t action_;

	/**
	 *  Request ID for the event, to identify corresponding BundleInjectedEvent
	 */
	private String request_id_;

	/**
	 * Acessor for the source EndpointID of the Bundle
	 * @return the src_
	 */
	public EndpointID src() {
		return src_;
	}

	/**
	 * Setter for the source EndpointID of the Bundle
	 * @param src the src_ to set
	 */
	public void set_src(EndpointID src) {
		src_ = src;
	}

	/**
	 * Accessor for the destination EndpointID of the Bundle
	 * @return the dest_
	 */
	public EndpointID dest() {
		return dest_;
	}

	/**
	 * Setter for the destination EndpointID of the Bundle
	 * @param dest the dest_ to set
	 */
	public void set_dest(EndpointID dest) {
		dest_ = dest;
	}

	/**
	 * Accessor for the Reply-to EndpointID of the Bundle
	 * @return the replyto_
	 */
	public EndpointID replyto() {
		return replyto_;
	}

	/**
	 * Setter for the Reply-to EndpointID of the Bundle
	 * @param replyto the replyto_ to set
	 */
	public void set_replyto(EndpointID replyto) {
		replyto_ = replyto;
	}

	/**
	 * Accessor for the Custodian EndpointID of the Bundle
	 * @return the custodian_
	 */
	public EndpointID custodian() {
		return custodian_;
	}

	/**
	 * Setter for the Custodian EndpointID of the Bundle
	 * @param custodian the custodian_ to set
	 */
	public void set_custodian(EndpointID custodian) {
		custodian_ = custodian;
	}

	/**
	 * Accessor for the Priority for this Bundle Delivery according to the Protocol
	 * @return the priority_
	 */
	public priority_values_t priority_values() {
		return priority_values_;
	}

	/**
	 * Setter for the Priority for this Bundle Delivery according to the Protocol
	 * @param priority the priority_ to set
	 */
	public void set_priority(priority_values_t priority) {
		priority_values_ = priority;
	}

	

	/**
	 * Accessor for the Payload file for this InjectedBundle
	 * @return the payload_file_
	 */
	public File payload_file() {
		return payload_file_;
	}

	/**
	 * Setter for the Payload file for this InjectedBundle
	 * @param payloadFile the payload_file_ to set
	 */
	public void set_payload_file(File payload_file) {
		payload_file_ = payload_file;
	}

	/**
	 * Accessor for the Outgoing Link
	 * @return the link_
	 */
	public Link link() {
		return link_;
	}

	/**
	 * Setter for the Outgoing Link
	 * @param link the link_ to set
	 */
	public void set_link(Link link) {
		link_ = link;
	}

	/**
	 * Accessor for the forwarding action of this Inject request
	 * @return the action_
	 */
	public action_t action() {
		return action_;
	}

	/**
	 * Setter for the forwarding action of this Inject request
	 * @param action the action_ to set
	 */
	public void set_action(action_t action) {
		action_ = action;
	}

	/**
	 * Accessor for the unique identifier of this BundleInjectRequest. This will be used to refer later including in BundleInjectedRequest
	 * @return the request_id_
	 */
	public String request_id() {
		return request_id_;
	}

	/**
	 * Setter for the unique identifier of this BundleInjectRequest. This will be used to refer later including in BundleInjectedRequest
	 * @param requestId the request_id_ to set
	 */
	public void set_request_id(String request_id) {
		request_id_ = request_id;
	}

	/**
	 * Accessor for the Bundle Expiration time in seconds
	 * @return the expiration_value_
	 */
	public int expiration() {
		return expiration_;
	}

	/**
	 * Setter for the Bundle Expiration time in seconds
	 * @param expirationValue the expiration_value_ to set
	 */
	public void set_expiration(int expiration) {
		expiration_ = expiration;
	}

};