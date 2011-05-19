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

import java.net.URI;



/**
 *  The base class for naming Scheme in DTN
 *  @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public abstract class Scheme {

	/**
     * Validate that the SSP within the given URI is legitimate for
     * this scheme. If the 'is_pattern' paraemeter is true, then the
     * ssp is being validated as an EndpointIDPattern.
     *
     * @return true if valid
     */
    public abstract boolean validate(URI uri, boolean is_pattern);
    

    /**
     * Match the pattern to the endpoint id in a scheme-specific
     * manner.
     */
    public abstract boolean match(final EndpointIDPattern pattern, final EndpointID eid);
    
    /**
     * Append the given service tag to the uri in a scheme-specific
     * manner. By default, the scheme is not capable of this.
     *
     * @return true if this scheme is capable of service tags and the
     * tag is a legal one, false otherwise.
     */
    public URI append_service_tag (URI uri, final String tag) throws InvalidURIException
    {
       return null;
    }
    
    /**
     * Get Scheme String representation String
     */
    abstract public String scheme_str();
    

    /**
     * Append wildcard to the uri in a scheme-specific manner. The
     * default scheme is not capable of this.
     *
     * @return The result if this scheme is capable of wildcards and the
     * wildcard is successfully appended, else exception will be null will be returned.
     * @throws NamingException 
     */
    public URI append_service_wildcard(URI uri) throws InvalidURIException
    {
    	return null;
    	
    }
    
    
    /**
     * Reduce URI to node ID in a scheme specific manner. The default
     * scheme is not capable of this.
     *
     * @return URI result objected if this scheme is capable of this reduction and 
     * the reduction is successful, else null object will be returned.
     */
    public URI remove_service_tag(URI uri) throws InvalidURIException
    {
      return null;
    }
    
    /**
     * Check if the given URI is a singleton endpoint id.
     */
    public abstract EndpointID.singleton_info_t is_singleton(final URI uri);
    
    
    
    
    

}
