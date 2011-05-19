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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Iterator;

import se.kth.ssvl.tslab.bytewalla.androiddtn.DTNService;
import se.kth.ssvl.tslab.bytewalla.androiddtn.R;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPICode.dtn_api_status_report_code;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPICode.dtn_bundle_delivery_opts_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPICode.dtn_bundle_payload_location_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPICode.dtn_reg_flags_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPICode.dtn_status_report_reason_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNBundleID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNBundlePayload;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNBundleSpec;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNBundleStatusReport;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNBundleTimestamp;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNEndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNHandle;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNRegistrationInfo;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNTime;
import se.kth.ssvl.tslab.bytewalla.androiddtn.apps.DTNAPIFailException;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.Bundle;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleDaemon;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleStatusReport;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundlePayload.location_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleAcceptRequest;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.BundleReceivedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.RegistrationAddedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.RegistrationRemovedEvent;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event.event_source_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointIDPattern;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.reg.APIRegistration;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.reg.Registration;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.reg.RegistrationList;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.reg.RegistrationTable;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.reg.Registration.failure_action_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.storage.RegistrationStore;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.Lock;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.MsgBlockingQueue;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.BufferHelper;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.List;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.SerializableByteBuffer;
import android.content.Context;
import android.os.Binder;
import android.util.Log;


/**
 *  An DTNAPI implementation using Android Binder
 *  @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public class DTNAPIBinder extends Binder implements DTNAPI {

	/**
	 * TAG String for Android logging system
	 */
	private static String TAG = "DTNAPIBinder";

	/**
	 *  List for holding DTN Handles registred with this binder
	 */
	private List<DTNHandle> handles_;
	
	/**
	 * Lock for manage concurrent accessing
	 */
	private Lock lock_;
	
	/**
	 *  a hashmap of DTNHandle and list of registration id bound to one or many DTNHandles
	 */
	private HashMap<DTNHandle, List<Integer>> bindings_;

	/**
	 * Constructor by initializing all the Data Structures inside
	 */
	public DTNAPIBinder() {
		super();
		handles_ = new List<DTNHandle>();

		lock_ = new Lock();
		bindings_ = new HashMap<DTNHandle, List<Integer>>();
	}

	/**
	 * An implementation of the DTNAPI's dtn_build_local_eid
	 * 
	 * @see
	 * se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPI#dtn_build_local_eid
	 * (se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNHandle,
	 * se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNEndpointID,
	 * se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNServiceTag)
	 */
	
	public dtn_api_status_report_code dtn_build_local_eid(DTNHandle handle,
			DTNEndpointID localEid, String service) {

		if (!is_handle_valid(handle))
			return dtn_api_status_report_code.DTN_EHANDLE_INVALID;

		EndpointID eid = new EndpointID(BundleDaemon.getInstance().local_eid());
		if (eid.append_service_tag(service) == false) {
			Log
					.e(TAG,
							"DTNAPIBinder:dtn_build_local_eid error appending service tag");
			return dtn_api_status_report_code.DTN_EINTERNAL;
		}

		Log.d(TAG, "Set EID response to " + eid.toString() + " success");
		return dtn_api_status_report_code.DTN_SUCCESS;
	}

	/**
	 * Check the validity of input Handle by checking the local list of DTNHandles
	 * @param handle
	 * @return whether the DTNHandle of openned before by the API or not
	 */
	private boolean is_handle_valid(DTNHandle handle) {
		lock_.lock();
		try {
			return handles_.contains(handle);

		} finally {
			lock_.unlock();
		}
	}

	/**
	 * An implementation of the DTNAPI's dtn_open
	 * @see se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPI#dtn_open
	 */
	
	public dtn_api_status_report_code dtn_open(DTNHandle handle) {
		lock_.lock();
		try {
			if (handles_.contains(handle))
				return dtn_api_status_report_code.DTN_EHANDLE_OPENNED;
			else {
				handles_.add(handle);
				handle.set_openned(true);
				return dtn_api_status_report_code.DTN_SUCCESS;
			}
		} finally {
			lock_.unlock();
		}
	}

	/**
	 * An implementation of the DTNAPI's dtn_close
	 * @see se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPI#dtn_close
	 */
	
	public dtn_api_status_report_code dtn_close(DTNHandle handle) {

		if (!is_handle_valid(handle))
			return dtn_api_status_report_code.DTN_EHANDLE_INVALID;
		else {
			handle.set_openned(false);

			lock_.lock();
			try {
				handles_.remove(handle);
				return dtn_api_status_report_code.DTN_SUCCESS;
			} finally {
				lock_.unlock();
			}
		}

	}

	/**
	 * An implementation of the DTNAPI's dtn_find_registration
	 * 
	 * @see
	 * se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPI#dtn_find_registration
	 * (se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNHandle,
	 * se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNEndpointID,
	 * se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNRegistrationID)
	 */
	
	public dtn_api_status_report_code dtn_find_registrations(DTNHandle handle,
			DTNEndpointID eid, List<Integer> registration_ids) {

		EndpointID _eid = new EndpointID(eid.uri());
		if (!_eid.valid()) {
			return dtn_api_status_report_code.DTN_EINTERNAL;

		}

		RegistrationList regs = new RegistrationList();
		int regs_count = BundleDaemon.getInstance().reg_table().get_matching(
				_eid, regs);

		if (regs_count == 0) {
			return dtn_api_status_report_code.DTN_ENOTFOUND;
		}

		Iterator<Registration> iter = regs.iterator();
		while (iter.hasNext()) {
			Registration reg = iter.next();
			registration_ids.add(new Integer(reg.regid()));
		}

		return dtn_api_status_report_code.DTN_SUCCESS;
	}

	/**
	 * 
	 * An implementation of the DTNAPI's dtn_recv
	 * 
	 * @see
	 * se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPI#dtn_recv(se.kth.
	 * ssvl.tslab.bytewalla.androiddtn.applib.types.DTNHandle,
	 * se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNBundleSpec,
	 * se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPICode.
	 * dtn_bundle_payload_location_t,
	 * se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNBundlePayload,
	 * int)
	 */
	
	public dtn_api_status_report_code dtn_recv(DTNHandle handle, int regid,
			DTNBundleSpec spec, 
			DTNBundlePayload dtn_payload, int timeout) throws InterruptedException {

		dtn_bundle_payload_location_t location = dtn_payload.location();
		
		Registration reg = BundleDaemon.getInstance().reg_table().get(regid);
		if (reg == null) return dtn_api_status_report_code.DTN_ENOTFOUND;
		if (!is_bound(handle, regid)) return dtn_api_status_report_code.DTN_EHANDLE_NOT_BOUND_REG;
		
		
		if (!(reg instanceof APIRegistration)) return dtn_api_status_report_code.DTN_EINTERNAL;
		
		APIRegistration api_reg = (APIRegistration) reg;
		
		Bundle b;
		try {
			b = api_reg.wait_for_Bundle(timeout);
		} catch (InterruptedException e) {
			throw e;
		}
		
		if (b == null) return dtn_api_status_report_code.DTN_EINTERNAL;
		
		spec.source().set_uri(b.source().toString());
		spec.dest().set_uri(b.dest().toString());
		
		if (b.replyto() != null)
		{
			spec.set_replyto(new DTNEndpointID(b.replyto().toString()));
		}
		
		spec.set_dopts(0);
		if( b.custody_requested()) 
		
			
	    if (b.custody_requested()) spec.set_dopts( spec.dopts() | dtn_bundle_delivery_opts_t.DOPTS_CUSTODY.getCode());
	    if (b.delivery_rcpt())     spec.set_dopts( spec.dopts() | dtn_bundle_delivery_opts_t.DOPTS_DELIVERY_RCPT.getCode());
	    if (b.receive_rcpt())      spec.set_dopts( spec.dopts() | dtn_bundle_delivery_opts_t.DOPTS_RECEIVE_RCPT.getCode());
	    if (b.forward_rcpt())      spec.set_dopts( spec.dopts() | dtn_bundle_delivery_opts_t.DOPTS_FORWARD_RCPT.getCode());
	    if (b.custody_rcpt())      spec.set_dopts( spec.dopts() | dtn_bundle_delivery_opts_t.DOPTS_CUSTODY_RCPT.getCode());
	    if (b.deletion_rcpt())     spec.set_dopts( spec.dopts() | dtn_bundle_delivery_opts_t.DOPTS_DELETE_RCPT.getCode());

	    spec.set_expiration( b.expiration());
	    DTNBundleTimestamp spec_creation_ts = spec.creation_ts();
	    
	    spec_creation_ts.set_secs(b.creation_ts().seconds());
	    spec_creation_ts.set_seqno(b.creation_ts().seqno());
	    
	    spec.set_delivery_regid(reg.regid());
	    
		
	    

	    int payload_len = b.payload().length();
	    dtn_payload.set_length(payload_len);
	    if (location == dtn_bundle_payload_location_t.DTN_PAYLOAD_MEM && payload_len > DTN_MAX_BUNDLE_MEM)
	    {
	        Log.d(TAG, String.format("app requested memory delivery but payload is too big (%d bytes)... " +
	                  "using files instead",
	                  payload_len));
	        location = dtn_bundle_payload_location_t.DTN_PAYLOAD_FILE;
	    }

	    if (location == dtn_bundle_payload_location_t.DTN_PAYLOAD_MEM) {
	        // the app wants the payload in memory
	    	
	        if (payload_len != 0) {
	            
	          b.payload().read_data(0, payload_len, dtn_payload.buf());
	        } 
	        
	    } else if (location == dtn_bundle_payload_location_t.DTN_PAYLOAD_FILE) {
	    	
	    	
	    	Context context = DTNService.context();
	    	
	    	String api_temp_dir_prefix = context.getResources().getString(R.string.DTNAPITempFilePrefix);
	    	File api_temp_dir = context.getDir(api_temp_dir_prefix, Context.MODE_PRIVATE );
	    	
	    	File payload_file = null;
			try {
				payload_file = File.createTempFile(String.format("reg_%d_bundle_%d_payload_", 
						                           regid , b.bundleid())
						          		 , ".payload.dat", api_temp_dir);
		
	    	
	    		        
	        b.payload().copy_to_file(payload_file);
	        dtn_payload.set_file(payload_file);
	        
			} catch (IOException e) {
				Log.e(TAG, "Create payload file fail with " + e.getMessage());
			}
	        
	        
	    } else {
	        Log.e("payload location %s not understood", location.toString());
	        return dtn_api_status_report_code.DTN_EINVAL;
	    }

	    dtn_payload.set_location(location);
	    
	    
	    if (b.is_admin())
	    {
	    	spec.set_is_admin(true);
	    	
	    	
	    /*
	     * If the bundle is a status report, parse it and copy out the
	     * data into the status report.
	     */
		    BundleStatusReport.data_t sr_data = new BundleStatusReport.data_t(); 
		    if (BundleStatusReport.parse_status_report(sr_data, b))
		    {
		        DTNBundleStatusReport dtn_status_report = new DTNBundleStatusReport();
		        dtn_payload.set_status_report(dtn_status_report);
		        
		        
		        dtn_status_report.set_bundle_id(b.bundleid());
		        dtn_status_report.set_reason(dtn_status_report_reason_t.get(sr_data.reason().getCode()));
		        		        
		        dtn_status_report.set_flags(sr_data.admin_flags());
		        dtn_status_report.set_receipt_dt(new DTNTime(sr_data.receipt_dt().seconds(), sr_data.receipt_dt().nanoseconds()));
		        dtn_status_report.set_custody_dt(new DTNTime(sr_data.custody_dt().seconds(), sr_data.custody_dt().nanoseconds()));
		        dtn_status_report.set_forwarding_dt(new DTNTime(sr_data.forwarding_dt().seconds(), sr_data.forwarding_dt().nanoseconds()));
		        dtn_status_report.set_delivery_dt(new DTNTime(sr_data.delivery_dt().seconds(), sr_data.delivery_dt().nanoseconds()));
		        dtn_status_report.set_deletion_dt(new DTNTime(sr_data.deletion_dt().seconds(), sr_data.deletion_dt().nanoseconds()));
	
		       dtn_status_report.set_ack_by_app_dt(new DTNTime());
		    	
		    }
		    
	    }
		return dtn_api_status_report_code.DTN_SUCCESS;
		
	}

	/**
	 * An implementation of the DTNAPI's dtn_register
	 * 
	 * @see
	 * se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPI#dtn_register(se.
	 * kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNHandle,
	 * se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNRegistrationInfo,
	 * se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNRegistrationID)
	 */
	
	public dtn_api_status_report_code dtn_register(DTNHandle handle,
			DTNRegistrationInfo reginfo, int[] newregid) throws DTNAPIFailException {

		EndpointID endpoint = new EndpointID(reginfo.endpoint().uri());

		if (!endpoint.valid()) {
			Log.e(TAG, String.format("invalid endpoint id in register: '%s'",
					reginfo.endpoint().uri()));
			return dtn_api_status_report_code.DTN_EINVAL;
		}

		// registration flags are a bitmask currently containing:
		//
		// [unused] [3 bits session flags] [2 bits failure action]

		dtn_reg_flags_t failure_action = dtn_reg_flags_t
				.get(reginfo.flags() & 0x3);

		int other_flags = reginfo.flags() & ~0x1f;
		if (other_flags != 0) {
			Log.e(TAG, String.format("invalid registration flags %s", reginfo
					.flags()));
			return dtn_api_status_report_code.DTN_EINVAL;
		}

		
		int regid = RegistrationStore.getInstance().next_regid();
		APIRegistration reg = new APIRegistration(regid, new EndpointIDPattern(
				endpoint), failure_action_t.get(failure_action.getCode()),
				reginfo.expiration());

		
		List<Integer> regids = new List<Integer>();
		handle.get_lock().lock();
		try {
			bindings_.put(handle, regids);
		} finally {
			handle.get_lock().unlock();
		}
		if (!reginfo.init_passive()) {

			regids.add(new Integer(regid));
			reg.set_active(true);
		}
		MsgBlockingQueue<Integer> notifier_ = new MsgBlockingQueue<Integer>(1);
		BundleDaemon.getInstance().post_and_wait(
				new RegistrationAddedEvent(reg, event_source_t.EVENTSRC_APP),
				notifier_, -1, true);

		// fill the data with new regid before return
		newregid[0] = regid;

		return dtn_api_status_report_code.DTN_SUCCESS;
	}

	/**
	 * An implementation of the DTNAPI's dtn_send
	 * 
	 * @see
	 * se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPI#dtn_send(se.kth.
	 * ssvl.tslab.bytewalla.androiddtn.applib.types.DTNHandle,
	 * se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNRegistrationID,
	 * se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNBundleSpec,
	 * se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNBundlePayload,
	 * se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNBundleID)
	 */
	
	public dtn_api_status_report_code dtn_send(DTNHandle handle,
			DTNBundleSpec spec, DTNBundlePayload dtn_payload,
			DTNBundleID dtn_bundle_id) {

		if (!is_handle_valid(handle))
			return dtn_api_status_report_code.DTN_EHANDLE_INVALID;

		Bundle b = new Bundle(location_t.DISK);

		// assign the addressing fields...
		// source and destination are always specified
		b.source().assign(spec.source().toString());
		b.dest().assign(spec.dest().toString());

		// replyto defaults to null
		if (spec.replyto() == null) {
			b.replyto().assign(EndpointID.NULL_EID());
		} else {
			b.replyto().assign(spec.replyto().toString());
		}
		// custodian is always null
		b.custodian().assign(EndpointID.NULL_EID());

		// set the is_singleton bit, first checking if the application
		// specified a value, then seeing if the scheme is known and can
		// therefore determine for itself, and finally, checking the
		// global default
		if ((spec.dopts() & dtn_bundle_delivery_opts_t.DOPTS_SINGLETON_DEST
				.getCode()) > 0) {
			b.set_singleton_dest(true);
		} else if ((spec.dopts() & dtn_bundle_delivery_opts_t.DOPTS_MULTINODE_DEST
				.getCode()) > 0) {
			b.set_singleton_dest(false);
		} else {
			EndpointID.singleton_info_t info;

			if (b.dest().known_scheme()) {
				info = b.dest().is_singleton();

				// all schemes must make a decision one way or the other
				assert (info != EndpointID.singleton_info_t.UNKNOWN);
			} else {
				info = EndpointID.is_singleton_default_;
			}

			switch (info) {
			case UNKNOWN:
				Log.e(TAG, String.format(
						"bundle destination %s in unknown scheme and "
								+ "app did not assert singleton/multipoint", b
								.dest()));
				return dtn_api_status_report_code.DTN_EINVAL;

			case SINGLETON:
				b.set_singleton_dest(true);
				break;

			case MULTINODE:
				b.set_singleton_dest(false);
				break;
			}
		}

		// check the priority code
		switch (spec.priority()) {
		case COS_BULK:
		case COS_NORMAL:
		case COS_EXPEDITED:
			break;
		default:
			Log.e(TAG, String.format("invalid priority level %s", spec
					.priority().toString()));
			return dtn_api_status_report_code.DTN_EINVAL;
		}
		;

		// The bundle's source EID must be either dtn:none or an EID
		// registered at this node so check that now.
		RegistrationTable reg_table = BundleDaemon.getInstance().reg_table();
		RegistrationList unused = new RegistrationList();
		if (b.source().equals(EndpointID.NULL_EID())) {
			// Bundles with a null source EID are not allowed to request reports
			// or
			// custody transfer, and must not be fragmented.
			if (spec.dopts() > 0) {
				Log.e(TAG,
						"bundle with null source EID requested reports and/or "
								+ "custody transfer");
				return dtn_api_status_report_code.DTN_EINVAL;
			}

			b.set_do_not_fragment(true);
		}
		else if (reg_table.get_matching(b.source(), unused) != 0) {
			// Local registration -- don't do anything
		} else if (b.source().subsume(BundleDaemon.getInstance().local_eid())) {
			// Allow source EIDs that subsume the local eid
		} else {
			Log
					.e(
							TAG,
							String
									.format(
											"this node is not a member of the bundle's source EID (%s)",
											b.source().toString()));
			return dtn_api_status_report_code.DTN_EINVAL;
		}

		// delivery options
		if ((spec.dopts() & dtn_bundle_delivery_opts_t.DOPTS_CUSTODY.getCode()) > 0)
			b.set_custody_requested(true);

		if ((spec.dopts() & dtn_bundle_delivery_opts_t.DOPTS_DELIVERY_RCPT
				.getCode()) > 0)
			b.set_delivery_rcpt(true);

		if ((spec.dopts() & dtn_bundle_delivery_opts_t.DOPTS_RECEIVE_RCPT
				.getCode()) > 0)
			b.set_receive_rcpt(true);

		if ((spec.dopts() & dtn_bundle_delivery_opts_t.DOPTS_FORWARD_RCPT
				.getCode()) > 0)
			b.set_forward_rcpt(true);

		if ((spec.dopts() & dtn_bundle_delivery_opts_t.DOPTS_CUSTODY_RCPT
				.getCode()) > 0)
			b.set_custody_rcpt(true);

		if ((spec.dopts() & dtn_bundle_delivery_opts_t.DOPTS_DELETE_RCPT
				.getCode()) > 0)
			b.set_deletion_rcpt(true);

		if ((spec.dopts() & dtn_bundle_delivery_opts_t.DOPTS_DO_NOT_FRAGMENT
				.getCode()) > 0)
			b.set_do_not_fragment(true);

		// expiration time
		b.set_expiration(spec.expiration());

		// validate the bundle metadata
		StringBuffer error_string_buf = new StringBuffer();
		if (!b.validate(error_string_buf)) {
			Log.e(TAG, String.format("bundle validation failed: %s",
					error_string_buf.toString()));
			return dtn_api_status_report_code.DTN_EINVAL;
		}

		// set up the payload, including calculating its length, but don't
		// copy it in yet
		int payload_len = -1;

		switch (dtn_payload.location()) {
		case DTN_PAYLOAD_MEM:
			payload_len = dtn_payload.buf().length;
			break;

		case DTN_PAYLOAD_FILE:

			Log.d(TAG, String.format(
					"dtn_send, getting payload from file name %s", dtn_payload
							.file().getAbsoluteFile()));

			File payload_file = dtn_payload.file();
			
			
			if (!payload_file.exists()) {
				Log.e(TAG, String.format("payload file %s does not exist!",
						dtn_payload.file().getAbsoluteFile()));
				return dtn_api_status_report_code.DTN_EINVAL;
			}

			payload_len = (int) payload_file.length();
			break;

		default:
			Log.e(TAG, String.format("payload.location of %d unknown",
					dtn_payload.location().toString()));
			return dtn_api_status_report_code.DTN_EINVAL;
		}

		// Set an allocate dat for Bundle to be store
		b.payload().set_length(payload_len);

		// before filling in the payload, we first probe the router to
		// determine if there's sufficient storage for the bundle
		boolean result[] = new boolean[1];

		MsgBlockingQueue<Integer> notifier_ = new MsgBlockingQueue<Integer>(1);
		BundleProtocol.status_report_reason_t reason[] = new BundleProtocol.status_report_reason_t[1];
		BundleDaemon.getInstance().post_and_wait(
				new BundleAcceptRequest(b, event_source_t.EVENTSRC_APP, result,
						reason), notifier_, -1, true);

		if (!result[0]) {
			Log.i(TAG, String.format("DTN_SEND bundle not accepted: reason %s",
					reason[0].toString()));

			switch (reason[0]) {
			case REASON_DEPLETED_STORAGE:
				return dtn_api_status_report_code.DTN_ENOSPACE;
			default:
				return dtn_api_status_report_code.DTN_EINTERNAL;
			}
		}

		switch (dtn_payload.location()) {
		case DTN_PAYLOAD_MEM:

			// Set the payload according to byte array inside dtn_payload
			b.payload().set_data(dtn_payload.buf());
			break;

		case DTN_PAYLOAD_FILE:

			FileInputStream in = null;
			try {
				in = new FileInputStream(dtn_payload.file());

				b.payload().set_length(dtn_payload.length());
				// Transfer bytes from in to payload
				java.nio.ByteBuffer temp_buffer = java.nio.ByteBuffer.allocate(32696);
				int offset = 0;
				ReadableByteChannel read_channel = Channels.newChannel(in);
				while (in.available() > 0) {
					
					
					read_channel.read(temp_buffer);
					int read_len = temp_buffer.position();
					temp_buffer.rewind();
					
					IByteBuffer serializable_temp_buffer = new SerializableByteBuffer(read_len);
					BufferHelper.copy_data(serializable_temp_buffer, 0, temp_buffer, 0, read_len);
					b.payload().write_data(serializable_temp_buffer, offset, read_len);
					offset += read_len;
				}
				
				
			} catch (FileNotFoundException e) {
				Log.e(TAG, String.format("payload file %s can't be opened: %s",
						dtn_payload.file().getAbsoluteFile(), e.getMessage()));
			} catch (SecurityException e) {
				Log.e(TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			} 
			catch (Exception e)
			{
				Log.e(TAG, e.getMessage());
			}
			finally {
				try {
					in.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}

			}

			break;

		}

		// Before deliver the bundle fill in data in dtn_bundle_id
		DTNBundleTimestamp dtn_bundle_creation_tiemstamp = new DTNBundleTimestamp();
		dtn_bundle_creation_tiemstamp.set_secs(b.creation_ts().seconds());
		dtn_bundle_creation_tiemstamp.set_seqno(b.creation_ts().seqno());
		dtn_bundle_id.set_creation_ts(dtn_bundle_creation_tiemstamp);
		dtn_bundle_id.set_frag_offset(0);
		dtn_bundle_id.set_orig_length(0);

		Log
				.i(
						TAG,
						String
								.format(
										"DTN_SEND bundle %d, with payload type %s, payload length %d bytes",
										b.bundleid(), b.payload().location()
												.toString(), b.payload()
												.length()));

		// deliver the bundle
		BundleDaemon.getInstance().post(new BundleReceivedEvent(b, event_source_t.EVENTSRC_APP));

		
		return dtn_api_status_report_code.DTN_SUCCESS;
	}

	/**
	 * An implementation of the DTNAPI's dtn_unregister
	 * 
	 * @see
	 * se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPI#dtn_unregister(se
	 * .kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNHandle,
	 * se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNRegistrationID)
	 */
	
	public dtn_api_status_report_code dtn_unregister(DTNHandle handle, int regid) {

		Registration reg = BundleDaemon.getInstance().reg_table().get(regid);
		if (reg == null) {
			return dtn_api_status_report_code.DTN_ENOTFOUND;
		}

		// handle the special case in which we're unregistering a
		// currently bound registration, in which we actually leave it
		// around in the expired state, soit will be cleaned up when the
		// application either calls dtn_unbind() or closes the api socket
		if (is_bound(reg.regid()) && reg.active()) {
			if (reg.expired()) {
				return dtn_api_status_report_code.DTN_EINVAL;
			}

			reg.force_expire();
			assert (reg.expired());
			
			
			return dtn_api_status_report_code.DTN_SUCCESS;
		}

		// otherwise it's an error to call unregister on a registration
		// that's in-use by someone else
		if (reg.active()) {
			return dtn_api_status_report_code.DTN_EBUSY;
		}

		MsgBlockingQueue<Integer> notifier_ = new MsgBlockingQueue<Integer>(1);
		BundleDaemon.getInstance().post_and_wait(
				new RegistrationRemovedEvent(reg), notifier_, -1, true);

		return dtn_api_status_report_code.DTN_SUCCESS;
	}

	/**
	 * Checking whether the specified registration ID is bound by the API service
	 * @param regid
	 * @return whether the regid is bound
	 */
	private boolean is_bound(int regid) {
		lock_.lock();
		try {
			Iterator<DTNHandle> iter = handles_.iterator();
			while (iter.hasNext()) {
				DTNHandle handle = iter.next();
				handle.get_lock().lock();
				try {
					List<Integer> regids = bindings_.get(handle);
					if (regids != null) {
						Iterator<Integer> regid_itr = regids.iterator();
						while (regid_itr.hasNext()) {
							Integer regid_item = regid_itr.next();
							if (regid_item.intValue() == regid) {
								return true;
							}
						}

					}
				} finally {
					handle.get_lock().unlock();
				}
			}
			return false;
		} finally {
			lock_.unlock();
		}
	}

	/**
	 * The helper method to check whether the handle is bound to particular registration
	 */
	private boolean is_bound(DTNHandle handle, int regid) {
		handle.get_lock().lock();
		try {
			List<Integer> regids = bindings_.get(handle);
			if (regids != null) {
				Iterator<Integer> regid_itr = regids.iterator();
				while (regid_itr.hasNext()) {
					Integer regid_item = regid_itr.next();
					if (regid_item.intValue() == regid) {
						return true;
					}
				}

			}
		} finally {
			handle.get_lock().unlock();
		}

		return false;
	}

	/**
	 * An implementation of the DTNAPI's dtn_unregister
	 * @see
	 * se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPI#dtn_bind
	 */
	
	public dtn_api_status_report_code dtn_bind(DTNHandle handle, int regid) {
		// look up the registration
		RegistrationTable regtable = BundleDaemon.getInstance().reg_table();
		Registration reg = regtable.get(regid);

		if (reg == null) {
			Log.e(TAG, String.format("can't find registration %d", regid));
			return dtn_api_status_report_code.DTN_ENOTFOUND;
		}

		if (!(reg instanceof APIRegistration)) {
			Log.e(TAG, String.format(
					"registration %d is not an API registration!!", regid));
			return dtn_api_status_report_code.DTN_ENOTFOUND;
		}
		APIRegistration api_reg = (APIRegistration) (reg);
		// store the registration in the list for this session

		handle.get_lock().lock();
		try {
			List<Integer> regids = bindings_.get(handle);
			if (regids == null) regids = new List<Integer>();
			
			boolean exist = false;
			Iterator<Integer> itr = regids.iterator();
			while(itr.hasNext())
			{
				Integer cur = itr.next();
				if (cur.intValue() == regid)
				{
					// This registration is already bound to this bundle
					exist = true;
				}
			}
			
			
			if (!exist)
			regids.add(new Integer(regid));
			
			bindings_.put(handle, regids);
			
		} finally {
			handle.get_lock().unlock();
		}
		api_reg.set_active(true);

		Log.i(TAG, String.format("DTN_BIND: bound to registration %d", reg
				.regid()));

		return dtn_api_status_report_code.DTN_SUCCESS;
	}

	/**
	 * An implementation of the DTNAPI's dtn_unbind
	 * @see
	 * se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPI#dtn_unbind
	 */
	
	public dtn_api_status_report_code dtn_unbind(DTNHandle handle, int regid) {
		handle.get_lock().lock();
		try {

			List<Integer> regids = bindings_.get(handle);
			if (regids == null)
				return dtn_api_status_report_code.DTN_EHANDLE_NOT_BOUND_REG;
			Iterator<Integer> iter = regids.iterator();
			while (iter.hasNext())

			{
				Integer regid_integer = iter.next();
				if (regid_integer.intValue() == regid) {
					iter.remove();

					// look up the registration
					RegistrationTable regtable = BundleDaemon.getInstance()
							.reg_table();
					Registration reg = regtable.get(regid);

					if (reg == null) {
						Log.e(TAG, String.format("can't find registration %d",
								regid));
						return dtn_api_status_report_code.DTN_ENOTFOUND;
					}

					if (!(reg instanceof APIRegistration)) {
						Log.e(TAG, String.format(
								"registration %d is not an API registration!!",
								regid));
						return dtn_api_status_report_code.DTN_ENOTFOUND;
					}
					APIRegistration api_reg = (APIRegistration) (reg);
					api_reg.set_active(false);

					bindings_.remove(handle);
					Log.i(TAG, String.format(
							"DTN_UNBIND: unbound from registration %d", regid));
					return dtn_api_status_report_code.DTN_SUCCESS;
				}

			}

		} finally {
			handle.get_lock().unlock();
		}
		Log.e(TAG, String.format(
				"registration %d not bound to this api client", regid));
		return dtn_api_status_report_code.DTN_ENOTFOUND;
	}

}
