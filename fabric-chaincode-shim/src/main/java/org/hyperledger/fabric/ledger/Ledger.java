/*
SPDX-License-Identifier: Apache-2.0
*/
package org.hyperledger.fabric.ledger;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.ledger.impl.LedgerImpl;

/**
 * Ledger representing the overall shared Transaction
 * Data of the Network
 * 
 * Is composed of a number of collections, one being the 
 * public or world state, and other private data collections
 * 
 */
public interface Ledger {
	
	/** 	  
	 * Get the Ledger instance that represents the current ledger state.
	 * 
	 * @param name
	 * @return
	 */
	public static Ledger getLedger(Context ctx) {
		return new LedgerImpl(ctx.getStub());
	};
		
	/** Return the general collection based on the supplied name
	 * Named ones are the private data collections
	 * 
	 * @param name
	 * @return
	 */
	Collection getCollection(String name);
	
	/**
	 * Return the World State collection
	 * 
	 * @return
	 */
	Collection getDefaultCollection();
	
	/**
	 * Get the collection for the given MSPID
	 */
	Collection getOrganizationCollection(String mspid);

}
