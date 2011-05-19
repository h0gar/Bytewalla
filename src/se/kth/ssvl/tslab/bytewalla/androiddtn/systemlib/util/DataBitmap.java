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

import java.util.BitSet;

/**
 *  DataBitmap implementation by embedding Java BitSet inside
 *  @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public class DataBitmap  {
	/**
	 * Internal java Bitset
	 */
	private BitSet  bitset_;
	
	/**
	 * Size of this DataBitmap
	 */
	private int size_ = 0;
	
	/**
	 * Constructor by initializing internal objects
	 */
	public DataBitmap()
	{
		bitset_ = new BitSet();
	}
	
	/**
	 * Set the bit at the pos. Grows the bit size if necessary
	 * @param pos
	 */
	public void set(int pos)
	{
		bitset_.set(pos);
		if ( pos > size_)
		{
			size_ = pos+1;
		}
		
	}
	
	/**
	 * Serial Version UID to support Java Serializable
	 */
	private static final long serialVersionUID = -4184638080030926182L;

	/**
	 * Accessor for the size of this bitmap is 
	 * @return the size
	 */
	public int size() {
		return size_;
	}
	
	/**
	 * Check whether this DataBitmap is empty or not
	 * @return the boolean whether this DataBitmap is empty or not 
	 */
	public boolean isEmpty()
	{
		return size_ == 0;
	}

	/**
	 * Clear the bit in the specified position. Grows the Databitmap if necessary
	 * @param pos
	 */
	public void clear(int pos) {
	
		bitset_.clear(pos);
		if ( pos > size_)
		{   
			size_ = pos+1;
		}
	}

	/**
	 * Find the next set bit of this Data bitmap
	 * @param pos
	 * @return next set bit's position of this Databitmap, -1 if there is not
	 */
	public int nextSetBit(int pos) {
		return bitset_.nextSetBit(pos);
	}
	
	/**
	 * clear the whole Databitmap.
	 */
	public void clear() {
		size_ = 0;
		bitset_.clear();
	}


}
