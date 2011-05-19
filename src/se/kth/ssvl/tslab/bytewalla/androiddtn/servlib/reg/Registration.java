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

import java.io.Serializable;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.Bundle;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleDaemon;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleInfoCache;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundlePayload;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundlePayload.location_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.RegistrationExpiredEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointIDPattern;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.VirtualTimerTask;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.List;
import android.util.Log;

/**
 * Registration class represents "registration' application that includes 
 * mechanisms to consume bundles.
 * 
 * Registration state is stored in the database while Registration is stored in the RegisrationTable.
 * 
 * @author Sharjeel Ahmed (sharjeel@kth.se)
 */


public abstract class Registration implements Serializable{

	/**
	 * SerialVersionID to Support Serializable.
	 */
	private static final long serialVersionUID = -3930115048521087734L;
	
	/**
	 * TAG for Android Logging
	 */

	private static final String TAG = "Registration";
	
	/**
	 * Administration Registrations ID of Admin Registration 
	 */
	final static int ADMIN_REGID = 0;

	/**
	 * Administration Registrations ID of Link State Router 
	 */
	final static int LINKSTATEROUTER_REGID = 1;

	/**
	 * Administration Registrations ID of Ping Registration 
	 */
	final static int PING_REGID = 2;

	/**
	 * Administration Registrations ID of External Router Registration 
	 */
	final static int EXTERNALROUTER_REGID = 3;
	/**
	 * Administration Registrations ID of DTLSR Registration 
	 */	
    final static int DTLSR_REGID = 4;

    /* prophet */
    protected final static int PROPHET_REGID = 5;
    
	/**
	 * Number of Max reserved id for Admin Registrations 
	 */	
    public final static int MAX_RESERVED_REGID = 10;
    
	/**
	 * List if BundlePayload 
	 */	
	private List<BundlePayload> api_temp_payload_list_;

	
	/**
     * Enum options for how to handle bundles when not connected.
     */
    public enum failure_action_t 
    {
    	DROP(0), 		// Drop bundles
    	DEFER(1),		// Spool bundles until requested
    	EXEC(2);		// Execute the specified callback procedure

        private static final Map<Integer,failure_action_t> lookup 
             = new HashMap<Integer,failure_action_t>();

        static {
             for(failure_action_t s : EnumSet.allOf(failure_action_t.class))
                  lookup.put(s.getCode(), s);
        }

        private int code;

        private failure_action_t(int code) {
             this.code = code;
        }

        public int getCode() { return code; }

        public static failure_action_t get(int code) { 
             return lookup.get(code); 
        }
    }

    
    /**
     * Get a string representation of enum failure_action_t.
     */
    final public static String failure_action_toa(failure_action_t action){
    	
        switch(action) {
	        case DROP:  return "DROP";
	        case DEFER:	return "DEFER";
	        case EXEC:  return "EXEC";
        }
		return null;
    };

    /**
     * Constructor to initialize Registration
     * @param regid Registration id of new Registration
     * @param endpoint EndpoidID of destination 
     * @param failure_action Action to perform DROP, DEFER, EXEC
     * @param session_flags Session flags if any else 0
     * @param expiration Set Expiration time of registration, -1 for unlimited time
     * @param script ""  
     *  
     */
    
    public Registration(int regid, final EndpointIDPattern endpoint,
    		failure_action_t failure_action,
            int session_flags,
            int expiration,
            final String script){
    	
	        regid_ = regid;
	        endpoint_ = endpoint;
	        failure_action_ = failure_action;
	        session_flags_ = session_flags;
	        script_ = script;
	        expiration_ = expiration;
	        expiration_timer_ = null;
	        active_ = false;
	        expired_ = false;
	        delivery_cache_ = new BundleInfoCache(1024);
	        
	        init_expiration_timer();
	        api_temp_payload_list_ = new List<BundlePayload>();
    }
    
    /**
     * Abstract hook for subclasses to deliver a bundle to this registration.
     * @param bundle Bundle to deliver
     */
    
    public abstract void deliver_bundle(Bundle bundle);

    /**
     *  Function to delete previously created file when migrate to api temp
     */
    public void free_payload()
    {
    	Iterator<BundlePayload>  itr = api_temp_payload_list_.iterator();
    	while (itr.hasNext())
    	{
    		BundlePayload payload = itr.next();
    		if (payload.location() == location_t.DISK)
    		payload.file().delete();
    	}
    	
    }
    
    /**
     * Deliver the bundle if it isn't a duplicate.
     */
    public boolean deliver_if_not_duplicate(Bundle bundle){
        if (! delivery_cache_.add_entry(bundle, EndpointID.NULL_EID())) {
            Log.e(TAG, String.format("suppressing duplicate delivery of bundle %s", bundle.bundleid()));
            
            return false;
        }
       // because the Bundle Daemon logic will tell Bundle Store to remove the content of the bundle
        // in order the API layer still able to use the data storing in DISK
        // the file have to be migrate
        if (bundle.payload().location() == location_t.DISK)
        {
        	bundle.payload().move_data_to_api_temp_folder();
        }
        
        Log.d(TAG, String.format("delivering bundle %s", bundle.bundleid()));
        deliver_bundle(bundle);
        
        
        return true;
    }
    
    //@{
    /// Accessors
    
    /**
     * Get the id of Registration
     * @return Registration id of current regisration.
     */
    public final int durable_key() { 
    	return regid_; 
    }

    /**
     * Get the id of Registration
     * @return Registration id of current registration.
     */
    
    final public int regid() { 
    	return regid_; 
    }
    
    /**
     * Test function to set the id of Registration
     * @param regid New Registration id
     */
    
    final public void test_set_regid(int regid) { 
    	regid_ = regid; 
    }


    /**
     * Getter function of endpoint_
     * @return Return endpoint_
     */
    
    public final EndpointIDPattern endpoint(){ 
    	return endpoint_; 
    } 

    /**
     * Getter function of failure_action_
     * @return Return failure_action_
     */

    public failure_action_t failure_action() {
    	return failure_action_;
    }
    
    /**
     * Getter function of session_flags
     * @return Return session_flags_
     */
    final public int session_flags() { 
    	return session_flags_; 
    }

    /**
     * Getter function of scrip_
     * @return Return script_
     */

    final public String script() { 
    	return script_;
    }
    

    /**
     * Getter function of expiration_
     * @return Return expiration_
     */
    final public int expiration() { 
    	return expiration_; 
    }

    /**
     * Current status of registration
     * @return True if registration is active else false 
     */
    
    final public boolean active() { 
    	return active_; 
    }
    
    /**
     * Current expiration status of registration
     * @return True if registration has expired else false 
     */

    final public boolean expired() { 
    	return expired_; 
    }

    /**
     * Set registration status
     * @param a current status of registration  
     */

    public void set_active(boolean a)  { 
    	active_ = a; 
    }

    /**
     * Set registration expiration status
     * @param e current status of registration  
     */

    public void set_expired(boolean e) { 
    	expired_ = e; 
    }

    /**
     * Force registration to expire and call unregister on bound registration. 
     */
    public void force_expire(){
        assert(active_)
        :TAG+": force_expire() Already inactive";
        
        cleanup_expiration_timer();
        set_expired(true);
    }

    /**
     * Protected class to handle automatic expiration of the registerations. 
     */

    protected static class ExpirationTimer extends VirtualTimerTask implements Serializable{

		private static final long serialVersionUID = -5622142083769634464L;

		public ExpirationTimer(Registration reg){
    		reg_ = reg;
    	}
        Registration reg_;

		
	    /**
	     * If registration has expired then forward the registration to BundleDaemon
	     * to handle it.  
	     */

		@Override
		protected void timeout(Date now) {
			
			reg_.set_expired(true);
	        
	        if (! reg_.active()) {
	            BundleDaemon.getInstance().post(new RegistrationExpiredEvent(reg_));
	        }			
		}

		
    }
    
    /**
     * Initiate expiration timer 
     */

    protected void init_expiration_timer(){
    	if(expiration_!=0){
        	expiration_timer_ = new ExpirationTimer(this);
        	expiration_timer_.schedule_in(expiration_);
	    } else {
	        set_expired(true);
	    }
    };
    
    /**
     * Protected class to handle automatic expiration of the registerations. 
     */

    /**
     * Clean up expiration time and set to null 
     */

    protected void cleanup_expiration_timer(){
        
        if (expiration_timer_!=null) {
        	expiration_timer_.cancel();
            boolean pending = expiration_timer_.cancelled();
            
            if (!pending) {
                assert(expired_);
            }
            
            expiration_timer_ = null;
        }

    }
    
    /**
     *  Int to store Registration id
     */
    protected int regid_;

    /**
     *  EndpointId to store the endpointid of destination.
     */

    protected EndpointIDPattern endpoint_;
    
    /**
     *  failure action an enum object to set different options 
     */

    protected failure_action_t failure_action_;
    
    /**
     *  Int to store session flags
     */

    protected int session_flags_;	

    /**
     *  Script to run
     */

    protected String script_;
    
    /**
     *  Expiration time of registration
     */

    protected int expiration_;
    
    /**
     *  Expiration Timer object
     */

    protected ExpirationTimer expiration_timer_;
    
    /**
     *  Registration status
     */
    protected boolean active_;    

    /**
     *  Registration status if its bound or not
     */
    protected boolean bound_;    
    /**
     *  Registration status if its already expired or not
     */

    protected boolean expired_;
    
    /**
     *  BundleInfoCache object
     */
    
    protected BundleInfoCache delivery_cache_;
    
}

