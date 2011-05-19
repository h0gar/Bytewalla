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

package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts;

import java.util.HashSet;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.ConvergenceLayer;

/**
 * "Abstraction for a SCHEDULED link. Scheduled links have a list of future
 * contacts" [DTN2].
 * 
 *@author María José Peroza Marval (mjpm@kth.se)
 */

public class ScheduledLink extends Link {

	/**
	 * Unique identifier according to Java Serializable specification
	 */
	private static final long serialVersionUID = -8410070509285427644L;

	/**
	 * Constructor
	 */
	public ScheduledLink(String name, ConvergenceLayer cl, String nexthop) {

		super(name, Link.link_type_t.SCHEDULED, cl, nexthop);
		fcts_ = new HashSet<FutureContact>();

	}

	/**
	 * Return the list of future contacts that exist on the link
	 */
	public HashSet<FutureContact> future_contacts() {
		return fcts_;
	}

	// Add a future contact
	public void add_fc(FutureContact fc) {
		fcts_.add(fc);
	}

	// Remove a future contact
	public void delete_fc(FutureContact fc) {
		fcts_.remove(fc);
	}

	// Return list of all future contacts
	public HashSet<FutureContact> future_contacts_list() {
		return fcts_;
	}

	protected HashSet<FutureContact> fcts_;
}

/**
 * "Abstract base class for FutureContact Relevant only for scheduled links"
 * [DTN2].
 */
class FutureContact {

	/**
	 * Constructor
	 */
	public FutureContact() {
		start_ = 0;
		duration_ = 0;
	}

	// / Time at which contact starts, 0 value means not defined
	long start_;

	// / Duration for this future contact, 0 value means not defined
	long duration_;

}
