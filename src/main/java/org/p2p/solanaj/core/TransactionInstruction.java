package org.p2p.solanaj.core;

import java.util.List;

public class TransactionInstruction {

    private List<AccountMeta> keys;
    private PublicKey programId;
    private byte[] data;

    public TransactionInstruction(PublicKey programId, List<AccountMeta> keys, byte[] data) {
        this.programId = programId;
        this.keys = keys;
        this.data = data;
    }

    public List<AccountMeta> getKeys() {
        return keys;
    }

    public PublicKey getProgramId() {
        return programId;
    }

    public byte[] getData() {
        return data;
    }

}
