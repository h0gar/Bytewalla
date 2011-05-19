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

package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming;

import java.util.HashMap;

/**
 * The table of registered Schemes.
 * This table is implemented as Singleton
 */
public  class SchemeTable {
	
	/**
	 *  Singleton instance Implementation of the SchemeTable
	 */
	private static SchemeTable instance_ = null;

	/**
	 * Private constructor for Singleton Implementation of the SchemeTable
	 */
    private SchemeTable() {
       DTNScheme dtnScheme = DTNScheme.getInstance();
       table_ = new HashMap<String, Scheme>();
       register_scheme("dtn", dtnScheme);
     
    	
    	// Exists only to defeat instantiation
    }
    
    /**
     * Singleton Implementation Getter function
     * @return an singleton instance of SchemeTable
     */
    public static SchemeTable getInstance() {
      if(instance_ == null) {
         instance_ = new SchemeTable();
      }
      return instance_;
   }
	// End Singleton Implementation of the SchemeTable
    
    
	
    /**
     * Register the given scheme.
     */
    public void register_scheme(String scheme_str, Scheme scheme)
    {
    	table_.put(scheme_str, scheme);
    }
    
    
    /**
     * Find the appropriate Scheme instance based on the URI
     * scheme of the endpoint id scheme.
     *
     * @return the instance if it exists or NULL if there's no match
     */ 
    public Scheme lookup(String scheme_str){
    	
    	
    	return table_.get(scheme_str);
    }
    
    /**
     * Internal hashmap to hold the Scheme
     */
    protected HashMap<String, Scheme> table_;
    

    
    

    
    
    
    
    

}
