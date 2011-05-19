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
import java.io.Serializable;

/**
 * A serializabe implementation of IByteBuffer
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public class SerializableByteBuffer implements Serializable, IByteBuffer {

	/**
	 * Serial version UID to support Java Serializable
	 */
	private static final long serialVersionUID = 342019056131683430L;
	
	/**
	 * Internal variable to hold the ByteBuffer capacity
	 */
	protected int capacity_;
	
	/**
	 * Internal variable to hold the ByteBuffer position 
	 */
	protected int position_;
	
	/**
	 * Internal variable to hold the mark
	 */
	protected int mark_ = 0;
	
	/**
	 * Internal variable for keeping data
	 */
	protected byte[] backing_array_;

	/**
	 * Constructor with specified capacity
	 * @param capacity
	 */
	public SerializableByteBuffer(int capacity) {
		backing_array_ = new byte[capacity]; 
		capacity_ = capacity;
		position_ = 0;
		
		
		
	}

	/**
	 * Constructor by providing another SerializableByteBuffer
	 * @param src
	 */
	private SerializableByteBuffer(SerializableByteBuffer src) {

		capacity_ = src.capacity_;
		position_ = src.position_;
		mark_ = src.mark_;
		backing_array_ = src.backing_array_.clone();
	}

	
	public int position() {
		return position_;
	}

	
	public void position(int pos) {
		position_ = pos;
	}

	
	public void rewind() {
		position_ = 0;
	}

	
	public byte get() {

		byte result = backing_array_[position_];
		position_++;
		return result;
	}

	
	public byte get(int index) {
		return backing_array_[index];
	}

	
	public void get(byte[] byte_array) {
		for (int i = 0; i < byte_array.length; i++) {
			byte_array[i] = backing_array_[position_++];
		}

	}

	
	public void put(byte b) {
		backing_array_[position_] = b;
		position_++;
	}

	
	public void put(byte[] byte_array) {
		for (int i = 0; i < byte_array.length; i++) {
			backing_array_[position_++] = byte_array[i];
		}

	}

	
	public byte[] array() {
		return backing_array_.clone();
	}

	
	public IByteBuffer asReadOnlyBuffer() {

		return new SerializableByteBuffer(this);
	}

	
	public int capacity() {

		return capacity_;
	}

	
	public int getInt(int i) {
		int old_position = position_;
		position_ = i;
		int result = getInt();
		position_ = old_position;
		return result;
	}

	
	public int getInt() {
		byte[] byte_array = new byte[4];
		get(byte_array);
		return byte_array_to_int(byte_array);
	}

	
	public short getShort() {
		byte[] byte_array = new byte[2];
		get(byte_array);
		return byte_array_to_short(byte_array);
	}

	
	public void mark() {
		mark_ = position_;

	}

	
	public void put(int index, byte b) {
		backing_array_[index] = b;

	}

	

	
	public void putInt(int value) {
		byte[] byte_array = int_to_byte_array(value);
		put(byte_array);

	}

	/**
	 * Routine to convert int to byte array
	 * @param value
	 * @return
	 */
	private static final byte[] int_to_byte_array(int value) {
		return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16),
				(byte) (value >>> 8), (byte) value };
	}

	/**
	 * Routine to convert byte array to int
	 * @param b
	 * @return
	 */
	private static final int byte_array_to_int(byte[] b) {
		return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8)
				+ (b[3] & 0xFF);
	}

	/**
	 * Routine to convert short to byte array
	 * @param value
	 * @return
	 */
	private static final byte[] short_to_byte_array(short value) {
		return new byte[] { (byte) (value >>> 8), (byte) value };
	}

	/**
	 * Routine to convert byte array to short
	 * @param b
	 * @return
	 */
	private static final short byte_array_to_short(byte[] b) {
		return (short) (
				           ( b[0] << 8) 
				           +
				            ((b[1] & 0xFF))
				            

		);

	}

	
	public void putShort(short value) {
		byte[] byte_array = short_to_byte_array(value);
		put(byte_array);

	}

	
	public int remaining() {
		return capacity_ - position_;
	}

	
	public void reset() {
		position_ = mark_;

	}

	
	public void writeWithStream(OutputStream out, int offset, int length) throws IOException {

		out.write(backing_array_, offset, length);
		
	}

	
	public void readFromStream(InputStream in) throws IOException {
		int read_count = in.read(backing_array_, position_, remaining());
		position_ += read_count;
		
	}

}
