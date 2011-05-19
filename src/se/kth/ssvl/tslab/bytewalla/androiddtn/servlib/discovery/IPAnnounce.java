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

package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.discovery;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleDaemon;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.ConvergenceLayer;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.TCPConvergenceLayer;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;
import android.util.Log;

/**
 * "Helper class that 1) formats outbound beacons to advertise this
 * CL instance via neighbor discovery, and 2) responds to inbound
 * advertisements by creating a new Contact"[DTN2].
 * 
 * @author María José Peroza Marval (mjpm@kth.se)
 */

public class IPAnnounce extends Announce{
	
	/**
	 * TAG for Android Logging mechanism
	 */
	private static final String TAG = "IPAnnounce";
	
	/**
     * Serialize announcement out to buffer
     */
	@Override
	public DiscoveryHeader format_advertisement(IByteBuffer buf, int len){
	    	
	    		    		    	
	    	BundleDaemon BDaemon = BundleDaemon.getInstance();
	    	EndpointID local = (BDaemon.local_eid());  
	    	    int length = FOUR_BYTE_ALIGN(local.length() + 19);

	    	    if (len <= length)
	    	        return null;

	    	    DiscoveryHeader hdr = new DiscoveryHeader();
	    	    
	    	    hdr.set_cl_type((byte)IPDiscovery.str_to_type(type()).getCode());
	    	    hdr.set_interval((byte)(interval_ / 100));
	    	    hdr.set_length((short)length);
	    	    hdr.set_inet_addr(cl_addr_);
	    	    hdr.set_inet_port(cl_port_);
	    	    hdr.set_name_len((short)local.length());
	    	    hdr.set_sender_name(local.str());
	    	   
	    	    data_sent_ = Calendar.getInstance().getTimeInMillis();
	    	    
	    	    return hdr;    	
	    	
	    }

	    /**
	     * Export cl_addr to use in sending Announcement out on correct interface
	     */
	    public InetAddress cl_addr(){ return cl_addr_; }

	
	    /**
	     * Constructor
	     */
	    public IPAnnounce(){
	    	
	    	super();
	    	try {
				cl_addr_ = InetAddress.getByName("0.0.0.0");
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	cl_port_ = TCPConvergenceLayer.TCPCL_DEFAULT_PORT;
	    }

	    /**
	     * Deserialize parameters for configuration
	     */
	    @Override
		public boolean configure(String name, ConvergenceLayer cl,int argc, String ClType, int interval){
	    	
	    	 if (cl == null) return false;

	    	    cl_ = cl;
	    	    name_ = name;
	    	    type_ =ClType;

	    	    // validate convergence layer details
	    	    if (type_.compareTo("tcp")!=0)	    	        
	    	    {
	    	        Log.e(TAG,"cl type not supported");
	    	        return false;
	    	    }
	    	    
	    	    interval_ = interval;
	    	  
	    	    if (interval_ == 0)
	    	    {
	    	        Log.e(TAG,"interval must be greater than 0");
	    	        return false;
	    	    }

	    	    // convert from seconds to ms
	    	    interval_ *= 1000;

	    	    StringBuffer buf = new StringBuffer();
	    	    buf.append(cl_addr_ + "/n");
	    	    buf.append(cl_port_);
	    	    int end = buf.length()-1;
	    	    local_ = buf.substring  (0,end);	    	    	    
	    	    return true;
	    	
	    }

	    /**
	     * next hop info for CL to be advertised
	     */	    
	    InetAddress cl_addr_;
	    short cl_port_;

}
