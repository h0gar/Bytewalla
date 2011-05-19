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
import java.util.Map;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundlePayload.location_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol.admin_record_type_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol.status_report_reason_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.SerializableByteBuffer;
import android.util.Log;

/**
 *  Class to read and write Bundle Status Report in the Bundle payload.
 */
public class BundleStatusReport {
	
	private static final String TAG = "BundleStatusReport";
    public static enum flag_t {
    	 STATUS_RECEIVED((byte)0x01),
         STATUS_CUSTODY_ACCEPTED((byte)0x02),
         STATUS_FORWARDED((byte)0x04),
         STATUS_DELIVERED((byte)0x08),
         STATUS_DELETED((byte)0x10);

		private static final Map<Byte, flag_t> lookup = new HashMap<Byte, flag_t>();

		static {
			for (flag_t s : EnumSet
					.allOf(flag_t.class))
				lookup.put(s.getCode(), s);
		}

		private byte code;

		private flag_t(byte code) {
			this.code = code;
		}

		public byte getCode() {
			return code;
		}

		public static flag_t get(byte code) {
			return lookup.get(code);
		}
	}

    /**
     * "Specification of the contents of a Bundle Status Report" [DTN2]
     */
    public static class data_t {
        private BundleProtocol.admin_record_type_t            admin_type_;
        private byte           admin_flags_;
        private byte            status_flags_; 
        private BundleProtocol.status_report_reason_t   reason_code_;
        private long            orig_frag_offset_;
        private long            orig_frag_length_;
        private DTNTime         receipt_dt_;
        private DTNTime         custody_dt_;
        private DTNTime         forwarding_dt_;
        private DTNTime         delivery_dt_;
        private DTNTime         deletion_dt_;
        private BundleTimestamp orig_creation_tr_;
        private EndpointID      orig_source_eid_;
        
        public data_t()
        {
        	receipt_dt_ = new DTNTime(0, 0);
        	custody_dt_ = new DTNTime(0, 0);
        	forwarding_dt_ = new DTNTime(0, 0);
        	delivery_dt_ = new DTNTime(0, 0);
        	deletion_dt_ = new DTNTime(0, 0);
        	orig_creation_tr_ = new BundleTimestamp(-1, -1);
        	orig_source_eid_ = new EndpointID();
        
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
		public byte  admin_flags() {
			return admin_flags_;
		}
		/**
		 * @param adminFlags the admin_flags_ to set
		 */
		public void set_admin_flags(byte  admin_flags) {
			admin_flags_ = admin_flags;
		}
		/**
		 * @return the status_flags_
		 */
		public byte status_flags() {
			return status_flags_;
		}
		/**
		 * @param statusFlags the status_flags_ to set
		 */
		public void set_status_flags(byte status_flags) {
			status_flags_ = status_flags;
		}
		/**
		 * @return the reason_code_
		 */
		public BundleProtocol.status_report_reason_t reason() {
			return reason_code_;
		}
		/**
		 * @param reasonCode the reason_code_ to set
		 */
		public void set_reason(BundleProtocol.status_report_reason_t reason) {
			reason_code_ = reason;
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
		 * @return the receipt_dt_
		 */
		public DTNTime receipt_dt() {
			return receipt_dt_;
		}
		/**
		 * @param receiptDt the receipt_dt_ to set
		 */
		public void set_receipt_dt(DTNTime receipt_dt) {
			receipt_dt_ = receipt_dt;
		}
		/**
		 * @return the custody_dt_
		 */
		public DTNTime custody_dt() {
			return custody_dt_;
		}
		/**
		 * @param custodyDt the custody_dt_ to set
		 */
		public void set_custody_dt(DTNTime custody_dt) {
			custody_dt_ = custody_dt;
		}
		/**
		 * @return the forwarding_dt_
		 */
		public DTNTime forwarding_dt() {
			return forwarding_dt_;
		}
		/**
		 * @param forwardingDt the forwarding_dt_ to set
		 */
		public void set_forwarding_dt(DTNTime forwarding_dt) {
			forwarding_dt_ = forwarding_dt;
		}
		/**
		 * @return the delivery_dt_
		 */
		public DTNTime delivery_dt() {
			return delivery_dt_;
		}
		/**
		 * @param deliveryDt the delivery_dt_ to set
		 */
		public void set_delivery_dt(DTNTime delivery_dt) {
			delivery_dt_ = delivery_dt;
		}
		/**
		 * @return the deletion_tv_
		 */
		public DTNTime deletion_dt() {
			return deletion_dt_;
		}
		/**
		 * @param deletionTv the deletion_tv_ to set
		 */
		public void set_deletion_dt(DTNTime deletion_dt) {
			deletion_dt_ = deletion_dt;
		}
		/**
		 * @return the orig_creation_tv_
		 */
		public BundleTimestamp orig_creation_ts() {
			return orig_creation_tr_;
		}
		/**
		 * @param origCreationTv the orig_creation_tv_ to set
		 */
		public void set_orig_creation_ts(BundleTimestamp orig_creation_tv) {
			orig_creation_tr_ = orig_creation_tv;
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
		public void set_orig_source_eid(EndpointID orig_source_eid) {
			orig_source_eid_ = orig_source_eid;
		}
    };

    /**
     * "Constructor-like function that fills in the bundle payload
     * buffer with the appropriate status report format." [DTN2]
     *
     */
    public static Bundle create_status_report( final Bundle orig_bundle,
                                     final EndpointID source,
                                     byte          status_flag_byte,
                                     BundleProtocol.status_report_reason_t          reason)
    {
    	Bundle bundle = new Bundle(location_t.MEMORY);
    	bundle.source().assign(source);
        if (orig_bundle.replyto().equals(EndpointID.NULL_EID())){
            bundle.dest().assign(orig_bundle.source());
        } else {
            bundle.dest().assign(orig_bundle.replyto());
        }
        bundle.replyto().assign(EndpointID.NULL_EID());
        bundle.custodian().assign(EndpointID.NULL_EID());
        
        bundle.set_is_admin(true);

        bundle.set_expiration(orig_bundle.expiration());


        EndpointID orig_source = orig_bundle.source();

        int sdnv_encoding_len = 0;  
        int report_length = 0;
        
       
        //
        // "Structure of bundle status reports:
        //
        // 1 byte Admin Payload type and flags
        // 1 byte Status Flags
        // 1 byte Reason Code
        // SDNV   [Fragment Offset (if present)]
        // SDNV   [Fragment Length (if present)]
        // SDNVx2 Time of {receipt/custody /forwarding/delivery/deletion}
        //        of bundle X
        // SDNVx2 Copy of bundle X's Creation Timestamp
        // SDNV   Length of X's source endpoint ID
        // variable   Source endpoint ID of bundle X" [DTN2]


        // "the non-optional, fixed-length fields above:" [DTN2]
        report_length = 1 + 1 + 1;

        // "the 2 SDNV fragment fields:" [DTN2]
        if (orig_bundle.is_fragment()) {
            report_length += SDNV.encoding_len(orig_bundle.frag_offset());
            report_length += SDNV.encoding_len(orig_bundle.orig_length());
        }

        // "Time field, set to the current time (with no nano-second
        // accuracy defined at all)" [DTN2]
        DTNTime now = new DTNTime();
        
        if ( (status_flag_byte & flag_t.STATUS_RECEIVED.getCode()) > 0 )
        {
             report_length += DTNTime.SDNV_encoding_len(now);
        }
        
        if ((status_flag_byte & flag_t.STATUS_CUSTODY_ACCEPTED.getCode()) > 0 )
        {
        	 report_length += DTNTime.SDNV_encoding_len(now);
        }
        
        if ((status_flag_byte & flag_t.STATUS_FORWARDED.getCode()) > 0 )
        {
        	 report_length += DTNTime.SDNV_encoding_len(now);
        }
        
        if ((status_flag_byte & flag_t.STATUS_DELIVERED.getCode()) > 0 )
        {
        	 report_length += DTNTime.SDNV_encoding_len(now);
        }
        
        if ((status_flag_byte & flag_t.STATUS_DELETED.getCode()) > 0 )
        {
        	 report_length += DTNTime.SDNV_encoding_len(now);
        }
        
        // "the BundleTimestamp of original bundle" [DTN2]
        report_length += BundleTimestamp.SDNV_encoding_len(orig_bundle.creation_ts());
        

        // |the Source Endpoint ID:" [DTN2]
        report_length += SDNV.encoding_len(orig_source.length()) +
                         orig_source.length();

        
        
       

        //
        // "Done calculating length, now create the report payload" [DTN2]
        //
        IByteBuffer bp = new SerializableByteBuffer(report_length);
        bp.rewind();
        int len = report_length;
        
        // "Admin Payload Type and flags Byte" [DTN2]
        byte type_and_flags_byte = (byte)(BundleProtocol.admin_record_type_t.ADMIN_STATUS_REPORT.getCode() << 4);
        if (orig_bundle.is_fragment()) {
        	type_and_flags_byte |= BundleProtocol.admin_record_flags_t.ADMIN_IS_FRAGMENT.getCode();
        }
        bp.put(type_and_flags_byte);
        len--;
        
        // "Status Flags Byte" [DTN2]
        bp.put(status_flag_byte);
        len--;

        // "Reason Code Byte" [DTN2]
        bp.put(reason.getCode());
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

        // "The optional DTNTimes" [DTN2]
        if ( (status_flag_byte & flag_t.STATUS_RECEIVED.getCode()) > 0 )
        {
        	  sdnv_encoding_len = DTNTime.SDNV_encoding_len(now);
              assert(sdnv_encoding_len > 0);
              DTNTime.encodeSDNV(now, bp);
              len -= sdnv_encoding_len;
        }
        
        if ((status_flag_byte & flag_t.STATUS_CUSTODY_ACCEPTED.getCode()) > 0 )
        {
        	sdnv_encoding_len = DTNTime.SDNV_encoding_len(now);
            assert(sdnv_encoding_len > 0);
            DTNTime.encodeSDNV(now, bp);
            len -= sdnv_encoding_len;
        }
        
        if ((status_flag_byte & flag_t.STATUS_FORWARDED.getCode()) > 0 )
        {
        	sdnv_encoding_len = DTNTime.SDNV_encoding_len(now);
            assert(sdnv_encoding_len > 0);
            DTNTime.encodeSDNV(now, bp);
            len -= sdnv_encoding_len;
        }
        
        if ((status_flag_byte & flag_t.STATUS_DELIVERED.getCode()) > 0 )
        {
        	sdnv_encoding_len = DTNTime.SDNV_encoding_len(now);
            assert(sdnv_encoding_len > 0);
            DTNTime.encodeSDNV(now, bp);
            len -= sdnv_encoding_len;
        }
        
        if ((status_flag_byte & flag_t.STATUS_DELETED.getCode()) > 0 )
        {
        	sdnv_encoding_len = DTNTime.SDNV_encoding_len(now);
            assert(sdnv_encoding_len > 0);
            DTNTime.encodeSDNV(now, bp);
            len -= sdnv_encoding_len;
        }
        
      

        // "Copy of bundle X's Creation Timestamp" [DTN2]
        sdnv_encoding_len =  BundleTimestamp.SDNV_encoding_len(orig_bundle.creation_ts());
        assert(sdnv_encoding_len > 0);
        BundleTimestamp.encodeSDNV(orig_bundle.creation_ts(), bp);
        len -= sdnv_encoding_len;
        
        // "The 2 Endpoint ID fields:" [DTN2]
        
        // "First put the length in SDNV" [DTN2]
        sdnv_encoding_len = SDNV.encode(orig_source.length(), bp, len);
        assert(sdnv_encoding_len > 0);
        len -= sdnv_encoding_len;
        
        
        assert(len == orig_source.length());
        // "Put the source EID as String" [DTN2]
        bp.put(orig_source.byte_array());
        
        bp.rewind();
         
        // Finished generating the payload
        
        bundle.payload().set_data(bp, report_length);
        return bundle;
    }

    
    /**
     * "Parse a byte stream containing a Status Report Payload and
     * store the fields in the given struct. Returns false if parsing
     * failed." [DTN2]
     */
    private static boolean parse_status_report(data_t data,
                                    IByteBuffer bp, int len)
    {
    	 // "1 byte Admin Payload Type + Flags:" [DTN2]
    	byte type_flag_byte = bp.get();
        if (len < 1) { return false; }
        data.admin_type_  = admin_record_type_t.get((byte)(type_flag_byte >> 4));
        data.admin_flags_ = (byte)(type_flag_byte & 0xf);
        
        len--;

        // "validate the admin type" [DTN2]
        if (data.admin_type_ != BundleProtocol.admin_record_type_t.ADMIN_STATUS_REPORT) {
            return false;
        }

        // "1 byte Status Flags:" [DTN2]
        if (len < 1) { return false; }
        data.status_flags_ = bp.get();
        len--;
        
        // "1 byte Reason Code:" [DTN2]
        if (len < 1) { return false; }
        data.reason_code_ = status_report_reason_t.get(bp.get());
        len--;
        
        // "Fragment SDNV Fields (offset & length), if present:" [DTN2]
        if ( (data.admin_flags_ & BundleProtocol.admin_record_flags_t.ADMIN_IS_FRAGMENT.getCode()) > 0) {
            
        	int[] value = new int[1];
        	int sdnv_bytes = SDNV.decode(bp, len, value);
            if (sdnv_bytes == -1) { return false; }
            data.orig_frag_offset_ = value[0];
            len -= sdnv_bytes;
            
            sdnv_bytes = SDNV.decode(bp, len, value);
            if (sdnv_bytes == -1) { return false; }
            data.orig_frag_length_ = value[0];
            len -= sdnv_bytes;
        }

        // "The 6 Optional ACK Timestamps:" [DTN2]
        
     
        if ((data.status_flags_ & BundleStatusReport.flag_t.STATUS_RECEIVED.getCode()) > 0) {
        	DTNTime ret = new DTNTime();
        	int sdnv_len  = DTNTime.SDNV_decoding_len(bp);
        	
        	if (sdnv_len == -1) return false;
        	else
        	{
        		DTNTime.decodeSDNV(ret, bp);
        	
        	}
        	len -= sdnv_len;
        	data.set_receipt_dt(ret);
        	
        }

        if ((data.status_flags_ & BundleStatusReport.flag_t.STATUS_CUSTODY_ACCEPTED.getCode()) > 0) {
          	DTNTime ret = new DTNTime();
        	int sdnv_len  = DTNTime.SDNV_decoding_len(bp);
        	
        	if (sdnv_len == -1) return false;
        	else
        	{
        		DTNTime.decodeSDNV(ret, bp);
        	
        	}
        	len -= sdnv_len;
        	data.set_custody_dt(ret);
         }
        
        if ((data.status_flags_ & BundleStatusReport.flag_t.STATUS_FORWARDED.getCode()) > 0) {
          	DTNTime ret = new DTNTime();
        	int sdnv_len  = DTNTime.SDNV_decoding_len(bp);
        	
        	if (sdnv_len == -1) return false;
        	else
        	{
        		DTNTime.decodeSDNV(ret, bp);
        	
        	}
        	len -= sdnv_len;
        	data.set_forwarding_dt(ret);
         }
        
        if ((data.status_flags_ & BundleStatusReport.flag_t.STATUS_DELIVERED.getCode()) > 0) {
           DTNTime ret = new DTNTime();
       	int sdnv_len  = DTNTime.SDNV_decoding_len(bp);
        	
        	if (sdnv_len == -1) return false;
        	else
        	{
        		DTNTime.decodeSDNV(ret, bp);
        	
        	}
        	len -= sdnv_len;
        
        	data.set_forwarding_dt(ret);
         }
        
        if ((data.status_flags_ & BundleStatusReport.flag_t.STATUS_DELETED.getCode()) > 0) {
        	DTNTime ret = new DTNTime();
        	int sdnv_len  = DTNTime.SDNV_decoding_len(bp);
        	
        	if (sdnv_len == -1) return false;
        	else
        	{
        		DTNTime.decodeSDNV(ret, bp);
        	
        	}
        	len -= sdnv_len;
        
        	data.set_deletion_dt(ret);
       
         }
        
        // "Bundle Creation Timestamp" [DTN2]
        int ts_len = BundleTimestamp.SDNV_decoding_len(bp);
        if (ts_len < 0) { return false; }
        BundleTimestamp.decodeSDNV(data.orig_creation_ts(), bp);
        len -= ts_len;
        
        // "EID of Bundle" [DTN2]
        int[] EID_len = new int[1];
        int num_bytes = SDNV.decode(bp, len, EID_len);
        if (num_bytes == -1) { return false; }
        len -= num_bytes;

        if (len != EID_len[0]) { return false; }
        
        byte[] eid_byte_array = new byte[EID_len[0]];
        bp.get(eid_byte_array);
        boolean ok = data.orig_source_eid_.assign(eid_byte_array);
        if (!ok) {
            return false;
        }
        
        return true;
    }


    /**
     * "Parse the payload of the given bundle into the given data_t
     * Returns false if the bundle is not a well formed status report." [DTN2]
     * @param data the output class
     * @param bundle input Bundle to parse
     */
    public static boolean parse_status_report(data_t data,
                                    final Bundle bundle)
    {
    	BundleProtocol.admin_record_type_t[] admin_type = new BundleProtocol.admin_record_type_t[1];
        if (! BundleProtocol.get_admin_type(bundle, admin_type)) {
            return false;
        }

        if (admin_type[0] != BundleProtocol.admin_record_type_t.ADMIN_STATUS_REPORT) {
            return false;
        }

        int payload_len = bundle.payload().length();
        if (payload_len > 16384) {
            Log.e(TAG, String.format(
                      "status report length %d too big to be parsed!!",
                      payload_len));
            return false;
        }

        IByteBuffer buf = new SerializableByteBuffer(payload_len);
        bundle.payload().read_data(0, payload_len, buf);
        return parse_status_report(data, buf, payload_len);
    }

 
};

