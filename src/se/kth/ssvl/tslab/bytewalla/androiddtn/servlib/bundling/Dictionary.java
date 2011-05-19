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

import java.io.Serializable;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.IByteBuffer;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.SerializableByteBuffer;
import android.util.Log;


/**
 * Data structure to handle dictionary data as defined in bundle protocol specification.
 * Every string is separated by null value.
 * 
 *  @author Sharjeel Ahmed (sharjeel@kth.se)
 */

public class Dictionary implements Serializable {

	/**
	 * SerialVersionID to Support Serializable.
	 */
	private static final long serialVersionUID = -6315587474343864675L;

	/**
	 * TAG for Android Logging
	 */

	private static final String TAG = "Dictionary";
	
	/** 
	 * Initialize Dictionary
	 */
    public Dictionary(){
    	dict_ = new byte[100];
    	dict_length_ = 100;
    	length_ = 0 ;
    }
    
	/** 
	 * Return the length of data on dictionary
	 * @return Length of data on dictionary 
	 */

    final public int length() { 
    	return length_; 
    }

	/** 
	 * Return the total length of dictionary
	 * @return Length of dictionary 
	 */
    
    final public int dict_length() { 
    	return dict_length_; 
    }

	/** 
	 * Return dictionary
	 * @return Length the object of dictionary 
	 */

    public final byte[] dict() { 
    	return dict_; 
    }

	/** 
	 * Set the dictionary from a buffer of size length.
	 * Take the data from current position of buffer 
	 * of size length and copy in new dictionary.
	 * @param buff Serializable buffer populated with data
	 * @param length Number of bytes copy from buff.
	 */

    public void set_dict(final IByteBuffer buff, int length){
    	Log.d(TAG, "set dict lent"+length);
    	dict_ = new byte[length];
    	Log.d(TAG, "set dict position"+buff.position());
        buff.mark();
        buff.get(dict_);
        buff.reset();
        
    	length_ = dict_length_ = length;
    }
    
    /**
     * Add the given string to the dictionary if it doesn't already
     * exist.
     * @param str String to add in dictionary.
     */
    public void add_str(String str){
    	long[] offset = new long[1];
    	offset[0] = 0;
    	
    	if(get_offset(str.getBytes(), offset)){
    		return;
		}
    	
        if (dict_length_ < length_ + str.length() + 1) {
        	
            do {
                dict_length_ = (dict_length_ == 0) ? 64 : dict_length_ * 2;
            } while (dict_length_ < length_ + str.length() + 1);
            
            byte[] new_byte_array = new byte[dict_length_];
            
            for(int i=0;i<dict_.length;i++){
            	new_byte_array[i] = dict_[i];
            }
            	
            dict_ = new_byte_array;

        }
        byte[] str_byte = str.getBytes();

        for(int i=0;i<str_byte.length;i++){
        	dict_[length_] = str_byte[i];
        	length_++;
        }
        
        dict_[length_] = (byte) '\0';
        length_++;
        
    }

    /**
     * Add the scheme and ssp of the given endpoint id to the dictionary.
     * @param eid EndpointID to add in dictionary
     */
    public void add_eid(final EndpointID eid)
    {
    	if(eid.valid()){
    		add_str(eid.scheme_str());
    		add_str(eid.ssp());
    	}
    }

    /**
     * Find given string in dictionary. If found set the offset of string and return.
     * @param find String value converted to a byte array
     * @param offset Set offset on the first index of offset array if given string found.
     * @return True if given string found in dictionary else false. 
     */
    public boolean get_offset(final byte[] find, long offset[]){
		
		byte[] source = dict_;
		int k, offset_temp = 0;
		for(int i=(int)offset[0];i<source.length;i++){
			k = offset_temp = i;
			for(int j=0;j<find.length && k<source.length ;j++,k++){
				if(source[k]!=find[j]){
					break;
				}
				
				if(j==find.length-1){
					offset[0] = offset_temp;
					return true;
				} 
			}
		}    	
    	
		offset[0] = offset_temp-1;
		return false;
    }

    /**
     * Find given string in dictionary. If found set the offset of string and return.
     * @param find String value converted to a byte array
     * @param offset Set offset on the first index of offset array if given string found.
     * @return True if given string found in dictionary else false. 
     */

    public boolean get_offset(final byte[] find, int offset[]){
		
		byte[] source = dict_;
		int k, offset_temp = 0;
		for(int i=offset[0];i<source.length;i++){
			k = offset_temp = i;
			for(int j=0;j<find.length && k<source.length ;j++,k++){
				if(source[k]!=find[j]){
					break;
				}
				
				if(j==find.length-1){
					offset[0] = offset_temp;
					return true;
				} 
			}
		}    	
    	
		offset[0] = offset_temp-1;
		return false;
    }    
    /**
     * Find given endpoint id in dictionary. If found set the 
     * scheme_offset & ssp_offset and return true.
     * @param eid EndpointID to find in dictionary
     * @param scheme_offset Set scheme_offset on the first index
     * of scheme_offset array if scheme part of endpoint id is found.
     * @param ssp_offset Set ssp_offset on the first index
     * of ssp_offset array if ssp part of endpoint id is found.
     * @return True if given endpoint id found in dictionary else false. 
     */
    
    public boolean get_offsets(final EndpointID eid, int[] scheme_offset, int[] ssp_offset)
    {

    	return (get_offset(eid.scheme_str().getBytes(), scheme_offset) &&
               get_offset(eid.ssp().getBytes(), ssp_offset));
    }

    /**
     * Find given endpoint id in dictionary. If found set the 
     * scheme_offset & ssp_offset and return true.
     * @param eid EndpointID to find in dictionary
     * @param scheme_offset Set scheme_offset on the first index
     * of scheme_offset array if scheme part of endpoint id is found.
     * @param ssp_offset Set ssp_offset on the first index
     * of ssp_offset array if ssp part of endpoint id is found.
     * @return True if given endpoint id found in dictionary else false. 
     */

    public boolean get_offsets(final EndpointID eid, long[] scheme_offset, long[] ssp_offset)
    {
    	if(eid.valid()){
    		return (get_offset(eid.scheme_str().getBytes(), scheme_offset) &&
               get_offset(eid.ssp().getBytes(), ssp_offset));
    	}
    	else{
    		return false;
    	}	
    }


    /**
     * Create an eid from the dictionary, given the offsets. 
     * @param eid Empty object of EndpointId
     * @param scheme_offset Scheme offset of eidpoint id
     * @param ssp_offset Ssp offset of eidpoint id 
     * @return True on if endpoint id created successfully else false.
     */
    public boolean extract_eid(EndpointID eid,int scheme_offset, int ssp_offset){
		
        // If there's nothing in the dictionary, return
        if (dict_length_ == 0) {
            Log.e(TAG, "cannot extract eid from zero-length dictionary");
            return false;
        }    
    	
        if (scheme_offset >= (dict_length_ - 1)) {
        	Log.e(TAG, String.format("illegal offset for scheme dictionary offset: "
                    +"offset %d, total length %s", scheme_offset, dict_length_));
        	return false;
        }

        if (ssp_offset >= (dict_length_ - 1)) {
        	Log.e(TAG, String.format("illegal offset for ssp dictionary offset: "
                +"offset %d, total length %s", ssp_offset, dict_length_));
        	return false;
        }
        	
        return eid.assign(get_value(scheme_offset), get_value(ssp_offset));
    }

    /**
     * Create an eid from the dictionary, given the offsets. 
     * @param eid Empty object of EndpointId
     * @param scheme_offset Scheme offset of eidpoint id in the first index of array
     * @param ssp_offset Ssp offset of eidpoint id in the first index of array
     * @return True on if endpoint id created successfully else false.
     */

    public boolean extract_eid(EndpointID eid,long[] scheme_offset, long[] ssp_offset){
    	
    	return extract_eid(eid, (int)scheme_offset[0], (int)ssp_offset[0]);
    	
    }
    
    /**
     * Extract a String from dictionary based on the given offset.  
     * @param offset_val Offset of string
     * @return Extracted string based on offset
     */
    
    public String get_value(int offset_val){
    	int offset[] = new int[1];
    	Log.d(TAG, "Get : "+offset_val);
    	offset[0] = offset_val;
    	
    	byte[] separator = new byte[1];
    	separator[0] = '\0';
    	byte[] temp_bytes = dict_;
    	
    	if(get_offset(separator, offset) && 
    			offset[0]>offset_val){
    		int length = (offset[0]-offset_val);
    		IByteBuffer r = new SerializableByteBuffer(length);
    		
    		for(int i=offset_val;i<offset_val+length;i++ ){
    			r.put(temp_bytes[i]);
    		}
    		return new String(r.array());
    	}
		return null;
    }

    /**
     * byte array to keep the dictionary.
     */
    protected byte[]   dict_;
    
    /**
     * Total length of dictionary.
     */
    protected int dict_length_;
    
    /**
     * Total length of filled dictionary.
     */
    protected int length_;		

}
