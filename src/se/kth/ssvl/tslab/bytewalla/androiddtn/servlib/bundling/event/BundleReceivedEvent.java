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
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.reg.Registration;

 
/**
 * Main event for receiving Bundle from multiple sources. The sources include local application, storage, peer, fragmentation, and others.
 * @see  event_source_t.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class BundleReceivedEvent extends BundleEvent {
	/**
	 * Constructor for bundles arriving from a peer, named by the prevhop and
	 * optionally marked with the link it arrived on.
	 */
	public BundleReceivedEvent(Bundle bundle, event_source_t source,
			int bytes_received, final EndpointID prevhop, Link originator) {
		super(event_type_t.BUNDLE_RECEIVED);
		bundle_      =  bundle;
		source_         =  source;
		bytes_received_ =  bytes_received;
		link_           =  originator;
		prevhop_        =  prevhop;
		registration_    =  null;
		
		assert (source == event_source_t.EVENTSRC_PEER): "BundleReceivedEvent:Event source assertion fail, it should be peer";
		
	}

	/**
	 * Constructor for bundles arriving from a local application identified by
	 * the given Registration.
	 */
	public BundleReceivedEvent(Bundle bundle, event_source_t source,
			Registration registration) {
		super(event_type_t.BUNDLE_RECEIVED);
		bundle_ =  bundle;
		source_ = source;
		bytes_received_ = 0;
		link_ = null;
		prevhop_ = EndpointID.NULL_EID();
		registration_ = registration;
		
	}

	/**
	 * Constructor for other "arriving" bundles, including reloading from
	 * storage and generated signals.
	 */
	public BundleReceivedEvent(Bundle bundle, event_source_t source) {
		super(event_type_t.BUNDLE_RECEIVED);
		bundle_ = bundle;
		source_    = source;
		bytes_received_ = 0;
		link_ = null;
		prevhop_ = EndpointID.NULL_EID();
		registration_ = null;
		
		
	}

	/**
	 *  The newly arrived bundle
	 */
	private Bundle bundle_;

	/**
	 *  The event source of the bundle
	 *  @see event_source_t
	 */
	private event_source_t source_;

	/**
	 *  The total bytes actually received
	 */
	private int bytes_received_;

	/**
	 *  Link from which bundle was received, if applicable
	 */
	private Link link_;

	/**
	 *  Previous hop Endpoint id.
	 */
	private EndpointID prevhop_;

	/** 
	 * Registration where the bundle arrived
	 */
	private Registration registration_;

	/**
	 * Accessor for the newly arrived bundle
	 * @return the bundle_
	 */
	public Bundle bundle() {
		return bundle_;
	}

	/**
	 * Setter for the newly arrived bundle
	 * @param bundle the bundle_ to set
	 */
	public void set_bundle(Bundle bundle) {
		bundle_ = bundle;
	}

	/**
	 * Accessor for the event source of the bundle
	 * @return the source_
	 */
	public event_source_t source() {
		return source_;
	}

	/**
	 * Setter for the event source of the bundle
	 * @param source the source_ to set
	 */
	public void set_source(event_source_t source) {
		source_ = source;
	}

	/**
	 * Accessor for the total bytes actually received
	 * @return the bytes_received_
	 */
	public int bytes_received() {
		return bytes_received_;
	}

	/**
	 * Setter for the total bytes actually received
	 * @param bytesReceived the bytes_received_ to set
	 */
	public void set_bytes_received(int bytesReceived) {
		bytes_received_ = bytesReceived;
	}

	/**
	 * Accessor for the Link from which bundle was received, if applicable
	 * @return the link_
	 */
	public Link link() {
		return link_;
	}

	/**
	 * Setter for the Link from which bundle was received, if applicable
	 * @param link the link_ to set
	 */
	public void set_link(Link link) {
		link_ = link;
	}

	/**
	 * Accessor for the Previous hop Endpoint id.
	 * @return the prevhop_
	 */
	public EndpointID prevhop() {
		return prevhop_;
	}

	/**
	 * Setter for the Previous hop Endpoint id.
	 * @param prevhop the prevhop_ to set
	 */
	public void set_prevhop(EndpointID prevhop) {
		prevhop_ = prevhop;
	}

	/**
	 * Accessor for the Registration where the bundle arrived
	 * @return the registration_
	 */
	public Registration registration() {
		return registration_;
	}

	/**
	 * Setter for the Registration where the bundle arrived
	 * @param registration the registration_ to set
	 */
	public void set_registration(Registration registration) {
		registration_ = registration;
	}
}