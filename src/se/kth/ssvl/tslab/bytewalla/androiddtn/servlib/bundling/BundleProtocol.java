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


import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.List;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.SerializableByteBuffer;
import android.util.Log;

/**
 * The main class for converting a Java object from/to binary representation. The convergence layer uses this 
 * class for sending/receiving data from the lower layer. This class relies heavily on the registered BlockProcessors to actually generate or consume data.
 * 
 * @see BlockProcessor
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class BundleProtocol {
	
	private final static String TAG = "BundleProtocol";
	/**
	 * "Register a new BlockProcessor handler to handle the given block type code
	 * when received off the wire." [DTN2]
	 */
	public static void register_processor(BlockProcessor bp) {
        boolean already_registered = processors_.contains(bp);
	
		
		if (!already_registered)
		{
			processors_.add(bp);
		}
	}

	/**
	 * "Find the appropriate BlockProcessor for the given block type code." [DTN2]
	 */
	public static BlockProcessor find_processor(bundle_block_type_t type) {
		
		Iterator<BlockProcessor> iter = processors_.iterator();
		while (iter.hasNext())
		{
			BlockProcessor bp = iter.next();
			if(bp.block_type() == type)
			{
				return bp;
			}
		}

		
		return null;
	}

	/**
	 * "Initialize the default set of block processors." [DTN2]
	 */
	public static void init_default_processors() {
	    register_processor(new PrimaryBlockProcessor());
	    register_processor(new PayloadBlockProcessor());
	}

	/**
	 * "Give the processors a chance to chew on the bundle after reloading from
	 * disk." [DTN2]
	 */
	public static void reload_post_process(Bundle bundle) {
		BlockInfoVec recv_blocks = bundle.recv_blocks();

		Iterator<BlockInfo> iter = recv_blocks.iterator();
		while (iter.hasNext())
		{
			// "allow BlockProcessors [and Ciphersuites] a chance to re-do
	        // things needed after a load-from-store" [DTN2]
			BlockInfo block = iter.next();
			block.owner().reload_post_process(bundle, recv_blocks, block);
		}
	      
	}

	/**
     * Bundle Processing Status Report Flags
     */
    public static enum bundle_processing_report_flag_t {
   	    REQUEST_STATUS_RECEIVED(1 << 14),
   	    REQUEST_STATUS_CUSTODY_ACCEPTED(1 << 15),
   	    REQUEST_STATUS_FORWARDED(1 << 16),
     	REQUEST_STATUS_DELIVERED(1 << 17),
   	    REQUEST_STATUS_DELETED(1 << 18);

		private static final Map<Integer, bundle_processing_report_flag_t> lookup = new HashMap<Integer, bundle_processing_report_flag_t>();

		static {
			for (bundle_processing_report_flag_t s : EnumSet
					.allOf(bundle_processing_report_flag_t.class))
				lookup.put(s.getCode(), s);
		}

		private int code_;

		private bundle_processing_report_flag_t(int code) {
			this.code_ = code;
		}

		public int getCode() {
			return code_;
		}

		public static bundle_processing_report_flag_t get(int code) {
			return lookup.get(code);
		}
	}
    
    
	/**
	 * "Generate a BlockInfoVec for the outgoing link and put it into
	 * xmit_blocks_." [DTN2]
	 * 
	 * @return a list of new BLockInfo
	 */
	public static BlockInfoVec prepare_blocks(Bundle bundle, final Link link) {
		// "create a new block list for the outgoing link by first calling
	    // prepare on all the BlockProcessor classes for the blocks that
	    // arrived on the link" [DTN2]
	    BlockInfoVec xmit_blocks = bundle.xmit_link_block_set().create_blocks(link);
	    BlockInfoVec recv_blocks = bundle.recv_blocks();
	   
	    if (recv_blocks.size() > 0) {
	        // "if there is a received block, the first one better be the primary" [DTN2]
	        assert(recv_blocks.front().type() == bundle_block_type_t.PRIMARY_BLOCK);
	    
	        Iterator<BlockInfo> iter = recv_blocks.iterator();
	        while(iter.hasNext())
	        {
	        	BlockInfo block = iter.next();
	        	
	            if (bundle.fragmented_incoming()
	                && xmit_blocks.find_block(BundleProtocol.bundle_block_type_t.PAYLOAD_BLOCK) != null) {
	                continue;
	            }
	            
	            block.owner().prepare(bundle, xmit_blocks, block, link,
	                                   BlockInfo.list_owner_t.LIST_RECEIVED);
	        }
	        
	        
	    }
	    else {
	        Log.d(TAG, "adding primary and payload block");
	        BlockProcessor bp = find_processor(BundleProtocol.bundle_block_type_t.PRIMARY_BLOCK);
	        bp.prepare(bundle, xmit_blocks, null, link, BlockInfo.list_owner_t.LIST_NONE);
	        bp = find_processor(bundle_block_type_t.PAYLOAD_BLOCK);
	        bp.prepare(bundle, xmit_blocks, null, link, BlockInfo.list_owner_t.LIST_NONE);
	    }


	    // "now we also make sure to prepare() on any registered processors
	    // that don't already have a block in the output list. this
	    // handles the case where we have a locally generated block with
	    // nothing in the recv_blocks vector" [DTN2]
	    
	    Iterator<BlockProcessor> itr = processors_.iterator();
	    while(itr.hasNext())
	    {
	    	BlockProcessor bp = itr.next();
		      if (! xmit_blocks.has_block(bp.block_type())) {
			        bp.prepare(bundle, xmit_blocks, null, link, BlockInfo.list_owner_t.LIST_NONE);
			   }
		    
	    }
	    
	    return xmit_blocks;
	}

	/**
	 * "Generate contents for the given BlockInfoVec on the given Link." [DTN2]
	 * 
	 * @return "the total length of the formatted blocks for this bundle." [DTN2]
	 */
	public static int generate_blocks(Bundle bundle, BlockInfoVec blocks,
			final Link link) {
		// "now assert there's at least 2 blocks (primary + payload) and
	    // that the primary is first" [DTN2]
	    assert(blocks.size() >= 2);
	    assert(blocks.front().type() == bundle_block_type_t.PRIMARY_BLOCK);

	    // "now we make a pass through the list and call generate on
	    // each block processor" [DTN2]
	    
	    for (int i=0; i < blocks.size(); i++)
	    {
	    	boolean last = i == blocks.size() - 1;
	    	
	    	BlockInfo iter = blocks.get(i);
	    	
	        iter.owner().generate(bundle, blocks, iter, link, last);

	        Log.d(TAG, String.format("generated block (owner %s type %s) "+
	                    "data_offset %d data_length %d , contents_length %d",
	                    iter.owner().block_type(), 
	                    iter.type(),
	                    iter.data_offset(), 
	                    iter.data_length(),
	                    iter.contents().position()));
	        
	        if (last) {
	            assert((iter.flags() & BundleProtocol.block_flag_t.BLOCK_FLAG_LAST_BLOCK.getCode()) != 0);
	        } else {
	            assert((iter.flags() & BundleProtocol.block_flag_t.BLOCK_FLAG_LAST_BLOCK.getCode()) == 0);
	        }

	    	
	    }
	    	
	    // "Now that all the EID references are added to the dictionary,
	    // generate the primary block." [DTN2]
	    PrimaryBlockProcessor pbp =    (PrimaryBlockProcessor)find_processor(BundleProtocol.bundle_block_type_t.PRIMARY_BLOCK);
	    assert(blocks.front().owner() == pbp);
	    pbp.generate_primary(bundle, blocks, blocks.front());
	    
	    // "make a final pass through, calling finalize() and extracting
	    // the block length" [DTN2]
	    int total_len = 0;
	    for (int i=blocks.size() -1; i >= 0; i--)
	    {
	    	BlockInfo iter = blocks.get(i);
	        iter.owner().finalize(bundle, blocks, iter, link);
	        total_len += iter.full_length();
	    }
	    
	    return total_len;
	}

	/**
	 * "Remove blocks for the Bundle from the given link." [DTN2]
	 * @param bundle
	 * @param link
	 */
	public static void delete_blocks(Bundle bundle, final Link link) {
		 assert(bundle != null);

		 bundle.xmit_link_block_set().delete_blocks(link);

	}

	/**
	 * "Return the total length of the formatted bundle block data." [DTN2]
	 */
	public static int total_length(final BlockInfoVec blocks) {

		 int ret = 0;
		 Iterator<BlockInfo> itr = blocks.iterator();
		 while(itr.hasNext())
		 {
			 BlockInfo block = itr.next();
			 ret += block.full_length();
		 }
		 return ret;
	}

	/**
	 * "Temporary helper function to find the offset of the first byte of the
	 * payload in a BlockInfoVec." [DTN2]
	 */
	public static int payload_offset(final BlockInfoVec blocks) {

		int ret = 0;
	    Iterator<BlockInfo> itr = blocks.iterator();
		while(itr.hasNext())
		{
			 BlockInfo block = itr.next();
			 
			 
			 if (block.type() == BundleProtocol.bundle_block_type_t.PAYLOAD_BLOCK) {
		            ret += block.data_offset();
		            return ret;
		        }

			 
			 ret += block.full_length();
		}
	    
	    return ret;
	}

	/**
	 * "Copies out a chunk of formatted bundle data at a specified offset from
	 * the provided BlockInfoVec." [DTN2]
	 * 
	 * @return "the length of the chunk produced (up to the supplied length) and
	 *         sets last to true if the bundle is complete." [DTN2]
	 */
	public static int produce(final Bundle bundle, final BlockInfoVec blocks,
			IByteBuffer data, int offset, int len, boolean[] last) {

			int old_position = data.position();
			int origlen = len;
		    last[0] = false;

		    if (len == 0)
		        return 0;
		    

		    assert(!blocks.isEmpty());
		    
		    
		    Iterator<BlockInfo> iter = blocks.iterator();
		    
		    BlockInfo current_block = iter.next();
		    // "advance past any blocks that are skipped by the given offset."[DTN2]
		    while (offset >= current_block.full_length()) {
		    	
		    	
		    	
		        Log.d(TAG, String.format("BundleProtocol::produce skipping block type %s " +
		                    "since offset %d >= block length %d",
		                    current_block.type().toString(),
		                    offset,
		                    current_block.full_length()));
		        
		        offset -= current_block.full_length();
		        current_block = iter.next();
		        
		    }
		    // "the offset value now should be within the current block" [DTN2]
		    
		    while (true) {
		    	// The first time remainder will be minus from leftover offset above
		    	// but later on it will be the full content of the block
		        int remainder = current_block.full_length() - offset;
		        int tocopy    = Math.min(len, remainder);
		        Log.d(TAG, String.format("BundleProtocol::produce copying %d/%d bytes from " +
		                    "block type %s at offset %d",
		                    tocopy, 
		                    remainder, 
		                    current_block.type().toString(), 
		                    offset
		                    
		        
		        		));
		        current_block.owner().produce(bundle, current_block, data, offset, tocopy);
		        
		        len    -= tocopy;
		        
		        // move the position of IByteBuffer
		        data.position(data.position() + tocopy);
		      
		        // "if we've copied out the full amount the user asked for,
		        // we're done. note that we need to check the corner case
		        // where we completed the block exactly to properly set the
		        // last bit" [DTN2]
		        if (len == 0) {
		            if ((tocopy == remainder) &&
		                ((current_block.flags() & BundleProtocol.block_flag_t.BLOCK_FLAG_LAST_BLOCK.getCode()) > 0))
		            {
		                
		                last[0] = true;
		            }
		            
		            break;
		        }

		        // "we completed the current block, so we're done if this
		        // is the last block, even if there's space in the user buffer" [DTN2]
		        assert(tocopy == remainder);
		        if ((current_block.flags() & BundleProtocol.block_flag_t.BLOCK_FLAG_LAST_BLOCK.getCode()) > 0) {
		           
		            last[0] = true;
		            break;
		        }
		        
		        // advance to next block
		        current_block = iter.next();
		        offset = 0;

		    }
		    
		    Log.d(TAG, String.format("BundleProtocol::produce complete: " +
		                "produced %d bytes, bundle id %d, status is  %s",
		                origlen - len,
		                bundle.bundleid(), 
		                last[0] ? "complete" : "not complete"));
		    
		    
		    data.position(old_position);
		    return origlen - len;
	}

	/**
	 * "Parse the supplied chunk of arriving data and append it to the
	 * rcvd_blocks_ list in the given bundle, finding the appropriate
	 * BlockProcessor element and calling its receive() handler.
	 * 
	 * When called repeatedly for arriving chunks of data, this properly fills
	 * in the entire bundle, including the in_blocks_ record of the arriving
	 * blocks and the payload (which is stored externally)." [DTN2]
	 * 
	 * @return "the length of data consumed or -1 on protocol error, plus sets
	 *         last to true if the bundle is complete." [DTN2]
	 */
	public static int consume(Bundle bundle, IByteBuffer data, int len,
			boolean []last) {
		    
			int old_position = data.position();
		    int origlen = len;
		    last[0] = false;

		    BlockInfoVec recv_blocks = bundle.recv_blocks();
		    
		    // "special case for first time we get called, since we need to
		    // create a BlockInfo struct for the primary block without knowing
		    // the typecode or the length" [DTN2]
		    if (recv_blocks.isEmpty()) {
		        Log.d(TAG,  "consume: got first block... " +
		                    "creating primary block info");
		        recv_blocks.append_block(find_processor(bundle_block_type_t.PRIMARY_BLOCK), null);
		    }

		    // "loop as long as there is data left to process" [DTN2]
		    while (len != 0) {
		        Log.d(TAG, String.format("consume: %d bytes left to process", len));
		        BlockInfo info = recv_blocks.back();

		        // "if the last received block is complete, create a new one
		        // and push it onto the vector. at this stage we consume all
		        // blocks, even if there's no BlockProcessor that understands
		        // how to parse it" [DTN2]
		        if (info.complete()) {
		        	data.mark();
		        	byte bundle_block_type_byte = data.get();
		        	bundle_block_type_t type = bundle_block_type_t.get(bundle_block_type_byte);
		        	data.reset();
		            info = recv_blocks.append_block(find_processor( type) , null);
		            Log.d(TAG, String.format("consume: previous block complete, " +
		                        "created new BlockInfo type %s",
		                        info.owner().block_type()));
		        }
		        
		        // "now we know that the block isn't complete, so we tell it to
		        // consume a chunk of data" [DTN2]
		        Log.d(TAG, String.format("consume: block processor %s type %s incomplete, " +
		                    "calling consume (%d bytes already buffered)",
		                    info.owner().block_type(),
		                    info.type(),
		                    info.contents().position()));
		        
		        int cc = info.owner().consume(bundle, info, data, len);
		        if (cc < 0) {
		            Log.e(TAG, String.format("consume: protocol error handling block %s",
		                      info.type()));
		            return -1;
		        }
		        
		        // "decrement the amount that was just handled from the overall
		        // total. verify that the block was either completed or
		        // consumed all the data that was passed in." [DTN2]
		        len  -= cc;
		        data.position(data.position() + cc);

		        Log.d(TAG, String.format("consume: consumed %d bytes of block type %s (%s)",
		                    cc, info.type(),
		                    info.complete() ? "complete" : "not complete"));

		        if (info.complete()) {
		            // check if we're done with the bundle
		            if ( (info.flags() & block_flag_t.BLOCK_FLAG_LAST_BLOCK.getCode()) > 0) {
		                last[0] = true;
		                break;
		            }
		                
		        } else {
		            assert(len == 0);
		        }
		    }
		    
		    Log.d(TAG, String.format("bundle id %d consume completed, %d/%d bytes consumed %s",
		                bundle.bundleid(),
		    		    origlen - len, 
		                origlen, 
		                last[0] ? "(completed bundle)" : ""
		                	));
		    
		    data.position( old_position);
		    return origlen - len;
	}

	/**
	 * "Bundle Status Report "Reason Code" flags" [DTN2]
	 */
	public static enum status_report_reason_t {
		REASON_NO_ADDTL_INFO("no additional information",(byte)0x00), 
		REASON_LIFETIME_EXPIRED("lifetime expired", (byte)0x01), 
		REASON_FORWARDED_UNIDIR_LINK("forwarded over unidirectional link", (byte)0x02), 
		REASON_TRANSMISSION_CANCELLED("transmission cancelled", (byte)0x03),
		REASON_DEPLETED_STORAGE("depleted storage", (byte)0x04),
		REASON_ENDPOINT_ID_UNINTELLIGIBLE("endpoint id unintelligible", (byte)0x05),
		REASON_NO_ROUTE_TO_DEST("no known route to destination", (byte)0x06), 
		REASON_NO_TIMELY_CONTACT("no timely contact", (byte)0x07), 
		REASON_BLOCK_UNINTELLIGIBLE("block unintelligible", (byte)0x08), 

		;

		private static final Map<Byte, status_report_reason_t> lookupCode = new HashMap<Byte, status_report_reason_t>();
		private static final Map<String, status_report_reason_t> lookupCaption = new HashMap<String, status_report_reason_t>();

		
		
		static {
			for (status_report_reason_t s : EnumSet
					.allOf(status_report_reason_t.class))
				{ 
				  lookupCode.put(s.getCode(), s);
				  lookupCaption.put(s.getCaption(), s);
				}
		}

		private byte code;
		private String caption;
		private status_report_reason_t(String caption, byte code) {
			this.code = code;
			this.caption = caption;
		}

		public byte getCode() {
			return code;
		}

		public String getCaption() {
			return caption;
		}
		public static status_report_reason_t get(byte code) {
			return lookupCode.get(code);
		}
	}

	/**
	 * "Loop through the bundle's received block list to validate each entry." [DTN2]
	 * 
	 * @return "true if the bundle is valid, false if it should be deleted." [DTN2]
	 */
	public static boolean validate(Bundle bundle,
			status_report_reason_t[] reception_reason,
			status_report_reason_t[] deletion_reason) {
		int primary_blocks = 0, payload_blocks = 0;
	    BlockInfoVec recv_blocks = bundle.recv_blocks();
	 
	    // "a bundle must include at least two blocks (primary and payload)" [DTN2]
	    if (recv_blocks.size() < 2) {
	        Log.e(TAG, "bundle fails to contain at least two blocks");
	        deletion_reason[0] = BundleProtocol.status_report_reason_t.REASON_BLOCK_UNINTELLIGIBLE;
	        return false;
	    }

	    // "the first block of a bundle must be a primary block" [DTN2]
	    if (recv_blocks.front().type() != BundleProtocol.bundle_block_type_t.PRIMARY_BLOCK) {
	        Log.e(TAG, "bundle fails to contain a primary block");
	        deletion_reason[0] = BundleProtocol.status_report_reason_t.REASON_BLOCK_UNINTELLIGIBLE;
	        return false;
	    }

	    // "validate each individual block" [DTN2]
	    
	    int last_block_index = recv_blocks.size() - 1;
	    for(int i=0; i < recv_blocks.size(); i++)
	    {
	    	BlockInfo  current_block = recv_blocks.get(i);
	    	
	    	 // "a block may not have enough data for the preamble" [DTN2]
	        if (current_block.data_offset() == 0) {
	            // "either the block is not the last one and something went
	            // badly wrong, or it is the last block present" [DTN2]
	            if (i != last_block_index) {
	                Log.e(TAG, "bundle block too short for the preamble");
	                deletion_reason[0] = BundleProtocol.status_report_reason_t.REASON_BLOCK_UNINTELLIGIBLE;
	                return false;
	            }
	            // "this is the last block, so drop it" [DTN2]
	            Log.d(TAG, "forgetting preamble-starved last block");
	            recv_blocks.remove(current_block);
	            if (recv_blocks.size() < 2) {
	                Log.e(TAG, "bundle fails to contain at least two blocks");
	                deletion_reason[0] = BundleProtocol.status_report_reason_t.REASON_BLOCK_UNINTELLIGIBLE;
	                return false;
	            }
	            // "continue with the tests; results may have changed now that
	            // a different block is last" [DTN2]
	            return false;
	        }
	        else {
	            if (current_block.type() == BundleProtocol.bundle_block_type_t.PRIMARY_BLOCK) {
	                primary_blocks++;
	            }

	            if (current_block.type() == BundleProtocol.bundle_block_type_t.PAYLOAD_BLOCK) {
	                payload_blocks++;
	            }
	        }

	        if (!current_block.owner().validate(bundle, recv_blocks, current_block, 
	                                reception_reason, deletion_reason)) {
	            return false;
	        }

	        // "a bundle's last block must be flagged as such,
	        // and all other blocks should not be flagged" [DTN2]
	        if (i == last_block_index) {
	            if (!current_block.last_block()) {
	                if (!bundle.fragmented_incoming()) {
	                    Log.e(TAG, "bundle's last block not flagged");
	                    deletion_reason[0] = BundleProtocol.status_report_reason_t.REASON_BLOCK_UNINTELLIGIBLE;
	                    return false;
	                }
	                else {
	                    Log.d(TAG, "bundle's last block not flagged, but " +
	                                     "it is a reactive fragment");
	                }
	            }
	        } else {
	            if (current_block.last_block()) {
	                Log.e(TAG, "bundle block incorrectly flagged as last");
	                deletion_reason[0] = BundleProtocol.status_report_reason_t.REASON_BLOCK_UNINTELLIGIBLE;
	                return false;
	            }
	        }  	
	    	
	    }
	    
	    // "a bundle must contain one, and only one, primary block" [DTN2]
	    if (primary_blocks != 1) {
	        Log.e(TAG, String.format("bundle contains %d primary blocks", primary_blocks));
	        deletion_reason[0] = BundleProtocol.status_report_reason_t.REASON_BLOCK_UNINTELLIGIBLE;
	        return false;
	    }
	           
	    // "a bundle must not contain more than one payload block" [DTN2]
	    if (payload_blocks > 1) {
	        Log.e(TAG, String.format("bundle contains %d payload blocks", payload_blocks));
	        deletion_reason[0] = BundleProtocol.status_report_reason_t.REASON_BLOCK_UNINTELLIGIBLE;
	        return false;
	    }

	

	    return true;
	}
 

	/**
	 * "The current version of the bundling protocol." [DTN2]
	 */
	static final int CURRENT_VERSION = 0x06;

	static final int PREAMBLE_FIXED_LENGTH = 1;

	/**
	 * "Valid type codes for bundle blocks." [DTN2] 
	 */
	public static enum bundle_block_type_t {
		PRIMARY_BLOCK((byte) 0), // /< "INTERNAL ONLY -- NOT IN SPEC" [DTN2]
		PAYLOAD_BLOCK((byte) 1),
		UNKNOWN_BLOCK((byte) 90) // /< "INTERNAL ONLY -- NOT IN SPEC" [DTN2]
		;

		private static final Map<Byte, bundle_block_type_t> lookup = new HashMap<Byte, bundle_block_type_t>();

		static {
			for (bundle_block_type_t s : EnumSet
					.allOf(bundle_block_type_t.class))
				lookup.put(s.getCode(), s);
		}

		private byte code;

		private bundle_block_type_t(byte code) {
			this.code = code;
		}

		public byte getCode() {
			return code;
		}

		public static bundle_block_type_t get(byte code) {
			return lookup.get(code);
		}
		
		
	}

	/**
	 * "Values for block processing flags that appear in all blocks except the
	 * primary block." [DTN2]
	 */
	public static enum block_flag_t {
		BLOCK_FLAG_REPLICATE((1 << 0)), BLOCK_FLAG_REPORT_ONERROR((1 << 1)), BLOCK_FLAG_DISCARD_BUNDLE_ONERROR(
				(1 << 2)), BLOCK_FLAG_LAST_BLOCK((1 << 3)), BLOCK_FLAG_DISCARD_BLOCK_ONERROR(
				(1 << 4)), BLOCK_FLAG_FORWARDED_UNPROCESSED((1 << 5)), BLOCK_FLAG_EID_REFS(
				(1 << 6));

		private static final Map<Integer, block_flag_t> lookup = new HashMap<Integer, block_flag_t>();

		static {
			for (block_flag_t s : EnumSet.allOf(block_flag_t.class))
				lookup.put(s.getCode(), s);
		}

		private int code;

		private block_flag_t(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static block_flag_t get(int code) {
			return lookup.get(code);
		}
	}

	/**
	 * "The basic block preamble that's common to all blocks (including the
	 * payload block but not the primary block)." [DTN2]
	 */

	class BlockPreamble {
		byte type;
		byte flags;
		byte length; // SDNV
	};

	/**
	 * "Administrative Record Type Codes" [DTN2]
	 */
	public static enum admin_record_type_t {
		ADMIN_STATUS_REPORT((byte) 0x01), ADMIN_CUSTODY_SIGNAL((byte) 0x02), ADMIN_ANNOUNCE(
				(byte) 0x05) // "NOT IN BUNDLE SPEC" [DTN2]
		;

		private static final Map<Byte, admin_record_type_t> lookup = new HashMap<Byte, admin_record_type_t>();

		static {
			for (admin_record_type_t s : EnumSet
					.allOf(admin_record_type_t.class))
				lookup.put(s.getCode(), s);
		}

		private byte code;

		private admin_record_type_t(byte code) {
			this.code = code;
		}

		public byte getCode() {
			return code;
		}

		public static admin_record_type_t get(byte code) {
			return lookup.get(code);
		}
	}

	/**
	 * "Administrative Record Flags." [DTN2]
	 */
	public static enum admin_record_flags_t {
		ADMIN_IS_FRAGMENT((byte) 0x01);

		private static final Map<Byte, admin_record_flags_t> lookup = new HashMap<Byte, admin_record_flags_t>();

		static {
			for (admin_record_flags_t s : EnumSet
					.allOf(admin_record_flags_t.class))
				lookup.put(s.getCode(), s);
		}

		private byte code;

		private admin_record_flags_t(byte code) {
			this.code = code;
		}

		public byte getCode() {
			return code;
		}

		public static admin_record_flags_t get(byte code) {
			return lookup.get(code);
		}
	}

	

	/**
	 * Custody Signal Reason Codes
	 */
	public static enum custody_signal_reason_t {
		CUSTODY_NO_ADDTL_INFO("no additional info",              (byte) 0x00),
		CUSTODY_REDUNDANT_RECEPTION("redundant reception",       (byte) 0x03), 
		CUSTODY_DEPLETED_STORAGE("depleted storage",             (byte) 0x04), 
		CUSTODY_ENDPOINT_ID_UNINTELLIGIBLE("eid unintelligible", (byte) 0x05),
		CUSTODY_NO_ROUTE_TO_DEST("no route to dest", (byte) 0x06), 
		CUSTODY_NO_TIMELY_CONTACT("no timely contact", (byte) 0x07),
		CUSTODY_BLOCK_UNINTELLIGIBLE("block unintelligible", (byte) 0x08);

		private static final Map<Byte, custody_signal_reason_t> lookup = new HashMap<Byte, custody_signal_reason_t>();
		private static final Map<String, custody_signal_reason_t> caption_map = new HashMap<String, custody_signal_reason_t>();

		static {
			for (custody_signal_reason_t s : EnumSet
					.allOf(custody_signal_reason_t.class))
				{ 
				  lookup.put(s.getCode(), s);
				  caption_map.put(s.getCaption(), s);
				}
			
		}

		private byte code_;
		private String caption_;
		private custody_signal_reason_t(String caption, byte code) {
			this.caption_ = caption;
			this.code_ = code;
		}

		public byte getCode() {
			return code_;
		}
		public String getCaption() {
			return caption_;
		}

		public static custody_signal_reason_t get(byte code) {
			return lookup.get(code);
		}
		
		
		
	}

	/**
	 * "Assuming the given bundle is an administrative bundle, extract the admin
	 * bundle type code from the bundle's payload." [DTN2]
	 * 
	 * @return true if successful
	 */
	public static boolean get_admin_type(final Bundle bundle,
			admin_record_type_t[] type) {
		 if (! bundle.is_admin()) {
		        return false;
		    }
		    
		    IByteBuffer buf = new SerializableByteBuffer(16);
		    bundle.payload().read_data(0, 1, buf);

		    admin_record_type_t admin_record_type = admin_record_type_t.get((byte)(buf.get(0) >> 4));
		    switch (admin_record_type)
		    {
		        case ADMIN_STATUS_REPORT:
		        case ADMIN_CUSTODY_SIGNAL:
		        case ADMIN_ANNOUNCE:
		    	
		        	type[0] = admin_record_type;
		        	return true;

		    default:
		        return false; // unknown type
		    }

		    
		    
	}

	/**
	 * List of registered processors to this BundleProtocol class
	 */
	private static List< BlockProcessor> processors_ = new List<BlockProcessor>();

};
