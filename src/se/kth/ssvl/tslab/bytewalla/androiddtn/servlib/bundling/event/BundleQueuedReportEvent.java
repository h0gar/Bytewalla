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
 * BundleEvent report class to response for the BundleQueuedQueryRequest
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class BundleQueuedReportEvent extends CLAQueryReport {
	/**
	 * main Constructor
	 * @param query_id
	 * @param is_queued
	 */
	public BundleQueuedReportEvent(String query_id, boolean is_queued) {
		super(event_type_t.CLA_BUNDLE_QUEUED_REPORT, query_id);
		is_queued_ = is_queued;

	}

	/**
	 *  Flag indicating whether the specific bundle was queued on the given link;
	 */ 
	private boolean is_queued_;

	/**
	 *  Accessor for the flag indicating whether the specific bundle was queued on the given link;
	 * @return the is_queued_
	 */
	public boolean is_queued() {
		return is_queued_;
	}

	/**
	 * Setter for the flag indicating whether the specific bundle was queued on the given link;
	 * @param isQueued the is_queued_ to set
	 */
	public void set_is_queued(boolean is_queued) {
		is_queued_ = is_queued;
	}
};