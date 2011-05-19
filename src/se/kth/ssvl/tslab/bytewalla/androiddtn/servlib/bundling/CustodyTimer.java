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

package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.CustodyTimeoutEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.VirtualTimerTask;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.TimeHelper;
import android.util.Log;
 

/**
 * A timer for retransmitting the bundle in custody of this node.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class CustodyTimer extends VirtualTimerTask implements Serializable{
	/**
	 * String TAG for using with Android Logging system
	 */
	private final static String TAG = "CustodyTimer";
    /**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -7470636238119877716L;

	/** Constructor */
	public CustodyTimer(final Date xmit_time,
                 final CustodyTimerSpec spec,
                 Bundle bundle, final Link link)
	{
		bundle_  = bundle;
		link_    = link;
		
		// All in seconds
		int delay = spec.calculate_timeout(bundle);
		Calendar xmit_calendar = Calendar.getInstance();
		xmit_calendar.setTime(xmit_time);
		
		Calendar now_calendar  = Calendar.getInstance();
		int delay_from_now   = (int)(TimeHelper.seconds_from_ref(xmit_calendar) -  TimeHelper.seconds_from_ref(now_calendar)
		                          + delay);
		
		Log.i(TAG,
				String.format("scheduling custody timer: xmit_time %s, " +
				"delay from xmit_time %d secs, " +
				"delay from now %d secs, " +
	             " for bundle id %d",
	             xmit_time.toString(), 
	             delay,
	             delay_from_now,
	             bundle.bundleid()));
		schedule_in(delay_from_now);
	}

   

    /**
     *  The bundle which the timers being responsible of
     */
    private Bundle bundle_;

    /**
     *  The link for retransimitting
     */
    private Link link_;

	/**
	 * Getter for the Bundle
	 * @return the bundle_
	 */
	public Bundle bundle() {
		return bundle_;
	}

	/**
	 * Setter for the Bundle
	 * @param bundle the bundle_ to set
	 */
	public void set_bundle(Bundle bundle) {
		bundle_ = bundle;
	}

	/**
	 * Getter for the link for retransmitting
	 * @return the link_
	 */
	public Link link() {
		return link_;
	}

	/**
	 * Setter for the link for retransmitting
	 * @param link the link_ to set
	 */
	public void set_link(Link link) {
		link_ = link;
	}

	
	@Override
	protected void timeout(Date now) {
		Log.i(TAG, "CustodyTimer::timeout");
		BundleDaemon.getInstance().post(new CustodyTimeoutEvent(bundle_, link_));
	}

	
};

