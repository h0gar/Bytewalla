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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.List;

/**
 * Class to represent discovery setting in the DTN Configuration
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class DiscoveriesSetting {

	/**
	 * The configurable address family
	 */
	public enum address_family_t {
		IP("ip");
		private static final Map<String, address_family_t> lookupCaption = new HashMap<String, address_family_t>();

		static {
			for (address_family_t s : EnumSet.allOf(address_family_t.class)) {

				lookupCaption.put(s.getCaption(), s);
			}
		}

		private String caption;

		private address_family_t(String caption) {

			this.caption = caption;
		}

		public String getCaption() {
			return caption;
		}
	}

	/**
	 * The class represents discovery record in the discovery settings
	 */
	public static class DiscoveryEntry {
		private String id;
		private address_family_t address_family_;
		private short port;

		/**
		 * Accessor for unique identification for the Discovery
		 * @return the id
		 */
		public String id() {
			return id;
		}

		/**
		 * Setter function for unique identification for the Discovery
		 * @param id the id to set
		 */
		public void set_id(String id) {
			this.id = id;
		}

		/**
		 * Accessor for the address family of this discovery
		 * @return the address_family_
		 */
		public address_family_t address_family() {
			return address_family_;
		}

		/**
		 * Setter for the address family of this discovery
		 * @param addressFamily the address_family_ to set
		 */
		public void set_address_family(address_family_t address_family) {
			address_family_ = address_family;
		}

		/**
		 * Accessor for the port communicating by this Discovery
		 * @return the port
		 */
		public short port() {
			return port;
		}

		/**
		 * Setter for the port communicating by this Discovery
		 * @param port
		 *            the port to set
		 */
		public void set_port(short port) {
			this.port = port;
		}
	}

	/**
	 * Constructor
	 */
	public DiscoveriesSetting()
	{
		discovery_entries_ = new List<DiscoveryEntry>();
		announce_entries_  = new List<AnnounceEntry>();
	}
	
	/**
	 * The class represents Announce 
	 */
	public static class AnnounceEntry {
		private String interface_id_;
		private String discovery_id_;
		private conv_layer_type_t conv_layer_type_;
		private int interval_;

		/**
		 * Accessor for the interface ID set in the Interfaces Setting. This ID identifies where in which 
		 * interface this announce will make
		 * @return the interface_id
		 */
		public String interface_id() {
			return interface_id_;
		}

		/**
		 * Setter for the interface ID set in the Interfaces Setting.  This ID identifies where in which 
		 * interface this announce will make
		 * @param interfaceId
		 *            the interface_id to set
		 */
		public void set_interface_id(String interface_id) {
			interface_id_ = interface_id;
		}

		/**
		 * Accessor for the unique identifier of this Discovery Entry
		 * @return the discovery_id
		 */
		public String discovery_id() {
			return discovery_id_;
		}

		/**
		 * Setter for the unique identifier of this Discovery Entry
		 * @param discoveryId
		 *            the discovery_id to set
		 */
		public void set_discovery_id(String discovery_id) {
			discovery_id_ = discovery_id;
		}

		/**
		 * Accessor for how many seconds this Discovery will try to discover other nodes. The more often means more power consumption to the phone.
		 * The less often means the less chances the node will be discovered.
		 * @return the tcp_interval
		 */
		public int interval() {
			return interval_;
		}

		/**
		 * Setter for how many seconds this Discovery will try to discover other nodes. The more often means more power consumption to the phone.
		 * The less often means the less chances the node will be discovered.
		 * @param tcpInterval
		 *            the tcp_interval to set
		 */
		public void set_interval(int interval) {
			interval_ = interval;
		}

		/**
		 * Accessor for the Convergence Layer used in this Discovery
		 * @return the conv_layer_type_
		 */
		public conv_layer_type_t conv_layer_type() {
			return conv_layer_type_;
		}

		/**
		 * Setter for the Convergence Layer used in this Discovery
		 * @param convLayerType
		 *            the conv_layer_type_ to set
		 */
		public void set_conv_layer_type(conv_layer_type_t conv_layer_type) {
			conv_layer_type_ = conv_layer_type;
		}
	}

	/**
	 * The list of Announce in this DiscoveriesSetting
	 */
	private List<AnnounceEntry> announce_entries_;

	/**
	 * The list of Discovery in this DiscoveriesSetting
	 */
	private List<DiscoveryEntry> discovery_entries_;

	/**
	 * The list of Discovery in this DiscoveriesSetting
	 * @return the discoveries_
	 */
	public List<DiscoveryEntry> discovery_entries() {
		return discovery_entries_;
	}

	/**
	 * Set the discovery_entries for this Discoveries Setting
	 * @param discoveries the discoveries_ to set
	 */
	public void set_discovery_entries(List<DiscoveryEntry> discovery_entries) {
		discovery_entries_ = discovery_entries;
	}

	/**
	 * The list of Announce in this DiscoveriesSetting
	 * @return the announce_entries_ the list of Announce in this DiscoveriesSetting
	 */
	public List<AnnounceEntry> announce_entries() {
		return announce_entries_;
	}

	/**
	 *  Set the announce_entries for this Discoveries Setting
	 * @param announceEntries
	 *            the announce_entries_ to set
	 */
	public void set_announce_entries(List<AnnounceEntry> announce_entries) {
		announce_entries_ = announce_entries;
	}
}
