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
package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.reg;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.Bundle;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleDaemon;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundlePayload.location_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleDeliveredEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleReceivedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.event_source_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointIDPattern;
import android.util.Log;

/**
 * Internal registration for the dtnping application. 
 * @author Sharjeel Ahmed (sharjeel@kth.se)
 */

public class PingRegistration extends Registration {


	/**
	 * SerialVersionID to Support Serializable.
	 */

	private static final long serialVersionUID = -5238042388837219144L;

	/**
	 * TAG for Android Logging
	 */

	private static String TAG = "PingRegistration";

	/**
	 * Constructor to initialize this class.
	 */

    public PingRegistration(final EndpointID eid){
    	
    	super(PING_REGID, new EndpointIDPattern(eid), Registration.failure_action_t.DEFER, 0,0,"");
    	set_active(true);
    }

	/**
	 * Delivery bundle process forward the bundle to BundleDaemon
	 * @param bundle Bundle to process.  
	 */

    @Override
	public void deliver_bundle(Bundle bundle){
    	
        int payload_len = bundle.payload().length();
        
        Log.d(TAG, String.format("%d byte ping from %s",
                  payload_len, bundle.source().str()));
        
        Bundle reply = new Bundle(location_t.MEMORY);
        
        reply.source().assign(endpoint_);
        reply.dest().assign(bundle.source());
        reply.replyto().assign(EndpointID.NULL_EID());
        reply.custodian().assign(EndpointID.NULL_EID());
        reply.set_expiration(bundle.expiration());

        reply.payload().set_length(payload_len);
        reply.payload().write_data(bundle.payload(), 0, payload_len, 0);
        
        BundleDaemon.getInstance().post_at_head(new BundleDeliveredEvent(bundle, this));
        BundleDaemon.getInstance().post_at_head(new BundleReceivedEvent(reply, event_source_t.EVENTSRC_ADMIN));
        	
    }
}
