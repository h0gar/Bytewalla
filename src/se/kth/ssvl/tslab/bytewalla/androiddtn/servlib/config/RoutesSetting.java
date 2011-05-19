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

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.BundleRouter.router_type_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.List;

/**
 * This class represents RouteSetting in the configuration file
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public class RoutesSetting {
	/**
	 * ROuter type for this setting
	 */
	private router_type_t router_type_;
	
	/**
	 * The local EID of this setting
	 */
	private String local_eid;
	
	/**
	 * Class to represents route entry inside
	 */
	public static class RouteEntry
	{
		/**
		 * Empty constructor
		 */
		public RouteEntry()
		{
			
		}
		
		/**
		 * Constructor with dest and link_id setting
		 */
		public RouteEntry(String dest, String link_id)
		{
			dest_ = dest;
			link_id_ = link_id;
		}
		private String dest_;
		private String link_id_;
		/**
		 * Accessor for the destination of this route entry
		 * @return the dest
		 */
		public String dest() {
			return dest_;
		}
		/**
		 * Setter for the destination this route entry
		 * @param dest the dest to set
		 */
		public void set_dest(String dest) {
			this.dest_ = dest;
		}
		/**
		 * Accessor for the link ID of this route entry
		 * @return the link_id
		 */
		public String link_id() {
			return link_id_;
		}
		/**
		 * Setter for the link ID of this route entry
		 * @param linkId the link_id to set
		 */
		public void set_link_id(String link_id) {
			link_id_ = link_id;
		}
		
	}
	/**
	 * Constructor of this route setting
	 */
	public RoutesSetting()
	{
		route_entries_ = new List<RouteEntry>();
	}
	/**
	 * Accessor for the router type of this route setting
	 * @return the router_type_
	 */
	public router_type_t router_type() {
		return router_type_;
	}
	/**
	 * Setter for the router type of this route setting
	 * @param routerType the router_type_ to set
	 */
	public void set_router_type(router_type_t router_type) {
		router_type_ = router_type;
	}
	
	private List<RouteEntry> route_entries_;


	/**
	 * Accessor for the route entries inside this route setting
	 * @return the route_entries_
	 */
	public List<RouteEntry> route_entries() {
		return route_entries_;
	}


	/**
	 * Setter for the route entries inside this route setting
	 * @param routeEntries the route_entries_ to set
	 */
	public void set_route_entries(List<RouteEntry> route_entries) {
		route_entries_ = route_entries;
	}
	/**
	 * Accessor for Local Endpoint ID of this route setting 
	 * @return the local_eid
	 */
	public String local_eid() {
		return local_eid;
	}
	/**
	 * Setter for Local Endpoint ID of this route setting 
	 * @param localEid the local_eid to set
	 */
	public void set_local_eid(String localEid) {
		local_eid = localEid;
	}
	
	public ProphetSetting prophet = new ProphetSetting();
}
