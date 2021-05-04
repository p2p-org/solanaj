package org.p2p.solanaj.programs;

import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.AccountMeta;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.TransactionInstruction;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * Functionality for SPL Naming Service - allows registration of .sol domain addresses
 */
public class NamingServiceProgram extends Program {

    public static final PublicKey PROGRAM_ID_SPL_NAME_SERVICE = new PublicKey("Gh9eN9nDuS3ysmAkKf4QJ6yBzf3YNqsn6MD8Ms3TsXmA");

    /**
     * Registers a name at the SPL Name Registry for the given Solana {@link Account}.
     * @param account Solana account to associate with the new name
     * @param newAccountName new domain name under the .sol registry
     * @return {@link org.p2p.solanaj.core.TransactionInstruction} object to use in a {@link org.p2p.solanaj.core.Transaction}
     */
    public static TransactionInstruction createNameRegistry(
            final Account account,
            final String newAccountName
    ) {
        // Add signer to AccountMeta keys
        final List<AccountMeta> keys = Collections.singletonList(
                new AccountMeta(
                        account.getPublicKey(),
                        true,
                        false
                )
        );

        // Convert memo string to UTF-8 byte array
        final byte[] nameBytes = newAccountName.getBytes(StandardCharsets.UTF_8);

        return createTransactionInstruction(
                PROGRAM_ID_SPL_NAME_SERVICE,
                keys,
                nameBytes
        );
    }
}
