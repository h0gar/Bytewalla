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
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleList;


/**
 * Event class for reassembly completion.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class ReassemblyCompletedEvent extends BundleEvent {
	public ReassemblyCompletedEvent(Bundle bundle, BundleList fragments)

	{
		super(event_type_t.REASSEMBLY_COMPLETED);
		bundle_ = bundle;
		fragments_ = new BundleList("fragments_");
		fragments.move_contents(fragments_);
	}

	/**
	 * The newly reassembled bundle
	 */
	private Bundle bundle_;

	/**
	 *  The list of bundle fragments
	 */
	private BundleList fragments_;

	/**
	 * Getter for the newly reassembled bundle
	 * @return the bundle_
	 */
	public Bundle bundle() {
		return bundle_;
	}

	/**
	 * Setter for the newly reassembled bundle
	 * @param bundle the bundle_ to set
	 */
	public void set_bundle(Bundle bundle) {
		bundle_ = bundle;
	}

	/**
	 * Getter for the list of bundle fragments 
	 * @return the fragments_
	 */
	public BundleList fragments() {
		return fragments_;
	}

	/**
	 * Setter for the list of bundle fragments 
	 * @param fragments the fragments_ to set
	 */
	public void set_fragments(BundleList fragments) {
		fragments_ = fragments;
	}
};