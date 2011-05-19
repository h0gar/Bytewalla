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
import java.io.File;

import se.kth.ssvl.tslab.bytewalla.androiddtn.DTNService;
import se.kth.ssvl.tslab.bytewalla.androiddtn.R;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointIDPattern;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.storage.RegistrationStore;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.Lock;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.VirtualTimerTask;
import android.content.Context;
import android.util.Log;


/**
 * This class is implemented as Singleton to store Registrations in memory. RegistrationTable
 * is used to store the registrations but registration storage changes are also made  
 * persistent using RegistrationStore class
 * 
 * 
 * @author Sharjeel Ahmed (sharjeel@kth.se)
 */

public class RegistrationTable{
	

    /**
     * Constructor to initialize RegistrationTable
     */

	public RegistrationTable(){
		lock_ = new Lock();
		reglist_ = new RegistrationList();
		      
		cleanup_api_temp_folder();
		
	}

	/**
	 *  Singleton instance Implementation of the RegistrationTable
	 */

	private static RegistrationTable instance_;

	/**
	 * TAG for Android Logging
	 */

	private static final String TAG = "RegistrationTable";
	
	/**
	 * This function clear all the data on application shutdown
	 */
	public void shutdown()
	{
		reglist_.clear();
		cleanup_api_temp_folder();
		instance_ = null;
	}
	
    /**
     * Singleton Implementation to get the instance of RegistrationTable class
     * @return an singleton instance of RegistrationStore
     */    

	public static RegistrationTable getInstance() {
		if (instance_ == null) {
			instance_ = new RegistrationTable();
		}
		reg_store_ = RegistrationStore.getInstance();
		return instance_;
	}
	
    /**
     * Cleanup function to clean API Temp Folder 
     */    
	private void cleanup_api_temp_folder()
	{
	
		Context context = DTNService.context();
		String TempPrefixName = context.getResources().getString(R.string.DTNAPITempFilePrefix);
		File dir = DTNService.context().getDir(TempPrefixName, Context.MODE_PRIVATE);
	
		
		if(dir.exists()){
			File[] files = dir.listFiles();
			for(int i=0;i<files.length;i++){
				files[i].delete();
			}
		}
			
		Log.d(TAG, "Clean up API Temp Folder Success");
		
		
	}
	
	
    /**
     * Add a new registration to RegistrationList.
     * @param reg Registration to save
     * @param add_to_store If true then add and store to database else just add to table
     * @return True If successfully added else false.
     */
    public boolean add(Registration reg, boolean add_to_store){
  
    	lock_.lock();
    	
    	try{
            reglist_.add(reg);
            
            // don't store (or log) default registrations 
            if (!add_to_store || reg.regid() <= Registration.MAX_RESERVED_REGID) {
                return true;
            }

            // now, all we should get are API registrations
            Registration api_reg = reg;
            
            if (api_reg == null) {
                Log.e(TAG, String.format("non-api registration %s passed to registration store",
                        reg.regid()));
                return false;
            }
            
            Log.d(TAG, String.format("adding registration %s/%s", reg.regid(),
                    reg.endpoint().str()));
           
           if (! RegistrationStore.getInstance().add(api_reg)) {
               Log.e(TAG, String.format("error adding registration %d/%s: error in persistent store",
                       reg.regid(), reg.endpoint().str()));
               return false;
           }
           
           return true;
           
    	}finally{
    		lock_.unlock();
    	}
    }
	
	
    /**
     * Get a Registration from RegistrationTable based on its registration id
     * @param regid Find a registration based on regid
     * @return If registration found then return registration else null
     */
    public final Registration get(int regid){

    	return this.find(regid);    	
    }

    /**
     * Get a Registration from RegistrationTable if endpoint id is matching in Table.
     * @param eid Find a registration based on eid
     * @return If registration found then return registration else null
     */
    public final Registration get(final EndpointIDPattern eid){
       
    	Registration reg;
    	
        for (int i= 0; i< reglist_.size(); i++) {
            reg = reglist_.get(i);

            if (reg.endpoint().equals(eid)) {
                return reg;
            }
        }
        return null;
    }
    
    /**
     * Delete a Registration from RegistrationTable
     * @param regid Delete Registration with this registration id
     * @return True If registration deleted successfully else false
     */
    public boolean del(int regid){

    	Registration reg;
    	
    	lock_.lock();
    	
    	try{
    		Log.d(TAG, String.format("removing registration %s", regid));
    		
    		reg = find(regid);
    		reg.free_payload();
    		
            if(reg==null) {
                Log.e(TAG, String.format("error removing registration %s: no matching registration",
                        regid));
                return false;
            }
            
            if(regid>Registration.MAX_RESERVED_REGID){
	            if (! RegistrationStore.getInstance().del(reg)) {
	                Log.d(TAG, String.format("error removing registration %s: error in persistent store",
	                        regid));
	                return false;
	            }
            }    
            reglist_.remove(reg);
            return true;    	            

    	}finally{
    		lock_.unlock();
    	}
    }

    /**
     * Update the registration in database and RegistrationTable
     * @param reg Registration to update
     * @return True if successfully updated else false
     */
    public boolean update(Registration reg){
    	
    	lock_.lock();
    	
    	try{
    	    Log.d(TAG, String.format("updating registration %s/%s",
    	             reg.regid(), reg.endpoint().str()));

    	    Registration api_reg = reg; 
    	    
    	    if (api_reg == null) {
    	        Log.d(TAG, String.format("non-api registration %s passed to registration store",
    	                reg.regid()));
    	        return false;
    	    }
    	    
    	    if (! RegistrationStore.getInstance().update(api_reg)) {
    	        Log.e(TAG, String.format("error updating registration %s/%s: error in persistent store",
    	                reg.regid(), reg.endpoint().str()));
    	        return false;
    	    }

    	    return true;
    	}finally{
    		lock_.unlock();
    	}
    }
    
    /**
     * Find all the registrations with given (eid) endpoint id and add to registration_list.
     * @param eid Endpoint id to find the matching registrations
     * @param reg_list Add all the found registration in this list.
     * @return Total count of matching registrations
     */
    public final int get_matching(final EndpointID eid, RegistrationList reg_list){

    	int count = 0;
    	
    	lock_.lock();
    	
    	try{
    	    Registration reg;

    	    Log.d(TAG, String.format("get_matching %s", eid.str()));

            for (int i= 0; i< reglist_.size(); i++) {
                reg = reglist_.get(i);

                if (reg.endpoint().equals(eid)) {
                    count++;
                    reg_list.add(reg);
                }
            }

    	    Log.d(TAG, String.format("get_matching %s: returned %d matches", eid.str(), count));
    	    return count;
    	    
    	}finally{
    		lock_.unlock();
    	}
    }
    
    /**
     * Delete any expired registrations
     * @param now Current time
     */
    int delete_expired(final VirtualTimerTask now){
    	
    	return 0;
    }
    
    /**
     * Load all the registrations from database
     * 
     */
    public void load(){
    	reglist_ = reg_store_.load();
    }
    
    /**
     * Dump out the registration database.
     * @param buf Add all the registration metadata to buf
     */
    final public void dump(StringBuffer buf){

    	Registration reg;
        for (int i= 0; i< reglist_.size(); i++) {
            reg = reglist_.get(i);
            
            String dump = String.format("id: %s, eid: %s", reg.regid(), reg.endpoint().str());
            Log.d(TAG, dump);
            buf.append(dump);
        }
    }
    

    /**
     * Return the reglist_ 
     * @return Return the reglist_
     */
    final public  RegistrationList reg_list(){
    	return reglist_;
    }
    /**
     * Find the Registration based on its id.
     * @param regid Find a registration based on this id
     */
    Registration find(int regid){
    	
        Registration reg;
        for (int i= 0; i< reglist_.size(); i++) {
            reg = reglist_.get(i);

            Log.d("TAG", ": "+ reglist_.size());
            Log.d("TAG", "id: "+reg.regid());
            
            if (reg.regid() == regid) {
                return reg;
            }
        }
		return null;
    }
    
    /**
     * This function return the total count of registrations.

     * @return Count of registrations
     */
    public int registration_count(){
    	return reglist_.size();
    }

    /**
     * RegistrationList object to store registration in-memory 
     */
    private static RegistrationList reglist_;

    /** 
     * Lock to protect data.
    */
    private Lock lock_;
 
    /** 
     * RegistrationStore object, It uses to store the registrations in the databae.
    */
    private static RegistrationStore reg_store_;
}
