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
package se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types;


/**
 * The data structure to get result from the IBinder
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public class DTNBundleID  {
	/**
	 * The source EndointID of this Bundle
	 */
	private DTNEndpointID source_;
	
	/**
	 * The Bundle creation Timestamp for this Bundle
	 */
	private DTNBundleTimestamp creation_ts_;
	
	/**
	 * The fragmentation offset of this Bundle
	 */
	private int frag_offset_;
	
	/**
	 * The total application data unit length of this Bundle. This is used when the Bundle is a fragment.
	 */
	private int orig_length_;
	
	/**
	 * Accessor for the source EndointID of this Bundle
	 * @return the source_
	 */
	public DTNEndpointID source() {
		return source_;
	}
	/**
	 * Setter for the source EndointID of this Bundle
	 * @param source the source_ to set
	 */
	public void set_source(DTNEndpointID source) {
		source_ = source;
	}
	/**
	 * Getter for the Bundle creation Timestamp for this Bundle
	 * @return the creation_ts_
	 */
	public DTNBundleTimestamp creation_ts() {
		return creation_ts_;
	}
	/**
	 * Setter for the Bundle creation Timestamp for this Bundle
	 * @param creationTs the creation_ts_ to set
	 */
	public void set_creation_ts(DTNBundleTimestamp creation_ts) {
		creation_ts_ = creation_ts;
	}
	/**
	 * Getter for the fragmentation offset of this Bundle
	 * @return the frag_offset_
	 */
	public int frag_offset() {
		return frag_offset_;
	}
	/**
	 * Setter for the fragmentation offset of this Bundle
	 * @param fragOffset the frag_offset_ to set
	 */
	public void set_frag_offset(int frag_offset) {
		frag_offset_ = frag_offset;
	}
	/**
	 * Getter for the total application data unit length of this Bundle. This is used when the Bundle is a fragment.
	 * @return the orig_length_
	 */
	public int orig_length() {
		return orig_length_;
	}
	/**
	 * Setter for the total application data unit length of this Bundle. This is used when the Bundle is a fragment.
	 * @param origLength the orig_length_ to set
	 */
	public void set_orig_length(int orig_length) {
		orig_length_ = orig_length;
	}

	
}
