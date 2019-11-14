/*
SPDX-License-Identifier: Apache-2.0
*/
package org.hyperledger.fabric.ledger.policy;

public class Expression implements ExpressionElement {

	public static Expression and(ExpressionElement... elements) {
		return new Expression();
	}

	public static Expression or(ExpressionElement... elements) {
		return new Expression();
	}
	
	public static Expression oneOf(int count, ExpressionElement... elements) {
		return new Expression();
	}	
}
