/*
SPDX-License-Identifier: Apache-2.0
*/

package org.hyperledger.fabric.ledger.impl;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.ledger.Collection;
import org.hyperledger.fabric.ledger.QueryHandlerFactory;
import org.hyperledger.fabric.ledger.State;
import org.hyperledger.fabric.shim.ChaincodeSpi;

public class CollectionImpl implements Collection {

	private String name;
	private ChaincodeSpi spi;
	
	public CollectionImpl(String name, LedgerImpl ledgerImpl) {
		this.name = name;
		this.spi = ledgerImpl.getSPI();
	}

	@Override
	public State getState(String key) {
		byte[] stateBytes = this.spi.getState(name, key);		
		return new StateImpl(stateBytes);
	}


	@Override
	public void deleteState(String key) {
		this.spi.deleteState(name, key);
		
	}

	@Override
	public CollectionIterable<State> getStates(QueryHandlerFactory queryFactory) {
		return queryFactory.getIterable(this.name, spi);
	}

	
	
	@Override
	public <T> @DataType T getObject(String key, Class<@DataType T> clz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R> CollectionIterable<R> getStates(QueryHandlerFactory queryFactory, Class<R> clz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public State createState(String key, byte[] valueAsBytes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public State updateState(String key, byte[] valueAsBytes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public State createState(String key, @DataType Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public State updateState(String key, @DataType Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

}
