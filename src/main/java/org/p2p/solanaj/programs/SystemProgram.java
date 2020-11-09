package org.p2p.solanaj.programs;

import java.util.ArrayList;

import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.TransactionInstruction;
import org.p2p.solanaj.core.AccountMeta;

import static org.bitcoinj.core.Utils.*;

public class SystemProgram {
    public static final PublicKey PROGRAM_ID = new PublicKey("11111111111111111111111111111111");

    private static final int PROGRAM_INDEX_TRANSFER = 2;

    public static TransactionInstruction transfer(PublicKey fromPublicKey, PublicKey toPublickKey, int lamports) {
        ArrayList<AccountMeta> keys = new ArrayList<AccountMeta>();
        keys.add(new AccountMeta(fromPublicKey, true, true));
        keys.add(new AccountMeta(toPublickKey, false, true));

        // 4 byte instruction index + 8 bytes lamports
        byte[] data = new byte[4 + 8];
        uint32ToByteArrayLE(PROGRAM_INDEX_TRANSFER, data, 0);
        int64ToByteArrayLE(lamports, data, 4);

        return new TransactionInstruction(PROGRAM_ID, keys, data);
    }
}
