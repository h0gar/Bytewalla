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

package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling;

import java.io.Serializable;
import java.util.HashMap;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;

/**
 * Caching of Bundle implementation for detecting duplicated Bundle
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class BundleInfoCache implements Serializable{

	/**
	 * Serial version UID to support Java Serializable 
	 */
	private static final long serialVersionUID = 6810683934464012479L;
	
	/**
	 * Internal HashMap to keep track of the Bundles
	 */
	private HashMap<Bundle,EndpointID> bundle_info_cache_;
	
	    /**
	     * "Constructor that takes the and the number of entries to
	     * maintain in the cache." [DTN2]
	     */
		public  BundleInfoCache(int capacity)
		{
			bundle_info_cache_ = new HashMap<Bundle, EndpointID>(capacity);
		}

	
	    /**
	     * "Try to add the bundle to the cache. If it already exists in the
	     * cache, adding it again fails, and the method returns false." [DTN2]
	     */
		public boolean add_entry(final Bundle bundle, final EndpointID prevhop)
		{
			if (bundle_info_cache_.containsKey(bundle)) return false;
			else
			{
			bundle_info_cache_.put(bundle, prevhop);
			return true;
			}
		}
		

	
		
	    /**
	     * "Check if the given bundle is in the cache, returning the EID of
	     * the node from which it arrived (if known).
	     * Calling get after word
	     * Return null if it's not found" [DTN2]
	     */
		public EndpointID lookup(final Bundle bundle)
		{
			
			return bundle_info_cache_.get(bundle);
		}

	    /**
	     * "Flush the cache." [DTN2]
	     */
		public void evict_all()
		{
			bundle_info_cache_.clear();
		}
	
}
