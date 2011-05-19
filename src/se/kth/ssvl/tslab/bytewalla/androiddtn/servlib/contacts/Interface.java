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

import java.net.ServerSocket;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.CLInfo;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.ConvergenceLayer;

/**
 * "Abstraction of a local dtn interface.
 * 
 * Generally, interfaces are created by the configuration file / console"
 * [DTN2].
 * 
 * @author María José Peroza Marval (mjpm@kth.se)
 */

public class Interface {

	// Accessors
	public String name() {
		return name_;
	}

	public String proto() {
		return proto_;
	}

	public ConvergenceLayer clayer() {
		return clayer_;
	}

	public CLInfo cl_info() {
		return cl_info_;
	}

	public ServerSocket socket() {
		return socket_;
	}

	public static int iface_counter() {
		return iface_counter_;
	}

	public static void set_iface_counter(int ifaceCounter) {
		iface_counter_ = ifaceCounter;
	}

	/**
	 * Store the ConvergenceLayer specific state.
	 */
	public void set_cl_info(CLInfo cl_info) {
		assert cl_info_ == null && cl_info != null : cl_info_ != null
				&& cl_info == null;

		cl_info_ = cl_info;
	}

	public void set_socket(ServerSocket socket) {
		socket_ = socket;
	}

	public Interface(String name, String proto, ConvergenceLayer clayer) {

		name_ = name;
		proto_ = proto;
		clayer_ = clayer;
		cl_info_ = null;
		socket_ = null;

	}

	protected String name_; // /< Name of the interface
	protected String proto_; // /< What type of CL
	protected ConvergenceLayer clayer_; // /< Convergence layer to use
	protected CLInfo cl_info_; // /< Convergence layer specific state
	protected ServerSocket socket_; // /< Socket of the interface
	private static int iface_counter_; // /< Number of interfaces

}
