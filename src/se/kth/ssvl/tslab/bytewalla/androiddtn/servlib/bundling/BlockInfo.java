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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol.bundle_block_type_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers.CLConnection;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointIDVector;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.BufferHelper;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.SerializableByteBuffer;
import android.util.Log;

/**
 * Class representing DTN protocol blocks.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class BlockInfo implements Serializable {

	/**
	 * Serial UID to support Java Serializable
	 */
	private static final long serialVersionUID = -5342675039485334323L;

	/**
	 * String TAG to support Android logging mechanism
	 */
	private final static String TAG = "BlockInfo";
	
	/**
	 * Internal byte array to store data
	 */
	private byte[] storage_array_;
	
	/**
	 * Default buffer size
	 */
	private final static int DATA_BUFFER_SIZE = CLConnection.DEFAULT_BLOCK_BUFFER_SIZE;

	/**
	 *  "Default constructor assigns the owner and optionally the 
	 *  BlockInfo source (i.e. the block as it arrived off the wire)" [DTN2]
	 * @param owner
	 * @param source
	 */
	public BlockInfo(BlockProcessor owner, BlockInfo source) {
		owner_ = owner;
		owner_type_ = owner_.block_type();
		source_ = source;
		eid_list_ = new EndpointIDVector();
		contents_ = new SerializableByteBuffer(DATA_BUFFER_SIZE);
		data_length_ = 0;
		data_offset_ = 0;
		complete_    = false;
		reloaded_    = false;
	
	}
	
	/**
	 * Getter for the storage array
	 * @return
	 */
	public byte[] storage_array()
	{
		return storage_array_;
	}
	
	/**
	 * Setter for the storage array
	 * @param storage_array
	 * @return
	 */
	public byte[] set_storage_array(byte[] storage_array)
	{
		return storage_array_ = storage_array;
	}

	/**
	 * "create BlockInfo by copying metadata from another BlockInfo" [DTN2]
	 * @param bi
	 */
	public BlockInfo(BlockInfo bi) {
		owner_ = bi.owner_;
		owner_type_ = bi.owner_type_;
		source_ = bi.source_;
		eid_list_ = (EndpointIDVector) bi.eid_list_.clone();
		
		IByteBuffer src = bi.contents();
		byte[] temp = new byte[bi.contents().capacity()];
		src.rewind();
		src.get(temp);
		contents_ = new SerializableByteBuffer(src.capacity());
		contents_.put(temp);
		
		data_length_ = bi.data_length_;
		data_offset_ = bi.data_offset_;
		complete_   = bi.complete_;
		reloaded_    = bi.reloaded_;
		

	}

	/**
	 * "List owner indicator (not transmitted)" [DTN2]
	 */
	public enum list_owner_t {
		LIST_NONE(0x00), LIST_RECEIVED(0x01), LIST_API(0x02), LIST_EXT(0x03), LIST_XMIT(
				0x04);
		private static final Map<Integer, list_owner_t> lookup = new HashMap<Integer, list_owner_t>();

		static {
			for (list_owner_t s : EnumSet.allOf(list_owner_t.class))
				lookup.put(s.getCode(), s);
		}

		private int code;

		private list_owner_t(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static list_owner_t get(int code) {
			return lookup.get(code);
		}
	}

	/**
	 * Setter for the contents in the Buffer
	 * @param contents
	 */
	public final void set_contents(IByteBuffer contents)
	{
		contents_ = contents;
	}
	
	/**
	 * Getter for the Block owner
	 * @return
	 */
	public final BlockProcessor owner() {
		return owner_;
	}

	/**
	 * "If the Block is created from another block info. This is how to get the source block info."[DTN2]
	 * @return
	 */
	public final BlockInfo source() {
		return source_;
	}

	/**
	 * EndpointID list of this BlockInfo
	 * @return
	 */
	public final EndpointIDVector eid_list() {
		return eid_list_;
	}

	/**
	 * Return a read-only copy of buffer for other method
	 */
	public final IByteBuffer contents() {
		return contents_.asReadOnlyBuffer();
	}
	
	/**
	 * Return writable buffer for other method
	*/
	public IByteBuffer writable_contents() {
		return contents_;
	}

	/**
	 * Getter for the data length of this BlockInfo
	 * @return
	 */
	public int data_length() {
		return data_length_;
	}

	/**
	 * Getter for the data offset of this BlockInfo
	 * @return
	 */
	public int data_offset() {
		return data_offset_;
	}

	/**
	 * The fulllength of this block calculated by the summation of data_offset and data_length
	 * @return
	 */
	public int full_length() {
		return (data_offset_ + data_length_);
	}

	/**
	 * Flag to indicate whether this BlockInfo is already processed by the BlockProcessor
	 * @return
	 */
	public boolean complete() {
		return complete_;
	}

	/**
	 * Flag to indicate whether the BlockInfo is reloaded
	 * @return
	 */
	public boolean reloaded() {
		return reloaded_;
	}

	/**
	 * Flag to check whether this Block is the last Block in the list of blocks
	 * @return
	 */
	public boolean last_block() {
		 //check if it's too small to be flagged as last
		if (contents_.position() < 2) return false;
		
		
		int flags = flags();
		int last_flag_bit = BundleProtocol.block_flag_t.BLOCK_FLAG_LAST_BLOCK.getCode();
		return (flags & last_flag_bit) > 0;
	}

	/**
	 * Setter for the owner of the Block ( BlockProcessor )
	 * @param o
	 */
	public void set_owner(BlockProcessor o) {
		owner_ = o;
	}

	/**
	 * Setter for the EndpointID List of this BlockInfo
	 * @param l
	 */
	public void set_eid_list(final EndpointIDVector l) {
		eid_list_ = l;
	}
	
	/**
	 * Setter for the complete flag
	 * @param t
	 */
	public void set_complete(boolean t) {
		complete_ = t;
	}

	/**
	 * Setter for the data_length field
	 * @param l
	 */
	public void set_data_length(int l) {
		data_length_ = l;
	}

	/**
	 * Setter for the data_offset
	 * @param o
	 */
	public void set_data_offset(int o) {
		data_offset_ = o;
	}

	/**
	 * Routine for adding EndpointID to the Endpoint ID List maintained by this BlockInfo
	 * @param e
	 */
	public void add_eid(EndpointID e) {
		eid_list_.add(e);
	}

	/**
	 * Setter for the reloaded flag
	 * @param t
	 */
	public void set_reloaded(boolean t) {
		reloaded_ = t;
	}

	/**
	 * Getter for the type of Block
	 * @return
	 */
	public bundle_block_type_t type() {
		
	    if (owner_ != null)
	         return owner_.block_type();

	    // if the data is there already read from the binary data
	    
	    byte[] type_value = BufferHelper.get_data(contents_, 0, BundleProtocol.PREAMBLE_FIXED_LENGTH);
	   
	    bundle_block_type_t type = bundle_block_type_t.get(type_value[0]);
	    
	    if (owner_ != null)
	        assert(type == owner_.block_type()
	               || owner_.block_type() == BundleProtocol.bundle_block_type_t.UNKNOWN_BLOCK);
	    
	
	    return type;
	}

	/**
	 * Getter Block Processing Control flag.
	 * Not Applicable to primary block
	 */
	public int flags() {
		 if (type() == BundleProtocol.bundle_block_type_t.PRIMARY_BLOCK) {
		        return bundle_block_type_t.PRIMARY_BLOCK.getCode();
		    }
		    
		
		 
		    int flags[] = new int[1];
		    
		    BufferHelper.read_SDNV(contents_, BundleProtocol.PREAMBLE_FIXED_LENGTH, flags);assert(flags[0] > 0) ;
		    return flags[0];
	}

	/**
	 * Set Block Processing control flag
	 * Not Applicable for PrimaryBlock
	 */
	public void set_flags(int flags) {
		 if (type() == BundleProtocol.bundle_block_type_t.PRIMARY_BLOCK) {
			 Log.e(TAG, "trying to set flags for primary block!!, Should not come here");
			 return;
		    }
		BufferHelper.write_SDNV(contents_, BundleProtocol.PREAMBLE_FIXED_LENGTH, flags);
	}

	/**
	 * Owner of this block
	 */
	protected BlockProcessor owner_;
	
	/**
	 * Extracted from owner
	 */
	protected bundle_block_type_t owner_type_;
	/**
	 * Owner of this block
	 */
	protected BlockInfo source_;  
	
	/**
	 * List of EIDs used in this block
	 */
	protected EndpointIDVector eid_list_;  
	
	/**
	 * Block contents with length set to the amount currently in the buffer
	 */
	protected IByteBuffer contents_; 
	
	/**
	 * Length of the block data (with out preamble)
	 */
	protected int data_length_; 
	/**
	 * Offset of first byte of the block data
	 */
	protected int data_offset_;
	
	/**
	 * Whether or not this block is complete
	 */
	protected boolean complete_;
	
	/**
	 * Whether or not this block is reloaded
	 */
	protected boolean reloaded_; 
	
};

