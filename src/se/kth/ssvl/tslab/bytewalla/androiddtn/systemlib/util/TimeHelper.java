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
package se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util;

import java.util.Calendar;
import java.util.Date;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.DTNTime;


/**
 * Helper class to support time calculation
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public class TimeHelper {

	/**
	 * Calculate Elaspse time compared with current time in Milliseconds
	 * @param time input time to calculate
	 * @return Milliseconds passed from the moment
	 */
	public static long elapsed_ms(Date time)
	{
		return Calendar.getInstance().getTimeInMillis() - time.getTime();
	}
	
	/**
	 * Get current seconds from the reference time in DTN ( Jan , 2000 )
	 */
	public static long current_seconds_from_ref()
	{
		return (Calendar.getInstance().getTimeInMillis()/1000) - DTNTime.TIMEVAL_CONVERSION;
	}
	/**
	 * Get seconds from the reference time in DTN( Jan , 2000 ) from the time specified
	 */
	public static long seconds_from_ref(Date time)
	{
		Calendar time_calendar  = Calendar.getInstance();
		time_calendar.setTime(time);
		return (time_calendar.getTimeInMillis()/1000) - DTNTime.TIMEVAL_CONVERSION;
	}
	
	
	/**
	 * Get seconds from the reference time in DTN( Jan , 2000 ) from the time specified
	 */
	public static long seconds_from_ref(Calendar time_calendar)
	{
	
		return (time_calendar.getTimeInMillis()/1000) - DTNTime.TIMEVAL_CONVERSION;
	}
}
