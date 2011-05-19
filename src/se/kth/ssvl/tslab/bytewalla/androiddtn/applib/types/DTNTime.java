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
package se.kth.ssvl.tslab.bytewalla.androiddtn.applib.types;

import java.util.Calendar;
import java.util.Date;

import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.TimeHelper;

/**
 * Class representing Time in DTN. This consists of seconds passed from 1 Jan, 2000 and the nanosecs passed from that particular seconds.
 */
public class DTNTime  {
	/**
	 * seconds passed from 1 Jan, 2000
	 */
	private long secs_;
	/**
	 * nanosecs passed from that particular seconds
	 */
	private long nanosecs_;
	/**
	 * Timeval conversion is the number of seconds from the starting point in Java( 1 Jan, 1970 to 1 Jan, 2000 )
	 */
	  public static long TIMEVAL_CONVERSION;
	 
	    static {
	    	Calendar ref_calendar         = Calendar.getInstance();
	    	// set year to 2000 , start date is 1900
	    	ref_calendar.setTime(new Date(100,0,1));
	    	
	    	TIMEVAL_CONVERSION = ref_calendar.getTimeInMillis() / 1000;
	    }
	    
	/**
	 * Constructor from seconds and nanoseconds    
	 * @param secs
	 * @param nanosecs
	 */
	public DTNTime(long secs, long nanosecs)
	{
		secs_ = secs;
		nanosecs_ = nanosecs;
	}
	
	/**
	 *  Construct the DTNTime based on the current time
	 */
    public DTNTime()
    {
		
    	secs_ =  TimeHelper.current_seconds_from_ref();
    	nanosecs_ = 0; // set to zero as it couldn't be done with java
	
    }
	
	
	
	/**
	 * Getter function for seconds passed from 1 Jan, 2000
	 * @return the secs_
	 */
	public long secs_() {
		return secs_;
	}

	/**
	 * Setter function for seconds passed from 1 Jan, 2000
	 * @param secs the secs_ to set
	 */
	public void set_secs(long secs) {
		secs_ = secs;
	}

	/**
	 * Getter function for nanosecs passed from that particular seconds
	 * @return the nanosecs_
	 */
	public long nanosecs() {
		return nanosecs_;
	}

	/**
	 * Setter functions for nanosecs passed from that particular seconds
	 * @param nanosecs the nanosecs_ to set
	 */
	public void set_nanosecs(long nanosecs) {
		nanosecs_ = nanosecs;
	}

	
}
