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
package se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock class for handle concurrency in the Android DTN System. This Lock is implemented by extending Android ReentrantLock.
 * This is done instead of using ReentrantLock directly to increase maintainability. For example, if someone would like to change Lock implementation in the future,
 * he can just change it here without modifying all the classes.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public class Lock extends ReentrantLock implements Serializable{

	/**
	 * Unique identifier according to Java Serializable specification
	 */
	private static final long serialVersionUID = 6472115469714829769L;
	

}
