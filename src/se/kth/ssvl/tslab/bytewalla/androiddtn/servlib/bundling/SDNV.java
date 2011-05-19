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

import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.SerializableByteBuffer;
import android.util.Log;

/**
 * Class to handle parsing and formatting of self describing numeric
 * values (SDNVs). This class is used to encode bundle metadata before sending
 * over the link and then decode on the other side.
 * 
 * Conceptually, the integer value to be encoded is split into 7-bit
 * segments. These are encoded into the output byte stream, such that
 * the high order bit in each byte is set to one for all bytes except
 * the last one.
 * 
 *  @author Sharjeel Ahmed (sharjeel@kth.se) 
 */
public class SDNV {
	
	/**
	 * TAG for Android Logging
	 */
	private final static String TAG = "SDNV";
	
	/**
	 * Maximum length of the SDNV implementation is 10 bytes
	 */
    private final static int MAX_LENGTH = 10;

    /**
     * Function to encode the the given value and store in the buffer
     * @param val Value to encode
     * @param bp Buffer to store the encoded value
     * @param len Number of bytes available in the buffer
     * @return Length of the stored bytes after encoding on the buffer
     */
    public static int encode(long val, IByteBuffer bp, int len)
    {
        IByteBuffer start = bp;

        /*
         * Figure out how many bytes we need for the encoding.
         */
        int val_len = 0;
        long tmp = val;
        
        do {
            tmp = tmp >> 7;
            val_len++;
        } while (tmp != 0);

        assert(val_len > 0)
        :"SDNV: encod val_len greater than 0";
        
        assert(val_len <= MAX_LENGTH)
        :"SDNV: val_len less than MAC size";

        /*
         * Make sure we have enough buffer space.
         */
        if (len < val_len) {
            return -1;
        }

        /*
         * Now advance bp to the last byte and fill it in backwards with
         * the value bytes.
         */
        int current_position = bp.position(); 
        bp.position(current_position+(val_len));
        
        byte high_bit = 0; // for the last octet
        
        do {
            bp.position(bp.position()-1);
            bp.put((byte) (high_bit | (val & 0x7f)));
            high_bit = (byte) (1 << 7); // for all but the last octet
            val = val >> 7;
            bp.position(bp.position()-1);
        } while (val != 0);
        
        bp.position(current_position+(val_len)); // Set position at the end of buffer
        assert(bp == start);

        return val_len;
    }

    /**
     * Function to get the number of bytes needed to encode
     * @param val Number of bytes needed to encode this value
     */
    public static int encoding_len(long val){
    	IByteBuffer buf = new SerializableByteBuffer(16);
    	int ret = encode(val, buf, buf.capacity());
    	
    	assert(ret != -1 && ret != 0)
    	:"SDNV: encoding_len ";
    	    return ret;
    }

    /**
     * Function to get the number of bytes needed to encode
     * @param val Number of bytes needed to encode this value
     */
    public static int encoding_len(long v[]){
    	return encoding_len(v[0]);
    }

    /**
     * Function to get the number of bytes needed to encode
     * @param val Number of bytes needed to encode this value
     */
    public static int encoding_len(int v[]){
    	long val = v[0];
    	return encoding_len(val);
    }

    /**
     * Decode bytes after reading from the given buffer
     * @param bp Buffer with bytes to read and decode
     * @param len Total length of the buffer
     * @param val Set the decoded value on the first index of this long[]
     * @return Number of bytes consumed from buffer
     */
    public static int decode(final IByteBuffer bp, int len, long[] val){
        
    	final IByteBuffer start = bp;
        if (val==null) {
            return -1;
        }
        /*
         * Zero out the existing value, then shift in the bytes of the
         * encoding one by one until we hit a byte that has a zero
         * high-order bit.
         */
        int val_len = 0;
        val[0] = 0;
        int bf = bp.position();
        
        do {
            if (len==0){
            	Log.e(TAG, "Buffer too short Current V: "+ len+" : val_len:"+ val_len);
                return -1; /// buffer too short
            }    
            
            val[0] = (val[0] << 7) | (bp.get(bf) & 0x7f);
            
            ++val_len;
            
            if ((bp.get(bf) & (1 << 7)) == 0){
            	--len;
                ++bf;
                break; // all done;            	
            }
            

            ++bf;
            --len;
        } while (true);

        
        /*
         * Since the spec allows for infinite length values but this
         * implementation only handles up to 64 bits, check for overflow.
         * Note that the only supportable 10 byte SDNV must store exactly
         * one bit in the first byte of the encoding (i.e. the 64'th bit
         * of the original value).
         * This is OK because a spec update says that behavior
         * is undefined for values > 64 bits.
         */
        if ((val_len > MAX_LENGTH) || // ToDo
            ((val_len == MAX_LENGTH) && (start.get() != 0x81)))
        {
        	Log.e("SDNV", "overflow value in sdnv!!!");
            return -1;
        }

        bp.position(bf);

        // a buffer and of error due to overflow or malformed SDNV since
        // callers just assume that they need more data to decode and
        // don't really check for errors
        
        return val_len;
    	
    }
    
    /**
     * Decode bytes after reading from the given buffer
     * @param bp Buffer with bytes to read and decode
     * @param len Total length of the buffer
     * @param val Set the decoded value on the first index of this int[]
     * @return Number of bytes consumed from buffer
     */
    public static int decode(final IByteBuffer bp, int len, int[] val){
        long lval[] = new long[1];
        int ret = decode(bp, len, lval);
        
        if (lval[0] > 0xffffffffL) {
            return -1;
        }

        val[0] = (int)lval[0];
        
        return ret;
    	
    }
    
    /**
     * Decode bytes after reading from the given buffer
     * @param bp Buffer with bytes to read and decode
     * @param val Set the decoded value on the first index of this int[]
     * @return Number of bytes consumed from buffer
     */
    
    public static int decode(final IByteBuffer bp, int[] val){
		return decode(bp, bp.remaining(), val);
    	
    }
    
    /**
     * Decode bytes after reading from the given buffer
     * @param bp Buffer with bytes to read and decode
     * @param val Set the decoded value on the first index of this long[]
     * @return Number of bytes consumed from buffer
    */
    public static int decode(final IByteBuffer bp, long[] val){
		return decode(bp, bp.remaining(), val);
    	
    }

    /**
     * Function to encode the the given value and store in the buffer
     * @param val Value to encode
     * @param bp Buffer to store the encoded value
     * @param len Number of bytes available in the buffer
     * @return Length of the stored bytes after encoding on the buffer
     */
    public static int encode(int val, IByteBuffer bp, int len)
    {	
    	return encode((long)val,bp,len);
    }
    
    /**
     * Function to encode the the given value and store in the buffer
     * @param val Value to encode, Value should be on the first index of the int[]
     * @param bp Buffer to store the encoded value
     * @param len Number of bytes available in the buffer
     * @return Length of the stored bytes after encoding on the buffer
     */

    public static int encode(int[] val, IByteBuffer bp, int len)
    {	
    	return encode((long)val[0],bp,len);
    }
    
    /**
     * Function to encode the the given value and store in the buffer
     * @param val Value to encode
     * @param bp Buffer to store the encoded value
     * @return Length of the stored bytes after encoding on the buffer
     */
    
    public static int encode(int val, IByteBuffer bp)
    {
    	return encode(val, bp, encoding_len(val));
    }
    
    /**
     * Function to encode the the given value and store in the buffer
     * @param val Value to encode, value should be on the first index of the long[]
     * @param bp Buffer to store the encoded value
     * @return Length of the stored bytes after encoding on the buffer
     */
    
    public static int encode(long[] val, IByteBuffer bp)
    {
    	return encode(val[0], bp, encoding_len(val));
    }    
    
    /**
     * Function to encode the the given value and store in the buffer
     * @param val Value to encode
     * @param bp Buffer to store the encoded value
     * @return Length of the stored bytes after encoding on the buffer
     */

    public static int encode(long val, IByteBuffer bp)
    {
    	return encode(val, bp, encoding_len(val));
    }
 
    /**
     * Return the number of bytes which comprise the given value.
     * Assumes that bp points to a valid encoded SDNV.
     * @param bp Buffer populated with bytes
     * @return Number of bytes which comprise the given value
     */
    public static int len(final IByteBuffer bp){
        int val_len = 1;
        bp.mark();
        
        for ( ; (bp.get() & 0x80)!=0; ++val_len )
            ;
        
        bp.reset();
        return val_len;
    }
    
    /**
     * Get the length of bytes can be decoded from the buffer
     * @param n Length of buffer
     * @param bp Buffer to read bytes
     * @return Number of bytes can be decoded
     */
    public static int SDNVs_decoding_len(int n, IByteBuffer bp)
    {
    	int old_position = bp.position();
    
    	try{
    	int sumSDNVs_len = 0;
    	for(int i =1; i <= n; i++)
    	{
	    	int current_SDNV_len =  SDNV.len(bp);
	    	if (current_SDNV_len == -1) return -1;
	    	
	    	
	    	bp.position(bp.position() + current_SDNV_len);
	    	sumSDNVs_len += current_SDNV_len;
    	}
    	return sumSDNVs_len;
    	}
    	finally
    	{
    		bp.position(old_position);
    	}
    }
}
