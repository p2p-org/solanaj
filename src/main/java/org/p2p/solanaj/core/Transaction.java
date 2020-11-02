package org.p2p.solanaj.core;

import java.nio.ByteBuffer;

import org.p2p.solanaj.utils.ShortvecEncoding;
import org.p2p.solanaj.utils.TweetNaclFast;

public class Transaction {

    private Message messgae;
    private byte[] signature;

    public Transaction() {
        this.messgae = new Message();
    }

    public Transaction addInstruction(TransactionInstruction instruction) {
        messgae.addInstruction(instruction);

        return this;
    }

    public void setRecentBlockHash(String recentBlockhash) {
        messgae.setRecentBlockHash(recentBlockhash);
    }

    public void sign(Account signer) {
        byte[] serializedMessage = messgae.serialize();

        TweetNaclFast.Signature signatureProvider = new TweetNaclFast.Signature(new byte[0], signer.getSecretKey());
        signature = signatureProvider.detached(serializedMessage);
    }

    public byte[] serialize() {
        byte[] serializedMessage = messgae.serialize();

        // TODO signature list
        byte[] signaturesLength = ShortvecEncoding.encodeLength(1);

        ByteBuffer out = ByteBuffer.allocate(signaturesLength.length + signature.length + serializedMessage.length);

        out.put(signaturesLength);
        out.put(signature);

        out.put(serializedMessage);

        return out.array();
    }
}
