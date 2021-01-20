package org.p2p.solanaj.programs;

import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.TransactionInstruction;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class MemoProgram {
    public static final PublicKey PROGRAM_ID = new PublicKey("Memo1UhkJRfHyvLMcVucJwxXeuD728EqVDDwQDxFMNo");

    public static TransactionInstruction writeUtf8(String memo) {
        final TransactionInstruction memoInstruction = new TransactionInstruction(
                PROGRAM_ID,
                Collections.emptyList(),
                memo.getBytes(StandardCharsets.UTF_8)
        );

        return memoInstruction;
    }
}
