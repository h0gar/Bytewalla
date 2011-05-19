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

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.Bundle;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.DTNConfiguration;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.StorageSetting.storage_type_t;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * GlobalStorage class is implemented as Singleton to keep the total application size.
 * 
 * @author Sharjeel Ahmed (sharjeel@kth.se)
 */


public class GlobalStorage {

	/**
	 *  Singleton instance Implementation of the GlobalStorage
	 */
	private static GlobalStorage instance_ = null;

	/**
	 * TAG for Android Logging
	 */
	private static String TAG = "GlobalStore";
    

    /**
     * Singleton Implementation Getter function
     * @return an singleton instance of GlobalStorage
     */    
	public static GlobalStorage getInstance() {
        if(instance_ == null) {
           instance_ = new GlobalStorage();
        }
        return instance_;
     }	
	
    /**
     * This function initiate the Global Storage and calculate the total size application size.
     * @param context The application context for getting for application paths  
     * @param config Get the application configuration to get the memory usage parameters 
     * @return returns true on success
     */        
    
	
    public boolean init(Context context, DTNConfiguration config){
    	config_ = config;

    	impt_storage_ = new StorageImplementation<Bundle>(context);
    	
		String app_folder = "/"+config_.storage_setting().storage_path();
		String path;
		
		if(config.storage_setting().storage_type()==storage_type_t.PHONE){
    		path = context.getFilesDir().getAbsolutePath().concat(app_folder);
    	}else{
    		path = Environment.getExternalStorageDirectory().getAbsolutePath().concat(app_folder);
    	}

		String path_registration = path+"/registration";
		
		String path_database = context.getDatabasePath(config.storage_setting().storage_path()).getAbsolutePath();
	
		// Total bundles size will be calculated in BundleStorge and added here 
	//total_size_ += impt_storage_.get_directory_size(path_storage);
		total_size_ += impt_storage_.get_directory_size(path_registration);
		Log.d(TAG, "Total Size of DTN Folder:"+total_size_);
		total_size_ += impt_storage_.get_file_size(path_database);
    	Log.d(TAG, "Total Size of DTN Folder:"+total_size_);

    	return true;
    }
    
	/**
	 * Private constructor for Singleton Implementation of the BundleStore
	 */
    private  GlobalStorage(){
		total_size_ = 0;
	}

    /**
     * This function is the setter of total_size_
     */        
    public void set_total_size(long size){
		total_size_ = size;
	}
	
    /**
     * Close any opened connection
     */        
    public void close()
    {
    	instance_ = null;
    }

    /**
     * This function is the getter of total_size_
     * @return Total storage currently using.
     */        

    public long get_total_size(){
		return total_size_ ;
	}
	
    /**
     * Add size to the total_size 
     * @param size Number of bytes to add
     */        
	public void add_total_size(long size){
		total_size_ += size;
	}

    /**
     * Remove size from the total_size 
     * @param size Number of bytes to delete from total size
     */        

	public void remove_total_size(long size){
		total_size_ -= size;
	}
	/**
	 * Total memory consumption 
	 */

	private long total_size_;
	
	/**
	 * DTNConfiguration to stores the application configurations,  
	 */
	private DTNConfiguration config_;

	/**
	 * StorageImplementation object to use with bundle 
	 */
 
    private static StorageImplementation<Bundle> impt_storage_;
}