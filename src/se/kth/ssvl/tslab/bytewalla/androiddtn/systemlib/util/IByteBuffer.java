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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for logical Byte Buffer used in Android DTN. 
 * This class functions are mainly inspired by the java.nio.ByteBuffer 
 * but have Serializable function in its subclass (SerializableByteBuffer).
 * This Serializable function is needed because Bundle have to be saved for persistent storage using Java Serializable function
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public interface IByteBuffer{

	/**
	 * Retriving current position of the Byte Buffer
	 * @return current position
	 */
	public int position();

	/**
	 * Setting the current position of the Byte Buffer
	 * @param pos the position needed to be set
	 */
	public void position(int pos);
	
	/**
	 * Change the position of this Buffer back to the beginning
	 */
	public void rewind();
	
	/**
	 * Retrieve one byte from the current position of the buffer and move the position forward
	 * @return
	 */
	public byte get();

	/**
	 * Mark the current position and will be used together with reset to revert the position back
	 */
	public void mark();
	
	/**
	 * Revert to the marked position before..
	 */
	public void reset();
	
	/**
	 * Accessor for the internal capacity of this IByteBuffer
	 * @return the internal capacity of this IByteBuffer 
	 */
	public int capacity();
	
	/**
	 * Get the byte in particular index without moving the buffer position
	 * @param index the input index
	 * @return the byte data at that index.
	 */
	public byte get(int index);

	
	/**
	 * Read from the stream and store them as maximum size is the remaining of this IByteBuffer
	 */
	public void readFromStream(InputStream in) throws IOException ;
	
	
	/**
	 * Write to the stream from the particular offset with specified length
	 */
	public void writeWithStream(OutputStream out, int offset, int length) throws IOException;
	
	/**
	 * Get data from this serializable byte_array from current position into the input byte_array
	 * and moving position to the end after that
	 */
	public void get(byte[] byte_array);
	
	/**
	 * Put byte into the ByteBuffer in the current position and move the position forward 
	 * @param b
	 */
	public void put(byte b);

	/**
	 * Write the byte to index without changing the position
	 * @param index
	 * @param b
	 */
	public void put(int index, byte b);
	
	/**
	 * Put data into this serializable ByteBuffer by getting data from this byte_array
	 */
	public void put(byte[] byte_array);

	/**
	 * Accessor for the remaining number 
	 * @return
	 */
	public int remaining();
	
	/**
	 * Return the IByteBuffer asReadOnlyBuffer. The mark and position are separated.
	 * @return
	 */
	public IByteBuffer asReadOnlyBuffer();

	/**
	 * Get int value from the ByteBuffer without changing position
	 */
	public int getInt(int i);

	
	/**
	 * Get short value from the ByteBuffer without changing position 
	 */
	public short getShort();

	/**
	 * Put int value into the ByteBuffer and move position by 4
	 * @param value the int value needed to be put
	 */
	public void putInt(int value);

	/**
	 * Put short value into the ByteBuffer and move position by 2
	 * @param value the short value needed to be put
	 */
	public void putShort(short value);

	
	/**
	 * Get int with adding position + 4
	 */
	public int getInt();

	/**
	 * Retrieve the byte array representation of this IByteBuffer. 
	 * @return
	 */
	public byte[] array();

	
	
}
