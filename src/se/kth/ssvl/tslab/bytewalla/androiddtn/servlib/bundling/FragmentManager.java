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

import java.util.HashMap;
import java.util.ListIterator;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundlePayload.location_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol.block_flag_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol.bundle_block_type_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol.status_report_reason_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleDeleteRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.ReassemblyCompletedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.List;
import android.util.Log;

/**
 * Fragment Manger stores the state of all the partial bundles/fragmentary bundles,
 * create bundle fragments from large bundles and reconstruct the whole bundle from the 
 * fragments.
 * 
 * @author Sharjeel Ahmed (sharjeel@kth.se)
 */
public class FragmentManager{

	/**
	 *  Singleton instance Implementation of the FragmentManager
	 */
	
	private static FragmentManager instance_;
	
	/**
	 * TAG for Android Logging
	 */
	private static String TAG = "FragmentManager";
	
    /**
     * Singleton Implementation to get the instance of FragmentManager
     * @return an singleton instance of FragmentManager
     */    	
	public static FragmentManager getInstance() {
		if (instance_ == null) {
			instance_ = new FragmentManager();
		}
		fragment_table_ = new HashMap<String, FragmentState>();  
		return instance_;
	}
	
	/**
     * Constructor.
     */
    public FragmentManager(){
    }
    
    /**
     * Create a bundle fragment from another bundle.
     * @param bundle
     *   the source bundle from which we create the
     *   fragment. Note: the bundle may itself be a fragment
     * @param offset
     *   the offset relative to this bundle (not the
     *   original) for the for the new fragment. note that if this
     *   bundle is already a fragment, the offset into the original
     *   bundle will be this bundle's frag_offset + offset
     * @param length
     *   the length of the fragment we want
     * @return
     *   Newly created bundle
     */
   public Bundle create_fragment(Bundle bundle,
                            BlockInfoVec blocks,
                            int offset,
                            int length){
    	
        Bundle fragment = new Bundle(location_t.MEMORY);
        bundle.copy_metadata(fragment);
        fragment.set_is_fragment(true);
        fragment.set_do_not_fragment(false);

        // initialize the fragment's orig_length and figure out the offset
        // into the payload
        if (! bundle.is_fragment()) {
            fragment.set_orig_length(bundle.payload().length());
            fragment.set_frag_offset(offset);
        } else {
            fragment.set_orig_length(bundle.orig_length());
            fragment.set_frag_offset(bundle.frag_offset() + offset);
        }

        // check for overallocated length
        if ((offset + length) > fragment.orig_length()) {
            Log.e(TAG, String.format("fragment length overrun: "
                  +"orig_length %d frag_offset %d requested offset %d length %d",
                  fragment.orig_length(), fragment.frag_offset(),
                  offset, length));
            
            //Panic;
            return null;
        }

        // initialize payload
        fragment.payload().write_data(bundle.payload(), offset, length, 0);

        // copy all blocks that follow the payload, and all those before
        // the payload that are marked with the "must be replicated in every
        // fragment" bit
        ListIterator<BlockInfo> iter = blocks.listIterator();
        
//        BlockInfoVec vec; 

        boolean found_payload = false;

        while (iter.hasNext())
        {
        	BlockInfo entry = iter.next();
        	
        	int type = entry.type().getCode();
        	
            if ((type == bundle_block_type_t.PRIMARY_BLOCK.getCode())
                    || (bundle_block_type_t.PAYLOAD_BLOCK.getCode()>0)
                    || found_payload
                    || ((entry.flags() & block_flag_t.BLOCK_FLAG_REPLICATE.getCode())>0)) {

                    // we need to include this block; copy the BlockInfo into the
                    // fragment
                    fragment.recv_blocks().add(entry);
                    
                    if (type == bundle_block_type_t.PAYLOAD_BLOCK.getCode()) {
                        found_payload = true;
                    }
                }        	
        }
        return fragment;
    }

   /**
    * Create a fragment to be sent out on a particular link.
    * @param bundle Source Bundle from which we create a fragment.
    * @param link Link on which we will send the fragment
    * @param blocks_to_copy Blocks to copy on this fragment.
    * @param offset The offset relative to this bundle for the for the new fragment
    * @param max_length Maximum length of the newly created fragment
    * @return Newly created fragment.
    */
    public Bundle create_fragment(Bundle bundle,
                            final Link link,
                            final BlockInfoVec blocks_to_copy,
                            int offset, 
                            int max_length){
        int block_length = 0;
        
        ListIterator<BlockInfo> entry = blocks_to_copy.listIterator();

        BlockInfo block_i;
        
        while(entry.hasNext()){
        	block_i = entry.next();
        	if(block_i.type()==bundle_block_type_t.PRIMARY_BLOCK){
        		block_length += block_i.data_length();
        	}
        	else{
        		block_length += block_i.data_offset();	// data offset is the starting point of
        	}
        }
            
        if (block_length > max_length) {
            Log.e(TAG, String.format("unable to create a fragment of length %s; minimum length "
                    +"required is %s", max_length, block_length));
            return null;
        }
        
        Bundle fragment = new Bundle(location_t.DISK);

        // copy the metadata into the new fragment (which can be further fragmented)
        bundle.copy_metadata(fragment);
        fragment.set_is_fragment(true);
        fragment.set_do_not_fragment(false);
        
        // initialize the fragment's orig_length and figure out the offset
        // into the payload
        if (! bundle.is_fragment()) {
            fragment.set_orig_length(bundle.payload().length());
            fragment.set_frag_offset(offset);
        } else {
            fragment.set_orig_length(bundle.orig_length());
            fragment.set_frag_offset(bundle.frag_offset() + offset);
        }

        // initialize payload
        int to_copy = Math.min(max_length - block_length, bundle.payload().length() - offset);
        fragment.payload().set_length(to_copy);
        fragment.payload().write_data(bundle.payload(), offset, to_copy, 0);
        BlockInfoVec xmit_blocks = fragment.xmit_link_block_set().create_blocks(link);
        
        entry = blocks_to_copy.listIterator(); //reset Iterator
        
        while(entry.hasNext()){
        	block_i = entry.next();

        	xmit_blocks.add(block_i);
        	
        }
        
        Log.d(TAG, String.format("created %s byte fragment bundle with %s bytes of payload",
                  to_copy + block_length, to_copy));

        return fragment;
    }

    /**
     * Given the given fragmentation threshold, determine whether the
     * given bundle should be split into several smaller bundles. If
     * so, this returns true and generates a bunch of bundle received
     * events for the individual fragments.
     * 
     * @param bundle Bundle to split into fragments 
     * @param link Bundle to send on the given link
     * @param max_length Maximum length of the fragment
     * @return FragmentState that has the list of created fragments for the given bundle.
     */
    public FragmentState proactively_fragment(Bundle bundle, 
                                        final Link link,
                                        int max_length){
        int payload_len = bundle.payload().length();
        
        Bundle fragment;
    	
        FragmentState state = new FragmentState(bundle);
        
        int todo = payload_len;
        int offset = 0;
        int count = 0;
        
       BlockInfoVec first_frag_blocks = new BlockInfoVec();
       BlockInfoVec all_frag_blocks = new BlockInfoVec();
       BlockInfoVec this_frag_blocks = first_frag_blocks;
        
       ListIterator<BlockInfo> entry = bundle.xmit_link_block_set().find_blocks(link).listIterator();
        
        while(entry.hasNext()){
            BlockInfo block_info = entry.next();
            
            if ((block_info.type() == bundle_block_type_t.PRIMARY_BLOCK) ||
            (block_info.type() == bundle_block_type_t.PAYLOAD_BLOCK)) {
            	
                all_frag_blocks.add(block_info);
                first_frag_blocks.add(block_info);
                
            }
            
            else if ((block_info.flags() & block_flag_t.BLOCK_FLAG_REPLICATE.getCode())>0){
            	   all_frag_blocks.add(block_info);
            }
             
            else{
            	   first_frag_blocks.add(block_info);
            }
             
        }
        
        do {
            fragment = create_fragment(bundle, link, this_frag_blocks, 
                                       offset, max_length);
            assert(fragment!=null):
            	TAG+": proactively_fragment() fragment not valid";
            
            state.add_fragment(fragment);
            offset += fragment.payload().length();
            todo -= fragment.payload().length();
            this_frag_blocks = all_frag_blocks;
            ++count;
            
        } while (todo > 0);
        
        Log.d(TAG, String.format("proactively fragmenting "
                +"%s byte payload into %s %s byte fragments",
                payload_len, count, max_length));
        
        String[] hash_key = new String[1];
        get_hash_key(fragment, hash_key);
        fragment_table_.put(hash_key[0], state);

        return state;
    }
    
    /**
     * Get the state of fragment from the fragment table 
     * @param bundle Fragment to find to the state
     * @return If found return the state else null
     */
    public FragmentState get_fragment_state(Bundle bundle){
        String[] hash_key = new String[1];
        
        get_hash_key(bundle, hash_key);
        
        FragmentState state = fragment_table_.get(hash_key);

        if (state == null){
            return null;
        } else {
            return state;
        }
    }
    
    /**
     * Remove Fragment state from the fragment table
     * @param fragment FragmentState to remove
     */
    public void erase_fragment_state(FragmentState fragment_state){
        String[] hash_key = new String[1];
        
        get_hash_key(fragment_state.bundle(), hash_key);
        fragment_table_.remove(hash_key);
    }

    
    /**
     * Given a newly arrived bundle fragment, append it to the table
     * of fragments and see if it allows us to reassemble the bundle.
     * If it does, a ReassemblyCompletedEvent will be posted.
     * 
     * @param fragment Newly received fragment
     */
    public void process_for_reassembly(Bundle fragment){
        FragmentState state;

        assert(fragment.is_fragment())
        :TAG+": process_for_reassembly() not a fragment";

        String[] hash_key = new String[1];
        get_hash_key(fragment, hash_key);
        
        FragmentState iter = fragment_table_.get(hash_key);

        Log.d(TAG, String.format("processing bundle fragment id=%s hash=%s %s",
                  fragment.bundleid(), hash_key,
                  fragment.is_fragment()));

        if (iter == null) {
            
            state = new FragmentState();

            fragment.copy_metadata(state.bundle());
            state.bundle().set_is_fragment(false);
            state.bundle().payload().set_length(fragment.orig_length());
            fragment_table_.put(hash_key[0], state);
        } else {
            state = iter;
            Log.d(TAG, String.format("found reassembly state for key %s (%s fragments)",
                      hash_key, state.fragment_list().size()));
        }

        // stick the fragment on the reassembly list
        state.add_fragment(fragment);
        
        // store the fragment data in the partially reassembled bundle file
        int fraglen = fragment.payload().length();
        
        Log.d(TAG, String.format("write_data: length_=%s src_offset=%s dst_offset=%s len %s",
                  state.bundle().payload().length(), 
                  0, fragment.frag_offset(), fraglen));

        state.bundle().payload().write_data(fragment.payload(), 0, fraglen,
                       fragment.frag_offset());
        
        // reassembled bundle, but eventually reassembly will have to do much more
        if (fragment.frag_offset() == 0 &&
            state.bundle().recv_blocks().size()>0)
        {
            BlockInfo block_i;
            
	        ListIterator<BlockInfo> entry = fragment.recv_blocks().listIterator();
	        
	        while(entry.hasNext()){
	        	block_i = entry.next();
                state.bundle().recv_blocks().add(block_i);
	        }
        }
        
        // check see if we're done
        if (state.check_completed()) {
            return;
        }

        BundleDaemon.getInstance().post_at_head(new ReassemblyCompletedEvent(state.bundle(),
                                          state.fragment_list()));
        assert(state.fragment_list().size() == 0)
        :TAG+": process_for_reassembly size not 0"; // moved into the event
        fragment_table_.remove(hash_key);
    }
    /**
     * Delete any fragments that are no longer needed given the incoming (non-fragment) bundle.
     */
    public void delete_obsoleted_fragments(Bundle bundle){
    	
        FragmentState state;
       
        // cons up the key to do the table lookup and look for reassembly state
        String[] hash_key = new String[1];
        hash_key[0] = "";
        
        get_hash_key(bundle, hash_key);
        state = fragment_table_.get(hash_key);

        Log.d(TAG, String.format("checking for obsolete fragments id=%s hash=%s...",
                  bundle.bundleid(), hash_key[0]));
        
        if (state == null) {
            Log.e(TAG, String.format("no reassembly state for key %s",
                      hash_key[0]));
            return;
        }

        Log.d(TAG, String.format("found reassembly state... deleting %d fragments",
                  state.num_fragments()));

        state.fragment_list().get_lock().lock();
        try{
        
            while (state.fragment_list().size()>0) {
                BundleDaemon.getInstance().post(new BundleDeleteRequest(state.fragment_list().pop_back(false),
                		status_report_reason_t.REASON_NO_ADDTL_INFO));
            }

            assert(state.fragment_list().size() == 0)
            :TAG+": delete_obsoleted_fragments Size not 0"; // moved into events
            
        }finally{
        	state.fragment_list().get_lock().unlock();
        }

        fragment_table_.remove(hash_key);
    }

    /**
     * Delete reassembly state for a bundle.
     */
    public void delete_fragment(Bundle fragment){

    	FragmentState state;

    	assert(fragment.is_fragment())
        :TAG+"delete_fragment() not a fragment";

        // cons up the key to do the table lookup and look for reassembly state
        String[] hash_key = new String[1];
        get_hash_key(fragment, hash_key);
        
        
        state = fragment_table_.get(hash_key);

        // remove the fragment from the reassembly list
        boolean erased = state.erase_fragment(fragment);

        // fragment was not in reassembly list, simply return
        if (!erased) {
            return;
        }

        // note that the old fragment data is still kept in the
        // partially-reassembled bundle file, but there won't be metadata
        // to indicate as such
        
        // delete reassembly state if no fragments now exist
        if (state.num_fragments() == 0) {
            fragment_table_.remove(hash_key);
        }
    }
    
    /**
     * Calculate a hash table key from a bundle
     * @param bundle Bundle to find the find key
     * @param key Save the key on the first index of the String Array
     */
    protected void get_hash_key(final Bundle bundle, String[] key){
        String temp;
        
        temp = String.format("%s.%s",
                 bundle.creation_ts().seconds(),
                 bundle.creation_ts().seqno());
        
        temp = temp + bundle.source().toString();
        temp = temp + bundle.dest().toString();

        key[0] = temp;
    }

    /**
     * Hash Table to store the partial bundles
     */
    static HashMap<String, FragmentState> fragment_table_;  
    
}

class BlockInfoPointerList extends List<BlockInfo>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}