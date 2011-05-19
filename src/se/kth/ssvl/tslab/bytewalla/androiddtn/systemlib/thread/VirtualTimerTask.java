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


package se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread;

import java.io.Serializable;
import java.util.Date;
import java.util.TimerTask;

import se.kth.ssvl.tslab.bytewalla.androiddtn.DTNService;
import android.util.Log;

/**
 * VirtualTimerTask for adding more complex constructor than Android TimerTask. This is executed by creating associated TimerTask and put it in DTNServer's Timer
 * 
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public abstract class VirtualTimerTask implements Serializable {

 	/**
	 * Serial version UID to support serialization in Java Serializable
	 */
	private static final long serialVersionUID = -9163883997670164701L;
	
	/**
	 * String TAG for supporting Android Logging mechanism
	 */
	private static String TAG = "VirtualTimerTask";
	
	/**
	 * flag whether this task is waiting to be executed in the future, in other words, whether it is scheduled to the DTNServer Timer.
	 */
	private boolean pending_   = false;
	/**
	 * flag whether this task was already cancelled
	 */
	private boolean cancelled_ = false;
	
	/**
	 * Accessor whether this task was already cancelled
	 * @return
	 */
	public boolean cancelled()
	{
		return cancelled_;		
	}
	/**
	 * Accessor whether this task is waiting to be executed in the future, in other words, whether it is scheduled to the DTNServer Timer.
	 * @return
	 */
	public boolean pending()
	{
		return pending_;
	}
	
	/**
	 * Constructor
	 */
	public VirtualTimerTask()
	{
		super();
	}
	
	/**
	 * Cancel the task and it will not be executed anymore in the DTNServer timer
	 */
	public void cancel()
	{
		cancelled_ = true;
		TimerTask timer_task = get_associated_TimerTask();
		if (timer_task!= null)
		{
			timer_task.cancel();
			DTNService.timer_tasks_map().remove(this);
		}
		
	}
	
	/**
	 * Accessor for the associate TimerTask in the DTNServer's Timer
	 * @return the associate TimerTask in the DTNServer's Timer 
	 */
	public TimerTask get_associated_TimerTask()
	{
		TimerTask timer_task = DTNService.timer_tasks_map().get(this);
		
		
		
		if (timer_task == null)
			Log.e(TAG, "Virtual Timer Task for this is not exist");
		
		return timer_task;
	}
	
	/**
	 * Create an associate TimerTask for this VirtualTimerTask
	 * @return
	 */
	private TimerTask create_associated_TimerTask()
	{
		return new TimerTask() {
			
			
			@Override
			public void run() {
				pending_ = false;
				timeout(new Date());
				
			}};
	}
	/**
	 * Schedule to work after the delay in seconds from current time
	 */
	public void schedule_in(int schedule_delay_second)
	{
		pending_   = true;
		cancelled_ = false;
		
		// create and associated timer task
		TimerTask associated_timer_task = create_associated_TimerTask();
		
		DTNService.timer_tasks_map().put(this, associated_timer_task);
		
		DTNService.timer().schedule(associated_timer_task, (schedule_delay_second * 1000));
		
	}
	
	/**
	 * Schedule to work at the specified time
	 */
	public void schedule_at(Date when)
	{
		pending_   = true;
		cancelled_ = false;
		
		TimerTask associated_timer_task = create_associated_TimerTask();
		
		DTNService.timer_tasks_map().put(this, associated_timer_task);
		
	
		DTNService.timer().schedule(associated_timer_task, when);
	}
	
	/**
	 * Abstract timeout code to be implemented by subclass
	 */
	abstract protected void timeout(final Date now);
}
