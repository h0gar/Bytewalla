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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.TCPConvergenceLayer.TCPLinkParams;
import android.util.Log;

/**
 * "Helper class (and thread) that listens on a registered interface for new
 * connections" [DTN2].
 * 
 * @author María José Peroza Marval (mjpm@kth.se)
 */

public class TCPListener extends CLInfo implements Runnable {

	/**
	 * Internal Thread
	 */
	private Thread thread_;

	/**
	 * ServerSocket instance
	 */
	private ServerSocket server_socket_;

	/**
	 * TAG for Android Logging mechanism
	 */
	private static final String TAG = "TCPListener";

	/**
	 * Unique identifier according to Java Serializable specification
	 */
	private static final long serialVersionUID = -6949424199702356461L;

	/**
	 * Constructor
	 */
	public TCPListener(ConvergenceLayer convergenceLayer, int port) {

		cl_ = (TCPConvergenceLayer) convergenceLayer;

		try {

			server_socket_ = new ServerSocket(port);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "IOException " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Check is the socket is bound
	 */
	public boolean isBound() {
		return server_socket_.isBound();
	}

	/**
	 * Start the TCPListener thread
	 */
	public void start() {
		thread_ = new Thread(this);
		listening_ = true;
		thread_.start();

	}

	/**
	 * Stop the TCPListener thread
	 */
	public void stop() {
		listening_ = false;
		thread_ = null;
		try {
			server_socket_.close();
		} catch (IOException e) {
			Log.d(TAG, "IOException stopping server_socket: " + e.getMessage());
		}
		server_socket_ = null;
	}

	TCPConvergenceLayer cl_; // / The TCPCL instance
	Socket socket; // / The socket

	private boolean listening_ = false; // / Listening flag

	
	/**
	 * Main loop
	 */
	public void run() {

		while (listening_) {
			try {
				Log.d(TAG, "start accepting connection");
				socket = server_socket_.accept();
			} catch (IOException e) {
				Log.d(TAG, "IOException in accept");
				continue;
			}
			Log.d(TAG, "Connection Accepted");

			TCPConnection tcpconnection;

			try {
				TCPLinkParams tlp = cl_.new TCPLinkParams(true);
				tlp.remote_addr_ = socket.getInetAddress();
				tcpconnection = new TCPConnection(cl_, tlp);
				tcpconnection.set_socket(socket);
				tcpconnection.start();

			} catch (OutOfMemoryError e) {
				Log.d(TAG, "Not enough resources");
			}

		}

	}
}
