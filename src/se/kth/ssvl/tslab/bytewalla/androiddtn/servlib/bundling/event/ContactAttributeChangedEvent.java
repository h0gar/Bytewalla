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

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Contact;


/**
 * Event class for contact attribute change
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class ContactAttributeChangedEvent extends ContactEvent {
	/**
	 * main constructor
	 * @param contact
	 * @param reason
	 */
	public ContactAttributeChangedEvent(final Contact contact,
			reason_t reason) 
	{
		super(event_type_t.CONTACT_ATTRIB_CHANGED, reason);
		
		contact_ = contact;
		reason_  = reason;
	}

	/**
	 * The contact whose attributes changed
	 */
	private Contact contact_;

	/**
	 * Getter for contact whose attributes changed
	 * @return the contact_
	 */
	public Contact contact() {
		return contact_;
	}

	/**
	 * Setter for the contact whose attributes change
	 * @param contact the contact_ to set
	 */
	public void set_contact(Contact contact) {
		contact_ = contact;
	}
};
