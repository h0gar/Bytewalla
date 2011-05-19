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

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.AttributeNameVector;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.ConvergenceLayer;

/**
 * BundleEvent to query parameters of the Convergence Layer
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class CLAParametersQueryRequest  extends CLAQueryReport {
	
	/**
	 * Constructor
	 * @param query_id unique identifier 
	 * @param cla the Convergence Layer in question
	 * @param parameter_names
	 */
	public CLAParametersQueryRequest(final String query_id,
			ConvergenceLayer cla, AttributeNameVector parameter_names) {

		super(event_type_t.CLA_PARAMS_QUERY, query_id, true);
		cla_ = cla;
		parameter_names_ = parameter_names;

	}

	/**
	 *  Convergence layer for which the given parameters are requested.
	 */
	private ConvergenceLayer cla_;

	/**
	 *  Convergence layer parameters requested by name.
	 */
	private AttributeNameVector parameter_names_;

	/**
	 * Getter for the Convergence Layer in question
	 * @return the cla_
	 */
	public ConvergenceLayer cla() {
		return cla_;
	}

	/**
	 * Setter for the Convergence Layer in question
	 * @param cla the cla_ to set
	 */
	public void set_cla(ConvergenceLayer cla) {
		cla_ = cla;
	}

	/**
	 * Getter for the Convergence layer parameters requested by name.
	 * @return the parameter_names_
	 */
	public AttributeNameVector parameter_names() {
		return parameter_names_;
	}

	/**
	 * Setter for the Convergence layer parameters requested by name.
	 * @param parameterNames the parameter_names_ to set
	 */
	public void set_parameter_names(AttributeNameVector parameterNames) {
		parameter_names_ = parameterNames;
	}
};
