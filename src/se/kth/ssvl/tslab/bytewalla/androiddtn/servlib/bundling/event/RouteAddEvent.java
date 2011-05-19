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

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.RouteEntry;

/**
 * Route Add Event 
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class RouteAddEvent  extends BundleEvent {
	public RouteAddEvent(RouteEntry entry) {
		super(event_type_t.ROUTE_ADD);
		entry_ = entry;
	}

	/**
	 *  The route table entry to be added
	 */
	private RouteEntry entry_;

	/**
	 * Getter for the route table entry to be added
	 * @return the entry_
	 */
	public RouteEntry entry() {
		return entry_;
	}

	/**
	 * Setter for the route table entry to be added
	 * @param entry the entry_ to set
	 */
	public void set_entry(RouteEntry entry) {
		entry_ = entry;
	}
}

