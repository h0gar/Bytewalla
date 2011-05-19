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

import java.io.File;

import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPICode.dtn_bundle_payload_location_t;

/**
 * The class to represent DTNBundlePayload used in the API
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public class DTNBundlePayload {
	/**
	 *  The internal bundle payload location. 
	 */
	private dtn_bundle_payload_location_t location_;

	/**
	 *  File Handle for payload location = DISK only
	 */
	private File file_;
	
	/**
	 *  Memory Buffer in case of payload location = MEMORY only
	 */
	private byte[] buf_;
	
	/**
	 * The returned DTNBundleStatus report
	 */
	private DTNBundleStatusReport status_report_;
	
	/**
	 * The length of DTNBundlePayload
	 */
	private int length_;
	
	/**
	 * Constructor that takes the location of the payload as argument
	 * @param location
	 */
	public DTNBundlePayload(dtn_bundle_payload_location_t location)
	{
		location_ = location;
	}
	
	/**
	 * Set the payload length. This will allocate memory buffer in the case of Memory location
	 * @param length the length that will be set
	 */
	public void set_length(int length)
	{
		if (location_ == dtn_bundle_payload_location_t.DTN_PAYLOAD_MEM)
		{
		buf_ = new byte[length];
		}
		length_ = length;
		
		
	}
	
	/**
	 * Getter for the length of this Bundle Payload
	 * @return
	 */
	public int length()
	{
		return length_;
	}
	/**
	 * Getter for the DTNBundlePayload location
	 * @return the location
	 */
	public dtn_bundle_payload_location_t location() {
		return location_;
	}
	/**
	 * Setter for the DTNBundlePayload location
	 * @param location the location to set
	 */
	public void set_location(dtn_bundle_payload_location_t location) {
		this.location_ = location;
	}
	
	/**
	 * Getter for the internal memory buffer 
	 * @return the buf_
	 */
	public byte[] buf() {
		return buf_;
	}
	/**
	 * Setter for the internal memory buffer
	 * @param buf the buf to set
	 */
	public void set_buf(byte[] buf) {
		this.buf_ = buf;
	}
	/**
	 * Getter for the DTNBundleStatusReport for this payload
	 * @return the status_report
	 */
	public DTNBundleStatusReport status_report() {
		return status_report_;
	}
	/**
	 * Setter for the DTNBundleStatusReport for this payload
	 * @param statusReport the status_report to set
	 */
	public void set_status_report(DTNBundleStatusReport status_report) {
		status_report_ = status_report;
	}

	/**
	 * Getter for the File Handle for payload location = DISK only
	 * @return the file_
	 */
	public File file() {
		return file_;
	}

	/**
	 * Setter for the File Handle for payload location = DISK only
	 * @param file the file_ to set
	 */
	public void set_file(File file) {
		file_ = file;
		set_length((int)file.length());
	}
	
	
}
