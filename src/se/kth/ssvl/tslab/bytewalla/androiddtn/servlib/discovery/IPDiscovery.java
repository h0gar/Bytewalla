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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleDaemon;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.SerializableByteBuffer;
import android.util.Log;

/**
 * "IPDiscovery is the main thread in IP-based neighbor discovery, configured
 * via config file or command console to listen to a specified UDP port for
 * unicast, broadcast, or multicast beacons from advertising neighbors" [DTN2].
 * 
 * @author María José Peroza Marval (mjpm@kth.se)
 */

public class IPDiscovery extends Discovery implements Runnable {

	/**
	 * TAG for Android Logging mechanism
	 */
	public static final String TAG = "IPDiscovery";

	/**
	 * Socket receive timeout in milisecs
	 */
	private static final int TIMEOUT_MS = 5000;

	/**
	 * Maximun interval for advertising the announcements
	 */
	private static final int INT_MAX = 500;

	/**
	 * Wifi Manager //
	 */
	// private static WifiManager mWifi = (WifiManager) DTNService.context()
	// .getSystemService(Context.WIFI_SERVICE);

	/**
	 * Internal thread
	 */
	private Thread thread_;

	/**
	 * Constructor
	 */
	IPDiscovery(String name, short port) {

		super(name, "ip");
		// remote_addr_ = null;
		mcast_ttl_ = DEFAULT_MCAST_TTL;
		port_ = port;
		shutdown_ = false;
		persist_ = false;
		thread_ = new Thread(this);
	}

	/**
	 * If no other options are set for multicast TTL, set to 1
	 */
	public static final int DEFAULT_MCAST_TTL = 1;

	/**
	 * Enumerate which type of CL is advertised
	 */
	public enum cl_type_t {
		UNDEFINED(0), TCPCL(1); // TCP Convergence Layer

		private static final Map<Integer, cl_type_t> lookup = new HashMap<Integer, cl_type_t>();

		static {
			for (cl_type_t s : EnumSet.allOf(cl_type_t.class))
				lookup.put(s.getCode(), s);
		}

		private int code;

		private cl_type_t(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static cl_type_t get(int code) {
			return lookup.get(code);
		}
	}

	public static String type_to_str(cl_type_t t) {
		switch (t) {
		case TCPCL:
			return "tcp";
		case UNDEFINED:
			return "undefined";
		default:
			return "invalid Converge Layer type";
		}

	}

	public static cl_type_t str_to_type(String cltype) {
		if (cltype.compareTo("tcp") == 0)
			return cl_type_t.TCPCL;
		else
			return cl_type_t.UNDEFINED;
	}

	/**
	 * Close main socket, causing thread to exit
	 */
	@Override
	public void shutdown() {
		shutdown_ = true;
		socket_.close();
	}

	//
	// /**
	// * Get the current ip addres of the mobile. Get the ip assigned by the
	// DHCP
	// * @return the current IPaddress
	// */
	// public static InetAddress getting_my_ip() {
	//
	// InetAddress local_addr_ = null;
	// DhcpInfo dhcp = mWifi.getDhcpInfo();
	// if (dhcp == null) {
	// Log.d(TAG, "Could not get dhcp info");
	// }
	//
	// short[] quads = new short[4];
	// byte[] quads2 = new byte[4];
	// for (int k = 0; k < 4; k++) {
	// quads[k] = (byte) ((dhcp.ipAddress >> k * 8) & 0xFF);
	// quads2[k] = (byte) quads[k];
	// }
	// try {
	// local_addr_ = InetAddress.getByAddress(quads2);
	//
	// } catch (UnknownHostException e) {
	//
	// Log.d(TAG, "error getting_my_ip");
	// }
	//
	// return local_addr_;
	//
	// }

	/**
	 * Get the broadcast IP of the network where the phone is connected
	 * 
	 * @return the broadcast IPaddress
	 */
	// public InetAddress getting_broadcast_ip() {
	//
	// BROADCAST = null;
	// DhcpInfo dhcp = mWifi.getDhcpInfo();
	// if (dhcp == null) {
	// Log.d(TAG, "Could not get dhcp info");
	// }
	//
	// int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	// byte[] quads = new byte[4];
	// for (int k = 0; k < 4; k++)
	// quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
	// try {
	// BROADCAST = InetAddress.getByAddress(quads);
	// } catch (UnknownHostException e) {
	// Log.d(TAG, "Error calculating Broadcast address");
	// }
	//
	// return BROADCAST;
	// }

	/**
	 * Set internal state using parameter list; return true on success, else
	 * false
	 */
	@Override
	protected boolean configure() {

		if (thread_.isAlive()) {
			Log.w(TAG, "reconfiguration of IPDiscovery not supported");
			return false;
		}

		if (port_ == 0) {
			Log.e(TAG, "must specify port");
			return false;
		}

		// remote_addr_ = getting_broadcast_ip();

		// "Assume everything is broadcast unless unicast flag is set or if
		// the remote address is a multicast address
		// static IntAddress mcast_mask = 224.0.0.0" [DTN2];
		//
		// if (remote_addr_ == BROADCAST) {
		// Log.d(TAG, "configuring broadcast socket for remote address");
		//
		// } else if (!remote_addr_.isMulticastAddress()) {
		// Log.d(TAG, "configuring unicast socket for remote address");
		// }

		try {
			socket_ = new DatagramSocket(port_);
			socket_.setBroadcast(true);
			socket_.setSoTimeout(TIMEOUT_MS);
		} catch (IOException e) {
			Log.e(TAG, "bind failed " + e.getMessage());
		}

		Log.d(TAG, "starting thread");

		return true;

	}

	/**
	 * Main loop in Discovery
	 */

	public void run() {

		Log.d(TAG, "discovery thread running");
		IByteBuffer buf = new SerializableByteBuffer(1024);

		while (true) {
			if (shutdown_)
				break;

			/* Send section */
			try {
				int min_diff = INT_MAX;
				Iterator<Announce> i = list_.iterator();

				while (i.hasNext()) {
					IPAnnounce announce = (IPAnnounce) i.next();

					int remaining = announce.interval_remaining();

					if (remaining == 0) {
						try {
							// Log.d(TAG, "announce ready for sending");
							hdr = announce.format_advertisement(buf, 1024);

							buf.put(hdr.cl_type());
							buf.put(hdr.interval());
							buf.putShort(hdr.length());
							byte[] ip_address = new byte[] { 0, 0, 0, 0 };
							buf.put(ip_address);
							buf.putShort(hdr.inet_port());
							buf.putShort(hdr.name_len());
							byte[] name = hdr.sender_name().getBytes();
							buf.put(name);

							int l = hdr.length();
							data = new byte[l];
							int z;
							for (z = 0; z < l; z++) {
								data[z] = buf.get(z);
							}

							DatagramPacket pack = new DatagramPacket(data,
									data.length, InetAddress
											.getByName("255.255.255.255"),
									port_);
							socket_.send(pack);
							min_diff = announce.interval();
						} catch (Exception e) {
							Log.e(TAG, "error sending the packet "
									+ e.getMessage());
						}
					} else {
						// Log.d(TAG, "Could not send discovery request");
						if (remaining < min_diff) {
							min_diff = announce.interval_remaining();
						}

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			/* receive section */
			try {
				buf.rewind();

				byte[] Rdata = new byte[1024];

				DatagramPacket packet = new DatagramPacket(Rdata, Rdata.length);

				socket_.receive(packet);

				// String s = new String(packet.getData(), 0,
				// packet.getLength());
				// Log.d(TAG, "Received response " + s);

				EndpointID remote_eid = new EndpointID();
				String nexthop = "";// For now

				byte[] b = packet.getData();
				ByteBuffer bb = ByteBuffer.wrap(b);

				hdr = new DiscoveryHeader();
				hdr = new DiscoveryHeader();
				hdr.set_cl_type(bb.get());

				hdr.set_interval(bb.get());
				hdr.set_length(bb.getShort());

				byte[] addr = new byte[4];
				bb.get(addr);

				hdr.set_inet_addr(InetAddress.getByAddress(addr));
				hdr.set_inet_port(bb.getShort());
				short name_len = bb.getShort();
				hdr.set_name_len(name_len);
				byte[] name = new byte[name_len];
				bb.get(name);
				String sender_name = new String(name);
				hdr.set_sender_name(sender_name);
				remote_eid = new EndpointID(hdr.sender_name());
				// if(hdr.inet_addr().toString().equals("0.0.0.0"))
				nexthop = packet.getAddress().toString() + ":"
						+ hdr.inet_port();
				// else
				// nexthop = hdr.inet_addr().toString()+":"+hdr.inet_port();

				String Type = IPDiscovery.type_to_str(cl_type_t.get(hdr
						.cl_type()));

				BundleDaemon BD = BundleDaemon.getInstance();

				if (remote_eid.equals(BD.local_eid())) {
					// Log.d(TAG, "ignoring beacon from self" + remote_eid);
				} else {
					// distribute to all beacons registered for this CL type
					handle_neighbor_discovered(Type, nexthop, remote_eid);
				}

			} catch (Exception e) {
				// Log.d(TAG, "Fail receiving the UDP datagram " +
				// e.getMessage());
			}

		}
	}

	/**
	 * Outbound address of advertisements sent by this Discovery instance
	 */
	@Override
	public String to_addr() {
		return to_addr_;
	}

	/**
	 * Local address on which to listen for advertisements
	 */
	@Override
	public String local_addr() {
		return local_;
	}

	/**
	 * Start the IPDiscovery thread
	 */
	@Override
	public void start() {

		thread_.start();
	}

	protected void handle_announce() {

	}

	protected volatile boolean shutdown_; // /< signal to close down thread
	// protected InetAddress local_addr_; // /< address for bind() to receive
	// beacons
	protected short port_; // /< local and remote
	// protected InetAddress remote_addr_; // /< whether unicast, multicast, or
	// broadcast
	protected int mcast_ttl_; // /< TTL hop count for multicast option
	protected DatagramSocket socket_; // /< the socket for beacons in- and
	// out-bound
	protected boolean persist_; // /< whether to exit thread on send/recv
	// failures
	protected DiscoveryHeader hdr; // / Header of the discovery packet
	protected byte[] data; // / the data to be sent
	protected InetAddress BROADCAST; // / Broadcast address of the network where
	// the phone is connected

}
