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

/**
 * Class for EIDReachable Report Event
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class EIDReachableReportEvent extends CLAQueryReport {
	/**
	 * main constructor
	 * @param query_id
	 * @param is_reachable
	 */
	public EIDReachableReportEvent(String query_id, boolean is_reachable) {
		super(event_type_t.CLA_EID_REACHABLE_REPORT, query_id);
		is_reachable_ = is_reachable;

	}

	/**
	 *  Flag indicating if the queried endpoint is reachable via the given interface
	 */
	private boolean is_reachable_;

	/**
	 * Getter for the Flag indicating if the queried endpoint is reachable via the given interface
	 * @return the is_reachable_
	 */
	public boolean is_reachable() {
		return is_reachable_;
	}

	/**
	 * Setter for the Flag indicating if the queried endpoint is reachable via the given interface
	 * @param isReachable the is_reachable_ to set
	 */
	public void set_is_reachable(boolean isReachable) {
		is_reachable_ = isReachable;
	}
};

