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

import java.io.File;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import se.kth.ssvl.tslab.bytewalla.androiddtn.DTNManager;
import se.kth.ssvl.tslab.bytewalla.androiddtn.DTNService;
import se.kth.ssvl.tslab.bytewalla.androiddtn.R;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol.custody_signal_reason_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol.status_report_reason_t;
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
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.event_source_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.event_type_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ContactEvent.reason_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.exception.BundleListLockNotHoldByCurrentThread;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.common.ServlibEventData;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.common.ServlibEventHandler;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.DTNConfiguration;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Contact;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.ContactManager;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Interface;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.LinkSet;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.NamedAttribute;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.ConvergenceLayer;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointIDPattern;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.reg.AdminRegistration;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.reg.PingRegistration;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.reg.Registration;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.reg.RegistrationList;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.reg.RegistrationTable;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.BundleRouter;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.RoutingException;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.BundleRouter.router_type_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.storage.BundleStore;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.storage.RegistrationStore;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.MsgBlockingQueue;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.List;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.TimeHelper;
import android.util.Log;

/**
 * main DTNService daemon class to execute Bundle events posted in its queue. 
 * This daemon will dispatch the events to Contact Manager and Router as it sees appropriate.
 * 
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */

public class BundleDaemon extends BundleEventHandler implements Runnable {

	/**
	 * General daemon parameters
	 */
	public static class Params {

		//  "Whether or not to accept custody when requested" [DTN2]
		public boolean accept_custody_;

		//  "Whether or not to delete bundles before they're expired if
		//  all routers / registrations have handled it" [DTN2]
		public boolean early_deletion_;

		// "Whether or not injected bundles are held in memory by default" [DTN2]
		public boolean injected_bundles_in_memory_;

		// "Whether or not reactive fragmentation is enabled" [DTN2]
		public boolean reactive_frag_enabled_;

		// "Whether or not to retry unacked transmissions on reliable CLs." [DTN2]
		public boolean retry_reliable_unacked_;

		//  "Whether or not to skip routing decisions for and delete duplicate
		//   bundles" [DTN2]
		public boolean suppress_duplicates_;

		// "Test hook to permute bundles before delivering to registrations" [DTN2]
		public boolean test_permuted_delivery_;

		// / Default constructor
		Params() {

			early_deletion_ = true;
			suppress_duplicates_ = true;
			accept_custody_ = true;
			reactive_frag_enabled_ = false;
			retry_reliable_unacked_ = false;
			test_permuted_delivery_ = false;
			injected_bundles_in_memory_ = false;
		}

	}
	
	/**
	 * Comparator implementation to sort BundleEvent according to the event's priority
	 */
	private static class BundleEventPriorityComparator implements Comparator<BundleEvent>
	{

		private static BundleEventPriorityComparator instance_;
		public static BundleEventPriorityComparator getInstance()
		{
			if (instance_ == null)
			{
				instance_ = new BundleEventPriorityComparator();
			}
			return instance_;
		}
		
		public int compare(BundleEvent event1, BundleEvent event2) {

			// according the PriorityQueue the lower the output value the closer to the top of the queue
			if (event1.priority() < event2.priority())
				return 1;
			else if (event1.priority() > event1.priority())
				return -1;
			else
				return 0;
		}
		
	}
	
	/**
	 *  "Statistics structure definition" [DTN2]
	 */
	protected static class Stats {

		int deleted_bundles_;
		int delivered_bundles_;
		int duplicate_bundles_;
		int events_processed_;
		int expired_bundles_;
		int generated_bundles_;
		int injected_bundles_;
		int received_bundles_;
		int transmitted_bundles_;

	}
	
	public static Params params_;
	
	/**
	 * Default Event Queue Capacity
	 */
	private final static int event_queue_capacity_ = 100;

	/**
	 * Singleton implementation instance
	 */
	private static BundleDaemon instance_ = null;

	/**
	 * String TAG for using in Android Logging system 
	 */
	private final static String TAG = "BundleDaemon";

	/**
	 *  "indicator that a BundleDaemon shutdown is in progress" [DTN2]
	 */
	protected static boolean shutting_down_;

	/**
	 * Singleton client implementation
	 * @return
	 */
	public static BundleDaemon getInstance() {
		if (instance_ == null) {
			instance_ = new BundleDaemon();
		}
		return instance_;
	}

	public static String localEid() {
		return getInstance().local_eid().str();
	}


	/**
	 * "Accessor for the BundleDaemon's shutdown status" [DTN2]
	 */
	public static boolean shutting_down() {
		return shutting_down_;
	}

	/**
	 * Event ticket for used in blocking execution
	 */
	private Integer event_ticket_;

	/**
	 * Thread for running this daemon
	 */
	private Thread thread_;

	/**
	 *  "The active bundle actions handler" [DTN2]
	 */
	protected BundleActions actions_;

	/**
	 *  "The administrative registration" [DTN2]
	 */
	protected AdminRegistration admin_reg_;
	
	/**
	 *  "The list of all bundles in the system" [DTN2]
	 */
	protected BundleList all_bundles_;

	/**
	 *  "Application-specific shutdown data" [DTN2]
	 */
	protected ServlibEventData app_shutdown_data_;

	/**
	 *  "Application-specific shutdown handler" [DTN2]
	 */
	protected ServlibEventHandler app_shutdown_proc_;

	/**
	 *  "The contact manager" [DTN2]
	 */
	protected ContactManager contactmgr_;

	/**
	 *  "The list of all bundles that we have custody of" [DTN2]
	 */
	protected BundleList custody_bundles_;

	/**
	 *  The event queue
	 */
	protected PriorityBlockingQueue<BundleEvent> eventq_;

	/**
	 *  "The fragmentation / reassembly manager" [DTN2]
	 */
	protected FragmentManager fragmentmgr_;

	/**
	 * The default EndpointID of this Daemon
	 */
	protected EndpointID local_eid_;


	/**
	 *  "The list of all bundles that are still being processed" [DTN2]
	 */
	protected BundleList pending_bundles_;

	/**
	 *  "The ping registration" [DTN2]
	 */
	protected PingRegistration ping_reg_;

	/**
	 *  "The table of active registrations" [DTN2]
	 */
	protected RegistrationTable reg_table_;

	/**
	 *  "The active bundle router" [DTN2]
	 */
	protected BundleRouter router_;

	/**
	 *  "Router-specific shutdown data" [DTN2]
	 */
	protected ServlibEventData rtr_shutdown_data_;

	/**
	 *  "Router-specific shutdown handler" [DTN2]
	 */
	protected ServlibEventHandler rtr_shutdown_proc_;

	/**
	 *  "Stats instance" [DTN2]
	 */
	protected Stats stats_;

	/**
	 * main constructor
	 */
	private BundleDaemon() {
		do_init();
	}

	/**
	 * Return the current actions handler.
	 */
	public BundleActions actions() {
		return actions_;
	}

	/**
	 * Accessor for the contact manager.
	 */
	public ContactManager contactmgr() {
		return contactmgr_;
	}

	/**
	 * Accessor for the custody bundles list.
	 */
	public BundleList custody_bundles() {
		return custody_bundles_;
	}

	/**
	 * Object initialization function
	 */
	public void do_init() {

		BundleProtocol.init_default_processors();
		shutting_down_ = false;
		actions_ = new BundleActions();
		contactmgr_ = ContactManager.getInstance();
		custody_bundles_ = new BundleList("custody_bundles");
		event_ticket_ = new Integer(0);
		eventq_ = new PriorityBlockingQueue<BundleEvent>(event_queue_capacity_, BundleEventPriorityComparator.getInstance());
		fragmentmgr_ = FragmentManager.getInstance();
		pending_bundles_ = new BundleList("pending_bundles");
		reg_table_ = RegistrationTable.getInstance();
		stats_ = new Stats();
		params_ = new Params();
	}

	/**
	 * Return the number of events in the queue
	 */
	public int event_queue_size() {

		return eventq_.size();

	}

	/**
	 * Accessor for the fragmentation manager.
	 */
	public FragmentManager fragmentmgr() {
		return fragmentmgr_;
	}

	/**
	 * Format the given StringBuffer with the current bundle statistics.
	 */
	public void get_bundle_stats(StringBuffer buf) {
		buf.append(String.format("%d pending -- " + "%d custody -- "
				+ "%d received -- " + "%d delivered -- " + "%d generated -- "
				+ "%d transmitted -- " + "%d expired -- " + "%d duplicate -- "
				+ "%d deleted -- " + "%d injected", pending_bundles_.size(),
				custody_bundles_.size(), stats_.received_bundles_,
				stats_.delivered_bundles_, stats_.generated_bundles_,
				stats_.transmitted_bundles_, stats_.expired_bundles_,
				stats_.duplicate_bundles_, stats_.deleted_bundles_,
				stats_.injected_bundles_

		)

		);

	}

	/**
	 * Format the given StringBuffer with the current internal statistics value.
	 */
	public void get_daemon_stats(StringBuffer buf) {
		buf.append(String.format(

		"%d pending_events -- " + "%d processed_events -- ",
				event_queue_size(), stats_.events_processed_

		));

	}

	/**
	 * Format the given StringBuffer with current routing info.
	 */
	public void get_routing_state(StringBuffer buf) {
		router_.get_routing_state(buf);
		contactmgr_.dump(buf);
	}

	/**
	 * Main event handling function.
	 */
	public void handle_event(BundleEvent event) {
		Log.i(TAG, String.format("BundleDaemon:handle_event %s", event
				.toString()));
		dispatch_event(event);

		if (!event.daemon_only()) { 
			// "dispatch the event to the router and also
			// the contact manager" [DTN2]
			router_.handle_event(event);
			contactmgr_.handle_event(event);
		}

		event_handlers_completed(event);

		stats_.events_processed_++;

		if (event.processed_notifier_ != null) {
			try {
				event.processed_notifier_.put(event_ticket_);
			} catch (InterruptedException e) {
				Log.e(TAG, "BundleDaemon: handle_event InterruptedException");
			}
		}
	}

	/**
	 * Initialzation from configuration object
	 */
	public void init(DTNConfiguration config) {
		local_eid_ = new EndpointID(config.routes_setting().local_eid());

	}

	/**
	 * Return the local endpoint identifier.
	 */
	public final EndpointID local_eid() {
		return local_eid_;
	}

	/**
	 * Accessor for the pending bundles list.
	 */
	public BundleList pending_bundles() {
		return pending_bundles_;
	}
    
	/**
	 * Queues the event at the tail of the queue for processing by the daemon
	 * thread.
	 */
	public void post(BundleEvent event) {
		post_event(event, true);
	}

	/**
	 * Post the given event and wait for it to be processed by the daemon thread
	 * or for the given timeout to elapse.
	 */
	public boolean post_and_wait(BundleEvent event,
			MsgBlockingQueue<Integer> notifier, int timeout, boolean at_back) {

		assert (event.processed_notifier() == null) : "BundleDeamon:post_and_wait, post_and_wait() event.process_notifier_ is not null";
		event.processed_notifier_ = notifier;
		if (at_back) {
			post(event);
		} else {
			post_at_head(event);
		}

		// ticket use for blocking and notifying queue
		Integer ticket = null;
		assert(ticket == null);
		
		if (timeout == -1) {
			// Indefinite timeout here
			// Block until we get the ticket
			try {
				ticket = notifier.take();
			} catch (InterruptedException e) {

			}
			return true;
		} else {
			// Use input timeout value here
			try {
				ticket = notifier.poll(timeout, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Log
						.e(TAG,
								"BundleDeamon, InterruptedException, post_and_wait case Finite timeout");
				return false;
			}
			return true;
		}

	}

	/**
	 * Queues the event at the head of the queue for processing by the daemon
	 * thread.
	 */
	public void post_at_head(BundleEvent event) {
		post_event(event, false);
	}

	/**
	 * Post event at the front or back of the queue according to at_back parameter
	 * @param the BundleEvent to be posted
	 * @param at_back true if it will be at the end of the queue, false if it will be at the front of the queue
	 */
	public void post_event(BundleEvent event, boolean at_back) {
		Log.d(TAG, String.format("posting event (%s) with type %s (at %s)",
				event.toString(), event.type().toString(), at_back ? "back"
						: "head"));
		event.set_posted_time(Calendar.getInstance().getTime());
		;
		if (!at_back)
		{
			// if it's not at back , set the priority to higher than the head of the queue
			if (eventq_.size() > 0)
			{
			int highest_priority = eventq_.peek().priority();
			event.set_priority(highest_priority + 1 );
		
			}
		}
		
		eventq_.put(event);
	

	}

	/**
	 * Accessor for the registration table.
	 */
	public final RegistrationTable reg_table() {
		return reg_table_;
	}

	/**
	 * Reset all internal stats.
	 */
	public void reset_stats() {
		stats_ = new Stats();

		contactmgr_.get_lock().lock();
		try {
			LinkSet links = contactmgr_.links();
			Iterator<Link> itr = links.iterator();
			while (itr.hasNext()) {
				Link link = itr.next();
				link.reset_stats();
			}
		} finally {
			contactmgr_.get_lock().unlock();
		}
	}

	/**
	 * Returns the current bundle router.
	 */
	public BundleRouter router() {
		assert (router_ != null) : "BundleDaemon router_ is null";
		return router_;
	}

	/**
	 * "Main thread function that dispatches events." [DTN2]
	 */
	public void run() {

		try {
			// Create router according to config in the configuration process
			router_ = BundleRouter.create_router();

			load_registrations();
			load_bundles();

			while (true) {
				if (shutting_down_ ) {
					Log.d(TAG, "BundleDaemon: shutting down");
					break;
				}

				BundleEvent event;
				try {
					event = eventq_.take();

					handle_event(event);
				} catch (InterruptedException e) {
					Log.e(TAG, "Event Handle Interuptted Exception");
				}

				//             
			}

			Log.d(TAG, "BundleDaemon: at the end of run() of Daemon");
		} catch (RoutingException e1) {
			Log.e(TAG, "BundleDeamon:run(), UnknownRouterType ");
		}
	}

	/**
	 * "Set an application-specific shutdown handler." [DTN2]
	 */
	public void set_app_shutdown(ServlibEventHandler proc, ServlibEventData data) {
		app_shutdown_proc_ = proc;
		app_shutdown_data_ = data;
	}

	/**
	 * Set the local endpoint id.
	 */
	public void set_local_eid(final String eid_str) {
		local_eid_.assign(eid_str);
	}

	/**
	 * Returns the current bundle router.
	 */
	public void set_router(BundleRouter router) {

		router_ = router;
	}

	/**
	 * "Set a router-specific shutdown handler." [DTN2]
	 */
	public void set_rtr_shutdown(ServlibEventHandler proc, ServlibEventData data) {
		rtr_shutdown_proc_ = proc;
		rtr_shutdown_data_ = data;
	}

	/**
	 *  Start the Bundle Daemon by executing a new thread
	 */
	public void start() {
		shutting_down_ = false;
		thread_ = new Thread(this);
		thread_.start();

	}

	/**
	 * Test function for getting this class event Queue
	 * @return
	 */
	public PriorityBlockingQueue<BundleEvent> test_get_eventq() {
		return eventq_;
	}

	/**
	 * "Take custody for the given bundle, sending the appropriate signal to the
	 * current custodian." [DTN2]
	 */
	protected void accept_custody(Bundle bundle) {
		Log.i(TAG, String.format("accept_custody bundle id %d", bundle
				.bundleid()));

		if (bundle.local_custody()) {
			Log
					.e(
							TAG,
							String
									.format(
											"accept_custody( bundle id %d): already have local custody",
											bundle.bundleid()));
			return;
		}

		if (bundle.custodian().equals(local_eid_)) {
			Log.e(TAG, String.format("send_custody_signal(%d): "
					+ "current custodian is already local_eid", bundle
					.bundleid()));
			return;
		}

		// "send a custody acceptance signal to the current custodian (if
		// it is someone, and not the null eid)" [DTN2]
		if (!bundle.custodian().equals(EndpointID.NULL_EID())) {
			generate_custody_signal(
					bundle,
					true,
					BundleProtocol.custody_signal_reason_t.CUSTODY_NO_ADDTL_INFO);
		}

		// "now we mark the bundle to indicate that we have custody and add
		// it to the custody bundles list" [DTN2]
		bundle.custodian().assign(local_eid_);
		bundle.set_local_custody(true);
		bundle.set_complete(true);
		actions_.store_update(bundle);

		custody_bundles_.push_back(bundle);

		// "finally, if the bundle requested custody acknowledgments,
		// deliver them now" [DTN2]
		if (bundle.custody_rcpt()) {
			generate_status_report(bundle,
					BundleStatusReport.flag_t.STATUS_CUSTODY_ACCEPTED,
					BundleProtocol.status_report_reason_t.REASON_NO_ADDTL_INFO);
		}
	}

	/**
	 * "Add the bundle to the pending list and (optionally) the persistent store,
	 * and set up the expiration timer for it." [DTN2]
	 * 
	 * @return true "if the bundle is legal to be delivered and/or forwarded,
	 *         false if it's already expired" [DTN2]
	 */
	protected boolean add_to_pending(Bundle bundle, boolean add_to_store) {
		Log.d(TAG, String.format("adding bundle id %d to pending list", bundle
				.bundleid()));

		pending_bundles_.push_back(bundle);

		if (add_to_store) {
			bundle.set_complete(true);
			actions_.store_update(bundle);
		}

		// "schedule the bundle expiration timer" [DTN2]
		Calendar expiration_calendar = Calendar.getInstance();
		expiration_calendar.setTimeInMillis(System.currentTimeMillis() + bundle.expiration() * 1000);
		
		Calendar now_Calendar = Calendar.getInstance();

		long when = TimeHelper.seconds_from_ref(expiration_calendar)
				- TimeHelper.seconds_from_ref(now_Calendar);
		boolean ok_to_route = true;

		bundle.set_expiration_timer(new ExpirationTimer(bundle));
		if (expiration_calendar.getTime().after(now_Calendar.getTime())) {
			Log.d(TAG, String.format(TAG,
					"scheduling expiration for bundle id %d at %d.%d "
							+ "(in %d seconds)", bundle.bundleid(), TimeHelper
							.seconds_from_ref(expiration_calendar),
					expiration_calendar.get(Calendar.MILLISECOND), when));

			bundle.expiration_timer()
					.schedule_at(expiration_calendar.getTime());

		} else {
			Log
					.w(
							TAG,
							String
									.format(
											"scheduling IMMEDIATE expiration for bundle id %d: "
													+ "[expiration %d, creation time %d.%d, offset %d, now %d.%d], Expire Time = %s",
											bundle.bundleid(),
											bundle.expiration(),
											bundle.creation_ts().seconds(),
											bundle.creation_ts().seqno(),
											BundleTimestamp.TIMEVAL_CONVERSION,
											TimeHelper
													.seconds_from_ref(now_Calendar),
											now_Calendar
													.get(Calendar.MILLISECOND),
											now_Calendar.getTime().toString()));
			bundle.expiration_timer().schedule_at(now_Calendar.getTime());
			ok_to_route = false;
		}

		return ok_to_route;
	}

	/**
	 * "Cancel any pending custody timers for the bundle." [DTN2]
	 */
	protected void cancel_custody_timers(Bundle bundle) {
		bundle.get_lock().lock();
		try {

			Iterator<CustodyTimer> iter = bundle.custody_timers().iterator();
			while (iter.hasNext()) {
				CustodyTimer timer = iter.next();
				timer.cancel();

			}

			bundle.custody_timers().clear();
		} finally {
			bundle.get_lock().unlock();
		}
	}

	/**
	 * "Check the registration table and optionally deliver the bundle to any
	 * that match." [DTN2]
	 * 
	 * @return "whether or not any matching registrations were found or if the
	 *         bundle is destined for the local node" [DTN2]
	 */
	protected boolean check_local_delivery(Bundle bundle, boolean deliver) {
		Log.d(TAG, String.format(
				"checking for matching registrations for bundle id %d", bundle
						.bundleid()));

		RegistrationList matches = new RegistrationList();

		reg_table_.get_matching(bundle.dest(), matches);

		if (deliver) {
			assert (!bundle.is_fragment()) : "BundleDaemon:check_local_delivery, bundle is fragmented";

			Iterator<Registration> iter = matches.iterator();

			while (iter.hasNext()) {
				Registration registration = iter.next();
				deliver_to_registration(bundle, registration);
			}
		}

		return (matches.size() > 0) || bundle.dest().subsume(local_eid_);
	}

	/**
	 * "Delete (rather than silently discard) a bundle, e.g., an expired bundle.
	 * Releases custody of the bundle, removes fragmentation state for the
	 * bundle if necessary, removes the bundle from the pending list, and sends
	 * a bundle deletion status report if necessary." [DTN2]
	 */
	protected boolean delete_bundle(final Bundle bundle,
			status_report_reason_t reason) {

		if (bundle == null) Log.e(TAG, " bundle in delete_bundle is null ");
		
		++stats_.deleted_bundles_;

		// "send a bundle deletion status report if we have custody or the
		// bundle's deletion status report request flag is set and a reason
		// for deletion is provided" [DTN2]
		boolean send_status = (bundle.local_custody() || (bundle
				.deletion_rcpt() && reason != BundleProtocol.status_report_reason_t.REASON_NO_ADDTL_INFO));

		// "check if we have custody, if so, remove it" [DTN2]
		if (bundle.local_custody()) {
			release_custody(bundle);
		}

		// "check if bundle is a fragment, if so, remove any fragmentation state" [DTN2]
		if (bundle.is_fragment()) {
			fragmentmgr_.delete_fragment(bundle);
		}

		// "notify the router that it's time to delete the bundle" [DTN2]
		router_.delete_bundle(bundle);

		// "delete the bundle from the pending list" [DTN2]
		Log.d(TAG, String.format("pending_bundles size %d", pending_bundles_
				.size()));
		boolean erased = true;
		if (bundle.is_queued_on(pending_bundles_)) {
			erased = delete_from_pending(bundle);
		}

		if (erased && send_status) {
			generate_status_report(bundle,
					BundleStatusReport.flag_t.STATUS_DELETED, reason);
		}

		// "cancel the bundle on all links where it is queued or in flight" [DTN2]
		Date now = Calendar.getInstance().getTime();
		contactmgr_.get_lock().lock();
		try {
			Iterator<Link> itr = contactmgr_.links().iterator();

			while (itr.hasNext()) {
				Link link = itr.next();
				if (link.queue().contains(bundle)
						|| link.inflight().contains(bundle)) {
					actions_.cancel_bundle(bundle, link);
				}

			
			}

			Log
					.d(
							TAG,
							String
									.format(
											"BundleDaemon: canceling deleted bundle on all links took %d ms",
											TimeHelper.elapsed_ms(now)));
			
			BundleDaemon.getInstance().post_at_head(new BundleFreeEvent(bundle));

			return erased;
		} finally {
			contactmgr_.get_lock().unlock();
		}
	}

	/**
	 * "Remove the bundle from the pending list and data store, and cancel the
	 * expiration timer." [DTN2]
	 */
	protected boolean delete_from_pending(final Bundle bundle) {
		Log.d(TAG, String.format("removing bundle %d from pending list", bundle
				.bundleid()));

		// "first try to cancel the expiration timer if it's still around" [DTN2]
		if (bundle.expiration_timer() != null) {
			Log.d(TAG, String.format(
					"cancelling expiration timer for bundle id %d", bundle
							.bundleid()));

			bundle.expiration_timer().cancel();
			if (!bundle.expiration_timer().cancelled()) {
				Log.e(TAG, String.format(
						"unexpected error cancelling expiration timer "
								+ "for bundle %d", bundle.bundleid()));
			}

			bundle.set_expiration_timer(null);
		}

		Log.d(TAG, String.format("pending_bundles size %d", pending_bundles_
				.size()));

		boolean erased = pending_bundles_.erase(bundle, false);

		if (!erased) {
			Log.e(TAG, "unexpected error removing bundle from pending list");
		}

		return erased;
	}

	/**
	 * "Deliver the bundle to the given registration" [DTN2]
	 */
	protected void deliver_to_registration(Bundle bundle,
			Registration registration) {
		assert (!bundle.is_fragment()) : "BundleDaemon:deliver_to_registration, bundle is fragmented!";

		ForwardingInfo.state_t state = bundle.fwdlog().get_latest_entry_state(
				registration);
		if (state != ForwardingInfo.state_t.NONE) {
			assert (state == ForwardingInfo.state_t.DELIVERED) : "BundleDaemon:deliver_to_registration, state is not None but is not Delivered as well!";
			Log.d(TAG, String.format(
					"not delivering bundle id %d to registration %d (%s) "
							+ "since already delivered", bundle.bundleid(),
					registration.regid(), registration.endpoint().toString()));
			return;
		}

		Log.d(TAG, String.format(
				"delivering bundle id %d to registration %d (%s)", bundle
						.bundleid(), registration.regid(), registration
						.endpoint().toString()));

		if (registration.deliver_if_not_duplicate(bundle)) {
			bundle.fwdlog().add_entry(registration,
					ForwardingInfo.action_t.FORWARD_ACTION,
					ForwardingInfo.state_t.DELIVERED);
		} else {
			Log.i(TAG, String.format(
					"suppressing duplicate delivery of bundle %d "
							+ "to registration %d (%s)", bundle.bundleid(),
					registration.regid(), registration.endpoint().toString()));
		}
	}

	/**
	 * "Routine executing after the every completion of the Bundle Event" [DTN2]
	 * @param event
	 */
	protected void event_handlers_completed(BundleEvent event) {
		Log.d(TAG, String.format("Event Handler Complete for (%s) %s", event
				.toString(), event.type().toString()));

		/**
		 * "Once bundle reception, transmission or delivery has been processed by
		 * the router, check to see if it's still needed, otherwise we delete
		 * it." [DTN2]
		 */
		Bundle bundle = null;

		if (event.type() == event_type_t.BUNDLE_TRANSMITTED) {
			bundle = ((BundleTransmittedEvent) event).bundle();

		}

		if (event.type() == event_type_t.BUNDLE_DELIVERED) {
			bundle = ((BundleDeliveredEvent) event).bundle();

		}

		if (bundle != null) {
			try_to_delete(bundle);
		}

		/**
		 * "Once the bundle expired event has been processed, the bundle
		 * shouldn't exist on any more lists." [DTN2]
		 */
		if (event.type() == event_type_t.BUNDLE_EXPIRED) {
			bundle = ((BundleExpiredEvent) event).bundle();
			int num_mappings = bundle.num_mappings();
			if (num_mappings != 0) {
				Log
						.w(
								TAG,
								String
										.format(
												"BundleDaemon:event_handlers_completed, expired bundle %s still has %d mappings ",
												bundle, num_mappings));
			}
		}

	}

	/**
	 * "Check if there are any bundles in the pending queue that match the source
	 * id, timestamp, and fragmentation offset/length fields." [DTN2]
	 */
	protected Bundle find_duplicate(Bundle b) {

		pending_bundles_.get_lock().lock();
		try {
			Log.d(TAG, String.format("pending_bundles size %d",
					pending_bundles_.size()));
			Bundle found = null;

			ListIterator<Bundle> iter = pending_bundles_.begin();

			while (iter.hasNext()) {
				Bundle b2 = iter.next();

				if ((b.source().equals(b2.source()))
						&& (b.creation_ts().seconds() == b2.creation_ts()
								.seconds())
						&& (b.creation_ts().seqno() == b2.creation_ts().seqno())
						&& (b.is_fragment() == b2.is_fragment())
						&& (b.frag_offset() == b2.frag_offset()) &&
						/* (b.orig_length() == b2.orig_length()) && */
						(b.payload().length() == b2.payload().length())) {
					// b is a duplicate of b2
					
					Log.d(TAG, "BUNDLE DUPLICATE: newly received bundle ( id = " + b2.bundleid() + " )  is a duplicate of bundle id " + b.bundleid());
					found = b2;
			
					if (params_.suppress_duplicates_ || b2.local_custody())
						break;

				}

			}

			return found;

		} catch (BundleListLockNotHoldByCurrentThread e) {
			Log
					.e(
							TAG,
							"BundleDaemon:find_duplicate, BundleList Lock Not Hold by current Thread Exception");
			return null;
		} finally {
			pending_bundles_.get_lock().unlock();
		}
	}

	/**
	 * "Generate a custody signal to be sent to the current custodian." [DTN2]
	 */
	protected void generate_custody_signal(Bundle bundle, boolean succeeded,
			custody_signal_reason_t reason) {
		if (bundle.local_custody()) {
			Log
					.e(
							TAG,
							String
									.format(
											"send_custody_signal(bundle id %d): already have local custody",
											bundle.bundleid()));
			return;
		}

		if (bundle.custodian().equals(EndpointID.NULL_EID())) {
			Log
					.e(
							TAG,
							String
									.format(
											"send_custody_signal(bundle id %d): current custodian is NULL_EID",
											bundle.bundleid()));
			return;
		}

		Bundle signal = CustodySignal.create_custody_signal(bundle, local_eid_,
				succeeded, reason);

		BundleReceivedEvent e = new BundleReceivedEvent(signal,
				event_source_t.EVENTSRC_ADMIN);
		handle_event(e);
	}

	/**
	 * "Locally generate a status report for the given bundle." [DTN2]
	 */
	protected void generate_status_report(Bundle orig_bundle,
			BundleStatusReport.flag_t flag, status_report_reason_t reason) {
		Log
				.d(TAG, String.format(
						"generating return receipt status report, "
								+ "flag = %s, reason = %s", flag.toString(),
						reason.toString()));

		Bundle report = BundleStatusReport.create_status_report(orig_bundle,
				local_eid_, flag.getCode(), reason);

		BundleReceivedEvent e = new BundleReceivedEvent(report,
				event_source_t.EVENTSRC_ADMIN);
		handle_event(e);
	}

	protected void handle_bundle_accept(BundleAcceptRequest request) {
		boolean[] result_value = request.result();
		result_value[0] = router_.accept_bundle(request.bundle(), request
				.reason());

		String reason_string = request.reason()[0] != null ? request.reason()[0]
				.toString()
				: "no reason";
		Log.i(TAG, String.format(
				"BUNDLE_ACCEPT_REQUEST: bundle %d %s (reason %s)", request
						.bundle().bundleid(), result_value[0] ? "accepted"
						: "not accepted", reason_string));
	}

	protected void handle_bundle_cancel(BundleCancelRequest event) {
		Bundle bundle = event.bundle();

		if (bundle == null) {
			Log.e(TAG, "NULL bundle object in BundleCancelRequest");
			return;
		}

		// "If the request has a link name, we are just canceling the send on
		// that link." [DTN2]
		if (!event.link().name_str().equals("")) {
			Link link = contactmgr_.find_link(event.link().name_str());
			if (link == null) {
				Log.e(TAG, String.format("BUNDLE_CANCEL no link with name %s",
						event.link().name_str()));
				return;
			}

			Log.i(TAG, String.format("BUNDLE_CANCEL bundle %d on link %s",
					bundle.bundleid(), event.link().name()));

			Log.d(TAG, " handle_bundle_cancel");
			
			if (bundle == null)
			{
				Log.e(TAG, "bundle is null in handle cancel bundle");
			}
			actions_.cancel_bundle(bundle, link);
		}

		// "If the request does not have a link name, the bundle itself has been
		// canceled (probably by an application)." [DTN2]
		else {
			delete_bundle(bundle,
					BundleProtocol.status_report_reason_t.REASON_NO_ADDTL_INFO);
		}

	}

	protected void handle_bundle_cancelled(BundleSendCancelledEvent event) {
		Bundle bundle = event.bundle();
		Link link = event.link();

		Log.i(TAG, String.format("BUNDLE_CANCELLED id:%d -> %s (%s)", bundle
				.bundleid(), link.name(), link.nexthop()));

		Log.d(TAG, String.format(
				"trying to find xmit blocks for bundle id:%d on link %s",
				bundle.bundleid(), link.name()));
		BlockInfoVec blocks = bundle.xmit_link_block_set().find_blocks(link);

		// "Because a CL is running in another thread or process (External CLs),
		// we cannot prevent all redundant transmit/cancel/transmit_failed
		// messages. If an event about a bundle bound for particular link is
		// posted after another, which it might contradict, the BundleDaemon
		// need not reprocess the event. The router (DP) might, however, be
		// interested in the new status of the send." [DTN2]
		if (blocks == null) {
			Log.i(TAG, String.format(
					"received a redundant/conflicting bundle_cancelled event "
							+ "about bundle id:%d -> %s (%s)", bundle
							.bundleid(), link.name(), link.nexthop()

			));
			return;
		}

		/*
		 * "The bundle should no longer be on the link queue or on the inflight
		 * queue if it was cancelled." [DTN2]
		 */
		if (link.queue().contains(bundle)) {
			Log.w(TAG, String.format(
					"cancelled bundle id:%d still on link %s queue", bundle
							.bundleid(), link.name()));
		}

		/*
		 * "The bundle should no longer be on the link queue or on the inflight
		 * queue if it was cancelled." [DTN2]
		 */
		if (link.inflight().contains(bundle)) {
			Log.w(TAG, String.format(
					"cancelled bundle id:%d still on link %s inflight list",
					bundle.bundleid(), link.name()));
		}

		/*
		 * "Update statistics. Note that the link's queued length must always be
		 * decremented by the full formatted size of the bundle." [DTN2]
		 */
		link.stats()
				.set_bundles_cancelled(link.stats().bundles_cancelled() + 1);

		/*
		 * "Remove the formatted block info from the bundle since we don't need
		 * it any more." [DTN2]
		 */
		Log.d(TAG, String.format(
				"trying to delete xmit blocks for bundle id:%d on link %s",
				bundle.bundleid(), link.name()));
		BundleProtocol.delete_blocks(bundle, link);
		blocks = null;

		/*
		 * "Update the forwarding log." [DTN2]
		 */
		Log.d(TAG, String.format("trying to update the forwarding log for "
				+ "bundle id:%d on link %s to state CANCELLED", bundle
				.bundleid(), link.name()));
		bundle.fwdlog().update(link, ForwardingInfo.state_t.CANCELLED);
	}

	protected void handle_bundle_delete(BundleDeleteRequest request) {
		if (request.bundle() != null) {
			Log.i(TAG, String.format("BUNDLE_DELETE: bundle %d (reason %s)",
					request.bundle().bundleid(), request.reason().toString()));
			delete_bundle(request.bundle(), request.reason());
		}
	}

	protected void handle_bundle_delivered(BundleDeliveredEvent event) {
		
		stats_.delivered_bundles_++;

		/*
		 * "The bundle was delivered to a registration." [DTN2]
		 */
		Bundle bundle = event.bundle();

		Log.i(TAG, String.format(
				"BUNDLE_DELIVERED id:%d (%d bytes) . regid %d (%s)", bundle
						.bundleid(), bundle.payload().length(), event
						.registration().regid(), event.registration()
						.endpoint().toString()));

		/*
		 * "Generate the delivery status report if requested." [DTN2]
		 */
		if (bundle.delivery_rcpt()) {
			generate_status_report(bundle,
					BundleStatusReport.flag_t.STATUS_DELIVERED,
					BundleProtocol.status_report_reason_t.REASON_NO_ADDTL_INFO);
		}

		/*
		 * "If this is a custodial bundle and it was delivered, we either release
		 * custody (if we have it), or send a custody signal to the current
		 * custodian indicating that the bundle was successfully delivered,
		 * unless there is no current custodian (the eid is still dtn:none).| [DTN2]
		 */
		if (bundle.custody_requested()) {
			if (bundle.local_custody()) {
				release_custody(bundle);

			} else if (bundle.custodian().equals(EndpointID.NULL_EID())) {
				Log
						.i(
								TAG,
								String
										.format(
												"custodial bundle %d delivered before custody accepted",
												bundle.bundleid()));

			} else {
				generate_custody_signal(
						bundle,
						true,
						BundleProtocol.custody_signal_reason_t.CUSTODY_NO_ADDTL_INFO);
			}
		}
	}

	protected void handle_bundle_expired(BundleExpiredEvent event) {
		stats_.expired_bundles_++;

		Bundle bundle = event.bundle();

		Log.i(TAG, String.format("BUNDLE_EXPIRED bundle ID %d", bundle
				.bundleid()));


		delete_bundle(bundle,
				BundleProtocol.status_report_reason_t.REASON_LIFETIME_EXPIRED);

	}

	protected void handle_bundle_free(BundleFreeEvent event) {
		Bundle bundle = event.bundle();
		event.set_bundle(null);

		bundle.get_lock().lock(); 
		try {
		
			Log.d(TAG, "removing freed bundle from data store");
			actions_.store_del(bundle);
		
			Log.d(TAG, "deleting freed bundle");
		} finally {
			bundle.get_lock().unlock();
		}

	}

	protected void handle_bundle_inject(BundleInjectRequest event) {

		EndpointID src = event.src();
		EndpointID dest = event.dest();
		if ((!src.valid()) || (!dest.valid()))
			return;

		// "The bundle's source EID must be either dtn:none or an EID
		// registered at this node." [DTN2]
		RegistrationTable reg_table = BundleDaemon.getInstance().reg_table();
		String base_reg_str = src.uri().getScheme() + "://"
				+ src.uri().getHost();

		Registration reg = reg_table.get(new EndpointIDPattern(base_reg_str));
		if (reg == null && src.equals(EndpointID.NULL_EID())) {
			Log.e(TAG, String.format(
					"this node is not a member of the injected bundle's source "
							+ "EID (%s)", src.toString()));
			return;
		}

		// "The new bundle is placed on the pending queue but not
		// in durable storage (no call to BundleActions::inject_bundle)" [DTN2]
		Bundle bundle = new Bundle(
				params_.injected_bundles_in_memory_ ? BundlePayload.location_t.MEMORY
						: BundlePayload.location_t.DISK);

		bundle.source().assign(src);
		bundle.dest().assign(dest);

		if (!bundle.replyto().assign(event.replyto()))
			bundle.replyto().assign(EndpointID.NULL_EID());

		if (!bundle.custodian().assign(event.custodian()))
			bundle.custodian().assign(EndpointID.NULL_EID());

		// "bundle COS defaults to COS_BULK" [DTN2]
		bundle.set_priority(event.priority_values());

		// "bundle expiration on remote dtn nodes
		// defaults to 5 minutes" [DTN2]

		if (event.expiration() == 0)
			bundle.set_expiration(300);
		else
			bundle.set_expiration(event.expiration());

		// "set the payload then removing original" [DTN2]

		bundle.payload().replace_with_file(event.payload_file());

		Log.d(TAG, String.format(
				"bundle payload size after replace_with_file(): %d", bundle
						.payload().length()));
		File event_payload_file = event.payload_file();
		event_payload_file.delete();

		/*
		 * "Deliver the bundle to any local registrations that it matches, unless
		 * it's generated by the router or is a bundle fragment. Delivery of
		 * bundle fragments is deferred until after re-assembly." [DTN2]
		 */
		boolean is_local = check_local_delivery(bundle, !bundle.is_fragment());

		/*
		 * "Re-assemble bundle fragments that are destined to the local node." [DTN2]
		 */
		if (bundle.is_fragment() && is_local) {
			Log.d(TAG, String.format(
					"deferring delivery of injected bundle %d "
							+ "since bundle is a fragment", bundle.bundleid()));
			fragmentmgr_.process_for_reassembly(bundle);
		}

		// "The injected bundle is no longer sent automatically. It is
		// instead added to the pending queue so that it can be resent
		// or sent on multiple links.

		// If add_to_pending returns false, the bundle has already expired" [DTN2]
		if (add_to_pending(bundle, false))
			BundleDaemon.getInstance().post(
					new BundleInjectedEvent(bundle, event.request_id()));

		++stats_.injected_bundles_;
	}

	protected void handle_bundle_injected(BundleInjectedEvent event) {

	}

	protected void handle_bundle_query(BundleQueryRequest event) {
		BundleDaemon.getInstance().post_at_head(new BundleReportEvent());
	}

	protected void handle_bundle_queued_query(BundleQueuedQueryRequest request) {
		Link link = request.link();
		assert (link != null) : "BundleDaemon:handle_bundle_queued_query, link is null";
		assert (link.clayer() != null) : "BundleDaemon:handle_bundle_queued_query, CLayer is null";

		Log.e(TAG, String.format("BundleDaemon::handle_bundle_queued_query: "
				+ "query %s, checking if bundle %d is queued on link %s",
				request.query_id(), request.bundle().bundleid(), link.name()));

		boolean is_queued = request.bundle().is_queued_on(link.queue());
		post(new BundleQueuedReportEvent(request.query_id(), is_queued));
	}

	protected void handle_bundle_queued_report(BundleQueuedReportEvent event) {

		Log.e(TAG, String.format(
				"BundleDaemon::handle_bundle_queued_report: query %s, %s",
				event.query_id(), event.is_queued() ? "true" : "false"));
	}

	protected void handle_bundle_received(BundleReceivedEvent event) {

		Bundle bundle = event.bundle();
		
		
		Log.d(TAG, " handle bundle received from " + event.source() + ", id = " + bundle.bundleid());
		// "update statistics and store an appropriate event descriptor" [DTN2]
		String source_str = "";
		switch (event.source()) {
		case EVENTSRC_PEER:
			stats_.received_bundles_++;
			DTNManager.getInstance().notify_user("DTN Bundle Received", "From " + bundle.source().toString());
			
			break;

		case EVENTSRC_APP:
			stats_.received_bundles_++;
			source_str = " (from app)";
			break;

		case EVENTSRC_STORE:
			source_str = " (from data store)";
			break;

		case EVENTSRC_ADMIN:
			stats_.generated_bundles_++;
			source_str = " (generated)";
			break;

		case EVENTSRC_FRAGMENTATION:
			stats_.generated_bundles_++;
			source_str = " (from fragmentation)";
			break;

		case EVENTSRC_ROUTER:
			stats_.generated_bundles_++;
			source_str = " (from router)";
			break;

		default:
			Log.e(TAG, "Bundle Daemon: handle_bundle_received");
		}

		StringBuffer buf = new StringBuffer();
		bundle.format(buf);
		Log
				.i(
						TAG,
						String
								.format(
										"BUNDLE_RECEIVED %s bundle id (%d) prevhop %s (%d bytes recvd)",
										source_str, bundle.bundleid(), event
												.prevhop().toString(), event
												.bytes_received()));

		// log the reception in the bundle's forwarding log
		if (event.source() == event_source_t.EVENTSRC_PEER
				&& event.link() != null) {
			bundle.fwdlog().add_entry(event.link(),
					ForwardingInfo.action_t.FORWARD_ACTION,
					ForwardingInfo.state_t.RECEIVED);
		} else if (event.source() == event_source_t.EVENTSRC_APP) {
			if (event.registration() != null) {
				bundle.fwdlog().add_entry(event.registration(),
						ForwardingInfo.action_t.FORWARD_ACTION,
						ForwardingInfo.state_t.RECEIVED);
			}
		}

		// "log a warning if the bundle doesn't have any expiration time or
		// has a creation time that's in the future. in either case, we
		// proceed as normal" [DTN2]

		if (bundle.expiration() == 0) {
			Log.w(TAG, String.format(
					"bundle id %d arrived with zero expiration time", bundle
							.bundleid()));
		}

		long now = TimeHelper.current_seconds_from_ref();
		if ((bundle.creation_ts().seconds() > now)
				&& (bundle.creation_ts().seconds() - now > 30000)) {
			Log.w(TAG, String.format(
					"bundle id %d arrived with creation time in the future "
							+ "(%d > %d)", bundle.bundleid(), bundle
							.creation_ts().seconds(), now));
		}

		/*
		 * "If a previous hop block wasn't included, but we know the remote
		 * endpoint id of the link where the bundle arrived, assign the prevhop_
		 * field in the bundle so it's available for routing." [DTN2]
		 */
		if (event.source() == event_source_t.EVENTSRC_PEER) {

			if (bundle.prevhop() == null || bundle.prevhop().uri() == null) {
				bundle.set_prevhop(new EndpointID(EndpointID.NULL_EID()));
			}

			if (bundle.prevhop().is_null()) {
				bundle.prevhop().assign(event.prevhop());
			}

			if (!bundle.prevhop().equals(event.prevhop())) {
				Log
						.w(
								TAG,
								String
										.format(
												"previous hop mismatch: prevhop header contains '%s' but "
														+ "convergence layer indicates prevhop is '%s'",
												bundle.prevhop().toString(),
												event.prevhop().toString()));
			}
		}

	
		/*
		 * "validate a bundle, including all bundle blocks, received from a peer" [DTN2]
		 */
		if (event.source() == event_source_t.EVENTSRC_PEER) {

			/*
			 * "Check all BlockProcessors to validate the bundle. Initialize the
			 * value in case the Bundle Protocol didn't give reason" [DTN2]
			 */
			status_report_reason_t[] reception_reason = new status_report_reason_t[1];
			reception_reason[0] = status_report_reason_t.REASON_NO_ADDTL_INFO;
			status_report_reason_t[] deletion_reason = new status_report_reason_t[1];
			deletion_reason[0] = status_report_reason_t.REASON_NO_ADDTL_INFO;

			boolean valid = BundleProtocol.validate(bundle, reception_reason,
					deletion_reason);

			/*
			 * "Send the reception receipt if requested within the primary block
			 * or some other error occurs that requires a reception status
			 * report but may or may not require deleting the whole bundle." [DTN2]
			 */
			if (bundle.receive_rcpt()
					|| reception_reason[0] != BundleProtocol.status_report_reason_t.REASON_NO_ADDTL_INFO) {
				generate_status_report(bundle,
						BundleStatusReport.flag_t.STATUS_RECEIVED,
						reception_reason[0]);
			}

			/* 
			 * "If the bundle is valid, probe the router to see if it wants to
			 * accept the bundle." [DTN2]
			 */
			boolean accept_bundle = false;
			if (valid) {
				BundleProtocol.status_report_reason_t[] reason = new BundleProtocol.status_report_reason_t[1];
				// "initialize the value in case the router didn't set the value
				// reason for us" [DTN2]
				reason[0] = status_report_reason_t.REASON_NO_ADDTL_INFO;
				accept_bundle = router_.accept_bundle(bundle, reason);
				deletion_reason[0] = reason[0];
			}

			/*
			 * "Delete a bundle if a validation error was encountered or the
			 * router doesn't want to accept the bundle, in both cases not
			 * giving the reception event to the router." [DTN2]
			 */
			if (!accept_bundle) {
				delete_bundle(bundle, deletion_reason[0]);
				event.set_daemon_only(true);
				return;
			}
		}

		/*
		 * "Check if the bundle is a duplicate, i.e. shares a source id,
		 * timestamp, and fragmentation information with some other bundle in
		 * the system." [DTN2]
		 */
		Bundle duplicate = find_duplicate(bundle);
		if (duplicate != null) {
			Log.i(TAG, String.format(
					"got duplicate bundle: %s . %s creation timestamp %d.%d",
					bundle.source().toString(), bundle.dest().toString(),
					bundle.creation_ts().seconds(), bundle.creation_ts()
							.seqno()));

			stats_.duplicate_bundles_++;

			if (bundle.custody_requested() && duplicate.local_custody()) {
				generate_custody_signal(
						bundle,
						false,
						BundleProtocol.custody_signal_reason_t.CUSTODY_REDUNDANT_RECEPTION);
			}

			if (params_.suppress_duplicates_) {
				// "since we don't want the bundle to be processed by the rest
				// of the system, we mark the event as daemon_only (meaning it
				// won't be forwarded to routers) and return, which should
				// eventually remove all references on the bundle and then it
				// will be deleted" [DTN2]
				event.set_daemon_only(true);
				return;
			}

			// "The BP says that the "dispatch pending" retention constraint
			// must be removed from this bundle if there is a duplicate we
			// currently have custody of. This would cause the bundle to have
			// no retention constraints and it now "may" be discarded. Assuming
			// this means it is supposed to be discarded, we have to suppress
			// a duplicate in this situation regardless of the parameter
			// setting. We would then be relying on the custody transfer timer
			// to cause a new forwarding attempt in the case of routing loops
			// instead of the receipt of a duplicate, so in theory we can indeed
			// suppress this bundle. It may not be strictly required to do so,
			// in which case we can remove the following block." [DTN2]
			if (bundle.custody_requested() && duplicate.local_custody()) {
				event.set_daemon_only(true);
				return;
			}

		}

		/*
		 * "Add the bundle to the master pending queue and the data store (unless
		 * the bundle was just reread from the data store on startup)
		 * 
		 * Note that if add_to_pending returns false, the bundle has already
		 * expired so we immediately return instead of trying to deliver and/or
		 * forward the bundle. Otherwise there's a chance that expired bundles
		 * will persist in the network." [DTN2]
		 */
		boolean ok_to_route = add_to_pending(bundle,
				(event.source() != event_source_t.EVENTSRC_STORE));

		if (!ok_to_route) {
			event.set_daemon_only(true);
			return;
		}

		/*
		 * "If the bundle is a custody bundle and we're configured to take
		 * custody, then do so. In case the event was delivered due to a reload
		 * from the data store, then if we have local custody, make sure it's
		 * added to the custody bundles list." [DTN2]
		 */
		if (bundle.custody_requested() && params_.accept_custody_
				&& (duplicate == null || !duplicate.local_custody())) {
			if (event.source() != event_source_t.EVENTSRC_STORE) {
				accept_custody(bundle);

			} else if (bundle.local_custody()) {
				custody_bundles_.push_back(bundle);
			}
		}

		/*
		 * "If this bundle is a duplicate and it has not been suppressed, we can
		 * assume the bundle it duplicates has already been delivered or added
		 * to the fragment manager if required, so do not do so again. We can
		 * bounce out now. Comments/jmmikkel If the extension blocks differ and
		 * we care to do something with them, we can't bounce out quite yet." [DTN2]
		 */
		if (duplicate != null) {
			// We have to delete the Bundle here
			delete_bundle(bundle, status_report_reason_t.REASON_NO_ADDTL_INFO);
			return;
		}

		/*
		 * "Check if this is a complete (non-fragment) bundle that obsoletes any
		 * fragments that we know about." [DTN2]
		 */
		if (!bundle.is_fragment() && DTNService.context().getResources().getString(R.string.DTNEnableProactiveFragmentation).equals("true")) {
			fragmentmgr_.delete_obsoleted_fragments(bundle);
		}

		/*
		 * "Deliver the bundle to any local registrations that it matches, unless
		 * it's generated by the router or is a bundle fragment. Delivery of
		 * bundle fragments is deferred until after re-assembly." [DTN2]
		 */
		boolean is_local = check_local_delivery(bundle,
				(event.source() != event_source_t.EVENTSRC_ROUTER)
						&& (bundle.is_fragment() == false));

		/*
		 * "Re-assemble bundle fragments that are destined to the local node." [DTN2]
		 */
		if (bundle.is_fragment() && is_local) {
			Log.d(TAG, String.format("deferring delivery of bundle %d "
					+ "since bundle is a fragment", bundle.bundleid()));
			fragmentmgr_.process_for_reassembly(bundle);
		}

		/*
		 * "Finally, bounce out so the router(s) can do something further with
		 * the bundle in response to the event." [DTN2]
		 */
	}

	protected void handle_bundle_report(BundleReportEvent event) {

	}

	protected void handle_bundle_send(BundleSendRequest event) {

		Link link = contactmgr_.find_link(event.link().name());
		if (link == null) {
			Log.e(TAG, String.format("Cannot send bundle on unknown link %s",
					event.link().name()));
			return;
		}

		Bundle bundle = event.bundle();
		if (bundle == null) {
			Log.e(TAG, "NULL bundle object in BundleSendRequest");
			return;
		}

		actions_.queue_bundle(bundle, link, event.action(), CustodyTimerSpec
				.getDefaultInstance());
	}

	protected void handle_bundle_transmitted(BundleTransmittedEvent event) {
		Bundle bundle = event.bundle();

		Link link = event.link();
		assert (link != null) : "BundleDaemon:handle_bundle_transmitted, link is null";

		Log.d(TAG, String.format(
				"trying to find xmit blocks for bundle id:%d on link %s",
				bundle.bundleid(), link.name()));
		BlockInfoVec blocks = bundle.xmit_link_block_set().find_blocks(link);


		if (blocks == null) {
			Log.i(TAG, String.format(
					"received a redundant/conflicting bundle_transmit event about "
							+ "bundle id:%d . %s (%s)", bundle.bundleid(), link
							.name(), link.nexthop()));
			return;
		}

		/*
		 * "Update statistics and remove the bundle from the link inflight queue.
		 * Note that the link's queued length statistics must always be
		 * decremented by the full formatted size of the bundle, yet the
		 * transmitted length is only the amount reported by the event." [DTN2]
		 */
		int total_len = BundleProtocol.total_length(blocks);

		stats_.transmitted_bundles_++;

		link.stats().set_bundles_transmitted(
				link.stats().bundles_transmitted() + 1);

		link.stats().set_bytes_transmitted(
				link.stats().bytes_transmitted() + event.bytes_sent());

		// "remove the bundle from the link's in flight queue" [DTN2]
		if (link.del_from_inflight(event.bundle(), total_len)) {
			Log.d(TAG, String.format(
					"removed bundle id:%d from link %s inflight queue", bundle
							.bundleid(), link.name()));
		} else {
			Log.w(TAG, String.format(
					"bundle id:%d not on link %s inflight queue", bundle
							.bundleid(), link.name()));
		}

		// "verify that the bundle is not on the link's to-be-sent queue" [DTN2]
		if (link.del_from_queue(event.bundle(), total_len)) {
			Log
					.w(
							TAG,
							String
									.format(
											"bundle id:%d unexpectedly on link %s queue in transmitted event",
											bundle.bundleid(), link.name()));
		}

		Log
				.i(
						TAG,
						String
								.format(
										"BUNDLE_TRANSMITTED id:%d (%d bytes_sent/%d reliable) . %s (%s)",
										bundle.bundleid(), event.bytes_sent(),
										event.reliably_sent(), link.name(),
										link.nexthop()));

		/*
		 * "If we're configured to wait for reliable transmission, then check the
		 * special case where we transmitted some or all a bundle but nothing
		 * was acked. In this case, we create a transmission failed event in the
		 * forwarding log and don't do any of the rest of the processing below.
		 * 
		 * Note also the special care taken to handle a zero-length bundle.
		 * XXX/demmer this should all go away when the lengths include both
		 * the header length and the payload length (in which case it's never
		 * zero).
		 * 
		 * XXX/demmer a better thing to do (maybe) would be to record the
		 * lengths in the forwarding log as part of the transmitted entry."[DTN2]
		 */
		if (params_.retry_reliable_unacked_ && link.is_reliable()
				&& (event.bytes_sent() != event.reliably_sent())
				&& (event.reliably_sent() == 0)) {
			bundle.fwdlog()
					.update(link, ForwardingInfo.state_t.TRANSMIT_FAILED);
			Log.d(TAG, String.format(
					"trying to delete xmit blocks for bundle id:%d on link %s",
					bundle.bundleid(), link.name()));
			BundleProtocol.delete_blocks(bundle, link);

			Log.d(TAG, "XXX/demmer fixme transmitted special case");

			return;
		}

		/*
		 * "Grab the latest forwarding log state so we can find the custody timer
		 * information (if any)." [DTN2]
		 */
		boolean[] found = new boolean[1];
		ForwardingInfo fwdinfo = bundle.fwdlog().get_latest_entry(link, found);
		if (!found[0]) {
			StringBuffer buf = new StringBuffer();
			bundle.fwdlog().dump(buf);
			Log.d(TAG, buf.toString());
		}
		if (fwdinfo.state() != ForwardingInfo.state_t.QUEUED) {
			Log.e(TAG, String.format(
					"Bundle ID %d fwdinfo state %s != expected QUEUED", bundle
							.bundleid(), fwdinfo.state().toString()));
		}

		/*
		 * "Update the forwarding log indicating that the bundle is no longer in
		 * flight."[DTN2]
		 */
		Log
				.d(
						TAG,
						String
								.format(
										"updating forwarding log entry on bundle id %d for link %s to TRANSMITTED",
										bundle.bundleid(), link.name()));
		bundle.fwdlog().update(link, ForwardingInfo.state_t.TRANSMITTED);

		
		DTNManager.getInstance().notify_user("DTN Bundle Transmitted", "To " + bundle.dest().toString());
		
		       
		/*
		 * "Remove the formatted block info from the bundle since we don't need
		 * it any more." [DTN2]
		 */
		Log.d(TAG, String.format(  
				"trying to delete xmit blocks for bundle id:%d on link %s",
				bundle.bundleid(), link.name()));
		BundleProtocol.delete_blocks(bundle, link);
		blocks = null;

		
		
		
		/*
		 * "Generate the forwarding status report if requested" [DTN2]
		 */
		if (bundle.forward_rcpt()) {
			generate_status_report(bundle,
					BundleStatusReport.flag_t.STATUS_FORWARDED,
					BundleProtocol.status_report_reason_t.REASON_NO_ADDTL_INFO);
		}

		/*
		 * "Schedule a custody timer if we have custody.} [DTN2
		 */
		if (bundle.local_custody()) {
			bundle.custody_timers().add(
					new CustodyTimer(fwdinfo.timestamp(), fwdinfo
							.custody_spec(), bundle, link));
		}
		

	}

	protected void handle_cla_parameters_query(CLAParametersQueryRequest request) {
		assert (request.cla() != null) : "BundleDaemon:handle_cla_parameters_query, CLayer is null";

		Log.e(TAG, String.format("BundleDaemon::handle_cla_parameters_query: "
				+ "query %s, convergence layer %s", request.query_id(), request
				.cla().name()));

		request.cla().query_cla_parameters(request.query_id(),
				request.parameter_names());
	}

	protected void handle_cla_parameters_report(CLAParametersReportEvent event) {
		Log.e(TAG, String.format(
				"Bundledaemon::handle_cla_parameters_report: query %s", event
						.query_id()));
	}

	protected void handle_cla_params_set(CLAParamsSetEvent event) {

	}

	protected void handle_cla_set_params(CLASetParamsRequest request) {
		assert (request.cla() != null) : "BundleDaemon:handle_cla_set_params, cla is null";
		request.cla().set_cla_parameters(request.parameters());
	}

	protected void handle_contact_attribute_changed(
			ContactAttributeChangedEvent event) {

	}

	protected void handle_contact_down(ContactDownEvent event) {
		Contact contact = event.contact();

		Link link = contact.link();
		assert (link != null);

		Log.i(TAG, String.format("CONTACT_DOWN %s (%s) (contact %s)", link
				.name(), event.reason().getCaption(), contact.toString()));

		// update the link stats , in seconds
		link.stats().set_uptime(link.stats().uptime()
				+ (TimeHelper.elapsed_ms(contact.start_time()) / 1000));

	}

	protected void handle_contact_query(ContactQueryRequest event) {
		post_at_head(new ContactReportEvent());
	}

	protected void handle_contact_report(ContactReportEvent event) {

	}

	protected void handle_contact_up(ContactUpEvent event) {
		Contact contact = event.contact();
		Link link = contact.link();
		assert (link != null);

		if (link.isdeleted()) {
			Log.e(TAG, String
					.format("BundleDaemon::handle_contact_up: "
							+ "cannot bring contact up on deleted link %s",
							link.name()));
			event.set_daemon_only(true);
			return;
		}

		// "ignore stale notifications that an old contact is up" [DTN2]
		contactmgr_.get_lock().lock();
		try {
			
			//XXX/ this was false so the contact status was not set to true
			if (link.contact() != contact) {
				Log
						.i(
								TAG,
								String
										.format(
												"CONTACT_UP %s (contact %s) being ignored (old contact)",
												link.name(), contact.toString()));
				return;
			}

			Log.i(TAG, String.format("CONTACT_UP %s (contact %s)", link.name(),
					contact.toString()));
			link.set_state(Link.state_t.OPEN);
			link.stats_.set_contacts(link.stats_.contacts() + 1);
		} finally {
			contactmgr_.get_lock().unlock();
		}
	}

	protected void handle_custody_signal(CustodySignalEvent event) {
		Log.i(TAG, String.format("CUSTODY_SIGNAL: %s %d.%d %s (%s)", event
				.data().orig_source_eid().toString(), event.data()
				.orig_creation_tv().seconds(), event.data().orig_creation_tv()
				.seqno(), event.data().succeeded() ? "succeeded" : "failed",
				event.data().reason().getCaption()));

		GbofId gbof_id = new GbofId();
		gbof_id.source_.assign(event.data().orig_source_eid());
		gbof_id.creation_ts_ = event.data().orig_creation_tv();
		gbof_id.is_fragment_ = (event.data().admin_flags() & BundleProtocol.admin_record_flags_t.ADMIN_IS_FRAGMENT
				.getCode()) > 0;
		gbof_id.frag_length_ = gbof_id.is_fragment() ? event.data()
				.orig_frag_length() : 0;
		gbof_id.frag_offset_ = gbof_id.is_fragment_ ? event.data()
				.orig_frag_offset() : 0;

		Bundle orig_bundle = custody_bundles_.find(gbof_id);

		if (orig_bundle == null) {
			Log.w(TAG, String.format(
					"received custody signal for bundle %s %d.%d "
							+ "but don't have custody", event.data()
							.orig_source_eid().toString(), event.data()
							.orig_creation_tv().seconds(), event.data()
							.orig_creation_tv().seqno()));
			return;
		}

		// "release custody if either the signal succeded or if it
		// (paradoxically) failed due to duplicate transmission" [DTN2]
		boolean release = event.data().succeeded();
		if ((event.data().succeeded() == false)
				&& (event.data().reason() == BundleProtocol.custody_signal_reason_t.CUSTODY_REDUNDANT_RECEPTION)) {
			Log.i(TAG, String.format("releasing custody for bundle %s %d.%d "
					+ "due to redundant reception", event.data()
					.orig_source_eid().toString(), event.data()
					.orig_creation_tv().seconds(), event.data()
					.orig_creation_tv().seqno()));

			release = true;
		}

		if (release) {
			release_custody(orig_bundle);
			try_to_delete(orig_bundle);
		}
	}

	protected void handle_custody_timeout(CustodyTimeoutEvent event) {
		Bundle bundle = event.bundle();
		Link link = event.link();
		assert (link != null) : "BundleDaemon, handle_custody_timeout link is null";

		Log.i(TAG, String.format("CUSTODY_TIMEOUT bundle %d, on link %s",
				bundle.bundleid(), link.name()));

		// "remove and delete the expired timer from the bundle" [DTN2]
		bundle.get_lock().lock();
		try {

			boolean found = false;
			CustodyTimer timer = null;
			Iterator<CustodyTimer> iter = bundle.custody_timers().iterator();
			while (iter.hasNext()) {
				timer = iter.next();
				if (timer.link().equals(link)) {
					
					if (timer.pending()) {
						Log.e("multiple pending custody timers for link %s",
								link.nexthop());
						continue;
					}

					found = true;
					bundle.custody_timers().remove(timer);
					break;
				}
			}

			if (!found) {
				Log
						.e(
								TAG,
								String
										.format(
												"custody timeout for bundle %d on link %s: timer not found in bundle list",
												bundle.bundleid(), link.name()));
				return;
			}

			assert (!timer.cancelled()) : "Bundled Timer:handle_custody_timout, timer is cancelled";

			if (!pending_bundles_.contains(bundle)) {
				Log
						.e(
								TAG,
								String
										.format(
												"custody timeout for bundle %d, on link %s: bundle not in pending list",
												bundle.bundleid(), link.name()));
			}

			// "modify the TRANSMITTED entry in the forwarding log to indicate
			// that we got a custody timeout. then when the routers go through
			// to figure out whether the bundle needs to be re-sent, the
			// TRANSMITTED entry is no longer in there" [DTN2]
			boolean ok = bundle.fwdlog().update(link,
					ForwardingInfo.state_t.CUSTODY_TIMEOUT);
			if (!ok) {
				Log
						.e(
								TAG,
								String
										.format(
												"custody timeout can't find ForwardingLog entry for link %s",
												link.name()));
			}

		} finally {
			bundle.get_lock().unlock();
		}
		// "now fall through to let the router handle the event, typically
		// triggering a retransmission to the link in the event" [DTN2]
	}

	protected void handle_eid_reachable_query(EIDReachableQueryRequest request) {
		Interface iface = request.iface();
		assert (iface != null) : "BundleDaemon:handle_eid_reachable_query, iface is null";
		assert (iface.clayer() != null) : "BundleDaemon:handle_eid_reachable_query, clayer is null";

		Log
				.e(
						TAG,
						String
								.format(
										"BundleDaemon::handle_eid_reachable_query: query %s, "
												+ "checking if endpoint %s is reachable via interface %s",
										request.query_id(), request.endpoint()
												.toString(), iface.name()));

		iface.clayer().is_eid_reachable(request.query_id(), iface,
				request.endpoint());
	}

	protected void handle_eid_reachable_report(EIDReachableReportEvent event) {

		Log.e(TAG, String.format(
				"BundleDaemon::handle_eid_reachable_report: query %s, %s",
				event.query_id(), event.is_reachable() ? "true" : "false"));
	}

	protected void handle_iface_attributes_query(
			IfaceAttributesQueryRequest request) {
		Interface iface = request.iface();
		assert (iface != null) : "BundleDaemon:handle_iface_attributes_query, iface is null";
		assert (iface.clayer() != null) : "BundleDaemon:handle_iface_attributes_query, clayer is null";

		Log.e(TAG, String.format(
				"BundleDaemon::handle_iface_attributes_query: "
						+ "query %s, interface %s", request.query_id(), iface
						.name()));

		iface.clayer().query_iface_attributes(request.query_id(), iface,
				request.attribute_names());

	}

	protected void handle_iface_attributes_report(
			IfaceAttributesReportEvent event) {

		Log.e(TAG, String.format(
				"BundleDaemon::handle_iface_attributes_report: query %s", event
						.query_id().toString()));
	}

	protected void handle_link_attribute_changed(LinkAttributeChangedEvent event) {
		Link link = event.link();

		if (link.isdeleted()) {
			Log.e(TAG, String.format(
					"BundleDaemon::handle_link_attribute_changed: "
							+ "link %s deleted", link.name()));
			event.set_daemon_only(true);
			return;
		}

		// "Update any state as necessary" [DTN2]
		Iterator<NamedAttribute> iter = event.attributes().iterator();

		while (iter.hasNext()) {
			NamedAttribute atr = iter.next();

			if (atr.name().equals("nexthop")) {
				link.set_nexthop(atr.string_val());
			} else if (atr.name() == "how_reliable") {
				link.stats().set_reliability(atr.int_val());
			} else if (atr.name() == "how_available") {
				link.stats().set_availability(atr.int_val());
			}
		}
		Log.i("LINK_ATTRIB_CHANGED %s", link.name());

	}

	protected void handle_link_attributes_query(
			LinkAttributesQueryRequest request) {
		Link link = request.link();
		assert (link != null) : "BundleDaemon:handle_link_attributes_query, link is null";
		assert (link.clayer() != null) : "BundleDaemon:handle_link_attributes_query, CLayer is null";

		Log
				.e(
						TAG,
						String
								.format(
										"BundleDaemon::handle_link_attributes_query: query %s, link %s",
										request.query_id(), link.name()));

		link.clayer().query_link_attributes(request.query_id(), link,
				request.attribute_names());
	}

	protected void handle_link_attributes_report(LinkAttributesReportEvent event) {

		Log.e(TAG, String.format(
				"BundleDaemon::handle_link_attributes_report: query %s", event
						.query_id()));
	}

	protected void handle_link_available(LinkAvailableEvent event) {
		Link link = event.link();
		assert (link != null);
		assert (link.isNotUnavailable());

		if (link.isdeleted()) {
			Log.w(TAG, String.format("BundleDaemon::handle_link_available: "
					+ "link %s already deleted", link.name()));
			event.set_daemon_only(true);
			return;
		}

		Log.i(TAG, String.format("LINK_AVAILABLE %s", link.name()));
	}

	protected void handle_link_created(LinkCreatedEvent event) {
		Link link = event.link();
		assert (link != null);

		if (link.isdeleted()) {
			Log.w("BundleDaemon::handle_link_created: "
					+ "link %s deleted prior to full creation", link.name());
			event.set_daemon_only(true);
			return;
		}

		Log.i(TAG, String.format("LINK_CREATED %s", link.name()));
	}

	protected void handle_link_delete(LinkDeleteRequest request) {
		Link link = request.link();
		assert (link != null);

		Log.i(TAG, String.format("LINK_DELETE %s", link.name()));
		if (!link.isdeleted()) {
			contactmgr_.del_link(link, false, reason_t.NO_INFO);
		}
	}

	protected void handle_link_deleted(LinkDeletedEvent event) {
		Link link = event.link();
		assert (link != null);

		Log.i(TAG, String.format("LINK_DELETED %s", link.name()));
	}

	protected void handle_link_query(LinkQueryRequest event) {
		post_at_head(new LinkReportEvent());

	}

	protected void handle_link_reconfigure(LinkReconfigureRequest request) {
		Link link = request.link();
		assert (link != null);

		link.reconfigure_link(request.parameters());
		Log.i(TAG, String.format("LINK_RECONFIGURE %s", link.name()));
	}

	protected void handle_link_report(LinkReportEvent event) {
	}

	protected void handle_link_state_change_request(
			LinkStateChangeRequest request) {
		Link link = request.link();
		
		
		if (link == null) {
			Log.w(TAG, "LINK_STATE_CHANGE_REQUEST received invalid link");
			return;
		}

		Link.state_t new_state = request.state();
		Link.state_t old_state = request.old_state();
		reason_t reason = request.reason();

		Log.d(TAG, "HANDLE link state change request from " + old_state.toString() + " to " + new_state.toString());
		
		if (link.isdeleted() && new_state != Link.state_t.CLOSED) {
			Log
					.w(
							TAG,
							String
									.format(
											"BundleDaemon::handle_link_state_change_request: "
													+ "link %s already deleted; cannot change link state to %s",
											link.name(), new_state.toString()));
			return;
		}

		if (request.contact() != null && link.contact() != null)
			if (!link.contact().equals(request.contact())) {
				Log.w(TAG, String.format(
						"stale LINK_STATE_CHANGE_REQUEST [%s -> %s] (%s) for "
								+ "link %s: contact %s != current contact %s",
						old_state.toString(), new_state.toString(), reason
								.toString(), link.name(), request.contact()
								.toString(), link.contact().toString()));
				return;
			}

		Log.i(TAG, String.format(
				"LINK_STATE_CHANGE_REQUEST [%s -> %s] (%s) for link %s",
				old_state.toString(), new_state.toString(), reason.toString(),
				link.name()));

		// "avoid a race condition caused by opening a partially closed link" [DTN2]

		contactmgr_.get_lock().lock();
		try {

			switch (new_state) {
			case UNAVAILABLE:
				if (link.state() != Link.state_t.AVAILABLE) {
					Log.e(TAG, String.format("LINK_STATE_CHANGE_REQUEST %s: "
							+ "tried to set state UNAVAILABLE in state %s",
							link.name(), link.state().toString()));
					return;
				}
				link.set_state(new_state);
				post_at_head(new LinkUnavailableEvent(link, reason));
				break;

			case AVAILABLE:
				if (link.state() == Link.state_t.UNAVAILABLE) {
					link.set_state(Link.state_t.AVAILABLE);

				} else {
					Log.e(TAG, String.format("LINK_STATE_CHANGE_REQUEST %s: "
							+ "tried to set state AVAILABLE in state %s", link
							.name(), link.state().toString()));
					return;
				}

				post_at_head(new LinkAvailableEvent(link, reason));
				break;

			case OPENING:
			case OPEN:
				// force the link to be available, since someone really wants it
				// open
				if (link.state() == Link.state_t.UNAVAILABLE) {
					link.set_state(Link.state_t.AVAILABLE);
				}
				actions_.open_link(link);
				break;

			case CLOSED:
				// If the link is open (not OPENING), we need a ContactDownEvent
				if (link.isopen()) {
					assert (link.contact() != null);
					post_at_head(new ContactDownEvent(link.contact(), reason));
				}

				// close the link
				actions_.close_link(link);

				
				if (!link.isdeleted())
				{
			
				
				Log.d(TAG, "posting link unavailable event for link " + link.name());
				link.set_state(Link.state_t.UNAVAILABLE);
				post_at_head(new LinkUnavailableEvent(link, reason));
				}
				

				break;

			default:
				Log.e(TAG, String.format("unhandled state %s", new_state
						.toString()));
			}
		} finally {
			contactmgr_.get_lock().unlock();
		}
	}

	protected void handle_link_unavailable(LinkUnavailableEvent event) {
		Link link = event.link();
		assert (link != null);
		assert (!link.isNotUnavailable());

		Log.i("LINK UNAVAILABLE %s", link.name());
	}

	protected void handle_new_eid_reachable(NewEIDReachableEvent event) {

	}

	protected void handle_reassembly_completed(ReassemblyCompletedEvent event) {
		Log.i(TAG, String.format("REASSEMBLY_COMPLETED bundle id %d", event
				.bundle().bundleid()));

		// "remove all the fragments from the pending list" [DTN2]
		event.fragments().get_lock().lock();
		try {
			ListIterator<Bundle> itr = event.fragments().begin();
			while (itr.hasNext()) {
				Bundle fragment = itr.next();
				delete_bundle(
						fragment,
						BundleProtocol.status_report_reason_t.REASON_NO_ADDTL_INFO);
			}
		} catch (BundleListLockNotHoldByCurrentThread e) {
			Log
					.e(
							TAG,
							"BundleDaemon:handle_reassembly_completed, get iterator of fragments while not getting lock");
		} finally {
			event.fragments().get_lock().unlock();
		}
		// "post a new event for the newly reassembled bundle" [DTN2]
		post_at_head(new BundleReceivedEvent(event.bundle(),
				event_source_t.EVENTSRC_FRAGMENTATION));
	}

	protected void handle_registration_added(RegistrationAddedEvent event) {
		Registration registration = event.registration();
		Log.i(TAG, String.format("REGISTRATION_ADDED %d %s", registration
				.regid(), registration.endpoint().toString()));

		if (!reg_table_.add(registration,
				(event.source() == event_source_t.EVENTSRC_APP) ? true : false)) {
			Log.e(TAG, String.format("error adding registration %d to table",
					registration.regid()));
		}

		pending_bundles_.get_lock().lock();
		try {

			ListIterator<Bundle> iter = pending_bundles_.begin();
			while (iter.hasNext()) {
				Bundle bundle = iter.next();

				if (!bundle.is_fragment()
						&& registration.endpoint().match(bundle.dest())) {
					deliver_to_registration(bundle, registration);

				}
			}
		} catch (BundleListLockNotHoldByCurrentThread e) {
			Log
					.e(
							TAG,
							"BundleDaemon:handle_registration_added, get pending bundles iterator while not getting lock");
		} finally {
			pending_bundles_.get_lock().unlock();
		}
	}

	protected void handle_registration_delete(RegistrationDeleteRequest request) {
		Log.i(TAG, String.format("REGISTRATION_DELETE %d", request
				.registration().regid()));

	}

	protected void handle_registration_expired(RegistrationExpiredEvent event) {
		Registration registration = event.registration();

		if (reg_table_.get(registration.regid()) == null) {
			// this shouldn't ever happen
			Log.e(TAG, String.format("REGISTRATION_EXPIRED -- dead regid %d",
					registration.regid()));
			return;
		}

		registration.set_expired(true);

		if (registration.active()) {
			// "if the registration is currently active (i.e. has a
			// binding), we wait for the binding to clear, which will then
			// clean up the registration"" [DTN2]
			Log.i(TAG, String.format(
					"REGISTRATION_EXPIRED %d -- deferred until binding clears",
					registration.regid()));
		} else {
			// "otherwise remove the registration from the table" [DTN2]
			Log.i(TAG, String.format("REGISTRATION_EXPIRED %d", registration
					.regid()));
			reg_table_.del(registration.regid());
			post_at_head(new RegistrationDeleteRequest(registration));
		}
	}

	protected void handle_registration_removed(RegistrationRemovedEvent event) {
		Registration registration = event.registration();
		Log.i(TAG, String.format("REGISTRATION_REMOVED %d %s", registration
				.regid(), registration.endpoint().toString()));

		if (!reg_table_.del(registration.regid())) {
			Log.e(TAG, String.format(
					"error removing registration %d from table", registration
							.regid()));
			return;
		}

		post(new RegistrationDeleteRequest(registration));
	}

	protected void handle_route_add(RouteAddEvent event) {
		Log.i(TAG, String.format("ROUTE_ADD %s", event.entry()));
	}

	protected void handle_route_del(RouteDelEvent event) {
		Log.i(TAG, String.format("ROUTE_DEL %s", event.dest().toString()));
	}

	protected void handle_route_query(RouteQueryRequest event) {
		post_at_head(new RouteReportEvent());
	}

	protected void handle_route_report(RouteReportEvent event) {
	}

	protected void handle_set_link_defaults(SetLinkDefaultsRequest event) {

	}

	
	protected void handle_shutdown_request(ShutdownRequest event) {
		// "Signal the main to bail out" [DTN2]
		shutting_down_ = true;
		Log.i(TAG, "BundleDeamon: Received Shutdown Request");

		
		contactmgr_.get_lock().lock();
		try {

			LinkSet links = contactmgr_.links();

			Iterator<Link> itr = links.iterator();

			while (itr.hasNext()) {
				Link link = itr.next();

				Log.d(TAG, String.format("Shutdown: checking whether link is opened before closing link %s \n", link.name()));
				//XXX/KLA this one was false 
				if (link.isopen()) {
					Log.d(TAG, String.format("Shutdown: closing link %s \n", link.name()));
					link.close();
				}

			}

			// "Shutdown all actively registered convergence layers." [DTN2]
			ConvergenceLayer.shutdown_clayers();

			// "call the rtr shutdown procedure" [DTN2]
			if (rtr_shutdown_proc_ != null) {
				rtr_shutdown_proc_.action(rtr_shutdown_data_);
			}

			// "call the app shutdown procedure" [DTN2]
			if (app_shutdown_proc_ != null) {
				app_shutdown_proc_.action(app_shutdown_data_);
			}

			
			
			
			thread_ = null;

			eventq_.clear();
			// "fall through -- the DTNServer will close and flush all the data
			// stores" [DTN2]
			instance_ = null;
		} finally {
			contactmgr_.get_lock().unlock();
		}

	}

	
	protected void handle_status_request(StatusRequest event) {

		Log.i(TAG, "Received status request");
	}

	
	/**
	 * "Initialize and load in stored bundles." [DTN2]
	 */
	protected void load_bundles() {

		
		Iterator<Bundle> iter = BundleStore.getInstance().new_iterator();

		Log.i(TAG, "BundleDaemon loading bundles from data store");

		List<Bundle> tobe_deleted_bundles = new List<Bundle>();
		while (iter.hasNext()) {
			Bundle bundle = iter.next();
			// "if the bundle payload file is missing, we need to kill the
			// bundle, but we can't do so while holding the durable
			// iterator or it may deadlock, so cleanup is deferred" [DTN2]
			if (bundle.payload().location() != BundlePayload.location_t.DISK) {
				Log.e(TAG, String.format(
						"error loading payload for bundle %d from data store",
						bundle.bundleid()));
				tobe_deleted_bundles.add(bundle);
				continue;
			}

			BundleProtocol.reload_post_process(bundle);

			BundleReceivedEvent event = new BundleReceivedEvent(bundle,
					event_source_t.EVENTSRC_STORE);
			post_event(event, true);
			//handle_event(event);

		}

		Iterator<Bundle> delete_itr = tobe_deleted_bundles.iterator();

		while (delete_itr.hasNext()) {
			Bundle bundle = delete_itr.next();
			actions_.store_del(bundle);
		}
		
	}

	
	/**
	 * "Initialize and load in the registrations." [DTN2]
	 */
	protected void load_registrations() {
		admin_reg_ = new AdminRegistration();
		{
			RegistrationAddedEvent event = new RegistrationAddedEvent(
					admin_reg_, event_source_t.EVENTSRC_ADMIN);
			admin_reg_.endpoint().assign(local_eid_);
			handle_event(event);
		}
		EndpointID ping_eid = new EndpointID(local_eid());
		boolean ok = ping_eid.append_service_tag("ping");
		if (!ok) {
			Log
					.e(
							TAG,
							String
									.format(
											"local eid (%s) scheme must be able to append service tags",
											local_eid().toString()));

		}

		ping_reg_ = new PingRegistration(ping_eid);
		{
			RegistrationAddedEvent event = new RegistrationAddedEvent(
					ping_reg_, event_source_t.EVENTSRC_ADMIN);
			handle_event(event);
		}
		
		EndpointID prophet_eid = new EndpointID(local_eid());
		ok = prophet_eid.append_service_tag("prophet");
		if (!ok) {
			Log
					.e(
							TAG,
							String
									.format(
											"prophet local eid (%s) scheme must be able to append service tags",
											local_eid().toString()));

		}

		if(BundleRouter.config().type() == router_type_t.PROPHET_BUNDLE_ROUTER)
		{
			RegistrationAddedEvent event = new RegistrationAddedEvent(
					router_.getProphetRegistration(), event_source_t.EVENTSRC_ADMIN);
			handle_event(event);
		}

		Iterator<Registration> iter = RegistrationStore.getInstance()
				.new_iterator();

		while (iter.hasNext()) {
			Registration reg = iter.next();
			if (reg == null) {
				Log.e(TAG, String
						.format("error loading registration  from data store"));
				continue;
			}

			RegistrationAddedEvent event = new RegistrationAddedEvent(reg,
					event_source_t.EVENTSRC_STORE);
			handle_event(event);
		}

	}

	
	/**
	 * "Release custody of the given bundle, sending the appropriate signal to
	 * the current custodian." [DTN2]
	 */
	protected void release_custody(Bundle bundle) {
		Log.i(TAG, String.format("release_custody bundle id %d", bundle
				.bundleid()));

		if (!bundle.local_custody()) {
			Log.e(TAG, String.format(
					"release_custody(bundle id %d): don't have local custody",
					bundle.bundleid()));
			return;
		}

		cancel_custody_timers(bundle);

		bundle.custodian().assign(EndpointID.NULL_EID());
		bundle.set_local_custody(false);
		bundle.set_complete(true);
		actions_.store_update(bundle);

		custody_bundles_.erase(bundle, false);
	}

	/**
	 * "Check if we should delete this bundle, called just after arrival, once
	 * it's been transmitted or delivered at least once, or when we release
	 * custody." [DTN2]
	 */
	protected boolean try_to_delete(final Bundle bundle) {
		/*
		 * "Check to see if we should remove the bundle from the system.
		 * 
		 * If we're not configured for early deletion, this never does anything.
		 * Otherwise it relies on the router saying that the bundle can be
		 * deleted." [DTN2]
		 */

		Log.d(TAG, String.format("pending_bundles size %d", pending_bundles_
				.size()));
		if (!bundle.is_queued_on(pending_bundles_)) {
			if (bundle.expired()) {
				Log.d(TAG, String.format(
						"try_to_delete( bundle id %d): bundle already expired",
						bundle.bundleid()));
				return false;
			}

			Log
					.e(
							TAG,
							String
									.format(
											"try_to_delete( bundle id %d): bundle not in pending list!",
											bundle.bundleid()));
			return false;
		}

		if (!params_.early_deletion_) {
			Log.d(TAG, String.format(
					"try_to_delete( bundle id %d): not deleting because "
							+ "early deletion disabled", bundle.bundleid()));
			return false;
		}

		if (!router_.can_delete_bundle(bundle)) {
			Log
					.d(TAG, String.format(
							"try_to_delete( bundle id %d): not deleting because "
									+ "router wants to keep bundle", bundle
									.bundleid()));
			return false;
		}

		return delete_bundle(bundle,
				BundleProtocol.status_report_reason_t.REASON_NO_ADDTL_INFO);

	}

}
