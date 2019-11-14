/*
SPDX-License-Identifier: Apache-2.0
*/
package org.hyperledger.fabric.ledger.impl;

import org.hyperledger.fabric.ledger.Collection;
import org.hyperledger.fabric.ledger.Ledger;
import org.hyperledger.fabric.shim.ChaincodeSpi;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.impl.ChaincodeSpiImpl;
import org.hyperledger.fabric.shim.impl.InnvocationStubImpl;

public class LedgerImpl implements Ledger {

	// The Chaincode Stub or SPI to provide access to the underlying Fabric 
	// APIs
	private ChaincodeStub stub;
	
	protected ChaincodeSpi getSPI() {
				
		return new ChaincodeSpiImpl((InnvocationStubImpl)this.stub);
	}
	
	public LedgerImpl(ChaincodeStub stub) {
		this.stub = stub;
	}
	
	@Override
	public Collection getCollection(String name) {
		return new CollectionImpl(name,this);
	}

	@Override
	public Collection getDefaultCollection() {
		return this.getCollection(Collection.WORLD);
	}

	@Override
	public Collection getOrganizationCollection(String mspid) {
		// TODO Auto-generated method stub
		return null;
	}

}
