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

package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts;

/**
 * This class represents of the statistics of a link
 * 
 * @author María José Peroza Marval (mjpm@kth.se)
 */
public class LinkStats {

	/**
	 * Constructor
	 */
	public LinkStats() {

		contact_attempts_ = 0;
		contacts_ = 0;
		bundles_transmitted_ = 0;
		bytes_transmitted_ = 0;
		bundles_cancelled_ = 0;
		uptime_ = 0;
		availability_ = 0;
		reliability_ = 0;
	}

	/**
	 * Number of times the link attempted to be opened.
	 */
	private int contact_attempts_;

	public int contact_attempts() {
		return contact_attempts_;
	}

	public void set_contact_attempts(int contactAttempts) {
		contact_attempts_ = contactAttempts;
	}

	/**
	 * Number of contacts ever successfully opened on the link (equivalent to
	 * the number of times the link was open)
	 */
	private int contacts_;

	public int contacts() {
		return contacts_;
	}

	public void set_contacts(int contacts) {
		contacts_ = contacts;
	}

	/**
	 * Number of bundles transmitted over the link.
	 */
	private int bundles_transmitted_;

	public int bundles_transmitted() {
		return bundles_transmitted_;
	}

	public void set_bundles_transmitted(int bundlesTransmitted) {
		bundles_transmitted_ = bundlesTransmitted;
	}

	/**
	 * Total byte count transmitted over the link.
	 */
	private int bytes_transmitted_;

	public int bytes_transmitted() {
		return bytes_transmitted_;
	}

	public void set_bytes_transmitted(int bytesTransmitted) {
		bytes_transmitted_ = bytesTransmitted;
	}

	/**
	 * Number of bundles with cancelled transmission.
	 */
	private int bundles_cancelled_;

	public int bundles_cancelled() {
		return bundles_cancelled_;
	}

	public void set_bundles_cancelled(int bundlesCancelled) {
		bundles_cancelled_ = bundlesCancelled;
	}

	/**
	 * The total uptime of the link, not counting the current contact.
	 */
	private long uptime_;

	public long uptime() {
		return uptime_;
	}

	public void set_uptime(long uptime) {
		uptime_ = uptime;
	}

	/**
	 * The availablity of the link, as measured over time by the convergence
	 * layer.
	 */
	private int availability_;

	public int availability() {
		return availability_;
	}

	public void set_availability(int availability) {
		availability_ = availability;
	}

	/**
	 * "The reliability of the link, as measured over time by the convergence
	 * layer. This is different from the is_reliable setting, which indicates
	 * whether the convergence layer should expect acks from the peer" [DTN2].
	 */
	private int reliability_;

	public int reliability() {
		return reliability_;
	}

	public void set_reliability(int reliability) {
		reliability_ = reliability;
	}

}
