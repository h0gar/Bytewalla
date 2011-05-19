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

import java.io.Serializable;
import java.net.URI;
import java.util.regex.Pattern;

import android.util.Log;

/**
 * This is the default scheme for the DTN network.
 * The pattern for the scheme is "dtn://{host}/{application_tag}"
 * The NULL EID for this scheme is "dtn:none"
 * 
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */

public class DTNScheme extends Scheme implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2671779767857883030L;

	/**
	 * The TAG name for Android Logging mechanism
	 */
	private String TAG = "DTNScheme";

	/**
	 *  schemeText used internally
	 */
	private static String schemeText = "dtn";
	
	/**
	 * Part of the Singleton implementation 
	 */
	private static DTNScheme instance_ = null;

	/**
	 * NULL EID Text Representation
	 */
	private static final String NullEIDText = "dtn:none";
	
	/**
	 * Wildcard EID Text Representation
	 */
	private static final String WildcardEIDText = "dtn://*";
	
	/**
	 * Variable to hold singleton NULL EndpointID
	 */
	private static EndpointID null_eid;
	
	/**
	 * Variable to hold singleton Wildcard EndpointID
	 */
	private static EndpointIDPattern wildcard_eid;
	
	/**
	 * Private constructor
	 */
	private DTNScheme() {
		// Exists only to defeat instantiation
	}

	static {
		// Right now only support DTN Scheme
		null_eid = new EndpointID(NullEIDText);
		
		wildcard_eid = new EndpointIDPattern(WildcardEIDText);
	}
	
	/**
	 * Singleton implementation of DTNScheme
	 * @return the instance of DTNScheme
	 */
	public static DTNScheme getInstance() {
		if (instance_ == null) {
			instance_ = new DTNScheme();
		}
		return instance_;
	}

	/**
	 * Getter function for the NULL Endpoint ID of this DTNScheme
	 * @return The NULL EndpointID
	 */
	public static EndpointID NULL_EID()
	{
		return null_eid;
	}
	
	/**
     * Return the special wildcard Endpoint ID. This functionality is
     * not in the bundle spec, but is used internally to this
     * implementation.
     */
    public final static EndpointIDPattern WILDCARD_EID()
    {
    	
    	return wildcard_eid;
    	
    }
	
	// End Singleton Implementation of the DTNScheme

	/**
	 * Validate that the SSP in the given URI is legitimate for this scheme. If
	 * the 'is_pattern' paraemeter is true, then the ssp is being validated as
	 * an EndpointIDPattern.
	 * 
	 * @return true if valid
	 */
	@Override
	public boolean validate(URI uri, boolean is_pattern) {
		if (uri.toString().equals(NullEIDText) || uri.toString().equals(WildcardEIDText))
		{
			return true;
		}
		
		if ( is_pattern ) return true;
		
		
		if (uri.getHost() == null ) return false;
		
		if (!uri.getScheme().equals(schemeText) ) return false;
		
		
		
		// There shouldn't be nested path in this Scheme. 
		// For example, this is fine: dtn://endpoint.com/tag
		//            But, this is not valid: dtn://endpoint.com/tag/nested
		
		
		if (!EndpointID.is_able_to_create_URI(uri.toString()))
		{
			Log.d(TAG, "DTNScheme::validate: invalid URI");
			return false;
		}
		
		// a valid dtn scheme uri must have a host component in the
	    // authority unless it's the special "dtn:none" uri
	    if (!uri.getSchemeSpecificPart().equals("none") && uri.getHost().length() == 0 ) {
	        return false;
	    }
		return true;
	}

	/**
	 * Match the pattern to the endpoint id in a scheme-specific manner.
	 */
	@Override
	public boolean match(final EndpointIDPattern pattern, final EndpointID eid) {
		
		
		
		// sanity check
	    assert (pattern.scheme() == this): "Sanity check in match method fail";

	    // we only match endpoint ids of the same scheme
	    if (!eid.known_scheme() || (!(eid.scheme() instanceof DTNScheme))) {
	        return false;
	    }
	    
	    // if the ssp of either string is "none", then nothing should
	    // match it (ever)
	    if (pattern.ssp().equals("none") || eid.ssp().equals("none")) {
	        return false;
	    }
	    
	    // check for a wildcard host specifier e.g dtn://*
	    if (pattern.equals(DTNScheme.WILDCARD_EID()))
	    {
	        return true;
	    }
	    
	    // return Instantly when the eid and Pattern is exactly the same
	    if (pattern.equals(eid)) 
	    {
	    	return true;
	    }
	    
	    try
	    {
	    	
	    	
	    	
	       String regexPattern = pattern.uri().toString().replaceAll("\\*", "[a-z[0-9]\\/\\.]*");
	       //regexPattern = regexPattern.replaceAll("\\.", ".");
	       
	       
	       boolean matchResult =  Pattern.matches(regexPattern, eid.uri().toString());
	       return matchResult;
	    }
	    catch (Exception e)
	    {
	    	Log.d(TAG, "DTNScheme::match: Pattern Exception");
	    	return false;
	    }
	    
	   
	}

	/**
	 * Check the URI, it will return the InvalidURIException otherwise, it will do nothing
	 */
	private void check_URI(URI uri)  throws InvalidURIException
	{
        if (uri == null) throw new InvalidURIException();
		
		if (!uri.getScheme().equals(schemeText)) throw new InvalidURIException();
		
		
		
		if (!EndpointID.is_able_to_create_URI(uri.toString())) throw new InvalidURIException();
	}
	
	
	/**
	 * Append the given service tag to the uri in a scheme-specific manner. By
	 * default, the scheme is not capable of this.
	 * 
	 * @return true if this scheme is capable of service tags and the tag is a
	 *         legal one, false otherwise.
	 * @throws InvalidURIException 
	 */
	@Override
	public URI append_service_tag(URI uri, final String tag) throws InvalidURIException{
		
		check_URI(uri);
		
		
		URI uri_result;
		
		
		if (tag.charAt(0) != '/')
		{
			uri_result = URI.create(uri.getScheme() + "://" + uri.getHost() + "/" + tag);
		}
		else
		{
			uri_result = URI.create(uri.getScheme() + "://" + uri.getHost()  + tag);
		}
		
		return uri_result;
	}

	/**
	 * Append wildcard to the uri in a scheme-specific manner. The default
	 * scheme is not capable of this.
	 * 
	 * @return true if this scheme is capable of wildcards and the wildcard is
	 *         successfully appended, else false.
	 * @throws NamingException 
	 */
	@Override
	public URI append_service_wildcard(URI uri) throws InvalidURIException {

		if (uri.toString().equals(NullEIDText))throw new InvalidURIException();
		
		if (!uri.getPath().equals("")) throw new InvalidURIException();
		
		check_URI(uri);
		
		URI result_uri = URI.create(uri.getScheme() + "://" + uri.getHost()  + "/*");
		
		return result_uri;
	}

	/**
	 * Reduce URI to node ID in a scheme specific manner. The default scheme is
	 * not capable of this.
	 * 
	 * @return true if this scheme is capable of this reduction and the
	 *         reduction is successful, else false.
	 */
	@Override
	public URI remove_service_tag(URI uri)  throws InvalidURIException {
		
	  if (uri == null)  throw new InvalidURIException();
	  
  	   URI result_uri = URI.create(uri.getScheme() + "://" + uri.getHost());
		
		return result_uri;
	}

	/**
	 * Check if the given URI is a singleton endpoint id.
	 */
	@Override
	public EndpointID.singleton_info_t is_singleton(final URI uri) {
		
		if (uri.toString().contains("*"))
		{
			Log.d(TAG, "URI host contains a wildcard, so is MULTINODE");
			return EndpointID.singleton_info_t.MULTINODE;
		}
		
		Log.d(TAG, "URI host contains a wildcard, so is SINGLETON");
		return EndpointID.singleton_info_t.SINGLETON;
	}

	/**
	 * Return scheme specific String
	 */
	
	@Override
	public String scheme_str() {
		
		return schemeText;
	}

}
