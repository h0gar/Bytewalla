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

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleFreeEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.exception.BundleListLockNotHoldByCurrentThread;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.exception.BundleLockNotHeldByCurrentThread;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.Lock;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.List;
import android.util.Log;

/**
 * Class for Bundles list having locking function for using in Multi-thread environment.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class BundleList implements Serializable {

	/**
	 * Serial version UID to support Java Serializable
	 */
	private static final long serialVersionUID = -3471463447653544553L;

	/**
	 * String TAG for supporting Android logging mechanism
	 */
	final private String TAG = "BundleList";

	/**
	 * Internal data structure to keep track of the list
	 */
	private List<Bundle> list_;
	
	/**
	 * Event ticket to support blocking operation of the list
	 */
	private Integer ticket = new Integer(0);



	/**
	 * Default Constructor. This will generate default name and new Lock. 
	 */
	public BundleList() {
		name_ = "default_name";
		list_ = new List<Bundle>();
		lock_ = new Lock();
		own_lock_ = true;
		assert(ticket!=null);
	}

	/**
	 * Constructor with default name and specified lock.
	 */
	public BundleList(Lock lock) {
		name_ = "default_name";
		list_ = new List<Bundle>();
		lock_ = lock;
		own_lock_ = false;
	}

	/**
	 * Constructor with specified name and lock
	 */
	public BundleList(String name, Lock lock) {
		name_ = name;
		list_ = new List<Bundle>();
		lock_ = lock;
		own_lock_ = true;
	}

	/**
	 * Constructor with specified name and new lock
	 */
	public BundleList(String name) {
		name_ = name;
		list_ = new List<Bundle>();
		lock_ = new Lock();
	}

	/**
	 * "Peek at the first bundle on the list." [DTN2]
	 * @return "the bundle or null if the list is empty" [DTN2]
	 */
	public Bundle front() {
		lock_.lock();
		try {

			if (list_.size() > 0)
				return list_.get(0);
			else
				return null;

		} finally {
			lock_.unlock();
		}
	}

	/**
	 * "Peek at the last bundle on the list." [DTN2]
	 * @return "the bundle or null if the list is empty" [DTN2]
	 */
	public Bundle back() {
		lock_.lock();
		try {

			if (list_.size() > 0)
				return list_.get(list_.size() - 1);
			else
				return null;

		} finally {
			lock_.unlock();
		}
	}

	/**
	 * Add a new bundle to the front of the list.
	 */
	public boolean push_front(Bundle bundle) {

		lock_.lock();
		bundle.get_lock().lock();
		try {
			add_bundle(bundle, 0);
			return true;
		} catch (BundleLockNotHeldByCurrentThread e) {
			Log.e(TAG, e.getMessage());
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage());
		} catch (BundleListLockNotHoldByCurrentThread e) {
			Log.e(TAG, e.getMessage());
		} finally {
			bundle.get_lock().unlock();
			lock_.unlock();
			
		}
		return true;
	}

	/**
	 * Add a new bundle to the back of the list.
	 */
	public boolean push_back(Bundle bundle) {
		lock_.lock();
		bundle.get_lock().lock();
		try {
			add_bundle(bundle, list_.size());
		
		} catch (BundleLockNotHeldByCurrentThread e) {
			Log.e(TAG, e.getMessage());
			return false;
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage());
			return false;
		} catch (BundleListLockNotHoldByCurrentThread e) {
			Log.e(TAG, e.getMessage());
			return false;
		} 
		finally {
			bundle.get_lock().unlock();
			lock_.unlock();
		}
		return true;

	}

	/**
	 * Comparator to sort bundle in the list according to the Bundle Fragmentation Offset
	 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
	 */

	public static class SORT_FRAG_OFFSET implements Comparator<Bundle>, Serializable{

		/**
		 * Serial version UID to support Java Serializable
		 */
		private static final long serialVersionUID = 1755643092976266794L;
		
		/**
		 * Singleton instance implementation
		 */
		private static SORT_FRAG_OFFSET instance_ = null;

		SORT_FRAG_OFFSET() {
			// Exists only to defeat instantiation
		}

		/**
		 * Singleton interface for SORT_FRAG_OFFSET object
		 * @return
		 */
		public static SORT_FRAG_OFFSET getInstance() {
			if (instance_ == null) {
				instance_ = new SORT_FRAG_OFFSET();
			}
			return instance_;
		}

		// End Singleton Implementation of the SORT_FRAG_OFFSET
		
		public int compare(Bundle arg0, Bundle arg1) {
			if (arg0.frag_offset() < arg1.frag_offset())
				return -1;
			else if (arg0.frag_offset() > arg1.frag_offset())
				return 1;
			else
				return 0;
		}

	}

	/**
	 * Comparator to sort Bundles in the list according to Bundle delivery priority
	 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
	 */
	public static class SORT_PRIORITY implements Comparator<Bundle>, Serializable {

		
		/**
		 * Serial version UID to support serial UID
		 */
		private static final long serialVersionUID = 6723296430467979292L;
		
		/**
		 * Internal Singleton implementation variable
		 */
		private static SORT_PRIORITY instance_ = null;

		/**
		 * empty constructor
		 */
		private SORT_PRIORITY() {
			// Exists only to defeat instantiation
		}

		/**
		 * Singleton interface
		 * @return
		 */
		public static SORT_PRIORITY getInstance() {
			if (instance_ == null) {
				instance_ = new SORT_PRIORITY();
			}
			return instance_;
		}

		// End Singleton Implementation of the SORT_PRIORITY
		
		public int compare(Bundle arg0, Bundle arg1) {
			if (arg0.priority().getCode() < arg1.priority().getCode())
				return 1;
			else if (arg0.priority().getCode() > arg1.priority().getCode())
				return -1;
			else
				return 0;
		}
		
	}

	/**
	 * Insert the given bundle sorted by the given sort method.
	 */
	public boolean insert_sorted(Bundle bundle, Comparator<Bundle> sort_comparator) {
		lock_.lock();
		bundle.get_lock().lock();
		try {
			add_bundle(bundle, list_.size());
			Collections.sort(this.list_, sort_comparator);
			return true;
		} catch (BundleLockNotHeldByCurrentThread e) {
			Log.e(TAG, e.getMessage());
			return false;
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage());
			return false;
		} catch (BundleListLockNotHoldByCurrentThread e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
		finally
		{
			bundle.get_lock().unlock();
			lock_.unlock();
		}
		
	}

	/**
	 * Sort the list according to the specified comparator.
	 */
	public boolean sort(Comparator<Bundle> sort_comparator) {
		lock_.lock();
		try
		{
		Collections.sort(this.list_, sort_comparator);
		return true;
		}
		finally
		{
			lock_.unlock();
		}
	}

	/**
	 * "As a testing hook, insert the given bundle into a random location in the
	 * list." [DTN2]
	 */
	public void insert_random(Bundle bundle) {

		lock_.lock();
		try
		{
			int random_position = (int) (Math.random() * this.size());
			add_bundle(bundle, random_position);
		} catch (BundleLockNotHeldByCurrentThread e) {
			Log.e(TAG, e.getMessage());
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage());
		} catch (BundleListLockNotHoldByCurrentThread e) {
			Log.e(TAG, e.getMessage());
		}
		finally
		{
			lock_.unlock();
		
		}
	}

	/**
	 * Remove (and return)  the first bundle on the list.
	 * 
	 * @param free whether to free the bundle. This freeing will remove Bundle from storage including its payload
	 * @return a bundle or null if the BundleList is empty
	 */
	public Bundle pop_front(boolean free) {
		lock_.lock();
		try {
			if (list_.isEmpty())
				return null;

			assert (!list_.isEmpty());

			Bundle ret = del_bundle(0, free);
			
			return ret;
		} catch (BundleListLockNotHoldByCurrentThread e) {
			Log.e(TAG, e.getMessage());
			return null;
		} finally {
			lock_.unlock();
		}
	}

	/**
	 * Remove (and return) the last bundle on the list.
	 * @param free whether to free the bundle. This freeing will remove Bundle from storage including its payload
	 */
	public Bundle pop_back(boolean free) {
		lock_.lock();
		try {
			if (list_.isEmpty())
				return null;

			Bundle ret = del_bundle(list_.size() - 1, free);
		
			return ret;
		} catch (BundleListLockNotHoldByCurrentThread e) {
			Log.e(TAG, e.getMessage());
			return null;
		} finally {
			lock_.unlock();
		}

	}

	
	
	/**
	 * Remove the given bundle from the list. Returns true if the bundle was
	 * successfully removed, false otherwise.
	 * 
	 * @param free whether to free the bundle. This freeing will remove Bundle from storage including its payload
	 */
	public boolean erase(Bundle bundle, boolean free) {
		if (bundle == null) {
			return false;
		}
		lock_.lock();

		// "Now we need to take the bundle lock in order to search through
		// its mappings" [DTN2]
		bundle.get_lock().lock();

		try {
			int pos = list_.indexOf(bundle);
			if (pos == -1) return false;


			Bundle b = del_bundle(pos, free);
			assert (b == bundle);

			
		
			return true;
	
		} catch (BundleListLockNotHoldByCurrentThread e) {
			Log.e(TAG, e.getMessage());
			return false;
		} finally {
			bundle.get_lock().unlock();
			lock_.unlock();
		}
	}

	/**
	 * "Search the list for the given bundle." [DTN2]
	 * 
	 * @return true if it's in the list, false if it's not
	 */
	public boolean contains(final Bundle bundle) {
		lock_.lock();
		try {
			return list_.contains(bundle);
		} finally {
			lock_.unlock();
		}
	}

	/**
	 * "Search the list for a bundle with the given id." [DTN2]
	 * 
	 * @return the found bundle otherwise, null.
	 */
	public Bundle find(int bundleid) {

		lock_.lock();
		try {
			Iterator<Bundle> iter = list_.iterator();
			while (iter.hasNext()) {
				Bundle bundle = iter.next();
				if (bundle.bundleid() == bundleid)
					return bundle;
			}
			return null;

		} finally {
			lock_.unlock();
		}

	}

	/**
	 * "Search the list for a bundle with the given source eid and timestamp." [DTN2]
	 * 
	 * @return the found bundle otherwise, null.
	 */
	public Bundle find(final EndpointID source_eid,
			final BundleTimestamp creation_ts) {
		lock_.lock();
    	try
    	{
    		Iterator<Bundle> iter = list_.iterator();
    		while(iter.hasNext())
    		{
    			Bundle bundle = iter.next();
    			if (bundle.creation_ts().equals(creation_ts)
    					&& bundle.source().equals(source_eid))
    				return bundle;
    		}
    		return null;
    		
    	}
    	finally
    	{
    		lock_.unlock();
    	}
	}

	/**
	 * "Search the list for a bundle with the given GBOF IDthe found bundle otherwise, null
	 * 
	 * @return the found bundle otherwise, null.
	 */
	public Bundle find(GbofId gbof_id) {
		lock_.lock();
    	try
    	{
    		Iterator<Bundle> iter = list_.iterator();
    		while(iter.hasNext())
    		{
    			Bundle bundle = iter.next();
    			if (gbof_id.equals(
    					bundle.source(), 
    					bundle.creation_ts(), 
    					bundle.is_fragment(),
    					bundle.payload().length(), 
    					bundle.frag_offset())
    					)
    				return bundle;
    		}
    		return null;
    		
    	}
    	finally
    	{
    		lock_.unlock();
    	}

	}


	/**
	 * "Move all bundles from this list to another." [DTN2]
	 */
	public void move_contents(BundleList other) {
		lock_.lock();
		try {
			other.lock_.lock();
			try {

				other.list_.addAll(this.list_);
				this.list_.clear();
			} finally {
				other.get_lock().unlock();
			}
		} finally {
			lock_.unlock();
		}
	}

	/**
	 * "Clear out the list." [DTN2]
	 */
	public void clear() {
		this.lock_.lock();
		try {

			list_.clear();
		} finally {
			this.lock_.unlock();
		}
	}

	/**
	 * "Return the size of the list." [DTN2]
	 */
	public final int size() {
		lock_.lock();
		try {
			return list_.size();
		} finally {
			lock_.unlock();
		}
	}

	/**
	 * "Return whether or not the list is empty." [DTN2]
	 */
	public final boolean empty() {
		lock_.lock();
		try
		{
		return list_.isEmpty();
		}
		finally
		{
			lock_.unlock();
		}
	}

	/**
	 * "Iterator used to iterate through the list. Iterations _must_ be completed
	 * while holding the list lock, and this method will assert as such." [DTN2]
	 * 
	 * @throws BundleListLockNotHoldByCurrentThread
	 */
	public final ListIterator<Bundle> begin()
			throws BundleListLockNotHoldByCurrentThread {
		if (!lock_.isHeldByCurrentThread()) {
			throw new BundleListLockNotHoldByCurrentThread();
		}
		
		return list_.listIterator();
	}

	/**
	 * "Iterator used to mark the end of the list. Iterations _must_ be completed
	 * while holding the list lock, and this method will assert as such." [DTN2]
	 */
	public final ListIterator<Bundle> end()
			throws BundleListLockNotHoldByCurrentThread {
		if (!lock_.isHeldByCurrentThread()) {
			throw new BundleListLockNotHoldByCurrentThread();
		}
 
		return list_.listIterator(list_.size());
	}

	/**
	 * "Return the internal lock on this list." [DTN2]
	 */
	public final Lock get_lock() {
		return lock_;
	}

	/**
	 * "Helper routine to add a bundle at the indicated position." [DTN2]
	 * @throws BundleLockNotHeldByCurrentThread 
	 * @throws InterruptedException 
	 */
	private void add_bundle(final Bundle b, final int pos) throws BundleListLockNotHoldByCurrentThread, BundleLockNotHeldByCurrentThread, InterruptedException {
		if(!lock_.isHeldByCurrentThread()) throw new BundleListLockNotHoldByCurrentThread();
	    if(!b.get_lock().isHeldByCurrentThread()) throw new BundleLockNotHeldByCurrentThread();
	    
	    if (b.is_queued_on(this)) {
	        Log.e(TAG, String.format("ERROR in add bundle: " +
	                "bundle id %d already on list [%s]",
	                b.bundleid(), name_));
	        
	        return;
	    }
	  
	    list_.add(pos, b);
	    
	    
	    b.mappings().add(this);
	    
	    

	    Log.d(TAG, String.format("bundle id %d is added to list [%s] , the size become",
	              b.bundleid(), name_, list_.size()));
	    
	}

	/**
	 * Helper routine to remove a bundle from the indicated position. This is called by other public functions such as pop_front
	 * This is the function will actually post the bundle free event to the Bundle daemon
	 * @param pos
	 *            Position to delete
	 *            a flag indicate whether to free the bundle as well
	 * @param free whether to free the bundle. This freeing will remove Bundle from storage including its payload
	 * @throws BundleListLockNotHoldByCurrentThread 
	 * @returns the bundle that, before this call, was at the position
	 * 
	 */
	private Bundle del_bundle(final int pos, boolean free) throws BundleListLockNotHoldByCurrentThread {

		Bundle b = list_.get(pos);
		assert (lock_.isHeldByCurrentThread());

		if(!lock_.isHeldByCurrentThread()) throw new BundleListLockNotHoldByCurrentThread();
		
		b.get_lock().lock();
		
		
		
		try {

			Log.d(TAG, String.format(
					"bundle id %d del_bundle: deleting mapping [%s]", b
							.bundleid(), name_));

			if (!b.mappings().contains(this)) {
				Log.e(TAG, String.format("ERROR in del bundle: "
						+ "bundle id %d has no mapping for list [%s]", b
						.bundleid(), name_));
			} else {
				b.mappings().remove(this);
			}

			// "remove the bundle from the list" [DTN2]
			list_.remove(b);

			if (free)
			BundleDaemon.getInstance().post(new BundleFreeEvent(b));
			
			return b;
		} catch (BundleLockNotHeldByCurrentThread e) {
			Log.e(TAG, e.getMessage());
			return null;
		
		} finally {
			b.get_lock().unlock();

		}
	}

	/**
	 * Getter for name of the list
	 * 
	 * @returns name of the list for debugging purpose
	 */
	public String name() {
		return name_;
	}

	/**
	 * name of the list
	 */
	private String name_; 

	

	/**
	 * Lock for supporting mutual exclusion
	 */
	protected Lock lock_;
	
	/**
	 * bit to define lock ownership
	 */
	protected boolean own_lock_;  
};
