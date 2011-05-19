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


/**
 * "Tunable parameter structure stored in each Link's CLInfo slot. Other
 * CL-specific parameters are handled by deriving from this class" [DTN2].
 * 
 * @author María José Peroza Marval (mjpm@kth.se)
 */

public class LinkParams extends CLInfo {

	/**
	 * Unique identifier according to Java Serializable specification
	 */
	private static final long serialVersionUID = -591469011590595531L;

	/**
	 * flag for ack segments
	 */
	private boolean segment_ack_enabled_ = false;
	private boolean negative_ack_enabled_ = false;

	/**
	 * Getter and setter for segment_ack_enabled
	 */
	public boolean segment_ack_enabled() {

		return segment_ack_enabled_;

	}

	public void set_segment_ack_enabled(boolean segment_ack_enabled) {
		segment_ack_enabled_ = segment_ack_enabled;
	}

	/**
	 * Getter and setter for negative_ack_enabled
	 */
	public boolean negative_ack_enabled() {
		return negative_ack_enabled_;
	}

	public void set_negative_ack_enabled(boolean negative_ack_enabled) {
		negative_ack_enabled_ = negative_ack_enabled;
	}

	/**
	 * Flag for reactive fragmentation
	 */
	private boolean reactive_frag_enabled_;

	/**
	 * Getter and setter for reactive fragmentation flag
	 */
	public boolean reactive_frag_enabled() {
		return reactive_frag_enabled_;
	}

	public void set_reactive_frag_enabled(boolean reactiveFragEnabled) {
		reactive_frag_enabled_ = reactiveFragEnabled;
	}

	/**
	 * Buffer size for sending data
	 */
	private int sendbuf_len_;

	/**
	 * Getter and setter for the sending buffer size
	 */
	public int sendbuf_len() {
		return sendbuf_len_;
	}

	public void set_sendbuf_len(int sendbufLen) {
		sendbuf_len_ = sendbufLen;
	}

	/**
	 * Buffer size for receiving data
	 */
	private int recvbuf_len_;

	/**
	 * Getter and setter for the receiving buffer size
	 */
	public int recvbuf_len() {
		return recvbuf_len_;
	}

	public void set_recvbuf_len_(int recvbufLen) {
		recvbuf_len_ = recvbufLen;
	}

	/**
	 * Msecs to wait for data arrival
	 */
	private int data_timeout_;

	/**
	 * Getter and setter for the data timeout
	 */
	public int data_timeout() {
		return data_timeout_;
	}

	public void set_data_timeout(int dataTimeout) {
		data_timeout_ = dataTimeout;
	}

	/**
	 * Msecs to sleep between read calls
	 */
	private int test_read_delay_;

	/**
	 * Getter and setter for the read delay
	 */
	public int test_read_delay() {
		return test_read_delay_;
	}

	public void set_test_read_delay(int testReadDelay) {
		test_read_delay_ = testReadDelay;
	}

	/**
	 * Msecs to sleep between write calls
	 */
	private int test_write_delay_;

	/**
	 * Getter and setter for the write delay
	 */
	public int test_write_delay() {
		return test_write_delay_;
	}

	public void set_test_write_delay(int testWriteDelay) {
		test_write_delay_ = testWriteDelay;
	}

	/**
	 * Msecs to sleep before recv event
	 */
	private int test_recv_delay_;

	/**
	 * Getter and setter for the receiving delay
	 */
	public int test_recv_delay() {
		return test_recv_delay_;
	}

	public void set_test_recv_delay(int testRecvDelay) {
		test_recv_delay_ = testRecvDelay;
	}

	/**
	 * Max amount to read from the channel
	 */
	private int test_read_limit_;

	/**
	 * Getter and setter for the amount of data to read from the channel
	 */
	public int test_read_limit() {
		return test_read_limit_;
	}

	public void set_test_read_limit(int testReadLimit) {
		test_read_limit_ = testReadLimit;
	}

	/**
	 * Max amount to write to the channel
	 */
	private int test_write_limit_;

	/**
	 * Getter and setter for the amount of data to write to the channel
	 */
	public int test_write_limit() {
		return test_write_limit_;
	}

	public void set_test_write_limit(int testWriteLimit) {
		test_write_limit_ = testWriteLimit;
	}

	// "The only time this constructor should be called is to
	// initialize the default parameters. All other cases (i.e.
	// derivative parameter classes) should use a copy constructor
	// to grab the default settings" [DTN2].
	protected LinkParams(boolean init_defaults) {

		reactive_frag_enabled_ = false;
		negative_ack_enabled_ = false;
		segment_ack_enabled_ = true;

		sendbuf_len_ = CLConnection.DEFAULT_SEND_RECEIVE_BUFFER_SIZE;
		recvbuf_len_ = CLConnection.DEFAULT_SEND_RECEIVE_BUFFER_SIZE;
		// data_timeout_ = 30000; // msec
		data_timeout_ = 10000; // msec

		// XXX/KLA: actually all of this test parameters are optional you could
		// leave them and not doing anything in here and the
		// the actual code to reduce the complexity of the code
		test_read_delay_ = 0;
		test_write_delay_ = 0;
		test_recv_delay_ = 0;
		test_read_limit_ = 0;
		test_write_limit_ = 0;

	}

}
