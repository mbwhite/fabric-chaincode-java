/*
SPDX-License-Identifier: Apache-2.0
*/
package org.hyperledger.fabric.ledger;

import org.hyperledger.fabric.ledger.Collection.CollectionIterable;
import org.hyperledger.fabric.shim.ChaincodeSpi;

public interface QueryHandlerFactory {

	CollectionIterable<State> getIterable(String collection, ChaincodeSpi spi);
	
}
