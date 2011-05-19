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
import java.util.Iterator;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol.bundle_block_type_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.common.ServlibEventData;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.common.ServlibEventHandler;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointIDVector;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.BufferHelper;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.SerializableByteBuffer;
import android.util.Log;

/**
 * Base Class for the Block Processing unit. This unit mainly converting from Binary to Java Object and vice versa.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public class BlockProcessor implements Serializable {

	/**
	 * Serial version UID 
	 */
	private static final long serialVersionUID = -2209185478203288495L;
	
	/**
	 * Number to show Bundle Protocol Success 
	 */
	public static int BP_SUCCESS = 0;
	
	/**
	 * Number to show Bundle Protocol Failure 
	 */
	public static int BP_FAIL = -1;
	
	/**
	 * 
	 */
	private static String TAG = "BlockProcessor";
	
	/**
	 * Class to remain consistent with the code from DTN2
	 */
	public static class OpaqueContext {
		
	}

	/**
	 * Handler class to represent the action for process function
	 */
	public static class process_func_event_data extends ServlibEventData {
		
		/**
		 * Getter for the Bundle to process
		 * @return
		 */
		public Bundle bundle() {
			return bundle_;
		}

		/**
		 * Setter for the Bundle to process
		 * @param bundle
		 */
		public void set_bundle(Bundle bundle) {
			this.bundle_ = bundle;
		}

		/**
		 * Getter for the caller Block
		 * @return
		 */
		public BlockInfo caller_block() {
			return caller_block_;
		}

		/**
		 * Setter for the caller Block
		 * @param callerBlock
		 */
		public void set_caller_block(BlockInfo callerBlock) {
			caller_block_ = callerBlock;
		}

		/**
		 * Getter for the Target Block
		 * @return
		 */
		public BlockInfo target_block() {
			return target_block_;
		}

		/**
		 * Setter for the target Block
		 * @param targetBlock
		 */
		public void set_target_block(BlockInfo targetBlock) {
			target_block_ = targetBlock;
		}

		/**
		 * Getter for the Buffer
		 * @return
		 */
		public IByteBuffer buf() {
			return buf_;
		}

		/**
		 * Setter for the Buffer
		 * @param buf
		 */
		public void set_buf(IByteBuffer buf) {
			this.buf_ = buf;
		}

		/**
		 * Getter for the len
		 * @return
		 */
		public int len() {
			return len_;
		}

		/**
		 * Setter for the len
		 * @param len
		 */
		public void set_len(int len) {
			this.len_ = len;
		}

		/**
		 * Getter for the OpagueContext
		 * @return
		 */
		public OpaqueContext context() {
			return context_;
		}

		/**
		 * Setter for the OpagueContext
		 * @param context
		 */
		public void setContext(OpaqueContext context) {
			this.context_ = context;
		}

		private Bundle bundle_;
		private BlockInfo caller_block_;
		private BlockInfo target_block_;
		private IByteBuffer buf_;
		private int len_;
		private OpaqueContext context_;
	}

	/**
	 * Interface class for processing function to be executed
	 */
	public static interface process_func extends ServlibEventHandler {

	}
	
	/**
	 * Mutate function to be executed
	 */
	public static class mutate_func_event_data extends ServlibEventData {
		public Bundle bundle() {
			return bundle_;
		}

		public void set_bundle(Bundle bundle) {
			this.bundle_ = bundle;
		}

		public BlockInfo caller_block() {
			return caller_block_;
		}

		public void set_caller_block(BlockInfo callerBlock) {
			caller_block_ = callerBlock;
		}

		public BlockInfo target_block() {
			return target_block_;
		}

		public void set_target_block(BlockInfo targetBlock) {
			target_block_ = targetBlock;
		}

		public IByteBuffer buf() {
			return buf_;
		}

		public void set_buf(IByteBuffer buf) {
			this.buf_ = buf;
		}

		public int len() {
			return len_;
		}

		public void set_len(int len) {
			this.len_ = len;
		}

		public OpaqueContext context() {
			return context_;
		}

		public void set_context(OpaqueContext context) {
			this.context_ = context;
		}

		private Bundle bundle_;
		private BlockInfo caller_block_;
		private BlockInfo target_block_;
		private IByteBuffer buf_;
		private int len_;
		private OpaqueContext context_;
	}

	/**
	 * Typedef for a mutate function pointer.
	 */

	public static interface mutate_func extends ServlibEventHandler {

	}

	/**
	 * Constructor for particular type of block
	 */
	public BlockProcessor(bundle_block_type_t block_type) {
		block_type_ = block_type;
	}


	public BundleProtocol.bundle_block_type_t block_type() {
		return block_type_;
	}
	/**
	 * "First callback for parsing blocks that is expected to append a chunk of
	 * the given data to the given block. When the block is completely received,
	 * this should also parse the block into any fields in the bundle class.
	 * 
	 * The base class implementation parses the block preamble fields to find
	 * the length of the block and copies the preamble and the data in the
	 * block's contents buffer.
	 * 
	 * This and all derived implementations must be able to handle a block that
	 * is received in chunks, including cases where the preamble is split into
	 * multiple chunks." [DTN2]
	 * 
	 * @return "the amount of data consumed or -1 on error" [DTN2]
	 */
	public int consume(Bundle bundle,  BlockInfo block,  IByteBuffer buf,  int len)
     {
        int consumed = 0;

        assert(! block.complete()):"BlockProcessor:consume, block.complete()";
        BlockInfoVec recv_blocks = bundle.recv_blocks();

         
        // "Check if we still need to consume the preamble by checking if
        // the data_offset_ field is initialized in the block info
        // structure." [DTN2]
        if (block.data_offset() == 0) 
        {
        	int[] processing_flags = new int[1];
            int cc = consume_preamble(recv_blocks, block, buf, len, processing_flags);
            if (cc == -1) {
                return -1;
            }

            len -= cc;
            consumed += cc;
        }


        // "If the preamble is complete (i.e., data offset is non-zero) and
        // the block's data length is zero, then mark the block as complete" [DTN2]
        if (block.data_offset() != 0 && block.data_length() == 0) {
            block.set_complete(true);
        }
        
        // "If there's nothing left to do, we can bail for now." [DTN2]
        if (len == 0)
            return consumed;

        
        
        
        // "Now make sure there's still something left to do for the block,
        // otherwise it should have been marked as complete" [DTN2]
        assert(block.data_length() == 0 ||
               block.full_length() > block.contents().position());
        
        int rcvd      = block.contents().position();
        int remainder = block.full_length() - rcvd;
        int tocopy;
        if (len >= remainder) {
            block.set_complete(true);
            tocopy = remainder;
        } else {
            tocopy = len;
        }
        
        // "copy in the data" [DTN2]
        
        BufferHelper.copy_data(block.writable_contents(),
        		block.writable_contents().position(),
        		buf, 
        		buf.position(),
        		tocopy);
        
        len -= tocopy;
        consumed += tocopy;

        Log.d(TAG, String.format( "BlockProcessor type %s " +
                    "consumed %d/%d , result is (%s)",
                    block_type(), 
                    consumed,
                    block.full_length(), 
                    block.type(),
                    block.complete() ? "complete" : "not complete"
                    	));
          
        return consumed;
     }

	/**
	 * "Perform any needed action in the case where a block/bundle has been
	 * reloaded from store" [DTN2]
	 */
	public boolean reload_post_process(Bundle bundle, BlockInfoVec block_list,
			BlockInfo block) {
		block.set_reloaded(false);
		return false;
	}

	/**
	 * "Validate the block. This is called after all blocks in the bundle have
	 * been fully received." [DTN2]
	 * 
	 * @return true "if the block passes validation" [DTN2]
	 */
	public boolean validate(final Bundle           bundle,
                          BlockInfoVec           block_list,
                          BlockInfo              block,
                          BundleProtocol.status_report_reason_t[] reception_reason,
                          BundleProtocol.status_report_reason_t[] deletion_reason)
    {
        // "An administrative bundle MUST NOT contain an extension block
        // with a processing flag that requires a reception status report
        // be transmitted in the case of an error" [DTN2]
        if (bundle.is_admin() &&
            block.type() != BundleProtocol.bundle_block_type_t.PRIMARY_BLOCK &&
            (block.flags() & BundleProtocol.block_flag_t.BLOCK_FLAG_REPORT_ONERROR.getCode()) > 0) {
            Log.e(TAG, String.format("invalid block flag %s for received admin bundle",
                      BundleProtocol.block_flag_t.BLOCK_FLAG_REPORT_ONERROR));
            deletion_reason[0] = BundleProtocol.status_report_reason_t.REASON_BLOCK_UNINTELLIGIBLE;
            return false;
        }
            
        return true;
    }

	/**
	 * "First callback to generate blocks for the output pass. The function is
	 * expected to initialize an appropriate BlockInfo structure in the given
	 * BlockInfoVec. The base class simply initializes an empty BlockInfo with the appropriate
	 * owner_ pointer." [DTN2]
	 */
	public int prepare(final Bundle    bundle,
                        BlockInfoVec    xmit_blocks,
                        final BlockInfo source,
                        final Link   link,
                        BlockInfo.list_owner_t     list)
    {
        
        // "Received blocks are added to the end of the list (which
        // maintains the order they arrived in) but blocks from any other
        // source are added after the primary block (that is, before the
        // payload and the received blocks). This places them "outside"
        // the original blocks." [DTN2]
        if (list == BlockInfo.list_owner_t.LIST_RECEIVED) {
            xmit_blocks.append_block(this, source);
        }
        else {
            assert(xmit_blocks.get(0).type() == BundleProtocol.bundle_block_type_t.PRIMARY_BLOCK):"BlockProcessor,prepare: The first block of xmit_blocks was not Primary Block";
            xmit_blocks.add(1, new BlockInfo(this, source));
        }
        return BP_SUCCESS;
    }

	/**
	 * "Second callback for transmitting a bundle. This pass should generate any
	 * data for the block that does not depend on other blocks' contents. It
	 * MUST add any EID references it needs by calling block.add_eid(), then
	 * call generate_preamble(), which will add the EIDs to the primary block's
	 * dictionary and write their offsets to this block's preamble." [DTN2]
	 */
	public int generate(final Bundle bundle, BlockInfoVec xmit_blocks,
			BlockInfo block, final Link link, boolean last)
	{
		return -1;
	}

	/**
	 * "Third callback for transmitting a bundle. This pass should generate any
	 * data (such as security signatures) for the block that may depend on other
	 * blocks' contents.* The base class implementation does nothing.
	 * We pass xmit_blocks explicitly to indicate that ALL blocks might be
	 * changed by finalize, typically by being encrypted. Parameters such as
	 * length might also change due to padding and encapsulation." [DTN2]
	 */
	public int finalize(final Bundle  bundle, 
                         BlockInfoVec  xmit_blocks, 
                         BlockInfo     block, 
                         final Link link)
    {
    	        
    	if (bundle.is_admin() && block.type() != BundleProtocol.bundle_block_type_t.PRIMARY_BLOCK) 
    	{
    	    assert ( (block.flags() & BundleProtocol.block_flag_t.BLOCK_FLAG_REPORT_ONERROR.getCode()) == 0 )
    	    		: "BlockProcessor:finalize, report on error is not set";
    	    
    	}
    	    return BP_SUCCESS;
    }

	/**
	 * "Accessor to virtualize read-only processing contents of the block in
	 * various ways. This is overloaded by the payload since the contents are
	 * not actually stored in the BlockInfo contents_ buffer but rather are
	 * on-disk.
	 * 
	 * Processing can be anything the calling routine wishes, such as digest of
	 * the block, encryption, decryption etc. This routine is permitted to
	 * process the data in several calls to the target "func" routine as long as
	 * the data is processed in order and exactly once.
	 * 
	 * Note that the supplied offset + length must be less than or equal to the
	 * total length of the block." [DTN2]
	 */
	public void process(process_func    func,
                         final Bundle    bundle,
                         final BlockInfo caller_block,
                         final BlockInfo target_block,
                         int           offset,            
                         int           len,
                         OpaqueContext   context)
    {
    	              
    	    
    	    assert(offset < target_block.contents().position()) : "BlockProcessor,process: Target Block postion is less than the offset";
    	    assert(target_block.contents().position() >= offset + len):"BlockProcessor,process: the position is longer than the to be processed data";
    	    
    	    // "convert the offset to a pointer in the target block" [DTN2]
    	    IByteBuffer buf = target_block.contents();
    	    buf.position( buf.position()+ offset);
    	    
    	    // "call the processing function to do the work" [DTN2]
    	    
    	    process_func_event_data data = new process_func_event_data();
    	    data.set_bundle(bundle);
    	    data.set_caller_block(caller_block);
    	    data.set_target_block(target_block);
    	    data.set_buf(buf);
    	    data.set_len(len);
    	    data.setContext(context);
    	    func.action(data);
    }

	/**
	 * "Similar to process() but for potentially mutating processing functions.
	 * The function returns true iff it modified the target_block." [DTN2]
	 */
	public boolean mutate(mutate_func     func,
                        Bundle          bundle,
                        final BlockInfo caller_block,
                        BlockInfo       target_block,
                        int           offset,
                        int           len,
                        OpaqueContext   context)
    {
    	
        
        
        assert(offset < target_block.contents().position()) : "BlockProcessor,mutate: Target Block position is less than the offset";
	    assert(target_block.contents().position() >= offset + len):"BlockProcessor,mutate: the position is longer than the to be processed data";
	  
        
        // "convert the offset to a pointer in the target block" [DTN2]
        IByteBuffer buf = target_block.contents();
        buf.position( buf.position()+ offset);
        
        // "call the mutating function to do the work" [DTN2]
        mutate_func_event_data data = new mutate_func_event_data();
	    data.set_bundle(bundle);
	    data.set_caller_block(caller_block);
	    data.set_target_block(target_block);
	    data.set_buf(buf);
	    data.set_len(len);
	    data.set_context(context);
	    return func.action(data);
    }

	/**
	 * Method to copy the data from the block info into the output IByteBuffer
	 * This and all its subclass should be able to produce with multiple chunks of data.
	 * 
	 */
	public void produce(final Bundle    bundle,
                         final BlockInfo block,
                         IByteBuffer    buf,
                         int           offset,
                         int           len)
    {
    	    assert(offset < block.contents().position()) : "BlockProcessor,produce: Target Block position is less than the offset";
    	    assert(block.contents().position() >= offset + len): "BlockProcessor,produce: the position is longer than the to be processed data";
    	    
    	    
    	    BufferHelper.copy_data(buf, buf.position(), block.contents(), offset, len);
    	    
    	    
    }

	/**
	 * "General hook to set up a block with the given contents. Used for testing
	 * generic extension blocks." [DTN2]
	 */
	public void init_block(BlockInfo block, BlockInfoVec block_list,
			BundleProtocol.bundle_block_type_t type, int flags, IByteBuffer bp,
			int len) {
		assert (block.owner() != null): "BlockProcessor:init_block, block.owner() is null";
		
		generate_preamble(block_list, block, type, flags, len);
		assert (block.data_offset() != 0);
		
		IByteBuffer reserved_buffer = BufferHelper.reserve(block.writable_contents(), block.full_length());
		block.set_contents( reserved_buffer );
		
		IByteBuffer writable_block_buffer = block.writable_contents();
		BufferHelper.copy_data(writable_block_buffer,
				                block.data_offset(), 
				                bp, 
				                0, 
				                len);

	}

	
	
	/**
	 * "Consume a block preamble consisting of type, flags(SDNV), EID-list
	 * (composite field of SDNVs) and length(SDNV). This method does not apply
	 * to the primary block, but is suitable for payload and all extensions." [DTN2]
	 */
	protected int consume_preamble(BlockInfoVec recv_blocks, BlockInfo block,
			IByteBuffer buf, int len, int[] processing_flags) 
	{
		  
		    int sdnv_len;
		    assert(! block.complete()):"BlockProcessor:consume_preamble, block.complete";
		    assert(block.data_offset() == 0):"BlockProcessor:consume_preamble, block data_offset known when it shouldn't be known";

		    if ( block.contents().remaining() == 0 ) {
		        block.set_contents(
		        			BufferHelper.reserve(
		        						block.writable_contents(), 
		        						block.contents().position() + 64
		        						)
		        						
		        );
		    }    
		    
		    int max_preamble  = block.contents().capacity();
		    int prev_consumed = block.contents().position();
		    int tocopy        = Math.min(len, max_preamble - prev_consumed);
		    
		    assert(max_preamble > prev_consumed);
		    IByteBuffer contents = block.writable_contents();
		    assert(contents.remaining() >= tocopy);
		    
		    BufferHelper.copy_data(contents, 
		    		contents.position(), 
		    		buf, 
		    		buf.position(), 
		    		tocopy);
		    
		    // Because copy_data will not move the buffer position, we have to move it here
		    contents.position(contents.position() + tocopy);

		    // "Make sure we have at least one byte of sdnv before trying to
		    // parse it." [DTN2]
		    if (contents.position() <= BundleProtocol.PREAMBLE_FIXED_LENGTH) {
		        assert(tocopy == len);
		        return len;
		    }
		    
		    int buf_offset = BundleProtocol.PREAMBLE_FIXED_LENGTH;
		    int[] flags  = new int[1];
		    flags[0]  = -1;
		    // "Now we try decoding the sdnv that contains the block processing
		    // flags. If we can't, then we have a partial preamble, so we can
		    // assert that the whole incoming buffer was consumed." [DTN2]
		    
		    sdnv_len = BufferHelper.try_consume_SDNV(contents, buf_offset, flags);
		    if (sdnv_len == -1) {
		    assert(tocopy == len);
		    return len;
		    }
		    // from now on flags value should be ready
		    processing_flags[0] = flags[0];
		    
		    buf_offset += sdnv_len;
		    
		    // point at the local dictionary
		    Dictionary dict = recv_blocks.dict();

		    
		    int[] eid_ref_count = new int[1];
		    int[] scheme_offset  = new int[1];
		    int[] ssp_offset  = new int[1];
		    
		    
		    assert(block.eid_list().isEmpty());
		    EndpointIDVector eid_list = new EndpointIDVector();
		        
		    if ( (flags[0] & BundleProtocol.block_flag_t.BLOCK_FLAG_EID_REFS.getCode()) > 0) {
		        
		    	sdnv_len = BufferHelper.try_consume_SDNV(contents, buf_offset, eid_ref_count);
			    if (sdnv_len == -1) 
			    {
			    assert(tocopy == len);
			    return len;
			    }
			 // from now on eid_ref_count value should be ready
		        
		        buf_offset += sdnv_len;
		            
		        for ( int i = 0; i < eid_ref_count[0]; ++i ) {
		        	
		        	
		        
		            // Now we try decoding the sdnv pair with the offsets
		        	sdnv_len = BufferHelper.try_consume_SDNV(contents, buf_offset, scheme_offset);
				    if (sdnv_len == -1) {
				    assert(tocopy == len);
				    return len;
				    }
				    
		            buf_offset += sdnv_len;
		                    
		            sdnv_len = BufferHelper.try_consume_SDNV(contents, buf_offset, ssp_offset);
				    if (sdnv_len == -1) {
				    assert(tocopy == len);
				    return len;
				    }
		            buf_offset += sdnv_len;
		                
		            EndpointID eid = new EndpointID();
		            dict.extract_eid(eid, scheme_offset[0], ssp_offset[0]);
		            eid_list.add(eid);
		        }
		    }
		    
		    int[] block_len = new int[1];
		    sdnv_len = BufferHelper.try_consume_SDNV(contents, buf_offset, block_len);
		    if (sdnv_len == -1) {
		    assert(tocopy == len);
		    return len;
		    }
		    if (block_len[0] > Integer.MAX_VALUE) {
		        Log.e(TAG, String.format("overflow in SDNV value for block type %s",
		                  block.type().toString()));
		        return -1;
		    }
		    
		    buf_offset += sdnv_len;

		    // "We've successfully consumed the preamble so initialize the
		    // data_length and data_offset fields of the block and adjust the
		    // length field of the contents buffer to include only the
		    // preamble part (even though a few more bytes might be in there." [DTN2]
		    block.set_data_length(block_len[0]);
		    block.set_data_offset(buf_offset);
		    

		    block.set_eid_list(eid_list);

		    Log.d(TAG, String.format("BlockProcessor type %s " +
		                "consumed preamble %d/%d for block: " +
		                "data_offset %d data_length %d eid_ref_count %d",
		                block_type().toString(), 
		                buf_offset + prev_consumed,
		                block.full_length(),
		                block.data_offset(),
		                block.data_length(),
		                eid_ref_count[0]));
		    
		    assert(buf_offset > prev_consumed): "BlockProcessor, consume_preamble";
		    
		    
		    
		    // "Finally, be careful to return only the amount of the buffer
		    // that we needed to complete the preamble." [DTN2]
		    return buf_offset - prev_consumed;
		    
	}

	/**
	 * "Generate the standard preamble for the given block type, flags, EID-list
	 * and content length." [DTN2]
	 */
	protected void generate_preamble(BlockInfoVec xmit_blocks, BlockInfo block,
			BundleProtocol.bundle_block_type_t type, int flags, int data_length) {
		    
		
			
		// "First calculate the size require for EID List by using ptr buffer" [DTN2]
		    IByteBuffer  ptr = new SerializableByteBuffer(2000);
		
        
		    int   scheme_offset[] = new int[1];
		    int   ssp_offset[]    = new int[1];
		    
	        Dictionary dict = xmit_blocks.dict();
		    
	        int eid_count = block.eid_list().size();
		    if ( eid_count > 0 ) {
		        flags |= BundleProtocol.block_flag_t.BLOCK_FLAG_EID_REFS.getCode();
		        SDNV.encode(eid_count, ptr);
		        
		        Iterator<EndpointID> iter = block.eid_list().iterator();
		        
		        while(iter.hasNext())
		        {
		        	EndpointID eid = iter.next();
		        	dict.add_eid(eid);
	        	    dict.get_offsets(eid, scheme_offset, ssp_offset);
		            SDNV.encode(scheme_offset[0], ptr);
		            SDNV.encode(ssp_offset[0], ptr);
		    
		        	
		        }
		       
		    }
		    
		    int eid_field_len = ptr.position();    
		    
		    int flag_sdnv_len = SDNV.encoding_len(flags);
		    int length_sdnv_len = SDNV.encoding_len(data_length);
		    assert(block.contents().position() == 0);
		    // assert to make sure that buffer overflow exception will not occur
		    assert(block.contents().capacity() >= BundleProtocol.PREAMBLE_FIXED_LENGTH 
		           + flag_sdnv_len + eid_field_len + length_sdnv_len);

		    // After make sure the buffer is large enough, start writing data to it
		    
		    IByteBuffer bp = block.writable_contents();
		   
		    // write the type byte
		    bp.put(type.getCode());
		    
		    // write the encoded version of flags
		    SDNV.encode(flags, bp,flag_sdnv_len);

		    
		    // write the eid field bytes by copying from the ptr buffer
		   
		    BufferHelper.copy_data(bp,
		    		bp.position(), 
		    		ptr, 
		    		0, 
		    		eid_field_len);
		    
		    // because the copy_data will not move buffer position,
		    // we have to move it here
		    bp.position(bp.position() + eid_field_len);
		    
		    // write the data length in encoded form
		    SDNV.encode(data_length, bp, length_sdnv_len);

		    block.set_data_length(data_length);
		    int offset = BundleProtocol.PREAMBLE_FIXED_LENGTH + 
		                       flag_sdnv_len + eid_field_len + length_sdnv_len;
		    block.set_data_offset(offset);
		    block.complete_ = true;

		    Log.d(TAG, String.format("BlockProcessor type %s " +
		                "generated preamble for block type %s flags %x " +
		                "data_offset %d data_length %d eid_count %d",
		                block_type(), 
		                block.type(),
		                block.flags(),
		                block.data_offset(),
		                block.data_length(), 
		                eid_count));
		    
		 
	}

	// "The block typecode for this handler" [DTN2]
	private bundle_block_type_t block_type_;

};