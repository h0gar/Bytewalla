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

package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.discovery;

import java.net.InetAddress;

/**
 * "On-the-wire (radio, whatever) representation of
 * IP address family's advertisement beacon" [DTN2].
 * 
 * @author María José Peroza Marval (mjpm@kth.se)
 */

public class DiscoveryHeader {
	 
	/**
	 * Constructor
	 */
	public DiscoveryHeader() {
		
		cl_type = (byte)IPDiscovery.cl_type_t.UNDEFINED.getCode();
		interval = 0;
		length = 0;
		inet_addr = null;
		inet_port = 0;
		name_len = 0;
		sender_name = "";
				
	}
	
	    	  
	    private byte cl_type;                          // Type of CL offered
    	private byte interval;                         // 100ms units
       	private short length;                          // total length of packet
        private InetAddress inet_addr;                 // IPv4 address of CL
        private short inet_port;                       // IPv4 port of CL
        private short name_len;                        // length of EID
        private String sender_name;                    // DTN URI of beacon sender
        
        /**
         * Accessors
         */
        public byte cl_type() {
    		return cl_type;
    	}
        
        public void set_cl_type(byte clType) {
    		this.cl_type = clType;
    	}
        
       	public byte interval() {
    		return interval;
    	}

    	public void set_interval(byte interval) {
    		this.interval = interval;
    	}
    	
    	 public short length() {
 			return length;
 		}

 		public void set_length(short length) {
 			this.length = length;
 		}

 		public InetAddress inet_addr() {
 			return inet_addr;
 		}

 		public void set_inet_addr(InetAddress inetAddr) {
 			inet_addr = inetAddr;
 		}

 		public short inet_port() {
 			return inet_port;
 		}

 		public void set_inet_port(short inetPort) {
 			inet_port = inetPort;
 		}

 		public short name_len() {
 			return name_len;
 		}

 		public void set_name_len(short nameLen) {
 			name_len = nameLen;
 		}

 		public String sender_name() {
 			return sender_name;
 		}

 		public void set_sender_name(String senderName) {
 			sender_name = senderName;
 		}








    

}
