package org.p2p.solanaj.core;

import org.bitcoinj.core.Base58;
import org.p2p.solanaj.utils.ShortvecEncoding;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Message {
    private static class MessageHeader {
        static final int HEADER_LENGTH = 3;

        byte numRequiredSignatures = 0;
        byte numReadonlySignedAccounts = 0;
        byte numReadonlyUnsignedAccounts = 0;

        byte[] toByteArray() {
            return new byte[]{numRequiredSignatures, numReadonlySignedAccounts, numReadonlyUnsignedAccounts};
        }
    }

    private static class CompiledInstruction {
        byte programIdIndex;
        byte[] keyIndicesCount;
        byte[] keyIndices;
        byte[] dataLength;
        byte[] data;

        int getLength() {
            // 1 = programIdIndex length
            return 1 + keyIndicesCount.length + keyIndices.length + dataLength.length + data.length;
        }
    }

    private static final int RECENT_BLOCK_HASH_LENGTH = 32;

    private MessageHeader messageHeader;
    private String recentBlockhash;
    private final AccountKeysList accountKeys;

    public List<TransactionInstruction> getInstructions() {
        return instructions;
    }

    private final List<TransactionInstruction> instructions;
    private Account feePayer;

    public Message() {
        this.accountKeys = new AccountKeysList();
        this.instructions = new ArrayList<>();
    }

    public Message addInstruction(TransactionInstruction instruction) {
        accountKeys.addAll(instruction.getKeys());
        accountKeys.add(new AccountMeta(instruction.getProgramId(), false, false));
        instructions.add(instruction);

        return this;
    }

    public void setRecentBlockHash(String recentBlockhash) {
        this.recentBlockhash = recentBlockhash;
    }

    public byte[] serialize() {

        if (recentBlockhash == null) {
            throw new IllegalArgumentException("recentBlockhash required");
        }

        if (instructions.size() == 0) {
            throw new IllegalArgumentException("No instructions provided");
        }

        messageHeader = new MessageHeader();

        List<AccountMeta> keysList = getAccountKeys();
        int accountKeysSize = keysList.size();

        byte[] accountAddressesLength = ShortvecEncoding.encodeLength(accountKeysSize);

        int compiledInstructionsLength = 0;
        List<CompiledInstruction> compiledInstructions = new ArrayList<>();

        for (TransactionInstruction instruction : instructions) {
            int keysSize = instruction.getKeys().size();

            byte[] keyIndices = new byte[keysSize];
            for (int i = 0; i < keysSize; i++) {
                keyIndices[i] = (byte) findAccountIndex(keysList, instruction.getKeys().get(i).getPublicKey());
            }

            CompiledInstruction compiledInstruction = new CompiledInstruction();
            compiledInstruction.programIdIndex = (byte) findAccountIndex(keysList, instruction.getProgramId());
            compiledInstruction.keyIndicesCount = ShortvecEncoding.encodeLength(keysSize);
            compiledInstruction.keyIndices = keyIndices;
            compiledInstruction.dataLength = ShortvecEncoding.encodeLength(instruction.getData().length);
            compiledInstruction.data = instruction.getData();

            compiledInstructions.add(compiledInstruction);

            compiledInstructionsLength += compiledInstruction.getLength();
        }

        byte[] instructionsLength = ShortvecEncoding.encodeLength(compiledInstructions.size());

        int bufferSize = MessageHeader.HEADER_LENGTH + RECENT_BLOCK_HASH_LENGTH + accountAddressesLength.length
                + (accountKeysSize * PublicKey.PUBLIC_KEY_LENGTH) + instructionsLength.length
                + compiledInstructionsLength;

        ByteBuffer out = ByteBuffer.allocate(bufferSize);

        ByteBuffer accountKeysBuff = ByteBuffer.allocate(accountKeysSize * PublicKey.PUBLIC_KEY_LENGTH);
        for (AccountMeta accountMeta : keysList) {
            accountKeysBuff.put(accountMeta.getPublicKey().toByteArray());

            if (accountMeta.isSigner()) {
                messageHeader.numRequiredSignatures += 1;
                if (!accountMeta.isWritable()) {
                    messageHeader.numReadonlySignedAccounts += 1;
                }
            } else {
                if (!accountMeta.isWritable()) {
                    messageHeader.numReadonlyUnsignedAccounts += 1;
                }
            }
        }

        out.put(messageHeader.toByteArray());

        out.put(accountAddressesLength);
        out.put(accountKeysBuff.array());

        out.put(Base58.decode(recentBlockhash));

        out.put(instructionsLength);
        for (CompiledInstruction compiledInstruction : compiledInstructions) {
            out.put(compiledInstruction.programIdIndex);
            out.put(compiledInstruction.keyIndicesCount);
            out.put(compiledInstruction.keyIndices);
            out.put(compiledInstruction.dataLength);
            out.put(compiledInstruction.data);
        }

        return out.array();
    }

    protected void setFeePayer(Account feePayer) {
        this.feePayer = feePayer;
    }

    private List<AccountMeta> getAccountKeys() {
        List<AccountMeta> keysList = accountKeys.getList();
        int feePayerIndex = findAccountIndex(keysList, feePayer.getPublicKey());

        List<AccountMeta> newList = new ArrayList<>();
        AccountMeta feePayerMeta = keysList.get(feePayerIndex);
        newList.add(new AccountMeta(feePayerMeta.getPublicKey(), true, true));
        keysList.remove(feePayerIndex);
        newList.addAll(keysList);

        return newList;
    }

    private int findAccountIndex(List<AccountMeta> accountMetaList, PublicKey key) {
        for (int i = 0; i < accountMetaList.size(); i++) {
            if (accountMetaList.get(i).getPublicKey().equals(key)) {
                return i;
            }
        }

        throw new RuntimeException("unable to find account index");
    }
}