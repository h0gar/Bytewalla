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

import java.util.Iterator;
import java.util.ListIterator;

import se.kth.ssvl.tslab.bytewalla.androiddtn.DTNService;
import se.kth.ssvl.tslab.bytewalla.androiddtn.R;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleReceivedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleSendCancelledEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.event_source_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.exception.BundleListLockNotHoldByCurrentThread;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.storage.BundleStore;
import android.util.Log;

/**
 * Helper class for Bundle daemon and router to execute particular actions relating to Bundle link management, storage and transmission.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class BundleActions {

	private static final String TAG = "BundleActions";

	/**
	 * Empty constructor
	 */
	public BundleActions() {
	}

	/**
	 * "Open a link for bundle transmission. The link should be in state
	 * AVAILABLE for this to be called.
	 * 
	 * This may either immediately open the link in which case the link's state
	 * will be OPEN, or post a request for the convergence layer to complete the
	 * session initiation in which case the link state is OPENING." [DTN2]
	 */
	public void open_link(Link link) {
		assert (link != null) : "BundleAction:open_link, link is null";
		if (link.isdeleted()) {
			Log.d(TAG, String.format("BundleActions::open_link: "
					+ "cannot open deleted link %s", link.name()));
			return;
		}

		link.get_lock().lock();
		try {

			if (link.isopen() || link.contact() != null) {
				Log.e(TAG, String.format(
						"not opening link %s since already open", link.name()));
				return;
			}

			if (!link.isNotUnavailable()) {
				Log
						.e(TAG, String.format(
								"not opening link %s since not available", link
										.name()));
				return;
			}

			Log.d(TAG, String.format(
					"BundleActions::open_link: opening link %s", link.name()));

			link.open();
		} finally {
			link.get_lock().unlock();
		}
	}

	/**
	 * "Open a link for bundle transmission. The link should be in an open state
	 * for this to be called." [DTN2]
	 */
	public void close_link(final Link link) {
		assert (link != null) : "BundleActions:close_link link is null";

		if (!link.isopen() && !link.isopening()) {
			Log.e(TAG, String.format("not closing link %s since not open", link
					.name()));
			return;
		}

		Log.d(TAG, String.format("BundleActions::close_link: closing link %s",
				link.name()));

		link.close();
		assert (link.contact() == null) : "BundleActions:close_link contact is null";
	}

	/**
	 * "Queue the bundle for transmission on the given link." [DTN2]
	 * 
	 * @param bundle
	 *            the bundle
	 * @param link
	 *            "the link on which to send the bundle" [DTN2]
	 * @param action
	 *            "the forwarding action that was taken, recorded in the log" [DTN2]
	 * @param custody_timer_spec
	 *            "custody timer specification" [DTN2]
	 * 
	 * @return true "if the transmission was successfully initiated" [DTN2]
	 */
	public boolean queue_bundle(Bundle bundle, final Link link,
			ForwardingInfo.action_t action,
			final CustodyTimerSpec custody_timer_spec) {

		assert (link != null) : "BundleDaemon:queue_bundle, link is null";
		if (link.isdeleted()) {
			Log.w(TAG, String.format("BundleActions::queue_bundle: "
					+ "failed to send bundle id %d on link %s", bundle
					.bundleid(), link.name()));
			return false;
		}

		Log.d(TAG, String.format(
				"trying to find xmit blocks for bundle id:%d on link %s",
				bundle.bundleid(), link.name()));

		if (bundle.xmit_link_block_set().find_blocks(link) != null) {
			Log
					.e(
							TAG,
							String
									.format("BundleActions::queue_bundle: "
											+ "link not ready to handle bundle (block vector already exists), "
											+ "dropping send request"));
			return false;
		}

		Log.d(TAG, String.format(
				"trying to create xmit blocks for bundle id:%d on link %s",
				bundle.bundleid(), link.name()));
		BlockInfoVec blocks = BundleProtocol.prepare_blocks(bundle, link);
		int total_len = BundleProtocol.generate_blocks(bundle, blocks, link);

		Log.d(TAG, String.format(
				"queue bundle id %d on %s link %s (%s) (total len %d)", bundle
						.bundleid(), link.type_str(), link.name(), link
						.nexthop(), total_len));

		ForwardingInfo.state_t state = bundle.fwdlog().get_latest_entry(link);
		if (state == ForwardingInfo.state_t.QUEUED) {
			Log.e(TAG, String.format("queue bundle id %d on %s link %s (%s): "
					+ "already queued or in flight", bundle.bundleid(), link
					.type_str(), link.name(), link.nexthop()));
			return false;
		}

		if ((link.params().mtu() != 0) && (total_len > link.params().mtu())) {
			Log
					.e(
							TAG,
							String
									.format(
											"queue bundle id %d on %s link %s (%s): length %d > mtu %d, generating fragmentations",
											bundle.bundleid(), link.type_str(),
											link.name(), link.nexthop(),
											total_len, link.params().mtu()));

			if (DTNService.context().getResources().getString(
					R.string.DTNEnableProactiveFragmentation).equals("true")) {

				FragmentState proactive_fragment_state = FragmentManager
						.getInstance().proactively_fragment(bundle, link,
								link.params().mtu());

				BundleList fragment_list = proactive_fragment_state
						.fragment_list();

				fragment_list.get_lock().lock();
				try {
					Iterator<Bundle> itr = fragment_list.begin();

					while (itr.hasNext()) {
						Bundle fragment = itr.next();
						BundleDaemon.getInstance().post_at_head(
								new BundleReceivedEvent(fragment,
										event_source_t.EVENTSRC_FRAGMENTATION));
					}

					return false;
				} catch (BundleListLockNotHoldByCurrentThread e) {
					Log
							.e(TAG,
									"Bundle Action queue bundle, fragments bundle list not locked");
				} finally {
					fragment_list.get_lock().unlock();
				}
			} else {
				return false;
			}
		}

		// "Make sure that the bundle isn't unexpectedly already on the
		// queue or in flight on the link" [DTN2]
		if (link.queue().contains(bundle)) {
			Log.e(TAG, String.format(
					"queue bundle id %d on link %s: already queued on link",
					bundle.bundleid(), link.name()));
			return false;
		}

		if (link.inflight().contains(bundle)) {
			Log
					.e(
							TAG,
							String
									.format(
											"queue bundle id %d  on link %s: already in flight on link",
											bundle.bundleid(), link.name()));
			return false;
		}

		Log.d(TAG, String.format(
				"adding QUEUED forward log entry for %s link %s "
						+ "with nexthop %s and remote eid %s to bundle id %d",
				link.type_str(), link.name(), link.nexthop(), link.remote_eid()
						.toString(), bundle.bundleid()));

		bundle.fwdlog().add_entry(link, action, ForwardingInfo.state_t.QUEUED,
				custody_timer_spec);

		Log.d(TAG, String.format(
				"adding bundle id %d to link %s's queue (length %d)", bundle
						.bundleid(), link.name(), link.bundles_queued()));

		if (!link.add_to_queue(bundle, total_len)) {
			Log.e(TAG, String.format(
					"error adding bundle id %d to link %s queue", bundle
							.bundleid(), link.name()));
			return false;
		}

		// "finally, kick the convergence layer" [DTN2]
		link.clayer().bundle_queued(link, bundle);

		return true;
	}

	/**
	 * "Attempt to cancel transmission of a bundle on the given link." [DTN2]
	 * 
	 * @param bundle
	 *            the bundle
	 * @param link
	 *            "the link on which the bundle was queued" [DTN2]
	 * 
	 * @return "true if successful" [DTN2]
	 */
	public void cancel_bundle(Bundle bundle, final Link link) {

		Log.d(TAG, "cancel bundle, bundle is " + bundle);
		
		if (bundle == null)
			Log.e(TAG, "bundle receive in bundle action is null");
		
		assert (link != null);
		if (link.isdeleted()) {
			Log.d(TAG, String.format("BundleActions::cancel_bundle: "
					+ "cannot cancel bundle on deleted link %s", link.name()));
			return;
		}

		Log
				.d(
						TAG,
						String
								.format(
										"BundleActions::cancel_bundle: cancelling bundle id %d on link %s",
										bundle.bundleid(), link.name()));

		// "First try to remove the bundle from the link's delayed-send
		// queue. If it's there, then safely remove it and post the send
		// cancelled request without involving the convergence layer.
		//
		// If instead it's actually in flight on the link, then call down
		// to the convergence layer to see if it can interrupt
		// transmission, in which case it's responsible for posting the
		// send cancelled event." [DTN2]

		BlockInfoVec blocks = bundle.xmit_link_block_set().find_blocks(link);
		if (blocks == null) {
			Log
					.w(
							TAG,
							String
									.format(
											"BundleActions::cancel_bundle: "
													+ "cancel bundle id but no blocks queued or inflight on link %s",
											bundle, link.name()));
			return;
		}

		int total_len = BundleProtocol.total_length(blocks);

		if (link.del_from_queue(bundle, total_len)) {
			BundleDaemon.getInstance().post(
					new BundleSendCancelledEvent(bundle, link));

		} else if (link.inflight().contains(bundle)) {
			link.clayer().cancel_bundle(link, bundle);
		} else {
			Log.w(TAG, String.format("BundleActions::cancel_bundle: "
					+ "cancel bundle id %d but not queued or inflight on %s",
					bundle.bundleid(), link.name()));
		}
	}

	/**
	 * Update the bundles in the specified list in the storage
	 * @param list
	 */
	public void update_bundles(BundleList list) {
		list.get_lock().lock();
		try {
			ListIterator<Bundle> itr = list.begin();
			while (itr.hasNext()) {
				Bundle bundle = itr.next();  
				bundle.set_complete(true);
				store_update(bundle);
			}
		} catch (BundleListLockNotHoldByCurrentThread e) {
			Log.e(TAG, "update_bundles lock not hold for list");
		} finally {
			list.get_lock().unlock();
		}

	}

	/**
	 * "Inject a new bundle into the core system, which places it in the pending
	 * bundles list as well as in the persistent store. This is typically used
	 * by routing algorithms that need to generate their own bundles for
	 * distribuing route announcements. It does not, therefore, generate a
	 * BundleReceivedEvent." [DTN2]
	 * 
	 * @param bundle
	 *            the new bundle
	 */
	public void inject_bundle(Bundle bundle) {
		Log.d(TAG, String.format("inject bundle id %d", bundle.bundleid()));
		BundleDaemon.getInstance().pending_bundles().push_back(bundle);
		store_add(bundle);
	}

	/**
	 * "Attempt to delete a bundle from the system." [DTN2]
	 * 
	 * @param bundle
	 *            The bundle
	 * @param reason
	 *            Bundle Status Report reason code
	 * @param log_on_error
	 *            Set to false to suppress error logging
	 * 
	 * @return true if successful
	 */
	public boolean delete_bundle(Bundle bundle,
			BundleProtocol.status_report_reason_t reason, boolean log_on_error) {

		Log.d(TAG, String.format(
				"attempting to delete bundle id %d from data store", bundle
						.bundleid()));
		boolean del = BundleDaemon.getInstance().delete_bundle(bundle, reason);

		if (log_on_error && !del) {
			Log.e(TAG, String.format(
					"Failed to delete bundle id %d from data store", bundle));
		}
		return del;
	}


	/**
	 * "Add the given bundle to the data store." [DTN2]
	 */
	protected void store_add(Bundle bundle) {
		Log.d(TAG, String.format("adding bundle %d to data store", bundle
				.bundleid()));
		boolean added = BundleStore.getInstance().add(bundle);
		if (!added) {
			Log.e(TAG, String.format("error adding bundle %d to data store!!",
					bundle.bundleid()));
		}
	}

	/**
	 * "Update the on-disk version of the given bundle, after it's bookkeeping or
	 * header fields have been modified." [DTN2]
	 */
	protected void store_update(Bundle bundle) {
		Log.d(TAG, String.format("updating bundle %d in data store", bundle
				.bundleid()));
		boolean updated = BundleStore.getInstance().update(bundle);
		if (!updated) {
			Log.e(TAG, String.format(
					"error updating bundle %d in data store!!", bundle
							.bundleid()));
		}
	}

	/**
	 * "Remove the given bundle from the data store." [DTN2]
	 */
	protected void store_del(Bundle bundle) {
		Log.d(TAG, String.format("removing bundle %d from data store", bundle
				.bundleid()));
		boolean removed = BundleStore.getInstance().del(bundle);
		if (!removed) {
			Log.e(TAG, String.format(
					"error removing bundle %d from data store!!", bundle
							.bundleid()));
		}
	}

};
