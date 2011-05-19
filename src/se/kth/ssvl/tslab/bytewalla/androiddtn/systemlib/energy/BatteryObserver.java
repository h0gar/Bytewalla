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
package se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.energy;


/**
 * The Interface for any applications would like to know about real time battery status in Android.
 * Any class would like to get an update should implement this Interface.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public interface BatteryObserver {

	/**
	 * The abstract way to see the raw_level of the battery. This will be run anytime the battery status is changed.
	 */
	public void update_battery_stat(BatteryStat bt_stat);
}
