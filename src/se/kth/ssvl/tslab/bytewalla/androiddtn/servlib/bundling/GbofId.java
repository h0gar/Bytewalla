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

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.naming.EndpointID;


/**
 * Class definition for a GBOF ID (Global Bundle Or Fragment ID)
 * 
 *  @author Sharjeel Ahmed (sharjeel@kth.se) 
 */

public class GbofId {

	/**
	 * Default constructor
	 */
	public GbofId(){
	}

	/**
	 * Constructor
	 * 
	 * @param source Bundle source endpoint id
	 * @param creation_ts Creation time of bundle
	 * @param is_fragment True if bundle is a fragment else false
	 * @param frag_length If fragment then fragment length
	 * @param frag_offset If fragment then fragment offset
	 */
	public GbofId(EndpointID source,
           BundleTimestamp creation_ts,
           boolean           is_fragment,
           long       frag_length,
           long       frag_offset){
	    
		  source_ = source;
	      creation_ts_ = creation_ts;
	      is_fragment_ = is_fragment;
	      frag_length_ = frag_length;
	      frag_offset_ = frag_offset;
		
	}
    
	/**
	 * Check if two GbofId are the same
	 * @param id Other GbofId object to compare
	 * @return True if both are the same else false
	 */
	final public boolean equals(final GbofId id){
    	if (creation_ts_.seconds() == id.creation_ts().seconds() &&
                creation_ts_.seqno()   == id.creation_ts().seqno() &&
                is_fragment_          == id.is_fragment_ &&
                (!is_fragment_ || 
                 (frag_length_ == id.frag_length_ && frag_offset_ == id.frag_offset_)) &&
                source_.equals(id.source_)) 
            {
                return true;
            } else {
                return false;
            }
    }

	/**
	 * Check if fields are equal to the this GbofId
	 * @param source Endpoint id to compare with the endpoint id of this
	 * @param bundleTimestamp Bundle timestamp to compare with the timestamp of this 
	 * @param is_fragment Boolean is_frament flag to compare with the is_fragment of this
	 * @param frag_length Fragment frag_length to compare with the frag_lenght of this
	 * @param frag_offset Fragment frag_offset to compare with the frag_offset of this
	 * @return True if all the fields match of this objects' fields else false
	 */
	final public boolean equals(EndpointID source,
                BundleTimestamp bundleTimestamp,
                boolean is_fragment,
                int frag_length,
                int frag_offset){
        
        if (creation_ts_.seconds() == bundleTimestamp.seconds() &&
        		creation_ts_.seqno() == bundleTimestamp.seqno() &&
        		is_fragment_ == is_fragment &&
        		(!is_fragment || 
        		 (frag_length_ == frag_length && frag_offset_ == frag_offset)) &&
        	        source_.equals(source))
        	    {
        	        return true;
        	    } else {
        	        return false;
        	    }
    	
    }

	/**
	 * Equality operator.
	 * @param id Other GbofId object to compare
	 * @return True if both are the same else false
	 */
	final public boolean isEqual(final GbofId id){
        return equals(id);
    }

    /**
     * Comparison operator if LessThan
	 * @param other Other GbofId object to compare with this
	 * @return True if this is less than other else false
     */
    final boolean isLessThan(final GbofId other){
        if (source_.is_less_than(other.source()) ) return true;
        if (other.source().is_less_than(source_)) return false;

        if (creation_ts_.isLessThan(other.creation_ts())) return true;
        if (creation_ts_.isGreaterThan(other.creation_ts())) return false;

        if (is_fragment_  && !other.is_fragment_) return true;
        if (!is_fragment_ && other.is_fragment_) return false;
        
        if (is_fragment_) {
            if (frag_length_ < other.frag_length_) return true;
            if (other.frag_length_ < frag_length_) return false;

            if (frag_offset_ < other.frag_offset_) return true;
            if (other.frag_offset_ < frag_offset_) return false;
        }

        return false; // all equal
    }
    
	/**
	 * Get the source endpoint id
	 * @return the source_
	 */
	public EndpointID source() {
		return source_;
	}
	/**
	 * Set the source endpoint id
	 * @param source the source_ to set
	 */
	public void set_source(EndpointID source) {
		source_ = source;
	}
	/**
	 * Get the creation time
	 * @return the creation_ts_
	 */
	public BundleTimestamp creation_ts() {
		return creation_ts_;
	}
	/**
	 * Set the bundle creation time
	 * @param creation_ts the creation_ts_ to set
	 */
	public void set_creation_ts(BundleTimestamp creation_ts) {
		creation_ts_ = creation_ts;
	}
	/**
	 * Get the bundle status if it's fragment or not
	 * @return the is_fragment_
	 */
	public boolean is_fragment() {
		return is_fragment_;
	}
	/**
	 * Set the bundle status if it's a fragment or not
	 * @param is_fragment the is_fragment_ to set
	 */
	public void set_is_fragment(boolean is_fragment) {
		is_fragment_ = is_fragment;
	}
	/**
	 * Get the length of fragment 
	 * @return the frag_length_
	 */
	public long frag_length() {
		return frag_length_;
	}
	/**
	 * Set the length of fragment length
	 * @param fragLength the frag_length_ to set
	 */
	public void set_frag_length(int frag_length) {
		frag_length_ = frag_length;
	}
	/**
	 * @return the frag_offset_
	 */
	public long frag_offset() {
		return frag_offset_;
	}
	/**
	 * Set the fragment offset
	 * @param frag_offset the frag_offset_ to set
	 */
	public void set_frag_offset(long frag_offset) {
		frag_offset_ = frag_offset;
	}


	/**
	 * Source eid
	 */
    protected EndpointID source_;	
    /**
     * Bundle creation timestamp
     */
    protected BundleTimestamp creation_ts_; 
    
    /**
     * Is bundle a fragment
     */
    protected boolean is_fragment_;
    /**
     * Length of fragment
     */
    protected long frag_length_;  	
    
    /**
     * Offset of fragment
     */
    protected long frag_offset_;
	
}
