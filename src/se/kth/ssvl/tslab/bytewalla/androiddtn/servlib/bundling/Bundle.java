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

package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.exception.BundleLockNotHeldByCurrentThread;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.storage.BundleStore;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.Lock;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.Set;
import android.util.Log;

/**
 * Class to represent DTN Bundle
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */

public class Bundle implements Serializable {

	/**
	 * String TAG for using with Android Logging system
	 */
	private static String TAG = "Bundle";

	/**
	 * Serial UID to support Java Serializable
	 */
	private static final long serialVersionUID = 6639315731778715222L;

	/**
	 * Default constructor to create an empty bundle, initializing all fields to
	 * defaults and allocating a new bundle id.
	 * 
	 * For temporary bundles, the location can be set to MEMORY
	 */
	public Bundle(BundlePayload.location_t location) {
		
		
		int id = BundleStore.getInstance().next_id();
		init(id);

		payload_ = new BundlePayload(lock_);
		payload_.init(this.bundleid_, location);

		creation_ts_ = new BundleTimestamp(id);
		// Only add to persistant storage when the location to store is disk
		if (location == BundlePayload.location_t.DISK) {
			BundleStore.getInstance().add(this);
		}

	}


	/**
	 * Copy the metadata from one bundle to another (used in fragmentation).
	 */
	public final void copy_metadata(Bundle new_bundle) {
		new_bundle.is_admin_ = is_admin_;
		new_bundle.is_fragment_ = is_fragment_;
		new_bundle.do_not_fragment_ = do_not_fragment_;
		new_bundle.source_.assign(source_);
		new_bundle.dest_.assign(dest_);
		new_bundle.custodian_.assign(custodian_);
		new_bundle.replyto_.assign(replyto_);
		new_bundle.prevhop_.assign(prevhop_);
		new_bundle.priority_ = priority_;
		new_bundle.custody_requested_ = custody_requested_;
		new_bundle.local_custody_ = false;
		new_bundle.singleton_dest_ = singleton_dest_;
		new_bundle.custody_rcpt_ = custody_rcpt_;
		new_bundle.receive_rcpt_ = receive_rcpt_;
		new_bundle.forward_rcpt_ = forward_rcpt_;
		new_bundle.delivery_rcpt_ = delivery_rcpt_;
		new_bundle.deletion_rcpt_ = deletion_rcpt_;
		new_bundle.app_acked_rcpt_ = app_acked_rcpt_;
		new_bundle.creation_ts_ = creation_ts_;
		new_bundle.expiration_ = expiration_;
	}

	/**
	 * Format the data to StringBuffer briefly
	 */
	public final void format(StringBuffer buf) {
		if (is_admin()) {
			buf.append(String.format(
					"bundle id %d [%s -> %s %d byte payload, is_admin]",
					bundleid_, source_.uri().toString(),
					dest_.uri().toString(), payload_.length()));
		} else if (is_fragment()) {
			buf.append(String.format(
					"bundle id %d [%s -> %s %d byte payload, fragment @%d/%d]",
					bundleid_, source_.toString(), dest_.toString(), payload_
							.length(), frag_offset_, orig_length_));
		} else {
			buf.append(String.format("bundle id %d [%s -> %s %d byte payload]",
					bundleid_, source_.toString(), dest_.toString(), payload_
							.length()));
		}
	}

	/**
	 * Format the data to StringBuffer verbosely
	 */
	public void format_verbose(StringBuffer buf) {
		buf.append(String.format("bundle id %d:\n", bundleid_));
		buf.append(String.format("            source: %s\n", source_.str()));
		buf.append(String.format("              dest: %s\n", dest_.str()));
		buf.append(String.format("         custodian: %s\n", custodian_.str()));
		buf.append(String.format("           replyto: %s\n", replyto_.str()));
		buf.append(String.format("           prevhop: %s\n", prevhop_.str()));
		buf.append(String.format("    payload_length: %d bytes\n", payload_
				.length()));
		buf.append(String.format("          priority: %s\n", priority_
				.toString()));
		buf.append(String.format(" custody_requested: %s\n",
				(custody_requested_)));
		buf.append(String.format("     local_custody: %s\n", local_custody_));
		buf.append(String.format("    singleton_dest: %s\n", singleton_dest_));
		buf.append(String.format("      receive_rcpt: %s\n", receive_rcpt_));
		buf.append(String.format("      custody_rcpt: %s\n", custody_rcpt_));
		buf.append(String.format("      forward_rcpt: %s\n", forward_rcpt_));
		buf.append(String.format("     delivery_rcpt: %s\n", delivery_rcpt_));
		buf.append(String.format("     deletion_rcpt: %s\n", deletion_rcpt_));
		buf.append(String.format("    app_acked_rcpt: %s\n", app_acked_rcpt_));
		buf.append(String.format("       creation_ts: %d.%d\n", creation_ts_
				.seconds(), creation_ts_.seqno()));
		buf.append(String.format("        expiration: %d \n", expiration_));
		buf.append(String.format("       is_fragment: %s\n", is_fragment_));
		buf.append(String.format("          is_admin: %s\n", is_admin_));
		buf.append(String.format("   do_not_fragment: %s\n", do_not_fragment_));
		buf.append(String.format("       orig_length: %d\n", orig_length_));
		buf.append(String.format("       frag_offset: %d\n", frag_offset_));
		buf.append("\n");

		buf.append("forwarding log:\n");
		fwdlog_.dump(buf);
		buf.append("\n");

		lock_.lock();
		try {
			buf
					.append(String.format("queued on %d lists:\n", mappings_
							.size()));
			Iterator<BundleList> map_itr = mappings_.iterator();
			while (map_itr.hasNext()) {
				BundleList list = map_itr.next();
				buf.append(String.format("\t%s\n", list.name()));
			}

			buf.append("\nblocks:");

			Iterator<BlockInfo> block_itr = recv_blocks_.iterator();
			while (block_itr.hasNext()) {
				BlockInfo block = block_itr.next();
				buf.append(String.format("type %s", block.type().toString()));
				if (block.data_offset() == 0)
					buf.append("(runt)");
				else {
					if (!block.complete()) {
						buf.append("(incomplete)");

					}
					buf.append(String.format("data length: %d", block
							.full_length()));
				}
			}

		} finally {
			lock_.unlock();
		}

		buf.append("\n");
	}

	/**
	 * Storage key (ID) in the database
	 */
	public int durable_key() {
		return bundleid_;
	}

	/**
	 * Size of the data stored
	 */
	public long durable_size() {
		return payload_.length();
	}

	

	/**
	 * Setter for the custody timer of this Bundle
	 * @param custody_timers
	 */
	public void set_custody_timers(CustodyTimerVec custody_timers) {
		custody_timers_ = custody_timers;
	}

	/**
	 * Return the number of mappings for this bundle.
	 */
	public int num_mappings() {
		lock_.lock();
		try {
			return mappings_.size();
		} finally {
			lock_.unlock();
		}
	}

	/**
	 * Return a pointer to the mappings. Requires that the bundle be locked.
	 * @throws BundleLockNotHeldByCurrentThread
	 */
	public Set<BundleList> mappings() throws BundleLockNotHeldByCurrentThread {
		if (lock_.isHeldByCurrentThread())
			return mappings_;
		else
			throw new BundleLockNotHeldByCurrentThread();
	}
	
	/**
	 * Setter for the mapping of this Bundle
	 * @param mappings
	 */
	public void set_mappings(Set<BundleList> mappings) {
		lock_.lock();
		try
		{
			mappings_ = mappings;
		}
		finally
		{
			lock_.unlock();
		}
	}
	
	/**
	 * Getter for the mapping copy of this Bundle
	 * @return
	 */
	public Set<BundleList> get_mappings_copy()
	{
		lock_.lock();
		try
		{
			Set<BundleList> copy_of_mappings = new Set<BundleList>();
			copy_of_mappings.addAll(mappings_);
			return copy_of_mappings;
		}
		finally
		{
			lock_.unlock();
		}
	}
	

	/**
	 * Return true if the bundle is on the given list.
	 */
	public boolean is_queued_on(final BundleList l) {
		lock_.lock();
		try {
			return mappings_.contains(l);
		} finally {
			lock_.unlock();
		}
	}

	/**
	 * Validate the bundle's fields
	 */
	public boolean validate(StringBuffer errbuf) {
		if (!source_.valid()) {
			errbuf.append(String.format("invalid source eid [%s]", source_
					.toString()));
			return false;
		}

		if (!dest_.valid()) {
			errbuf.append(String.format("invalid dest eid [%s]", dest_
					.toString()));
			return false;
		}

		if (!replyto_.valid()) {
			errbuf.append(String.format("invalid replyto eid [%s]", replyto_
					.toString()));
			return false;
		}

		if (!custodian_.valid()) {
			errbuf.append(String.format("invalid custodian eid [%s]",
					custodian_.toString()));
			return false;
		}

		return true;
	}

	/**
	 * True if any return receipt fields are set
	 */
	public final boolean receipt_requested() {
		return (receive_rcpt_ || custody_rcpt_ || forward_rcpt_
				|| delivery_rcpt_ || deletion_rcpt_);
	}

	/**
	 * Values for the bundle priority field.
	 */
	public enum priority_values_t{
		COS_INVALID("_UNKNOWN_PRIORITY_", -1), // /< invalid
		COS_BULK("BULK", 0), // /< lowest priority
		COS_NORMAL("NORMAL", 1), // /< regular priority
		COS_EXPEDITED("EXPEDITED", 2), // /< important
		COS_RESERVED("_RESERVE", 3); // /< TBD

		private static final Map<priority_values_t, String> captionLookup = new HashMap<priority_values_t, String>();
		private static final Map<Integer, priority_values_t> codeLookup = new HashMap<Integer, priority_values_t>();

		static {
			for (priority_values_t event : EnumSet
					.allOf(priority_values_t.class)) {
				captionLookup.put(event, event.getCaption());
				codeLookup.put(event.getCode(), event);
			}
		}

		private String caption;
		private int code = -1; // -1 = undefined

		private priority_values_t(String caption) {
			this.caption = caption;
		}

		private priority_values_t(String caption, int code) {
			this.caption = caption;
			this.code = code;
		}

		public String getCaption() {
			return caption;
		}

		// Keep the name similarity in DTN2
		public String event_to_str() {
			return caption;
		}

		public Integer getCode() {
			return code;
		}

		public static priority_values_t get(int code) {
			return codeLookup.get(code);
		}

		public static String get(priority_values_t event) {
			return captionLookup.get(event);
		}
	}

	/**
	 * Getter for the bundle ID field in the database
	 * @return
	 */
	public final int bundleid() {
		return bundleid_;
	}

	/**
	 * Getter for the lock used in this Bundle
	 * @return
	 */
	public final Lock get_lock() {
		return lock_;
	}

	/**
	 * Getter for the flag whether this Bundle is expired
	 * @return
	 */
	public final boolean expired() {
		return expiration_timer_ == null;
	}

	/**
	 * Getter for the source EndpointID of this Bundle
	 * @return
	 */
	public final EndpointID source() {
		return source_;
	}

	/**
	 * Setter for the source EndpointID of this Bundle 
	 * @param source
	 */
	public final void set_source(EndpointID source) {
		source_ = source;
	}

	/**
	 * Getter for the destination EndpointID of this Bundle
	 * @return
	 */
	public final EndpointID dest() {
		return dest_;
	}

	/**
	 * Setter for the destination EndpointID of this Bundle
	 * @param dest
	 */
	public final void set_dest(EndpointID dest) {
		dest_ = dest;
	}

	/**
	 * Getter for the custodian EndpointID of this Bundle
	 * @return
	 */
	public final EndpointID custodian() {
		return custodian_;
	}

	/**
	 * Setter for the custodian EndpointID of this Bundle
	 * @param custodian
	 */
	public final void set_custodian(EndpointID custodian) {
		custodian_ = custodian;
	}
	
	/**
	 * Getter for the replyto EndpointID of this Bundle
	 * @return
	 */
	public final EndpointID replyto() {
		return replyto_;
	}

	/**
	 * Setter for the replyto EndpointID of this Bundle
	 * @param replyto
	 */
	public final void set_replyto(EndpointID replyto) {
		replyto_ = replyto;
	}

	/**
	 * Getter for the previous hop EndpointID
	 * @return
	 */
	public final EndpointID prevhop() {
		return prevhop_;
	}

	/**
	 * Setter for the previous hop EndpointID
	 * @param prevhop
	 */
	public final void set_prevhop(EndpointID prevhop) {
		prevhop_ = prevhop;
	}

	/**
	 * Getter for the flag whether this Bundle is a fragment of another Bundle
	 * @return
	 */
	public boolean is_fragment() {
		return is_fragment_;
	}

	/**
	 * Getter for the flag whether this Bundle is admin Bundle, for example, Bundle Status Report and Custody Signal Bundle.
	 * @return
	 */
	public boolean is_admin() {
		return is_admin_;
	}

	/**
	 * Getter for the flag whether this Bundle shouldn't be fragmented
	 * @return
	 */
	public boolean do_not_fragment() {
		return do_not_fragment_;
	}

	/**
	 * Getter for the flag whether this Bundle was requested for Custody
	 * @return
	 */
	public boolean custody_requested() {
		return custody_requested_;
	}

	/**
	 * Getter for the flag whether this Bundle have Singleton destination
	 * @return
	 */
	public boolean singleton_dest() {
		return singleton_dest_;
	}

	/**
	 * Getter for the priority value of this Bundle
	 * @return
	 */
	public priority_values_t priority() {
		return priority_;
	}

	/**
	 * Getter for the flag indicating whether the sender of this Bundle would like to 
	 * receive receipt
	 * @return
	 */
	public boolean receive_rcpt() {
		return receive_rcpt_;
	}

	/**
	 * Getter for the flag indicating whether the sender would like to know about custody status
	 * @return
	 */
	public boolean custody_rcpt() {
		return custody_rcpt_;
	}

	/**
	 * Getter for the forwarding receipt flag
	 * @return
	 */
	public boolean forward_rcpt() {
		return forward_rcpt_;
	}

	/**
	 * Getter for the delivery receipt flag
	 * @return
	 */
	public boolean delivery_rcpt() {
		return delivery_rcpt_;
	}

	/**
	 * Getter for the deletion receipt flag
	 * @return
	 */
	public boolean deletion_rcpt() {
		return deletion_rcpt_;
	}

	/**
	 * Getter for the application acknowledge receipt flag
	 * @return
	 */
	public boolean app_acked_rcpt() {
		return app_acked_rcpt_;
	}

	/**
	 * Getter for the Bundle expiration time in seconds
	 * @return
	 */
	public int expiration() {
		return expiration_;
	}

	/**
	 * Getter for the fragment offset of this Bundle. This is applicable when this Bundle is a fragment.
	 * @return
	 */
	public int frag_offset() {
		return frag_offset_;
	}

	/**
	 * If this Bundle is a fragment, this is a getter for the original length which this Bundle is fragmented from.
	 * @return
	 */
	public int orig_length() {
		return orig_length_;
	}

	/**
	 * Getter for the flag indicating whether this Bundle is a local custody of this node.
	 * @return
	 */
	public boolean local_custody() {
		return local_custody_;
	}

	/**
	 * Getter for the owner of this Bundle
	 * @return
	 */
	public final String owner() {
		return owner_;
	}

	/**
	 * Getter for the flag indicating that fragment of this Bundle is coming.
	 * @return
	 */
	public boolean fragmented_incoming() {
		return fragmented_incoming_;
	}

	/**
	 * Getter for the payload of this Bundle
	 * @return
	 */
	public final BundlePayload payload() {
		return payload_;
	}

	/**
	 * Getter for the forwarding log of this Bundle
	 * @return
	 */
	public final ForwardingLog fwdlog() {
		return fwdlog_;
	}

	/**
	 * Getter for the Bundle Creation Timestamp of this Bundle according to the Protocol
	 * @return
	 */
	public final BundleTimestamp creation_ts() {
		return creation_ts_;
	}

	/**
	 * Getter for the list of blocks received in this Bundle
	 * @return
	 */
	public final BlockInfoVec recv_blocks() {
		return recv_blocks_;
	}


	/**
	 *  Use to retrieve local custody value publicly, for test case coding
	 *  purpose only
	 * @return
	 */
	public final boolean test_local_custody() {
		return local_custody_;
	}

	/**
	 * Setter for the is_fragment flag
	 * @param t
	 */
	public void set_is_fragment(boolean t) {
		is_fragment_ = t;
	}

	/**
	 * Setter for the is_admin flag
	 * @param t
	 */
	public void set_is_admin(boolean t) {
		is_admin_ = t;
	}

	/**
	 * Setter for the do_not_fragment
	 * @param t
	 */
	public void set_do_not_fragment(boolean t) {
		do_not_fragment_ = t;
	}

	/**
	 * Setter for the custody requested flag
	 * @param t
	 */
	public void set_custody_requested(boolean t) {
		custody_requested_ = t;
	}

	/**
	 * Setter for the singleton destination flag
	 * @param t
	 */
	public void set_singleton_dest(boolean t) {
		singleton_dest_ = t;
	}

	/**
	 * Setter for the priority value
	 * @param p
	 */
	public void set_priority(priority_values_t p) {
		priority_ = p;
	}

	/**
	 * Setter for thr receive receipt flag
	 * @param t
	 */
	public void set_receive_rcpt(boolean t) {
		receive_rcpt_ = t;
	}

	/**
	 * Setter for the custody receipt flag
	 * @param t
	 */
	public void set_custody_rcpt(boolean t) {
		custody_rcpt_ = t;
	}

	/**
	 * Setter for the forwarding receipt flag
	 * @param t
	 */
	public void set_forward_rcpt(boolean t) {
		forward_rcpt_ = t;
	}

	/**
	 * Setter for the delivery receipt flag
	 * @param t
	 */
	public void set_delivery_rcpt(boolean t) {
		delivery_rcpt_ = t;
	}

	/**
	 * Setter for the deletion receipt flag
	 * @param t
	 */
	public void set_deletion_rcpt(boolean t) {
		deletion_rcpt_ = t;
	}

	/**
	 * Setter for the application acknowledgment flag
	 * @param t
	 */
	public void set_app_acked_rcpt(boolean t) {
		app_acked_rcpt_ = t;
	}

	/**
	 * Setter for the expiration time of this Bundle in seconds
	 * @param e
	 */
	public void set_expiration(int e) {
		expiration_ = e;
	}

	/**
	 * Setter for the fragment offset
	 * @param o
	 */
	public void set_frag_offset(int o) {
		frag_offset_ = o;
	}

	/**
	 * Setter for the total application unit length
	 * @param l
	 */
	public void set_orig_length(int l) {
		orig_length_ = l;
	}

	/**
	 * Setter for the local custdy flag
	 * @param t
	 */
	public void set_local_custody(boolean t) {
		local_custody_ = t;
	}

	/**
	 * Setter for the owner of this Bundle
	 * @param s
	 */
	public void set_owner(final String s) {
		owner_ = s;
	}

	/**
	 * Setter for the fragmented incoming flag
	 * @param t
	 */
	public void set_fragmented_incoming(boolean t) {
		fragmented_incoming_ = t;
	}

	/**
	 * Setter for the creation timestamp
	 * @param ts
	 */
	public void set_creation_ts(final BundleTimestamp ts) {
		creation_ts_ = ts;
	}

	/**
	 * Test function for setting the lock. This is used in test case only
	 * @param lock
	 */
	public void test_set_lock(Lock lock) {
		lock_ = lock;
	}

	
	/**
	 * Setter function for the BundleID
	 * @param id
	 */
	public void set_bundleid(int id) {
		bundleid_ = id;
	}
	
	/**
	 * Setter function for the BundlePayload
	 * @param payload
	 */
	public void set_payload(BundlePayload payload) {
		payload_ = payload;
	}

	/**
	 * Setter for the expiration timer
	 * @return
	 */
	public ExpirationTimer expiration_timer() {
		return expiration_timer_;
	}

	/**
	 * Setter for the custody timer list
	 * @return
	 */
	public CustodyTimerVec custody_timers() {
		return custody_timers_;
	}

	/**
	 * Getter for the outgoing link block set
	 * @return
	 */
	public LinkBlockSet xmit_link_block_set() {
		return xmit_link_block_set_;
	}

	/**
	 * Setter for the outgoing link block set 
	 * @param xmit_blocks
	 */
	public void set_xmit_blocks(LinkBlockSet xmit_blocks) {
		xmit_link_block_set_ = xmit_blocks;
	}


	/**
	 * Setter for the list of blocks received
	 * @param xmit_blocks
	 */
	public void set_recv_blocks(BlockInfoVec recv_blocks) {
		recv_blocks_ = recv_blocks;
	}


	/**
	 * Setter for the expiration timer
	 * @param e
	 */
	public void set_expiration_timer(ExpirationTimer e) {
		expiration_timer_ = e;
	}

	/**
	 * Source EndpointID
	 */
	private EndpointID source_;
	
	/**
	 * Destination EndpointID
	 */
	private EndpointID dest_; 
	
	/**
	 * Current custodian EndpointID
	 */
	private EndpointID custodian_;
	
	/**
	 * Reply-To EndpointID
	 */
	private EndpointID replyto_; 
	
	/**
	 * Previous hop EndpointID
	 */
	private EndpointID prevhop_; 
	
	/**
	 * Flag indicating whether this bundle is a fragmented of another Bundle
	 */
	private boolean is_fragment_;
	
	/**
	 * Flag indicating whether this bundle is an admin bundle ( ex. Custody Signal Bundle, or Status Report Bundle )
	 */
	private boolean is_admin_; 
	
	/**
	 * Flag indicating whether this bundle shouldn't be fragmented
	 */
	private boolean do_not_fragment_; 
	
	/**
	 * Flag indicating whether this bundle is requested for custody
	 */
	private boolean custody_requested_;
	
	/**
	 * Flag indicating whether this bundle has singleton destination
	 */
	private boolean singleton_dest_; 
	
	/**
	 * Internal Bundle priority
	 */
	private priority_values_t priority_; 
	
	/**
	 * Receive report flag
	 */
	private boolean receive_rcpt_;
	
	/**
	 * Custody transfer report flag
	 */
	private boolean custody_rcpt_; 
	
	/**
	 * Forwarding report flag
	 */
	private boolean forward_rcpt_; 
	
	/**
	 * Delivery report flag
	 */
	private boolean delivery_rcpt_; 
	
	/**
	 * Deletion report flag
	 */
	private boolean deletion_rcpt_; 
	
	/**
	 * Application Acknowledgement Flag
	 */
	private boolean app_acked_rcpt_; 
	
	/**
	 * Bundle Creation Timestamp
	 */
	private BundleTimestamp creation_ts_; 
	
	/**
	 * Bundle expiration time in seconds
	 */
	private int expiration_;

	/**
	 * Fragmentation offset
	 */
	private int frag_offset_;
	
	/**
	 * Original application unit length
	 */
	private int orig_length_; 
	
	/**
	 * Internal reference to this Bundle's payload
	 */
	private BundlePayload payload_; 

	
	/**
	 * Bundle identifier
	 */
	private int bundleid_ = -1; 
	
	/**
	 * Lock for mutual exclusion of the Bundle from different threads
	 */
	private Lock lock_; 
	

	/**
	 * Flag indicating whether this Bundle have local custody in this daemon
	 */
	private boolean local_custody_;
	
	/**
	 * String owner of this Bundle. 
	 */
	private String owner_;
	
	/**
	 * ForwardingLog of this Bundle
	 */
	private ForwardingLog fwdlog_; 
	
	/**
	 * Expiration Timer of this Bundle
	 */
	private ExpirationTimer expiration_timer_;
	
	/**
	 * Custody Timers list of this Bundle
	 */
	private CustodyTimerVec custody_timers_;
	
	/**
	 * Flag indicating whether this Bundle has fragments incoming. 
	 */
	boolean fragmented_incoming_; 

	/**
	 * List of BlockInfo received
	 */
	private BlockInfoVec recv_blocks_; 


	/**
	 * Block vector for each link
	 */
	private LinkBlockSet xmit_link_block_set_;  


	/**
	 * The set of BundleLists that contain the Bundle.
	 */
	private Set<BundleList> mappings_;

	/**
	 * Flag indicating whether this Bundle is fully consumed or generated by the BlockProcessor
	 */
	private boolean complete_;


	/**
	 * Initialization helper function.
	 */
	private void init(int id) {
		app_acked_rcpt_ = false;
		bundleid_ = id;
		creation_ts_ = new BundleTimestamp(bundleid_);
		custodian_ = new EndpointID();
		custody_rcpt_ = false;
		custody_requested_ = false;
		custody_timers_ = new CustodyTimerVec();
		deletion_rcpt_ = false;
		delivery_rcpt_ = false;
		dest_ = new EndpointID();
		do_not_fragment_ = false;
		expiration_ = 0;
		expiration_timer_ = null;
		forward_rcpt_ = false;
		frag_offset_ = 0;
		fragmented_incoming_ = false;
		complete_ = false;
		lock_ = new Lock();
		fwdlog_ = new ForwardingLog(lock_);
		//in_datastore_ = false;
		is_admin_ = false;
		is_fragment_ = false;
		local_custody_ = false;
		mappings_ = new Set<BundleList>();
		orig_length_ = 0;
		owner_ = "";
		// payload is initiazlied in the Constructor
		prevhop_ = new EndpointID();
		priority_ = priority_values_t.COS_NORMAL;
		receive_rcpt_ = false;
		recv_blocks_ = new BlockInfoVec();
		replyto_ = new EndpointID();
		singleton_dest_ = true;
		source_ = new EndpointID();
		xmit_link_block_set_ = new LinkBlockSet(lock_);

		Log.d(TAG, String.format("Bundle::init bundle id %d", id));
	}

	/**
	 * @return the complete_
	 */
	public boolean complete() {
		return complete_;
	}

	/**
	 * @param complete the complete_ to set
	 */
	public void set_complete(boolean complete) {
		complete_ = complete;
	}
};
