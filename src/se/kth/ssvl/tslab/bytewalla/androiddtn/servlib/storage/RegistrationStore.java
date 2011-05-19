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



package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.storage;

import java.util.Iterator;
import java.util.List;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.DTNConfiguration;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.StorageSetting.storage_type_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.reg.Registration;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.reg.RegistrationList;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.storage.StorageIterator;
import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.util.Log;


/**
 * The class for registration storage is simply an instantiation of the
 * generic oasys durable table interface.
 */
/**
 * This class is implemented as Singleton to Registration.
 * This class generates registration id, stores registration on the disk 
 * . This class also uses Generic StorageImplementation and 
 * SQLiteImplementation to store registration.
 * 
 * @author Sharjeel Ahmed (sharjeel@kth.se)
 */

public class RegistrationStore{

	/**
	 *  Singleton instance Implementation of the RegistrationStore
	 */
	private static RegistrationStore instance_ = null;

	/**
	 * Database table name for storing registrations.  
	 */

	private static String table = "registration";

	/**
	 * TAG for Android Logging
	 */
	private static String TAG = "RegistrationStore";

	/**
	 * default registration file name  
	 */

	private static String registrationFileName = "registration_";
	
	/**
	 * SQL Query for creating new Registration in the SQLite database. 
	 */
    private static final String Table_CREATE_Registration = 
        "create table IF NOT EXISTS  registration (id integer primary key autoincrement, " 	
            + " uri text);";     
    

    /**
     * Singleton Implementation Getter function
     * @return an singleton instance of RegistrationStore
     */    

    public static RegistrationStore getInstance() {
        if(instance_ == null) {
           instance_ = new RegistrationStore();
        }
        return instance_;
     }

	/**
	 * Private constructor for Singleton Implementation of the RegistrationStore
	 */
    private  RegistrationStore(){
    	registration_count_ = 0;
    }
    
    /**
     * This function initiate the Registration Storage for storing Registration.
     * This function creates StorageImplementation & SQLiteImplementation instances,
     * set storage directory path & counts valid number of registrations.
     * @param context The application context for getting for application paths  
     * @param config Get the application configuration to get the memory usage parameters 
     * @return returns true on success
     */        
    
    public boolean init(Context context, DTNConfiguration config){

    	config_ = config;
    	
    	Log.d(TAG, "Going to init" );
    	if(!init_){
    		impt_sqlite_ = new SQLiteImplementation(context, Table_CREATE_Registration);
    		impt_storage_ = new StorageImplementation<Registration>(context);
    		init_ = true;
    	}
    	String cond_find_record = "id = "+Registration.MAX_RESERVED_REGID;
    	
    	if(!impt_sqlite_.find_record(table, cond_find_record)){
    		ContentValues values = new ContentValues();
    		values.put("id", Registration.MAX_RESERVED_REGID);
    		values.put("uri", "");
    		impt_sqlite_.add(table, values);
    	}
    	String app_folder = "/"+config_.storage_setting().storage_path();
		
    	if(config.storage_setting().storage_type()==storage_type_t.PHONE){
    		path_ = context.getFilesDir().getAbsolutePath().concat(app_folder).concat("/registration");
    	}else{
    		path_ = Environment.getExternalStorageDirectory().getAbsolutePath().concat(app_folder).concat("/registration");
    	}
		
    	String condition = "id > "+Registration.MAX_RESERVED_REGID;
    	String[] field = new String[1];
    	field[0] = "count(id)";
    	registration_count_ = impt_sqlite_.get_count(table,condition, field );
		return impt_storage_.create_dir(path_);    	
    }

    /**
     * Store the new registration. 
     * @param reg Store the registration
     * @return return true if bundle successfully saved otherwise return false
     */        
    
    public boolean add(Registration reg){
    	// check If there no reg and endpoint then add
    	
    	if(isUnique(reg)){
    		ContentValues values = new ContentValues();
    		values.put("uri", reg.endpoint().str());
    		String condition = "id = "+reg.regid();
    		
    		if(impt_sqlite_.update(table, values, condition, null)){

    			String filename = registrationFileName+reg.regid();
    			
    			if(impt_storage_.add_object(reg, filename)){
    				registration_count_ += 1;
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    /**
     * Delete the registration if exists. 
     * @param reg Get the Registration to delete
     * @return true if bundle successfully deleted otherwise return false
     */        

    public boolean del(Registration reg){
    	String condition = " id = "+reg.regid();
    	
		if(impt_sqlite_.delete_record(table, condition)){
			
			String filename = registrationFileName+reg.regid();
			
			if(impt_storage_.delete_file(filename)){
				registration_count_ -= 1;
				return true;
			}
		}
    	return false;
    }

    /**
     * Update the registration if registration already stored. 
     * @param reg Get the Registration to update
     * @return return true if bundle successfully updated otherwise return false
     */        
    
    public boolean update(Registration reg){
    	ContentValues values = new ContentValues();
    	values.put("uri", reg.endpoint().uri().toString());
		String condition = "id = "+reg.regid();
		
		if(impt_sqlite_.update(table, values, condition, null)){
			String filename = registrationFileName+reg.regid();
			return impt_storage_.add_object(reg, filename );
		}
		return false;
    }

    /**
     * Get the registration from storage based on regid and return the registration. 
     * @param regid Get the registration with this regid from storage
     * @return If registration  found then return the bundle otherwise return null 
     */        

    public Registration get(int regid){
    	String filename = registrationFileName+regid;
    	return impt_storage_.get_object(filename);
    }

    /**
     * Load all the registrations to RegistrationList
     * @return RegistrationList Populated Registration list with all the registrations
     */
    public RegistrationList load(){
    	RegistrationList reg_list = new RegistrationList();
    	
    	String condition = " id > "+Registration.MAX_RESERVED_REGID ;
    	String field = "id";
    	List<Integer> regids = impt_sqlite_.get_records(table,condition, field );
    	
    	if(regids!=null){
    		Iterator<Integer> iter = regids.iterator();
    		while(iter.hasNext()){
    			reg_list.add(get(iter.next()));
    		}
    	}
    	
		return reg_list;
    }

    /**
     * Generate an unique bundle id for new registration. 
     * @return the next registration id
     */        

    public int next_regid(){
		ContentValues values = new ContentValues();
		values.put("uri", "");

		return impt_sqlite_.add(table, values);
    }
    
    /**
     * Reset all the registration storage. 
     * It resets registration storage folder and registration table in database.
     * @return If storage has been reset successfully then return true otherwise false.
     */        
    
    public boolean reset_storage(){

    	
    	if(impt_sqlite_.drop_table(table)){
    		
        	if(!impt_storage_.delete_dir(path_)){
        		return false;
        	}

    		if(impt_sqlite_.create_table(Table_CREATE_Registration)){

    			ContentValues values = new ContentValues();
    			values.put("id", Registration.MAX_RESERVED_REGID);
    			values.put("uri", "");

    			if(impt_sqlite_.add(table, values)==Registration.MAX_RESERVED_REGID){
    				return true;
    			};
    			
    		}
    	}
    	registration_count_ = 0;
    	return false;
    }

    /**
     * Get total number of stored registrations. 
     * @return Total number of stored registration
     */        
    
    public int get_registration_count(){
    	return registration_count_;
    	
    }
    
    /**
     * Return true if initialization has completed.
     */
    public static boolean initialized() {
		return false;
    }

    /**
     * Close the SQLite connection.
     */        
    
    public void close(){
    	impt_sqlite_.close();
    	init_ = false;
    	instance_ = null;
    }
    
    /**
     * Check if registration is unique or not.
     * @param reg Registration to check if already stored or not.
     * @return True if registration is unique
     */        

    public boolean isUnique(Registration reg){
    	String uri = reg.endpoint().str();
    	String condition = "id ="+reg.regid()+" AND uri LIKE '"+uri+"'";
    	return impt_sqlite_.find_record(table, condition);
    }

    /**
     * Test function to check if registration file exists or not. 
     * @return True if file exist else return false
     */        

    public boolean test_is_registration_file(int regid){
    	String filename = registrationFileName+regid;
    	return impt_storage_.is_bundle_file(filename);
    }

    /**
     * Create a new iterator to iterator all the registrations.
     * @return New registration iterator to iterator registrations
     */        
    
    public  Iterator<Registration> new_iterator()
    {
    	RegistrationStoreIterator itr_ = new RegistrationStoreIterator() ;
    	String pre_condition = " id > ";
    	String post_condition = " AND id >"+Registration.MAX_RESERVED_REGID;
    	String first_condition = " id > "+Registration.MAX_RESERVED_REGID;
    	
    	itr_.set_itr(impt_sqlite_,this, table, pre_condition, post_condition, first_condition);
    	return itr_;
    	
    }

    /**
     * Private Class that implements Iterator to make a Registration Iterator.
     */        

    private static class RegistrationStoreIterator implements Iterator<Registration>
    {
        /**
         * StorageIterator<Integer> object to storage Iterator. Logic is
         * implemented in StorageIterator that is a generic iterator.
         */        
    	
		private StorageIterator<Registration> itr_;

        /**
         * Set the iterator for registrations before iterating registrations.
         * Iterator checks for the next registration in the database.  
         * @param  impt_sqlite SQLite obejct to access database
         * @param registration_store to access registration_store objects
         * @param table database table name 
         * @param pre_condition Database query condition has two parts pre_condition & post_condition  
         * @param post_condition Database query condition has two parts pre_condition & post_condition
         * @param first_condition Database query condition for initializing iterator
         */        
		
		
		public void set_itr(SQLiteImplementation impt_sqlite, RegistrationStore registration_store, String table, String pre_condition, String post_condition, String first_condition)
		{
	    	itr_ = new StorageIterator<Registration>(impt_sqlite, table, pre_condition, post_condition, first_condition);
		}
		
		
        /**
         * Check if iterator has more objects.
         * @return True if there is more else false  
         */        
		
		public boolean hasNext() {
			return itr_.hasNext();
		}

		
        /**
         * Return the next Registration
         * @return Next Registration in the iterator  
         */        

		public Registration next() {
			return instance_.get(itr_.next());
		}

		
		public void remove() {
			// TODO Auto-generated method stub
			
		}

    }    
    
	/**
	 * StorageImplementation object to use with registration 
	 */
    
    private static StorageImplementation<Registration> impt_storage_;

    /**
	 * SQLiteImplementation object 
	 */
    private static SQLiteImplementation impt_sqlite_;

    /**
	 * Number of registrations stored  
	 */
    private int registration_count_;

    /**
	 * DTNConfiguration to stores the application configurations,  
	 */
    private DTNConfiguration config_;

	/**
	 * init_ to make sure in init() it only makes SQLiteImplementation only once   
	 */

    private static boolean init_ = false;

	/**
	 * Storage path of registration folder
	 */

    private String path_;
}
