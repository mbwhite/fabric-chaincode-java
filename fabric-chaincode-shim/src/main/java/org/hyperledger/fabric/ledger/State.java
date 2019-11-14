/*
SPDX-License-Identifier: Apache-2.0
*/

package org.hyperledger.fabric.ledger;

import java.util.List;

import org.hyperledger.fabric.FuturePrototype;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.ledger.Collection.CollectionIterable;
import org.hyperledger.fabric.ledger.policy.StateBasedEndorsement;

/**
 * A State is the combination of key and value that are contained within a collection.
 * 
 * State-based endorsement should be set on this object as well. 
 * 
 * Represents the things that are contained within the Collections.
 * 
 * @param <T>
 */
public interface State  {
	
	/**
	 * For composite keys, form up the key based on the supplied strings
	 * 
	 * @param attributes String[]
	 * @return Key in the composite namespace
	 */
	static String makeComposite(String type, String... attributes) {
		return null;
	};
	
	
	/**
	 * For composite keys, form up the key based on the supplied strings
	 * 
	 * @param attributes List of Strings
	 * @return Key in the composite namespace
	 */
	static String makeComposite(String type, List<String> attributes) {
		return null;
	};

	/** Return the value held within this state
	 * 
	 * @return
	 */
	byte[] getValue();
	
	/**
	 * Return the key that this state is held in
	 */
	String getKey();
	
    /**
     * Returns a history of key values across time.
     * <p>
     * For each historic key update, the historic value and associated
     * transaction id and timestamp are returned. The timestamp is the
     * timestamp provided by the client in the proposal header.
     * This method requires peer configuration
     * <code>core.ledger.history.enableHistoryDatabase</code> to be true.
     *
     * @param key The state variable key
     * @return an {@link Iterable} of {@link StateHistory}
     */
    CollectionIterable<StateHistory> getHistory();
	
    /**
     * If this a state within a private data collection, then this will
     * return the hash value of this state.
     * 
     * @return the private data hash
     */
    byte[] getHash();	
    
    /**
     * Sets the Endorsement policy of the state 
     * 
     * @param sbe
     */
	State setEndorsement(StateBasedEndorsement sbe);
   
	/**
     * Gets the Endorsement policy of the state 
     * 
     * @param sbe
     */
	StateBasedEndorsement getEndorsement();

	// Future RFC candidates
	
	/**
	 * 
	 */
	@FuturePrototype
	<@DataType T> T getObject(Class<T> clz);
	
}
