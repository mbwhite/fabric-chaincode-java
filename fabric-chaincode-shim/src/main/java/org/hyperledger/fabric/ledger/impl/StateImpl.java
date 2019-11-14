/*
SPDX-License-Identifier: Apache-2.0
*/
package org.hyperledger.fabric.ledger.impl;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.ledger.Collection.CollectionIterable;
import org.hyperledger.fabric.ledger.StateHistory;
import org.hyperledger.fabric.ledger.State;
import org.hyperledger.fabric.ledger.policy.StateBasedEndorsement;
import org.hyperledger.fabric.protos.ledger.queryresult.KvQueryResult.KV;

public class StateImpl implements State {

	public StateImpl(byte[] stateBytes) {
		// TODO Auto-generated constructor stub
	}

	public StateImpl(KV keyvalue) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public <T> @DataType T getObject(Class<@DataType T> clz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CollectionIterable<StateHistory> getHistory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getHash() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public State setEndorsement(StateBasedEndorsement sbe) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StateBasedEndorsement getEndorsement() {
		// TODO Auto-generated method stub
		return null;
	}

}
