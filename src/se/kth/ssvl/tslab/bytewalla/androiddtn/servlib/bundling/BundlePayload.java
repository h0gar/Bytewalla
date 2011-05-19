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

package se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import se.kth.ssvl.tslab.bytewalla.androiddtn.DTNService;
import se.kth.ssvl.tslab.bytewalla.androiddtn.R;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.exception.BundleLockNotHeldByCurrentThread;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.exception.BundlePayloadWrongTypeException;
import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.storage.BundleStore;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.thread.Lock;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.SerializableByteBuffer;
import android.content.Context;
import android.util.Log;

/**
 * This class is created separately from DTN Bundle for supporting different locations of the Bundle including memory, disk, or no data at all.
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class BundlePayload implements Serializable {

	/**
	 * String TAG to support Android logging system
	 */
	private static String TAG = "BundlePayload";
	/**
	 * Serial version UID to support Java Serializable
	 */
	private static final long serialVersionUID = -6766691157807333304L;
	
	/**
	 * Default memory size in case of Bundle location is Memory only
	 */
	private static final int DEFAULT_DATA_BUFFER_SIZE = 200;
	private int bundleid_;
	public BundlePayload(Lock lock) {

		location_ = location_t.DISK;
		length_ = 0;
		lock_ = lock;

	}

	/**
	 * Options for payload location state.
	 */
	public static enum location_t {
		MEMORY(1), 
		DISK(2), 
		NODATA(3), 
		;

		private static final Map<Integer, location_t> lookup = new HashMap<Integer, location_t>();

		static {
			for (location_t s : EnumSet.allOf(location_t.class))
				lookup.put(s.getCode(), s);
		}

		private int code;

		private location_t(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static location_t get(int code) {
			return lookup.get(code);
		}
	}

	/**
	 * Actual payload initialization function.
	 */
	public void init(int bundleid, location_t location) {
		bundleid_ = bundleid;
		location_ = location;
		
		if ( location == location_t.NODATA)
			return;

		
		if (location == location_t.MEMORY)
			 data_ = new byte[DEFAULT_DATA_BUFFER_SIZE];
		if (location_ == location_t.DISK)
		// Only when the location is DISK
		file_ = BundleStore.getInstance().get_payload_file(bundleid);

	}

	/** 
	 * Get File object, this will be valid in case of Payload location as Disk
	 */
	public File file()
	{
		return file_;
	}
	/**
	 * Set the payload length in preparation for filling in with data. This will
	 * remove all existing data in case of Memory
	 */
	public void set_length(int length) {

		length_ = length;
		if (location_ == location_t.MEMORY) {
			lock_.lock();
			try {
				
				byte[] old_data =  data_.clone();
				int old_length = old_data.length;
				data_ = new byte[length];

				if (old_length < length)
				{
					for(int i=0; i < old_data.length;i++)
					{
						data_[i] = old_data[i];
					}
				}
			} finally {
				lock_.unlock();
			}
		}
	}

	/**
	 * Move payload data from the current file object to the newly created file in API temp folder. 
	 * @return
	 */
	public boolean move_data_to_api_temp_folder()
	{
		Context context = DTNService.context();
		String TempPrefixName = context.getResources().getString(R.string.DTNAPITempFilePrefix);
		File dir = DTNService.context().getDir(TempPrefixName, Context.MODE_PRIVATE);
		try {
			File file = File.createTempFile(
					"bundle_payload_for_api_bid" + bundleid_, ".dat", dir);
			copy_to_file(file);
			
			file_ = file;
			
			return true;
		} catch (IOException e) {
			Log.e(TAG, "migrate IO Exception");
			return false;
		}
	}
	
	/**
	 * Truncate the payload.
	 * 
	 * @throws BundlePayloadWrongTypeException
	 * @throws IOException
	 */
	public void truncate(int length) {
		lock_.lock();
		try {
			assert (length <= length_);
			length_ = length;

			switch (location_) {
			case MEMORY:

				// copy existing data to new temp
				byte[] temp = new byte[length];
				for(int i=0; i < length; i ++)
				{
					temp[i] = data_[i];
				}
				data_ = temp;
				length_ = length;
				break;
			case DISK:
				RandomAccessFile file_handle_ =   pin_file_handle();
				file_handle_.setLength(length);
				unpin_file_handle(file_handle_);
				break;
			case NODATA:
			}
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		} catch (BundlePayloadWrongTypeException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			lock_.unlock();
		}
	}

	/**
	 * The payload length.
	 */
	public int length() {
		return length_;
	}

	/**
	 * The payload location.
	 */
	public location_t location() {
		return location_;
	}

	/**
	 * Set the payload data and length.
	 * 
	 * @throws IOException
	 */
	public void set_data(IByteBuffer bp, int len) {
		set_length(len);
		write_data(bp, 0, len);
	}

	/**
	 * Set payload data and length according to byte array
	 */
	public void set_data(byte[] byte_array) {
		
		IByteBuffer temp = new SerializableByteBuffer(byte_array.length);
		temp.put(byte_array);
		temp.rewind();
		set_data(temp, byte_array.length);
	}
	
	/**
	 * Append a chunk of payload data, extending the length to accomodate the
	 * new data.
	 * 
	 * @throws IOException
	 */

	public void append_data(IByteBuffer bp, int len) {
		lock_.lock();
		try {
			int old_length = length_;
			set_length(length_ + len);
			internal_write(bp, old_length, len);

		} finally {
			lock_.unlock();
		}
	}

	/**
	 * Write a chunk of payload data at the specified offset. The length must
	 * have been previously set to at least offset + len.
	 * 
	 * @throws IOException
	 */
	public void write_data(IByteBuffer bp, int offset, int len) {
		lock_.lock();
		try {
			assert length_ >= len + offset : "BundlePayload:write_data, lengh is less than len+offset";
			internal_write(bp, offset, len);
		} finally {
			lock_.unlock();
		}
	}
	
	

	/**
	 * Writes len bytes of payload data from another payload at the given
	 * src_offset to the given dst_offset.
	 * The data to be read can not be larger than 1024.
	 * @throws IOException
	 * @throws BundlePayloadWrongTypeException
	 */
	public void write_data(final BundlePayload src, int src_offset, int len,
			int dst_offset) {
		lock_.lock();
		try {
			Log.d(TAG, String.format(
					"write_data: length_=%d src_offset=%d "
							+ "dst_offset=%d len %d", 
					length_, src_offset, dst_offset, len));

			assert (length_ >= dst_offset + len) : "BundlePayload:write_data, length_ is not longer than dest offset + length ";
			assert (src.length() >= src_offset + len) : "BundlePayload:write_data, src.length is not longer than dest offset + length ";

			IByteBuffer buf = new SerializableByteBuffer(1024);

			src.read_data(src_offset, len, buf);

			internal_write(buf, dst_offset, len);

		} finally {
			lock_.unlock();
		}

	}

	/**
	 * Get a pointer to the memory buffer.
	 * 
	 * @throws BundleLockNotHeldByCurrentThread
	 */
	public byte[] memory_buf() throws BundleLockNotHeldByCurrentThread {
		if (!lock_.isHeldByCurrentThread()) {
			throw new BundleLockNotHeldByCurrentThread();
		} else
			return data_;
	}

	/**
	 * Copy Payload content to the specified file regardless of payload location.
	 * @param file target file
	 */
	public boolean copy_to_file(File file)
	{
		lock_.lock();
		FileOutputStream out = null;
		try {
		
			

		out = new FileOutputStream(file);
		int DEFAULT_BUFFER_SIZE = 1024;
		
		int len = DEFAULT_BUFFER_SIZE;
		int left = length_;
		int offset = 0;
		do
		{
			if (left < DEFAULT_BUFFER_SIZE) 
			{
				len = left;
			}
			else
			{
				len = DEFAULT_BUFFER_SIZE;
			}
			byte[] byte_array_buf = new byte[len];
			read_data(offset, len, byte_array_buf);
			
			out.write(byte_array_buf);
			
			offset+= len;
			left  -= len;
		}
		while(   left > 0   );
	} catch (FileNotFoundException e) {
		
		Log.e(TAG, e.getMessage());
		return false;
	} catch (IOException e) {
		Log.e(TAG, e.getMessage());
		return false;
	} finally {
		try {
			out.close();

			
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		
		lock_.unlock();
	}
	
		
		return true;
	}
	
	/**
	 * Replace existing payload with the specified file according to the input file.
	 * @param file input file
	 * @return
	 */
	public boolean replace_with_file(File file) {
		lock_.lock();
		FileInputStream in = null;
		try {  
			if (location_ != location_t.DISK) throw new BundlePayloadWrongTypeException();
			assert location_ == location_t.DISK;

			set_length((int)file.length());
			in = new FileInputStream(file);

			RandomAccessFile file_handle_ = pin_file_handle();
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			int offset = 0;
			while ((len = in.read(buf)) > 0) {

				file_handle_.write(buf, 0, len);
				offset += len;
			}
			
			unpin_file_handle(file_handle_);
			
		} catch (FileNotFoundException e) {
			
			Log.e(TAG, e.getMessage());
			return false;
		} catch (BundlePayloadWrongTypeException e) {
			Log.e(TAG, e.getMessage());
			return false;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return false;
		} finally {
			try {
				in.close();

			
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			lock_.unlock();
			return false;
			}
		}
		
			return true;
	}
	
	/**
	 * Read the payload content from offset position with len bytes
	 * and copy into the beginning of byte_array.
	 */
	public final boolean read_data(int offset, int len, byte[] byte_array) {
		lock_.lock();
		try {
			assert length_ >= offset + len : "BundlePayload:read_data, the offset+len is larger than length_";

			assert (byte_array != null) : "BundlePayload:read_data, byte_array is null";

			
			switch (location_) {
			case MEMORY:

				
				for (int i = offset, j=0; i < offset + len && j < len ; i++, j++)
				{
					byte_array[j] = data_[i];
				}
				
				
				break;

			case DISK:
				RandomAccessFile file_handle_ =  pin_file_handle();
				file_handle_.seek(offset);
				file_handle_.read(byte_array);
			

				unpin_file_handle(file_handle_);
				break;

			case NODATA:
				throw new BundlePayloadWrongTypeException();
			}
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
			return false;
		} catch (BundlePayloadWrongTypeException e) {
			Log.e(TAG, e.getMessage());
			return false;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return false;
		} finally {
			lock_.unlock();
		}

		return true;
	}

	
	/**
	 * Copy out a chunk of payload data into the supplied Byte buffer. The data
	 * will be pasted from the current position of the buffer
	 * The position of the Byte Buffer (buf) will not be moved from this operation
	 * @return pointer to the buffer for convenience
	 * @throws BundlePayloadWrongTypeException
	 * @throws IOException
	 */
	public final boolean read_data(int offset, int len, IByteBuffer buf) {
		lock_.lock();
		buf.mark();
		try {
			assert length_ >= offset + len : "BundlePayload:read_data, the offset+len is larger than length_";

			assert (buf != null) : "BundlePayload:read_data, buf is null";

			
			switch (location_) {
			case MEMORY:

				
				for (int i = offset; i < offset + len ; i++)
				{
					buf.put(data_[i]);
				}
				
				break;

			case DISK:
				byte[] temp = new byte[len];
				RandomAccessFile file_handle_ =  pin_file_handle();
				 file_handle_.seek(offset);
				file_handle_.read(temp);
				buf.put(temp);

				unpin_file_handle(file_handle_);
				break;

			case NODATA:
				throw new BundlePayloadWrongTypeException();
			}
			return true;
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
			return false;
		} catch (BundlePayloadWrongTypeException e) {
			Log.e(TAG, e.getMessage());
			return false;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return false;
		} finally {
			buf.reset();
			lock_.unlock();
		}

	}

	/**
	 * Connect the file handle to internal file object for reading or writing.
	 * This is applicable only for the location type DISK.
	 */
	protected RandomAccessFile pin_file_handle() throws FileNotFoundException,
			BundlePayloadWrongTypeException {
		if (location_ != location_t.DISK)
			throw new BundlePayloadWrongTypeException();

		return new RandomAccessFile(file_, "rwd");
	}

	/**
	 * Close the file_ to return resource
	 */
	protected void unpin_file_handle(RandomAccessFile file_handle_) throws IOException,
			BundlePayloadWrongTypeException {
		if (location_ != location_t.DISK)
			throw new BundlePayloadWrongTypeException();

		file_handle_.close();
	}

	public final Lock test_get_lock() {
		return lock_;
	}

	

	/**
	 * Write data from the current buffer position to the payload offset with the length
	 */
	protected void internal_write(IByteBuffer bp, int offset, int len) {
		assert lock_.isHeldByCurrentThread() : "BundlePayload:internal_write, lock not held by current Thread";
		assert length_ >= (offset + len) : "BundlePayload:internal_write, length_ is not longer than offset + len";

		byte[] temp = new byte[len];
		switch (location_) {

		case MEMORY:

			bp.mark();
			try
			{
				for( int i = offset; i < offset + len; i++)
				{
					data_[i] = bp.get();
				}
			}
			finally
			{
				bp.reset();
			}
			break;
		case DISK:
			// check if we need to seek
			bp.mark();
			try {
				
				RandomAccessFile file_handle_ =  pin_file_handle();
				bp.get(temp);
				file_handle_.seek(offset);

				file_handle_.write(temp);

				
				
				unpin_file_handle(file_handle_);
			} catch (BundlePayloadWrongTypeException e) {

				Log.e(TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
			finally
			{
				bp.reset();
			}
			break;
		case NODATA:
			
		}
	}

	/**
	 * location of the data
	 */
	protected location_t location_; // /< 

	/**
	 * internal payload data in case of memory
	 */
	protected byte[] data_;
	
	/**
	 * the payload length
	 */
	protected int length_; 
	
	/**
	 * file handle, for location = DISK only
	 */
	protected File file_;
	
	/**
	 * the lock for this Bundle payload to support mutual exclusion
	 */
	protected Lock lock_;
};
