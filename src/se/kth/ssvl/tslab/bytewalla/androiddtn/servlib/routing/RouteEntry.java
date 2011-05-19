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

package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing;

import java.io.Serializable;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.Bundle;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.CustodyTimerSpec;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.ForwardingInfo;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.ForwardingInfo.action_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointIDPattern;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.StringVector;

/**
 * Class to represent route entry in the routing table
 * User can specify the EndpointIDPattern and the link for the route entry
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class RouteEntry implements Serializable {


	/**
	 *  serial versionUID to make this RouteEntry Serializable
	 */
	private static final long serialVersionUID = -151889647103500848L;

	/**
	 * First Constructor require EndpointIDPattern, Link
	 * @param dest_pattern the destination pattern this route represents
	 * @param link the link this route should forward to when it receive such dest_pattern
	 */
	public RouteEntry(final EndpointIDPattern dest_pattern, Link link) {
		dest_pattern_ = dest_pattern;
		source_pattern_ = EndpointIDPattern.WILDCARD_EID();
		bundle_cos_ = (1 << Bundle.priority_values_t.COS_BULK.getCode()
				| 1 << Bundle.priority_values_t.COS_NORMAL.getCode() | 1 << Bundle.priority_values_t.COS_EXPEDITED
				.getCode());
		priority_ = BundleRouter.config().default_priority();
		link_ = link;
		route_to_ = null;
		action_ = action_t.FORWARD_ACTION;
		custody_spec_ = null;
		info_ = null;

	}

	/**
	 * Second constructor require two EndpointIDPattern
	 * @param dest_pattern destination of this route
	 * @param route_to the pattern that the router should route to get to the destination
	 */

	public RouteEntry(final EndpointIDPattern dest_pattern,
			final EndpointIDPattern route_to) {
		dest_pattern_ = dest_pattern;
		source_pattern_ = EndpointIDPattern.WILDCARD_EID();
		bundle_cos_ = (1 << Bundle.priority_values_t.COS_BULK.getCode()
				| 1 << Bundle.priority_values_t.COS_NORMAL.getCode() | 1 << Bundle.priority_values_t.COS_EXPEDITED
				.getCode());
		priority_ = BundleRouter.config().default_priority();
		link_ = null;
		route_to_ = route_to;
		action_ = action_t.FORWARD_ACTION;
		custody_spec_ = null;
		info_ = null;

	}

	/**
	 * Format class in to String Buffer
	 * @see StringBuffer
	 */
	public final int format(StringBuffer buf) {
		String text = "\n" + dest_pattern_.uri().toString() + " -> "
				+ next_hop_str() + " (" + action_.toString() + " )";
		buf.append(text);
		return text.length();
	}

	/**
	 * Set CustodyTimerSpec for this Router
	 */
	public void set_custodyspec(CustodyTimerSpec spec) {
		custody_spec_ = spec;
	}


	/**
	 * Dump a header string into StringBuffer
	 */
	public static void dump_header(StringBuffer buf, int dest_eid_width,
			int source_eid_width, int next_hop_width) {
		buf
				.append(String
						.format(
								"%s %s %s    %s %s %s %s]\n"
								+ "%s %s %s    %s %s %s %s %s %s]\n"
										+ "%s\n",
								dest_eid_width,
								dest_eid_width, 
								"destination",
								source_eid_width, 
								source_eid_width,
								"source",
								"COS",
								next_hop_width,
								next_hop_width,
								"next hop",
								" fwd  ",
								"route",
								"custody timeout",

								dest_eid_width, 
								 
								"endpoint id",
								 
								source_eid_width,
								" eid"

								));

	}

	/**
	 * Dump a string representation of the route entry.
	 */
	public void dump(StringBuffer buf, StringVector long_strings,
			int dest_eid_width, int source_eid_width, int next_hop_width) {
		
		append_long_string(buf, long_strings, dest_eid_width, dest_pattern().uri().toString());
	    append_long_string(buf, long_strings, source_eid_width, source_pattern().uri().toString());

	    
	    int cos_bulk_value = (bundle_cos_ & (1 << Bundle.priority_values_t.COS_BULK.getCode()) ) > 0 ? '1' : '0';
	    int cos_normal_value = (bundle_cos_ & (1 << Bundle.priority_values_t.COS_NORMAL.getCode()) ) > 0 ? '1' : '0';
	    int cos_expedited_value = (bundle_cos_ & (1 << Bundle.priority_values_t.COS_EXPEDITED.getCode()) ) > 0 ? '1' : '0';
	    buf.append(String.format("%d%d%d -> ",cos_bulk_value, cos_normal_value, cos_expedited_value ));
	    
	    append_long_string(buf, long_strings, next_hop_width, next_hop_str());

	    if(custody_spec()!=null)
	    buf.append(String.format("%s %d [%d %d %d]\n",
	                 action_.toString(),
	                 priority(),
	                 custody_spec().min(),
	                 custody_spec().lifetime_pct(),
	                 custody_spec().max()));
		
	}

	/**
	 * Test function to JUnit test case
	 * @return Bundle Cos for this route
	 */
	public int test_bundle_cos() {
		return bundle_cos_;
	}

	/**
	 * Getter function for Destination EndpointIDPattern 
	 * @return
	 */
	public final EndpointIDPattern dest_pattern() {
		return dest_pattern_;
	}

	/**
	 * Getter function for Source EndpointIDPattern
	 * @return
	 */
	public final EndpointIDPattern source_pattern() {
		return source_pattern_;
	}

	/**
	 * Getter function for Link object
	 * @return link object associated with this Route Entry
	 */
	public final Link link() {
		return link_;
	}

	/**
	 * Getter function for route_to object
	 * @return EndpointIDPattern representing the route_to of this RouteEntry
	 */
	public final EndpointIDPattern route_to() {
		return route_to_;
	}

	
	/**
	 * Getter function for the priority of this route
	 * @return the priority of this route as an int
	 */
	public int priority() {
		return priority_;
	}

	
	/**
	 * Getter function for the RouteEntryInfo
	 * @return the RouteEntryInfo of this Route
	 * @see RouteEntryInfo
	 */
	public RouteEntryInfo info() {
		return info_;
	}

	/**
	 * Getter function for the CustodayTimerSpec for this RouteEntry
	 * @return the CustodayTimerSpec for this RouteEntry
	 */
	public final CustodyTimerSpec custody_spec() {
		return custody_spec_;
	}

	/**
	 * Getter function for the forwarding action for this RouteEntry 
	 * @return the forwarding action for this RouteEntry
	 */
	public ForwardingInfo.action_t action() {
		return action_;
	}

	
	/**
	 * Getter function for the next_hop_str of this RouteEntry
	 * @return the next hop String of this Route Entry
	 */
	public final String next_hop_str() {

		return (link_ != null) ? link_.name() : route_to().str();
	}

	/**
	 * Setter function for forwarding action for this RouteEntry 
	 * @param action forwarding action for this RouteEntry 
	 */
	public void set_action(ForwardingInfo.action_t action) {
		action_ = action;
	}

	
	/**
	 * Setter function for the RouteEntryInfo of this Route
	 * @param action the RouteEntryInfo of this Route
	 */
	public void set_info(RouteEntryInfo info) {
		info_ = info;
	}

	/**
	 * Add string to be display in dump function
	 * @param buf the StringBuffer to put data to
	 * @param long_strings the List of String
	 * @param width the width for display
	 * @param str the String to append
	 */
	private static void append_long_string(StringBuffer buf,
			StringVector long_strings, int width, final String str) {
		int tmplen;
		if (str.length() <= width) {
			buf.append(String.format("%d %d %s ", width, width, str));
		} else {
			int index;
			for (index = 0; index < long_strings.size(); index++) {
				if (long_strings.get(index).equals(str))
					break;
			}

			if (index == long_strings.size()) {
				long_strings.add(str);
			}

			String tmp   =  String.format("[%d] ", index);
			tmplen = tmp.length();
			buf.append(String.format("%d ... %s .. %d", width - 3 - tmplen, 
					str, tmp));
		}
	}

	/**
	 *  The pattern that matches bundles' destination eid
	 */
	private EndpointIDPattern dest_pattern_;

	/**
	 *  The pattern that matches bundles' source eid
	 */
	private EndpointIDPattern source_pattern_;

	/**
	 *  Bit vector of the bundle priority classes that should match this route
	 */
	private int bundle_cos_;

	/**
	 *  Route priority
	 */
	private int priority_;

	/**
	 *  Next hop link if known
	 */
	private Link link_;

	/**
	 *  Route destination for recursive lookups
	 */
	private EndpointIDPattern route_to_;

	/**
	 *  Forwarding action code
	 */
	private ForwardingInfo.action_t action_;

	/**
	 *  Custody timer specification
	 */
	private CustodyTimerSpec custody_spec_;

	/**
	 *  An abstraction to store algorithm specific information
	 */ 
	private RouteEntryInfo info_;

};
