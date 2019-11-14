/*
SPDX-License-Identifier: Apache-2.0
*/
package org.hyperledger.fabric.ledger;

import org.hyperledger.fabric.FuturePrototype;
import org.hyperledger.fabric.contract.annotation.DataType;

public interface StateHistory {
	
    /**
     * Returns the transaction id.
     *
     * @return tx id of modification
     */
    String getTxId();

    /**
     * Returns the state at the time returned by {@link #getTimestamp()}.
     *
     * @return value
     */
    State getState();

    /**
     * Returns the timestamp of the key modification entry.
     *
     * @return timestamp
     */
    java.time.Instant getTimestamp();

    /**
     * Returns the deletion marker.
     *
     * @return is key was deleted
     */
    boolean isDeleted();
    
	@FuturePrototype
	<@DataType T> T getObject(Class<T> clz);    
}
