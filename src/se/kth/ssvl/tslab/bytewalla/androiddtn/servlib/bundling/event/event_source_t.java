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
 * Class for specifying source of the Bundle Event
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public enum event_source_t {
	EVENTSRC_PEER("peer", 1), // /< a peer dtn forwarder
	EVENTSRC_APP("application", 2), // /< a local application
	EVENTSRC_STORE("dataStore", 3), // /< the data store
	EVENTSRC_ADMIN("admin", 4), // /< the admin logic
	EVENTSRC_FRAGMENTATION("fragmentation", 5), // /< the fragmentation engine
	EVENTSRC_ROUTER("router", 6) // /< the routing logic

	;

	private static final Map<event_source_t, String> captionLookup = new HashMap<event_source_t, String>();
	private static final Map<event_source_t, Integer> codeLookup = new HashMap<event_source_t, Integer>();

	static {
		for (event_source_t event : EnumSet.allOf(event_source_t.class)) {
			captionLookup.put(event, event.getCaption());
			codeLookup.put(event, event.getCode());
		}
	}

	private String caption;
	private int code = -1; // -1 = undefined

	private event_source_t(String caption) {
		this.caption = caption;
	}

	private event_source_t(String caption, int code) {
		this.caption = caption;
		this.code = code;
	}

	public String getCaption() {
		return caption;
	}

	// Keep the name similarity in DTN2
	public String event_to_str() {
		return caption;
	}

	public Integer getCode() {
		return code;
	}

	public static String get(event_source_t event) {
		return captionLookup.get(event);
	}

}
