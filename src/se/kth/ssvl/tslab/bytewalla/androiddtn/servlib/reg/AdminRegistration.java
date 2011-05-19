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
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.CustodySignal;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol.admin_record_type_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleDeliveredEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.CustodySignalEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointIDPattern;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.SerializableByteBuffer;
import android.util.Log;

/**
 * This class is for internal registration. It receives all the administrative 
 * destined for the router itself. It receives bundles like ping bundles, acknowledgments,
 * status reports.
 * 
 * @author Sharjeel Ahmed (sharjeel@kth.se)
 */

public class AdminRegistration extends Registration{
	

	/**
	 * SerialVersionID to Support Serializable.
	 */
	private static final long serialVersionUID = 5047066101158253955L;
	
	/**
	 * TAG for Android Logging
	 */

	private static String TAG = "AdminRegistration";
	

	/**
	 * Constructor to initialize this class.
	 */
	public AdminRegistration(){
		super(Registration.ADMIN_REGID, new EndpointIDPattern(BundleDaemon.getInstance().local_eid()), Registration.failure_action_t.DEFER, 0, 0, "");
		set_active(true);
	}
	
	/**
	 * Delivery bundle process the administrative bundles and forward it to BundleDaemon
	 * @param bundle Administrative bundle to process.  
	 */
	@Override
	public void deliver_bundle(Bundle bundle){

	    int payload_len = bundle.payload().length();
	    	
	    byte[] payload_buf = new byte[payload_len];
	
        bundle.payload().read_data(0, payload_len, payload_buf);
	    
	    Log.d(TAG, String.format("got %d byte bundle", payload_len));
	        
	    if (payload_len == 0) {
	        Log.d(TAG, String.format("admin registration got 0 byte %d", bundle.bundleid()));
		    BundleDaemon.getInstance().post(new BundleDeliveredEvent(bundle, this));		
		    return;
	    }

	    if (!bundle.is_admin()) {
	        Log.d(TAG, String.format("non-admin %d sent to local eid", bundle.bundleid()));
		    BundleDaemon.getInstance().post(new BundleDeliveredEvent(bundle, this));		
		    return;
	    }

	    byte typecode = (byte) (payload_buf[0] >> 4);
	    
	    switch(admin_record_type_t.get(typecode)) {
	    
	    case ADMIN_STATUS_REPORT:
	    {
	        Log.d(TAG, String.format("status report %d received at admin registration", bundle.bundleid()));
	        break;
	    }
	    
	    case ADMIN_CUSTODY_SIGNAL:
	    {
	        Log.d(TAG, String.format("ADMIN_CUSTODY_SIGNAL %d received", bundle.bundleid()));
	        CustodySignal.data_t data = new CustodySignal.data_t();
	        
	        IByteBuffer buf = new SerializableByteBuffer(payload_buf.length);
	        buf.put(payload_buf);
	        
	        boolean ok = CustodySignal.parse_custody_signal(data, buf, payload_len);
	        
	        if (!ok) {
	            Log.d(TAG, String.format("malformed custody signal %d", bundle.bundleid()));
	            break;
	        }

	        BundleDaemon.getInstance().post(new CustodySignalEvent(data));

	        break;
	    }
	    
	    case ADMIN_ANNOUNCE:
	    {
	        Log.d(TAG, String.format("ADMIN_ANNOUNCE from %d", bundle.source().str()));
	        break;
	    }
	        
	    default:
	        Log.w(TAG, String.format("unexpected admin bundle with type %s %s",
	                 typecode, bundle.bundleid()));
	    }    


	    BundleDaemon.getInstance().post(new BundleDeliveredEvent(bundle, this));		
	};
}
