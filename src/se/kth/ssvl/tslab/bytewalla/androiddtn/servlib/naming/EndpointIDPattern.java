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

import android.util.Log;

/**
 * Class to represent group of EndpointID. For example, dtn://host1.dtn/*, represents all Endpoint ID from dtn://host1.dtn.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class EndpointIDPattern extends EndpointID {

	/**
	 * Unique identifier according to Java Serializable specification
	 */
	private static final long serialVersionUID = -4212572941546886080L;
	
	/**
	 * TAG string for supporting Android Logging mechanism
	 */
	private String TAG   = "EndpointIDPattern";

	/**
     * Default constructor
     */
    public EndpointIDPattern() 
    {
    	super();
    	is_pattern_ = true;
    	
    }
    
    /**
     * Construct the endpoint id pattern from the given string.
     */
    public EndpointIDPattern(final String str)
    {
    	super(str);
    	is_pattern_ = true;
    	
    }
    
    /**
     * Construct the endpoint id pattern from another.
     */
    public EndpointIDPattern(final EndpointIDPattern other)
    {
    	super(other);
        is_pattern_ = true;
      
    }
    
    /**
     * Getter function for Wildcard EndpointID
     * @return Wildcard Endpoint ID
     */
    public static EndpointIDPattern WILDCARD_EID()
    {
    	return DTNScheme.WILDCARD_EID();
    }
    
    /**
     * Construct the endpoint id pattern from another that is not
     * necessarily a pattern.
     */
    public EndpointIDPattern(final EndpointID other)
    {
    	super(other);
    	is_pattern_ = true;
    }
    
    /**
     * Shortcut to the matching functionality implemented by the
     * scheme.
     */
    public final boolean match(final String eid)
    {
    	return match(new EndpointID(eid));
    }
    
    public final boolean match(final EndpointID eid)
    {
    	if (eid != null)
    	{

    		if (eid.uri() == null)
    		{
    			Log.e(TAG, "eid is not null but eid.uri() is null ");
    			return false;
    		}


    		// only match valid eids
    		if (!EndpointID.is_valid_URI(eid.uri().toString())) {
    			Log.w(TAG,
    					"match error: eid " + uri_.toString()+ " not a valid uri");
    			return false;
    		}

    		if (known_scheme()) 
    			if( scheme().match(this, eid) )
    				return true;

    		if(this.equals(eid) )
    			return true;

    		return eid.str().trim().startsWith(this.toString().trim().replaceAll("\\*", ""));
    	}
    	else
    	{
    		Log.e(TAG, "eid is not null but eid.uri() is null ");
    		return false;
    	}
    }

    
    
    
	
}
