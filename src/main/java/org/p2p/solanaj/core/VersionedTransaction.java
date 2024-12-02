package org.p2p.solanaj.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bitcoinj.core.Base58;

public class VersionedTransaction {

    private static int SIGNATURE_LENGTH = 64;

    public TransactionMessage message;
    public List<String> signatures;

    public VersionedTransaction(TransactionMessage message, List<String> signatures) {
        this.message = message;
        this.signatures = signatures;
    }

    public static VersionedTransaction fromEncodeedTransaction(byte[] encodedTx) {

        byte[] byteArray = encodedTx;
        Binary.DecodedLength signaturesDecodedLength = Binary.decodeLength(byteArray);
        byteArray = signaturesDecodedLength.bytes;

        List<String> signatures = new ArrayList<>();
        for (int i = 0; i < signaturesDecodedLength.length; i++) {
            byte[] signature = Arrays.copyOfRange(byteArray, 0, SIGNATURE_LENGTH);
            byteArray = Arrays.copyOfRange(byteArray, SIGNATURE_LENGTH, byteArray.length);
            String encodedSignature = Base58.encode(signature);
            signatures.add(encodedSignature);
        }

        TransactionMessage message = TransactionMessage.deserialize(byteArray);

        if (signaturesDecodedLength.length > 0 &&
            message.header.numRequiredSignatures != signaturesDecodedLength.length) {
            throw new RuntimeException("numRequireSignatures is not equal to signatureCount");
        }

        return new VersionedTransaction(message, signatures);
    }

    public void setRecentBlockhash(String recentBlockhash) {
        message.setRecentBlockhash(recentBlockhash);
    }

    public void addSignature(String signature) {
        if (message.header.numRequiredSignatures == 1 && signatures.size() == 1) {
            signatures.set(0, signature);
        } else {
            signatures.add(signature);
        }
    }

    public byte[] getMessageData() {
        return message.serialize();
    }

    public byte[] serialize() throws IOException {
        if (signatures.isEmpty() || signatures.size() != message.header.numRequiredSignatures) {
            throw new RuntimeException("Signature verification failed");
        }

        byte[] messageData = message.serialize();

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        b.write(Binary.encodeLength(signatures.size()));
        for (String s : signatures) {
            b.write(Base58.decode(s));
        }
        b.write(messageData);
        return b.toByteArray();
    }
}
