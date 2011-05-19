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
 * Class to represent Bundle Timestamp according to the Bundle Protocol. 
 * The time in the protocol is referenced by the number of
 * seconds from the 1 Jan, 2000. 
 */
public class BundleTimestamp implements Serializable{
	/**
	 * String TAG for using with Android Logging system
	 */
	private static final String TAG = "BundleTimestamp";
	
	/**
	 * Serial UID to support Java Serializable
	 */
	private static final long serialVersionUID = 558281790936386429L;
	/**
     * "The number of seconds between 1/1/1970 and 1/1/2000." [DTN2]
     */
    public static long TIMEVAL_CONVERSION;
  
	/**
	 * Seconds since 1/1/2000
	 */
	private long seconds_;
	
	/**
	 * Sequence number to the particular seconds
	 */
    private long seqno_; 
    
  
    /**
     * Static initialization
     */
    static {
    	Calendar ref_calendar         = Calendar.getInstance();
    	// set year to 2000 , start date is 1900
    	ref_calendar.setTime(new Date(100,0,1));
    	TIMEVAL_CONVERSION = ref_calendar.getTimeInMillis() / 1000;
    }
    
    /**
     * Constructor by parts.
     */
    public BundleTimestamp(long seconds, long seqno)
    {
    	init(seconds, seqno);
    }
    
    /**
     * Check whether two BundleTimestamp equal to each other 
     * @param other
     * @return
     */
    public boolean equals(BundleTimestamp other)
    {
    	return other.isEqual(other);
    }
    
    /**
     * 
     * @param inputTime
     * @param seqno
     */
    public BundleTimestamp(Date inputTime, long seqno)
    {
    	Calendar current_calendar     = Calendar.getInstance();
    	current_calendar.setTime(inputTime);
		long secondsPassFromRef =  TimeHelper.seconds_from_ref(inputTime);
		init(secondsPassFromRef, seqno);
    }
    
    /**
     * Construct the BundleTimestamp based on the current time
     * @param seqno
     */
    public BundleTimestamp(long seqno)
    {
    	long secondsPassFromRef = TimeHelper.current_seconds_from_ref();
		init(secondsPassFromRef, seqno);
    }
    
    /**
     * Return the length of SDNV encoding of the input BundleTimestamp
     */
    public static int SDNV_encoding_len(BundleTimestamp ts)
    {
    	return SDNV.encoding_len(ts.seconds_) + SDNV.encoding_len(ts.seqno_);
    }
    
    /**
     * Decode the data in the buffer and set the all the values in the DTNTime
     */
    public static int SDNV_decoding_len(IByteBuffer buf)
    {
    	return SDNV.SDNVs_decoding_len(2, buf);
    }
    /**
     *  Encode this into SDNV and move the buffer position
     */
    public static void encodeSDNV(BundleTimestamp ts, IByteBuffer buf)
    {
    	SDNV.encode(ts.seconds_, buf);
    	SDNV.encode(ts.seqno_, buf);
    }
    /**
     * Decode the data in the buffer and set the all the values in the BundleTimestamp
     *  and move the buffer position as well
     */
    public static void decodeSDNV(BundleTimestamp ts, IByteBuffer buf)
    {
    	long[] decoded_seconds = new long[1];
    	long[] decoded_seqnos = new long[1];
    	SDNV.decode(buf, decoded_seconds);
    	ts.set_seconds(decoded_seconds[0]);
    	
    	SDNV.decode(buf, decoded_seqnos);
    	ts.set_seqno(decoded_seqnos[0]);
    }
    
    
    /**
     * Init the BundleTimestamp according to input values
     * @param second_pass_from_ref
     * @param seqno
     */
    private void init(long second_pass_from_ref, long seqno) {
		seconds_ = second_pass_from_ref;
		seqno_ = seqno;
		
	}

	/**
     * Return the current seconds pass from the reference time ( Jan 1, 2000 ).
     */
    public static long get_current_time()
    {
    	
		
		long secondsPassFromRef =  TimeHelper.current_seconds_from_ref();
		return secondsPassFromRef;
    }

    /**
     * Check that the local clock setting is valid (i.e. is at least
     * up to date with the protocol.
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
     * is equal operator. This will be true iff both seconds and seqno are the same.
     */
    public boolean isEqual(final BundleTimestamp other) 
    {
        return seconds_ == other.seconds_ &&
            seqno_ == other.seqno_;
    }
        
    /**
     * is less than operator. This checks first with the seconds. If the seconds are equal, break the tie with the sequence number.
     */
    public boolean isLessThan(final BundleTimestamp other) 
    {
        if (seconds_ < other.seconds_) return true;
        if (seconds_ > other.seconds_) return false;
        return (seqno_ < other.seqno_);
    }
        
    /**
     * is greater than operator. This checks first with the seconds. If the seconds are equal, break the tie with the sequence number.
     */
    public  boolean isGreaterThan(final BundleTimestamp other)
    {
        if (seconds_ > other.seconds_) return true;
        if (seconds_ < other.seconds_) return false;
        return (seqno_ > other.seqno_);
    }
        
    /**
	 * Getter the seconds of this Bundle Timestamp from 1/1/2000
	 */
	public long seconds() {
		return seconds_;
	}

	/**
	 * Setter the seconds of this Bundle Timestamp from 1/1/2000
	 */
	public void set_seconds(long seconds) {
		seconds_ = seconds;
	}

	/**
	 * Getter the sequence number of this Bundle Timestamp
	 */
	public long seqno() {
		return seqno_;
	}

	/**
	 * Setter the sequence number of this Bundle Timestamp
	 */
	public void set_seqno(long seqno) {
		seqno_ = seqno;
	}
  
};
