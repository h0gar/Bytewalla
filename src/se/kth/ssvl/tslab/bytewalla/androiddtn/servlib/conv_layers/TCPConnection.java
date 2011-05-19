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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Date;

import se.kth.ssvl.tslab.bytewalla.androiddtn.DTNService;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ContactEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ContactEvent.reason_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.TCPConvergenceLayer.TCPLinkParams;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.VirtualTimerTask;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.BufferHelper;
import android.util.Log;

/**
 * "Helper class (and thread) that manages an established connection with a peer
 * daemon. 
 * 
 * Although the same class is used in both cases, a particular Connection is
 * either a receiver or a sender, as indicated by the direction variable. Note
 * that to deal with NAT, the side which does the active connect is not
 * necessarily the sender" [DTN2].
 * 
 * @author María José Peroza Marval (mjpm@kth.se)
 */

public class TCPConnection extends Connection {

	/**
	 * Socket timeout in seconds both write and read
	 */
	private int SOCKET_TIMEOUT = 1;

	/**
	 * Unique identifier according to Java Serializable specification
	 */
	private static final long serialVersionUID = 3954903454581565591L;

	/**
	 * TAG for Android Logging mechanism
	 */
	private final static String TAG = "TCPConnection";

	/**
	 * Channel used for reading the data from the socket
	 */
	protected ReadableByteChannel read_channel_;

	/**
	 * Channel used for writing the data into the socket
	 */
	protected WritableByteChannel write_channel_;

	/**
	 * Stream for reading the data from the socket
	 */
	protected InputStream read_stream_;

	/**
	 * Stream for writing the data into the socket
	 */
	protected OutputStream write_stream_;

	/**
	 * bytes to be read
	 */
	private int num_to_read_;

	/**
	 * Constructor
	 */
	TCPConnection(TCPConvergenceLayer cl, TCPLinkParams params)
			throws OutOfMemoryError {

		super(cl, params, true);

		String nexthop = String.format("%s:%s", (params.remote_addr_),
				params.remote_port_);
		set_nexthop(nexthop.toString());
	}

	/**
	 * Call to initialize reading and writting network streams
	 */
	private void initialize_channels_and_streams() throws IOException {
		// initialze the channels for reading and writting
		read_stream_ = socket_.getInputStream();
		write_stream_ = socket_.getOutputStream();

		read_channel_ = Channels.newChannel(read_stream_);
		write_channel_ = Channels.newChannel(write_stream_);

	}

	/**
	 * Connect the socket to the remote endpoint.
	 */
	
	@Override
	void connect() throws ConnectionException {

		// XXX/KLA: this connection was originated from the server socket ,
		if (socket_ != null) {
			// case for server receiving connection
			try {
				initialize_channels_and_streams();
			} catch (IOException e) {
				Log.i(TAG,
						"receiving connection from remote side and fail with "
								+ e.getMessage());
				throw new ConnectionException();
			}
			initiate_contact();
			return;
		}

		// case for client connecting to another server
		socket_ = new Socket();
		// cache the remote addr and port in the fields in the socket
		TCPLinkParams params = (TCPLinkParams) (params_);
		assert (params != null);
		Log.d(TAG, "connect: connecting to " + params.remote_addr_
				+ params.remote_port_);
		InetSocketAddress remote = new InetSocketAddress(params.remote_addr_,
				params.remote_port_);
		try {
			socket_.connect(remote);
			initialize_channels_and_streams();

		} catch (IOException e) {
			throw new ConnectionException();
		} catch (Exception e) {

			throw new ConnectionException();
		}
		// start a connection to the other side... in most cases, this
		// returns EINPROGRESS, in which case we wait for a call to
		// handle_poll_activity

		assert (contact_ == null || contact_.link().isopening());

		initiate_contact();

		try {
			socket_.setSoTimeout(SOCKET_TIMEOUT * 1000);
		} catch (SocketException e1) {
			Log.e(TAG, "Socket exception in set socket timeout");
		}

	}

	/**
	 * Disconnect the socket
	 */
	@Override
	void disconnect() {
		try {
			socket_.close();
			//stop();
		} catch (IOException e) {
			Log.d(TAG, "IOException in disconnect");
		}
	}

	/**
	 * Sending and receiving data by the socket
	 */
	
	@Override
	void handle_poll_activity(int timeout) {

		if (!socket_.isConnected()) {
			Log.d(TAG, "Socket is not connected");
			break_contact(ContactEvent.reason_t.BROKEN);
		}

		// if we have something to send , send it first
		if (sendbuf_.position() > 0)
			send_data();

		// poll to receive and process data
		try {

			num_to_read_ = read_stream_.available();
			// check that there's something to read
			if (num_to_read_ > 0) {

				
				Log.d(TAG, "before reading position is " + recvbuf_.position());

				java.nio.ByteBuffer temp_java_nio_buf = java.nio.ByteBuffer
						.allocate(recvbuf_.remaining());
				read_channel_.read(temp_java_nio_buf);

				BufferHelper.copy_data(recvbuf_, recvbuf_.position(),
						temp_java_nio_buf, 0, temp_java_nio_buf.position());

				recvbuf_.position(recvbuf_.position()
						+ temp_java_nio_buf.position());

				if (DTNService.is_test_data_logging())
					TestDataLogger.getInstance().set_downloaded_size(
							TestDataLogger.getInstance().downloaded_size()
									+ temp_java_nio_buf.position()

					);

				
				Log.d(TAG, "buffer position now is " + recvbuf_.position());

				process_data();

				if (recvbuf_.remaining() == 0) {
					Log.e(TAG, "after process_data left no space in recvbuf!!");

				}

			}

		} catch (IOException e) {
			Log.e(TAG, "IOException, in reading data from the read_stream_:"
					+ e.getMessage());
		}

		// send keep alive message if we should send it
		if (contact_up_ && !contact_broken_) {
			check_keepalive();
		}

		if (!contact_broken_)
			check_timeout();
	}

	/**
	 * The unique name of this convergence layer.
	 */
	protected String name_;

	/**
	 * Accessor for the convergence layer name.
	 */
	public String name() {
		return name_;
	}

	private static class WriteSocketTimeoutTimer extends VirtualTimerTask {
		/**
		 * Unique identifier according to Java Serializable specification
		 */
		private static final long serialVersionUID = -4967126443901245108L;

		WritableByteChannel write_channel_;

		public WriteSocketTimeoutTimer(WritableByteChannel write_channel) {
			write_channel_ = write_channel;
		}

		
		@Override
		protected void timeout(Date now) {

			Log
					.e(TAG,
							"write socket timeout timer fire, closing the write_channel");
			try {
				write_channel_.close();
			} catch (IOException e) {
				Log.e(TAG,
						"IOException write socket timeout timer close write channel :"
								+ e.getMessage());
			}

		}

	}

	
	@Override
	void send_data() {

		int last_position = sendbuf_.position();
		sendbuf_.rewind();
		try {

		
			Log.d(TAG, "Going to write " + last_position
					+ " bytes to the stream");
			java.nio.ByteBuffer temp = java.nio.ByteBuffer
					.allocate(last_position);
			BufferHelper.copy_data(temp, 0, sendbuf_, 0, last_position);

			WriteSocketTimeoutTimer write_socket_timeout_timer = new WriteSocketTimeoutTimer(
					write_channel_);

			Log.d(TAG, "scheduling write_timeout_task in " + SOCKET_TIMEOUT
					+ " seconds");
			try{
			// add the timer to keep looking for Socket timeout
			write_socket_timeout_timer.schedule_in(SOCKET_TIMEOUT);

			}
			catch
			(IllegalStateException e){
				Log.e(TAG, "write socket timer stop when it shouldn't be stopped");
			}
			write_channel_.write(temp);

			// cancel the timer if it's come here, this means the writting is
			// successful
			write_socket_timeout_timer.cancel();
			write_socket_timeout_timer = null;

		

			// move the remaining data back to beginning for next writting
			// the position of the buffer will be moved to the newly available
			// position after movement
			sendbuf_.rewind();

			if (DTNService.is_test_data_logging())
				TestDataLogger.getInstance().set_uploaded_size(
						TestDataLogger.getInstance().uploaded_size()
								+ last_position);

		} catch (AsynchronousCloseException e) {
			Log.e(TAG,
					"another thread close the channel because of the timeout");
			break_contact(reason_t.CL_ERROR);
			sendbuf_.position(last_position);
		} catch (IOException e) {

			Log.e(TAG, "writting broken pipe");
			break_contact(reason_t.CL_ERROR);
			sendbuf_.position(last_position);
		}

	}

}
