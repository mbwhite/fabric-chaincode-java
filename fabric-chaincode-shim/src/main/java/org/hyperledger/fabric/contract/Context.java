/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/

package org.hyperledger.fabric.contract;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.FuturePrototype;
import org.hyperledger.fabric.blockchain.Transaction;
import org.hyperledger.fabric.shim.Chaincode.Response;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.json.JSONException;

/**
 *
 * This context is available to all 'transaction functions' and provides the
 * transaction context. It also provides access to the APIs for the world state
 * using {@link #getStub()}
 * <p>
 * Applications can implement their own versions if they wish to add
 * functionality. All subclasses MUST implement a constructor, for example
 * <pre>
 * {@code
 *
 * public MyContext extends Context {
 *
 *     public MyContext(ChaincodeStub stub) {
 *        super(stub);
 *     }
 * }
 *
 *}
 *</pre>
 *
 */
public class Context {
    protected ChaincodeStub stub;
    protected ClientIdentity clientIdentity;

    /**
     * Constructor
     * Creates new client identity and sets it as a property of the stub
     * @param stub Instance of the {@link ChaincodeStub} to use
     */
    public Context(ChaincodeStub stub) {
        this.stub = stub;
        try {
            this.clientIdentity = new ClientIdentity(stub);
        } catch (CertificateException | JSONException | IOException e) {
            throw new ContractRuntimeException("Could not create new client identity", e);
        }
    }

    /**
     *
     * @return ChaincodeStub instance to use
     * 
     * @deprecated recommended to use the Ledger API
     * 
     */
    public ChaincodeStub getStub() {
        return this.stub;
    }

    /**
     *
     * @deprecated recommended to access this via the transaction
     * 
     * @return ClientIdentity object to use
     */
    public ClientIdentity getClientIdentity() {
        return this.clientIdentity;
    }


    public String getFunction(){
        return "";
    }

    public List<String> getParameters(){
    	return new ArrayList<String>();
    }
   
    @FuturePrototype
    public Transaction getTransaction() {
        return null;
    }

    /**
     * Returns the channel id for the current proposal.
     * <p>
     * This would be the 'channel_id' of the transaction proposal
     * except where the chaincode is calling another on a different channel.
     *
     * @return the channel id
     */
    String getChannelId() {
    	return "";
    };
    
    /**
     * Invoke another chaincode using the same transaction context.
     * <p>
     * Same as {@link #invokeChaincode(String, List, String)}
     * using channelId to <code>null</code>
     *
     * @param chaincodeName Name of chaincode to be invoked.
     * @param args          Arguments to pass on to the called chaincode.
     * @return {@link Response} object returned by called chaincode
     */
    Object invokeChaincode(String chaincodeName, List<String> args) {
        return null;
    }

    /**
     * Invoke another chaincode using the same transaction context.
     * <p>
     * This is a convenience version of
     * {@link #invokeChaincode(String, List, String)}. The string args will be
     * encoded into as UTF-8 bytes.
     *
     * @param chaincodeName Name of chaincode to be invoked.
     * @param args          Arguments to pass on to the called chaincode.
     * @param channel       If not specified, the caller's channel is assumed.
     * @return {@link Response} object returned by called chaincode
     */
    Object invokeChaincode(String chaincodeName, List<String> args, String channel) {
        return null;
    }
    
}
