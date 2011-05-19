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
package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.Bundle;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleDaemon;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleInfoCache;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleList;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.ForwardingInfo;
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
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.exception.BundleListLockNotHoldByCurrentThread;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.DTNConfiguration;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.RoutesSetting;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.ContactManager;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.LinkSet;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointIDPattern;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.reg.Registration;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.VirtualTimerTask;
import android.util.Log;

/**
 * This is the basic router mainly rely on the route entries in the RoutingTable
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public abstract class TableBasedRouter extends BundleRouter {

	/**
	 * TAG name for logging in Android Logging system
	 */
	private final static String TAG = "TableBasedRouter";

	/**
	 * Constructor 
	 */
	protected TableBasedRouter() {
		route_table_ = new RouteTable("TableBasedRouter");
		reception_cache_ = new BundleInfoCache(1024);
		reroute_timers_  = new HashMap<String, RerouteTimer>();
	}
	
	


	/**
	 * Dump the routing state.
	 */
	@Override
	public void get_routing_state(StringBuffer buf) {

		buf.append(String.format("Route table for %s router:\n\n", name_));
		route_table_.dump(buf);

	}


	/**
	 * "Add a route entry to the routing table." [DTN2]
	 */
	void add_route(RouteEntry entry) {

		route_table_.add_entry(entry);
		handle_changed_routes();

	}

	/**
	 * "Remove matching route entry(s) from the routing table." [DTN2]
	 */
	void del_route(final EndpointIDPattern id) {

		route_table_.del_entries(id);
		handle_changed_routes();
	}

	/**
	 * "Update forwarding state due to changed routes." [DTN2]
	 */
	void handle_changed_routes() {

		// clear the reception cache when the routes change since we might
		// want to send a bundle back where it came from
		reception_cache_.evict_all();
		reroute_all_bundles();

	}

	/**
	 * "Try to forward a bundle to a next hop route." [DTN2]
	 */
	protected boolean fwd_to_nexthop(Bundle bundle, RouteEntry route) {

		Link link = route.link();

		// "if the link is available and not open, open it" [DTN2]
		if (link.isNotUnavailable() && (!link.isopen()) && (!link.isopening())) {
			Log.d(TAG, String.format(
					"opening %s because a message is intended for it", link.name()));
			actions_.open_link(link);
		}

		// "if the link is open and has space in the queue, then queue the
		// bundle for transmission there" [DTN2]
		if (link.isopen() && !link.queue_is_full()) {
			Log.d(TAG, String.format("queuing %d on %s", bundle.bundleid(), link.name()));
			actions_.queue_bundle(bundle, link, route.action(), route
					.custody_spec());
			return true;
		}

		// "otherwise we can't send the bundle now, so put it on the link's
		// deferred list and log reason why we can't forward it" [DTN2]
		DeferredList deferred = deferred_list(link);
		if (!bundle.is_queued_on(deferred.list())) {
			ForwardingInfo info = new ForwardingInfo(
					ForwardingInfo.state_t.NONE, route.action(), link
							.name_str(), 0xffffffff, link.remote_eid(), route
							.custody_spec());
			deferred.add(bundle, info);
		} else {
			Log.w(TAG, String.format(
					"bundle %d already exists on deferred list of link %s",
					bundle.bundleid(), link.name()));
		}

		if (!link.isNotUnavailable()) {
			Log.d(TAG, String.format(
					"can't forward bundle %d to %s because link not available",
					bundle.bundleid(), link.name()));
		} else if (!link.isopen()) {
			Log.d(TAG, String.format(TAG,
					"can't forward bundle %d to %s because link not open", bundle.bundleid(),
					link.name()));
		} else if (link.queue_is_full()) {
			Log.d(TAG, String.format(TAG,
					"can't forward bundle %d to %s because link queue is full",
					bundle.bundleid(), link.name()));
		} else {
			Log.d(TAG, String.format(TAG, "can't forward %d to %s", bundle.bundleid(),
					link.name()));
		}

		return false;
	}

	/**
	 * "Check whether the Bundle should be forwarded to the give route or not" [DTN2]
	 * @param bundle the Bundle to check
	 * @param route the route to check 
	 */
	protected boolean should_fwd(final Bundle bundle, RouteEntry route) {

		if (route == null)
			return false;


		EndpointID prevhop = reception_cache_.lookup(bundle);
		if (prevhop != null) {
			if (prevhop.equals(route.link().remote_eid())
					&& !prevhop.equals(EndpointID.NULL_EID())) {
				Log.d(TAG, String.format("should_fwd bundle %d: "
						+ "skip %s since bundle arrived from the same node",
						bundle.bundleid(), route.link().name()));
				return false;
			}
		}

		return super.should_fwd(bundle, route.link(), route.action());
	}

	/**
	 * "Check the route table entries that match the given bundle and have not
	 * already been found in the bundle history. If a match is found, call
	 * fwd_to_nexthop on it." [DTN2]
	 * 
	 * @param bundle
	 *            the bundle to forward
	 * 
	 * @return "the number of links on which the bundle was queued
	 *            (i.e. the number of matching route entries.)" [DTN2]
	 */
	protected int route_bundle(Bundle bundle) {

		RouteEntryVec matches = new RouteEntryVec();

		Log.d(TAG, String.format("route_bundle: checking bundle %d", bundle
				.bundleid()));

		// "check to see if forwarding is suppressed to all nodes" [DTN2]
		if (bundle.fwdlog().get_count(EndpointIDPattern.WILDCARD_EID(),
				ForwardingInfo.state_t.SUPPRESSED.getCode(),
				ForwardingInfo.ANY_ACTION) > 0) {
			Log.i(TAG, String.format("route_bundle: "
					+ "ignoring bundle %d since forwarding is suppressed",
					bundle.bundleid()));
			return 0;
		}

		Link null_link = null;
		route_table_.get_matching(bundle.dest(), null_link, matches);

		// "sort the matching routes by priority, allowing subclasses to
		// override the way in which the sorting occurs" [DTN2]
		sort_routes(matches);

		Log.d(TAG, String.format(
				"route_bundle bundle id %d: checking %d route entry matches",
				bundle.bundleid(), matches.size()));

		int count = 0;
		Iterator<RouteEntry> itr = matches.iterator();
		while (itr.hasNext()) {
			RouteEntry route = itr.next();
			Log.d(TAG, String.format("checking route entry %s link %s (%s)",
					route.toString(), route.link().name(), route.link()));

			if (!should_fwd(bundle, route)) {
				continue;
			}

			if (deferred_list(route.link()).list().contains(bundle)) {
				Log.d(TAG, String.format("route_bundle bundle %d: "
						+ "ignoring link %s since already deferred", bundle
						.bundleid(), route.link().name()));
				continue;
			}

			// "because there may be bundles that already have deferred
			// transmission on the link, we first call check_next_hop to
			// get them into the queue before trying to route the new
			// arrival, otherwise it might leapfrog the other deferred
			// bundles" [DTN2]
			check_next_hop(route.link());

			if (!fwd_to_nexthop(bundle, route)) {
				continue;
			}

			++count;
		}

		Log.d(TAG, String.format(
				"route_bundle bundle id %d: forwarded on %d links", bundle
						.bundleid(), count));
		return count;
	}

	/**
	 * "Once a vector of matching routes has been found, sort the vector. The
	 * default uses the route priority, breaking ties by using the number of
	 * bytes queued." [DTN2]
	 */
	protected void sort_routes(RouteEntryVec routes) {

		Collections.sort(routes, new RouteEntryComparator());

	}

	/**
	 * "Called when the next hop link is available for transmission (i.e. either
	 * when it first arrives and the contact is brought up or when a bundle is
	 * completed and it's no longer busy).
	 * 
	 * Loops through the bundle list and calls fwd_to_matching on all bundles." [DTN2]
	 */
	protected void check_next_hop(Link next_hop) {

		// "if the link isn't open, there's nothing to do now" [DTN2]
		if (!next_hop.isopen()) {
			Log.d(TAG, String.format(
					"check_next_hop %s -> %s: link not open...", next_hop
							.name(), next_hop.nexthop()));
			return;
		}

		// "if the link queue doesn't have space (based on the low water
		// mark) don't do anything" [DTN2]
		if (!next_hop.queue_has_space()) {
			Log.d(TAG, String.format(
					"check_next_hop %s -> %s: no space in queue...", next_hop
							.name(), next_hop.nexthop()));
			return;
		}

		Log.d(TAG, String.format(
				"check_next_hop %s -> %s: checking deferred bundle list...",
				next_hop.name(), next_hop.nexthop()));

		// "because the loop below will remove the current bundle from
		// the deferred list, invalidating any iterators pointing to its
		// position, make sure to advance the iterator before processing
		// the current bundle" [DTN2]
		DeferredList deferred = deferred_list(next_hop);

		deferred.list().get_lock().lock();
		try {

			Iterator<Bundle> iter = deferred.list().begin();
			while (iter.hasNext()) {
				if (next_hop.queue_is_full()) {
					Log
							.d(
									TAG,
									String
											.format(
													"check_next_hop %s: link queue is full, stopping loop",
													next_hop.name()));
					break;
				}

				Bundle bundle = iter.next();

				ForwardingInfo info = deferred.find(bundle);
				assert info != null : "TableBasedRouter: check_next_hop, ForwardingInfo regarding Bundle is null";

				// "if should_fwd returns false, then the bundle was either
				// already transmitted or is in flight on another node. since
				// it's possible that one of the other transmissions will
				// fail, we leave it on the deferred list for now, relying on
				// the transmitted handlers to clean up the state" [DTN2]
				if (!this.should_fwd(bundle, next_hop, info.action())) {
					Log.d(TAG, String.format(
							"check_next_hop: not forwarding to link %s",
							next_hop.name()));
					continue;
				}

				// "if the link is available and not open, open it" [DTN2]
				if (next_hop.isNotUnavailable() && (!next_hop.isopen())
						&& (!next_hop.isopening())) {
					Log
							.d(
									TAG,
									String
											.format(
													"check_next_hop: "
															+ "opening %s because a message is intended for it",
													next_hop.name()));
					actions_.open_link(next_hop);
				}

				// "remove the bundle from the deferred list" [DTN2]
				Log.d(TAG, String.format("check_next_hop: sending bundle %d to %s",
						bundle.bundleid(), next_hop.name()));
				
				iter.remove();
				actions_.queue_bundle(bundle, next_hop, info.action(), info
						.custody_spec());
				
				
			}
		} catch (BundleListLockNotHoldByCurrentThread e) {
			Log.e(TAG, "Table Based Router " + e.toString());
		} finally {
			deferred.list().get_lock().unlock();
		}
	}

	/**
	 * "Go through all known bundles in the system and try to re-route them." [DTN2]
	 */
	protected void reroute_all_bundles() {

		pending_bundles_.get_lock().lock();
		try {
			Log.d(TAG, String.format(
					"reroute_all_bundles... %d bundles on pending list",
					pending_bundles_.size()));

			ListIterator<Bundle> iter = pending_bundles_.begin();
			while (iter.hasNext()) {
				Bundle bundle = iter.next();
				route_bundle(bundle);
			}

		} catch (BundleListLockNotHoldByCurrentThread e) {
			Log.e(TAG, "TableBasedRouter: reroute_all_bundles " + e.toString());
		} finally {
			pending_bundles_.get_lock().unlock();
		}

	}

	/**
	 * "Generic hook in response to the command line indication that we should
	 * reroute all bundles." [DTN2]
	 */
	@Override
	public void recompute_routes() {
		reroute_all_bundles();
	}

	/**
	 * "When new links are added or opened, and if we're configured to add
	 * nexthop routes, try to add a new route for the given link." [DTN2]
	 */
	public void add_nexthop_route(final Link link) {

		// "If we're configured to do so, create a route entry for the eid
		// specified by the link when it connected, using the
		// scheme-specific code to transform the URI to wildcard
		// the service part" [DTN2]
		EndpointID eid = link.remote_eid();
		if (config_.add_nexthop_routes() && !eid.equals(EndpointID.NULL_EID())) {
			EndpointIDPattern eid_pattern = new EndpointIDPattern(link
					.remote_eid());

			// "attempt to build a route pattern from link's remote_eid" [DTN2]
			if (!eid_pattern.append_service_wildcard())
				// "else assign remote_eid as-is" [DTN2]
				eid_pattern.assign(link.remote_eid());

			RouteEntryVec ignored = new RouteEntryVec();
			if (route_table_.get_matching(eid_pattern, link, ignored) == 0) {
				RouteEntry entry = new RouteEntry(eid_pattern, link);
				entry.set_action(ForwardingInfo.action_t.FORWARD_ACTION);
				add_route(entry);
			}
		}

	}

	/**
	 * "Hook to ask the router if the bundle can be deleted." [DTN2]
	 */
	@Override
	public boolean can_delete_bundle(final Bundle bundle) {

		Log
				.d(
						TAG,
						String
								.format(
										"TableBasedRouter::can_delete_bundle: checking if we can delete %s",
										bundle));

		// "check if we haven't yet done anything with this bundle" [DTN2]
		if (bundle.fwdlog().get_count(
				ForwardingInfo.state_t.TRANSMITTED.getCode()
						| ForwardingInfo.state_t.DELIVERED.getCode(),
				ForwardingInfo.ANY_ACTION) == 0) {
			Log.d(TAG, String.format(
					"TableBasedRouter::can_delete_bundle(%d): "
							+ "not yet transmitted or delivered", bundle
							.bundleid()));
			return false;
		}

		// check if we have local custody
		if (bundle.local_custody()) {
			Log.d(TAG, String.format(
					"TableBasedRouter::can_delete_bundle(%d): "
							+ "not deleting because we have custody", bundle
							.bundleid()));
			return false;
		}

		return true;
	}

	/**
	 * "Hook to tell the router that the bundle should be deleted." [DTN2]
	 */
	@Override
	public void delete_bundle(final Bundle bundle) {

		Log.d(TAG, String.format("delete bundleid %d", bundle.bundleid()));
		remove_from_deferred(bundle, ForwardingInfo.ANY_ACTION);

	}

	/**
	 * "Remove matching deferred transmission entries." [DTN2]
	 */
	void remove_from_deferred(final Bundle bundle, int actions) {

		ContactManager cm = BundleDaemon.getInstance().contactmgr();

		cm.get_lock().lock();
		try {
			final LinkSet links = cm.links();
			Iterator<Link> iter = links.iterator();

			while (iter.hasNext()) {
				Link link = iter.next();

				// "a bundle might be deleted immediately after being loaded
				// from storage, meaning that remove_from_deferred is called
				// before the deferred list is created (since the link isn't
				// fully set up yet). so just skip the link if there's no
				// router info, and therefore no deferred list" [DTN2]
				if (link.router_info() == null) {
					continue;
				}

				DeferredList deferred = deferred_list(link);
				ForwardingInfo info = deferred.find(bundle);
				if (info != null) {
					if ((info.action().getCode() & actions) > 0) {
						Log
								.d(
										TAG,
										String
												.format(
														"removing bundle %s from link %s deferred list",
														bundle, link));
						deferred.del(bundle);
					}
				}
			}
		} finally {
			cm.get_lock().unlock();
		}

	}

	/**
	 *  "Cache to check for duplicates and to implement a simple RPF check" [DTN2]
	 */
	private BundleInfoCache reception_cache_;

	/**
	 *  "The routing table used in this router" [DTN2]
	 */
	protected RouteTable route_table_;


	/**
	 *  "Timer class used to cancel transmission on down links after waiting for them to potentially reopen" [DTN2]
	 *  @author Rerngvit Yanggratoke (rerngvit@kth.se)
	 */ 
	static class RerouteTimer extends VirtualTimerTask {

		/**
		 * The generated UID to support Java Serializable
		 */
		private static final long serialVersionUID = -7823740664913515872L;

		public RerouteTimer(TableBasedRouter router, final Link link) {
			router_ = router;
			link_ = link;
		}

		protected TableBasedRouter router_;
		protected Link link_;

		
		@Override
		protected void timeout(Date now) {
			router_.reroute_all_bundles();

		}
	};

	/**
	 *  Helper function for rerouting
	 * @param link
	 */
	void reroute_bundles(final Link link) {

		assert !link.isdeleted() : "TableBasedRouter : reroute_bundles, link is deleted";

		// "if the reroute timer fires, the link should be down and there
		// should be at least one bundle queued on it." [DTN2]
		if (link.state() != Link.state_t.UNAVAILABLE) {
			Log
					.w(
							TAG,
							String
									.format(
											"reroute timer fired but link %s state is %s, not UNAVAILABLE",
											link, link.state().toString()));
			return;
		}

		Log.d(TAG, String.format(
				"reroute timer fired -- cancelling %s bundles on link %s", link
						.queue().size(), link.toString()));


		link.queue().get_lock().lock();
		try {
			while (!link.queue().empty()) {
				Bundle bundle = link.queue().front();
				actions_.cancel_bundle(bundle, link);
				assert !bundle.is_queued_on(link.queue()) : "TableBasedRouter : reroute_bundles, bundle is not queued on link";
			}

			// "there should never have been any in flight since the link is
			// unavailable" [DTN2]
			assert link.inflight().empty() : "TableBasedRouter : reroute_bundles, link on flight list is not empty";

		} finally {
			link.queue().get_lock().unlock();
		}

	}

	private HashMap<String, RerouteTimer> reroute_timers_;

	/**
	 * "Per-link class used to store deferred transmission bundles
	 * that helps cache route computations" [DTN2]
	 */ 
	public static class DeferredList implements RouterInfo {

		private static final String TAG = "DeferredList";
		public DeferredList(final Link link) {
			list_ = new BundleList(link.name() + ":deferred");
			info_ = new HashMap<Bundle,ForwardingInfo>();
			count_ = 0;
		}

		/**
		 * Getter for the bundle list
		 * @return
		 */
		public BundleList list() {
			return list_;
		}

		/**
		 * "Accessor for the forwarding info associated with the" [DTN2]
		 * @param bundle bundle, which must be on the list
		 * @return
		 */ 
		 public final ForwardingInfo info(final Bundle bundle) {
			 ForwardingInfo result = find(bundle);
			 assert result!=null: "DeferredList: info, find(Bundle) is null";
			 return result;
		 }

		 /**
		  * "Check if the bundle is on the list. If so, return its forwarding info
		  * otherwise, return null if it can not find" [DTN2]
		  */
		public ForwardingInfo find(final Bundle bundle) {
			ForwardingInfo info  = info_.get(bundle);
			return info;
		}

		/**
		 * "Add a new bundle/info pair to the deferred list" [DTN2]
		 * @param bundle
		 * @param info
		 * @return
		 */
		boolean add(final Bundle bundle, final ForwardingInfo info) {
			  if (list_.contains(bundle)) {
			        Log.e(TAG, String.format("bundle %d already in deferred list!",
			                bundle.bundleid()));
			        return false;
			    }
			    
			    Log.d(TAG, String.format("adding bundle %d to deferred (list length %d)",
			              bundle.bundleid(), count_));

			    count_++;
			    list_.push_back(bundle);

			    info_.put(bundle, info);

			    return true;
		}

		/**
		 * "Remove the bundle and its associated forwarding info from the list" [DTN2] 
		 * @param bundle
		 * @return
		 */ 
		boolean del(final Bundle bundle) {
			if (! list_.erase(bundle,false)) {
		        return false;
		    }
		    
		    assert (count_ > 0) : "DeferredList::del count <= 0";
		    count_--;
		    
		    Log.d(TAG, String.format("removed bundle %d from deferred (length %d)",
		              bundle.bundleid(), count_));

		    ForwardingInfo removed_result = info_.remove(bundle);
		    assert removed_result != null: "DeferredList::del failed to remove info map";
		    
		    return true;
		}

		/**
		 * "Print out the stats, called from Link::dump_stats" [DTN2]
		 */
		public void dump_stats(StringBuffer buf) {
			buf.append(String.format(" -- %d bundles_deferred", count_));
		}

		/**
		 * the list of Bundle maintained by this router
		 */
		private BundleList list_;
		protected HashMap<Bundle, ForwardingInfo> info_;
		private int count_;

		
		public void dump(StringBuffer buf) {
			dump_stats(buf);

		}
	};

	
	/**
	 * "Helper accessor to return the deferred queue for a link" [DTN2]
	 * @param link
	 * @return
	 */
	public DeferredList deferred_list(final Link link) {
		DeferredList dq = (DeferredList) link.router_info();
		assert dq!=null : "TableBasedRouter: deferred_list() deferedList is null";
		
		if (dq == null) dq = new DeferredList(link);
		return dq;
	}

	
	@Override
	public void initialize(DTNConfiguration dtn_config) {
	Iterator<RoutesSetting.RouteEntry> itr = dtn_config.routes_setting().route_entries().iterator();
		
		while( itr.hasNext())
		{
			RoutesSetting.RouteEntry rentry = itr.next();
			
			EndpointIDPattern pattern = new EndpointIDPattern(rentry.dest());
			Link link  = ContactManager.getInstance().find_link(rentry.link_id());
			route_table_.add_entry(new RouteEntry(pattern , link ));			
		}
	}

	
	@Override
	protected void handle_bundle_accept(BundleAcceptRequest event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_bundle_cancel(BundleCancelRequest event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_bundle_cancelled(BundleSendCancelledEvent event) {
		Bundle bundle = event.bundle();
		Log.d(TAG, String.format("handle bundle cancelled: bundle %d", bundle.bundleid()));

		// "if the bundle has expired, we don't want to reroute it." [DTN2]
		if (!bundle.expired()) {
			route_bundle(bundle);
		}

	}

	
	@Override
	protected void handle_bundle_delete(BundleDeleteRequest request) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_bundle_delivered(BundleDeliveredEvent event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_bundle_expired(BundleExpiredEvent event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_bundle_free(BundleFreeEvent event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_bundle_inject(BundleInjectRequest event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_bundle_injected(BundleInjectedEvent event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_bundle_query(BundleQueryRequest request) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_bundle_queued_query(BundleQueuedQueryRequest event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_bundle_queued_report(BundleQueuedReportEvent event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_bundle_received(BundleReceivedEvent event) {
		boolean should_route = true;

		Bundle bundle = event.bundle();
		Log.d(TAG, String.format("handle bundle received: bundle %d", bundle.bundleid()));

		EndpointID remote_eid = EndpointID.NULL_EID();

		if (event.link() != null) {
			remote_eid = event.link().remote_eid();
		}

		if (!reception_cache_.add_entry(bundle, remote_eid)) {
			Log.i(TAG, String.format("ignoring duplicate bundle: bundle %d", bundle.bundleid()));
			BundleDaemon
					.getInstance()
					.post_at_head(
							new BundleDeleteRequest(
									bundle,
									BundleProtocol.status_report_reason_t.REASON_NO_ADDTL_INFO));
			return;
		}

		if (should_route) {
			route_bundle(bundle);
		} else {
			BundleDaemon
					.getInstance()
					.post_at_head(
							new BundleDeleteRequest(
									bundle,
									BundleProtocol.status_report_reason_t.REASON_NO_ADDTL_INFO));
		}

	}

	
	@Override
	protected void handle_bundle_report(BundleReportEvent request) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_bundle_send(BundleSendRequest event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_bundle_transmitted(BundleTransmittedEvent event) {
		Bundle bundle = event.bundle();
		Log.d(TAG, String.format("handle bundle transmitted: bundle %d", bundle.bundleid()));

		// "if the bundle has a deferred single-copy transmission for
		// forwarding on any links, then remove the forwarding log entries" [DTN2]
		remove_from_deferred(bundle, ForwardingInfo.action_t.FORWARD_ACTION
				.getCode());

		// "check if the transmission means that we can send another bundle
		// on the link" [DTN2]
		Link link = event.contact().link();
		check_next_hop(link);
	}

	
	@Override
	protected void handle_cla_parameters_query(CLAParametersQueryRequest event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_cla_parameters_report(CLAParametersReportEvent event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_cla_params_set(CLAParamsSetEvent event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_cla_set_params(CLASetParamsRequest event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_contact_attribute_changed(
			ContactAttributeChangedEvent event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_contact_down(ContactDownEvent event) {
		Link link = event.contact().link();
		assert (link != null) : "TableBasedRouter : handle_contact_down : link is null";
		assert (!link.isdeleted()) : "TableBasedRouter : handle_contact_down : link is deleted";

		// "if there are any bundles queued on the link when it goes down,
		// schedule a timer to cancel those transmissions and reroute the
		// bundles in case the link takes too long to come back up" [DTN2]

		int num_queued = link.queue().size();
		if (num_queued != 0) {
			RerouteTimer reroute_timer = reroute_timers_.get(link.name());
			if (reroute_timer == null) {
				Log.d(TAG, String.format(
						"link %s went down with %d bundles queued, "
								+ "scheduling reroute timer in %d seconds",
						link.name(), num_queued, link.params()
								.potential_downtime()));
				RerouteTimer t = new RerouteTimer(this, link);
				t.schedule_in(link.params().potential_downtime());

				reroute_timers_.put(link.name(), t);
			}
		}

	}

	
	@Override
	protected void handle_contact_query(ContactQueryRequest request) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_contact_report(ContactReportEvent request) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_contact_up(ContactUpEvent event) {
		Link link = event.contact().link();
		assert (link != null) : "TabledBasedRouter: handle_contact_up : link is null";
		assert (!link.isdeleted()) : "TabledBasedRouter: handle_contact_up : link is deleted";

		if (!link.isopen()) {
			Log.e(TAG, String.format(
					"contact up(link %s): event delivered but link not open", link.name()));
		}

		add_nexthop_route(link);
		check_next_hop(link);

		// "check if there's a pending reroute timer on the link, and if
		// so, cancel it.
		// 
		// note that there's a possibility that a link just bounces
		// between up and down states but can't ever really send a bundle
		// (or part of one), which we don't handle here since we can't
		// distinguish that case from one in which the CL is actually
		// sending data, just taking a long time to do so." [DTN2]

		RerouteTimer reroute_timer = reroute_timers_.get(link.name());
		if (reroute_timer != null) {
			Log.d(TAG, String.format(
					"link %s reopened, cancelling reroute timer", link.name()));
			reroute_timers_.remove(link.name());
			reroute_timer.cancel();
		}

	}

	
	@Override
	protected void handle_custody_signal(CustodySignalEvent event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_custody_timeout(CustodyTimeoutEvent event) {
		// "the bundle daemon should have recorded a new entry in the
	    // forwarding log for the given link to note that custody transfer
	    // timed out, and of course the bundle should still be in the
	    // pending list.
	    //
	    // therefore, trying again to forward the bundle should match
	    // either the previous link or any other route" [DTN2]
	    route_bundle(event.bundle());

	}

	
	@Override
	protected void handle_eid_reachable_query(EIDReachableQueryRequest event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_eid_reachable_report(EIDReachableReportEvent event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_iface_attributes_query(
			IfaceAttributesQueryRequest event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_iface_attributes_report(
			IfaceAttributesReportEvent event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_link_attribute_changed(LinkAttributeChangedEvent event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_link_attributes_query(LinkAttributesQueryRequest event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_link_attributes_report(LinkAttributesReportEvent event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_link_available(LinkAvailableEvent event) {
		Link link = event.link();
	    assert(link != null): "TableBasedRouter: handle_link_available: link is null";
	    assert(!link.isdeleted()): "TableBasedRouter: handle_link_available: link is deleted";

	    // "if it is a discovered link, we typically open it" [DTN2]
	    if (config_.open_discovered_links() &&
	        !link.isopen() &&
	        link.type() == Link.link_type_t.OPPORTUNISTIC &&
	        event.reason() == ContactEvent.reason_t.DISCOVERY)
	    {
	        actions_.open_link(link);
	    }
	    
	    // "check if there's anything to be forwarded to the link" [DTN2]
	    check_next_hop(link);

	}


	
	@Override
	protected void handle_link_created(LinkCreatedEvent event) {
		Link link = event.link();
		assert(link != null): "TableBasedRouter: handle_link_created: link is null";
	    assert(!link.isdeleted()): "TableBasedRouter: handle_link_available: link is deleted";

	    link.set_router_info(new DeferredList(link));
	                          
	    add_nexthop_route(link);

	}

	
	@Override
	protected void handle_link_delete(LinkDeleteRequest request) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_link_deleted(LinkDeletedEvent event) {
		Link link = event.link();
		assert(link != null): "TableBasedRouter: handle_link_deleted: link is null";
		   

	    route_table_.del_entries_for_nexthop(link);

	    RerouteTimer t  = reroute_timers_.get(link.name());
	    if (t != null) {
	        Log.d(TAG, String.format("link %s deleted, cancelling reroute timer",
	        		link.name()));
	        reroute_timers_.remove(link.name());
	        t.cancel();
	    }

	}

	
	@Override
	protected void handle_link_query(LinkQueryRequest request) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_link_reconfigure(LinkReconfigureRequest request) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_link_report(LinkReportEvent request) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_link_state_change_request(LinkStateChangeRequest req) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_link_unavailable(LinkUnavailableEvent event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_new_eid_reachable(NewEIDReachableEvent event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_reassembly_completed(ReassemblyCompletedEvent event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_registration_added(RegistrationAddedEvent event) {
		 Registration reg = event.registration();
		    
	    if (reg == null || reg.session_flags() == 0) {
	        return;
	    }

	}

	
	@Override
	protected void handle_registration_delete(RegistrationDeleteRequest event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_registration_expired(RegistrationExpiredEvent event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_registration_removed(RegistrationRemovedEvent event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_route_add(RouteAddEvent event) {
		add_route(event.entry());

	}

	
	@Override
	protected void handle_route_del(RouteDelEvent event) {
		del_route(event.dest());
	}

	
	@Override
	protected void handle_route_query(RouteQueryRequest request) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_route_report(RouteReportEvent request) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_set_link_defaults(SetLinkDefaultsRequest event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_shutdown_request(ShutdownRequest event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	protected void handle_status_request(StatusRequest event) {
		//NOT IMPLEMENTED IN THIS ROUTER

	}

	
	@Override
	public void handle_event(BundleEvent event) {
		dispatch_event(event);
	};
};
