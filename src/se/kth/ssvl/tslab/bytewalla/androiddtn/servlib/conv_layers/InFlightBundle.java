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

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BlockInfoVec;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.Bundle;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.DataBitmap;

/**
 * "Class used to record bundles that are in-flight along with their
 * transmission state and optionally acknowledgement data" [DTN2].
 * 
 * @author María José Peroza Marval (mjpm@kth.se)
 */
public class InFlightBundle {

	/**
	 * Constructor
	 */
	public InFlightBundle(Bundle b) {

		bundle_ = b;
		total_length_ = 0;
		send_complete_ = false;
		transmit_event_posted_ = false;
		sent_data_ = new DataBitmap();
		ack_data_ = new DataBitmap();

	}

	/**
	 * InflightBundle and its getter
	 */
	private Bundle bundle_;

	public Bundle bundle() {
		return bundle_;
	}

	/**
	 * BlockInfo vector / Getter and setter
	 */
	private BlockInfoVec blocks_;

	public BlockInfoVec blocks() {
		return blocks_;
	}

	public void set_blocks(BlockInfoVec blocks) {
		blocks_ = blocks;
	}

	/**
	 * Total length of the Inflight bundles, its getter and its setter
	 */
	private int total_length_;

	public int total_length() {
		return total_length_;
	}

	public void set_total_length(int totalLength) {
		total_length_ = totalLength;
	}

	/**
	 * Flag for complete sending / Getter and Setter
	 */
	private boolean send_complete_;

	public boolean send_complete() {
		return send_complete_;
	}

	public void set_send_complete(boolean send_complete) {
		send_complete_ = send_complete;
	}

	/**
	 * Flag for posting a transmit event/ Getter and setter
	 */
	boolean transmit_event_posted_;

	public boolean transmit_event_posted() {
		return transmit_event_posted_;
	}

	public void set_transmit_event_posted(boolean transmitEventPosted) {
		transmit_event_posted_ = transmitEventPosted;
	}

	/**
	 * DataBitmap for sent data / Getter and setter
	 */
	private DataBitmap sent_data_;

	public DataBitmap sent_data() {
		return sent_data_;
	}

	public void set_sent_data(DataBitmap sentData) {
		sent_data_ = sentData;
	}

	/**
	 * DataBitmap for acked data / Getter and setter
	 */
	private DataBitmap ack_data_;

	public DataBitmap ack_data() {
		return ack_data_;
	}

	public void set_ack_data(DataBitmap ackData) {
		ack_data_ = ackData;
	}

}
