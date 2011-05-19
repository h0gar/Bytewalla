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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.CLInfo;
import android.util.Log;

/**
 * "Encapsulation of an active connection to a next-hop DTN contact. This is
 * basically a repository for any state about the contact opportunity including
 * start time, estimations for bandwidth or latency, etc.
 * 
 * It also contains the CLInfo slot for the convergence layer to put any state
 * associated with the active connection.
 * 
 * Since the contact object may be used by multiple threads in the case of a
 * connection-oriented convergence layer, and because the object is intended to
 * be deleted when the contact opportunity ends, all object instances are
 * reference counted and will be deleted when the last reference is removed"
 * [DTN2].
 * 
 * @author María José Peroza Marval (mjpm@kth.se)
 */

public class Contact implements Serializable {

	/**
	 * Unique identifier according to Java Serializable specification
	 */
	private static final long serialVersionUID = -1151889301561623754L;

	/**
	 * TAG for Android Logging mechanism
	 */
	private static final String TAG = "Contact";

	/**
	 * Constructor
	 */
	public Contact(Link link) {

		Log.d(TAG, "" + link.name());

		cl_info_ = null;
		start_time_ = Calendar.getInstance().getTime();
		duration_ = 0;
		bps_ = 0;
		latency_ = 0;
		link_ = link;
		Log.i(TAG, "new contact" + this);
	}

	
	/**
	 * Store the convergence layer state associated with the contact.
	 */
	public void set_cl_info(CLInfo cl_info) {
		assert cl_info_ == null && cl_info != null : cl_info_ != null
				&& cl_info == null;

		cl_info_ = cl_info;
	}

	/**
	 * Accessor to the convergence layer info.
	 */
	public CLInfo cl_info() {
		return cl_info_;
	}

	/**
	 * Accessor to the link
	 */
	public Link link() {
		return link_;
	}

	/**
	 * Virtual from formatter
	 */
	public int format(StringBuffer buf, int sz) {

		String text = String.format("contact %s started %s /n", link_.nexthop_,
				start_time_);

		buf.append(text);

		return text.length();

	}

	/**
	 * Virtual from SerializableObject
	 */
	
	// / @{ Accessors
	public Date start_time() {
		return start_time_;
	}

	public int duration() {
		return duration_;
	}

	public int bps() {
		return bps_;
	}

	public int latency() {
		return latency_;
	}

	public void set_start_time(Date t) {
		start_time_ = t;
	}

	public void set_duration(int duration) {
		duration_ = duration;
	}

	public void set_bps(int bps) {
		bps_ = bps;
	}

	public void set_latency(int latency) {
		latency_ = latency;
	}

	// / @}

	// / Time when the contact begin
	protected Date start_time_;

	// / Contact duration (0 if unknown)
	protected int duration_;

	// / Approximate bandwidth
	protected int bps_;

	// / Approximate latency
	protected int latency_;

	protected Link link_; // /< Parent link on which this contact exists

	protected CLInfo cl_info_; // /< convergence layer specific info

}
