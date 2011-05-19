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

import java.util.Comparator;

/**
 * Comparator class for sorting the RouteEntry. This is used for sorting the Route Entry before processing
 * This comparator compares based on the Route Priority.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class RouteEntryComparator implements Comparator<RouteEntry>{


	/**
	 * compare function override from Java Comparator
	 */
	
	public int compare(RouteEntry r1, RouteEntry r2) {
		
		if ( r1.priority() < r2.priority() ) return -1;
		if ( r1.priority() > r2.priority() ) return +1;
		return r1.link().bytes_queued() < r2.link().bytes_queued()? -1 :+1;
	} 


}
