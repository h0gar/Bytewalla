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
package se.kth.ssvl.tslab.bytewalla.androiddtn;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Android Activity to support editing DTN configuration file
 */
public class DTNConfigEditor extends Activity {
	/**
	 * String TAG to support Android Logging system
	 */
	private final static String TAG = "DTNConfigEditor";
		
	/**
	 * Reference object to saveButton in DTNConfigEditor layout
	 */
	private Button saveButton; 
	
	/**
	 * Reference object to closeButton in DTNConfigEditor layout
	 */
	private Button closeButton;
	
	/**
	 * Reference object to configEditText in DTNConfigEditor layout
	 */
	private EditText configEditText;
	
	/**
	 * self object for accessing from anonymous inner class
	 */
	private DTNConfigEditor self_;
	
	/**
	 * flag to check whether the config is saved or not
	 */
	private boolean is_config_not_saved_;
	
	/**
	 * onCreate override from Android Activity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    setContentView(se.kth.ssvl.tslab.bytewalla.androiddtn.R.layout.dtnconfigeditor);
	    init();
    }
	
	/**
	 * initialize configurations including texts and buttons
	 */
	private void init()
	{
		self_ = this;
		initConfigEditText();
		initButtons();
	}
	
	/**
	 * initialize buttons including onclick listener
	 */
	private void initButtons()
	{
		saveButton = (Button)this.findViewById(R.id.DTNConfig_SaveButton);
		saveButton.setEnabled(false);
		saveButton.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				// Write the content to file and disable save Button
				OutputStream config_out_stream = null;
				OutputStreamWriter out_writer  = null;
				try {
				    config_out_stream = openFileOutput(getResources().getString(R.string.DTNConfigFilePath), MODE_PRIVATE);
				    out_writer = new OutputStreamWriter(config_out_stream);
					out_writer.write(configEditText.getText().toString());
					out_writer.flush();
					saveButton.setEnabled(false);
					is_config_not_saved_ = false;
					
				} catch (FileNotFoundException e) {
					Log.e(TAG, "FileNotFoundException :" + e.getMessage());
				} catch (NotFoundException e) {
					Log.e(TAG, "NotFoundException :" + e.getMessage());
				} catch (IOException e) {
					Log.e(TAG, "IOException from reader :" + e.getMessage());
				}
				finally
				{
					try {
						config_out_stream.close();
						out_writer.close();
					} catch (IOException e) {
						Log.e(TAG, "IO Exception in configuration writting");
					}
				}
				
			}
		});
		closeButton = (Button)this.findViewById(R.id.DTNConfig_CloseButton);
		
		closeButton.setOnClickListener(new OnClickListener(){

			
			public void onClick(View v) {
				if (is_config_not_saved_)
				{
					 new AlertDialog.Builder(self_)
				      .setMessage("The configuration is not saved. Your change will be lost. Proceed?")
				      .setPositiveButton("OK", new DialogInterface.OnClickListener() {
						
						
						public void onClick(DialogInterface dialog, int which) {
							
							self_.finish();
						}
				      }
				      )
				      .setNegativeButton("Cancel", null)
				      .show()
				      
				      ;
				      

				}
				else
				self_.finish();
				
			}});
		
	}
	
	/**
	 * Initialization config edit text by reading from the configuration file
	 */
	private void initConfigEditText()
	{
	 	configEditText = (EditText) this.findViewById(R.id.DTNConfig_EditText);
		
		// load configuration from file into the EditText text
		InputStream config_in_stream = null;
		InputStreamReader  isr       = null;
		try {
			
		    config_in_stream = openFileInput(getResources().getString(R.string.DTNConfigFilePath));
		    isr     = new InputStreamReader(config_in_stream);
            BufferedReader     in      = new BufferedReader(isr);
            String line;
        	
         	 
             while( (line = in.readLine()) != null )
            	 configEditText.append(line + "\n");

		
		
		} catch (FileNotFoundException e) {
			
			Log.e(TAG, "FileNotFoundException :" + e.getMessage());
		} catch (NotFoundException e) {
			Log.e(TAG, "NotFoundException :" + e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "IOException from reader :" + e.getMessage());
		}
		finally
		{
			try {
				config_in_stream.close();
		
				isr.close();
			} catch (IOException e) {
				Log.e(TAG, "error in reading configuration file");
			}
		}
		
		// Add TextChange event handler to enable the save button only when there is a change in the configuration file 
		configEditText.addTextChangedListener(new TextWatcher() {
			
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				saveButton.setEnabled(true);
				is_config_not_saved_ = true;
			}
			
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				}
			
			
			public void afterTextChanged(Editable s) {
				
			}
		});
	}
	
	
	
	
    
}