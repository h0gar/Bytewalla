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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


/**
 * Type codes for events / requests.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public enum event_type_t {
	BUNDLE_RECEIVED("BUNDLE_RECEIVED", 0x1), // /< New bundle arrival
	BUNDLE_TRANSMITTED("BUNDLE_TRANSMITTED"), // /< Bundle or fragment
												// successfully sent
	BUNDLE_DELIVERED("BUNDLE_DELIVERED"), // /< Bundle locally delivered
	BUNDLE_DELIVERY("BUNDLE_DELIVERY"), // /< Bundle delivery (with payload)
	BUNDLE_EXPIRED("BUNDLE_EXPIRED"), // /< Bundle expired
	BUNDLE_NOT_NEEDED("BUNDLE_NOT_NEEDED"), // /< Bundle no longer needed
	BUNDLE_FREE("BUNDLE_FREE"), // /< No more references to the bundle
	BUNDLE_FORWARD_TIMEOUT("BUNDLE_FORWARD_TIMEOUT"), // /< A Mapping timed out
	BUNDLE_SEND("BUNDLE_SEND"), // /< Send a bundle
	BUNDLE_CANCEL("BUNDLE_CANCEL"), // /< Cancel a bundle transmission
	BUNDLE_CANCELLED("BUNDLE_CANCELLED"), // /< Bundle send cancelled
	BUNDLE_INJECT("BUNDLE_INJECT"), // /< Inject a bundle
	BUNDLE_INJECTED("BUNDLE_INJECTED"), // /< A bundle was injected
	BUNDLE_ACCEPT_REQUEST("BUNDLE_ACCEPT_REQUEST"), // /< Request acceptance of
													// a new bundle
	BUNDLE_DELETE("BUNDLE_DELETE"), // /< Request deletion of a bundle
	BUNDLE_QUERY("BUNDLE_QUERY"), // /< Bundle query
	BUNDLE_REPORT("BUNDLE_REPORT"), // /< Response to bundle query
	BUNDLE_ATTRIB_QUERY("BUNDLE_ATTRIB_QUERY"), // /< Query for a bundle's
												// attributes
	BUNDLE_ATTRIB_REPORT("BUNDLE_ATTRIB_REPORT"), // /< Report with bundle
													// attributes

	CONTACT_UP("CONTACT_UP"), // /< Contact is up
	CONTACT_DOWN("CONTACT_DOWN"), // /< Contact abnormally terminated
	CONTACT_QUERY("CONTACT_QUERY"), // /< Contact query
	CONTACT_REPORT("CONTACT_REPORT"), // /< Response to contact query
	CONTACT_ATTRIB_CHANGED("CONTACT_ATTRIB_CHANGED"), // /< An attribute changed

	LINK_CREATED("LINK_CREATED"), // /< Link is created into the system
	LINK_DELETED("LINK_DELETED"), // /< Link is deleted from the system
	LINK_AVAILABLE("LINK_AVAILABLE"), // /< Link is available
	LINK_UNAVAILABLE("LINK_UNAVAILABLE"), // /< Link is unavailable
	LINK_BUSY("LINK_BUSY"), // /< Link is busy
	LINK_CREATE("LINK_CREATE"), // /< Create and open a new link
	LINK_DELETE("LINK_DELETE"), // /< Delete a link
	LINK_RECONFIGURE("LINK_RECONFIGURE"), // /< Reconfigure a link
	LINK_QUERY("LINK_QUERY"), // /< Link query
	LINK_REPORT("LINK_REPORT"), // /< Response to link query
	LINK_ATTRIB_CHANGED("LINK_ATTRIB_CHANGED"), // /< An attribute changed

	LINK_STATE_CHANGE_REQUEST("LINK_STATE_CHANGE_REQUEST"), // /< Link state
															// should be changed

	REASSEMBLY_COMPLETED("REASSEMBLY_COMPLETED"), // /< Reassembly completed

	REGISTRATION_ADDED("REGISTRATION_ADDED"), // /< New registration arrived
	REGISTRATION_REMOVED("REGISTRATION_REMOVED"), // /< Registration removed
	REGISTRATION_EXPIRED("REGISTRATION_EXPIRED"), // /< Registration expired
	REGISTRATION_DELETE("REGISTRATION_DELETE"), // /< Registration to be deleted

	ROUTE_ADD("ROUTE_ADD"), // /< Add a new entry to the route table
	ROUTE_DEL("ROUTE_DEL"), // /< Remove an entry from the route table
	ROUTE_QUERY("ROUTE_QUERY"), // /< Static route query
	ROUTE_REPORT("ROUTE_REPORT"), // /< Response to static route query

	CUSTODY_SIGNAL("CUSTODY_SIGNAL"), // /< Custody transfer signal received
	CUSTODY_TIMEOUT("CUSTODY_TIMEOUT"), // /< Custody transfer timer fired

	DAEMON_SHUTDOWN("SHUTDOWN"), // /< Shut the daemon down cleanly
	DAEMON_STATUS("DAEMON_STATUS"), // /< No-op event to check the daemon

	CLA_SET_PARAMS("CLA_SET_PARAMS"), // /< Set CLA configuration
	CLA_PARAMS_SET("CLA_PARAMS_SET"), // /< CLA configuration changed
	CLA_SET_LINK_DEFAULTS("CLA_SET_LINK_DEFAULTS"), // /< Set defaults for new
													// links
	CLA_EID_REACHABLE("CLA_EID_REACHABLE"), // /< A new EID has been discovered

	CLA_BUNDLE_QUEUED_QUERY("CLA_BUNDLE_QUEUED_QUERY"), // /< Query if a bundle
														// is queued at the CLA
	CLA_BUNDLE_QUEUED_REPORT("CLA_BUNDLE_QUEUED_REPORT"), // /< Report if a
															// bundle is queued
															// at the CLA
	CLA_EID_REACHABLE_QUERY("CLA_EID_REACHABLE_QUERY"), // /< Query if an EID is
														// reachable by the CLA
	CLA_EID_REACHABLE_REPORT("CLA_EID_REACHABLE_REPORT"), // /< Report if an EID
															// is reachable by
															// the CLA
	CLA_LINK_ATTRIB_QUERY("CLA_LINK_ATTRIB_QUERY"), // /< Query CLA for a link's
													// attributes
	CLA_LINK_ATTRIB_REPORT("CLA_LINK_ATTRIB_REPORT"), // /< Report from CLA with
														// link attributes
	CLA_IFACE_ATTRIB_QUERY("CLA_IFACE_ATTRIB_QUERY"), // /< Query CLA for an
														// interface's
														// attributes
	CLA_IFACE_ATTRIB_REPORT("CLA_IFACE_ATTRIB_REPORT"), // /< Report from CLA
														// with interface
														// attributes
	CLA_PARAMS_QUERY("CLA_PARAMS_QUERY"), // /< Query CLA for config parameters
	CLA_PARAMS_REPORT("CLA_PARAMS_REPORT"), // /< Report from CLA with config
											// paramters

	;

	private static final Map<event_type_t, String> captionLookup = new HashMap<event_type_t, String>();
	private static final Map<event_type_t, Integer> codeLookup = new HashMap<event_type_t, Integer>();

	static {
		for (event_type_t event : EnumSet.allOf(event_type_t.class)) {
			captionLookup.put(event, event.getCaption());
			codeLookup.put(event, event.getCode());
		}
	}

	private String caption;
	private int code = -1; // -1 = undefined

	private event_type_t(String caption) {
		this.caption = caption;
	}

	private event_type_t(String caption, int code) {
		this.caption = caption;
		this.code = code;
	}

	public String getCaption() {
		return caption;
	}

	// Keep the name similarity in DTN2
	public String event_to_str() {
		return caption;
	}

	public Integer getCode() {
		return code;
	}

	public static String get(event_type_t event) {
		return captionLookup.get(event);
	}
}

