/*
SPDX-License-Identifier: Apache-2.0
*/
package org.hyperledger.fabric.ledger;

import org.hyperledger.fabric.ledger.Collection.CollectionIterable;
import org.hyperledger.fabric.ledger.impl.KeyedIterableImpl;
import org.hyperledger.fabric.shim.ChaincodeSpi;

public enum KeyQueryHandler implements QueryHandlerFactory {

	RANGE(), RANGE_FROM() {
		public KeyQueryHandler to(String toKey) {
			throw new RuntimeException("The 'to' key is pre-defined, can not be changed, use RANGE instead");
		};
	},
	RANGE_TO() {
		public KeyQueryHandler from(String fromKey) {
			throw new RuntimeException("The 'from' key is pre-defined, can not be changed, use RANGE instead");
		};
	},
	PARTIAL_RANGE();

	String fromKey;
	String toKey;
	int pageSize;
	String bookmark;

	public KeyQueryHandler to(String toKey) {
		this.toKey = toKey;
		return this;
	};

	public KeyQueryHandler from(String fromKey) {
		this.fromKey = fromKey;
		return this;
	}

	public KeyQueryHandler pageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}
	
	public KeyQueryHandler bookmark(String bookmark) {
		this.bookmark = bookmark;
		return this;
	}
	
	@Override
	public CollectionIterable<State> getIterable(String collection, ChaincodeSpi spi) {
		return new KeyedIterableImpl(collection, fromKey, toKey, spi);

	};
}
