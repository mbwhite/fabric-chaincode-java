/*
SPDX-License-Identifier: Apache-2.0
*/
package org.hyperledger.fabric.ledger.policy;

public class Principal implements ExpressionElement {

	String mspId;
	Role role;
	
	public Principal(String mspId, Role role) {
		this.mspId = mspId;
		this.role = role;
	}
	
	public String getMspId() {
		return mspId;
	}
	public void setMspId(String mspId) {
		this.mspId = mspId;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	
	
	
}
