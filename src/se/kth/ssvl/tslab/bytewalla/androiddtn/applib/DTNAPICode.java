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
package se.kth.ssvl.tslab.bytewalla.androiddtn.applib;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * The DTNAPICode for reporting error for the BP Application
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public class DTNAPICode {

	 /**
    * DTN API error codes base
    */
	public final static int DTN_ERRBASE = 128;
	
	/**
	 * The enum for DTNAPICode for reporting error for the BP Application
	 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
	 */
	public enum dtn_api_status_report_code
	{
		DTN_SUCCESS     ("success"                    , 0), 		/* ok */
		 DTN_EINVAL     ("DTN_EINVAL"                 , DTN_ERRBASE+1), /* invalid argument */
		 DTN_EXDR       ("error in xdr routines"      , DTN_ERRBASE+2), /* error in xdr routines */
		 DTN_ECOMM   	("error in ipc communication" , DTN_ERRBASE+3), /* error in ipc communication */
		 DTN_ECONNECT 	("error connecting to server" , DTN_ERRBASE+4), /* error connecting to server */
		 DTN_ETIMEOUT 	("operation timed out"        , DTN_ERRBASE+5), /* operation timed out */
		 DTN_ESIZE 	    ("payload too large"          , DTN_ERRBASE+6), /* payload / eid too large */
		 DTN_ENOTFOUND 	("not found"                  , DTN_ERRBASE+7), /* not found (e.g. reg) */
		 DTN_EINTERNAL 	("internal error"             , DTN_ERRBASE+8), /* misc. internal error */
		 DTN_EINPOLL 	("illegal operation called "+
		 		         " after dtn_poll"            , DTN_ERRBASE+9), /* illegal op. called after dtn_poll */
		 DTN_EBUSY 	    ("registration already in use", DTN_ERRBASE+10), /* registration already in use */
		 DTN_EVERSION   ("ipc version mismatch"       , DTN_ERRBASE+11), /* ipc version mismatch */
		 DTN_EMSGTYPE   ("unknown ipc message type"   , DTN_ERRBASE+12), /* unknown message type */
		 DTN_ENOSPACE	("no storage space"           , DTN_ERRBASE+13), /* no storage space */
		 DTN_EHANDLE_OPENNED("DTN handle already open"           , DTN_ERRBASE+14), /* handle already open */
		 DTN_EHANDLE_INVALID	("DTN handle is invalid or not openned"           , DTN_ERRBASE+15), /* handle is invalid  or not openned*/
		 DTN_EHANDLE_NOT_BOUND_REG 	    ("handle is not bound to registration", DTN_ERRBASE+16); /* handle is not bound to registration */


	     private static final Map<dtn_api_status_report_code,String> captionLookup 
	          = new HashMap<dtn_api_status_report_code,String>();
	     private static final Map<dtn_api_status_report_code,Integer> codeLookup 
	     = new HashMap<dtn_api_status_report_code,Integer>();
	     

	     static {
	          for(dtn_api_status_report_code event : EnumSet.allOf(dtn_api_status_report_code.class))
	          {
	        	  captionLookup.put(event, event.getCaption());
	        	  codeLookup.put(event, event.getCode());
	          }
	     }

	     private String caption;
	     private int code = -1; // -1 = undefined

	     private dtn_api_status_report_code(String caption) {
	          this.caption = caption;
	     }
	     
	     private dtn_api_status_report_code(String caption, int code) {
	         this.caption = caption;
	         this.code    = code;
	    }

	     
	     
	     
	     public String getCaption() { return caption; }
	     
	     public String event_to_str() { return caption; }
	     
	     public Integer getCode() { return code; }

	     public static String get(dtn_api_status_report_code event) { 
	          return captionLookup.get(event); 
	     }
	}
	
	
	
	
	/**
	 * Bundle Status Report "Reason Code" flags
	 */
	public static enum dtn_reg_flags_t {
		DTN_REGID_NONE(0),
		DTN_REG_DROP(1),
		DTN_REG_DEFER(2)
	

		;

		private static final Map<Integer, dtn_reg_flags_t> lookup = new HashMap<Integer, dtn_reg_flags_t>();

		static {
			for (dtn_reg_flags_t s : EnumSet
					.allOf(dtn_reg_flags_t.class))
				lookup.put(s.getCode(), s);
		}

		private int code;

		private dtn_reg_flags_t(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static dtn_reg_flags_t get(int code) {
			return lookup.get(code);
		}
	}
	
	
	/**
	 * Bundle Status Report "Reason Code" flags
	 */
	public static enum dtn_bundle_priority_t {
		COS_BULK       (0),
		COS_NORMAL     (1),
		COS_EXPEDITED  (2),
		COS_RESERVED   (3);

		private static final Map<Integer, dtn_bundle_priority_t> lookup = new HashMap<Integer, dtn_bundle_priority_t>();

		static {
			for (dtn_bundle_priority_t s : EnumSet
					.allOf(dtn_bundle_priority_t.class))
				lookup.put(s.getCode(), s);
		}

		private int code;

		private dtn_bundle_priority_t(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static dtn_bundle_priority_t get(int code) {
			return lookup.get(code);
		}
	}
	
	

	/**
	 * Bundle delivery option flags
	 */
	public static enum dtn_bundle_delivery_opts_t {
		DOPTS_NONE            (0),
		DOPTS_CUSTODY         (1),
		DOPTS_DELIVERY_RCPT   (2),
		DOPTS_RECEIVE_RCPT    (4),
		DOPTS_FORWARD_RCPT    (8),
		DOPTS_CUSTODY_RCPT    (16),
		DOPTS_DELETE_RCPT     (32),
		DOPTS_SINGLETON_DEST  (64),
		DOPTS_MULTINODE_DEST  (128),
		DOPTS_DO_NOT_FRAGMENT (256);

		private static final Map<Integer, dtn_bundle_delivery_opts_t> lookup = new HashMap<Integer, dtn_bundle_delivery_opts_t>();

		static {
			for (dtn_bundle_delivery_opts_t s : EnumSet
					.allOf(dtn_bundle_delivery_opts_t.class))
				lookup.put(s.getCode(), s);
		}

		private int code;

		private dtn_bundle_delivery_opts_t(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static dtn_bundle_delivery_opts_t get(int code) {
			return lookup.get(code);
		}
	}
	
	

	/**
	 * Extension block flags.
	 */
	public static enum dtn_extension_block_flags_t {
		BLOCK_FLAG_NONE          (0),
		BLOCK_FLAG_REPLICATE     (1),
		BLOCK_FLAG_REPORT        (2),
		BLOCK_FLAG_DELETE_BUNDLE (4),
		BLOCK_FLAG_LAST          (8),
		BLOCK_FLAG_DISCARD_BLOCK (16),
		BLOCK_FLAG_UNPROCESSED   (32);

		private static final Map<Integer, dtn_extension_block_flags_t> lookup = new HashMap<Integer, dtn_extension_block_flags_t>();

		static {
			for (dtn_extension_block_flags_t s : EnumSet
					.allOf(dtn_extension_block_flags_t.class))
				lookup.put(s.getCode(), s);
		}

		private int code;

		private dtn_extension_block_flags_t(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static dtn_extension_block_flags_t get(int code) {
			return lookup.get(code);
		}
	}
	
	
	/**
	 * "Reason Code" flags
	 */
	public static enum dtn_status_report_reason_t {
		REASON_NO_ADDTL_INFO(0x00), 
		REASON_LIFETIME_EXPIRED(0x01),
		REASON_FORWARDED_UNIDIR_LINK(0x02), 
		REASON_TRANSMISSION_CANCELLED(0x03), 
		REASON_DEPLETED_STORAGE(0x04), 
		REASON_ENDPOINT_ID_UNINTELLIGIBLE(0x05), 
		REASON_NO_ROUTE_TO_DEST(0x06),
		REASON_NO_TIMELY_CONTACT(0x07), 
		REASON_BLOCK_UNINTELLIGIBLE(0x08)
		

		;

		private static final Map<Integer, dtn_status_report_reason_t> lookup = new HashMap<Integer, dtn_status_report_reason_t>();

		static {
			for (dtn_status_report_reason_t s : EnumSet
					.allOf(dtn_status_report_reason_t.class))
				lookup.put(s.getCode(), s);
		}

		private int code;

		private dtn_status_report_reason_t(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static dtn_status_report_reason_t get(int code) {
			return lookup.get(code);
		}
	}
	
	/**
	 * Status flags
	 */
	public static enum dtn_status_report_flags_t {
		STATUS_RECEIVED           (0x01),
		STATUS_CUSTODY_ACCEPTED   (0x02),
		STATUS_FORWARDED          (0x04),
		STATUS_DELIVERED          (0x08),
		STATUS_DELETED            (0x10),
		STATUS_ACKED_BY_APP       (0x20);

		private static final Map<Integer, dtn_status_report_flags_t> lookup = new HashMap<Integer, dtn_status_report_flags_t>();

		static {
			for (dtn_status_report_flags_t s : EnumSet
					.allOf(dtn_status_report_flags_t.class))
				lookup.put(s.getCode(), s);
		}

		private int code;

		private dtn_status_report_flags_t(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static dtn_status_report_flags_t get(int code) {
			return lookup.get(code);
		}
	}
	
	

	/**
	 * The payload location specified as followed:
	 *     DTN_PAYLOAD_MEM         - payload contents in memory buffer
	 *     DTN_PAYLOAD_FILE        - payload contents in file
	 */
	public static enum dtn_bundle_payload_location_t {
		DTN_PAYLOAD_FILE  (0),
		DTN_PAYLOAD_MEM   (1);
	

		private static final Map<Integer, dtn_bundle_payload_location_t> lookup = new HashMap<Integer, dtn_bundle_payload_location_t>();

		static {
			for (dtn_bundle_payload_location_t s : EnumSet
					.allOf(dtn_bundle_payload_location_t.class))
				lookup.put(s.getCode(), s);
		}

		private int code;

		private dtn_bundle_payload_location_t(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static dtn_bundle_payload_location_t get(int code) {
			return lookup.get(code);
		}
	}
	
}
