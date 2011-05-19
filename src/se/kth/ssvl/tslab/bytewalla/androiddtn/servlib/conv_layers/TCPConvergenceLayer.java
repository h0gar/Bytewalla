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


import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import se.kth.ssvl.tslab.bytewalla.androiddtn.DTNService;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Interface;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * The TCP Convergence Layer.
 * 
 * @author María José Peroza Marval (mjpm@kth.se)
 */

public class TCPConvergenceLayer extends StreamConvergenceLayer implements Serializable{

	/**
	 * Unique identifier according to Java Serializable specification
	 */
	private static final long serialVersionUID = -5515355466259078293L;
	
	/**
	 * TCP ConvergeceLayer follow in this application
	 */
	public static final byte TCPCL_VERSION = 0x00;
	
	/**
	 * Default port for TCP ConvergenceLayer
	 */
	public static final short TCPCL_DEFAULT_PORT = 4556;
	
	/**
	 * TAG for Android Logging mechanism
	 */
	private static final String TAG = "TCPConvergenceLayer";
	
	/**
	 * Constructors
	 */
	public TCPConvergenceLayer() {
		super();
		cl_version_ = 3;
	}

	public TCPConvergenceLayer(String cl_name) {
		super();
		name_ = cl_name;
		cl_version_ = 3;
	}

	/**
	 * Get the IP address that the DHCP server assigns to the mobile phone
	 * @return The current IP address
	 */
	public static InetAddress getting_my_ip() {
		/**
		 * WifiManager
		 */
		WifiManager mWifi = (WifiManager) DTNService.context()
				.getSystemService(Context.WIFI_SERVICE);

		InetAddress local_addr_ = null;
		DhcpInfo dhcp = mWifi.getDhcpInfo();
		if (dhcp == null) {
			Log.d(TAG, "Could not get dhcp info");
			return null;
		}

		short[] quads = new short[4];
		byte[] quads2 = new byte[4];
		for (int k = 0; k < 4; k++) {
			quads[k] = (byte) ((dhcp.ipAddress >> k * 8) & 0xFF);
			quads2[k] = (byte) quads[k];
		}
		try {
			local_addr_ = InetAddress.getByAddress(quads2);

		} catch (UnknownHostException e) {

			Log.d(TAG, "error getting_my_ip");
		}

		return local_addr_;
	}
	

	/**
	 * Bring up an interface.
	 */
	@Override
	public boolean interface_up(Interface iface) {

		Log.d(TAG, "adding interface " + iface.name());
		InetAddress local_addr_ = getting_my_ip();
			
		// check that the local interface / port are valid
		if (local_addr_ == null) {
			Log.e(TAG, "invalid local address setting of null");
			return false;
		}

		if (local_port == 0) {
			Log.e(TAG, "invalid local port setting of 0");
			return false;
		}

		// create a new server socket for the requested interface
		
		listen_ = new TCPListener(iface.clayer(),local_port);
		
		if (!listen_.isBound()) {

			Log.w(TAG, "listener in not bound");
		}
		
		iface.set_cl_info(listen_);
		listen_.start();
		Interface.set_iface_counter(Interface.iface_counter()+1);
		
		return true;

	}
	
	/**
	 * Set the local port
	 */
	@Override
	public void set_local_port (short port){
		local_port = port;
	}

	/**
	 * Bring down an interface.
	 */
	@Override
	public boolean interface_down(Interface iface) {
		
		TCPListener listener = (TCPListener)(iface.cl_info());
		assert (listener != null) : "TCPConvergenceLayer : interface_down, socket is null";
		listener.stop();
		Interface.set_iface_counter(Interface.iface_counter()-1);
		return true;
	}

	/**
	 * Dump out CL specific interface information.
	 */
	@Override
	public void dump_interface(Interface iface, StringBuffer buf) {

		ServerSocket listener = iface.socket();
		assert (listener != null) : "TCPConvergenceLayer : dump_interface, socket is null";

		String text = String.format("\tlocal_addr: %s local_port: %s\n",
				listener.getInetAddress(), listener.getLocalPort());
		buf.append(text);

	}
	
	/**
	 * Parse the destination  IPaddress and the remote port
	 */
	@Override
	public boolean parse_nexthop (Link link, LinkParams lparams){
		
		TCPLinkParams params = (TCPLinkParams)(lparams);
	    assert(params != null);
	   
	    
	    params.remote_addr_ = link.dest_ip();
	    params.remote_port_ = link.remote_port();
	        	    
	    
	    // if the port wasn't specified, use the default
	    if (params.remote_port_ == 0) {
	        params.remote_port_ = TCPCL_DEFAULT_PORT;
	    }
	    
	    return true;
	}

	/**
     * Tunable link parameter.
     * 
     * @author María José Peroza Marval (mjpm@kth.se)
     */
	public class TCPLinkParams extends StreamLinkParams {

		/**
		 * Unique identifier according to Java Serializable specification
		 */
		private static final long serialVersionUID = -1953097665691499712L;

		public boolean hexdump_; // /< Log a hexdump of all traffic
		public InetAddress local_addr_; // /< Local address to bind to
		public InetAddress remote_addr_; // /< Peer address used for
		
		public InetAddress remote_addr(){
			return remote_addr_;
		}
		
		public short local_port_;									// rcvr-connect
		public short remote_port_; // /< Peer port used for rcvr-connect

		/**
		 * Constructor
		 */
		protected TCPLinkParams(boolean init_defaults) {

			super(init_defaults);
			hexdump_ = false;
			local_addr_ = getting_my_ip();
			local_port_ = local_port;
			remote_addr_ = dest_addr_;
			remote_port_ = TCPCL_DEFAULT_PORT;
		}

	}
	
	
	@Override
	public void dump_link(Link link, StringBuffer buf) {

		assert (link != null) : "TCPConvergenceLayer : dump_link, link is null";
		assert (!link.isdeleted()) : "TCPConvergenceLayer : dump_link, link is deleted";
		assert (link.cl_info() != null) : "TCPConvergenceLayer : dump_link, cl_info is null";

		super.dump_link(link, buf);

		TCPLinkParams params = (TCPLinkParams) (link.cl_info());
		assert (params != null) : "TCPConvergenceLayer : dump_link, params are null";

		buf.append("local_addr: " + (params.local_addr_) + "\n");
		buf.append("remote_addr: " + (params.remote_addr_) + "\n");
		buf.append("remote_port: " + (params.remote_port_) + "\n");

	}

	
	@Override
	public LinkParams new_link_params(){
		
		return new TCPLinkParams(true);
	}
	
	/**
	 * Create a new TCPConnection
	 */
	@Override
	public TCPConnection new_connection(Link link, LinkParams p)throws OutOfMemoryError {
		
		TCPLinkParams params = (TCPLinkParams) p;
		assert (params != null);
		dest_addr_ = link.dest_ip();
		dest_port_ = link.remote_port();
		return new TCPConnection(this, params);
	}

	
	
	TCPListener listen_;  // / Listener (Represents a server socket waiting for a connection) 
	
	protected InetAddress dest_addr_; // / Destination IPaddress
	protected short dest_port_; // / destination port
	protected short local_port; // / local port
	
	


}
