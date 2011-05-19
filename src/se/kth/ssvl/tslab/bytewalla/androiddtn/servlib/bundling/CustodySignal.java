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

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundlePayload.location_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol.admin_record_type_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.SerializableByteBuffer;
import android.util.Log;


/**
 * Class to read and write custody signal in the Bundle payload. 
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class CustodySignal {
	/**
	 * String TAG for using with Android Logging system
	 */
	private static String TAG = "CustodySignal";
    /**
     * The reason codes are defined in the bundle protocol class.
     */

    /**
     * Struct to hold the payload data of the custody signal.
     */
   public static class data_t {
        private BundleProtocol.admin_record_type_t  admin_type_;
        private byte admin_flags_;
        private boolean         succeeded_;
        private BundleProtocol.custody_signal_reason_t reason_;
        private long            orig_frag_offset_;
        private long            orig_frag_length_;
        private DTNTime         custody_signal_tv_;
        private BundleTimestamp orig_creation_tv_;
        private EndpointID      orig_source_eid_;
        
        
        public data_t()
        {
        	custody_signal_tv_ = new DTNTime(0,0);
        	orig_creation_tv_  = new BundleTimestamp(0,0);
        	orig_source_eid_   = new EndpointID();
        	
        	
        }
        
		/**
		 * @return the admin_type_
		 */
		public BundleProtocol.admin_record_type_t admin_type() {
			return admin_type_;
		}
		/**
		 * @param adminType the admin_type_ to set
		 */
		public void set_admin_type(BundleProtocol.admin_record_type_t admin_type) {
			admin_type_ = admin_type;
		}
		/**
		 * @return the admin_flags_
		 */
		public byte admin_flags() {
			return admin_flags_;
		}
		/**
		 * @param adminFlags the admin_flags_ to set
		 */
		public void set_admin_flags(byte admin_flags) {
			admin_flags_ = admin_flags;
		}
		/**
		 * @return the succeeded_
		 */
		public boolean succeeded() {
			return succeeded_;
		}
		/**
		 * @param succeeded the succeeded_ to set
		 */
		public void set_succeeded(boolean succeeded) {
			succeeded_ = succeeded;
		}
		/**
		 * @return the reason_
		 */
		public BundleProtocol.custody_signal_reason_t reason() {
			return reason_;
		}
		/**
		 * @param reason the reason_ to set
		 */
		public void set_reason(BundleProtocol.custody_signal_reason_t reason) {
			reason_ = reason;
		}
		/**
		 * @return the orig_frag_offset_
		 */
		public long orig_frag_offset() {
			return orig_frag_offset_;
		}
		/**
		 * @param origFragOffset the orig_frag_offset_ to set
		 */
		public void set_orig_frag_offset(long orig_frag_offset) {
			orig_frag_offset_ = orig_frag_offset;
		}
		/**
		 * @return the orig_frag_length_
		 */
		public long orig_frag_length() {
			return orig_frag_length_;
		}
		/**
		 * @param origFragLength the orig_frag_length_ to set
		 */
		public void set_orig_frag_length(long orig_frag_length) {
			orig_frag_length_ = orig_frag_length;
		}
		/**
		 * @return the custody_signal_tv_
		 */
		public DTNTime custody_signal_tv() {
			return custody_signal_tv_;
		}
		/**
		 * @param custodySignalTv the custody_signal_tv_ to set
		 */
		public void set_custody_signal_tv(DTNTime custody_signal_tv) {
			custody_signal_tv_ = custody_signal_tv;
		}
		/**
		 * @return the orig_creation_tv_
		 */
		public BundleTimestamp orig_creation_tv() {
			return orig_creation_tv_;
		}
		/**
		 * @param origCreationTv the orig_creation_tv_ to set
		 */
		public void set_orig_creation_tv(BundleTimestamp orig_creation_tv) {
			orig_creation_tv_ = orig_creation_tv;
		}
		/**
		 * @return the orig_source_eid_
		 */
		public EndpointID orig_source_eid() {
			return orig_source_eid_;
		}
		/**
		 * @param origSourceEid the orig_source_eid_ to set
		 */
		public void set_orig_source_eid_(EndpointID orig_source_eid) {
			orig_source_eid_ = orig_source_eid;
		}
    };

    /**
     * "Constructor-like function to create a new custody signal bundle." [DTN2]
     */
    public static Bundle create_custody_signal( final Bundle     orig_bundle,
                                      final EndpointID source_eid,
                                      boolean              succeeded,
                                      BundleProtocol.custody_signal_reason_t          reason)
    {
    	
    	  Bundle bundle = new Bundle(location_t.MEMORY);
    	
    	  bundle.source().assign(source_eid);
    	    if (orig_bundle.custodian().equals(EndpointID.NULL_EID())) {
    	        Log.e(TAG, String.format("create_custody_signal(for bundle id %d): " +
    	              "custody signal cannot be generated due to custodian is  null eid",
    	              orig_bundle.bundleid()));
    	    }
    	    bundle.dest().assign(orig_bundle.custodian());
    	    bundle.replyto().assign(EndpointID.NULL_EID());
    	    bundle.custodian().assign(EndpointID.NULL_EID());
    	    bundle.set_is_admin(true);

    	    bundle.set_expiration(orig_bundle.expiration());

    	    int sdnv_encoding_len = 0;
    	    int signal_len = 0;
    	    
    	 
    	    
    	    // "format of custody signals:
    	    //
    	    // 1 byte admin payload type and flags
    	    // 1 byte status code
    	    // SDNV   [Fragment Offset (if present)]
    	    // SDNV   [Fragment Length (if present)]
    	    // SDNVx2 Time of custody signal
    	    // SDNVx2 Copy of bundle X's Creation Timestamp
    	    // SDNV   Length of X's source endpoint ID
    	    // vari   Source endpoint ID of bundle X

    	    //
    	    // first calculate the length
    	    //

    	    // the non-optional, fixed-length fields above:" [DTN2]
    	    signal_len =  1 + 1;

    	    // "the 2 SDNV fragment fields:" [DTN2]
    	    if (orig_bundle.is_fragment()) {
    	        signal_len += SDNV.encoding_len(orig_bundle.frag_offset());
    	        signal_len += SDNV.encoding_len(orig_bundle.orig_length());
    	    }
    	    
    	    // "Time field, set to the current time:" [DTN2]
    	    DTNTime now = new DTNTime();
    	 
    	    
    	    signal_len += DTNTime.SDNV_encoding_len(now);
    	    
    	    // "The bundle's creation timestamp:" [DTN2]
    	    signal_len += BundleTimestamp.SDNV_encoding_len(orig_bundle.creation_ts());

    	    // the Source Endpoint ID length and value
    	    signal_len += SDNV.encoding_len(orig_bundle.source().length()) +
    	                  orig_bundle.source().length();

    	    //
    	    // "We got all the data ready, now format the buffer" [DTN2]
    	    //
    	    IByteBuffer bp = new SerializableByteBuffer(signal_len);
    	    int len = signal_len;
    	    bp.rewind();
    	    // "Admin Payload Type and flags" [DTN2]
    	    byte type_and_flags_byte = (byte)(BundleProtocol.admin_record_type_t.ADMIN_CUSTODY_SIGNAL.getCode() << 4);
    	    if (orig_bundle.is_fragment()) {
    	    	type_and_flags_byte |= BundleProtocol.admin_record_flags_t.ADMIN_IS_FRAGMENT.getCode();
    	    }
    	    bp.put(type_and_flags_byte);
    	    len--;
    	    
    	    // Status_Flag_Byte consists of Success flag and reason code
    	    byte status_flag_byte = (byte)(
    	    		                  (succeeded ? 1 : 0) << 7 
    	                            | (reason.getCode() & 0x7f) )
    	                         ;
    	    bp.put(status_flag_byte);
    	    len--;
    	    
    	    // "The 2 Fragment Fields" [DTN2]
    	    if (orig_bundle.is_fragment()) {
    	        sdnv_encoding_len = SDNV.encode(orig_bundle.frag_offset(), bp, len);
    	        assert(sdnv_encoding_len > 0);
    	        len -= sdnv_encoding_len;
    	        
    	        sdnv_encoding_len = SDNV.encode(orig_bundle.orig_length(), bp, len);
    	        assert(sdnv_encoding_len > 0);
    	        len -= sdnv_encoding_len;
    	    }
    	    
    	    // DTNTime which is a time of signal field
    	    sdnv_encoding_len = DTNTime.SDNV_encoding_len(now);
    	    assert(sdnv_encoding_len > 0);
    	    DTNTime.encodeSDNV(now, bp);
    	    len -= sdnv_encoding_len;

    	    // "Copy of bundle X's Creation Timestamp" [DTN2]
    	    sdnv_encoding_len =  BundleTimestamp.SDNV_encoding_len(orig_bundle.creation_ts());
    	    assert(sdnv_encoding_len > 0);
    	    BundleTimestamp.encodeSDNV(orig_bundle.creation_ts(), bp);
    	    len -= sdnv_encoding_len;

    	    // "The Endpoint ID length and data" [DTN2]
    	    sdnv_encoding_len = SDNV.encode(orig_bundle.source().length(), bp, len);
    	    assert(sdnv_encoding_len > 0);
    	    len -= sdnv_encoding_len;
    	   
    	    
    	    assert(len == orig_bundle.source().length());
    	    bp.put(orig_bundle.source().byte_array());
    	    
    	    // 
    	    // "Finished generating the payload" [DTN2]
    	    //
    	    bp.rewind();
    	    bundle.payload().set_data(bp, signal_len);
    	    return bundle;
    }

    /**
     * "Parsing function for custody signal bundles." [DTN2]
     */
    public static boolean parse_custody_signal(data_t data,
                                     IByteBuffer bp, int len)
    {
    	 // "1 byte Admin Payload Type + Flags:" [DTN2]
    	byte type_and_flag_byte = bp.get();
        if (len < 1) { return false; }
        data.admin_type_  =  admin_record_type_t.get((byte)(type_and_flag_byte >> 4));
        data.admin_flags_ = (byte)(type_and_flag_byte & 0xf);
        len--;

        // "validate the admin type" [DTN2]
        if (data.admin_type_ != BundleProtocol.admin_record_type_t.ADMIN_CUSTODY_SIGNAL) {
            return false;
        }

        
        int status_flag_byte = bp.get();
        // Success flag and reason code
        if (len < 1) { return false; }
        data.succeeded_ = (status_flag_byte & 0x80 ) > 0;
        data.reason_    = BundleProtocol.custody_signal_reason_t.get((byte)(status_flag_byte & 0x7f));
        
        len--;
        
        // "Fragment SDNV Fields (offset & length), if present:" [DTN2]
        if ((data.admin_flags_ & BundleProtocol.admin_record_flags_t.ADMIN_IS_FRAGMENT.getCode()) > 0)
        {
        	int[] value = new int[1];
        	
            int sdnv_bytes = SDNV.decode(bp, len, value);
            if (sdnv_bytes == -1) { return false; }
            len -= sdnv_bytes;
            data.set_orig_frag_offset(value[0]);
            
            sdnv_bytes = SDNV.decode(bp, len, value);
            if (sdnv_bytes == -1) { return false; }
            len -= sdnv_bytes;
            data.set_orig_frag_length(value[0]);
        }
        
        int ts_len;

        // The signal DTNTime
        ts_len = DTNTime.SDNV_decoding_len(bp);
        if (ts_len < 0) { return false; }
        DTNTime.decodeSDNV(data.custody_signal_tv(), bp);
        len -= ts_len;

        // Bundle Creation Timestamp
        ts_len = BundleTimestamp.SDNV_decoding_len(bp);
        if (ts_len < 0) { return false; }
        BundleTimestamp.decodeSDNV(data.orig_creation_tv(), bp);
        len -= ts_len;

        // Source Endpoint ID of Bundle
        int[] EID_len = new int[1];
        int num_bytes = SDNV.decode(bp, len, EID_len);
        if (num_bytes == -1) { return false; }
       
        len -= num_bytes;

        if (len != EID_len[0]) { return false; }
        
        byte[] source_eid_bytes = new byte[EID_len[0]];
        
        bp.get(source_eid_bytes);
        boolean ok = data.orig_source_eid_.assign(source_eid_bytes);
        if (!ok) {
            return false;
        }
        
        return true;
    }

   
};