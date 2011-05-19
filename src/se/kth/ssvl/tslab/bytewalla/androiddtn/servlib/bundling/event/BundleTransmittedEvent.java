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
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Contact;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;

/**
 * Event generated from the Contact in the Convergence Layer to notify the main components about Bundle successfully transmission
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class BundleTransmittedEvent extends BundleEvent {
	/**
	 * main constructor
	 * @param bundle
	 * @param contact
	 * @param link
	 * @param bytes_sent
	 * @param reliably_sent
	 */
	public BundleTransmittedEvent(Bundle bundle, Contact contact,
			Link link, int bytes_sent,

			int reliably_sent) {

		super(event_type_t.BUNDLE_TRANSMITTED);
		bundle_ = bundle;
		contact_   = contact;
		link_      = link;
		bytes_sent_ = bytes_sent;
		reliably_sent_ = reliably_sent;
		
	}

	/**
	 *  The transmitted bundle
	 */
	private Bundle bundle_;

	/**
	 *  The contact where the bundle was sent
	 */
	private Contact contact_;

	/**
	 *  Total number of bytes sent
	 */
	private int bytes_sent_;

	/**
	 *  The total number of bytes reliably sent
	 */
	private int reliably_sent_;

	/**
	 * The link over which the bundle was sent
	 * (may not have a contact when the transmission result is reported)
	 */
	private Link link_;

	/**
	 * Getter for the transmitted bundle
	 * @return the bundle_
	 */
	public Bundle bundle() {
		return bundle_;
	}

	/**
	 * Setter for the transmitted bundle
	 * @param bundle the bundle_ to set
	 */
	public void set_bundle(Bundle bundle) {
		bundle_ = bundle;
	}

	/**
	 * Getter for the contact where the bundle was sent
	 * @return the contact_
	 */
	public Contact contact() {
		return contact_;
	}

	/**
	 * Setter for the contact where the bundle was sent
	 * @param contact the contact_ to set
	 */
	public void set_contact(Contact contact) {
		contact_ = contact;
	}

	/**
	 * Getter for the total number of bytes sent
	 * @return the bytes_sent_
	 */
	public int bytes_sent() {
		return bytes_sent_;
	}

	/**
	 * Setter for the total number of bytes sent
	 * @param bytesSent the bytes_sent_ to set
	 */
	public void set_bytes_sent(int bytesSent) {
		bytes_sent_ = bytesSent;
	}

	/**
	 * Getter for the total number of bytes reliably sent
	 * @return the reliably_sent_
	 */
	public int reliably_sent() {
		return reliably_sent_;
	}

	/**
	 * Setter for the total number of bytes reliably sent
	 * @param reliablySent the reliably_sent_ to set
	 */
	public void set_reliably_sent(int reliablySent) {
		reliably_sent_ = reliablySent;
	}

	/**
	 * Getter for the link the bundle was transmitted
	 * @return the link_
	 */
	public Link link() {
		return link_;
	}

	/**
	 * Setter for the link the bundle was transmitted
	 * @param link the link_ to set
	 */
	public void set_link(Link link) {
		link_ = link;
	}
}
