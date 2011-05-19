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
package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config;

import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.List;

/**
 * The class represents interface setting.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public class InterfacesSetting {
	
	/**
	 * The interface entry object inside the InterfacesSetting
	 */
	public static class InterfaceEntry
	{
		private conv_layer_type_t conv_layer_type_;
		private String id;
		private short local_port;
		
		private boolean fixed_local_port_;
		/**
		 * @return the conv_layer_type_
		 */
		public conv_layer_type_t conv_layer_type() {
			return conv_layer_type_;
		}
		/**
		 * @param convLayerType the conv_layer_type_ to set
		 */
		public void set_conv_layer_type(conv_layer_type_t conv_layer_type) {
			conv_layer_type_ = conv_layer_type;
		}
		/**
		 * @return the id
		 */
		public String id() {
			return id;
		}
		/**
		 * @param id the id to set
		 */
		public void set_id(String id) {
			this.id = id;
		}
		/**
		 * @return the local_port
		 */
		public short local_port() {
			return local_port;
		}
		/**
		 * @param localPort the local_port to set
		 */
		public void set_local_port(short localPort) {
			local_port = localPort;
		}
		/**
		 * @return the fixed_local_port
		 */
		public boolean fixed_local_port() {
			return fixed_local_port_;
		}
		/**
		 * @param fixedLocalPort the fixed_local_port to set
		 */
		public void set_fixed_local_port(boolean fixed_local_port) {
			fixed_local_port_ = fixed_local_port;
		}
		
	}
	/**
	 * Constructor
	 */
	public InterfacesSetting()
	{
		interface_entries_ = new List<InterfaceEntry>();
	}
	
	/**
	 * The list maintaining internal interface entries
	 */
	private List<InterfaceEntry> interface_entries_;

	/**
	 * Accessor for the interface entries of this InterfaceSettings
	 * @return the interfaces_
	 */
	public List<InterfaceEntry> interface_entries() {
		return interface_entries_;
	}

	/**
	 * Setter for the interface entries of this InterfaceSettings
	 * @param interfaces the interfaces_ to set
	 */
	public void set_interface_entries(List<InterfaceEntry> interface_entries) {
		interface_entries_ = interface_entries;
	}
	
}
