package org.p2p.solanaj.core;

import org.bitcoinj.core.Base58;
import org.p2p.solanaj.utils.ShortvecEncoding;
import org.p2p.solanaj.utils.TweetNaclFast;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Transaction {

    public static final int SIGNATURE_LENGTH = 64;

    private final Message message;
    private final List<String> signatures;
    private byte[] serializedMessage;
    private PublicKey feePayer;

    public Transaction() {
        this.message = new Message();
        this.signatures = new ArrayList<>();
    }

    public Transaction addInstruction(TransactionInstruction instruction) {
        message.addInstruction(instruction);

        return this;
    }

    public void setRecentBlockHash(String recentBlockhash) {
        message.setRecentBlockHash(recentBlockhash);
    }

    public void setFeePayer(PublicKey feePayer) {
        this.feePayer = feePayer;
    }

    public void sign(Account signer) {
        sign(Arrays.asList(signer));
    }

    public void sign(List<Account> signers) {

        if (signers.size() == 0) {
            throw new IllegalArgumentException("No signers");
        }

        if (feePayer == null) {
            feePayer = signers.get(0).getPublicKey();
        }
//        message.setFeePayer(feePayer);

        serializedMessage = message.serialize();

        for (Account signer : signers) {
            TweetNaclFast.Signature signatureProvider = new TweetNaclFast.Signature(new byte[0], signer.getSecretKey());
            byte[] signature = signatureProvider.detached(serializedMessage);

            signatures.add(Base58.encode(signature));
        }
    }

    public byte[] serialize() {
        int signaturesSize = signatures.size();
        byte[] signaturesLength = ShortvecEncoding.encodeLength(signaturesSize);

        ByteBuffer out = ByteBuffer
                .allocate(signaturesLength.length + signaturesSize * SIGNATURE_LENGTH + serializedMessage.length);

        out.put(signaturesLength);

        for (String signature : signatures) {
            byte[] rawSignature = Base58.decode(signature);
            out.put(rawSignature);
        }

        out.put(serializedMessage);

        return out.array();
    }

    public Message getMessage() {
        return message;
    }

    public String getSignature() {
        if (signatures.size() > 0) {
            return signatures.get(0);
        }

        return null;
    }
}
