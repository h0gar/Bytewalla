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
 * Event class for new registration arrivals.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class RegistrationAddedEvent extends BundleEvent {
	public RegistrationAddedEvent(Registration reg, event_source_t source) {
		
		super(event_type_t.REGISTRATION_ADDED);
		
		registration_ = reg;
		source_ = source;
		
	}

	/**
	 *  The newly added registration
	 */
	private Registration registration_;

	/**
	 *  Where is the registration added
	 */
	private event_source_t source_;

	/**
	 * Getter for the newly added registration
	 * @return the registration_
	 */
	public Registration registration() {
		return registration_;
	}

	/**
	 * Setter for the newly added registration
	 * @param registration the registration_ to set
	 */
	public void set_registration(Registration registration) {
		registration_ = registration;
	}

	/**
	 * Getter for where is the registration added
	 * @return the source_
	 */
	public event_source_t source() {
		return source_;
	}

	/**
	 * Setter for where is the registration added
	 * @param source the source_ to set
	 */
	public void set_source(event_source_t source) {
		source_ = source;
	}
};
