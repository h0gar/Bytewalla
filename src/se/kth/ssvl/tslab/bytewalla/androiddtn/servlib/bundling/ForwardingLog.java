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
import java.util.ArrayList;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.ForwardingInfo.state_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointIDPattern;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.reg.Registration;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.Lock;
import android.util.Log;

/**
 * Class to keep the log of where and when the bundles have been forward.
 * This log is used to determine if bundle needs to transmit to same 
 * next hope twice or not.
 *  
 * Although a bundle can be sent over multiple links, and may even be
 * sent over the same link multiple times, the forwarding logic
 * assumes that for a given link and bundle, there is only one active
 * transmission. Thus the accessors below always return / update the
 * last entry in the log for a given link.
 * 
 *  @author Sharjeel Ahmed (sharjeel@kth.se) 
 */

public class ForwardingLog extends ArrayList<ForwardingInfo> implements Serializable{

	/**
	 * SerialVersionID to Support Serializable.
	 */
	private static final long serialVersionUID = 5863005198370542402L;
	
    /**
     * Default constructor.
     */
	private static final String TAG = "ForwardingLog";
	
	public ForwardingLog(Lock lock){
		lock_ = lock;
		log_ = new ArrayList<ForwardingInfo>();
	}

    /**
     * Get the most recent entry for the given link from the log.
     * @param link Find the entry of this given link name
     * @param found Pass a blank boolean array. If found an entry then set first index
     * of the boolean array to true.
     * @return Return ForwardingInfo If found in list else null.
     */
    final public ForwardingInfo get_latest_entry(final Link link, boolean[] found){
    	lock_.lock();
    	try{
    	    for (int i = log_.size()-1; i>=0; --i)
    	    {
    	        if (log_.get(i).link_name() == link.name_str()){
    	            assert(log_.get(i).remote_eid() == EndpointID.NULL_EID() ||
    	            		log_.get(i).remote_eid() == link.remote_eid())
    	            		: "ForwardingLog:get_latest_entry_link, remote_eid is null";
    	            
    	            found[0] = true;
    	        	return log_.get(i);
    	        }
    	    }
    	        	
    	}finally{
    		lock_.unlock();
    	}
    	
        found[0] = false;
    	return null;
    }

    /**
     * Get the most recent state for the given link from the log.
     * @param link Find the state of this given link
     * @return Return state If found in list else null.

     */
    final public state_t get_latest_entry(final Link link){
    	
    	boolean found[] = new boolean[1];
    	
    	ForwardingInfo info = get_latest_entry(link, found);
    	
        if (!found[0]) {
            return state_t.NONE;
        }

        return info.state();
    }
    
    /**
     * Get the most recent entry for the given registration from the log.
     * @param reg Find the entry for this given registration
     * @param found Pass a blank boolean array. If found an entry then set first index
     * of the boolean array to true.
     * @return Return ForwardingInfo If found in list else null.

     */
    final public ForwardingInfo get_latest_entry(final Registration reg, boolean[] found){
    	lock_.lock();
    	try{
    	    for (int i = log_.size()-1; i>=0; --i)
    	    {
    	        if (log_.get(i).regid() == reg.regid())
    	        {
    	            // This assertion holds as long as the mapping of
    	            // registration id to registration eid is persistent,
    	            // which will need to be revisited once the forwarding log
    	            // is serialized to disk.
    	            assert(log_.get(i).remote_eid() == EndpointID.NULL_EID() ||
    	            		log_.get(i).remote_eid() == reg.endpoint())
    	            		: "ForwardingLog:get_latest_entry_registration, remote_eid is null";;
    	    	    found[0] = true;
    	    	    return log_.get(i);
    	        }
    	    }    		
		}finally{
			lock_.unlock();
		}
    	return null;
    }
    
    /**
     * Get the most recent state for the given registration from the log.
     * @param reg Find the State for this given reg
     * @param found Pass a blank boolean array. If found the state then set first index
     * of the boolean array to true.
     * @return Return state If found in list else null.
     */
    final public state_t get_latest_entry_state(final Registration reg){
    	boolean found[] = new boolean[1];
    	
    	ForwardingInfo info = get_latest_entry(reg, found);
        if (!found[0]) {
            return state_t.NONE;
        }

        return info.state();
    }
    
    /**
     * Get the most recent entry for the given state from the log.
     * @param state Find the entry for this given state
     * @param found Pass a blank boolean array. If found an entry then set first index
     * of the boolean array to true.
     * @return Return ForwardingInfo If found in list else null.
     */
    final public ForwardingInfo get_latest_entry(state_t state, boolean[] found){
    	
    	lock_.lock();
    	try{
    	    for (int i = log_.size()-1; i>=0; --i)
    	    {
    	        if (log_.get(i).state() == state){
    	    	    found[0] = true;
    	    	    return log_.get(i);
    	        }
    		}   	
    		
		}finally{
			lock_.unlock();
		}
    	
	    found[0] = false;
		return null;
    }
    
    
	/**
	 * Test function to get the lock     
	 * @return Return the lock object
	 */
    final public Lock test_get_lock()
    {
    	return lock_;
    }
    
    /**
     * Return the count of matching entries. The states and actions
     * parameters should contain a concatenation of the requested
     * states/actions to filter the count.
     * @param states Concatenation of all the requested states
     * @param actions Concatenation of all the requested actions
     * @return Count of total ForwardingInfos where both state and action match
     * 
     */
    final public int get_count(int states ,int actions){
    	int count = 0;
    	
    	lock_.lock();
    	
    	try{
    		Log.d(TAG, "Log size: "+ log_.size());
    		
    	    for (int i = 0; i<log_.size(); i++)
    	    {
    	    	
    	        if ((log_.get(i).state().getCode() & states)!=0 && (log_.get(i).action().getCode() & actions)!=0){
    	        	count++;
    	        }
    	    }   
    	}finally{
    		lock_.unlock();
    	}
		return count;
    }

    /**
     * Return the count of matching entries for the given remote
     * endpoint id. The states and actions parameters should contain a
     * concatenation of the requested states/actions to filter the
     * count.
     * @param eid Endpoint id to match
     * @param states Concatenation of all the requested states
     * @param actions Concatenation of all the requested actions
     * @return Count of total ForwardingInfos where eid, state and action match
     */
    final public int get_count(final EndpointID eid,int states,int actions){

    	int count = 0;
    	
    	lock_.lock();
    	ForwardingInfo info;
    	try{
    	    for (int i = 0; i<log_.size(); i++)
    	    {
    	    	info = log_.get(i);
    	        if ((info.remote_eid() == EndpointIDPattern.WILDCARD_EID() ||
    	                info.remote_eid().equals(eid))  && (info.state().getCode() & states)!=0 
    	        		&& (info.action().getCode() & actions)!=0){
    	        	count++;
    	        }
    	    }   
    	}finally{
    		lock_.unlock();
    	}
		return count;
    }

    /**
     * Add a new forwarding info entry for the given link.
     * @param link Name of the link for ForwardingInfo 
     * @param action Type of action
     * @param state Type of state
     * @param custody_timer CustodyTimer for ForwardingInfo
     */
    public void add_entry(final Link link, ForwardingInfo.action_t action,
                   state_t state,
                   final CustodyTimerSpec custody_timer){
    	
    	lock_.lock();

    	try{
    	    log_.add(new ForwardingInfo(state, action, link.name_str(), 0xffffffff,
                    link.remote_eid(), custody_timer));
    	}finally{
    		lock_.unlock();
    		
    	}
    }
    
    /**
     * Add a new forwarding info entry for the given link using the
     * default custody timer info. Used for states other than
     * TRANSMITTED for which the custody timer is irrelevant.
     * @param link Name of the link for ForwardingInfo 
     * @param action Type of action
     * @param state Type of state
     */

    public void add_entry(final Link link, ForwardingInfo.action_t action,
                   state_t state){
        CustodyTimerSpec default_spec = CustodyTimerSpec.getDefaultInstance();
        add_entry(link, action, state, default_spec);
    }
    
    /**
     * Add a new forwarding info entry for the given registration.
     * @param reg Registration object to get the registration id for ForwardingInfo
     * @param action Action type for ForwardingInfo
     * @param state State type for ForwardingInfo
     */
    public void add_entry(final Registration reg, ForwardingInfo.action_t action,
                   state_t state){
    	
    	lock_.lock();

    	try{
    		String name = String.format("registration-%d", reg.regid());
    	    CustodyTimerSpec spec = CustodyTimerSpec.getDefaultInstance();
    	    
    	    log_.add(new ForwardingInfo(state, action, name, reg.regid(),
    	                                  reg.endpoint(), spec));
    	}finally{
    		lock_.unlock();
    		
    	}
    	
    }
    
    /**
     * Add a new forwarding info entry for the remote EID without a
     * specific link or registration.
     * @param eid Endpoint ID for the FowardingInfo
     * @param action Action type for the ForwardingInfo
     * @param state State type for the ForwardingInfo
     */
    public void add_entry(final EndpointID eid, ForwardingInfo.action_t action, state_t state){
    	lock_.lock();

    	try{
    		String name = "eid-"+eid.str();
    	    CustodyTimerSpec spec = CustodyTimerSpec.getDefaultInstance();
    	    
    	    log_.add(new ForwardingInfo(state, action, name, 0xffffffff,
                    eid, spec));
    	}finally{
    		lock_.unlock();
    		
    	}
    }

    /**
     * Update the state for the latest forwarding info entry for the
     * given link.
     * @param link Link to update the state
     * @param state New state to replace with old state of ForwardingInfo
     * @return True if state updated successfully else false
     */
    public boolean update(final Link link, state_t state){

    	lock_.lock();

    	ForwardingInfo info;
    	try{
    		for (int i = log_.size()-1; i>=0; --i)
    	    {
    	    	info = log_.get(i);
    	        if (info.link_name() == link.name_str())
    	        {
    	            // This assertion holds as long as the mapping of link
    	            // name to remote eid is persistent. This may need to be
    	            // revisited once link tables are serialized to disk.
    	            assert(info.remote_eid() == EndpointID.NULL_EID() ||
    	                   info.remote_eid() == link.remote_eid());

    	            info.set_state(state);
    	            return true;
    	        }
    	    }	
    		
    	}finally{
    		lock_.unlock();
    	}
    	return false;
    }

    /**
     * Update all entries in the given state to the new state.
     * @param old_state State to replace with new state
     * @param new_state New state to replace old state
     */
    public void update_all(state_t old_state, state_t new_state){

    	lock_.lock();

    	ForwardingInfo info;
    	try{
    		for (int i = log_.size()-1; i>=0; --i)
    	    {
    	    	info = log_.get(i);
    	    	
    	        if (info.state() == old_state)
    	        {
    	            info.set_state(new_state);
    	        }
    	    }	
    		
    	}finally{
    		lock_.unlock();
    	}
    }
    
    /**
     * Generate a log of ForwardingInfo 
     * @param buf Buffer to append all the information of stored in ForwardingInfos 
     */
    final public void dump(StringBuffer buf){

    	ForwardingInfo info;
    	
    	lock_.lock();

    	try{
    	    for (int i = 0; i<log_.size(); i++)
    	    {
    	    	info = log_.get(i);
    	    	String format = String.format("\t%s -> %s [%s] %s at %s.%s "
                     +"[custody min %s pct %s max %s]\n",
                     ForwardingInfo.state_to_str(info.state()),
                     info.link_name(),
                     info.remote_eid(),
                     ForwardingInfo.action_to_str(info.action()),
                     info.timestamp().getSeconds(),
                     info.timestamp().getSeconds(),
                     info.custody_spec().min(),
                     info.custody_spec().lifetime_pct(),
                     info.custody_spec().max());
    	    	
    	    	buf.append(format);

    	    }   
    	}finally{
    		lock_.unlock();
    	}
    	
    	
    }

    /**
     * Clear the log (used for testing).
     */
    @Override
	public void clear(){
    	lock_.lock();

    	try{
    		log_.clear();
    	}finally{
    		lock_.unlock();
    	}
    }
    
    /**
     * The actual log
     */
    protected ArrayList<ForwardingInfo> log_;
    
    /**
     * Copy of the bundle's lock 
     */
    Lock lock_; 
}
