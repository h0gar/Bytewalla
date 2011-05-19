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
package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


/**
 * This class represents StorageSetting in the configuration file
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public class StorageSetting {
	
	/**
	 * Storage Type of the DTN Bundles
	 */
	public static enum storage_type_t
	{
		PHONE("phone"),
		SDCard("sdcard");
		
		private static final Map<String, storage_type_t> lookupCaption = new HashMap<String, storage_type_t>();

		
		
		static {
			for (storage_type_t s : EnumSet
					.allOf(storage_type_t.class))
				{ 
			
				  lookupCaption.put(s.getCaption(), s);
				}
		}

		
		private String caption;
		private storage_type_t(String caption) {

			this.caption = caption;
		}


		public String getCaption() {
			return caption;
		}
	} 
	
	/**
	 * Relative storage path where to put DTNBundle data.
	 * If the storage type is SDCard, it's relative to root of the SDCard ( normally "/sdcard" in filesystem ).
	 * If the storage type is Phone, it's relative to the application specific folder in the phone
	 */
	private String storage_path_;
	
	private storage_type_t storage_type_;
	
	/**
	 *  Quota of storage to consume, Unit is in MB
	 */
	private int quota;

	/**
	 * Accessor for the storage_type_t of this storage setting
	 * @return the storage_type_
	 * @see storage_type_t
	 */
	public storage_type_t storage_type() {
		return storage_type_;
	}

	/**
	 * Setter for the storage_type_t of this storage setting
	 * @param storageType the storage_type_ to set
	 * @see storage_type_t
	 */
	public void set_storage_type(storage_type_t storage_type) {
		storage_type_ = storage_type;
	}

	/**
	 * Accessor for the quota of storage to consume, Unit is in MB
	 * @return the quota
	 */
	public int quota() {
		return quota;
	}

	/**
	 * Setter for the quota of storage to consume, Unit is in MB
	 * @param quota the quota to set
	 */
	public void set_quota(int quota) {
		this.quota = quota;
	}

	/**
	 * Accessor for the relative storage path where to put DTNBundle data.
	 * If the storage type is SDCard, it's relative to root of the SDCard ( normally "/sdcard" in filesystem ).
	 * If the storage type is Phone, it's relative to the application specific folder in the phone
	 * @return the storage_path_
	 */
	public String storage_path() {
		return storage_path_;
	}

	/**
	 * Setter for the relative storage path where to put DTNBundle data.
	 * If the storage type is SDCard, it's relative to root of the SDCard ( normally "/sdcard" in filesystem ).
	 * If the storage type is Phone, it's relative to the application specific folder in the phone
	 * @param storagePath the storage_path_ to set
	 */
	public void set_storage_path(String storagePath) {
		storage_path_ = storagePath;
	}
	
}
