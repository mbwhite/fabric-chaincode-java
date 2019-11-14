/*
SPDX-License-Identifier: Apache-2.0
*/
package org.hyperledger.fabric.blockchain;

/**
 * Identity returned from the 'getCreator()'
 * This could either by an X509, Idemix or some other msp's identity
 * 
 * Subclasses are provided for the X509 or Idemix. Other implementations will need
 * to use the getRawIdentity() method to decode the id manually
 */
public interface SerializedIdentity {

    // If you wish to provide a custom identity provider
    // please ensure this method is implemented,  This will be called with the 
    // bytes to be parsed.
    public SerializedIdentity setRawIdentity();

    public Byte[] getRawIdentity();

    public String getMspIO();

}