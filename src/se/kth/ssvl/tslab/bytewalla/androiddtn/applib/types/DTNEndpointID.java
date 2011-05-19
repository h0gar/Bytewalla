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
package se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types;


/**
 * This class represents DTNEndpointID to be used by the Application
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public class DTNEndpointID  {
    
	
	/**
	 * Empty Constructor
	 */
	public DTNEndpointID()
	{
	
	}
	/**
	 * Constructor with input URI
	 * @param uri input URI for this EndpointID
	 */
	public DTNEndpointID(String uri)
	{
		uri_ = uri;
	}
	
	/**
	 * Internal variable for holding URI
	 */
	private String uri_;

	/**
	 * @return the uri_
	 */
	public String uri() {
		return uri_;
	}

	/**
	 * @param uri the uri_ to set
	 */
	public void set_uri(String uri) {
		uri_ = uri;
	}
	
	@Override
	public String toString()
	{
		return uri_;
	}
}
