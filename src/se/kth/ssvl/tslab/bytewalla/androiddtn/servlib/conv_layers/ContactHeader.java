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

package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.conv_layers;

/**
 * "Contact initiation header. Sent at the beginning of a contact" [DTN2].
 * 
 * @author María José Peroza Marval (mjpm@kth.se)
 */

public class ContactHeader {

	int magic; // /< magic word (MAGIC: "dtn!")
	byte version; // /< cl protocol version
	byte flags; // /< connection flags (see above)
	short keepalive_interval; // /< seconds between keepalive packets

	/**
	 * Default Constructor.
	 */
	public ContactHeader() {
	}

}
