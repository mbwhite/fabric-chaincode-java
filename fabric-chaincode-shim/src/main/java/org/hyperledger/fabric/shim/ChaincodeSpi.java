/*
Copyright IBM Corp. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/
package org.hyperledger.fabric.shim;

import org.hyperledger.fabric.protos.peer.ChaincodeShim.ChaincodeMessage;

import com.google.protobuf.ByteString;

public interface ChaincodeSpi {

	byte[] getState(String name, String key);

	void putState(String name, String key, byte[] valueAsBytes);

	void deleteState(String name, String key);
	
	ByteString invokeRequest(ChaincodeMessage.Type type,ByteString data);
		
}
