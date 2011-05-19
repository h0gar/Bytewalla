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
package se.kth.ssvl.tslab.bytewalla.androiddtn.apps;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import se.kth.ssvl.tslab.bytewalla.androiddtn.DTNService;
import se.kth.ssvl.tslab.bytewalla.androiddtn.R;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPIBinder;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPICode.dtn_api_status_report_code;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPICode.dtn_bundle_payload_location_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.DTNAPICode.dtn_reg_flags_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNBundlePayload;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNBundleSpec;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNEndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNHandle;
import se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types.DTNRegistrationInfo;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleDaemon;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
/**
 * The DTNReceive Activity. This Activity allows user to receive Text message from DTN
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class DTNReceive extends Activity {
	/**
	 * The String TAG for supporting Android Logging mechanism
	 */
	private final static String TAG = "DTNReceive";
	
	/**
	 * The closeButton reference object
	 */
	private Button closeButton;
	
	/**
	 * The Destination EndpointID EditText reference object
	 */
	private EditText DestEIDEditText;
	
	/**
	 * The receiveButton reference object
	 */
	private Button receiveButton;
	
	/**
	 * The resultTextView reference object
	 */
	private TextView resultTextView;
	
	/**
	 * The DTNAPIBinder for calling API in DTNService
	 */
	private DTNAPIBinder dtn_api_binder_;
	
	/**
	 * The DTNAPIService connection
	 */
	private ServiceConnection conn_;

	/**
	 * The onCreate function overridden from Android Activity. 
	 * This basically will associate the display view to dtnreceive layout and initialize the class
	 */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dtnapps_dtnreceive);
		init();
	}

	/**
	 * The onDestroy function override form Android Activity. This will free the DTNAPIBinder resource by unbinding the service
	 */
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindDTNService();
	}

	/**
	 * Initialize the User Interface and bind the DTNAPI to the service
	 */
	private void init() {
		init_UIs();
		bindDTNService();
		DestEIDEditText.setText(BundleDaemon.getInstance().local_eid().str()+"/test");
	}

	/**
	 * Unbind DTNService 
	 */
	private void unbindDTNService() {
		unbindService(conn_);
	}

	/**
	 * Intiailize user interface by adding events and setting appropriate text
	 */
	private void init_UIs() {
		closeButton = (Button) this
				.findViewById(R.id.DTNApps_DTNReceive_CloseButton);
		closeButton.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				DTNReceive.this.finish();

			}
		});

		DestEIDEditText = (EditText) this
				.findViewById(R.id.DTNApps_DTNReceive_DestEIDEditText);
		
		receiveButton = (Button) this
				.findViewById(R.id.DTNApps_DTNReceive_ReceiveButton);
		receiveButton.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				try {
					int received_count = receive_messages();

					if (received_count > 0)
						new AlertDialog.Builder(DTNReceive.this).setMessage(
								"Receive successfully " + received_count
										+ " messages").setPositiveButton("OK",
								null).show();

					else {
						new AlertDialog.Builder(DTNReceive.this).setMessage(
								"Receive fail from timeout").setPositiveButton(
								"OK", null).show();

					}

				} catch (DTNAPIFailException e) {
					clear_result_ui();
					new AlertDialog.Builder(DTNReceive.this).setMessage(
							"Receive Fail with DTNAPIFail Exception")
							.setPositiveButton("OK", null).show();
				}

			}
		});

		resultTextView = (TextView) this
				.findViewById(R.id.DTNApps_DTNReceive_ResultTextView);

	}

	/**
	 * Bind DTNService by using ServiceConnection
	 */
	private void bindDTNService() {

		conn_ = new ServiceConnection() {

			
			public void onServiceConnected(ComponentName arg0, IBinder ibinder) {
				Log.i(TAG, "DTN Service is bound");
				dtn_api_binder_ = (DTNAPIBinder) ibinder;
			}

			
			public void onServiceDisconnected(ComponentName arg0) {
				Log.i(TAG, "DTN Service is Unbound");
				dtn_api_binder_ = null;
			}

		};

		Intent i = new Intent(DTNReceive.this, DTNService.class);

		bindService(i, conn_, Context.BIND_AUTO_CREATE);
	}

	/**
	 * Helper method to write down the user interface
	 */
	private void append_result_to_ui(DTNBundleSpec spec,
			DTNBundlePayload payload) {
		StringBuffer buf = new StringBuffer();

		buf.append(String.format("Receive from %s , payload type = %s \n", spec
				.source().toString(), payload.location().toString()));

		if (payload.location() == dtn_bundle_payload_location_t.DTN_PAYLOAD_MEM) {
			try {
				buf.append(String.format("Message: %s\n", new String(payload
						.buf(), "US-ASCII")));
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, e.getMessage());
			}

		} else if (payload.location() == dtn_bundle_payload_location_t.DTN_PAYLOAD_FILE) {
			byte[] result = new byte[payload.length()];

			RandomAccessFile file_handle = null;
			try {
				file_handle = new RandomAccessFile(payload.file(), "r");
				file_handle.read(result);
				String output = new String(result, "US-ASCII");

				buf.append(String.format("Message: %s\n", output));
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, e.getMessage());
			} catch (FileNotFoundException e) {
				Log.e(TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			} finally {
				try {
					file_handle.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
			}

		}

		buf.append("\n\n");

		resultTextView.setText(resultTextView.getText() + buf.toString());

	}

	/**
	 * Clear out the user interface
	 */
	private void clear_result_ui() {
		resultTextView.setText("");
	}

	/**
	 * Helper function to receive_messages
	 * @return the number of messages receive
	 * @throws DTNAPIFailException
	 */
	private int receive_messages() throws DTNAPIFailException {
		int count = 0;
		// Get value from user interfaces
		String dest_eid_string = DestEIDEditText.getText().toString();

		// Clear result ui
		clear_result_ui();

		// find registrations first with dtn_find_registration
		DTNEndpointID dest_eid = new DTNEndpointID(dest_eid_string);
		List<Integer> registration_ids = new List<Integer>();
		DTNHandle handle = new DTNHandle();
		dtn_api_status_report_code find_result = dtn_api_binder_
				.dtn_find_registrations(handle, dest_eid, registration_ids);

		if (find_result != dtn_api_status_report_code.DTN_SUCCESS) {
			// There are no existing registration for this Endpoint ID
			// Create the registration for this and put the regid in
			// registration_ids
			DTNRegistrationInfo reginfo = new DTNRegistrationInfo(dest_eid,
					dtn_reg_flags_t.DTN_REG_DEFER.getCode(), 3600, false);

			int[] newregid = new int[1];
			dtn_api_binder_.dtn_register(handle, reginfo, newregid);

			registration_ids.add(new Integer(newregid[0]));

		}

		// From here on there at least one registration bound to this handle
		// Iterate over registration_ids to get
		try {
			Iterator<Integer> iter = registration_ids.iterator();
			while (iter.hasNext()) {
				Integer regid = iter.next();
				// Bind the handle to registration if it's not already bind
				dtn_api_binder_.dtn_bind(handle, regid.intValue());

				// create an empty spec and payload to retrieve value from an
				// API
				DTNBundleSpec spec = new DTNBundleSpec();
				DTNBundlePayload dtn_payload = new DTNBundlePayload(
						dtn_bundle_payload_location_t.DTN_PAYLOAD_FILE);

				// Block Receiving call from API
				dtn_api_status_report_code receive_result = null;
				try {
					do {
						receive_result = dtn_api_binder_.dtn_recv(handle, regid
								.intValue(), spec, dtn_payload, 1);

						if (receive_result == dtn_api_status_report_code.DTN_SUCCESS)
							append_result_to_ui(spec, dtn_payload);
						else
							break;

						count++;

					} while (receive_result == dtn_api_status_report_code.DTN_SUCCESS);
				} catch (InterruptedException e) {
					// If we got more than one result try the next step

					continue;
				} finally {
					dtn_api_binder_.dtn_unbind(handle, regid.intValue());
				}

			}
		} finally {
			// unregister all the found registration
			Iterator<Integer> itr = registration_ids.iterator();
			while (itr.hasNext()) {
				Integer regid = itr.next();
				dtn_api_binder_.dtn_unregister(handle, regid.intValue());
			}
		}

		return count;

	}
}
