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


package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.reg;

import java.util.concurrent.TimeUnit;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BlockingBundleList;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.Bundle;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleDaemon;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleDeliveredEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointIDPattern;
import android.util.Log;

/**
 * API for the Registration.  
 * @author Sharjeel Ahmed (sharjeel@kth.se)
 */


public class APIRegistration extends Registration{


	/**
	 * SerialVersionID to Support Serializable.
	 */
	private static final long serialVersionUID = 4449363325162246715L;

	/**
	 * TAG for Android Logging
	 */

	private static String TAG = "APIRegistration";
	

	/**
	 * Constructor to initialize this class.
	 * @param regid Registration id of the API Registration
	 * @param endpoint Endpoint ID of host
	 * @param action Action type 
	 * @param expiration Expiration time of the Registration
	 */

	public APIRegistration(int regid,
            final EndpointIDPattern endpoint,
            failure_action_t action,
            int expiration){
    	
		super(regid,endpoint, action, -1,expiration, "");
		
		int capacity = 20;
		boolean fair = true;
		
		bundle_list_ = new BlockingBundleList(capacity, fair);
    }
    

	/**
	 * Delivery bundle pass the bundle to BundleDaemon for delivery
	 * @param bundle Bundle to Deliver  
	 */

    @Override
	public void deliver_bundle(Bundle bundle){

        if (!active() && (failure_action_ == failure_action_t.DROP)) {
            Log.d(TAG, String.format("deliver_bundle: "
                     + " dropping bundle id %s for passive registration %s (%s)",
                     bundle.bundleid(), regid_, endpoint_.str()));
            
           
        }
        
        if (!active() && (failure_action_ == failure_action_t.EXEC)) {
            // this sure seems like a security hole, but what can you
            // do -- it's in the spec
            Log.d(TAG, String.format("deliver_bundle: running script '%s' for registration %s (%s)",
            			script_, regid_, endpoint_.str()));
        }

        Log.d(TAG, String.format("deliver_bundle: queuing bundle id %d for %s delivery to %s",
                 bundle.bundleid(),
                 active() ? "active" : "deferred",
                 endpoint_.str()));
        
        if (active())
        {
        	try
        	{
        	bundle_list_.add(bundle);
        	// post an event saying we "delivered" it
        	BundleDaemon.getInstance().post(new BundleDeliveredEvent(bundle, this));
        	}
        	catch(IllegalStateException e)
        	{
        		Log.e(TAG, "Bundle List add fail because Illegal state exception");
        	}
        }
    }
    
    

    /**
     * This will block and wait
     * other wise, put timeout in seconds 
     * This is for used in DTNAPI to get Bundle result from registration
     * @param timeout Wait for the time before timeout, if timeout equal -1 it means wait indefinite
     * @return Next Bundle from the waiting list.
     * @exception Throws  InterruptedException
     */

    public Bundle wait_for_Bundle(int timeout) throws InterruptedException{
    	if (timeout == -1)   	
    	return bundle_list_.take();
    	
    	else
    	{	
    		return bundle_list_.poll(timeout, TimeUnit.SECONDS);
    		
    	}
    }
    
    /**
     * Getter for the bundle list queue
     */
    public BlockingBundleList bundle_list() { 
    	return bundle_list_; 
    }

    /**
     * BlockingBundleList object to store the bundle list
     */
    
    protected BlockingBundleList bundle_list_;
}
