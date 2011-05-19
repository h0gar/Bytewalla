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
import java.util.Date;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleExpiredEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.VirtualTimerTask;


/**
 * This class handles time expiration of bundle
 * 
 *  @author Sharjeel Ahmed (sharjeel@kth.se)
 */

public class ExpirationTimer extends VirtualTimerTask implements Serializable{

	/**
	 * SerialVersionID to Support Serializable.
	 */

	private static final long serialVersionUID = 7128087288863023333L;

	/**
	 * Constructor
	 * @param bundle Set bundle to monitor its expiration time. 
	 */
	public ExpirationTimer(Bundle bundle){
		bundleref_ =  bundle;
	}
	
	/**
	 * On timeout forward the bundle to BundleDaemon to handle it.
	 * @param now Current time
	 */
	@Override
	protected void timeout(Date now) {
	    bundleref_.set_expiration_timer(null);

	    BundleDaemon.getInstance().post_at_head(new BundleExpiredEvent(bundleref_));

	}
	
    private Bundle bundleref_;
}
