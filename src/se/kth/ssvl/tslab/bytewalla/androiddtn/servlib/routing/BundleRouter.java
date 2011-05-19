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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.Bundle;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleActions;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleDaemon;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleEventHandler;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleList;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.ForwardingInfo;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.DTNConfiguration;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.prophet.ProphetBundleRouter;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.prophet.ProphetRegistration;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.storage.BundleStore;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.storage.GlobalStorage;
import android.util.Log;

/**
 * The BundleRouter is responsible for making routing decision. It will contact other components if the Bundle should be forward to particular links.
 * It received Event from BundleDaemon and process accordingly.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public abstract class BundleRouter extends BundleEventHandler {

	/**
	 * The String TAG to support Android Logging mechanism
	 */
	private static String TAG = "BundleRouter";

	/**
	 * Router Type implemented. In this implementation, it supports only Static Bundle routing
	 */
	public static enum router_type_t {

		STATIC_BUNDLE_ROUTER("static", (byte) 0x00),
		PROPHET_BUNDLE_ROUTER("prophet", (byte) 0x01);

		private static final Map<Byte, router_type_t> lookup = new HashMap<Byte, router_type_t>();
		private static final Map<String, router_type_t> caption_map = new HashMap<String, router_type_t>();

		static {
			for (router_type_t s : EnumSet.allOf(router_type_t.class)) {
				lookup.put(s.getCode(), s);
				caption_map.put(s.getCaption(), s);
			}

		}

		private byte code_;
		private String caption_;

		private router_type_t(String caption, byte code) {
			this.caption_ = caption;
			this.code_ = code;
		}

		public byte getCode() {
			return code_;
		}

		public String getCaption() {
			return caption_;
		}

		public static router_type_t get(byte code) {
			return lookup.get(code);
		}

	}

	/**
	 *  Initialization function called by the DTNServer upon, the start service is requested
	 */
	public static void init(DTNConfiguration dtn) {
		config_ = new Config();
		config_.set_type(dtn.routes_setting().router_type());
		dtn_config_ = dtn;		
	}
	
	

	/**
	 * Method to create a Bundle Router. The type of BundleRouter created depends on the config
	 * inside the BundleRouter object
	 * 
	 * @throws RoutingException
	 */
	public static BundleRouter create_router()
			throws RoutingException {
		router_type_t type = config_.type_;
		BundleRouter router;
		if (type == router_type_t.STATIC_BUNDLE_ROUTER) {
			router = new StaticBundleRouter();
			router.name_ = "static bundle router";
		}
		else if (type == router_type_t.PROPHET_BUNDLE_ROUTER) {
			router = new ProphetBundleRouter();
			router.name_ = "prophet bundle router";
		}
		else {
			Log.e(TAG, String.format("Unknow router Type %s with code %d", type
					.getCaption(), type.getCode()));
			throw new RoutingException();
		}

		router.initialize(dtn_config_);
		return router;
	}

	/**
	 * Config variables. These must be static since they're set by the config
	 * parser before any router objects are created.
	 */
	public static class Config {
		public Config() {

			type_ = router_type_t.STATIC_BUNDLE_ROUTER;
			add_nexthop_routes_ = true;
			open_discovered_links_ = true;
			default_priority_ = 0;
			max_route_to_chain_ = 10;

		}

		/**
		 *  "The routing algorithm type" [DTN2]
		 */
		private router_type_t type_;

		/**
		 * "Whether or not to add routes for nexthop links that know" [DTN2]
		 */
		private boolean add_nexthop_routes_;

		/**
		 *  "Whether or not to open discovered opportunistic links when
		 *  they become available" [DTN2]
		 */
		private boolean open_discovered_links_;

		/**
		 *  "Default priority for new routes" [DTN2]
		 */
		private int default_priority_;

		/**
		 * "Maximum number of route_to entries to follow for a lookup" [DTN2]
		 */

		private int max_route_to_chain_;

		
		/**
		 * Accessor for the routing algorithm type
		 * @return the type_
		 */
		public router_type_t type() {
			return type_;
		}

		/**
		 * Setter for the routing algorithm type
		 * @param type
		 *            the type_ to set
		 */
		public void set_type(router_type_t type) {
			type_ = type;
		}

		/**
		 * Accessor for whether or not to add routes for nexthop links that know
		 * @return the add_nexthop_routes_
		 */
		public boolean add_nexthop_routes() {
			return add_nexthop_routes_;
		}

		/**
		 * Setter for whether or not to add routes for nexthop links that know
		 * @param addNexthopRoutes
		 *            the add_nexthop_routes_ to set
		 */
		public void set_add_nexthop_routes(boolean add_nexthop_routes) {
			add_nexthop_routes_ = add_nexthop_routes;
		}

		/**
		 * Accessor for whether or not to open discovered opportunistic links when
		 *  they become available
		 * @return the open_discovered_links_
		 */
		public boolean open_discovered_links() {
			return open_discovered_links_;
		}

		/**
		 * Setter for whether or not to open discovered opportunistic links when
		 *  they become available
		 * @param openDiscoveredLinks
		 *            the open_discovered_links_ to set
		 */
		public void set_open_discovered_links(boolean open_discovered_links) {
			open_discovered_links_ = open_discovered_links;
		}

		/**
		 * Accessor for default priority for new routes
		 * @return the default_priority_
		 */
		public int default_priority() {
			return default_priority_;
		}

		/**
		 * Setter for default priority for new routes
		 * @param defaultPriority
		 *            the default_priority_ to set
		 */
		public void set_default_priority(int default_priority) {
			default_priority_ = default_priority;
		}

		/**
		 * Accessor for maximum number of route_to entries to follow for a lookup
		 * @return the max_route_to_chain_
		 */
		public int max_route_to_chain() {
			return max_route_to_chain_;
		}

		/**
		 * Setter for maximum number of route_to entries to follow for a lookup
		 * @param maxRouteToChain
		 *            the max_route_to_chain_ to set
		 */
		public void set_max_route_to_chain(int max_route_to_chain) {
			max_route_to_chain_ = max_route_to_chain;
		}

	
	};

	/**
	 * stored DTNConfiguration inside Bundle Router to check value later on
	 */
	private static DTNConfiguration dtn_config_;
	
	/**
	 * stored Config particulary for the router
	 */
	protected static Config config_;

	
	/**
	 * Getter function for the Config object
	 * @return the saved Config object or the result of generation
	 */
	public static Config config() {

		if (config_ != null)
			return config_;
		else
			return new Config();

	}

	/**
	 * Set config Object inside the Bundle Router
	 * @param config object to set
	 */
	public void set_config(Config config) {
		config_ = config;
	}
	/**
	 * "called after all the global data structures are set up" [DTN2]
	 */
	abstract public void initialize(DTNConfiguration dtn_config_);

	/**
	 * "Check whether or not this bundle should be
	 * accepted by the router.
	 * 
	 * The default implementation checks if the bundle size will exceed the
	 * configured DTNConfiguration storage quota." [DTN2]
	 * 
	 * @return "true if the bundle was accepted." [DTN2]
	 */
	public boolean accept_bundle(Bundle bundle,
			BundleProtocol.status_report_reason_t[] error_status) {
		BundleStore bs = BundleStore.getInstance();
		if (bs.quota() != 0
				&& (GlobalStorage.getInstance().get_total_size()
						+ bundle.payload().length() > bs.quota())) {
			Log
					.i(
							TAG,
							String
									.format(
											"accept_bundle: rejecting bundle %d since "
													+ "cur size %d + bundle size %d bytes > quota %d bytes",
											bundle.bundleid(), bs.total_size(),
											bundle.payload().length(), bs
													.quota()));

			error_status[0] = BundleProtocol.status_report_reason_t.REASON_DEPLETED_STORAGE;
			return false;
		}

		return true;
	}

	/**
	 * "Check whether or not this bundle can be deleted by
	 * the router.
	 * 
	 * The default implementation returns true if the bundle is queued on more
	 * than one list (i.e. the pending bundles list)." [DTN2]
	 */
	public boolean can_delete_bundle(Bundle bundle) {
		int num_mapping = bundle.num_mappings();
		if (num_mapping > 1) {
			Log
					.d(TAG, "can_delete_bundle(" + bundle
							+ "): not deleting because " + " bundle has "
							+ num_mapping);
			return false;

		}
		return true;
	}

	/**
	 * "Synchronous call indicating that the bundle is being deleted from the
	 * system and that the router should remove it from any lists where it may
	 * be queued." [DTN2]
	 */
	abstract public void delete_bundle(final Bundle bundle);

	/**
	 * "Format the given StringBuffer with current routing info." [DTN2]
	 */
	public abstract void get_routing_state(StringBuffer buf);

	/**
	 * "Check if the bundle should be forwarded to the given next hop. Reasons
	 * why it would not be forwarded include that it was already transmitted or
	 * is currently in flight on the link, or that the route indicates
	 * ForwardingInfo::FORWARD_ACTION and it is already in flight on another
	 * route." [DTN2]
	 */
	public boolean should_fwd(final Bundle bundle, final Link link,
			ForwardingInfo.action_t action) {
		boolean[] found = new boolean[1];
		ForwardingInfo info = bundle.fwdlog().get_latest_entry(link, found);

		if (found[0]) {
			assert info.state() != ForwardingInfo.state_t.NONE;
		} else {
			assert info.state() == ForwardingInfo.state_t.NONE;
		}

		// "check if we've already sent or are in the process of sending
		// the bundle on this link" [DTN2]
		if (info!=null)
		{
		if (info.state() == ForwardingInfo.state_t.TRANSMITTED
				|| info.state() == ForwardingInfo.state_t.QUEUED) {
			Log.d(TAG, String.format("should_fwd bundle %d: "
					+ "skip %s due to forwarding log entry %s", bundle
					.bundleid(), link.name(), info.state().toString()));
			return false;
		}
		}
		// "check if we've already sent or are in the process of sending
		// the bundle to the node via some other link" [DTN2]
		if (link.remote_eid().equals(EndpointID.NULL_EID())) {
			int count = bundle.fwdlog().get_count(
					link.remote_eid(),
					ForwardingInfo.state_t.TRANSMITTED.getCode()
							| ForwardingInfo.state_t.QUEUED.getCode(),
					ForwardingInfo.ANY_ACTION);

			if (count > 0) {
				Log
						.d(
								TAG,
								String
										.format(
												"should_fwd bundle %d: "
														+ "skip %s since already sent or queued %d times for remote eid %s",
												bundle.bundleid(), link.name(),
												count, link.remote_eid().toString()));
				return false;
			}

			// "check whether transmission was suppressed. this could be
			// coupled with the previous one but it's better to have a
			// separate log message" [DTN2]
			count = bundle.fwdlog().get_count(link.remote_eid(),
					ForwardingInfo.state_t.SUPPRESSED.getCode(),
					ForwardingInfo.ANY_ACTION);

			if (count > 0) {
				Log
						.d(
								TAG,
								String
										.format(
												"should_fwd bundle %d: "
														+ "skip %s since transmission suppressed to remote eid %s",
												bundle.bundleid(), link.name(),
												link.remote_eid().toString()));
				return false;
			}
		}

		// "if the bundle has a a singleton destination endpoint, then
		// check if we already forwarded it or are planning to forward it
		// somewhere else. if so, we shouldn't forward it again" [DTN2]
		if (bundle.singleton_dest()
				&& action == ForwardingInfo.action_t.FORWARD_ACTION) {
			int count = bundle.fwdlog().get_count(
					ForwardingInfo.state_t.TRANSMITTED.getCode()
							| ForwardingInfo.state_t.QUEUED.getCode(),
					action.getCode());

			if (count > 0) {
				Log
						.d(
								TAG,
								String
										.format(
												"should_fwd bundle %d: "
														+ "skip %s since already transmitted or queued (count %d)",
												bundle.bundleid(), link.name(),
												count));
				return false;
			} else {
				Log.d(TAG, String.format("should_fwd bundle %d: "
						+ "link %s ok since transmission count=%d", bundle
						.bundleid(), link.name(), count));
			}
		}

		// "otherwise log the reason why we should send it" [DTN2]
		
		String info_string = info!=null? info.state().toString() : "no info";
		
		Log.d(TAG, String.format("should_fwd bundle %d: "
				+ "match %s: forwarding log entry %s", bundle.bundleid(), link
				.name(), info_string));

		return true;
	}

	

	/**
	 * "Hook to force route recomputation from the command interpreter. The
	 * default implementation does nothing." [DTN2]
	 */
	public abstract void recompute_routes();

	/**
	 * "for registration with the BundleDaemon" [DTN2]
	 */
	public void shutdown() {
	}

	/**
	 * Constructor
	 */
	public BundleRouter() {
		actions_ = BundleDaemon.getInstance().actions();
		pending_bundles_ = BundleDaemon.getInstance().pending_bundles();
		custody_bundles_ = BundleDaemon.getInstance().custody_bundles();
		name_ = "default router";
	}

	/**
	 *  "Name of this particular router" [DTN2]
	 */
	protected String name_;

	/**
	 *  "The list of all bundles still pending delivery" [DTN2]
	 */
	protected BundleList pending_bundles_;

	/**
	 *  "The list of all bundles that I have custody of" [DTN2]
	 */
	protected BundleList custody_bundles_;

	/**
	 *  "The actions interface, set by the BundleDaemon when the router is initialized." [DTN2]
	 */
	protected BundleActions actions_;


	public ProphetRegistration getProphetRegistration() {
		return null;
	}


}
