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
import java.util.HashMap;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.Lock;
import android.util.Log;

/**
 * A set of BlockInfoVecs, one for each outgoing link.
 * 
 *  @author Sharjeel Ahmed (sharjeel@kth.se) 
 */
public class LinkBlockSet implements Serializable {
	

	/**
	 * SerialVersionID to Support Serializable.
	 */
	
	private static final long serialVersionUID = -2517952327858102558L;
	
	/**
	 * TAG for Android Logging
	 */
	private static String TAG = "LinkBlockSet";
	
	/**
	 * Lock object for this class
	 */
	private Lock lock_;
	
	/**
	 * HashMap to store BlockInfoVec of each link
	 */
	private HashMap<Link, BlockInfoVec> map_;

	/**
	 * Disable external construction of the object
	 * @param lock Lock object 
	 */
	public LinkBlockSet(Lock lock) {
		lock_ = lock;
		map_ =  new HashMap<Link, BlockInfoVec>();
	
	}

	

	/**
	 * Create a new BlockInfoVec for the given link.
	 * @param link Create a new link for given link
	 * @return Pointer to the new BlockInfoVec
	 */
	public BlockInfoVec create_blocks(final Link link) {
		lock_.lock();
		try
		{
		if (map_.get(link) != null)
		{
			Log.e(TAG, "Blocks already exist for the given link");
			return null;
		}
		
		BlockInfoVec blocks = new BlockInfoVec();
		map_.put(link, blocks);
		return blocks;
		}
		finally
		{
			lock_.unlock();
		}
	}

	/**
	 * Find the BlockInfoVec for the given link.
	 * @param link Find blocks for given link
	 * @return Pointer to the BlockInfoVec or NULL if not found
	 */
	public BlockInfoVec find_blocks(final Link link) {
		lock_.lock();
		try
		{
			return map_.get(link);
		}
		finally
		{
			lock_.unlock();
		}
	}

	/**
	 * Remove the BlockInfoVec for the given link.
	 * @param link Delete blocks for the given link
	 * @return True if successfully deleted else false
	 */
	public final boolean delete_blocks(final Link link) {
		lock_.lock();
		try
		{
			BlockInfoVec result = map_.remove(link);
			
			if ( result == null) 
			{
				Log.e(TAG, "delete_block when there are not blocks for the given link");
				return false;
			}
			
			
			return true;
		}
		finally
		{
			lock_.unlock();
		}
	}

	/**
	 * Size of map_
	 * @return Size of map
	 */
	public int size()
	{
		lock_.lock();
		try
		{
			return map_.size();
		}
		finally
		{
			
			lock_.unlock();
		}
		
	}
	
	/**
	 * Clear the map
	 */
	public void clear()
	{
		lock_.lock();
		try
		{
		    map_.clear();
		}
		finally
		{
			
			lock_.unlock();
		}
		
	}
	
	/**
	 * Get the map
	 * @return Return the map
	 */
	public HashMap<Link, BlockInfoVec> map()
	{
		lock_.lock();
		try
		{
			return map_;
		}
		finally
		{
			lock_.unlock();
		}
	}
	
	/**
	 * Get a copy of this object
	 * @return Return a copy of LinkBlockSet
	 */
	public LinkBlockSet get_copy() {
		lock_.lock();
		try
		{
			LinkBlockSet new_link_block_set = new LinkBlockSet(lock_);
			
			new_link_block_set.map().putAll(map_);
			
			
			return new_link_block_set;
		}
		finally
		{
			lock_.unlock();
		}
	}
	
	/**
	 * Test function to get the lock
	 * @return Return the lock function
	 */
    final public Lock test_get_lock()
    {
    	return lock_;
    }
}