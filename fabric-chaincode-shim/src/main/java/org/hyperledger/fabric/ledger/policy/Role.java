/*
SPDX-License-Identifier: Apache-2.0
*/
package org.hyperledger.fabric.ledger.policy;

import java.util.HashMap;
import java.util.Map;


/**
 * RoleType of an endorsement policy's identity
 */
public enum Role {
    /**
     * RoleTypeMember identifies an org's member identity
     */
    MEMBER("member"),PEER("peer"),ADMIN("admin"),CLIENT("client");


    private String val;

    Role(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    static Map<String, Role> reverseLookup = new HashMap<>();

    static {
        for (Role item : Role.values()) {
            reverseLookup.put(item.getVal(), item);
        }
    }

    public static Role forVal(String val) {
        if (!reverseLookup.containsKey(val)) {
            throw new IllegalArgumentException("role type "+ val + " does not exist");
        }
        return reverseLookup.get(val);
    }
}