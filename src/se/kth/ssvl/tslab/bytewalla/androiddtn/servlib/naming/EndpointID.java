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
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNEndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;
import android.util.Log;


/**
 * This class represents EndpointID in the DTNSystem. It consists of unique URI depending on the naming scheme used.
 * 
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class EndpointID implements Serializable {

	/**
	 * TAG for Android Logging mechanism
	 */
	private static final String TAG = "EndpointID";
	/**
	 * Unique identifier according to Java Serializable specification
	 */
	private static final long serialVersionUID = -3930115048521087734L;

	/**
	 * A static validator used for validating Input URI
	 */
	private static EndpointID validator_ = new EndpointID();
	
	/**
	 * Endpoint ID URI 
	 */
	protected URI uri_; 
	
	/**
	 * the scheme class (if known) 
	 */
	protected Scheme scheme_; 

	/**
	 * true iff the endpoint id is valid
	 */
	protected boolean valid_ = false;
	
	/**
	 * true iff this is an EndpointIDPattern
	 */
	protected boolean is_pattern_; 

	
	/**
	 * Default constructor
	 */
	public EndpointID() {
		scheme_ = null;
		valid_ = false;
		is_pattern_ = false;
	}

	/**
	 * This basically is the URI String representation
	 * @return String representation of URI.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return uri_.toString();
	}
	
	
	
	/**
	 * Construct the endpoint id from the given string.
	 * 
	 * @param uri an absolute URI representation for this new EndpointID
	 */
	public EndpointID(String uri) {
		
		uri_ = URI.create(uri);
		scheme_ = null;
		valid_ = false;
		is_pattern_ = false;
		validate();
	}

	/**
	 * Construct the endpoint id from another.
	 * @param other The source EndpointID to create from
	 */
	public EndpointID(EndpointID other) {
		if (other != this)
		{
			assign(other);
			validate();
		}
	}
	
	/**
	 * Assign this endpoint ID as a copy of the other.
	 * @param other The source EndpointID to assign the value from
	 * @return whether the assign is successful ( it's not an invalid URI, for example )
	 */
	public boolean assign(EndpointID other) {
		uri_        = other.uri_;
		scheme_     = other.scheme_;
		valid_      = other.valid_;
		is_pattern_ = other.is_pattern_;
		return validate();
	}
	
	
	/**
	 * Assign this endpoint ID from a byte array
	 */
	public boolean assign(byte[] array) {
		
		try {
			return assign(new String(array, "US-ASCII"));
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "EndpointID:assign, unsupportedEncoding Exception");
			return false;
		}
	}
	
	/**
	 * Assign EndpointID based on ByteBuffer contents and length
	 * The position of the input buffer will not change from this method
	 * 
	 * 
	 */
	public boolean assign(IByteBuffer buf, int len) {
		buf.mark();
		try
		{
			byte[] temp = new byte[len];
			buf.get(temp);
			
			
			try {
				return assign(new String(temp,"US-ASCII"));
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "EndpointID:assign unsupportedEncoding Exception");
				return false;
			}
		}
		finally
		{
			buf.reset();
		}
	}

	/**
	 * Getter function for the byte array representation of this EndpointID
	 * @return byte array representation of this EndpointID
	 */
	public byte[] byte_array() {
		return uri_.toASCIIString().getBytes();
	}

	/**
	 * Set the string and validate it.
	 * @param  str  The source String to assign to this EndpointID
	 * @return true if the string is a valid id, false if not.
	 */
	public boolean assign(String str) {

		uri_ =  URI.create(str);
		return validate();
	}

	/**
	 * Set the string and validate it.
	 * @param scheme The input scheme to be set to this EndpointID
	 * @param ssp The input schedme specific part to be set to this EndpointID
	 * @return true if the string is a valid id, false if not.
	 */
	public boolean assign(String scheme, String ssp) {
		assign(scheme + ":" + ssp);
		return validate();
	}

	/**
	 *  Override from equals of object to check whether the EndpointID is the same or not
	 *  This basically checks the URI of both the EndpointID
	 *  
	 *  @return other The EndpointID to be compared with
	 */
	public boolean equals(EndpointID other)
	{
		return is_equal(other);
	}
	
	/**
	 *  Internal is_equal function
	 */
	private final boolean is_equal(final EndpointID other) {
		return uri_.equals(other.uri_);
	}

	
	/**
	 * Operator overload for comparison of EndpointID
	 */
	public final boolean is_less_than(final EndpointID other) {
		return uri_.compareTo(other.uri_) < 0;
	}
	/**
	 * function to check whether this EndpointID is considered Null or not
	 * It will be either the URI is null, it's equal to NULL EndpoindID( this depends on the naming Scheme, for example, in DTN Scheme, it is "dtn:none" ), or 
	 * the EndpointID String is ""
	 * @return the result whether it is NULL or not
	 */
	public final boolean is_null()
	{
		return this.uri() == null || this.equals(EndpointID.NULL_EID()) || 
				this.toString().equals("");
	}
	/**
	 * Three way lexographical comparison
	 */
	public final int compare(EndpointID other) {
		return uri_.compareTo(other.uri_);
	}

	/**
	 * Set the string from the API type dtn_endpoint_id_t
	 * 
	 * @return true if the string is a valid id, false if not.
	 */
	 public boolean assign( DTNEndpointID eid) {
	 
		 uri_  = URI.create(eid.uri());
		 return validate();
	 }

	/**
	 * function to check whether the given EndpointID is contained within this EndpointID; otherwise false.
	 * @param  other The input EndpointID to check whether it is subsumed or not
	 * @return subsume result
	 */
	public final boolean subsume(EndpointID other) {
		return uri_.toASCIIString().contains(other.uri().toASCIIString());
	}

	/**
	 * Append the specified service tag (in a scheme-specific manner) to the
	 * ssp.
	 * 
	 * @return true if successful, false if the scheme doesn't support service
	 *         tags
	 */
	public boolean append_service_tag(final String tag) {
		 if (scheme_ == null)
		        return false;

		 try
		 {
		  uri_ = scheme_.append_service_tag(uri_, tag);
		 }
		 catch (InvalidURIException e)
		 {
			 return false;
		 }
		 
		    // rebuild the string
		    if (!validate()) {
		        Log.e(TAG,
		                  "EndpointID::append_service_tag: " +
		                  "failed to format appended URI");
		        return false;
		    }

		    return true;
		
		
	}

	/**
	 * Append a wildcard (in a scheme-specific manner) to form a route pattern.
	 * 
	 * @return true if successful, false if the scheme doesn't support wildcards
	 */
	public boolean append_service_wildcard() {

		if (uri_.equals(DTNScheme.NULL_EID().toString())) return false;
		
		if (scheme_ == null) return false;
		
		try {
			uri_ = scheme_.append_service_wildcard(uri_);
		} catch (InvalidURIException e) {
			  Log.e(TAG,
	                  "EndpointID::append_service_wildcard: " +
	                  "invalid URL Exception");
			return false;
		}
		
		  if (uri_== null)
		        return false;

		// rebuild the string
		if (!validate())
		{
			Log.e(TAG, "EndpointID::append_service_wildcard: failed to format appended URI");
			return false;
		}
		
		return true;
	}

	/**
	 * Reduce EndpointID to routing endpoint
	 * 
	 * @return true if eid is set to node_id, false otherwise
	 */
	public boolean remove_service_tag() {
		 if (scheme_ == null)
		        return false;

		    try {
				uri_ = scheme_.remove_service_tag(uri_);
			} catch (InvalidURIException e) {
			    Log.e(TAG,
		                  "EndpointID::remove_service_tag: " +
		                  "invalid URL Exception");
				return false;
			}

		  if (uri_== null)
			        return false;

		    // rebuild the string
		    if (!validate()) {
		        Log.e(TAG,
		                  "EndpointID::remove_service_tag: " +
		                  "failed to format reduced URI");
		        
		        // see note in append_service_wildcard() ... :(
		        return false;
		    }
		return true;
	}

	/**
	 * Typedef for the return value possibilities from is_singleton.
	 */
	public enum singleton_info_t {
		UNKNOWN, SINGLETON, MULTINODE
	}

	/**
	 * Return whether or not this endpoint id is a singleton or a multi-node
	 * endpoint.
	 */
	public final singleton_info_t is_singleton() {
		if (!known_scheme())
		{
			Log.w(TAG, "returning is_singleton_default= " + is_singleton_default_ + 
					 " for unknown scheme");
		}
		return scheme_.is_singleton(uri_);
	}

	/**
	 * Default setting for endpoint ids in unknown schemes.
	 */
	public static singleton_info_t is_singleton_default_ = singleton_info_t.SINGLETON;

	

	/**
	 * Return an indication of whether or not the scheme is known.
	 */
	public final boolean known_scheme() {
		return scheme_ != null;
	}

	/**
	 * Return the special endpoint id used for the null endpoint, namely
	 * Support only DTN Scheme for now
	 * "dtn:none".
	 */
	public final static EndpointID NULL_EID() {
		
		return DTNScheme.NULL_EID();

	}

	/**
	 * The scheme and SSP parts each must not exceed this length.
	 */
	public final static int MAX_EID_PART_LENGTH = 1023;

	/**
	 * Virtual from SerializableObject
	 */
	// virtual void serialize(oasys::SerializeAction* a);

	public final URI uri() {
		return uri_;

	}

	/**
	 * Getter function for the String representation fo this EndpointID
	 * @return the String representation fo this EndpointID
	 */
	public final String str() {
		if(uri_!=null)
			return uri_.toString();
		
		return null;
	}

	/**
	 * Getter function for the Scheme part of this URI
	 * @return the Scheme part of this URI
	 */
	public final String scheme_str() {
		return uri_.getScheme();
	}

	/**
	 * Getter function for the Scheme specific part of this URI
	 * @return the Scheme specific part of this URI
	 */
	public final String ssp() {
		return uri_.getSchemeSpecificPart();
	}

	
	/**
	 * Getter function for the Scheme object of this URI
	 * @return the Scheme object of this URI
	 */
	
	public final Scheme scheme() {
		return scheme_;
	}

	/**
	 * Getter function for whether  this URI is valid from the validation process
	 * @return whether this URI is valid from the validation process
	 */
	public final boolean valid() {

		return valid_;
	}

	/**
	 *  Getter function for whether this EndpointID is an instance of EndpointIDPattern
	 * @return whether this EndpointID is an instance of EndpointIDPattern
	 */
	public final boolean is_pattern() {
		return is_pattern_;
	}

	/**
	 * Getter function for  the length of URI representation
	 * @return the length of URI representation
	 */
	public final int length() {
		return uri_.toString().length();
	}

	/**
	 * Check whether uri is valid by try creating the URI object
	 * @param str input URI as String
	 * @return whether uri is valid by try creating the URI object
	 */
	public static boolean is_able_to_create_URI(String str)
	{
		
		try {
			new URI(str);
		} catch (URISyntaxException e) {
			return false;
		}
		return true;
		
	}
	
	/**
	 * 
	 * Getter function for whether the Input String is a valid URI
	 * @param str Input String to validate
	 * @return whether the Input String is a valid URI
	 */
	public static boolean is_valid_URI(String str)
	{
		validator_.assign(str);
		return validator_.valid_;
	}
	
	/**
	 * Internal function to validate URI. This function is used by other operations to validate the URI
	 * @return whether the URI is valid from several processes.
	 */
	protected boolean validate() {
		
		scheme_ = null;
		valid_  = false;
		try{
		
		
		if (!is_able_to_create_URI(uri_.toString()))
		{
			Log.d(TAG,"EndpointID::validate: invalid URI");
			return false;
		}
		
		
		
		if (scheme_str().length() > MAX_EID_PART_LENGTH) {
	        Log.e(TAG , "scheme name is too large > " +  MAX_EID_PART_LENGTH);
	        valid_ = false;
	        return false;
	    }
	    
	    if (ssp().length() > MAX_EID_PART_LENGTH) {
	    	Log.e(TAG, "ssp is too large (>" +  MAX_EID_PART_LENGTH);
	        valid_ = false;
	        return false;
	    }

	    valid_ = true;

	    scheme_ = SchemeTable.getInstance().lookup(uri_.getScheme());
	    if (scheme_ != null) {
	        valid_ = scheme_.validate(uri_, is_pattern_);
	    }
	    else valid_ = false;
	     
	  

		}
		catch (NullPointerException e)
		{
		   Log.e(TAG, "EndpointID:validate() fail");
		}
	    return valid_;
	}


}
