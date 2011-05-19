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
package se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.energy;

import java.util.Iterator;

import se.kth.ssvl.tslab.bytewalla.androiddtn.DTNService;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.List;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


/**
 * The main status receiver who will distribute battery status update to 
 * any battery observers registered. The receiver have interface to register 
 * and unregister the BatteryObserver who would like to update on the battery status.  
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 * @see BatteryObserver
 */
public class BatteryStatsReceiver extends BroadcastReceiver{
	
	/**
	 * internal list to manage BatteryObserver registered with the receiver
	 */
	private List<BatteryObserver> observers_;
	
	/**
	 * Singleton implementation instance
	 */
	private static BatteryStatsReceiver instance_;
	
	/**
	 * Base Constructor. This mainly initializes the list of BatteryObserver
	 */
	public BatteryStatsReceiver()
	{
		observers_= new List<BatteryObserver>();
	}
	
	/**
	 * Register the input BatteryObserver for receiving Battery Stat update
	 * @param observer the BatteryObserver 
	 */
	public synchronized void registerBatteryObserver(BatteryObserver observer )
	{
		observers_.add(observer);
	}
	
	/**
	 * Remove the input BatteryObserver for receiving Battery Stat update. This observer will no longer receive battery stat update.
	 * @param observer the BatteryObserver 
	 */
	public synchronized void unregisterBatteryObserver(BatteryObserver observer )
	{
		if (observers_.contains(observer))
		{
			observers_.remove(observer);
		}
	}
	
	/**
	 * Shutdown and clean resource
	 */
	public void shutdown()
	{
		observers_.clear();
		instance_ = null;
		
		
	}
	
	/**
	 * Singleton implementation of the BatteryStat Receiver
	 * @return
	 */
	public static BatteryStatsReceiver getInstance()
	{
		if (instance_ == null) 
		{
			instance_ = new BatteryStatsReceiver();
			
			IntentFilter battFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			DTNService.context().registerReceiver(instance_, battFilter);
			
		}
		return instance_;
	}
	
	/**
	 * The onReceive method overridden from the BroadcastReceiver. This will receive the status and iterate over the list of observers to send an update.
	 */
	
	@Override
	public void onReceive(Context context, Intent intent) {
		int rawlevel = intent.getIntExtra("level", -1);
		int scale = intent.getIntExtra("scale", -1);
		int status = intent.getIntExtra("status", -1);
		int health = intent.getIntExtra("health", -1);
		int level = (int) ((rawlevel * 100.0) / scale);
		
	
		BatteryStat stat_ = new BatteryStat();
		
		stat_.set_raw_level(rawlevel);
		stat_.set_scale(scale);
		stat_.set_status(status);
		stat_.set_health(health);
		stat_.set_level(level);
		
		Iterator<BatteryObserver> itr = observers_.iterator();
		while (itr.hasNext())
		{
			BatteryObserver bto = itr.next();
			bto.update_battery_stat(stat_);
		}
		
		
	}
};