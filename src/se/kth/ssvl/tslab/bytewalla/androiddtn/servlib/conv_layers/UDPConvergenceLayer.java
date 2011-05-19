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

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Interface;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import android.util.Log;

/**
 * The UDP Convergence Layer.
 * 
 * @author Mahesh Bogadi Shankr Prasad(mabsp@kth.se)
 */

public class UDPConvergenceLayer extends StreamConvergenceLayer implements Serializable{

	/**
	 * Unique identifier according to Java Serializable specification
	 */
	private static final long serialVersionUID = -5515355466259078293L;
	
	/**
	 * UDP ConvergeceLayer follow in this application
	 */
	public static final byte UDPCL_VERSION = 0x00;
	
	/**
	 * Default port for UDP ConvergenceLayer
	 */
	public static final short UDPCL_DEFAULT_PORT = 4556;
	
	/**
	 * TAG for Android Logging mechanism
	 */
	private static final String TAG = "UDPConvergenceLayer";
	
	/**
	 * Constructors
	 */
	public UDPConvergenceLayer() {
		super();
		cl_version_ = 3;
	}

	public UDPConvergenceLayer(String cl_name) {
		super();
		name_ = cl_name;
		cl_version_ = 3;
	}

	/**
	 * Get the IP address that the DHCP server assigns to the mobile phone
	 * @return The current IP address
	 */
	public static InetAddress getting_my_ip() {
		return TCPConvergenceLayer.getting_my_ip();
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
//
		// create a new server socket for the requested interface
		
		listen_ = new UDPListener(iface.clayer(),local_port);
		
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
		
		UDPListener listener = (UDPListener)(iface.cl_info());
		assert (listener != null) : "UDPConvergenceLayer : interface_down, socket is null";
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
		assert (listener != null) : "UDPConvergenceLayer : dump_interface, socket is null";

		String text = String.format("\tlocal_addr: %s local_port: %s\n",
				listener.getInetAddress(), listener.getLocalPort());
		buf.append(text);
	}
	
	/**
	 * Parse the destination  IPaddress and the remote port
	 */
	@Override
	public boolean parse_nexthop (Link link, LinkParams lparams){
		
		UDPLinkParams params = (UDPLinkParams)(lparams);
	    assert(params != null);
	   	    
	    params.remote_addr_ = link.dest_ip();
	    params.remote_port_ = link.remote_port();
	        	    
	    
	    // if the port wasn't specified, use the default
	    if (params.remote_port_ == 0) {
	        params.remote_port_ = UDPCL_DEFAULT_PORT;
	    }
	    
	    return true;
	}

	/**
     * Tunable link parameter.
     * 
     * @author María José Peroza Marval (mjpm@kth.se)
     */
	public class UDPLinkParams extends StreamLinkParams {

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
		protected UDPLinkParams(boolean init_defaults) {

			super(init_defaults);
			hexdump_ = false;
			local_addr_ = getting_my_ip();
			local_port_ = local_port;
			remote_addr_ = dest_addr_;
			remote_port_ = UDPCL_DEFAULT_PORT;
		}
	}
	
	
	@Override
	public void dump_link(Link link, StringBuffer buf) {

		assert (link != null) : "UDPConvergenceLayer : dump_link, link is null";
		assert (!link.isdeleted()) : "UDPConvergenceLayer : dump_link, link is deleted";
		assert (link.cl_info() != null) : "UDPConvergenceLayer : dump_link, cl_info is null";

		super.dump_link(link, buf);

		UDPLinkParams params = (UDPLinkParams) (link.cl_info());
		assert (params != null) : "UDPConvergenceLayer : dump_link, params are null";

		buf.append("local_addr: " + (params.local_addr_) + "\n");
		buf.append("remote_addr: " + (params.remote_addr_) + "\n");
		buf.append("remote_port: " + (params.remote_port_) + "\n");
	}

	
	@Override
	public LinkParams new_link_params(){
		
		return new UDPLinkParams(true);
	}
	
	/**
	 * Create a new UDPConnection
	 */
	@Override
	public UDPConnection new_connection(Link link, LinkParams p)throws OutOfMemoryError {
		
		UDPLinkParams params = (UDPLinkParams) p;
		assert (params != null);
		dest_addr_ = link.dest_ip();
		dest_port_ = link.remote_port();
		return new UDPConnection(this, params);
	}

	
	
	UDPListener listen_;  // / Listener (Represents a server socket waiting for a connection) 
	
	protected InetAddress dest_addr_; // / Destination IPaddress
	protected short dest_port_; // / destination port
	protected short local_port; // / local port
	
	


}
