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

import java.util.Calendar;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.ConvergenceLayer;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;

/**
 * "Announce represents a ConvergenceLayer (Interface). Each announce instance
 * records its CL's address and the interval at which to advertise to or poll
 * for neighbors. Discovery maintains a list of Announce which serve as the
 * basis for its advertisement.
 * 
 * Additionally, Announce serves as a responder. For each discovery it creates a
 * new Contact to the remote node by placing the appropriate call into its
 * CL"[DTN2].
 * 
 * @author María José Peroza Marval (mjpm@kth.se)
 */

public abstract class Announce {

	/**
	 * The name of this Announce instance
	 */
	public String name() {
		return name_;
	}

	/**
	 * Which type of CL is represented by this Announce
	 */
	public String type() {
		return type_;
	}

	/**
	 * "Return a string representation of the ConvergenceLayer address info to
	 * be advertised by parent Discovery"[DTN2].
	 */
	public String local_addr() {
		return local_;
	}

	/**
	 * Hook for derived classes to format information to be advertised
	 */
	public abstract DiscoveryHeader format_advertisement(IByteBuffer buf,
			int len);

	public int FOUR_BYTE_ALIGN(int x) {
		if (((x) % 4) != 0) {
			return ((x) + (4 - ((x) % 4)));
		}
		return (x);

	}

	/**
	 * Return the number of milliseconds remaining until the interval expires,
	 * or 0 if it's already expired
	 */
	public int interval_remaining() {

		long now = Calendar.getInstance().getTimeInMillis();
		long timediff1 = (now - data_sent_);
		int timediff = (int) timediff1;
		if (timediff > interval_) {
			return 0;
		} else
			return (interval_ - timediff);

	}

	/**
	 * Factory method for creating instances of derived classes
	 */
	public static Announce create_announce(String name, ConvergenceLayer cl,
			int argc, String ClType, int interval) {

		String clname = cl.name();
		assert (cl != null);
		Announce announce = null;
		if ((clname.compareTo("tcp") == 0)) {
			announce = new IPAnnounce();
		} else {
			announce = null;

		}

		if (announce.configure(name, cl, argc, ClType, interval)) {
			return announce;
		}

		return null;

	}

	/**
	 * Number of milliseconds between announcements
	 */
	public int interval() {
		return interval_;
	}

	public void set_interval(byte interval) {
		interval_ = interval;
	}

	protected Announce() {
		cl_ = null;
		interval_ = 0;
		data_sent_ = Calendar.getInstance().getTimeInMillis();
	}

	public abstract boolean configure(String name, ConvergenceLayer cl,
			int argc, String ClType, int interval);

	protected ConvergenceLayer cl_; // /< CL represented by this Announce
	protected String local_; // /< Beacon info to advertise
	protected String name_; // /< name for this beacon instance
	protected String type_; // /< pulled from cl_
	protected int interval_; // /< interval (in milliseconds) for beacon header
	protected long data_sent_; // /< mark each time data is sent

}
