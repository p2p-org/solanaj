package org.p2p.solanaj.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bitcoinj.core.Base58;


public class TransactionMessage {

    private MessageVersion version;
    public MessageHeader header;
    private List<PublicKey> accountKeys = new ArrayList();
    private String recentBlockhash;
    private List<CompiledInstruction> instructions = new ArrayList();
    private List<CompiledAddressLookupTable> addressLookupTables = new ArrayList();

    private static final int RECENT_BLOCK_HASH_LENGTH = 32;

    public TransactionMessage(MessageVersion version, MessageHeader header, List<PublicKey> accounts,
                                 String recentBlockhash, List<CompiledInstruction> instructions,
                                 List<CompiledAddressLookupTable> addressLookupTables) {
        this.version = version;
        this.header = header;
        this.accountKeys = accounts;
        this.recentBlockhash = recentBlockhash;
        this.instructions = instructions;
        this.addressLookupTables = addressLookupTables;
    }

    private enum MessageVersion {
        Legacy, V0
    }

    public static class MessageHeader {

        static final int HEADER_LENGTH = 3;
        public int numRequiredSignatures;
        public int numReadonlySignedAccounts;
        public int numReadonlyUnsignedAccounts;

        public MessageHeader(int numRequiredSignatures, int numReadonlySignedAccounts,
                                int numReadonlyUnsignedAccounts) {
            this.numRequiredSignatures = numRequiredSignatures;
            this.numReadonlySignedAccounts = numReadonlySignedAccounts;
            this.numReadonlyUnsignedAccounts = numReadonlyUnsignedAccounts;
        }

        byte[] toByteArray() {
            byte numRequiredSignatures = (byte) this.numRequiredSignatures;
            byte numReadonlySignedAccounts = (byte) this.numReadonlySignedAccounts;
            byte numReadonlyUnsignedAccounts = (byte) this.numReadonlyUnsignedAccounts;
            return new byte[]{numRequiredSignatures, numReadonlySignedAccounts, numReadonlyUnsignedAccounts};
        }
    }

    public static class CompiledInstruction {

        public byte[] data;
        public byte[] accounts;
        public int programIdIndex;

        public CompiledInstruction(byte[] data, byte[] accounts, int programIdIndex) {
            this.data = data;
            this.accounts = accounts;
            this.programIdIndex = programIdIndex;
        }

        public boolean equals(CompiledInstruction other) {
            if (this == other) {
                return true;
            }

            if (!Arrays.equals(data, other.data)) {
                return false;
            }
            if (!Arrays.equals(accounts, other.accounts)) {
                return false;
            }
            if (programIdIndex != other.programIdIndex) {
                return false;
            }

            return true;
        }

        public int hashCode() {
            int result = java.util.Arrays.hashCode(data);
            result = 31 * result + Arrays.hashCode(accounts);
            result = 31 * result + programIdIndex;
            return result;
        }

    }

    public static class CompiledAddressLookupTable {

        public PublicKey publicKey;
        public byte[] writableIndexes;
        public byte[] readonlyIndexes;

        public CompiledAddressLookupTable(PublicKey publicKey, byte[] writableIndexes, byte[] readonlyIndexes) {
            this.publicKey = publicKey;
            this.writableIndexes = writableIndexes;
            this.readonlyIndexes = readonlyIndexes;
        }

        public boolean equals(CompiledAddressLookupTable other) {
            if (this == other) {
                return true;
            }

            if (!Arrays.equals(writableIndexes, other.writableIndexes)) {
                return false;
            }
            if (!Arrays.equals(readonlyIndexes, other.readonlyIndexes)) {
                return false;
            }
            return publicKey.equals(other.publicKey);
        }

        public int hashCode() {
            int result = publicKey.hashCode();
            result = 31 * result + java.util.Arrays.hashCode(writableIndexes);
            result = 31 * result + java.util.Arrays.hashCode(readonlyIndexes);
            return result;
        }
    }

    public void setRecentBlockhash(String recentBlockhash) {
        this.recentBlockhash = recentBlockhash;
    }

    public static TransactionMessage deserialize(byte[] d) {
        if (d.length < 1) {
            throw new IllegalArgumentException("Transaction data too short");
        }
        byte[] data = d;
        int v = data[0] & 0xFF;

        MessageVersion version = v > 127 ? MessageVersion.V0 : MessageVersion.Legacy;
        if (version == MessageVersion.V0) {
            data = Arrays.copyOfRange(data, 1, data.length);
        }

        int numRequiredSignatures = data[0];
        data = Arrays.copyOfRange(data, 1, data.length);

        int numReadonlySignedAccounts = data[0];
        data = Arrays.copyOfRange(data, 1, data.length);

        int numReadonlyUnsignedAccounts = data[0];
        data = Arrays.copyOfRange(data, 1, data.length);

        Binary.DecodedLength accountKeyDecodedLength = Binary.decodeLength(data);

        //        val accountKeyDecodedLength = Binary.decodeLength(data)
        data = accountKeyDecodedLength.bytes;

        //        val accountKeys = mutableListOf<PublicKey>() // list of all accounts
        List<PublicKey> accountKeys = new ArrayList();
        for (int i = 0; i < accountKeyDecodedLength.length; i++) {
            byte[] account = Arrays.copyOfRange(data, 0, PublicKey.PUBLIC_KEY_LENGTH);
            data = Arrays.copyOfRange(data, PublicKey.PUBLIC_KEY_LENGTH, data.length);
            accountKeys.add(new PublicKey(account));
        }

        byte[] recentBlockhash = Arrays.copyOfRange(data, 0, RECENT_BLOCK_HASH_LENGTH);
        data = Arrays.copyOfRange(data, RECENT_BLOCK_HASH_LENGTH, data.length);

        //        val recentBlockhash = data.slice(0 until PUBLIC_KEY_LENGTH).toByteArray().also {
        //            data = data.drop(PUBLIC_KEY_LENGTH).toByteArray()
        //        }

        Binary.DecodedLength instructionDecodedLength = Binary.decodeLength(data);

        data = instructionDecodedLength.bytes;

        List<CompiledInstruction> instructions = new ArrayList();
        for (int i = 0; i < instructionDecodedLength.length; i++) {
            int programIdIndex = data[0];
            data = Arrays.copyOfRange(data, 1, data.length);

            Binary.DecodedLength accountDecodedLength = Binary.decodeLength(data);
            data = accountDecodedLength.bytes;
            byte[] accountIndices = Arrays.copyOfRange(data, 0, accountDecodedLength.length);
            data = Arrays.copyOfRange(data, accountDecodedLength.length, data.length);

            Binary.DecodedLength dataDecodedLength = Binary.decodeLength(data);
            data = dataDecodedLength.bytes;
            byte[] dataSlice = Arrays.copyOfRange(data, 0, dataDecodedLength.length);
            data = Arrays.copyOfRange(data, dataDecodedLength.length, data.length);

            instructions.add(new CompiledInstruction(dataSlice, accountIndices, programIdIndex));
        }

        List<CompiledAddressLookupTable> addressLookupTables = new ArrayList();

        if (version == MessageVersion.V0) {
            Binary.DecodedLength addressLookupTableDecodedLength = Binary.decodeLength(data);
            data = addressLookupTableDecodedLength.bytes;

            for (int i = 0; i < addressLookupTableDecodedLength.length; i++) {
                byte[] account = Arrays.copyOfRange(data, 0, PublicKey.PUBLIC_KEY_LENGTH);
                data = Arrays.copyOfRange(data, PublicKey.PUBLIC_KEY_LENGTH, data.length);

                Binary.DecodedLength writableAccountIdxDecodedLength = Binary.decodeLength(data);
                data = writableAccountIdxDecodedLength.bytes;
                byte[] writableAccountIdx = Arrays.copyOfRange(data, 0, writableAccountIdxDecodedLength.length);
                data = Arrays.copyOfRange(data, writableAccountIdxDecodedLength.length, data.length);

                Binary.DecodedLength readOnlyAccountIdxDecodedLength = Binary.decodeLength(data);
                data = readOnlyAccountIdxDecodedLength.bytes;
                byte[] readOnlyAccountIdx = Arrays.copyOfRange(data, 0, readOnlyAccountIdxDecodedLength.length);
                data = Arrays.copyOfRange(data, readOnlyAccountIdxDecodedLength.length, data.length);

                addressLookupTables.add(new CompiledAddressLookupTable(new PublicKey(account),
                                                                          writableAccountIdx,
                                                                          readOnlyAccountIdx));
            }
        }

        MessageHeader header = new MessageHeader(numRequiredSignatures,
                                                       numReadonlySignedAccounts,
                                                       numReadonlyUnsignedAccounts);

        return new TransactionMessage(version,
                                         header,
                                         accountKeys,
                                         Base58.encode(recentBlockhash),
                                         instructions,
                                         addressLookupTables);

    }

    public byte[] serialize() {

        try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
            if (version == MessageVersion.V0) {
                b.write(new byte[]{(byte) 128});
            }

            b.write(header.numRequiredSignatures);
            b.write(header.numReadonlySignedAccounts);
            b.write(header.numReadonlyUnsignedAccounts);
            b.write(Binary.encodeLength(accountKeys.size()));
            for (PublicKey a : accountKeys) {
                b.write(a.toByteArray());
            }
            b.write(Base58.decode(recentBlockhash));
            b.write(Binary.encodeLength(instructions.size()));
            for (CompiledInstruction i : instructions) {
                b.write(i.programIdIndex);
                b.write(Binary.encodeLength(i.accounts.length));
                b.write(i.accounts);
                b.write(Binary.encodeLength(i.data.length));
                b.write(i.data);
            }

            if (version != MessageVersion.Legacy) {
                int validAddressLookupCount = 0;
                try (ByteArrayOutputStream accountLookupTableSerializedData = new ByteArrayOutputStream()) {
                    for (CompiledAddressLookupTable a : addressLookupTables) {
                        if (a.writableIndexes.length > 0 || a.readonlyIndexes.length > 0) {
                            accountLookupTableSerializedData.write(a.publicKey.toByteArray());
                            accountLookupTableSerializedData.write(Binary.encodeLength(a.writableIndexes.length));
                            accountLookupTableSerializedData.write(a.writableIndexes);
                            accountLookupTableSerializedData.write(Binary.encodeLength(a.readonlyIndexes.length));
                            accountLookupTableSerializedData.write(a.readonlyIndexes);
                            validAddressLookupCount++;
                        }
                    }

                    b.write(Binary.encodeLength(validAddressLookupCount));
                    b.write(accountLookupTableSerializedData.toByteArray());
                }
            }
            return b.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
