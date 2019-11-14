/*
SPDX-License-Identifier: Apache-2.0
*/
package org.hyperledger.fabric.blockchain;

import java.time.Instant;

import org.hyperledger.fabric.FuturePrototype;
import org.hyperledger.fabric.protos.peer.ProposalPackage.SignedProposal;

@FuturePrototype
public interface Transaction {

    /**
     * Get the transaction id as a string
     */
    String getId();

    /**
     * Get transaction timestamp
     */
    Instant getTimestamp();

    /**
     * Set event
     */
    public Transaction setEvent(String name);
    public Transaction setEvent(String name, byte[] payload);

    /** 
     * Get event
     */
    public String getEventName();
    public byte[]  getEventPayload();

    /**
     * Get the raw identity (X.509 or otherwise) of the creator of transaction proposal.
     * Typically this will return either a X509Identity or IdemixIdentity
     * 
     * Pass the Class to create the identity as required
     * 
     * X509Identity and IdemixIdentity are already provided
     * 
     * ```
     *   Transaction tx = ctx.getTransaction();
     * 
     *   // get the identity, in the form of a X509
     *   X509Identity x509id = tx.getSerializedIdentity(X509Identity.class);
     *   
     *   x509id.getId();
     * ``` 
     * 
     */
    public SerializedIdentity getCreatorIdentity(Class<SerializedIdentity> identityClass);

    /**
     * returns a signed copy of the current transaction proposal being processed by the smart contract.
     * 
     * This requires using the protobuf generated classes to parse. 
     * 
     */
    public SignedProposal getSignedProposoal();

    /**
     * Returns a HEX-encoded string of SHA256 hash of the transaction's nonce, creator and epoch 
     * concatenated, as a unique representation of the specific transaction. This value can be 
     * used to prevent replay attacks in chaincodes that need to authenticate an identity independent 
     * of the transaction's submitter. In a chaincode proposal, the submitter will have been authenticated 
     * by the peer such that the identity returned by stub.getCreator() can be trusted. But in some
     *  scenarios, the chaincode needs to authenticate an identity independent of the proposal submitter.
     */
    public String getBinding();



}