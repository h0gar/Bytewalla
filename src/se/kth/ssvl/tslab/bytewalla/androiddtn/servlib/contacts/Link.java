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

package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import se.kth.ssvl.tslab.bytewalla.androiddtn.DTNService;
import se.kth.ssvl.tslab.bytewalla.androiddtn.R;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.Bundle;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleDaemon;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleList;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.CLInfo;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.ConvergenceLayer;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.RouterInfo;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.Lock;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.TimeHelper;
import android.util.Log;

/**
 * "Abstraction for a DTN link, i.e. a one way communication channel to a next
 * hop node in the DTN overlay.
 * 
 * The state of a link (regarding its availability) is described by the
 * Link::state_t enumerated type.
 * 
 * All links in the OPEN state have an associated contact that represents an
 * actual connection.
 * 
 * Every link has a unique name associated with it which is used to identify it.
 * The name is configured explicitly when the link is created.
 * 
 * Creating new links: Links are created explicitly in the configuration file.
 * Syntax is:
 * 
 * @code link add <name> <nexthop> <type> <conv_layer> <args>
 * @endcode See servlib/cmd/LinkCommand.cc for implementation of this TCL cmd.
 * 
 *          ----------------------------------------------------------
 * 
 *          Links are of three types as discussed in the DTN architecture
 *          ONDEMAND, SCHEDULED, OPPORTUNISTIC.
 * 
 *          The key differences from an implementation perspective are "who" and
 *          "when" manipulates the link state regarding availability.
 * 
 *          ONDEMAND links are initialized in the AVAILABLE state, as one would
 *          expect. It remains in this state until a router explicitly opens it.
 * 
 *          An ONDEMAND link can then be closed either due to connection failure
 *          or because the link has been idle for too long, both triggered by
 *          the convergence layer. If an ONDEMAND link is closed due to
 *          connection failure, then the contact manager is notified of this
 *          event and periodically tries to re-establish the link.
 * 
 *          For OPPORTUNISTIC links the availability state is set by the code
 *          which detects that there is a new link available to be used.
 * 
 *          SCHEDULED links have their availability dictated by the schedule
 *          implementation.
 * 
 *          ----------------------------------------------------------
 * 
 *          Links are used for input and/or output. In other words, for
 *          connection-oriented convergence layers like TCP, a link object is
 *          created whenever a new connection is made to a peer or when a
 *          connection arrives from a peer" [DTN2].
 * 
 * @author María José Peroza Marval (mjpm@kth.se)
 */

public class Link implements Serializable {

	/**
	 * Unique identifier according to Java Serializable specification
	 */

	private static final long serialVersionUID = 8437187013472087581L;

	/**
	 * TAG for Android Logging mechanism
	 */
	private static final String TAG = "Link";

	public Link() {

		remote_eid_ = EndpointID.NULL_EID();
		stats_ = new LinkStats();
	}

	/**
	 * 
	 * Constructor (using a ConvergenceLayer)
	 */
	public Link(ConvergenceLayer cl) {
		String name = "generated_link_" + TimeHelper.current_seconds_from_ref();
		Log.d(TAG, "Link" + name);
		type_ = link_type_t.OPPORTUNISTIC;
		state_ = Link.state_t.UNAVAILABLE;
		deleted_ = false;
		create_pending_ = false;
		usable_ = true;
		nexthop_ = "";
		name_ = name;
		reliable_ = false;
		lock_ = new Lock();
		queue_ = new BundleList(name, lock_);
		inflight_ = new BundleList(name, lock_);
		bundles_queued_ = 0;
		bytes_queued_ = 0;
		bundles_inflight_ = 0;
		bytes_inflight_ = 0;
		contact_ = null;
		clayer_ = cl;
		cl_info_ = null;
		router_info_ = null;
		remote_eid_ = new EndpointID(EndpointID.NULL_EID());

		assert (clayer_ != null) : "Link : Link, clayer is null";

		params_ = new Params();
		retry_interval_ = 5; // set in ContactManager

		stats_ = new LinkStats();

		BundleDaemon.getInstance().contactmgr().links().add(this);

	}

	/**
	 * Valid types for a link.
	 */
	public enum link_type_t {
		LINK_INVALID(-1),

		/**
		 * The link is expected to be ALWAYS available, and any convergence
		 * layer connection state is always maintained for it.
		 */
		ALWAYSON(1),

		/**
		 * The link is expected to be either always available, or can be made
		 * available easily. Examples include DSL (always), and dialup (easily
		 * available). Convergence layers are free to tear down idle connection
		 * state, but are expected to be able to easily re-establish it.
		 */
		ONDEMAND(2),

		/**
		 * The link is only available at pre-determined times.
		 */
		SCHEDULED(3),

		/**
		 * The link may or may not be available, based on uncontrollable
		 * factors. Examples include a wireless link whose connectivity depends
		 * on the relative locations of the two nodes.
		 */
		OPPORTUNISTIC(4);

		private static final Map<Integer, link_type_t> lookup = new HashMap<Integer, link_type_t>();

		static {
			for (link_type_t s : EnumSet.allOf(link_type_t.class))
				lookup.put(s.getCode(), s);
		}

		private int code;

		private link_type_t(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static link_type_t get(int code) {
			return lookup.get(code);
		}

	}

	/**
	 * Link type string conversion.
	 */

	public static String link_type_to_str(link_type_t type) {
		switch (type) {
		case ALWAYSON:
			return "ALWAYSON";
		case ONDEMAND:
			return "ONDEMAND";
		case SCHEDULED:
			return "SCHEDULED";
		case OPPORTUNISTIC:
			return "OPPORTUNISTIC";
		default:
			Log.e(TAG, "Bogus link_type_t");
			return null;

		}
	}

	public static link_type_t str_to_link_type(String Str) {
		String A = "ALWAYSON";
		if (Str.compareTo(A) == 0)
			return link_type_t.ALWAYSON;

		A = "ONDEMAND";
		if (Str.compareTo(A) == 0)
			return link_type_t.ONDEMAND;

		A = "SCHEDULED";
		if (Str.compareTo(A) == 0)
			return link_type_t.SCHEDULED;

		A = "OPPORTUNISTIC";
		if (Str.compareTo(A) == 0)
			return link_type_t.OPPORTUNISTIC;

		return link_type_t.LINK_INVALID;
	}

	/**
	 * "The possible states for a link. These are defined as distinct bitfield
	 * values so that various functions can match on a set of states (e.g. see
	 * ContactManager::find_link_to)" [DTN2].
	 */

	public enum state_t {
		UNAVAILABLE(1), // /< "The link is closed and not able to be
		// / opened currently" [DTN2].

		AVAILABLE(2), // /< "The link is closed but is able to be
		// / opened, either because it is an on demand
		// / link, or because an opportunistic peer
		// / node is in close proximity but no
		// / convergence layer session has yet been
		// / opened" [DTN2].

		OPENING(4), // /< "A convergence layer session is in the
		// / process of being established" [DTN2].

		OPEN(8), // /< "A convergence layer session has been
		// / established, and the link has capacity
		// / for a bundle to be sent on it. This may
		// / be because no bundle is currently being
		// / sent, or because the convergence layer
		// / can handle multiple simultaneous bundle
		// / transmissions" [DTN2].

		CLOSED(16), // /< "Bogus state that's never actually used in
		// / the Link state_ variable, but is used for
		// / signalling the daemon thread with a
		// / LinkStateChangeRequest" [DTN2].

		ANY(0xFFFFFFFF);

		private static final Map<Integer, state_t> lookup = new HashMap<Integer, state_t>();

		static {
			for (state_t s : EnumSet.allOf(state_t.class))
				lookup.put(s.getCode(), s);
		}

		private int code;

		private state_t(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static state_t get(int code) {
			return lookup.get(code);
		}

	}

	/**
	 * Convert a link state into a string.
	 */

	public static String state_to_str(state_t state) {
		switch (state) {
		case UNAVAILABLE:
			return "UNAVAILABLE";
		case AVAILABLE:
			return "AVAILABLE";
		case OPENING:
			return "OPENING";
		case OPEN:
			return "OPEN";
		case CLOSED:
			return "CLOSED";
		default:
			Log.e(TAG, "Bogus state_t");
			return null;
		}

	}

	/**
	 * Static function to create appropriate link object from link type.
	 */

	public static Link create_link(String name, link_type_t type,
			ConvergenceLayer cl, String nexthop) {

		Link link;

		switch (type) {
		case ALWAYSON:
			link = new AlwaysOnLink(name, cl, nexthop);
			break;
		case ONDEMAND:
			link = new OndemandLink(name, cl, nexthop);
			break;
		case SCHEDULED:
			link = new ScheduledLink(name, cl, nexthop);
			break;
		case OPPORTUNISTIC:
			link = new OpportunisticLink(name, cl, nexthop);
			break;
		default:
			Log.d(TAG, "bogus type of the link" + name);
			link = null;

		}

		if (!link.clayer_.init_link(link)) {
			link.deleted_ = true;
			link = null;
			return link;
		}

		Log.i(TAG, "new link created");

		// "now dispatch to the subclass for any initial state events that
		// need to be posted. this needs to be done after all the above is
		// completed to avoid potential race conditions if the core of the
		// system tries to use the link before its completely created
		// MOVED to ContactManager::handle_link_created()
		// link->set_initial_state()" [DTN2].

		return link;
	}

	/**
	 * Constructor
	 */
	public Link(String name, link_type_t type, ConvergenceLayer cl,
			String nexthop) {

		Log.d(TAG, "Link" + name);
		type_ = type;
		state_ = Link.state_t.UNAVAILABLE;
		deleted_ = false;
		create_pending_ = false;
		usable_ = true;
		nexthop_ = nexthop;
		String[] parameters = nexthop.split(":", 2);
		try {
			dest_ip_ = InetAddress.getByName(parameters[0].replace("/", ""));
		} catch (IOException e) {
			Log.d(TAG, "IOException getting dest_ip");
		}
		remote_port_ = (short) Integer.parseInt(parameters[1]);
		name_ = name;
		reliable_ = false;
		lock_ = new Lock();
		queue_ = new BundleList(name, lock_);
		inflight_ = new BundleList(name, lock_);
		bundles_queued_ = 0;
		bytes_queued_ = 0;
		bundles_inflight_ = 0;
		bytes_inflight_ = 0;
		contact_ = null;
		clayer_ = cl;
		cl_info_ = null;
		router_info_ = null;
		remote_eid_ = new EndpointID(EndpointID.NULL_EID());

		assert (clayer_ != null) : "Link : Link, clayer is null";

		params_ = new Params();
		retry_interval_ = 5; // set in ContactManager

		stats_ = new LinkStats();

	}

	/**
	 * Handle and mark deleted link
	 */
	public void delete_link() {

		lock_.lock();
		try {
			assert (!isdeleted()) : "Link : delete_link, Proble deleting the link";
			assert (clayer_ != null) : "Link : delete_link, clayer is null";

			clayer_.delete_link(this);
			deleted_ = true;

		} finally {
			lock_.unlock();
		}
	}

	/**
	 * Reconfigure the link parameters.
	 */
	public boolean reconfigure_link(int argc, String argv) {

		lock_.lock();
		try {
			if (isdeleted()) {
				Log.d(TAG, "cannot reconfigure deleted link" + name());
				return false;
			}

			assert (clayer_ != null) : "Link : reconfigure_link, clayer is null";

			return clayer_.reconfigure_link(this, argc, argv);

		} finally {
			lock_.unlock();
		}
	}

	public void reconfigure_link(AttributeVector params) {

		lock_.lock();
		try {
			if (isdeleted()) {
				Log.d(TAG, "reconfigure_link: cannot reconfigure deleted link"
						+ name());
				return;
			}

			Iterator<NamedAttribute> iter = params.iterator();
			NamedAttribute NA;
			while (iter.hasNext()) {
				NA = iter.next();

				if (NA.name() == "is_usable") {
					if (NA.bool_val()) {
						set_usable(true);
					} else {
						set_usable(false);
					}

				} else if (NA.name() == "nexthop") {
					set_nexthop(NA.string_val());

					// Following are DTN2 parameters not listed in the DP
					// interface.
				} else if (NA.name() == "min_retry_interval") {
					params_.min_retry_interval_ = NA.int_val();

				} else if (NA.name() == "max_retry_interval") {
					params_.max_retry_interval_ = NA.int_val();

				} else if (NA.name() == "idle_close_time") {
					params_.idle_close_time_ = NA.int_val();

				} else if (NA.name() == "potential_downtime") {
					params_.potential_downtime_ = NA.int_val();

				}

			}

			assert (clayer_ != null) : "Link : reconfigure_link (AV), clayer is null";
			clayer_.reconfigure_link(this, params);

			return;

		} finally {
			lock_.unlock();
		}
	}

	/**
	 * Hook for subclass to post events to control the initial link state, after
	 * all initialization is complete.
	 */
	public void set_initial_state() {
	}

	/**
	 * Return the type of the link.
	 */
	public link_type_t type() {
		return type_;
	}

	/**
	 * Return the string for of the link.
	 */
	public String type_str() {
		return link_type_to_str(type_);
	}

	/**
	 * Return whether or not the link is open.
	 */
	public boolean isopen() {
		return ((state_ == state_t.OPEN));
	}

	/**
	 * Return the availability state of the link.
	 */
	public boolean isNotUnavailable() {
		return (state_ != state_t.UNAVAILABLE);
	}

	/**
	 * Return whether the link is in the process of opening.
	 */
	public boolean isopening() {
		return (state_ == state_t.OPENING);
	}

	/**
	 * Returns true if the link has been deleted; otherwise returns false.
	 */
	public boolean isdeleted() {
		return deleted_;
	}

	/**
	 * Return the actual state.
	 */
	public state_t state() {
		return (state_);
	}

	/**
	 * "Sets the state of the link. Performs various assertions to ensure the
	 * state transitions are legal.
	 * 
	 * This function should only ever be called by the main BundleDaemon thread
	 * and helper classes. All other threads must use a LinkStateChangeRequest
	 * event to cause changes in the link state.
	 * 
	 * The function isn't protected since some callers (i.e. convergence layers)
	 * are not friend classes but some functions are run by the BundleDaemon
	 * thread" [DTN2].
	 */
	public void set_state(state_t new_state) {

		switch (new_state) {
		case UNAVAILABLE:
			break; // any old state is valid

		case AVAILABLE:
			assert (state_ == state_t.OPEN || state_ == state_t.UNAVAILABLE) : Log
					.d(TAG, "The state of the Link can not be changed to"
							+ state_to_str(new_state));
			break;

		case OPENING:
			assert (state_ == state_t.AVAILABLE || state_ == state_t.UNAVAILABLE) : Log
					.d(TAG, "The state of the Link can not be changed to"
							+ state_to_str(new_state));
			break;

		case OPEN:
			/*
			 * for opportunisticlinks
			 */
			assert (state_ == state_t.OPENING || state_ == state_t.UNAVAILABLE) : Log
					.d(TAG, "The state of the Link can not be changed to"
							+ state_to_str(new_state));
			break;

		default:
			Log.e(TAG, "Not Reached");
		}

		state_ = new_state;

	}

	/**
	 * Set/get the create_pending_ flag on the link.
	 */
	public void set_create_pending(boolean create_pending) {

		create_pending = true;
		create_pending_ = create_pending;
	}

	public boolean is_create_pending() {
		return create_pending_;
	}

	/**
	 * Set/get the usable_ flag on the link.
	 */
	public void set_usable(boolean usable) {

		usable_ = usable;
	}

	public boolean is_usable() {
		return usable_;
	}

	/**
	 * Return the current contact information (if any).
	 */
	public Contact contact() {
		return contact_;
	}

	/**
	 * Set the contact information.
	 */
	public void set_contact(Contact contact) {
		contact_ = contact;
	}

	/**
	 * Store convergence layer state associated with the link.
	 */
	public void set_cl_info(CLInfo cl_info) {
		assert cl_info_ == null && cl_info != null : cl_info_ != null
				&& cl_info == null;

		cl_info_ = cl_info;
	}

	/**
	 * Accessor to the convergence layer state.
	 */
	public CLInfo cl_info() {
		return cl_info_;
	}

	/**
	 * Store router state associated with the link.
	 */
	public void set_router_info(RouterInfo router_info) {
		assert router_info_ == null && router_info != null : router_info_ != null
				&& router_info == null;

		router_info_ = router_info;
	}

	/**
	 * Accessor to the convergence layer state.
	 */
	public RouterInfo router_info() {
		return router_info_;
	}

	/**
	 * Accessor to this contact's convergence layer.
	 */
	public ConvergenceLayer clayer() {
		return clayer_;
	}

	/**
	 * Accessor to this links name
	 */
	public String name() {
		return name_;
	}

	/**
	 * Accessor to this links name as a c++ string
	 */
	public String name_str() {
		return name_;
	}

	/**
	 * Accessor to next hop string
	 */
	public String nexthop() {
		return nexthop_;
	}

	/**
	 * Accessor to next hop string
	 */
	public String nexthop_str() {
		return nexthop_;
	}

	/**
	 * Override for the next hop string.
	 */
	public void set_nexthop(String nexthop) {
		nexthop_ = nexthop;
	}

	/**
	 * Accessor to the reliability bit.
	 */
	public boolean is_reliable() {
		return reliable_;
	}

	/**
	 * Accessor to set the reliability bit when the link is created.
	 */
	public void set_reliable(boolean r) {
		reliable_ = r;
	}

	/**
	 * Accessor to set the remote endpoint id.
	 */

	public void set_remote_eid(EndpointID remote) {

		remote_eid_.assign(remote);
	}

	/**
	 * Accessor to the remote endpoint id.
	 */
	public EndpointID remote_eid() {
		return remote_eid_;
	}

	/**
	 * Accessor for the link's queue of bundles that are awaiting transmission.
	 */
	public BundleList queue() {
		return queue_;
	}

	/**
	 * Return whether or not the queue is full, based on the configured queue
	 * limits.
	 */
	public boolean queue_is_full() {

		return ((bundles_queued_ > params_.qlimit_bundles_high_) || (bytes_queued_ > params_.qlimit_bytes_high_));

	}

	/**
	 * Return whether or not the queue has space, based on the configured queue
	 * limits.
	 */
	public boolean queue_has_space() {

		return ((bundles_queued_ < params_.qlimit_bundles_low_) && (bytes_queued_ < params_.qlimit_bytes_low_));

	}

	/**
	 * Accessor for the link's list of bundles that have been transmitted but
	 * for which the convergence layer is awaiting acknowledgement.
	 */
	public BundleList inflight() {
		return inflight_;
	}

	/**
	 * Accessor functions to add/remove bundles from the link queue and inflight
	 * list, keeping the statistics in-sync with the state of the lists.
	 */
	public boolean add_to_queue(Bundle bundle, int total_len) {

		lock_.lock();
		try {

			if (queue_.contains(bundle)) {
				String text = String
						.format(
								"add_to_queue: bundle id %d already in queue for link %s",
								bundle.bundleid(), name_str());
				Log.e(TAG, text);
				return false;
			}

			String text = String.format(
					"adding bundle %d to queue (length %d)", bundle.bundleid(),
					bundles_queued_);
			Log.d(TAG, text);

			bundles_queued_++;
			bytes_queued_ += total_len;

			queue_.push_back(bundle);

			return true;
		} finally {
			lock_.unlock();
		}

	}

	public boolean del_from_queue(Bundle bundle, int total_len) {

		lock_.lock();
		try {
			if (!queue_.erase(bundle, false)) {
				return false;
			}

			assert (bundles_queued_ > 0) : "Link : del_from_queue, bundles_quued is less than 0";
			bundles_queued_--;

			// sanity checks
			assert (total_len != 0) : "Link : del_from_queue, total_len is equal to zero";
			if (bytes_queued_ >= total_len) {
				bytes_queued_ -= total_len;

			} else {
				String text = String.format(
						"del_from_queue: %s bytes_queued %s < total_len %s",
						bundle, bytes_queued_, total_len);
				Log.e(TAG, text);
			}

			String text = String.format("removed %s from queue (length %s)",
					bundle, bundles_queued_);
			Log.d(TAG, text);

			return true;
		} finally {
			lock_.unlock();
		}
	}

	public boolean add_to_inflight(Bundle bundle, int total_len) {

		lock_.lock();
		try {
			if (bundle.is_queued_on(inflight_)) {
				String text = String.format(
						"bundle %s already in flight for link %s", bundle,
						name_str());
				Log.e(TAG, text);
				return false;
			}

			String text = String.format(
					"adding %s to in flight list for link %s", bundle,
					name_str());
			Log.d(TAG, text);

			inflight_.push_back(bundle);

			bundles_inflight_++;
			bytes_inflight_ += total_len;

			return true;
		} finally {
			lock_.unlock();
		}

	}

	public boolean del_from_inflight(Bundle bundle, int total_len) {

		lock_.lock();

		try {
			// free because this is at the end
			if (!inflight_.erase(bundle, false)) {
				return false;
			}

			assert (bundles_inflight_ > 0) : "Link : del_from_inflight, bundles_inflight is less than zero";
			bundles_inflight_--;

			// sanity checks
			assert (total_len != 0) : "Link : del_from_inflight, total_len is zero";
			if (bytes_inflight_ >= total_len) {
				bytes_inflight_ -= total_len;

			} else {
				String text = String
						.format(
								"del_from_inflight: %s bytes_inflight %s < total_len %s",
								bundle, bytes_inflight_, total_len);
				Log.e(TAG, text);
			}

			String text = String.format(
					"removed %s from inflight list (length %s)", bundle,
					bundles_inflight_);
			Log.d(TAG, text);

			return true;
		} finally {
			lock_.unlock();

		}
	}

	/**
	 * Virtual from formatter
	 */
	public int format(StringBuffer buf, int sz) {

		String text = String.format("%s [%s %s %s %s state=%s]", name(),
				nexthop(), remote_eid(), link_type_to_str(type()), clayer_
						.name(), state_to_str(state()));
		buf.append(text);
		return text.length();

	}

	/**
	 * Debugging printout.
	 */
	public void dump(StringBuffer buf) {

		lock_.lock();

		try {

			if (isdeleted()) {
				Log.d(TAG, "Link.dump: cannot dump deleted link %s" + name());
				return;
			}

			String text = String
					.format(
							"Link %s:\n clayer: %s\n type: %s\n state: %s\n nexthop: %s\n remote eid: "
									+ "%s\n mtu: %s\n min_retry_interval: %s\n max_retry_interval: %s\n idle_close_time: %s\n "
									+ "potential_downtime: %s\n prevhop_hdr: %s\n",
							name(), clayer_.name(), link_type_to_str(type()),
							state_to_str(state()), nexthop(), remote_eid(),
							params_.mtu_, params_.min_retry_interval_,
							params_.max_retry_interval_,
							params_.idle_close_time_,
							params_.potential_downtime_);

			text = text.concat(String.valueOf(deleted_));
			text = text.concat(String.valueOf(params_.prevhop_hdr_));

			buf.append(text);

			assert (clayer_ != null) : "Link : dump, clayer_ is null";
			clayer_.dump_link(this, buf);
		} finally {
			lock_.unlock();
		}
	}

	/**
	 * Reset the stats.
	 */
	public void reset_stats() {
		stats_.set_contact_attempts(0);
		stats_.set_contacts(0);
		stats_.set_bundles_transmitted(0);
		stats_.set_bytes_transmitted(0);
		stats_.set_bundles_cancelled(0);
		stats_.set_uptime(0);
		stats_.set_availability(0);
		stats_.set_reliability(0);
	}

	/**************************************************************
	 * Link Parameters
	 */
	static public class Params {
		/**
		 * Default constructor.
		 */
		Params() {

			if (DTNService.context().getResources().getString(
					R.string.DTNEnableProactiveFragmentation).equals("true")) {
				mtu_ = Integer.parseInt(DTNService.context().getResources()
						.getString(R.string.DTNFragmentationMTU));
			} else {
				mtu_ = 0;
			}

			min_retry_interval_ = 3;
			max_retry_interval_ = 3;
			idle_close_time_ = 30;

			potential_downtime_ = 30;
			prevhop_hdr_ = false;
			cost_ = 100;
			qlimit_bundles_high_ = 10;
			qlimit_bytes_high_ = 1024 * 1024; // 1M
			qlimit_bundles_low_ = 5;
			qlimit_bytes_low_ = (512 * 1024); // 512K
		}

		/**
		 * MTU of the link, used to control proactive fragmentation.
		 */
		private int mtu_;

		public int mtu() {
			return mtu_;
		}

		public void set_mtu(int mtu) {
			mtu_ = mtu;
		}

		/**
		 * "Minimum amount to wait between attempts to re-open the link (in
		 * seconds).
		 * 
		 * Default is set by the various Link types but can be overridden by
		 * configuration parameters" [DTN2].
		 */
		private int min_retry_interval_;

		public int min_retry_interval() {
			return min_retry_interval_;
		}

		public void set_min_retry_interval(int minRetryInterval) {
			min_retry_interval_ = minRetryInterval;
		}

		/**
		 * "Maximum amount to wait between attempts to re-open the link (in
		 * seconds).
		 * 
		 * Default is set by the various Link types but can be overridden by
		 * configuration parameters" [DTN2].
		 */
		private int max_retry_interval_;

		public int max_retry_interval() {
			return max_retry_interval_;
		}

		public void set_max_retry_interval(int maxRetryInterval) {
			max_retry_interval_ = maxRetryInterval;
		}

		/**
		 * "Seconds of idle time before the link is closed. Must be zero for
		 * always on links (i.e. they are never closed).
		 * 
		 * Default is 30 seconds for on demand links, zero for opportunistic
		 * links" [DTN2].
		 */
		private int idle_close_time_;

		public int idle_close_time() {
			return idle_close_time_;
		}

		public void set_idle_close_time(int idleCloseTime) {
			idle_close_time_ = idleCloseTime;
		}

		/**
		 * "Conservative estimate of the maximum amount of time that the link
		 * may be down during "normal" operation. Used by routing algorithms to
		 * determine how long to leave bundles queued on the down link before
		 * rerouting them. Fefault is 30 seconds".
		 */
		private int potential_downtime_;

		public int potential_downtime() {
			return potential_downtime_;
		}

		public void set_potential_downtime(int potentialDowntime) {
			potential_downtime_ = potentialDowntime;
		}

		/**
		 * "Whether or not to send the previous hop header on this link. Default
		 * is false" [DTN2].
		 */
		private boolean prevhop_hdr_;

		public boolean prevhop_hdr() {
			return prevhop_hdr_;
		}

		public void set_prevhop_hdr(boolean prevhopHdr) {
			prevhop_hdr_ = prevhopHdr;
		}

		/**
		 * "Abstract cost of the link, used by routing algorithms. Default is
		 * 100" [DTN2].
		 */
		private int cost_;

		public int cost() {
			return cost_;
		}

		public void set_cost(int cost) {
			cost_ = cost;
		}

		/**
		 * @{
		 * 
		 *    "Configurable high / low limits on the number of bundles/bytes
		 *    that should be queued on the link.
		 * 
		 *    The high limits are used by Link::is_queue_full() to indicate
		 *    whether or not more bundles can be queued onto the link to effect
		 *    backpressure from the convergence layers.
		 * 
		 *    The low limits can be used by the router to determine when to
		 *    re-scan the pending bundle lists" [DTN2].
		 */
		private int qlimit_bundles_high_;

		public int qlimit_bundles_high() {
			return qlimit_bundles_high_;
		}

		public void set_qlimit_bundles_high(int qlimitBundlesHigh) {
			qlimit_bundles_high_ = qlimitBundlesHigh;
		}

		private long qlimit_bytes_high_;

		public long qlimit_bytes_high() {
			return qlimit_bytes_high_;
		}

		public void set_qlimit_bytes_high(long qlimitBytesHigh) {
			qlimit_bytes_high_ = qlimitBytesHigh;
		}

		private int qlimit_bundles_low_;

		public int qlimit_bundles_low() {
			return qlimit_bundles_low_;
		}

		public void set_qlimit_bundles_low(int qlimitBundlesLow) {
			qlimit_bundles_low_ = qlimitBundlesLow;
		}

		private long qlimit_bytes_low_;

		public long qlimit_bytes_low() {
			return qlimit_bytes_low_;
		}

		public void set_qlimit_bytes_low(long qlimitBytesLow) {
			qlimit_bytes_low_ = qlimitBytesLow;
		}
	};

	/**
	 * "Seconds to wait between attempts to re-open an unavailable link.
	 * Initially set to min_retry_interval_, then doubles up to
	 * max_retry_interval_" [DTN2].
	 */
	private int retry_interval_;

	public int retry_interval() {
		return retry_interval_;
	}

	public void set_retry_interval(int retry_interval) {
		retry_interval_ = retry_interval;
	}

	/**
	 * Accessor for the parameter class.
	 */

	public Params params() {
		return params_;
	}

	/**
	 * Accessor for the stats class.
	 */
	public LinkStats stats_;

	public LinkStats stats() {
		return stats_;
	}

	/**
	 * Dump a printable version of the stats.
	 */
	public void dump_stats(StringBuffer buf) {

		lock_.lock();

		if (isdeleted()) {
			Log.d(TAG, "Link.dump_stats: cannot dump stats for deleted link %s"
					+ name());
			return;
		}

		long uptime = stats_.uptime();
		if (contact_ != null) {
			uptime += TimeHelper.elapsed_ms(contact_.start_time_);
		}

		long throughput = 0;
		if (uptime != 0) {
			throughput = (stats_.bytes_transmitted() * 8) / uptime;
		}

		String text = String
				.format(
						"%s contact_attempts -- %s contacts -- %s bundles_transmitted -- %s bytes_transmitted -- "
								+ "%s bundles_queued -- %s bytes_queued -- %s bundles_inflight -- %s bytes_inflight -- "
								+ "%s bundles_cancelled -- %s uptime -- "
								+ "%s throughput_bps", stats_
								.contact_attempts(), stats_.contacts(), stats_
								.bundles_transmitted(), stats_
								.bytes_transmitted(), bundles_queued_,
						bytes_queued_, bundles_inflight_, bytes_inflight_,
						stats_.bundles_cancelled(), uptime, throughput);

		buf.append(text);

		if (router_info_ != null) {
			router_info_.dump_stats(buf);
		}

	}

	/*
	 * Accessor for the bundles queue
	 */

	public int bundles_queued() {
		return bundles_queued_;
	}

	/*
	 * Accessors for the destination ip
	 */
	public InetAddress dest_ip() {
		return dest_ip_;
	}

	/*
	 * Accessors for the remote port
	 */
	public short remote_port() {
		return remote_port_;
	}

	/*
	 * Accessors for the bytes queue
	 */
	public int bytes_queued() {
		return bytes_queued_;
	}

	/*
	 * Accessors for the bundles inflight
	 */
	public int bundles_inflight() {
		return bundles_inflight_;
	}

	/*
	 * Accessors for the bytes inflight
	 */
	public int bytes_inflight() {
		return bytes_inflight_;
	}

	/**
	 * Accessor for the Link state lock.
	 */
	public Lock get_lock() {
		return lock_;
	}

	/**
	 * Open the link.
	 */
	public void open() {

		assert (!isdeleted());

		if (state_ != state_t.AVAILABLE) {
			String text = String.format(
					"Link.open: in state %s: expected state AVAILABLE",
					state_to_str(state()));
			Log.e(TAG, text);
			return;
		}

		set_state(state_t.OPENING);

		// "tell the convergence layer to establish a new session however
		// it needs to, it will set the Link state to OPEN and post a
		// ContactUpEvent when it has done the deed" [DTN2].
		assert (contact_ == null) : "Link : open, contact_ is no null";
		contact_ = new Contact(this);
		clayer().open_contact(contact_);

		stats_.set_contact_attempts(stats_.contact_attempts() + 1);

		String text = String.format("Link.open: %s new contact %s", this,
				contact_);
		Log.d(TAG, text);

	}

	/**
	 * Close the link.
	 */
	public void close() {

		Log.d(TAG, "Closing the link");

		// we should always be open, therefore we must have a contact
		if (contact_ == null) {
			Log.e(TAG, "Link.close with no contact");
			return;
		}

		// "Kick the convergence layer to close the contact and make sure
		// it cleaned up its state" [DTN2].
		clayer().close_contact(contact_);
		assert (contact_.cl_info() == null) : "Link : close, cl_info is not null";

		// "Remove the reference from the link, which will clean up the
		// object eventually" [DTN2].
		contact_ = null;

		Log.d(TAG, "Link.close complete");

	}

	/*
	 * Set/get for the link counter (number of active links)
	 */
	public static int link_counter() {
		return link_counter_;
	}

	public static void set_link_counter(int linkCounter) {
		link_counter_ = linkCounter;
	}

	// / Type of the link
	protected link_type_t type_;

	// / State of the link
	protected state_t state_;

	// / "Flag, that when set to true, indicates that the link has been deleted"
	// [DTN2].
	protected boolean deleted_;

	// / "Flag, that when set to true, indicates that the creation of the
	// / link is pending; the convergence layer will post a creation event
	// / when the creation is complete. While creation is pending, the
	// / link cannot be opened nor can bundles be queued for it" [DTN2].
	protected boolean create_pending_;

	// / "Flag, that when set to true, indicates that the link is allowed
	// / to be used to transmit bundles" [DTN2].
	protected boolean usable_;

	// / Next hop address
	protected String nexthop_;

	// / Internal name of the link
	protected String name_;

	// / Whether or not this link is reliable
	protected boolean reliable_;

	// / Parameters of the link
	protected Params params_;

	// / Default parameters of the link
	protected static Params default_params_;

	// / Lock to protect internal data structures and state.
	private Lock lock_;

	public Lock lock() {
		return lock_;
	}

	// / Queue of bundles currently active or pending transmission on the Link
	protected BundleList queue_;

	// / Queue of bundles that have been sent but not yet acknowledged
	protected BundleList inflight_;

	/**
	 * @{
	 * 
	 *    "Data counters about the link queues, both in terms of bundles and
	 *    bytes.
	 * 
	 *    *_queued: the link queue size *_inflight: transmitted but not yet
	 *    acknowledged" [DTN2].
	 */
	protected int bundles_queued_;
	protected int bytes_queued_;
	protected int bundles_inflight_;
	protected int bytes_inflight_;

	// / Current contact. contact_ != null if link is open
	protected Contact contact_;

	// / Pointer to convergence layer
	protected ConvergenceLayer clayer_;

	// / Convergence layer specific info, if needed
	protected CLInfo cl_info_;

	// / Router specific info, if needed
	protected RouterInfo router_info_;

	// / Remote's endpoint ID (eg, dtn://hostname.dtn)
	protected EndpointID remote_eid_;

	// / Destination ip
	protected InetAddress dest_ip_;

	// / Remote port
	protected short remote_port_;

	// / Counter of active links
	private static int link_counter_;

}
