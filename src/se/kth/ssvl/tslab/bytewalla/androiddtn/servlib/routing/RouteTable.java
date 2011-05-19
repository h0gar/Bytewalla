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
import java.util.Iterator;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointIDPattern;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.Lock;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.StringVector;
import android.util.Log;
  
/**
 * Class to represent routing table in the router. It hold route entries and can check what route entries should be used for particular
 * destination.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class RouteTable{
	
		/**
		 * TAG for collaborating with Android built-in Logging system
		 */
		private static String TAG = "RouteTable_"; 
	    /**
	     * Constructor based on the name of the router as String
	     */
		public RouteTable(final String router_name)
		{
			TAG  +=  router_name;
			lock_ = new Lock();
			route_table_ = new RouteEntryVec();
		}

	   
	    /**
	     * "Add a route entry." [DTN2]
	     */
	    public boolean add_entry(RouteEntry entry)
	    {
	    	lock_.lock();
	    	try
	    	{
	    		Log.d(TAG, String.format(" add_route %s", entry.toString()));
				route_table_.add(entry);
				return true;
	    	}
	    	finally 
	    	{
	    		lock_.unlock();
	    	}
	    }
	    
	    /**
	     * "Remove a route entry." [DTN2]
	     */
	    public boolean del_entry(final EndpointIDPattern dest, final Link next_hop)
	    {
	    	lock_.lock();
	    	try
	    	{
	    		Iterator<RouteEntry> iter = route_table_.iterator();
	    		
	    		while (iter.hasNext())
	    		{
	    			RouteEntry entry = iter.next();
	    			if ( entry.dest_pattern().equals(dest) && entry.link().equals(next_hop)  )
	    			{
	    				Log.d(TAG, String.format("del_entry %s", entry.toString()));
	    				iter.remove();
	    				return true;
	    			}
	    		}
	    		Log.d(TAG, String.format("del_entry %s -> %s: no match!",
	    	              dest.uri().toString(), next_hop.name()));
	    		return false;
	    	}
	    	finally
	    	{
	    		lock_.unlock();
	    	}
	    }

	    /**
	     * Remove entries that match the given RouteEntryMatcher
	     */

	   public int del_matching_entries(RouteEntryMatcher matcher)
	    {
    		lock_.lock();
    		
	    	try
	    	{
		    		int old_size = route_table_.size();
		    		boolean found = false;
		    		Iterator<RouteEntry> iter = route_table_.iterator();
		    		
		    		while (iter.hasNext())
		    		{
		    			RouteEntry entry = iter.next();
		    			if (matcher.match(entry))
		    			{
		    				found = true;
		    				iter.remove();
		    			}
		    			
		    			
		    			
		    		}
		    		
		    		// "short circuit if nothing was deleted" [DTN2]
		    	    if (!found) 
		    	        return 0;
		    		
		    	    int num_deleted_entries = old_size - route_table_.size();
		    		return num_deleted_entries;
		    	}
		    	finally
		    	{
		    		lock_.unlock();
		    	}
	    		
	    	}
	    
	    
	    /**
	     * Remove all entries to the given endpoint id pattern.
	     *
	     * @return the number of entries removed
	     */
	    public int  del_entries(final EndpointIDPattern dest)
	    {
	    	lock_.lock();
	    	try
	    	{
	    		return del_matching_entries(new RouteEntryMatcher(dest));
	    	}
	    	finally
	    	{
	    		lock_.unlock();
	    	}
	    }

	    
	    /**
	     * Remove all entries that is going to use next_hop link
	     *
	     * @return the number of entries removed
	     **/
	    public int del_entries_for_nexthop( Link next_hop)
	    {
	    	lock_.lock();
	    	try
	    	{
	    		return del_matching_entries(new RouteEntryMatcher(next_hop));
	    	}
	    	finally
	    	{
	    		lock_.unlock();
	    	}
	    }

	    /**
	     * Clear the whole route table.
	     */
	    public void clear()
	    {
	    	lock_.lock();
	    	try
	    	{
	    		route_table_.clear();
	    	}
	    	finally
	    	{
	    		lock_.unlock();
	    	}
	    
	    }

	    /**
	     * "Fill in the RouteEntryVec with the list of all entries whose
	     * patterns match the given eid and next hop." [DTN2] If the next hop is
	     * null, it is ignored.
	     *
	     * @return the count of matching entries
	     */
	    public int get_matching(final EndpointID eid,
	    		final Link next_hop, RouteEntryVec entry_vec) 
	    {
	    	lock_.lock();
	    	try
	    	{
	    		boolean[] loop = new boolean[1];
	    		loop[0] = false;
	    		Log.d(TAG, String.format("get_matching %s (link %s)...",
	    				eid.uri().toString(), 
	    				next_hop != null ? next_hop.name() : "NULL"
	    					
	    		        )
	    		     );
	    		
	    		int ret = get_matching_helper(eid, next_hop, entry_vec, loop, 0);
	    	    if (loop[0]) {
	    	        Log.w(TAG, String.format("route destination %s caused route table lookup loop",
	    	                 eid.uri().toString()));
	    	    }
	    		
	    		return ret;
	    	}
	    	finally
	    	{
	    		lock_.unlock();
	    	}
	    }
	    
	    /**
	     * Another proxy to call the main get_matching
	     *
	     * @return the list of matching entries
	     */
	    public int get_matching(final EndpointID eid , RouteEntryVec entry_vec) 
	    {
	    	return get_matching(eid, null, entry_vec);
	    }

	    /**
	     * Dump a string representation of the routing table.
	     */
	    public void dump(StringBuffer buf) 
	    {
	    	lock_.lock();
	    	try
	    	{
	    		StringVector long_strings = new StringVector();

	    	    // "calculate appropriate lengths for the long strings" [DTN2]
	    	    int dest_eid_width   = 10;
	    	    int source_eid_width = 6;
	    	    int next_hop_width   = 10;
	    		
	    	    Iterator<RouteEntry> itr = route_table_.iterator();
	    	    
	    	    while( itr.hasNext())
	    	    {
	    	    	RouteEntry e = itr.next();
	    	    	dest_eid_width   = Math.max(dest_eid_width, e.dest_pattern().length());
	    	    	source_eid_width = Math.max(source_eid_width, e.source_pattern().length());
		    	    next_hop_width   = Math.max(next_hop_width, (e.link() != null) ?  e.link().name().length() :  e.route_to().length());
	    	    	
	    	    	
	    	    }
	    	    
	    	    
	    	

	    	    dest_eid_width   = Math.min(dest_eid_width,  25);
	    	    source_eid_width = Math.min(source_eid_width,15);
	    	    next_hop_width   = Math.min(next_hop_width,  15);
	    	    
	    	    RouteEntry.dump_header(buf, dest_eid_width, source_eid_width, next_hop_width);
	    	    
	    	    itr = route_table_.listIterator();
	    	    
	    	    while( itr.hasNext())
	    	    {
	    	    	RouteEntry e = itr.next();
	    	    	e.dump(buf, long_strings, dest_eid_width, source_eid_width, next_hop_width);
	    	    	
	    	    	
	    	    }
	    	    
	    	    
	    	    
	    	    if (long_strings.size() > 0) 
	    	    {
	    	        buf.append("\nLong EIDs/Links referenced above:\n");
	    	       
	    	        for (int i=0; i < long_strings.size(); i++)
	    	        {
	    	        	String long_string = long_strings.get(i);
	    	        	buf.append(String.format("\t[%d]: %s\n", i, long_string));
	    	        }
	    	        
	    	        buf.append("\n");
	    	    }
	    	    
	    	    buf.append("\nClass of Service (COS) bits:\n" + 
	    	                "\tB: Bulk  N: Normal  E: Expedited\n\n");
	    	    
	    		
	    		
	    	}finally
	    	{
	    		lock_.unlock();
	    	}
	    	
	    	
	    }

	    /**
	     * Return the size of the table.
	     */
	    public int size() { return route_table_.size(); }

	    /**
	     * Return the routing table.  
	     */
	    public final RouteEntryVec route_table()
	    {
	    	assert lock_.isHeldByCurrentThread() : "RouteTable.route_table must be called while holding lock";
	    	return route_table_;
	    }

	    /**
	     * Test method for setting lock
	     */
	    public void test_set_lock(Lock lock)
	    {
	    	lock_ = lock;
	    }
	    
	    /**
	     * Accessor for the RouteTable internal lock.
	     */
	    public Lock lock() { return lock_; }

	
	    /**
	     *  Helper function for get_matching. It's a recursive function to get the actual RouteEntry
	     * @param eid EndpointID to find
	     * @param next_hop The next hop link of the route
	     * @param entry_vec the entry_vec to fill up
	     * @param loop sanity flag checker whether the running system is looping or not
	     * @param level the current depth level of this recursive call
	     * @return the number of matching
	     */
		protected int get_matching_helper( final EndpointID eid,
	                               final Link    next_hop,
	                               RouteEntryVec    entry_vec,
	                               boolean[]             loop,
	                               int               level) 
	    {
			lock_.lock();
	    	try
	    	{
	    		int count = 0;
	    		
	    		Iterator<RouteEntry> iter = route_table_.iterator();
	    		
	    		while (iter.hasNext())
	    		{
	    			RouteEntry entry = iter.next();
	    			
	    			Log.d(TAG, String.format("check entry %s", entry.toString()));
	    			
	    			if (!entry.dest_pattern().match(eid))
	    				continue;
	    			
	    			if (entry.link() == null)
	    			{
	    				assert entry.route_to().length()!=0 : "RouteTable get_matching_helper assertion fail";
	    				
	    				if (level >= BundleRouter.config().max_route_to_chain())
	    				{
	    					loop[0] = true;
	    					continue;
	    				}
	    				
	    				count += get_matching_helper(entry.route_to(), next_hop , entry_vec, loop,
	    						                                    level+1);  
	    				
	    				
	    			}
	    			// "next_hop is null when we were adding route_to style
	    			// entry.link() .equals next hop when we were adding next_hop style" [DTN2]
	    			else if ( next_hop == null || entry.link().equals(next_hop))
	    			{
	    				// "Adding entry only when we can know the link" [DTN2]
	    				if(!entry_vec.contains(entry))
	    				{
	    					Log.d(TAG,String.format("match entry %s", entry.toString() ));
	    					entry_vec.add(entry);
	    					++count;
	    				}
	    				else
	    				{
	    					Log.d(TAG,String.format("entry %s already in matches... ignoring", entry.toString() ));
	    				}
	    				
	    			}
	    			
	    		}
	    		
	    		
	    		Log.d(TAG, String.format("get_matching %s done (level %d), %d match(es)",
	    				eid.toString(), level, count));
	    	    
	    		return count;
	    	}
	    	finally {
	    		lock_.unlock();
	    	}
	    }
	    
	    /**
	     *  The list of route Entry
	     */
		protected RouteEntryVec route_table_;

	    /**
	     * Lock to protect internal data structures.
	     */
		protected Lock lock_;
	};

	