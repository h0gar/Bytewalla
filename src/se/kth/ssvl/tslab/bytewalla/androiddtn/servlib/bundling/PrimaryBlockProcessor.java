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

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BlockInfo.list_owner_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.Bundle.priority_values_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol.bundle_block_type_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol.status_report_reason_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.exception.BlockProcessorTooShortException;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.contacts.Link;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.BufferHelper;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.SerializableByteBuffer;
import android.util.Log;


/**
 * This class extends BlockProcessor and is the implementation of the primary bundle block.
 * Block processor implementation for the primary bundle block.
 * @author Sharjeel Ahmed (sharjeel@kth.se)
 */

public class PrimaryBlockProcessor extends BlockProcessor implements Serializable{

	/**
	 * SerialVersionID to Support Serializable.
	 */
	private static final long serialVersionUID = -32023555271236134L;

	/**
	 * TAG for Android Logging
	 */

	public static String TAG = "PrimaryBlockProcessor";    

	/**
	 * Constructor 
	 */
	
    public PrimaryBlockProcessor(){
    	super(bundle_block_type_t.PRIMARY_BLOCK);
    }

    /**
     * This function consumes the primary block of the bundle. It is a 
     * virtual from BlockProcessor.
     * @param bundle Bundle to set data after consuming
     * @param blcok Primary block to set data after consuming
     * @param buf Populated buffer to read data from for consuming 
     * @param len Number of bytes to consume
     * @return  Return number of bytes successfully consumed, In case of error return -1
     */    

    @Override
	public int consume(Bundle bundle,
                BlockInfo block,
                IByteBuffer    buffer,
                int     len){
    	

    	int consumed = buffer.position();

        PrimaryBlock primary = new PrimaryBlock();

//        buf.position(0);
        assert(! block.complete())
        :TAG+": consume() block already complete";
        
        Dictionary dict = bundle.recv_blocks().dict();
        
        IByteBuffer byte_buffer_temp = new SerializableByteBuffer(len);
        
//      	byte_buffer_temp = BufferHelper.reserve(byte_buffer_temp, len);
        block.set_contents(byte_buffer_temp);

        BufferHelper.copy_data(byte_buffer_temp, byte_buffer_temp.position(), buffer, buffer.position(), len);
        byte_buffer_temp.position(byte_buffer_temp.position() + len);
        
        IByteBuffer buf_block_content = block.contents();
        
        int primary_len = len = buf_block_content.capacity()-buf_block_content.remaining();
        buf_block_content.position(0);

        Log.d(TAG, " primary_len: "+primary_len+" : len:"+len);
        
        assert(primary_len == len)
        :TAG+":  consume() primary!=len";

        primary.set_version(buf_block_content.get()); 

        if (primary.version() != BundleProtocol.CURRENT_VERSION) {
            Log.e(TAG, String.format("protocol version mismatch %s != %s",
                       primary.version, BundleProtocol.CURRENT_VERSION));
            return -1;
        }
        len -= 1;
        
        try{
        // Grab the SDNVs representing the flags and the block length.
        len -= read_sdnv(buf_block_content, primary.processing_flags());
        len -= read_sdnv(buf_block_content, primary.block_length());
        
        
        
        Log.d(TAG, String.format("parsed primary block: version %s length %s",
                primary.version(), block.data_length()));
        
        // Parse the flags.
        parse_bundle_flags(bundle, primary.processing_flags_value());
        parse_cos_flags(bundle, primary.processing_flags_value());
        parse_srr_flags(bundle, primary.processing_flags_value());        

        // What remains in the buffer should now be equal to what the block-length
        // field advertised./
        assert(len == block.data_length())
        :TAG+": consume() data and block length not equal";
        
        //set data_offset
        
        block.set_data_offset(buf_block_content.position());
        block.set_data_length((int)primary.block_length_value());
        
        len -= read_sdnv(buf_block_content, primary.dest_scheme_offset());
        len -= read_sdnv(buf_block_content, primary.dest_ssp_offset());
        len -= read_sdnv(buf_block_content, primary.source_scheme_offset());
        len -= read_sdnv(buf_block_content, primary.source_ssp_offset());
        len -= read_sdnv(buf_block_content, primary.replyto_scheme_offset());
        len -= read_sdnv(buf_block_content, primary.replyto_ssp_offset());
        len -= read_sdnv(buf_block_content, primary.custodian_scheme_offset());
        len -= read_sdnv(buf_block_content, primary.custodian_ssp_offset());

        len -= read_sdnv(buf_block_content, primary.creation_time());
        if (primary.creation_time_value() > Integer.MAX_VALUE) {
            Log.e(TAG, String.format("creation timestamp time is too large: %s",
                      primary.creation_time_value()));
            return -1;
        }

        len -= read_sdnv(buf_block_content, primary.creation_sequence());
        if (primary.creation_sequence_value() > Integer.MAX_VALUE) {
        	Log.e(TAG, String.format("creation timestamp sequence is too large: %s",
                      primary.creation_sequence()));
        	return -1;
        }
        
        len -= read_sdnv(buf_block_content, primary.lifetime());
        if (primary.lifetime_value() > Integer.MAX_VALUE) {
            Log.e(TAG, String.format("lifetime is too large: %s", primary.lifetime));
            return -1;
        }
        
        len -= read_sdnv(buf_block_content, primary.dictionary_length());
        
        // Make sure that the creation timestamp parts and the lifetime fit into
        // a 32 bit integer.
        
        bundle.set_creation_ts(new BundleTimestamp(primary.creation_time_value(),
                                                primary.creation_sequence_value()));
        bundle.set_expiration((int)primary.lifetime_value());

        /*
         * Verify that we have the whole dictionary.
         */
        if (len < primary.dictionary_length_value()) {

        	Log.e(TAG, String.format("primary block advertised incorrect length %s",
                      block.data_length()));
        	
        	return -1;
        }
        	
        /*
         * Make sure that the dictionary ends with a null byte./
         */
        if (buf_block_content.get((int) (buf_block_content.position()+primary.dictionary_length_value()-1)) != '\0') {
            Log.e(TAG, "dictionary does not end with a NULL character! "+primary_len);
            return -1;

        }
        																									
        /*
         * Now use the dictionary buffer to parse out the various endpoint
         * identifiers, making sure that none of them peeks past the end
         * of the dictionary block.
         */
        IByteBuffer dictionary = buf_block_content;

        len -= primary.dictionary_length_value();

        Log.d(TAG, "Dict starting point :"+(primary_len-primary.dictionary_length_value()));
//        dictionary.position((int)(primary_len-primary.dictionary_length_value()));
        
        dict.set_dict(dictionary, (int)primary.dictionary_length_value());
        
        Log.d(TAG, "Extract source :"+(primary_len-primary.dictionary_length_value()));
        
        if(!dict.extract_eid(bundle.source(),
                          primary.source_scheme_offset(),
                          primary.source_ssp_offset())){
        	Log.e(TAG, "Extract source fail:");
        }
        else{
        	block.eid_list().add(bundle.source());
        	Log.d(TAG, "Extract source :"+bundle.source().str());
        }
        
        if(!dict.extract_eid(bundle.dest(),
                          primary.dest_scheme_offset(),
                          primary.dest_ssp_offset())){
        	Log.e(TAG, "Extract dest fail:");
        }
        else{
        	block.eid_list().add(bundle.dest());
        	Log.d(TAG, "Extract dest :"+bundle.dest().str());
        }
        
        if(!dict.extract_eid(bundle.replyto(),
                          primary.replyto_scheme_offset(),
                          primary.replyto_ssp_offset())){
        	
        	Log.e(TAG, "Extract reply fail :");
        }
        else{
        	block.eid_list().add(bundle.replyto());
        	Log.d(TAG, "Extract reply :"+bundle.replyto().str());
        }

        
        if(!dict.extract_eid(bundle.custodian(),
                          primary.custodian_scheme_offset(),
                          primary.custodian_ssp_offset())){
        	Log.e(TAG, "Extract custodian fail:");
        }
        else{
        	block.eid_list().add(bundle.custodian());
        	Log.d(TAG, "Extract custodian :"+bundle.custodian().str());
        }

        buf_block_content.position((int) (buf_block_content.position()+primary.dictionary_length_value()));
        // If the bundle is a fragment, grab the fragment offset and original
        // bundle size (and make sure they fit in a 32 bit integer).
        if (bundle.is_fragment()) {

        	int[] sdnv_buf = new int[1];
            sdnv_buf[0] = 0;

            len -= read_sdnv(buf_block_content, sdnv_buf);
            if (sdnv_buf[0] > Integer.MAX_VALUE) {
                Log.e(TAG, String.format("fragment offset is too large: %s",
                          sdnv_buf));
                return -1;
            }
            
            bundle.set_frag_offset(sdnv_buf[0]);
            sdnv_buf[0] = 0;
            
            len -= read_sdnv(buf_block_content, sdnv_buf);

            if (sdnv_buf[0] > Integer.MAX_VALUE) {
                Log.e(TAG, String.format("fragment original length is too large: %s",
                          sdnv_buf));
                return -1;
            }
            
            bundle.set_orig_length(sdnv_buf[0]);

            Log.d(TAG, String.format(TAG, "parsed fragmentation info: offset %s orig_len %s",
                        bundle.frag_offset(), bundle.orig_length()));
        }
        
        Log.d(TAG, "primary_len: "+primary_len+" : ln"+len + ": Consumed"+consumed);

        block.set_complete(true);
        
    	return primary_len-len;
    	
        }
        
        catch (BlockProcessorTooShortException e)
        {
        	// revert position
        	buf_block_content.position();
        	return -1;
        }
    }

    /**
     * Check the validity  of the Primary block
     * @param bundle Bundle to check the generic validity of block
     * @param block_list List of Blocks
     * @param block Block to check if it's valid or not
     * @param reception_reason If block is not valid then reception reason
     * @param deletion_reason If block is not balid then deletion reason
     * 
     * @return True if the block is valid else false
     */
    
    @Override
	public boolean validate(final Bundle  bundle,
                  BlockInfoVec           block_list,
                  BlockInfo              block,
                  status_report_reason_t[] reception_reason,
                  status_report_reason_t[] deletion_reason){
        // Make sure all four EIDs are valid.
        boolean eids_valid = true;
        eids_valid &= bundle.source().valid();
        eids_valid &= bundle.dest().valid();
        eids_valid &= bundle.custodian().valid();
        eids_valid &= bundle.replyto().valid();
        
        if (!eids_valid) {
            Log.e(TAG, "bad value for one or more EIDs");
            deletion_reason[0] = status_report_reason_t.REASON_BLOCK_UNINTELLIGIBLE;
            return false;
        }
        
        // According to BP section 3.3, there are certain things that a bundle
        // with a null source EID should not try to do. Check for these cases
        // and reject the bundle if any is true.
        
        
        Log.d(TAG, "Going to check null eid");
        if (bundle.source().equals(EndpointID.NULL_EID())) {
        	Log.d(TAG, "Inside of Going to check null eid");
            if (bundle.receipt_requested() || bundle.app_acked_rcpt()) { 
                Log.e(TAG,
                          "bundle with null source eid has requested a report; reject it");
                deletion_reason[0] = status_report_reason_t.REASON_BLOCK_UNINTELLIGIBLE;
                return false;
            }
        
            if (bundle.custody_requested()) {
                Log.e(TAG,"bundle with null source eid has requested custody transfer; reject it");
                deletion_reason[0] = status_report_reason_t.REASON_BLOCK_UNINTELLIGIBLE;
                return false;
            }

            if (!bundle.do_not_fragment()) {
                Log.e(TAG,"bundle with null source eid has not set "
                          +"'do-not-fragment' flag; reject it");
                deletion_reason[0] = status_report_reason_t.REASON_BLOCK_UNINTELLIGIBLE;
                return false;
            }
        }
        
        Log.d(TAG, "Out of Going to check null eid");
        // Admin bundles cannot request custody transfer.
        if (bundle.is_admin()) {
            if (bundle.custody_requested()) {
                Log.e(TAG, "admin bundle requested custody transfer; reject it");
                deletion_reason[0] = status_report_reason_t.REASON_BLOCK_UNINTELLIGIBLE;
                return false;
            }

            if ( bundle.receive_rcpt() ||
                 bundle.custody_rcpt() ||
                 bundle.forward_rcpt() ||
                 bundle.delivery_rcpt() ||
                 bundle.deletion_rcpt() ||
                 bundle.app_acked_rcpt() )
            {
                Log.e(TAG, "admin bundle has requested a report; reject it");
                deletion_reason[0] = status_report_reason_t.REASON_BLOCK_UNINTELLIGIBLE;
                return false;
            }
        }
        
        return true;
    }

    /**
     * Prepare function prepare bundle to generate. It adds Primary Block Info at 
     * the start of xmit_blocks and add endpoint eids at the start of dictionary.
     * @param bundle Bundle to prepare
     * @param xmit_blocks Empty xmit_blocks of the blundle
     * @param source Source endpoint id
     * @param link Link of the bundle
     * @param list Owner type
     * @return Return success message on success   
     */
    @Override
	public int prepare(final Bundle    bundle,
                BlockInfoVec    xmit_blocks,
                final BlockInfo source,
                final Link   link,
                list_owner_t     list){

    	// There shouldn't already be anything in the xmit_blocks
    	
    	assert(xmit_blocks.size() == 0)
    	:TAG+": prepare() there shouldn't be anything already in xmit_blocks";
            
        // Add EIDs to start off the dictionary
        xmit_blocks.dict().add_eid(bundle.dest());
        xmit_blocks.dict().add_eid(bundle.source());
        xmit_blocks.dict().add_eid(bundle.replyto());
        xmit_blocks.dict().add_eid(bundle.custodian());

        // make sure to add the primary to the front
        xmit_blocks.add(0, new BlockInfo(this, source));
        
        return BP_SUCCESS;
    	
    }


    /**
     * Generate function for primary will do nothing as primary block can't be last block. 
     */
    @Override
	public int generate(final Bundle  bundle,
                 BlockInfoVec  xmit_blocks,
                 BlockInfo     block,
                 final Link link,
                 boolean           last){

        /*
         * The primary can't be last since there must be a payload block
         */
        assert(!last)
        :TAG+": generate() The primary can't be last since there must be a payload block";

        return BP_SUCCESS;
    }

    /**
     * Generate primary block by encoding all the metadata of the primary block
     * and copy to primary block writeable buffer. 
     * @param bundle Bundle to generate
     * @param xmit_blocks xmit_blocks of the bundle
     * @param block Primary block of the bundle to generate and write to the writeable buffer 
     */
    public void generate_primary(final Bundle bundle,
                          BlockInfoVec xmit_blocks,
                          BlockInfo    block){

        // point at the local dictionary
        Dictionary dict = xmit_blocks.dict();
        int primary_len = 0;     // total length of the primary block
        PrimaryBlock primary = new PrimaryBlock();
        
        
        primary_len = get_primary_len(bundle, dict, primary);
        
        block.set_contents(new SerializableByteBuffer(primary_len));
        
        block.set_data_length((int)primary.block_length_value());
        block.set_data_offset((int)(primary_len-primary.block_length_value()));
        /*
         * Advance buf and decrement len as we go through the process.
         */
        IByteBuffer buf = block.writable_contents();
        int     len = primary_len;
        
        Log.d(TAG, String.format("generating primary: length %s", primary_len));
        
        // Stick the version number in the first byte.
        buf.put((byte)BundleProtocol.CURRENT_VERSION);
        len -= 1;
        
        len -= write_sdnv(primary.processing_flags(), buf);
        len -= write_sdnv(primary.block_length(), buf);
        len -= write_sdnv(primary.dest_scheme_offset(), buf);
        len -= write_sdnv(primary.dest_ssp_offset(), buf);
        len -= write_sdnv(primary.source_scheme_offset(), buf);
        len -= write_sdnv(primary.source_ssp_offset(), buf);
        len -= write_sdnv(primary.replyto_scheme_offset(), buf);
        len -= write_sdnv(primary.replyto_ssp_offset(), buf);
        len -= write_sdnv(primary.custodian_scheme_offset(), buf);
        len -= write_sdnv(primary.custodian_ssp_offset(), buf);
        len -= write_sdnv(bundle.creation_ts().seconds(), buf);
        len -= write_sdnv(bundle.creation_ts().seqno(), buf);
        len -= write_sdnv(bundle.expiration(), buf);
        len -= write_sdnv(primary.dictionary_length(), buf);

        // Add the dictionary.
        Log.d(TAG, "Current Buf: "+buf.position());
        Log.d(TAG, "Dict length: "+dict.dict_length());
        Log.d(TAG, "Dict length: "+dict.dict_length());
        buf.put(dict.dict());
        //memcpy(buf, dict->dict(), dict->length());
//        buf += dict->length();
        len -= dict.dict_length();
        Log.d(TAG, "Preparing len:"+len);
        /*
         * If the bundle is a fragment, stuff in SDNVs for the fragment
         * offset and original length.
         */
        
        if (bundle.is_fragment()) {
        	len -= write_sdnv(bundle.frag_offset(), buf);
            Log.d(TAG, "Preparing len:"+len);

        	len -= write_sdnv(bundle.orig_length(), buf);
            Log.d(TAG, "Preparing len:"+len);
        	
        }
        /*
         * Asuming that get_primary_len is written correctly, len should
         * now be zero since we initialized it to primary_len at the
         * beginning of the function.
         */
        
      buf.position(0);
        assert(len == 0)
        :TAG+": len not ==0";
        Log.e(TAG, "Current Len: "+ len);
    }


    /**
     * Encode the value and write to the buffer
     * @param val Value to encode
     * @param buf Buffer to write encoded value
     * @return Number of bytes encoded
     */
    public int write_sdnv(long[] val, IByteBuffer buf){
    	
        int sdnv_len = SDNV.encode(val, buf);
        assert(sdnv_len > 0):
            TAG+"write sdnv: incorrect length";;
		return sdnv_len; 
    }

    /**
     * Encode the value and write to the buffer
     * @param val Value to encode
     * @param buf Buffer to write encoded value
     * @return Number of bytes encoded
     */
    
    public int write_sdnv(long val, IByteBuffer buf){
    	
        int sdnv_len = SDNV.encode(val, buf);
        assert(sdnv_len > 0):
        TAG+"write sdnv: incorrect length";;
        return sdnv_len; 
    }

    /**
     * decode the value from the buffer and return 
     * @param buf Buffer to read encoded value form 
     * @param val Get the empty array and set the decoded value on the first index of array
     * @return Number of bytes decoded
     */

    public int read_sdnv(IByteBuffer buf, long[] val) throws BlockProcessorTooShortException{
    	
        int sdnv_len = SDNV.decode(buf, val);
        if(sdnv_len<0 ){
        	throw new BlockProcessorTooShortException();
        }
        assert(sdnv_len < 0):
        TAG+"read sdnv: incorrect length";

        return sdnv_len; 
    }

    /**
     * decode the value from the buffer and return 
     * @param buf Buffer to read encoded value form 
     * @param val Get the empty array and set the decoded value on the first index of array
     * @return Number of bytes decoded
     */
    
    public int read_sdnv(IByteBuffer buf, int[] val) throws BlockProcessorTooShortException{
    	
        int sdnv_len = SDNV.decode(buf, val);
        if(sdnv_len<1 ){
        	throw new BlockProcessorTooShortException();
        }
        assert(sdnv_len < 0):
        TAG+"read sdnv: incorrect length";

        return sdnv_len; 
    }    

    /**
     * Values for bundle processing flags that appear in the primary
     * block.
     */
    public enum bundle_processing_flag_t 
    {
    	BUNDLE_IS_FRAGMENT(1 << 0),
    	BUNDLE_IS_ADMIN(1 << 1),
    	BUNDLE_DO_NOT_FRAGMENT(1 << 2),
    	BUNDLE_CUSTODY_XFER_REQUESTED(1 << 3),
    	BUNDLE_SINGLETON_DESTINATION(1 << 4),
    	BUNDLE_ACK_BY_APP(1 << 5),
    	BUNDLE_UNUSED(1 << 6)
    ;

        private static final Map<Integer,bundle_processing_flag_t> lookup 
             = new HashMap<Integer,bundle_processing_flag_t>();

        static {
             for(bundle_processing_flag_t s : EnumSet.allOf(bundle_processing_flag_t.class))
                  lookup.put(s.getCode(), s);
        }

        private int code;

        private bundle_processing_flag_t(int code) {
             this.code = code;
        }

        public int getCode() { return code; }

        public static bundle_processing_flag_t get(int code) { 
             return lookup.get(code); 
        }
    }
    
    /**
     * Internal class to store the data structure of all the values of primary block
     */
    protected class PrimaryBlock {
        private long version;
        private long[] processing_flags = new long[1];
        private long[] block_length =  new long[1];
        private long[] dest_scheme_offset = new long[1];
        private long[] dest_ssp_offset = new long[1];
        private long[] source_scheme_offset = new long[1];
        private long[] source_ssp_offset = new long[1];
        private long[] replyto_scheme_offset = new long[1];
        private long[] replyto_ssp_offset = new long[1];
        private long[] custodian_scheme_offset = new long[1];
        private long[] custodian_ssp_offset = new long[1];
        private long[] creation_time = new long[1];
        private long[] creation_sequence = new long[1];
        private long[] lifetime  = new long[1];
        private long[] dictionary_length  = new long[1];;
      
        public PrimaryBlock(){
            version = 0 ;
            processing_flags[0] = 0;
            block_length[0] = 0;
            dest_scheme_offset[0] = 0;
            dest_ssp_offset[0] = 0;
            source_scheme_offset[0] = 0;
            source_ssp_offset[0] = 0;
            replyto_scheme_offset[0] = 0;
            replyto_ssp_offset[0] = 0;
            custodian_scheme_offset[0] = 0;
            custodian_ssp_offset[0] = 0;
            creation_time[0] = 0;
            creation_sequence[0] = 0;
            lifetime[0]  = 0;
        	dictionary_length[0] = 0;
        }
        public long version(){
        	return version;
        }
        
        public long[] processing_flags(){
        	return processing_flags;
        }
        
        public long processing_flags_value(){
        	return processing_flags[0];
        }

        public long[] block_length(){
        	return block_length;
        }
        
        public long block_length_value(){
        	return block_length[0];
        }

        public long[] dest_scheme_offset(){
        	return dest_scheme_offset;
        }
        
        public long[] dest_ssp_offset(){
        	return dest_ssp_offset;
        }
        public long[] source_scheme_offset(){
        	return source_scheme_offset;
        }
        
        public long[] source_ssp_offset(){
        	return source_ssp_offset;
        }
        
        public long[] replyto_scheme_offset(){
        	return replyto_scheme_offset;
        }
        
        public long[] replyto_ssp_offset(){
        	return replyto_ssp_offset;
        }
        
        public long[] custodian_scheme_offset(){
        	return custodian_scheme_offset;
        }
        
        public long[] custodian_ssp_offset(){
        	return custodian_ssp_offset;
        }
        
        public long[] creation_time(){
        	return creation_time;
        }
        
        public long[] creation_sequence(){
        	return creation_sequence;
        }
        
        public long[] lifetime(){
        	return lifetime;
        }
        
        public long creation_time_value(){
        	return creation_time[0];
        }
        
        public long creation_sequence_value(){
        	return creation_sequence[0];
        }
        
        public long lifetime_value(){
        	return lifetime[0];
        }

        public long[] dictionary_length(){
        	return dictionary_length;
        }

        public long dictionary_length_value(){
        	return dictionary_length[0];
        }

        public void set_version(long v){
        	version = v;
        }
        
        public void set_processing_flags(long v){
        	processing_flags[0] = v;
        }
        
        public void set_block_length(long v){
        	block_length[0] = v;
        }
        
        public void set_dest_scheme_offset(long v){
        	dest_scheme_offset[0] = v;
        }
        
        public void set_dest_ssp_offset(int v){
        	dest_ssp_offset[0] = v;
        }
        
        public void set_source_scheme_offset(long v){
        	source_scheme_offset[0] = v;
        }
        
        public void set_source_ssp_offset(long v){
        	source_ssp_offset[0] = v;
        }
        
        public void set_replyto_scheme_offset(long v){
        	replyto_scheme_offset[0] = v;
        }
        
        public void set_replyto_ssp_offset(long v){
        	replyto_ssp_offset[0] = v;
        }
        
        public void set_custodian_scheme_offset(long v){
        	custodian_scheme_offset[0] = v;
        }
        
        public void set_custodian_ssp_offset(long v){
        	custodian_ssp_offset[0] = v;
        }
        
        public void set_creation_time(long v){
        	creation_time[0] = v;
        }
        
        public void set_creation_sequence(long v){
        	creation_sequence[0] = v;
        }
        
        public void set_lifetime(long v){
        	lifetime[0] = v;
        }
        
        public void set_dictionary_length(long v){
        	dictionary_length[0] = v;
        }
    };

    /**
     * Internal function get the total length of primary block to write on the buffer
     * @param bundle Bundle to generate  
     * @param dict Dictionary to get the offsets of the endpoint eids 
     * @param primary PrimaryBlock data strucre object
     * @return Total numbers of Bytes required to write primary block
     */
    protected static int get_primary_len(final Bundle bundle,
                                  Dictionary dict,
                                  PrimaryBlock primary){
    	int primary_len =  0;
    	int block_len = 0;
        primary.set_dictionary_length(0);
        primary.set_block_length(0);
        
        
        /*
         * We need to figure out the total length of the primary block,
         * except for the SDNVs used to encode flags and the length itself and
         * the one byte version field.
         *
         * First, we determine the size of the dictionary by first
         * figuring out all the unique strings, and in the process,
         * remembering their offsets and summing up their lengths
         * (including the null terminator for each).
         */
        
        
        dict.get_offsets(bundle.dest(),primary.dest_scheme_offset(), primary.dest_ssp_offset());
        
        block_len += SDNV.encoding_len(primary.dest_scheme_offset());
        block_len += SDNV.encoding_len(primary.dest_ssp_offset());

        dict.get_offsets(bundle.source(), primary.source_scheme_offset(), primary.source_ssp_offset());
  
        block_len += SDNV.encoding_len(primary.source_scheme_offset());
        block_len += SDNV.encoding_len(primary.source_ssp_offset());

        dict.get_offsets(bundle.replyto(), primary.replyto_scheme_offset(), primary.replyto_ssp_offset());

        block_len += SDNV.encoding_len(primary.replyto_scheme_offset());
        block_len += SDNV.encoding_len(primary.replyto_ssp_offset());
        
        dict.get_offsets(bundle.custodian(), primary.custodian_scheme_offset(), primary.custodian_ssp_offset());

        block_len += SDNV.encoding_len(primary.custodian_scheme_offset());
        block_len += SDNV.encoding_len(primary.custodian_ssp_offset());
        
        primary.set_dictionary_length(dict.dict_length());
        
        block_len += SDNV.encoding_len(bundle.creation_ts().seconds());
        block_len += SDNV.encoding_len(bundle.creation_ts().seqno());
        block_len += SDNV.encoding_len(bundle.expiration());
        
        block_len += SDNV.encoding_len(primary.dictionary_length_value());
        block_len += primary.dictionary_length_value();
        
        /*
         * If the bundle is a fragment, we need to include space for the
         * fragment offset and the original payload length.
         *
         * Note: Any changes to this protocol must be reflected into the
         * FragmentManager since it depends on this length when
         * calculating fragment sizes.
         */
        if (bundle.is_fragment()) {
        	block_len += SDNV.encoding_len(bundle.frag_offset());
        	block_len += SDNV.encoding_len(bundle.orig_length());
        }
        
        // Format the processing flags.
        primary.set_processing_flags(format_bundle_flags(bundle));
        
        primary.set_processing_flags(format_bundle_flags(bundle));
        primary.set_processing_flags(primary.processing_flags_value() | format_cos_flags(bundle));
        primary.set_processing_flags(primary.processing_flags_value() | format_srr_flags(bundle));
        

        /*
         * Finally, add up the initial preamble and the variable
         * length part.
         */
        
        primary.set_block_length(block_len);

        primary_len = (int) (1 + SDNV.encoding_len(primary.processing_flags) +
                      SDNV.encoding_len(primary.block_length()) +
                      primary.block_length_value());
        
        Log.d(TAG, "get_primary_len: for bundleid = "+bundle.bundleid()+": "+primary_len);        
        // Fill in the remaining values of 'primary' just for the sake of returning
        // a complete data structure.
        primary.set_version(BundleProtocol.CURRENT_VERSION);
        primary.set_creation_time(bundle.creation_ts().seconds());
        primary.set_creation_sequence(bundle.creation_ts().seqno());
        primary.set_lifetime(bundle.expiration());
        return primary_len;    	
    }

    /**
     * Internal function to format the bundle flags so they can be stored in one byte.
     * @param bundle Bundle to get the flags values
     * @return Formatted value of the flags
     */
    protected static int format_bundle_flags(final Bundle bundle){

    	int flags = 0;

        if (bundle.is_fragment()) {
            flags |= bundle_processing_flag_t.BUNDLE_IS_FRAGMENT.getCode();
        }

        if (bundle.is_admin()) {
            flags |= bundle_processing_flag_t.BUNDLE_IS_ADMIN.getCode();
        }

        if (bundle.do_not_fragment()) {
            flags |= bundle_processing_flag_t.BUNDLE_DO_NOT_FRAGMENT.getCode();
        }

        if (bundle.custody_requested()) {
            flags |= bundle_processing_flag_t.BUNDLE_CUSTODY_XFER_REQUESTED.getCode();
        }

        if (bundle.singleton_dest()) {
            flags |= bundle_processing_flag_t.BUNDLE_SINGLETON_DESTINATION.getCode();
        }
        
        if (bundle.app_acked_rcpt()) {
            flags |= bundle_processing_flag_t.BUNDLE_ACK_BY_APP.getCode();
        }
        return flags;
    }

    /**
     * Function to parse the formatted value of the flags
     * @param bundle Bundle to set the flag values
     * @param flags Formatted value of flags
     */
    public static void parse_bundle_flags(Bundle bundle, long flags){
        
    	if ((flags & bundle_processing_flag_t.BUNDLE_IS_FRAGMENT.getCode())>0) {
            bundle.set_is_fragment(true);
        } else {
            bundle.set_is_fragment(false);
        }

        if ((flags & bundle_processing_flag_t.BUNDLE_IS_ADMIN.getCode())>0) {
            bundle.set_is_admin(true);
        } else {
            bundle.set_is_admin(false);
        }

        if ((flags & bundle_processing_flag_t.BUNDLE_DO_NOT_FRAGMENT.getCode())>0) {
            bundle.set_do_not_fragment(true);
        } else {
            bundle.set_do_not_fragment(false);
        }

        if ((flags & bundle_processing_flag_t.BUNDLE_CUSTODY_XFER_REQUESTED.getCode())>0) {
            bundle.set_custody_requested(true);
        } else {
            bundle.set_custody_requested(false);
        }

        if ((flags & bundle_processing_flag_t.BUNDLE_SINGLETON_DESTINATION.getCode())>0) {
            bundle.set_singleton_dest(true);
        } else {
            bundle.set_singleton_dest(false);
        }
        
        if ((flags & bundle_processing_flag_t.BUNDLE_ACK_BY_APP.getCode())>0) {
            bundle.set_app_acked_rcpt(true);
        } else {
            bundle.set_app_acked_rcpt(false);
        }
    	
    }

    /**
     * Internal function to format priority flag value
     * @param bundle Bundle to get the flag value
     * @return Formatted value of the priority flag
     */
    protected static int format_cos_flags(final Bundle bundle){
        int cos_flags = 0;

        cos_flags = ((bundle.priority().getCode() & 0x3) << 7);

        return cos_flags;
    	
    }
    
    /**
     * Internal function to parse the formatted value of the priority flag
     * @param bundle Bundle to set the priority flag
     * @param cos_flags Formatted value of the priority flag
     */
    protected static void parse_cos_flags(Bundle bundle, long cos_flags){
    	int cos_flags_temp = (int) cos_flags; 
    	bundle.set_priority(priority_values_t.get((cos_flags_temp >> 7) & 0x3));
    }

    /**
     * Internal function to format the status flags of the bundle
     * @param bundle Bundle to get the flags values
     * @return Formatted value of the flags
     */
    
    protected static int format_srr_flags(final Bundle bundle){
        int srr_flags = 0;
        
        if (bundle.receive_rcpt())
            srr_flags |= BundleProtocol.bundle_processing_report_flag_t.REQUEST_STATUS_RECEIVED.getCode();

        if (bundle.custody_rcpt())
            srr_flags |= BundleProtocol.bundle_processing_report_flag_t.REQUEST_STATUS_CUSTODY_ACCEPTED.getCode();

        if (bundle.forward_rcpt())
            srr_flags |= BundleProtocol.bundle_processing_report_flag_t.REQUEST_STATUS_FORWARDED.getCode();

        if (bundle.delivery_rcpt())
            srr_flags |= BundleProtocol.bundle_processing_report_flag_t.REQUEST_STATUS_DELIVERED.getCode();

        if (bundle.deletion_rcpt())
            srr_flags |= BundleProtocol.bundle_processing_report_flag_t.REQUEST_STATUS_DELETED.getCode();

        return srr_flags;
   	
    }
    
    /**
     * Function to parse the formatted value of the status flags
     * @param bundle Bundle to set the status flag values
     * @param data.flags Formatted value of the status flags
     */
    
    protected static void parse_srr_flags(Bundle bundle, long srr_flags){
        if ((srr_flags & BundleProtocol.bundle_processing_report_flag_t.REQUEST_STATUS_RECEIVED.getCode())>0)
            bundle.set_receive_rcpt(true);

        if ((srr_flags & BundleProtocol.bundle_processing_report_flag_t.REQUEST_STATUS_CUSTODY_ACCEPTED.getCode())>0)
            bundle.set_custody_rcpt(true);

        if ((srr_flags & BundleProtocol.bundle_processing_report_flag_t.REQUEST_STATUS_FORWARDED.getCode())>0)
            bundle.set_forward_rcpt(true);

        if ((srr_flags & BundleProtocol.bundle_processing_report_flag_t.REQUEST_STATUS_DELIVERED.getCode())>0)
            bundle.set_delivery_rcpt(true);

        if ((srr_flags & BundleProtocol.bundle_processing_report_flag_t.REQUEST_STATUS_DELETED.getCode())>0)
            bundle.set_deletion_rcpt(true);
    }
}
