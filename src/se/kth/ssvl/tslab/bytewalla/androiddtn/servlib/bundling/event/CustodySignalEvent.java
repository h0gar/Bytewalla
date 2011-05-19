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

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.CustodySignal;


/**
 * Event class for custody transfer signal arrivals.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */

public class CustodySignalEvent extends BundleEvent {
	public CustodySignalEvent(CustodySignal.data_t data) {
		super(event_type_t.CUSTODY_SIGNAL);
		data_ = data;

	}

	/**
	 *  The parsed data from the custody transfer signal
	 */
	private CustodySignal.data_t data_;

	/**
	 * Getter for the parsed data from the custody transfer signal
	 * @return the data_
	 */
	public CustodySignal.data_t data() {
		return data_;
	}

	/**
	 * Setter for the parsed data from the custody transfer signal
	 * @param data the data_ to set
	 */
	public void set_data(CustodySignal.data_t data) {
		data_ = data;
	}
};
