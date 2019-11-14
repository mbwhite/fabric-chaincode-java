/*
SPDX-License-Identifier: Apache-2.0
*/
package org.hyperledger.fabric.ledger.policy;

public class StateBasedEndorsement  {

	private StateBasedEndorsement() {
		
	}
	
	private StateBasedEndorsement(String endorsement) {
		// TODO Auto-generated constructor stub
	}

	public static StateBasedEndorsement build(String endorsement) {
		return new StateBasedEndorsement(endorsement);
	}
	
	public static StateBasedEndorsement build(Expression e) {
		return new StateBasedEndorsement();
	}

	/**
	 * Returns as a string of the policy in standard format
	 */
	public String toString() {
		return "";
	}
	
}
