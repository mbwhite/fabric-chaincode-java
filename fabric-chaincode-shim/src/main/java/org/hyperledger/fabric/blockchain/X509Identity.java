/*
SPDX-License-Identifier: Apache-2.0
*/
package org.hyperledger.fabric.blockchain;

import java.security.cert.X509Certificate;

/**
 * X509 Identity that extends the general serialized identity
 * 
 * 
 * ```
 *   Transaction tx = ctx.getTransaction();
 * 
 *   // get the identity, in the form of a X509
 *   X509Identity x509id = tx.getSerializedIdentity(X509Identity.class);
 *   
 *   x509id.getId();
 * ```
 */
public interface X509Identity extends SerializedIdentity {

        /**
     * getId returns the ID associated with the invoking identity. This ID
     * is guaranteed to be unique within the MSP.
     * @return {String} A string in the format: "x509::{subject DN}::{issuer DN}"
     */
    public String getId() ;


    /**
     * getAttributeValue returns the value of the client's attribute named `attrName`.
     * If the invoking identity possesses the attribute, returns the value of the attribute.
     * If the invoking identity does not possess the attribute, returns null.
     * @param attrName Name of the attribute to retrieve the value from the
     *     identity's credentials (such as x.509 certificate for PKI-based MSPs).
     * @return {String | null} Value of the attribute or null if the invoking identity
     *     does not possess the attribute.
     */
    public String getAttributeValue(String attrName);


    /**
     * assertAttributeValue verifies that the invoking identity has the attribute named `attrName`
     * with a value of `attrValue`.
     * @param attrName Name of the attribute to retrieve the value from the
     *     identity's credentials (such as x.509 certificate for PKI-based MSPs)
     * @param attrValue Expected value of the attribute
     * @return {boolean} True if the invoking identity possesses the attribute and the attribute
     *     value matches the expected value. Otherwise, returns false.
     */
    public boolean assertAttributeValue(String attrName, String attrValue);


    /**
     * getX509Certificate returns the X509 certificate associated with the invoking identity,
     * or null if it was not identified by an X509 certificate, for instance if the MSP is
     * implemented with an alternative to PKI such as [Identity Mixer](https://jira.hyperledger.org/browse/FAB-5673).
     * @return {X509Certificate | null}
     */
    public X509Certificate getX509Certificate() ;
}