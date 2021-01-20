package org.p2p.solanaj.programs;

import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.TransactionInstruction;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * Interface for the Memo program, used for writing UTF-8 data into Solana transactions.
 */
public class MemoProgram {
    
    public static final PublicKey PROGRAM_ID = new PublicKey("Memo1UhkJRfHyvLMcVucJwxXeuD728EqVDDwQDxFMNo");

    /**
     * Returns a {@link TransactionInstruction} object containing instructions to call the Memo program with the
     * specified memo.
     * @param memo utf-8 string to be written into Solana transaction
     * @return {@link TransactionInstruction} object with memo instruction
     */
    public static TransactionInstruction writeUtf8(String memo) {
        final TransactionInstruction memoInstruction = new TransactionInstruction(
                PROGRAM_ID,
                Collections.emptyList(),
                memo.getBytes(StandardCharsets.UTF_8)
        );

        return memoInstruction;
    }
}
