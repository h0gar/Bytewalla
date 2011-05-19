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
package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.config;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * The enum of Convergence Layer type
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public enum conv_layer_type_t {
	TCP("tcp");
		private static final Map<String, conv_layer_type_t> lookupCaption = new HashMap<String, conv_layer_type_t>();
		static {
			for (conv_layer_type_t s : EnumSet
					.allOf(conv_layer_type_t.class))
				{ 
			
				  lookupCaption.put(s.getCaption(), s);
				}
		}

		
		private String caption;
		private conv_layer_type_t(String caption) {

			this.caption = caption;
		}


		public String getCaption() {
			return caption;
		}
}
