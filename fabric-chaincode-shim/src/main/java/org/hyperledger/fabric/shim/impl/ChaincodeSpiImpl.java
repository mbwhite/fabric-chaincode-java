/*
SPDX-License-Identifier: Apache-2.0
*/
package org.hyperledger.fabric.shim.impl;

import org.hyperledger.fabric.protos.peer.ChaincodeShim.ChaincodeMessage;
import org.hyperledger.fabric.shim.ChaincodeSpi;

import com.google.protobuf.ByteString;

public class ChaincodeSpiImpl implements ChaincodeSpi {

	ChaincodeInnvocationTask handler;
	String channelId;
	String txId;
	
	public ChaincodeSpiImpl(InnvocationStubImpl stub) {		
		handler = stub.getHandler();
		channelId = stub.getChannelId();
		txId = stub.getTxId();
	}

	@Override
	public byte[] getState(String collection, String key) {
		ChaincodeMessage msg = ChaincodeMessageFactory.newGetStateEventMessage(channelId, txId, collection, key);
        return this.handler.invoke(msg).toByteArray();
	}

	@Override
	public void putState(String collection, String key, byte[] value) {
		ChaincodeMessage msg = ChaincodeMessageFactory.newPutStateEventMessage(channelId, txId, collection, key, ByteString.copyFrom(value));
		this.handler.invoke(msg);    
	}

	@Override
	public void deleteState(String collection, String key) {
        ChaincodeMessage msg = ChaincodeMessageFactory.newDeleteStateEventMessage(channelId, txId, collection, key);
        this.handler.invoke(msg);
	}

	public ByteString invokeRequest(ChaincodeMessage.Type type, ByteString bytes) {
	        ChaincodeMessage requestMessage = ChaincodeMessageFactory.newEventMessage(type, channelId, txId,
	                bytes);
	        return handler.invoke(requestMessage);
		
	};

	
}
