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

package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.event;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


/**
 * Base class for the Contact related Event
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public class ContactEvent extends BundleEvent {

	/**
	 * Reason codes for the particular event
	 */
	public static enum reason_t {
		INVALID("INVALID", 0), 
		NO_INFO("no additional info"),
		USER("user action"), 
		BROKEN("connection broken"), 
		DISCOVERY("link discovery"), 
		CL_ERROR("cl protocol error"), 
		CL_VERSION("cl version mismatch"),
											
		SHUTDOWN("peer shut down"), 
		RECONNECT("re-establishing connection"), 
												
		IDLE("connection idle"), 
		TIMEOUT("schedule timed out"), 
		MAGIC_NUMBER("remote magic number"), 
		PEER_EID("Invalid Peer EID"), 
		FIND_CONTACT("No link available/create"),

		;

		private static final Map<reason_t, String> captionLookup = new HashMap<reason_t, String>();
		private static final Map<reason_t, Integer> codeLookup = new HashMap<reason_t, Integer>();

		static {
			for (reason_t event : EnumSet.allOf(reason_t.class)) {
				captionLookup.put(event, event.getCaption());
				codeLookup.put(event, event.getCode());
			}
		}

		private String caption;
		private int code = -1; // -1 = undefined

		private reason_t(String caption) {
			this.caption = caption;
		}

		private reason_t(String caption, int code) {
			this.caption = caption;
			this.code = code;
		}

		public String getCaption() {
			return caption;
		}

		public String event_to_str() {
			return caption;
		}

		public Integer getCode() {
			return code;
		}

		public static String get(reason_t event) {
			return captionLookup.get(event);
		}

	}

	/**
	 *  Constructor
	 * @param type
	 * @param reason
	 */
	public ContactEvent(event_type_t type, reason_t reason) {
		super(type);
		type_ = type;
		reason_ = reason;

	}

	/**
	 * reason code used internally, default to NO_INFO
	 */
	protected reason_t reason_ = reason_t.NO_INFO; 

	/**
	 * Getter for the reason code used internally, default to NO_INFO
	 * @return the reason_
	 */
	public reason_t reason() {
		return reason_;
	}

	/**
	 * Setter for the reason code used internally, default to NO_INFO
	 * @param reason the reason_ to set
	 */
	public void set_reason(reason_t reason) {
		reason_ = reason;
	}
};
