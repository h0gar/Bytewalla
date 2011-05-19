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
package se.kth.ssvl.tslab.bytewalla.androiddtn;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPIBinder;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleDaemon;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ShutdownRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.common.ServlibEventData;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.common.ServlibEventHandler;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.DTNConfiguration;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.DTNConfigurationParser;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.InvalidDTNConfigurationException;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config.ReadingConfigurationFileException;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.ContactManager;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.InterfaceTable;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.ConvergenceLayer;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.TestDataLogger;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.discovery.DiscoveryTable;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.reg.RegistrationTable;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.routing.BundleRouter;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.storage.BundleStore;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.storage.GlobalStorage;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.storage.RegistrationStore;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.energy.BatteryObserver;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.energy.BatteryStat;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.energy.BatteryStatsReceiver;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.Lock;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.MsgBlockingQueue;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.VirtualTimerTask;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
 
/**
 * The DTNService running as backend service. This is implemented as Android Service
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public class DTNService extends android.app.Service {
	private static String TAG = "DTNService";
	private Lock lock_;
	private static Context context_;
	private static boolean is_running_ = false;
	private static Timer timer_;
	private static HashMap<VirtualTimerTask, TimerTask> timer_tasks_map_;
	private static BatteryStat battery_stat_ = null;
	private static BatteryObserver battery_observer_ = null;
	
	private static boolean test_data_logging_ = false;
	public  static BatteryStat battery_stat()
	{
		return battery_stat_;
	}
	
	
	public static boolean is_running()
	{
		return is_running_;
	}
	
	public static boolean is_test_data_logging()
	{
		return test_data_logging_;
	}
	
	public static Context context()
	{
		return context_;
	}
	
	/**
	 * Getter for internal Timer
	 * @return
	 */
	public static Timer timer()
	{
		return timer_;
	}
	
	
	/**
	 * Getter for the HashMap control timer and virtual timer task
	 * @return the internal HashMap
	 * @see VirtualTimerTask, TimerTask
	 */
	public static HashMap<VirtualTimerTask,TimerTask> timer_tasks_map()
	{
		return timer_tasks_map_;
	}
	
	/**
	 * onCreate overriden frm Android Service. Initialization of the DTNServer is done here.
	 */
	@Override
	public void onCreate() {
		
		Log.d(TAG, "DTNServer: DTN Service is created ");
		try {
			init();
		} catch (ReadingConfigurationFileException e) {
			Log.d(TAG, "DTNServer:onCreate, ReadingConfigurationFileException : " + e.getMessage());
		} catch (InvalidDTNConfigurationException e) {
			Log.d(TAG, "DTNServer:onCreate, InvalidDTNConfigurationException : " + e.getMessage());
		} catch (FileNotFoundException e) {
			Log.d(TAG, "DTNServer:onCreate, FileNotFoundException : " + e.getMessage());
		}
		
	};
	
	/**
	 * onStart overridden from Android Service. The DTN Service is started here
	 */
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		start();
		Log.d(TAG, "DTNServer: BunldeDaemon is started ");
		
	}
	
	/**
	 * onDestroy override from Android Service. This happens when the parent app (DTNManager) is closed.
	 * Shuting down the service here.
	 */
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		shutdown();
		Log.d(TAG, "DTNServer: DTN Service is terminated");
	}
   
    
    

    /**
     * Init the components inside DTNServer include Timer and BatteryObserver
     *
     * @see BatteryObserver, Timer
     */
    private void init() throws  ReadingConfigurationFileException, InvalidDTNConfigurationException, FileNotFoundException
    {    	
    	// then open the configuration file for reading
    	try{
    	context_ = getApplicationContext();
    	timer_   = new Timer();
    	
    	battery_observer_ = new BatteryObserver() {
			
			
			public void update_battery_stat(BatteryStat btStat) {
				battery_stat_ = btStat;
				
			}
		};
		// start listening to battery status update
		BatteryStatsReceiver.getInstance().registerBatteryObserver(battery_observer_);
		// create a HashMap for timer_tasks_map_
    	timer_tasks_map_ = new HashMap<VirtualTimerTask,TimerTask>();
    	    	 	
    	if (getResources().getString(
				(R.string.DTNTestDataLogging_ONSDCard)).equals("true"))
		{
    		test_data_logging_ = true;
    		TestDataLogger.getInstance().init();
		}
    	
    	lock_ = new Lock();
    	
    	try {
    		DTNConfiguration.default_dtn_config().routes_setting().set_local_eid(getDefaultEID());
        	config_ = DTNConfigurationParser.parse_config_file(
        			openFileInput(getResources().getString(R.string.DTNConfigFilePath)));
        	    	
        	ConvergenceLayer.init_clayers();
        	DiscoveryTable.getInstance().init(config_);
            ContactManager.getInstance().init(config_);
        	InterfaceTable.init(config_);
        	BundleRouter.init(config_);
        	BundleDaemon.getInstance().init(config_);
        	init_datastore(config_);
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	}
    	
    	catch(Exception e)
    	{
    		throw new FileNotFoundException();
    	}

    	
    }

    private String getDefaultEID() {
    	String localEid = "dtn://" 
    		+ ((WifiManager)context().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress().replace(":", "") 
    		+ ".bytewalla.com";
    	return localEid;
	}


	/**
     *  Initialize the datastore
     * @param config
     */
    private void init_datastore(DTNConfiguration config)
    {
    	BundleStore.getInstance().init(getApplicationContext(), config );
    	RegistrationStore.getInstance().init(getApplicationContext(), config);
    	GlobalStorage.getInstance().init(getApplicationContext(), config);
    	
    	
    	if (getResources().getString(
				(R.string.DTNCleanUpInitialize)).equals("true"))
		{
    		BundleStore.getInstance().reset_storage();
    		RegistrationStore.getInstance().reset_storage();
		}
    	
    }

    /**
     *  Close the data store by calling each individual datastore close method
     */
    private void close_datastore()
    {
    	Log.d(TAG, "DTNServer closing data storage");
    	BundleStore.getInstance().close();
    	RegistrationStore.getInstance().close();
    	GlobalStorage.getInstance().close();
    }
    
    
    
    /**
     *  start DTN daemon
     */
    private void start()
    {
    	BundleDaemon.getInstance().start();
    	DiscoveryTable.getInstance().start();
    	Log.d(TAG, "DTNServer:  started Bundle Daemon");
    	is_running_ = true;
    }

    /**
     * stop the daemon daemon and shutdown the service
     */
    private void shutdown()
    {
    	Log.d(TAG, "DTNServer: shutting down dtn server");
        lock_.lock();
        try
        {
        	MsgBlockingQueue<Integer> notifier = new MsgBlockingQueue<Integer>(1);
        	Log.i(TAG, "DTNServer:shutdown, shutdown called, posting shutdown request to daemon");
        	ShutdownRequest event = new ShutdownRequest();
        	
        	// Post the shutdown request and wait until it is executed
        	BundleDaemon.getInstance().post_and_wait
        	(event, notifier, -1, true);
        
        	RegistrationTable.getInstance().shutdown();
        	DiscoveryTable.getInstance().shutdown();
        	InterfaceTable.getInstance().shutdown();
        	ContactManager.getInstance().shutdown();
        	BatteryStatsReceiver.getInstance().shutdown();
        	TestDataLogger.getInstance().shutdown();
        	close_datastore();
        	timer_.cancel();
        	timer_tasks_map_.clear();
        	
        	
        	Log.i(TAG,"DTNService shutdown routine finished");
        	
        }
        finally
        {
        	lock_.unlock();
        	is_running_ = false;
        }
    }


   /**
    * Setter for application shutdown routine
    * @param proc the routine to be set
    * @param data the data passed for the routine
    */
    public void set_app_shutdown(ServlibEventHandler proc, ServlibEventData data)
    {
    	BundleDaemon.getInstance().set_app_shutdown(proc, data);
    }

    private DTNAPIBinder dtn_api_binder_ = new DTNAPIBinder();


    /**
     * onBind method override from Android Service
     */
	
	@Override
	public IBinder onBind(Intent arg0) {
		
		return dtn_api_binder_;
	}

	static DTNConfiguration config_;
	public static DTNConfiguration getConfig() {
		if(config_ == null)
			return DTNConfiguration.default_dtn_config();
		
		return config_;
	}

};
