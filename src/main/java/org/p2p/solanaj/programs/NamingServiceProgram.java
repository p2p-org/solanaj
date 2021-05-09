package org.p2p.solanaj.programs;

import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.AccountMeta;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.TransactionInstruction;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Functionality for SPL Naming Service - allows registration of .sol domain addresses
 */
public class NamingServiceProgram extends Program {

    public static final PublicKey PROGRAM_ID_SPL_NAME_SERVICE = new PublicKey("Gh9eN9nDuS3ysmAkKf4QJ6yBzf3YNqsn6MD8Ms3TsXmA");

    /**
     * Registers a name at the SPL Name Registry for the given Solana {@link Account}.
     * @return {@link org.p2p.solanaj.core.TransactionInstruction} object to use in a {@link org.p2p.solanaj.core.Transaction}
     */
    public static TransactionInstruction createNameRegistry(
            final PublicKey nameProgramId,
            final PublicKey systemProgramid,
            final PublicKey nameKey,
            final PublicKey nameOwnerKey,
            final PublicKey payerKey,
            final byte[] hashedName,
            final long lamports,
            final int space,
            final PublicKey nameClassKey,
            final PublicKey nameParent
    ) {
        // 1000 bytes should be enough for all the data, then we compact it
        // TODO - calculate the normal size of the data so we don't have to compact it

        ByteBuffer buffer = ByteBuffer.allocate(1000);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0);
        buffer.put((byte) hashedName.length);
        buffer.put(hashedName);
        buffer.putLong(lamports);
        buffer.putInt(space);
        buffer.compact();

        // Add signer to AccountMeta keys
        final List<AccountMeta> keys = new ArrayList<>();

        // Convert memo string to UTF-8 byte array
        final byte[] nameBytes = newAccountName.getBytes(StandardCharsets.UTF_8);

        return createTransactionInstruction(
                PROGRAM_ID_SPL_NAME_SERVICE,
                keys,
                nameBytes
        );
    }
}
