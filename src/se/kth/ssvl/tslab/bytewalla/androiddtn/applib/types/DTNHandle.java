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

import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.Lock;



/**
 * This class represents connection object between the application and the APIService. The client of this DTNHandle will keep the object and every time
 * he needs to make connection with the APIService, he have to provide this Handle
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public class DTNHandle  {
	
	/**
	 *  lock for controling bound registration ID List in DTNAPIBinder implmenetation
	 */
  private Lock lock_;
  private boolean openned_;

  /**
   * Constructor with empty lock
   */
  public DTNHandle()
  {
	  lock_ = new Lock();
  }
/**
 * Getter for whether this Handle was openned the conneciton to DTNService already or not
 * @return the open
 */
public boolean openned() {
	return openned_;
}

/**
 * Setter for this Handle was openned the conneciton to DTNService already or not
 * @param open the open to set
 */
public void set_openned(boolean openned) {
	this.openned_ = openned;
}

/**
 * Getter function for Lock inside this class
 * @return the lock_
 */
public Lock get_lock() {
	return lock_;
}

/**
 * @param lock the lock_ to set
 */
public void set_lock(Lock lock) {
	lock_ = lock;
}
  
  
  
  
	

}
