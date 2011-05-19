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

import java.util.Iterator;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.DTNConfiguration;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.DiscoveriesSetting.AnnounceEntry;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.DiscoveriesSetting.DiscoveryEntry;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.List;
import android.util.Log;

/**
 * This class represents a table where the Discoveries instance are stored.
 */

public class DiscoveryTable {

	/**
	 * TAG for Android Logging mechanism
	 */
	private static final String TAG = "DiscoveryTable";

	/**
	 * Singleton Implementation of the DiscoveryTable
	 */
	private static DiscoveryTable instance_ = null;

	public static DiscoveryTable getInstance() {
		if (instance_ == null) {
			instance_ = new DiscoveryTable();
		}

		return instance_;
	}

	private DTNConfiguration config_;

	/**
	 * Parse, at a boot time, all the parameters needed to create discoveries
	 * instances. This parameter are parsed from the configuration file. The
	 * discoveries instances are stored in the table.
	 */
	public void init(DTNConfiguration config) {
		config_ = config;
		List<DiscoveryEntry> discovery_entries = config.discoveries_setting()
				.discovery_entries();
		Iterator<DiscoveryEntry> i = discovery_entries.iterator();
	
		while (i.hasNext()) {

			DiscoveryEntry element = i.next();
			String name_id = element.id();
			Log.d(TAG, name_id);
			String afamily = element.address_family().getCaption();
			Log.d(TAG, afamily);
			int port = element.port();
			Log.d(TAG, "" + port);
			add(name_id, afamily, (short) port);

		}

	}
	
	public void start(){
		try {
			List<AnnounceEntry> announce_entries = config_.discoveries_setting()
			.announce_entries();
			Iterator<AnnounceEntry> it = announce_entries.iterator();

			while (it.hasNext()) {

				AnnounceEntry element = it.next();
				String AnnounceID = element.interface_id();
				Log.d(TAG, AnnounceID);
				String DiscoveryID = element.discovery_id();
				Log.d(TAG, DiscoveryID);
				int interval = element.interval();
				String ClType = element.conv_layer_type().getCaption();
				int code;
				if (ClType.compareTo("tcp") == 0) {
					code = 1;
				} else {
					code = 0;
				}

				Iterator<Discovery> iter = dlist_.iterator();

				Discovery disc = find(DiscoveryID, iter);
				if (disc.equals(null)) {
					String text = String
					.format(
							"error adding announce %s to %s: no such discovery agent",
							AnnounceID, DiscoveryID);
					Log.d(TAG, text);
					return;
				}

				if (!disc.announce(AnnounceID, code, ClType, interval)) {
					Log.d(TAG, "Error creting the Announce" + AnnounceID);
				}
				disc.start();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Add a new discovery instance to the table
	 */
	public boolean add(String name, String addr_family, short port) {

		Iterator<Discovery> iter = dlist_.iterator();

		Discovery disc = find(name, iter);
		if (disc != null) {
			Log.e(TAG, "agent exists with that name");
			return false;
		}

		disc = Discovery.create_discovery(name, addr_family, port);

		if (disc == null) {
			return false;
		}

		Log.i(TAG, "adding discovery agent" + name);

		dlist_.add(disc);
		return true;
	}

	public boolean del(String name) {

		Iterator<Discovery> iter = dlist_.iterator();

		Log.i(TAG, "removing discovery agent" + name);

		Discovery disc = find(name, iter);

		if (disc == null) {
			Log.e(TAG, "error removing agent" + name);
			return false;
		}

		iter.remove();

		return true;

	}


	/**
	 * Clear the table
	 */
	public void shutdown() {

		Iterator<Discovery> i = dlist_.iterator();

		while (i.hasNext()) {
			Discovery d = i.next();
			d.shutdown();
			i.remove();
		}
		dlist_.clear();

		instance_ = null;

	}


	/**
	 * Constructor
	 */
	protected DiscoveryTable() {
		dlist_ = new DiscoveryList();
	}


	/**
	 * Find a discory instance for name
	 */
	protected Discovery find(String name, Iterator<Discovery> iter) {

		Discovery disc;
		// iter = dlist_.iterator();
		while (iter.hasNext()) {
			disc = iter.next();
			if (disc.name().equals(name))
				return disc;
		}
		disc = null;
		return disc;

	}

	protected DiscoveryList dlist_;  // / List of Discoveries

}
