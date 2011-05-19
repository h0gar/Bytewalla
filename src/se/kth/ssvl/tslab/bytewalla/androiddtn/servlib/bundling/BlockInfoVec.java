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

import java.util.Iterator;

import se.kth.ssvl.tslab.bytewalla.androiddtn.servlib.bundling.BundleProtocol.bundle_block_type_t;
import se.kth.ssvl.tslab.bytewalla.androiddtn.systemlib.util.List;



/**
 * Class for a list of BlockInfos
 * @author Rerngvit Yanggratoke (rerngvit@kth.se)
 */
public class BlockInfoVec extends List<BlockInfo> {

	/**
	 * Default constructor.
	 */
	 public BlockInfoVec()
	 {
		 super();
		 dict_        = new Dictionary();
	 }
	

	/**
	 * SerialUID to support Java Serializable
	 */
	private static final long serialVersionUID = -3597536043536434003L;

	/**
	 * "Append a block using the given processor and optional source block." [DTN2]
	 * 
	 * @return "the newly allocated block." [DTN2]
	 */
	public BlockInfo append_block(BlockProcessor owner, final BlockInfo source) {
		
		BlockInfo newly_added_block = new BlockInfo(owner, source);
		this.add(newly_added_block);
		return newly_added_block;
	
	}
	
	
	/**
	 * "Find the block for the given type." [DTN2]
	 * @return the block or null if not found
	 */
	public final BlockInfo find_block(bundle_block_type_t type) {
		
		Iterator<BlockInfo> iter = this.iterator();
		while(iter.hasNext())
		{
			BlockInfo block_info = iter.next();
			if (block_info.type() == type)
			{
				return block_info;
			}
		}
		return null;
	}

	/**
	 * Check if an entry exists in the vector for the given block type.
	 */
	public boolean has_block(bundle_block_type_t type) {
		return find_block(type) != null;
	}

	/**
	 * Return the dictionary.
	 */
	public Dictionary dict() {
		return dict_;
	}

	/**
	 * Dictionary for this vector of BlockInfo structures
	 */
	protected Dictionary dict_;

	
}
