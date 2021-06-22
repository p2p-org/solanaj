package org.p2p.solanaj.rpc.types;

import lombok.Getter;

import java.util.AbstractMap;

@Getter
public class LargeAccount {

    private final double lamports;
    private final String address;

    @SuppressWarnings("rawtypes")
    public LargeAccount(AbstractMap am) {
        this.lamports = (double) am.get("lamports");
        this.address = (String) am.get("address");
    }

}
