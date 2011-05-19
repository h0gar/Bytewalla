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



import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPICode.dtn_api_status_report_code;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNBundleID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNBundlePayload;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNBundleSpec;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNEndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNHandle;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNRegistrationInfo;
import se.kth.ssvl.tslab.bytewalla.androiddtn.apps.DTNAPIFailException;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.List;
/**
 *  an API interface for BP application to communicate with the DTNService
 *  @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public interface DTNAPI {

	/**
	 * "biggest in-memory bundle is ~50K" [DTN2]
	 */
	final int DTN_MAX_BUNDLE_MEM  = 50 * 1024; 
	
	/**
	 * Get an appropriate EndpointID by appending the input service tag with Bundle Daemon local_eid
	 */
	dtn_api_status_report_code dtn_build_local_eid(DTNHandle handle,
	                               DTNEndpointID local_eid,
	                               final String service_tag);

	/**
	 * Create an API Registration and put it in the main Registration table 
	 */
	dtn_api_status_report_code dtn_register(DTNHandle handle,
							DTNRegistrationInfo reginfo,
	                        int[] newregid) throws DTNAPIFailException;

	/**
	 * Delete a dtn registration.
	 */
	dtn_api_status_report_code dtn_unregister(DTNHandle handle,
			int regid);

	/**
	 * Check for an existing registration on the given endpoint id,
	 * returning DTN_SUCCSS and filling in the registration id if it
	 * exists, or returning ENOENT if it doesn't.
	 */
	dtn_api_status_report_code dtn_find_registrations(DTNHandle handle,
	                                 DTNEndpointID eid,
	                                 List<Integer> registration_ids);

	
	

	/**
	 * Put the registration in "active" mode so others can not close until all other people finished using it
	 */
	dtn_api_status_report_code dtn_bind(DTNHandle handle, int regid);

	/**
	 * This serves to put the registration back in "passive" mode.
	 */
	dtn_api_status_report_code dtn_unbind(DTNHandle handle, int regid);
	
	
	/**
	 * Open DTNHandle and book resource if necessary
	 */
	dtn_api_status_report_code dtn_open(DTNHandle handle);
	

	/**
	 * Close DTNHandle and free all resources
	 */

	dtn_api_status_report_code dtn_close(DTNHandle handle);
	
	/**
	 * Send a bundle either from memory or from a file. This depends on the DTNBundlePayload location set
	 */
	dtn_api_status_report_code dtn_send(DTNHandle handle,
	                    DTNBundleSpec spec,
	                    DTNBundlePayload payload,
	                    DTNBundleID dtn_bundle_id);

	

	/**
	 * Try to receive DTNBundle by block waiting according to input timeout. Timeout -1 means wait forever.
	 * InterruptedExceptio will be thrown if Timeout occur before the DTNBundle was received
	 * @param timeout time to wait for Bundle in milliseconds
	 */
	dtn_api_status_report_code dtn_recv(DTNHandle handle, int regid,			  
	                    DTNBundleSpec spec,
	                    DTNBundlePayload payload,
	                    int timeout)  throws InterruptedException ;

	
}
