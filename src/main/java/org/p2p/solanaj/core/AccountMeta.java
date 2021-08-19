package org.p2p.solanaj.core;

import java.util.List;

public class AccountMeta {
    private PublicKey publicKey;
    private boolean isSigner;
    private boolean isWritable;

    public AccountMeta(PublicKey publicKey, boolean isSigner, boolean isWritable) {
        this.publicKey = publicKey;
        this.isSigner = isSigner;
        this.isWritable = isWritable;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public boolean isSigner() {
        return isSigner;
    }

    public boolean isWritable() {
        return isWritable;
    }

    public static int findAccountIndex(List<AccountMeta> accountMetaList, PublicKey key) {
        for (int i = 0; i < accountMetaList.size(); i++) {
            if (accountMetaList.get(i).getPublicKey().equals(key)) {
                return i;
            }
        }

        return -1;
    }
}