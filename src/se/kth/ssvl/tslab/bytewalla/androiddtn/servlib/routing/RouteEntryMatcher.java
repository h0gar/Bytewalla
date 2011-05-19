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

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointIDPattern;

/**
 * Code for matching Router Entry during the querying process
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class RouteEntryMatcher
{
	/**
	 * matching type enum. There are two matching types including EndpointIDPattern Matching and Link Matching
	 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
	 */
	public static enum match_type_t
	{
		EndpointIdPatternMatching,
		LinkMatching
	}
	
	/**
	 * Destination EndpointID Pattern that will be matched using this matcher
	 * @see EndpointIDPattern
	 */
	private EndpointIDPattern dest_pattern_;
	
	/**
	 * Link object for matching with this matcher
	 * @see Link 
	 */
	private Link link_;
	
	/**
	 * The matching type enum
	 * @see match_type_t
	 */
	private match_type_t match_type_;
	/**
	 * Constructor for the matcher to match with the Destination EndpointIDPattern
	 * @param dest_pattern The EndpointIDPattern to be matched
	 */
	public  RouteEntryMatcher(final EndpointIDPattern dest_pattern) {
		dest_pattern_ = dest_pattern;
		match_type_ = match_type_t.EndpointIdPatternMatching;
	}
	
	
	/**
	 * Constructor for the matcher to match with the given link
	 * @param link the given link
	 */
	public  RouteEntryMatcher(final Link link) {
		link_ = link;
		match_type_ = match_type_t.LinkMatching;
	}
	
	/**
	 * Check whether it is matched by this matcher or not
	 * @param entry the input RouteEntry to test
	 * @return the result of matching
	 */
	public  boolean match(RouteEntry entry){
		if ( match_type_ ==  match_type_t.EndpointIdPatternMatching && entry.route_to() != null)  
			  return entry.dest_pattern().equals(dest_pattern_);
			  
		if ( match_type_ ==  match_type_t.LinkMatching  && entry.link() != null)	  
			  return entry.link().equals(link_);
	    
		// Invalid match type, return false
		return false;
	}
	


	/**
	 * Getter function for the match type
	 * @return the match_type
	 */
	public match_type_t match_type() {
		return match_type_;
	}
	
	
}
