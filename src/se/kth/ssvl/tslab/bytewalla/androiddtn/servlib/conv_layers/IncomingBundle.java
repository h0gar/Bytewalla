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

package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.Bundle;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.DataBitmap;

/**
 * "Class used to record bundles that are in the process of being received along
 * with their transmission state and relevant acknowledgement data"[DTN2].
 * 
 * @author María José Peroza Marval (mjpm@kth.se)
 */

public class IncomingBundle {

	/**
	 * Constructor
	 */
	public IncomingBundle(Bundle b) {
		bundle_ = b;
		total_length_ = 0;
		acked_length_ = 0;
		rcvd_data_ = new DataBitmap();
		// ack_data_ = new DataBitmap();
	}

	/**
	 * Incoming bundle instance
	 */
	private Bundle bundle_;

	/**
	 * Getter of the incoming bundle
	 */
	public Bundle bundle() {
		return bundle_;
	}

	/**
	 * Length of the incoming bundle
	 */
	private int total_length_;

	/**
	 * Getter and setter of the length of the incoming bundle
	 */
	public int total_length() {
		return total_length_;
	}

	public void set_total_length(int totalLength) {
		total_length_ = totalLength;
	}

	/**
	 * Length of the acked data
	 */
	private int acked_length_;

	/**
	 * Getter and setter of the acked data length
	 */
	public int acked_length() {
		return acked_length_;
	}

	public void set_acked_length(int ackedLength) {
		acked_length_ = ackedLength;
	}

	/**
	 * DataBitmap of the received data
	 */
	private DataBitmap rcvd_data_;

	/**
	 * Getter of the DataBitmap of the received data
	 */
	public DataBitmap rcvd_data() {

		return rcvd_data_;
	}

}
