package org.p2p.solanaj.core;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Base58;

import org.p2p.solanaj.utils.ShortvecEncoding;

public class Message {
    private class MessageHeader {
        static final int HEADER_LENGTH = 3;

        // TODO
        byte numRequiredSignatures = 1;
        byte numReadonlySignedAccounts = 0;
        byte numReadonlyUnsignedAccounts = 1;

        byte[] toByteArray() {
            return new byte[] { numRequiredSignatures, numReadonlySignedAccounts, numReadonlyUnsignedAccounts };
        }
    }

    private class CompiledInstruction {
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

    private static final int RECENT_BLOCK_HASH_LENGT = 32;

    private MessageHeader messageHeader;
    private String recentBlockhash;
    private List<AccountMeta> accountKeys;
    private List<TransactionInstruction> instructions;

    public Message() {
        this.messageHeader = new MessageHeader();
        this.accountKeys = new ArrayList<AccountMeta>();
        this.instructions = new ArrayList<TransactionInstruction>();
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
        int accountKeysSize = accountKeys.size();

        byte[] accountAddressesLength = ShortvecEncoding.encodeLength(accountKeysSize);

        int compiledInstructionsLength = 0;
        List<CompiledInstruction> compiledInstructions = new ArrayList<Message.CompiledInstruction>();

        for (TransactionInstruction instruction : instructions) {
            int keysSize = instruction.getKeys().size();
            
            byte[] keyIndices = new byte[keysSize];
            for (int i = 0; i < keysSize; i++) {
                keyIndices[i] = (byte) findAccountIndex(instruction.getKeys().get(i).getPublicKey());
            }

            CompiledInstruction compiledInstruction = new CompiledInstruction();
            compiledInstruction.programIdIndex = (byte) findAccountIndex(instruction.getProgramId());
            compiledInstruction.keyIndicesCount = ShortvecEncoding.encodeLength(keysSize);
            compiledInstruction.keyIndices = keyIndices;
            compiledInstruction.dataLength = ShortvecEncoding.encodeLength(instruction.getData().length);
            compiledInstruction.data = instruction.getData();

            compiledInstructions.add(compiledInstruction);

            compiledInstructionsLength += compiledInstruction.getLength();
        }

        byte[] instructionsLength = ShortvecEncoding.encodeLength(compiledInstructions.size());

        int bufferSize = MessageHeader.HEADER_LENGTH + RECENT_BLOCK_HASH_LENGT + accountAddressesLength.length
                + (accountKeysSize * PublicKey.PUBLIC_KEY_LENGTH) + instructionsLength.length
                + compiledInstructionsLength;

        ByteBuffer out = ByteBuffer.allocate(bufferSize);

        out.put(messageHeader.toByteArray());
       
        out.put(accountAddressesLength);
        for (AccountMeta accountMeta : accountKeys) {
            out.put(accountMeta.getPublicKey().toByteArray());
        }

        out.put(Base58.decode(recentBlockhash));

        out.put(instructionsLength);
        for(CompiledInstruction compiledInstruction:compiledInstructions) {
            out.put(compiledInstruction.programIdIndex);
            out.put(compiledInstruction.keyIndicesCount);
            out.put(compiledInstruction.keyIndices);
            out.put(compiledInstruction.dataLength);
            out.put(compiledInstruction.data);
        }

        return out.array();
    }

    private int findAccountIndex(PublicKey key) {
        for (int i = 0; i < accountKeys.size(); i++) {
            if (accountKeys.get(i).getPublicKey().equals(key)) {
                return i;
            }
        }

        // TODO throw an error
        return -1;
    }
}
