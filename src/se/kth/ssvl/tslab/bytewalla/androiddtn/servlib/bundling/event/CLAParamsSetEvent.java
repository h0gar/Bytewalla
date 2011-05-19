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

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.ConvergenceLayer;

/**
 * BundleEvent to update CLA's parameter.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class CLAParamsSetEvent extends BundleEvent {
	/**
	 * main constructor
	 * @param cla
	 * @param name
	 */
	public CLAParamsSetEvent(ConvergenceLayer cla, String name) {
		super(event_type_t.CLA_PARAMS_SET);
		cla_ = cla;
		name_ = name;

	}

	/**
	 *  CL that changed
	 */
	private ConvergenceLayer cla_;

	/**
	 *  Name of CL 
	 */
	private String name_;

	/**
	 * Getter for the CL
	 * @return the cla_
	 */
	public ConvergenceLayer cla() {
		return cla_;
	}

	/**
	 * Setter for the CL
	 * @param cla the cla_ to set
	 */
	public void set_cla(ConvergenceLayer cla) {
		cla_ = cla;
	}

	/**
	 * Getter for the Name of CL 
	 * @return the name_
	 */
	public String name() {
		return name_;
	}

	/**
	 * Setter for the Name of CL 
	 * @param name the name_ to set
	 */
	public void set_name(String name) {
		name_ = name;
	}
};