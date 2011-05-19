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

import java.util.ListIterator;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundlePayload.location_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.exception.BundleListLockNotHoldByCurrentThread;
import android.util.Log;


/**
 * Class to store the fragments of a bundle and keep their state.
 * @author Sharjeel Ahmed (sharjeel@kth.se)
 */

public class FragmentState{
	
	/**
	 * TAG for Android Logging
	 */

	private static String TAG = "FragmentState";
	
	/**
	 * Constructor to initialize FragmentState with existing bundle
	 * @param bundle
	 */
	public FragmentState(Bundle bundle){ 
      bundle_ = bundle;
      fragments_ = new BundleList();
    }	

	/**
	 * Constructor to initialize FragmentState with new bundle
	 */
	
    public FragmentState(){ 
        bundle_ = new Bundle(location_t.MEMORY);
        fragments_ = new BundleList();
    }	

    /**
     * Add new fragment to the list of fragment.
     * @param fragment Fragment to add to the existing list.
     * @return True if successfully added else false.
     */
    public boolean add_fragment(Bundle fragment){
        return fragments_.insert_sorted(fragment, BundleList.SORT_FRAG_OFFSET.getInstance());
    }

    /**
     * Remove the given fragment from the list
     * @param fragment Fragment to remove from the 
     * @return
     */
    public boolean erase_fragment(Bundle fragment){
    	return fragments_.erase(fragment, false);
    }
    
    /**
     * Function to check if bundle is complete or still some fragments are missing.
     * @return True if bundle is complete else false.
     */
    @SuppressWarnings("finally")
	final public boolean check_completed(){
    	
        fragments_.get_lock().lock();

        try{
        	Bundle fragment;
            ListIterator<Bundle> iter;

	        int done_up_to = 0;  // running total of completed reassembly
	        int f_len;
	        int f_offset;
	        int f_origlen;
	
	        int total_len = bundle_.payload().length();
	        
	        int fragi = 0;
	        int fragn = fragments_.size();
	        
	        
	        for (iter = fragments_.begin(); !iter.equals(fragments_.end()) ; ++fragi)
	        {
	        	
	            fragment = iter.next();
	
	            f_len = fragment.payload().length();
	            f_offset = fragment.frag_offset();
	            f_origlen = fragment.orig_length();
	            
	            assert(fragment.is_fragment())
	            :"FragmentState: Check Completed() fragment.is_fragment is false";
	            
	            
	            if (f_origlen != total_len) {
	            	
	            	Log.e("FragmentState", String.format("check_completed: error fragment orig len %d != total %d",
	                        f_origlen, total_len));
	                //panic("check_completed: error fragment orig len %d != total %d",
	                //      f_origlen, total_len);
	            }
	
	            if (done_up_to == f_offset) {
	                /*
	                 * fragment is adjacent to the bytes so far
	                 * bbbbbbbbbb
	                 *           fff
	                 */
	                Log.d(TAG, String.format("check_completed fragment %d/%d: "
	                          +"offset %d len %d total %d done_up_to %d: "
	                          +"(perfect fit)",
	                          fragi, fragn, f_offset, f_len, f_origlen, done_up_to));
	                done_up_to += f_len;
	            }
	
	            else if (done_up_to < f_offset) {
	                /*
	                 * there's a gap
	                 * bbbbbbb ffff
	                 */
	            	Log.d(TAG, String.format("check_completed fragment %d/%d: "
	                          +"offset %d len %d total %d done_up_to %d: "
	                          +"(found a hole)",
	                          fragi, fragn, f_offset, f_len, f_origlen, done_up_to));
	                return false;
	
	            }
	
	            else if (done_up_to > (f_offset + f_len)) {
	                /* fragment is completely redundant, skip
	                 * bbbbbbbbbb
	                 *      fffff
	                 */
	            	Log.d(TAG, String.format("check_completed fragment %d/%d: "
	                          +"offset %d len %d total %d done_up_to %d: "
	                          +"(redundant fragment)",
	                          fragi, fragn, f_offset, f_len, f_origlen, done_up_to));
	                continue;
	            }
	            
	            else if (done_up_to > f_offset) {
	                /*
	                 * there's some overlap, so reduce f_len accordingly
	                 * bbbbbbbbbb
	                 *      fffffff
	                 */
	            	Log.d(TAG, String.format("check_completed fragment %d/%d: "
	                          +"offset %d len %d total %d done_up_to %d: "
	                          +"(overlapping fragment, reducing len to %d)",
	                          fragi, fragn, f_offset, f_len, f_origlen, done_up_to,
	                          (f_len - (done_up_to - f_offset))));
	                
	                f_len -= (done_up_to - f_offset);
	                done_up_to += f_len;
	            }
	
	            else {
	                // all cases should be covered above
	            	Log.e(TAG, "NOT REACHABLE");
	            }
	        }
	        
            if (done_up_to == total_len) {
                Log.d(TAG, "check_completed reassembly complete!");
                return true;
            } else {
                Log.d(TAG, String.format("check_completed reassembly not done (got %d/%d)",
                          done_up_to, total_len));
                return false;
            }
            
            
        } catch (BundleListLockNotHoldByCurrentThread e) {
			e.printStackTrace();
		}finally{
        	fragments_.get_lock().unlock();
            return false;
        }


    }
    
    /**
     * Get number of total fragments in the list
     * @return Number of total fragments in the list
     */
    final public int num_fragments() { 
    	return fragments_.size(); 
    }
    
    /**
     * Get the bundle
     * @return Bundle 
     */
    public Bundle bundle() { 
    	return bundle_; 
    }
    
    /**
     * Get the list that stores all the fragments.
     * @return BundleList that stores fragments
     */
    public BundleList fragment_list() { 
    	return fragments_; 
    }
        
    /**
     * Bundle object to store the bundle metadata
     */
    private Bundle  bundle_;
    
    /**
     * List of partial fragments
     */
    private BundleList fragments_;
}


