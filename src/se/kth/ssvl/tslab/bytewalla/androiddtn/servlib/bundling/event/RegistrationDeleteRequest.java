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

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.reg.Registration;

/**
 * Registration Delete Event
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class RegistrationDeleteRequest  extends BundleEvent {
	/**
	 * main constructor
	 * @param registration
	 */
	public RegistrationDeleteRequest(Registration registration) {
		super(event_type_t.REGISTRATION_DELETE);
		daemon_only_ = true;
		registration_ = registration;

	}

	/**
	 *  The registration to be deleted
	 */
	private Registration registration_;
	
	/**
	 * Getter for the registration to be deleted
	 * @return the registration_
	 */
	public Registration registration() {
		return registration_;
	}

	/**
	 * Setter for the registration to be deleted
	 * @param registration the registration_ to set
	 */
	public void set_registration(Registration registration) {
		registration_ = registration;
	}
};