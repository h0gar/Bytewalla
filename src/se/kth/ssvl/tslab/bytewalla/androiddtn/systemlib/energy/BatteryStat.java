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
 * Class abstraction to communicate battery status
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public class BatteryStat {

	/**
	 * raw level of the battery
	 */
	private int raw_level_;
	
	/**
	 * maximum level in which the raw level is based on
	 */
	private int scale_;
	
	/**
	 * percentage of the raw level according to the scale
	 */
	private int level_;
	
	/**
	 * The health representing number according to http://developer.android.com/reference/android/os/BatteryManager.html 
	 */
	private int health_;
	
	/**
	 * The status representing number according to http://developer.android.com/reference/android/os/BatteryManager.html
	 */
	private int status_;
	/**
	 * Accessor for the raw level of the battery
	 * @return the raw_level_
	 */
	public int raw_level() {
		return raw_level_;
	}
	/**
	 * Setter for the raw level of the battery
	 * @param rawLevel the raw_level_ to set
	 */
	public void set_raw_level(int raw_level) {
		raw_level_ = raw_level;
	}
	/**
	 * Accessor for the maximum level in which the raw level is based on
	 * @return the scale_
	 */
	public int scale() {
		return scale_;
	}
	/**
	 * Setter for the maximum level in which the raw level is based on
	 * @param scale the scale_ to set
	 */
	public void set_scale(int scale) {
		scale_ = scale;
	}
	/**
	 * Accessor for the percentage of the raw level according to the scale
	 * @return the level_
	 */
	public int level() {
		return level_;
	}
	/**
	 * Setter for the percentage of the raw level according to the scale
	 * @param level the level_ to set
	 */
	public void set_level(int level) {
		level_ = level;
	}
	/**
	 * Accessor for the health representing number according to http://developer.android.com/reference/android/os/BatteryManager.html
	 * @return the health_
	 */
	public int health() {
		return health_;
	}
	/**
	 * Setter for the health representing number according to http://developer.android.com/reference/android/os/BatteryManager.html
	 * @param health the health_ to set
	 */
	public void set_health(int health) {
		health_ = health;
	}
	/**
	 * Accessor for the status representing number according to http://developer.android.com/reference/android/os/BatteryManager.html
	 * @return the status_
	 */
	public int status() {
		return status_;
	}
	/**
	 * Setter for the status representing number according to http://developer.android.com/reference/android/os/BatteryManager.html
	 * @param status the status_ to set
	 */
	public void set_status(int status) {
		status_ = status;
	}
}
