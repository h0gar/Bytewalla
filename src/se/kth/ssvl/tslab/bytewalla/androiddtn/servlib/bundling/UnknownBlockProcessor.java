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

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BlockInfo.list_owner_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol.block_flag_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol.bundle_block_type_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol.status_report_reason_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;

/**
 * Block processor implementation for any unknown bundle blocks.
 * 
 * @author Sharjeel Ahmed (sharjeel@kth.se)
 * 
 */

public class UnknownBlockProcessor extends BlockProcessor{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8927605141320447882L;
	/**
	 *  Singleton instance Implementation of the BundleStore
	 */
	private static UnknownBlockProcessor instance_ = null;

    /**
     * Singleton Implementation to get the instance of UnknownBlockProcessor
     * @return an singleton instance of UnknownBlockProcessor
     */    	
	public static UnknownBlockProcessor getInstance() {
      if(instance_ == null) {
         instance_ = new UnknownBlockProcessor();
      }
      return instance_;
   }
	
	/**
	 * Constructor 
	 */
    public  UnknownBlockProcessor() {

      	super(bundle_block_type_t.UNKNOWN_BLOCK);
    }

    /**
     * Virtual from BlockProcessor to prepare the bundle
     * @param bundle Bundle to prepare
     * @param xmit_blocks Empty xmit_blocks of the blundle
     * @param source Source endpoint id
     * @param link Link of the bundle
     * @param list Owner type
     * @return  Return success message on success
     */
    @Override
	public int prepare(final Bundle    bundle,
                BlockInfoVec    xmit_blocks,
                final BlockInfo source,
                final Link   link,
                list_owner_t     list){

        assert(source != null)
        :"UnknowBlockProcess: prepare() source is null";
        
        assert(source.owner() == this)
        :"UnknowBlockProcess: prepare() source.owner is not this";

        if ((source.flags() & block_flag_t.BLOCK_FLAG_DISCARD_BLOCK_ONERROR.getCode())>0) {
            return BP_FAIL;
        }

      
        return super.prepare(bundle, xmit_blocks, source, link, list);
    	
    }
    
    /**
     * Generate bundle block.
	 * @param bundle Bundle to generate
	 * @param xmit_blocks xmit_blocks to get the dictionary for generating
	 * @param block Block to write the data
	 * @param link Link type
	 * @param last If its a last block
	 * @return If successfully generated then return Success message.  	 
     */
    @Override
	public int generate(final Bundle  bundle,
                 BlockInfoVec  xmit_blocks,
                 BlockInfo     block,
                 final Link link,
                 boolean last){

        final BlockInfo source = block.source();
        assert(source != null)
        :"UnKnownBlockProcess: generate, source is null";
        assert(source.owner() == this)
        :"UnKnownBlockProcess: generate, this==owner";

        assert((source.flags() &
        		block_flag_t.BLOCK_FLAG_DISCARD_BUNDLE_ONERROR.getCode()) == 0)
        		:"UnKnownBlockProcess: Flag set BLOCK_FLAG_DISCARD_BUNDLE_ONERROR";
        assert((source.flags() &
        		block_flag_t.BLOCK_FLAG_DISCARD_BLOCK_ONERROR.getCode()) == 0)
        		:"UnKnownBlockProcess: Flag set BLOCK_FLAG_DISCARD_BLOCK_ONERROR";
        
        assert(source.contents().capacity() != 0)
        :"UnKnownBlockProcess: no data";
        assert(source.data_offset() != 0)
        :"UnKnownBlockProcess: Data offset 0";
        
        int flags = source.flags();
        
        if (last) {
            flags |= block_flag_t.BLOCK_FLAG_LAST_BLOCK.getCode();
        } else {
            flags &= block_flag_t.BLOCK_FLAG_LAST_BLOCK.getCode();
        }
        flags |= block_flag_t.BLOCK_FLAG_FORWARDED_UNPROCESSED.getCode();
        
        block.set_eid_list(source.eid_list());

        generate_preamble(xmit_blocks, block, source.type(), flags,
                          source.data_length());
        
        assert(block.data_length() == source.data_length())
        :"UnKnownBlockProcess: block and source length not equal";
        
        IByteBuffer contents = block.writable_contents();
        
        contents.position(block.data_offset());
        source.contents().position(source.data_offset());
        
        return BP_SUCCESS;
    	
    }

    /**
     * Check the validity of the the bundle
     * @param bundle Bundle to check its generic validity
     * @param block_list List of Blocks
     * @param block Block to check if it's valid or not
     * @param reception_reason If block is not valid then reception reason
     * @param deletion_reason If block is not balid then deletion reason
     * @return True if the block is valid else false
     */
    @Override
	public boolean validate(final Bundle  bundle,
                  BlockInfoVec           block_list,
                  BlockInfo              block,
                  status_report_reason_t[] reception_reason,
                  status_report_reason_t[] deletion_reason){
    	
        // check for generic block errors
        if (!super.validate(bundle, block_list, block,
                                      reception_reason, deletion_reason)) {
            return false;
        }

        // extension blocks of unknown type are considered to be "invalid"
        if ((block.flags() & block_flag_t.BLOCK_FLAG_REPORT_ONERROR.getCode())>0) {
            reception_reason[0] = status_report_reason_t.REASON_BLOCK_UNINTELLIGIBLE;
        }

        if ((block.flags() & block_flag_t.BLOCK_FLAG_DISCARD_BUNDLE_ONERROR.getCode())>0) {
            deletion_reason[0] = status_report_reason_t.REASON_BLOCK_UNINTELLIGIBLE;
            return false;
        }

        return true;
    	
    }
}
