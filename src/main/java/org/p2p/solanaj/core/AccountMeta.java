package org.p2p.solanaj.core;

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
}