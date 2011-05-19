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

package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling;

import java.io.Serializable;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;

/**
 * 
 * Class to encapsulate bundle forwarding information.  This is created
 * when a bundle is forwarded to log a record of the forwarding event,
 * along with any route-specific information about the action, such as
 * the custody timer.
 *  @author Sharjeel Ahmed (sharjeel@kth.se) 
 */

public class ForwardingInfo implements Serializable{

	/**
	 * SerialVersionID to Support Serializable.
	 */
	private static final long serialVersionUID = -6019412240618452331L;
	
    /**
     * Default constructor.
     */
    public ForwardingInfo(){
        state_ = state_t.NONE;
        action_ = action_t.INVALID_ACTION;
        link_name_= "" ;
        regid_ = 0xffffffff;
        remote_eid_ = null;
        custody_spec_ = null; 
    }

	/**
     * The forwarding action type codes.
     */
	
    /**
     * Convenience flag to specify any forwarding action for use in
     * searching the log.
     */
    
    public enum action_t 
    {
        INVALID_ACTION (0),	
        FORWARD_ACTION (1 << 0),///< Forward the bundle to only this next hop
        COPY_ACTION    (1 << 1)///< Forward a copy of the bundle
    ;

        private static final Map<Integer,action_t> lookup 
             = new HashMap<Integer,action_t>();

        static {
             for(action_t s : EnumSet.allOf(action_t.class))
                  lookup.put(s.getCode(), s);
        }

        private int code;

        private action_t(int code) {
             this.code = code;
        }

        public int getCode() { return code; }

        public static action_t get(int code) { 
             return lookup.get(code); 
        }
    }
    
    /**
     * Convenience flag to specify any forwarding action for use in
     * searching the log.
     */
    final public static int ANY_ACTION = 0xffffffff;

    /**
     * Convert action to string value
     * @param action Action object which needs to convert
     * @return String value of action
     */
    final public static String action_to_str(action_t action)
    {
        switch(action) {
		    case INVALID_ACTION:	return "INVALID";
		    case FORWARD_ACTION:	return "FORWARD";
		    case COPY_ACTION:	return "COPY";
		    default:
		        // NOTREACHED;
		    	return null;
		    }
    }

    /**
     * The forwarding log state codes.
     */
    public enum state_t 
    {
    NONE             (0),       ///< Return value for no entry
    QUEUED           (1 << 0) ,  ///< Currently queued or being sent
    TRANSMITTED      (1 << 1),  ///< Successfully transmitted
    TRANSMIT_FAILED  (1 << 2),  ///< Transmission failed
    CANCELLED        (1 << 3),  ///< Transmission cancelled
    CUSTODY_TIMEOUT  (1 << 4),  ///< Custody transfer timeout
    DELIVERED        (1 << 5),  ///< Delivered to local registration
    SUPPRESSED       (1 << 6),  ///< Transmission suppressed
    RECEIVED         (1 << 10) ///< Where the bundle came from    	
    ;

        private static final Map<Integer,state_t> lookup 
             = new HashMap<Integer,state_t>();

        static {
             for(state_t s : EnumSet.allOf(state_t.class))
                  lookup.put(s.getCode(), s);
        }

        private int code;

        private state_t(int code) {
             this.code = code;
        }

        public int getCode() { return code; }

        public static state_t get(int code) { 
             return lookup.get(code); 
        }
    }

    /**
     * Convenience flag to specify any forwarding state for use in
     * searching the log.
     */
    final public static int ANY_STATE = 0xffffffff;

    /**
     * Convert state to string value
     * @param state State_t object which needs to convert
     * @return String value of state
     */

    final public static String state_to_str(state_t state)
    {
        switch(state) {
        case NONE:      	return "NONE";
        case QUEUED: 		return "QUEUED";
        case TRANSMITTED:      	return "TRANSMITTED";
        case TRANSMIT_FAILED:  	return "TRANSMIT_FAILED";
        case CANCELLED: 	return "CANCELLED";
        case CUSTODY_TIMEOUT:	return "CUSTODY_TIMEOUT";
        case DELIVERED:      	return "DELIVERED";
        case SUPPRESSED:      	return "SUPPRESSED";
        case RECEIVED:      	return "RECEIVED";

        default:
            //NOTREACHED;
        	return null;
        }
		
    }

    /**
     * Constructor used for new entries.
     * @param state Set State of ForwardingInfo
     * @param action Set Action to perform of ForwardingInfo
     * @param link_name Link name
     * @param regid Set registration id of ForwardingInfo
     * @param remote_eid Set remote eid of ForwardingInfo
     * @param custody_spec Set Set custody timer of ForwardingInfo
     */
    
    public ForwardingInfo(state_t state, action_t action, String link_name,int regid,
                   final EndpointID remote_eid,final CustodyTimerSpec custody_spec){
    	state_ = state_t.NONE;
        action_ = action;
        link_name_ = link_name;
        regid_ = regid;
        remote_eid_ = remote_eid;
        custody_spec_ = custody_spec;
        
    	set_state(state);
    	
    }

    /**
     * Set the state and update the timestamp.
     * @param new_state New state type to update old state of ForwardingInfo
     */
    public void set_state(state_t new_state){
       state_ = new_state;
       timestamp_ = new Date();
    }

    /**
     * Get the state of FowrdingInfo
     * @return Return the state
     */
    
    final public state_t  state(){ 
    	return state_;
    }
    
    /**
     * Get the action of ForwardingInfo
     * @return Return the action
     */
    final public action_t action() {
    	return action_;
    }
    
    /**
     * Get the link name of ForwardingInfo
     * @return Return the link_name
     */
    final public String link_name() { 
    	return link_name_; 
    }
    
    /**
     * Get the registration id of ForwardingInfo
     * @return Return the registration id
     */
    final public int regid()  { 
    	return regid_; 
    }
    
    /**
     * Get the remote_endpoint id of ForwardingInfo
     * @return Return the remote_eid
     */
    final public EndpointID remote_eid(){ 
    	return remote_eid_; 
    }
    
    /**
     * Get the timestamp of ForwardingInfo
     * @return Return the timestamp
     */
    final public Date timestamp() { 
    	return timestamp_; 
    }
    
    /**
     * Get the custody spec of ForwardingInfo
     * @return Return the custody spec
     */
    final public CustodyTimerSpec custody_spec() { 
    	return custody_spec_; 
    }

    /**
     * Set the link name of ForwardingInfo
     * @param name Name of new link name to set
     */
    final public void set_link_name(String name) { 
    	link_name_ = name; 
    }

    /**
     * Set the regid of ForwardingInfo
     * @param regid Registration id to set
     */
    final public void set_regid(int regid)  { 
    	regid_ = regid; 
    }
    
    /**
     * Set remote eid of ForwardingInfo
     * @param eid EndpointID to set 
     */
    final public void set_remote_eid(EndpointID eid){ 
    	remote_eid_ = eid; 
    }
    
    /**
     * State of the transmission
     */
    private state_t state_;   
    
    /**
     * Forwarding action
     */
    private action_t action_;
    
    /**
     *  The name of the link
     */
    private String link_name_; 
    
    /**
     * Registration id
     */
    private int    regid_; 
    
    /**
     * Eid of next hop
     */
    private EndpointID       remote_eid_; 
    
    /**
     * Timestamp of last state update
     */
    private Date timestamp_;   
    
    /**
     * Custody timer information
     */
    private CustodyTimerSpec custody_spec_;      
    
}
