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


import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.SDNV;
import android.util.Log;

/**
 * Helper routine for managing and manipulating Byte Buffer
 * @author Rerngvit Yanggratoke (rerngvit@kth.se) 
 */
public class BufferHelper {
	
	/**
	 * Helper tool to guarantee the length of the ByteBuffer,
	 * If it's shorter the buffer will be extend and copy old values in it 
	 * Otherwise, do nothing ( This will make the buffer not change at all )
	 * The return buffer also will keep the old position
	 */
	public static IByteBuffer reserve(IByteBuffer buf, int length)
	{
		if (buf.capacity() < length)
        {
        	// allocate new ByteBuffer and copy the old data to that size
	        IByteBuffer new_buf = new SerializableByteBuffer(length);
	        
	        int old_position =  buf.position();
	        int old_capacity = buf.capacity();
	        
	        
	        byte[] temp =   new byte[old_capacity];
	        buf.rewind();
	        buf.get(temp);
	        
	        new_buf.rewind();
	        new_buf.put(temp);
	        
	        new_buf.position(old_position);
	        
	        return new_buf;
        }
		return buf;
	}
	
	/**
	 * Shared routine to try consume SDNV if it's possible to consume the SDNV length will be return else, -1 will be returned
	 * This for handle the case when multiple chunks of data have to be handled
	 */
	public static int try_consume_SDNV(IByteBuffer buf, int buf_offset, int[] result)
	{
		int sdnv_len = -1;
		
	    int old_position = buf.position();
	    try
	    {
	    	 
	    	buf.position(buf_offset);
	    	
	        sdnv_len = SDNV.decode(buf,
	        		old_position - buf_offset,
	        		result);
	    }
	    finally
	    {
	    	buf.position(old_position);
	    }
	    return sdnv_len;
		
	}
	
	
	/**
	 * Shared routine to read SDNV, it will read the SDNV value from the specify position and give the result
	 * The buffer position will not be changed after this call
	 */
	public static int read_SDNV(IByteBuffer buf, int buf_offset, int[] result)
	{
		
		int sdnv_len = -1;
	    int old_position = buf.position();
	    try
	    {
	    	 
	    	buf.position(buf_offset);
	    	
	    	sdnv_len = SDNV.decode(buf,  result);
	    }
	    finally
	    {
	    	buf.position(old_position);
	    }
	    return sdnv_len;
		
	}
	
	
	/**
	 * Shared routine to write SDNV, it will write SDNV value to the specify position 
	 * The buffer position will not be changed after this call
	 */
	public static void write_SDNV(IByteBuffer buf, int buf_offset, int value)
	{
		int old_position = buf.position();
	    try
	    {
	    	 
	    	buf.position(buf_offset);
	    	
	    	SDNV.encode(value, buf);
	    }
	    finally
	    {
	    	buf.position(old_position);
	    }
	 
		
	}
	
	
	/**
	 * Shared routine for copying data from one ByteBuffer to another
	 * The offsets from both side can be specified
	 * The position will be reverted back to orginal postion for both of ByteBuffer
	 * This function try to mimic the functionality of memcpy function in C++
	 */
	public static void copy_data( IByteBuffer dest, int dest_offset, IByteBuffer src, int src_offset, int len)
	{
		byte[] temp = new byte[len];
		int old_dest_position = dest.position();
		int old_src_position = src.position();
		
		try
		{
			src.position(src_offset);
			Log.d("BufferHelper", "src p: "+src.position()+" len: "+len+" : src"+src.capacity());
			Log.d("BufferHelper", "des p: "+dest.position()+" len: "+len+" : src"+dest.capacity());			
			src.get(temp);
			
			dest.position(dest_offset);
			dest.put(temp);
		
		}
		finally
		{
			src.position(old_src_position);
			dest.position(old_dest_position);
		}
		
		
	}
	
	/** 
	 * Shared routine to copy data from this system ByteBuffer to java.nio.ByteBuffer
	 */
	public static void copy_data( java.nio.ByteBuffer dest, int dest_offset, IByteBuffer src, int src_offset, int len)
	{
		byte[] temp = new byte[len];
		int old_dest_position = dest.position();
		int old_src_position = src.position();
		
		try
		{
			src.position(src_offset);
			Log.d("BufferHelper", "src p: "+src.position()+" len: "+len+" : src"+src.capacity());
			Log.d("BufferHelper", "des p: "+dest.position()+" len: "+len+" : src"+dest.capacity());			
			src.get(temp);
			
			dest.position(dest_offset);
			dest.put(temp);
		
		}
		finally
		{
			src.position(old_src_position);
			dest.position(old_dest_position);
		}
		
		
	}
	
	
	/** 
	 * Shared routine to copy data from this system java.nio.ByteBuffer to java.nio.ByteBuffer
	 */
	public static void copy_data( java.nio.ByteBuffer dest, int dest_offset, java.nio.ByteBuffer src, int src_offset, int len)
	{
		byte[] temp = new byte[len];
		int old_dest_position = dest.position();
		int old_src_position = src.position();
		
		try
		{
			src.position(src_offset);
			Log.d("BufferHelper", "src p: "+src.position()+" len: "+len+" : src"+src.capacity());
			Log.d("BufferHelper", "des p: "+dest.position()+" len: "+len+" : src"+dest.capacity());			
			src.get(temp);
			
			dest.position(dest_offset);
			dest.put(temp);
		
		}
		finally
		{
			src.position(old_src_position);
			dest.position(old_dest_position);
		}
		
		
	}
	
	/** 
	 * Shared routine to copy data to this system java.nio.ByteBuffer to java.nio.ByteBuffer
	 */
	public static void copy_data( IByteBuffer dest, int dest_offset, java.nio.ByteBuffer src, int src_offset, int len)
	{
		byte[] temp = new byte[len];
		int old_dest_position = dest.position();
		int old_src_position = src.position();
		
		try
		{
			src.position(src_offset);
			Log.d("BufferHelper", "src p: "+src.position()+" len: "+len+" : src"+src.capacity());
			Log.d("BufferHelper", "des p: "+dest.position()+" len: "+len+" : src"+dest.capacity());			
			src.get(temp);
			
			dest.position(dest_offset);
			dest.put(temp);
		
		}
		finally
		{
			src.position(old_src_position);
			dest.position(old_dest_position);
		}
		
		
	}
	
	
	/**
	 * Move data start from the specified position "from" to the beginning of the buffer
	 * the position of src buffer will not be moved
	 */
	public static void move_data_back_to_beginning(IByteBuffer src, int from)
	{
		int old_position = src.position();
		int length = src.capacity() - from;
		byte[] temp = new byte[length];
		
		src.position(from);
		src.get(temp);
		src.rewind();
		src.put(temp);
		
		src.position(old_position);
	}
	
	/**
	 * Move data start from the ByteBuffer's position to the beginning of the buffer
	 * the position of src buffer shouldn't be move by this operation
	 */
	public static void move_data_back_to_beginning(IByteBuffer src)
	{
		move_data_back_to_beginning(src, src.position());
	}
	
	/**
	 * Get byte array result from byte buffer from particular position without moving the position of ByteBuffer
	 */
	public static byte[] get_data(IByteBuffer src, int offset, int len)
	{
		byte[] result = new byte[len];
		int old_position = src.position();
		
		src.position(offset);
		src.get(result);
		src.position(old_position);
		return result;
	}
	
	/**
	 * Get byte array result from byte buffer from particular position without moving the position of ByteBuffer
	 */
	public static IByteBuffer set_data(byte[] src)
	{
		IByteBuffer buf = new SerializableByteBuffer(src.length);
		buf.put(src);
		buf.rewind();
		return buf;
	}
	
	
}
