package org.p2p.solanaj.programs;

import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.AccountMeta;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.TransactionInstruction;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Functionality for SPL Naming Service - allows registration of .sol domain addresses
 */
public class NamingServiceProgram extends Program {

    /**
     * Registers a name at the SPL Name Registry for the given Solana {@link Account}.
     * @return {@link org.p2p.solanaj.core.TransactionInstruction} object to use in a {@link org.p2p.solanaj.core.Transaction}
     */
    public static TransactionInstruction createNameRegistry(
            final PublicKey nameProgramId,
            final PublicKey systemProgramId,
            final PublicKey nameKey,
            final PublicKey nameOwnerKey,
            final PublicKey payerKey,
            final byte[] hashedName,
            final long lamports,
            final int space,
            final PublicKey nameClassKey,
            final PublicKey nameParent
    ) {
        // 49 bytes - derived from an explorer TX
        ByteBuffer buffer = ByteBuffer.allocate(49);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0);
        buffer.putInt(hashedName.length);
        buffer.put(hashedName);
        buffer.putLong(lamports);
        buffer.putInt(space);

        // Add signer to AccountMeta keys
        final List<AccountMeta> keys = new ArrayList<>();
        keys.add(new AccountMeta(systemProgramId, false, false));
        keys.add(new AccountMeta(payerKey, true, true));
        keys.add(new AccountMeta(nameKey, false, true));
        keys.add(new AccountMeta(nameOwnerKey, false, false));

        if (nameClassKey != null) {
            keys.add(new AccountMeta(nameClassKey, true, false));
        } else {
            keys.add(new AccountMeta(new Account().getPublicKey(), false, false));
        }

        if (nameParent != null) {
            keys.add(new AccountMeta(nameClassKey, false, false));
        } else {
            keys.add(new AccountMeta(new Account().getPublicKey(), false, false));
        }

        byte[] instructionData = buffer.array();

        try {
            Files.write(Path.of("namingdata.dat"), instructionData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return createTransactionInstruction(
                nameProgramId,
                keys,
                instructionData
        );
    }
}
