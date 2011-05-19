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

package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event;

import java.util.Date;

import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.MsgBlockingQueue;

/**
 * Bundle Event class used in DTN System for communicating between different components.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class BundleEvent {
	/**
	 *  The enum indicating the Event Type
	 */
	protected event_type_t type_;

	/**
	 * Flag showing whether this particular event should be processed by other components as well.
	 */
	protected boolean daemon_only_ = false;

	/**
	 * Priority value to sort BundleEvent to be run in the Bundle Daemon
	 */
	private int priority_; 
	
	
	/**
	 * Notifier to be notified when the event is completed.
	 */
	public MsgBlockingQueue<Integer> processed_notifier_ = null;

	/**
	 * The time the event is posted to BundleDaemon's queue.
	 */
	protected Date posted_time_;

	/**
	 * Used for printing
	 */
	public final String type_str() {
		return type_.getCaption();
	}

	/**
	 * main Constructor
	 * @param type
	 */
	public BundleEvent(event_type_t type) {
		type_ = type;
		daemon_only_ = false;
		processed_notifier_ =  null;
	}

	/**
	 * Accessor for the enum indicating the Event Type 
	 * @return the type_
	 */
	public event_type_t type() {
		return type_;
	}

	/**
	 * Setter for the enum indicating the Event Type
	 * @param type the type_ to set
	 */
	public void set_type(event_type_t type) {
		type_ = type;
	}

	/**
	 * Accessor for the flag showing whether this particular event should be processed by other components as well.
	 * @return the daemon_only_
	 */
	public boolean daemon_only() {
		return daemon_only_;
	}

	/**
	 * Setter for the flag showing whether this particular event should be processed by other components as well.
	 * @param daemonOnly the daemon_only_ to set
	 */
	public void set_daemon_only(boolean daemon_only) {
		daemon_only_ = daemon_only;
	}

	/**
	 * Accessor for the Notifier to be notified when the event is completed
	 * @return the processed_notifier_
	 */
	public MsgBlockingQueue<Integer> processed_notifier() {
		return processed_notifier_;
	}

	/**
	 * Setter for the Notifier to be notified when the event is completed
	 * @param processedNotifier the processed_notifier_ to set
	 */
	public void set_processed_notifier(MsgBlockingQueue<Integer> processed_notifier) {
		processed_notifier_ = processed_notifier;
	}

	/**
	 * Accessor for the time the event is posted to BundleDaemon's queue.
	 * @return the posted_time_
	 */
	public Date posted_time() {
		return posted_time_;
	}

	/**
	 * Setter for the time the event is posted to BundleDaemon's queue.
	 * @param postedTime the posted_time_ to set
	 */
	public void set_posted_time(Date postedTime) {
		posted_time_ = postedTime;
	}

	/**
	 * Accessor for the priority value to sort BundleEvent to be run in the Bundle Daemon
	 * @return the priority
	 */
	public int priority() {
		return priority_;
	}

	/**
	 * Setter for the priority value to sort BundleEvent to be run in the Bundle Daemon
	 * @param priority the priority to set
	 */
	public void set_priority(int priority) {
		this.priority_ = priority;
	}

}