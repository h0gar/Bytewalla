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
import java.util.Calendar;
import java.util.Date;

import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.TimeHelper;
import android.util.Log;
 
/**
 * Class to represent DTNTime according to the protocol. The DTNTime consists of seconds from 1 Jan, 2000 and the nanoseconds from that second.
 * Because of the limitation of Java to retrieve nanoseconds value, it's always zero here.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class DTNTime implements Serializable{
	
	
	/**
	 *  SerialVersionID to Support serializable.
	 */
	private static final long serialVersionUID = -8810672023468218606L;

	/**
	 *  String TAG for using with Android Logging system
	 */
	private static final String TAG = "DTNTime";
	 
	 
	    static {
	    	Calendar ref_calendar         = Calendar.getInstance();
	    	ref_calendar.setTime(new Date(100,0,1));
	    	
	    	TIMEVAL_CONVERSION = ref_calendar.getTimeInMillis() / 1000;
	    }
	    
	    
	/**
	 * Getter for the seconds of this DTNTime from 1/1/2000
	 */
	public long seconds() {
		return seconds_;
	}

	/**
	 * Setter for the seconds of this DTNTime from 1/1/2000
	 * @param seconds Set seconds
	 */
	public void set_seconds(long seconds) {
		seconds_ = seconds;
	}

	/**
	 * Getter for the sequence number of this Bundle Timestamp
	 * @return Retrun time in nanoseconds
	 */
	public long nanoseconds() {
		return nanoseconds_;
	}

	/**
	 * Setter for the sequence number of this Bundle Timestamp
	 * @param nanoseconds Set nanosends
	 */
	public void set_nanoseconds(long nanoseconds) {
		nanoseconds_ = nanoseconds;
		
	}

	/**
	 * Seconds since 1 Jan, 2000
	 */
	private long seconds_; 
	
	/**
	 * Nanoseconds passed since the second above
	 */
    private long nanoseconds_;

    /**
     * Constructor by parts.
     * @param seconds Time in seconds to init DTNTime 
     * @param nanoseconds Time in nanoseconds to init DTNTime
     */
    public DTNTime(long seconds, long nanoseconds)
    {
    	init(seconds, nanoseconds);
    }
    
    /**
     * Construct the BundleTimestamp based on the inputTime Date and the seqno specified
     * @param  inputTime Date object, get seconds from date and init DTNTime
     */
    
    public DTNTime(Date inputTime)
    {
    	
		long secondsPassFromRef =  TimeHelper.seconds_from_ref(inputTime);
		long nanoseconds = 0; // set to zero as it couldn't be done with java
		init(secondsPassFromRef, nanoseconds);
    }
    
    /**
     * Construct the DTNTime based on the current time
     */
    public DTNTime()
    {
		
		long secondsPassFromRef =  TimeHelper.current_seconds_from_ref();
		long nanoseconds = 0; // set to zero as it couldn't be done with java
		init(secondsPassFromRef, nanoseconds);
    }
    
    
    /**
     * Return the length of SDNV encoding of the input DTNTime
     * @param dt DTNTime object to get the length of SDNV encoding of the input DTNTime
     * @return Total length of SDVN encoding of DTNTime 
     */
    public static int SDNV_encoding_len(DTNTime dt)
    {
    	return SDNV.encoding_len(dt.seconds_) + SDNV.encoding_len(dt.nanoseconds_);
    }
    
    
   
    
    
    /**
     *  Encode this into SDNV and move the buffer position
     *  @param dt DTNTime to encode
     *  @param buf Serializable Buffer to store SDNV of DTNTime
     */
    public static void encodeSDNV(DTNTime dt, IByteBuffer buf)
    {
    	SDNV.encode(dt.seconds_, buf);
    	SDNV.encode(dt.nanoseconds_, buf);
    }
    
    
    /**
     * Decode the data in the buffer and set the all the values in the DTNTime
     *  @param dt Empty DTNTime to store decoded DTNTime
     *  @param buf Serializable Buffer to read and recode DTNTime
     */
    public static void decodeSDNV(DTNTime dt, IByteBuffer buf)
    {
    	long[] decoded_seconds = new long[1];
    	long[] decoded_nanoseconds = new long[1];
    	SDNV.decode(buf, decoded_seconds);
    	dt.set_seconds(decoded_seconds[0]);
    	
    	SDNV.decode(buf, decoded_nanoseconds);
    	dt.set_nanoseconds(decoded_nanoseconds[0]);
    }
    
    
    /**
     * Decode the data in the buffer and set the all the values in the DTNTime
     * @param buf Buffer to decode data
     */
    public static int SDNV_decoding_len(IByteBuffer buf)
    {
    	return SDNV.SDNVs_decoding_len(2, buf);
    }
    
    
    /**
     *  Method for parse DTNTime , return null if it can't pass
     *  @param bp Buffer pupulated with data
     */
    public static DTNTime decodeSDNV_and_Create_DTNTime(IByteBuffer bp)
    {
    	DTNTime ret = new DTNTime();
    	int sdnv_len  = SDNV.len(bp);
    	
    	if (sdnv_len == -1) return null;
    	else
    	{
    		decodeSDNV(ret, bp);
    		return ret;
    	}
    }
    
    /**
     *  Internal function for init DTNTime
     *  @param seconds_pass_from_ref Set seconds 
     *  @param nano_seconds_pass_from_the_second Set Nanoseconds
     */
    private void init(long seconds_pass_from_ref, long nano_seconds_pass_from_the_second) {
		seconds_ = seconds_pass_from_ref;
		nanoseconds_ = nano_seconds_pass_from_the_second;
		
	}
    /**
     * Check that the local clock setting is valid (i.e. is at least
     * up to date with the protocol.
     * @return True if clock is valid
     */
    public static boolean check_local_clock()
    {
           	if ((System.currentTimeMillis()/1000) < TIMEVAL_CONVERSION)
    	        	{
    	        		Log.e(TAG, "invalid clock setting");
    	        		return false;
    	        	}
    	        	else
    	        		return true;
    	        
    
    }

    /**
     * IsEqual operation. The DTNTime will be equal iff both the seconds and nanoseconds are equal
     * @param other DTNTime to compare with current DTNTime
     * @return True if both DTNTime are equal
     */
    public boolean isEqual(final DTNTime other) 
    {
        return seconds_ == other.seconds_ &&
            nanoseconds_ == other.nanoseconds_;
    }

    /**
     * The DTNTime will be equal iff both the seconds and nanoseconds are equal
     * @param other DTNTime to compare with current DTNTime
     * @return True if both DTNTime are equal
     */

    public boolean equals(DTNTime other)
    {
    	return isEqual(other);
    }
    
    /**
     * isLessThan operation. Use seconds to compare and nanoseconds to break tie.
     * @param other DTNTime to compare with current DTNTime
     * @return True if current DTNTime is less than other DTNTime
     */
    public boolean isLessThan(final DTNTime other) 
    {
        if (seconds_ < other.seconds_) return true;
        if (seconds_ > other.seconds_) return false;
        return (nanoseconds_ < other.nanoseconds_);
    }
        
    /**
     * isGreaterThan operation. Use seconds to compare and nanoseconds to break tie.
     * @param other DTNTime to compare with current DTNTime
     * @return True if current DTNTime is greater than other DTNTime
     */
    public  boolean isGreaterThan(final DTNTime other)
    {
        if (seconds_ > other.seconds_) return true;
        if (seconds_ < other.seconds_) return false;
        return (nanoseconds_ > other.nanoseconds_);
    }
        
    /**
     * "The number of seconds between 1/1/1970 and 1/1/2000" [DTN2]
     */
    public static long TIMEVAL_CONVERSION;
};
