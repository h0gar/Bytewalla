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
 * API type to represent DTNBundleTimestamp
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public class DTNBundleTimestamp{
	/**
	 * Seconds from reference time in the protocol ( 1 Jan, 2000)
	 */
	private long secs_;
	
	/**
	 * Unique sequence number of Bundle within the second according to the protocol
	 */
	private long seqno_;
	/**
	 * Getter function for seconds from reference Time in the protocol ( 1 Jan, 2000)
	 * @return the secs_
	 */
	public long secs() {
		return secs_;
	}
	/**
	 * Setter function for seconds from reference Time in the protocol ( 1 Jan, 2000)
	 * @param secs the secs_ to set
	 */
	public void set_secs(long secs) {
		secs_ = secs;
	}
	/**
	 * Getter function for unique sequence number of Bundle within the second according to the protocol
	 * @return the seqno_
	 */
	public long seqno() {
		return seqno_;
	}
	/**
	 * Setter function for unique sequence number of Bundle within the second according to the protocol
	 * @param seqno the seqno_ to set
	 */
	public void set_seqno(long seqno) {
		seqno_ = seqno;
	}
}
