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

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleDaemon;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleEventHandler;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleAcceptRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleCancelRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleDeleteRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleDeliveredEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleExpiredEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleFreeEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleInjectRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleInjectedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleQueryRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleQueuedQueryRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleQueuedReportEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleReceivedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleReportEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleSendCancelledEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleSendRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleTransmittedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.CLAParametersQueryRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.CLAParametersReportEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.CLAParamsSetEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.CLASetParamsRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ContactAttributeChangedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ContactDownEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ContactEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ContactQueryRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ContactReportEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ContactUpEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.CustodySignalEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.CustodyTimeoutEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.EIDReachableQueryRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.EIDReachableReportEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.IfaceAttributesQueryRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.IfaceAttributesReportEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkAttributeChangedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkAttributesQueryRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkAttributesReportEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkAvailableEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkCreateRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkCreatedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkDeleteRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkDeletedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkQueryRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkReconfigureRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkReportEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkStateChangeRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.LinkUnavailableEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.NewEIDReachableEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ReassemblyCompletedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.RegistrationAddedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.RegistrationDeleteRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.RegistrationExpiredEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.RegistrationRemovedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.RouteAddEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.RouteDelEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.RouteQueryRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.RouteReportEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.SetLinkDefaultsRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ShutdownRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.StatusRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.DTNConfiguration;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.LinksSetting.LinkEntry;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link.Params;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.ConvergenceLayer;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.Lock;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.MsgBlockingQueue;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.VirtualTimerTask;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.List;
import android.util.Log;

/**
 * "A contact manager class. Maintains topological information and connectivity
 * state regarding available links and contacts"[DTN2].
 * 
 * @author María José Peroza Marval (mjpm@kth.se)
 */

public class ContactManager extends BundleEventHandler {

	/**
	 * TAG for Android Logging mechanism
	 */
	private static final String TAG = "ContactManager";

	/**
	 * Contact Manager instance
	 */
	private static ContactManager instance_ = null;

	/**
	 * Number of uploading bundles
	 */
	private int number_uploading_bundles_ = 0;

	/**
	 * Number of downloading bundles
	 */
	private int number_downloading_bundles_ = 0;

	/**
	 * Singleton pattern for ContactManager
	 */

	public static ContactManager getInstance() {
		if (instance_ == null) {

			instance_ = new ContactManager();
		}
		return instance_;
	}

	/**
	 * Constructor
	 */
	public ContactManager() {

		opportunistic_cnt_ = 0;
		links_ = new LinkSet();
		lock_ = new Lock();
		availability_timers_ = new HashMap<Link, LinkAvailabilityTimer>();
		number_uploading_bundles_ = 0;
		number_downloading_bundles_ = 0;
	}

	/**
	 * Shutdown and clean the Contact Instance
	 */

	public void shutdown() {
		lock_.lock();
		links_.clear();
		availability_timers_.clear();
		instance_ = null;
		lock_.unlock();

	}

	/**
	 * Parsing the link's parameters specified in the configuration file
	 * (config). Create the links and add them to the Contact Manager.
	 */
	public void init(DTNConfiguration config) {

		List<LinkEntry> EntriesList = config.links_setting().link_entries();
		Iterator<LinkEntry> i = EntriesList.iterator();
		Link.set_link_counter(0);

		while (i.hasNext()) {

			LinkEntry element = i.next();
			String id_ = element.id();
			Log.d(TAG, id_);
			String dest_ = element.des();
			Log.d(TAG, dest_);

			Link.link_type_t type_ = element.type();
			Log.d(TAG, String.valueOf(type_));
			String convl_type = element.conv_layer_type().getCaption();

			if (type_ == Link.link_type_t.LINK_INVALID) {
				Log.d(TAG, "invalid link type" + type_);

			}

			ConvergenceLayer cl = ConvergenceLayer.find_clayer(convl_type);
			if (cl == null) {
				Log.d(TAG, "invalid convergence layer" + convl_type);

			}

			// Create the link, parsing the cl-specific next hop string
			// and other arguments
			Link link;
			link = Link.create_link(id_, type_, cl, dest_);

			if (link == null) {
				Log.d(TAG, "invalid link option");

			}

			// Add the link to contact manager's table if it is not already
			// present. The contact manager will post a LinkCreatedEvent to
			// the daemon if the link is added successfully.
			BundleDaemon BD = BundleDaemon.getInstance();
			if (!BD.contactmgr().add_new_link(link)) {
				// A link of that name already exists
				link.delete_link();
				String text = String.format(
						"link name %s already exists, use different name", id_);
				Log.d(TAG, text);
			}

		}
	}

	/**
	 * Dump a string representation of the info inside contact manager.
	 */
	public void dump(StringBuffer buf) {

		lock_.lock();
		try {

			buf.append("Links:\n");
			Iterator<Link> iter = links_.iterator();

			while (iter.hasNext()) {
				Link element = iter.next();
				buf.append(element);
			}
		} finally {

			lock_.unlock();
		}

	}

	/**********************************************
	 * 
	 * Link set accessor functions[DTN2]
	 * 
	 *********************************************/
	/**
	 * Add a link if the contact manager does not already have a link by the
	 * same name.
	 */
	public boolean add_new_link(Link link) {

		lock_.lock();

		try {
			assert (link != null) : "ContactManager : add_new_link, link is null";
			assert (!link.isdeleted()) : "ContactManager : add_new_link, link is deleted";
	
			Log.d(TAG, "adding NEW link " + link.name());
			if (has_link(link)) {
				return false;
			}
			links_.add(link);

			if (!link.is_create_pending()) {

				Log.d(TAG, "posting LinkCreatedEvent");
				BundleDaemon BD = BundleDaemon.getInstance();
				BD.post_at_head(new LinkCreatedEvent(link,
						ContactEvent.reason_t.USER));
			}

			return true;
		} finally {
			lock_.unlock();
		}
	}

	/**
	 * Delete a link
	 */
	public void del_link(Link link, boolean wait, ContactEvent.reason_t reason) {

		lock_.lock();

		assert (link != null) : "ContactManager : del_link, link is null";

		if (!has_link(link)) {
			String text = String.format(
					"ContactManager.del_link: link %s does not exist", link
							.name());
			Log.e(TAG, text);
			return;
		}
		assert (!link.isdeleted()) : "ContactManager : del_link, Link is already deleted";

		Log.d(TAG, "ContactManager.del_link: deleting link %s" + link.name());

		if (!wait)
			link.delete_link();

		// Close the link if it is open or in the process of being opened.
		if (link.isopen() || link.isopening()) {
			BundleDaemon BD = BundleDaemon.getInstance();
			BD.post(new LinkStateChangeRequest(link, Link.state_t.CLOSED,
					reason));
		}

		// Cancel the link's availability timer (if one exists).

		if (availability_timers_.containsKey(link)) {
			LinkAvailabilityTimer timer = availability_timers_.remove(link);
			timer.cancel();
		}

		links_.remove(link);

		if (wait) {
			lock_.unlock();
			// If some parent calling del_link already locked the Contact
			// Manager,
			// the lock will remain locked, and an event ahead of the
			// LinkDeletedEvent may wait for the lock, causing deadlock [DTN2]
			assert (!get_lock().isHeldByCurrentThread());
			MsgBlockingQueue<Integer> notifier = new MsgBlockingQueue<Integer>(
					5);

			BundleDaemon BD = BundleDaemon.getInstance();
			LinkDeletedEvent event = new LinkDeletedEvent(link, reason);
			BD.post_and_wait(event, notifier, -1, true);

			link.delete_link();
		} else {

			BundleDaemon BD = BundleDaemon.getInstance();
			BD.post(new LinkDeletedEvent(link, reason));
		}
	}

	/**
	 * Check if contact manager already has this link.
	 */
	public boolean has_link(Link link) {

		lock_.lock();
		try {
			assert (link != null) : "ContactManager : HasLink(link), Link is null";

			Iterator<Link> iter = links_.iterator();

			while (iter.hasNext()) {

				Link element = iter.next();
				if (element == link) {
					return true;
				}
			}

			return false;
		} finally {
			lock_.unlock();
		}

	}

	/**
	 * 
	 * Check if contact manager already has a link by the same name.
	 */
	public boolean has_link(String name) {

		lock_.lock();
		try {
			assert (link != null) : "ContactManager : HasLink(name), Link is null";

			Iterator<Link> iter = links_.iterator();
			while (iter.hasNext()) {

				Link element = iter.next();
				if (name.compareTo(element.name()) == 0)
					return true;
			}
			return false;
		} finally {
			lock_.unlock();
		}

	}

	/**
	 * Finds link corresponding to this name
	 */
	public Link find_link(String name) {

		lock_.lock();
		try {

			Iterator<Link> iter = links_.iterator();

			while (iter.hasNext()) {

				Link element = iter.next();
				if (name.compareTo(element.name()) == 0) {
					assert (!element.isdeleted());
					return element;
				}
			}
			return null;
		} finally {
			lock_.unlock();
		}

	}

	/**
	 * Helper routine to find a link based on criteria:
	 * 
	 * @param cl
	 *            The convergence layer
	 * @param nexthop
	 *            The next hop string
	 * @param remote_eid
	 *            Remote endpoint id (NULL_EID for any)
	 * @param type
	 *            Link type (LINK_INVALID for any)
	 * @param states
	 *            Bit vector of legal link states, e.g. ~(OPEN | OPENING)
	 * 
	 * @return The link if it matches or NULL if there's no match [DTN2]
	 */
	public Link find_link_to(EndpointID remote_eid) {
		lock_.lock();
		try {

			Iterator<Link> iter = links_.iterator();

			String text = String
			.format(
					"find_link_to: remote_eid",	remote_eid);

			Log.d(TAG, text);

			// make sure some sane criteria was specified
			assert ((remote_eid != EndpointID.NULL_EID()));

			while (iter.hasNext()) {
				Link element = iter.next();
				
				if (remote_eid.equals(element.remote_eid())) {
					Log.d(TAG, "find_link_to: matched link" + element);
					assert (!element.isdeleted()) : "ContactManager : find_link_to, link is deleted";
					return element;
				}
			}

			Log.d(TAG, "find_link_to, no match");
			return null;

		} finally {
			lock_.unlock();
		}
	}

	/**
	 * Return the list of links.
	 */
	public LinkSet links() {

		assert (lock_.isHeldByCurrentThread()) : "ContactManager : links,links must be called while holding lock";
		return links_;

	}

	/**
	 * Accessor for the ContactManager internal lock.
	 */
	public Lock get_lock() {
		return lock_;
	}

	/**********************************************
	 * 
	 * Event handler routines[DTN2]
	 * 
	 *********************************************/
	/**
	 * Generic event handler.
	 */
	@Override
	public void handle_event(BundleEvent event) {
		dispatch_event(event);
	}

	/**
	 * Event handler when a link has been created.
	 */
	@Override
	public void handle_link_created(LinkCreatedEvent event) {

		lock_.lock();

		try {
			Link link = event.link();
			assert (link != null) : "ContactManager : handle_link_created, Link is null";

			if (link.isdeleted()) {
				String text = String.format(
						"handle_link_created: link %s is being deleted", link
								.name());
				Log.w(TAG, text);
				return;
			}

			if (!has_link(link)) {

				String text = String.format(
						"handle_link_created: link %s does not exist", link
								.name());
				Log.e(TAG, text);
				return;
			}

			// Post initial state events.
			link.set_initial_state();
		} finally {
			lock_.unlock();
		}

	}

	/**
	 * Event handler when a link becomes available
	 */
	@Override
	public void handle_link_available(LinkAvailableEvent event) {

		lock_.lock();
		try {

			Link link = event.link();
			assert (link != null) : "ContactManager : handle_link_available, Link is null";

			if (link.isdeleted()) {
				String text = String.format(
						"handle_link_available: link %s is being deleted", link
								.name());
				Log.w(TAG, text);
				return;
			}

			if (!has_link(link)) {
				String text = String.format(
						"handle_link_available: link %s does not exist", link
								.name());
				Log.w(TAG, text);
				return;
			}

			if (!availability_timers_.containsKey(link)) {
				return; // no timer for this link
			}

			LinkAvailabilityTimer timer = availability_timers_.remove(link);

			// try to cancel the timer.
			timer.cancel();

		} finally {
			lock_.unlock();
		}
	}

	/**
	 * Event handler when a link becomes unavailable.
	 */
	@Override
	public void handle_link_unavailable(LinkUnavailableEvent event) {

		Log.d(TAG, "handling link unavailable event");
		lock_.lock();
		try {

			Link link = event.link();
			assert (link != null) : "ContactManager : handle_link_unavailable, link is null";

			if (!has_link(link)) {
				String text = String.format(
						"handle_link_unavailable: link %s does not exist", link
								.name());
				Log.w(TAG, text);
				return;
			}

			if (link.isdeleted()) {
				String text = String.format(
						"handle_link_unavailable: link %s is being deleted",
						link.name());
				Log.w(TAG, text);
				return;
			}

			// don't do anything for links that aren't ondemand or alwayson
			if ((link.type() != Link.link_type_t.ONDEMAND)
					&& (link.type() != Link.link_type_t.ALWAYSON)) {
				Log.d(TAG,
						"handle_link_unavailable: ignoring link unavailable for link of type %s"
								+ link.type_str());
				return;
			}

			Params params_ = link.params();
			if (params_.min_retry_interval() != 0) {

				link.set_retry_interval(params_.min_retry_interval());

			}

			int amount = 2 * link.retry_interval();
			link.set_retry_interval(amount);
			if (link.retry_interval() > params_.max_retry_interval()) {

				link.set_retry_interval(params_.max_retry_interval());
			}

			LinkAvailabilityTimer timer = new LinkAvailabilityTimer(this, link);

			availability_timers_.put(link, timer);
			if (availability_timers_.containsValue(timer) == false) {
				String text = String
						.format(
								"handle_link_unavailable: error inserting timer for link %s into table!",
								link.name());
				Log.e(TAG, text);
				return;
			}

			String text = String
					.format(
							"link %s unavailable (%s): scheduling retry timer in %d seconds",
							link.name(), String.valueOf(event.reason()), link
									.retry_interval());
			Log.d(TAG, text);

			timer.schedule_in(link.retry_interval());

		} finally {
			lock_.unlock();
		}

	}

	/**
	 * Event handler when a link is opened successfully. That means the contact
	 * is up.
	 */

	@Override
	public void handle_contact_up(ContactUpEvent event) {

		lock_.lock();
		try {

			Link link = event.contact().link();
			assert (link != null) : "ContactManager : handle_contact_up, Link is null";

			if (link.isdeleted()) {
				String text = String
						.format(
								"handle_contact_up: link %s is being deleted, not marking its contact up",
								link.name());
				Log.w(TAG, text);
				return;
			}

			if (!has_link(link)) {
				String text = String.format(
						"handle_contact_up: link %s does not exist", link
								.name());
				Log.w(TAG, text);
				return;
			}

			if (link.type() == Link.link_type_t.ONDEMAND
					|| link.type() == Link.link_type_t.ALWAYSON) {
				String text = String
						.format(
								"handle_contact_up: resetting retry interval for link %s: %s -> %s",
								link.name(), link.retry_interval(), link
										.params().min_retry_interval());
				Log.d(TAG, text);
				link.set_retry_interval(link.params().min_retry_interval());
			}

		} finally {
			lock_.unlock();
		}

	}

	/**********************************************
	 * 
	 * Opportunistic contact routines[DTN2]
	 * 
	 *********************************************/
	/**
	 * Notification from a convergence layer that a new opportunistic link has
	 * come knocking.
	 * 
	 * @return An idle link to represent the new contact
	 */
	public Link new_opportunistic_link(ConvergenceLayer cl, String nexthop,
			EndpointID remote_eid) {

		lock_.lock();
		try {
			if(remote_eid.str().equals(BundleDaemon.getInstance().local_eid().str())){
				Log.d(TAG,"Tried to create a new opportunistic link to self.  Return null");
				return null;
			}

			String text = String.format(
					"new_opportunistic_link: cl %s nexthop %s remote_eid %s",
					cl.name(), nexthop, remote_eid);
			Log.d(TAG, text);

			// find a unique link name
			String name;
//
//			if (link_name != null) {
//				name = link_name;
//
//				while (find_link(name) != null) {
//
//					name = String
//							.format("%s-%s", link_name, opportunistic_cnt_);
//					opportunistic_cnt_++;
//				}
//			}
//
//			else {
			do {

				name = String.format("link-%d", opportunistic_cnt_);
				opportunistic_cnt_++;

			} while (find_link(name) != null);
//			}

			Link link = Link.create_link(name, Link.link_type_t.OPPORTUNISTIC,
					cl, nexthop);
			if (link == null) {
				Log
						.w(TAG,
								"new_opportunistic_link: unexpected error creating opportunistic link");
				return link;
			}

			Link new_link = link;

			new_link.set_remote_eid(remote_eid);

			if (!add_new_link(new_link)) {
				new_link.delete_link();
				Log.e(TAG,
						"new_opportunistic_link: failed to add new opportunistic link %s"
								+ new_link.name());
				new_link = null;
			}

			return new_link;

		} finally {
			lock_.unlock();
		}

	}

	protected LinkSet links_; // /< Set of all links
	protected int opportunistic_cnt_; // /< Counter for opportunistic links

	/**
	 * Reopen a broken link.
	 */
	protected void reopen_link(Link link) {

		lock_.lock();
		try {
			assert (link != null) : "ContactManager : reopen_link, link is null";

			Log.d(TAG, "reopen link" + link.name());

			availability_timers_.remove(link);

			if (!has_link(link)) {
				String text = String.format(
						"reopen_link: link %s does not exist", link.name());
				Log.w(TAG, text);
				return;
			}
			assert (!link.isdeleted()) : "Link : reopen_link, link is deleted";

			if (link.state() == Link.state_t.UNAVAILABLE) {

				BundleDaemon BD = BundleDaemon.getInstance();
				BD.post(new LinkStateChangeRequest(link, Link.state_t.OPEN,
						ContactEvent.reason_t.RECONNECT));
			} else {

				String text = String.format(
						"availability timer fired for link %s but state is %s",
						link.name(), Link.state_to_str(link.state()));

				Log.e(TAG, text);
			}
		} finally {
			lock_.unlock();
		}
	}

	/**
	 * Timer class used to re-enable broken ondemand links.
	 */
	protected static class LinkAvailabilityTimer extends VirtualTimerTask {
		/**
		 * Unique identifier according to Java Serializable specification
		 */
		private static final long serialVersionUID = -2373747419043071576L;

		public LinkAvailabilityTimer(ContactManager cm, Link link) {

			cm_ = cm;
			link_ = link;
		}

		ContactManager cm_; // /< The contact manager object
		Link link_; // /< The link in question

		
		@Override
		protected void timeout(Date now) {

			Log.d(TAG, "Link availablity timer for link " + link_.name_
					+ " fired, trying to open link");
			if (!link_.isopening() && !link_.isopen()) {
				Log.d(TAG, "Timer ask Contact Manager to reopen link "
						+ link_.name_);
				cm_.reopen_link(link_);

			} else {
				Log.d(TAG, "Link " + link_.name_
						+ " is already open or is openning");
			}

		}

	}

	/**
	 * Table storing link -> availability timer class.
	 */
	protected Map<Link, LinkAvailabilityTimer> availability_timers_;

	/**
	 * Lock to protect internal data structures.
	 */

	protected Lock lock_;
	protected Link link;

	
	@Override
	protected void handle_bundle_accept(BundleAcceptRequest event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_bundle_cancel(BundleCancelRequest event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_bundle_cancelled(BundleSendCancelledEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_bundle_delete(BundleDeleteRequest request) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_bundle_delivered(BundleDeliveredEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_bundle_expired(BundleExpiredEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_bundle_free(BundleFreeEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_bundle_inject(BundleInjectRequest event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_bundle_injected(BundleInjectedEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_bundle_query(BundleQueryRequest request) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_bundle_queued_query(BundleQueuedQueryRequest event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_bundle_queued_report(BundleQueuedReportEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_bundle_received(BundleReceivedEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_bundle_report(BundleReportEvent request) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_bundle_send(BundleSendRequest event) {
		// NOT Implemented

	}

	
	@Override
	protected void handle_bundle_transmitted(BundleTransmittedEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_cla_parameters_query(CLAParametersQueryRequest event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_cla_parameters_report(CLAParametersReportEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_cla_params_set(CLAParamsSetEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_cla_set_params(CLASetParamsRequest event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_contact_attribute_changed(
			ContactAttributeChangedEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_contact_down(ContactDownEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_contact_query(ContactQueryRequest request) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_contact_report(ContactReportEvent request) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_custody_signal(CustodySignalEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_custody_timeout(CustodyTimeoutEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_eid_reachable_query(EIDReachableQueryRequest event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_eid_reachable_report(EIDReachableReportEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_iface_attributes_query(
			IfaceAttributesQueryRequest event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_iface_attributes_report(
			IfaceAttributesReportEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_link_attribute_changed(LinkAttributeChangedEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_link_attributes_query(LinkAttributesQueryRequest event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_link_attributes_report(LinkAttributesReportEvent event) {
		// TODO Auto-generated method stub

	}

	// 
	protected void handle_link_create(LinkCreateRequest request) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_link_delete(LinkDeleteRequest request) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_link_deleted(LinkDeletedEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_link_query(LinkQueryRequest request) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_link_reconfigure(LinkReconfigureRequest request) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_link_report(LinkReportEvent request) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_link_state_change_request(LinkStateChangeRequest req) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_new_eid_reachable(NewEIDReachableEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_reassembly_completed(ReassemblyCompletedEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_registration_added(RegistrationAddedEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_registration_delete(RegistrationDeleteRequest event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_registration_expired(RegistrationExpiredEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_registration_removed(RegistrationRemovedEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_route_add(RouteAddEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_route_del(RouteDelEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_route_query(RouteQueryRequest request) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_route_report(RouteReportEvent request) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_set_link_defaults(SetLinkDefaultsRequest event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_shutdown_request(ShutdownRequest event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void handle_status_request(StatusRequest event) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the number_download_bundles_
	 */
	public int number_downloading_bundles() {
		return number_downloading_bundles_;
	}

	/**
	 * @param numberDownloadBundles
	 *            the number_download_bundles_ to set
	 */
	public void set_number_downloading_bundles(int number_downloading_bundles) {
		number_downloading_bundles_ = number_downloading_bundles;
	}

	/**
	 * @return the number_uploading_bundles_
	 */
	public int number_uploading_bundles() {
		return number_uploading_bundles_;
	}

	/**
	 * @param numberUploadingBundles
	 *            the number_uploading_bundles_ to set
	 */
	public void set_number_uploading_bundles(int number_uploading_bundles) {
		number_uploading_bundles_ = number_uploading_bundles;
	}
}