package org.p2p.solanaj.programs;

import org.p2p.solanaj.core.AccountMeta;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.TransactionInstruction;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class TokenProgram extends Program {

    public static final PublicKey PROGRAM_ID = new PublicKey("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA");
    private static final int TRANSFER_METHOD_ID = 3;

    /**
     * Transfers an SPL token from the owner's source account to destination account.
     * Destination pubkey must be the Token Account (created by token mint), and not the main SOL address.
     * @param source SPL token wallet funding this transaction
     * @param destination Destined SPL token wallet
     * @param amount 64 bit amount of tokens to send
     * @param owner account/private key signing this transaction
     * @return transaction id for explorer
     */
    public static TransactionInstruction transfer(PublicKey source, PublicKey destination, long amount, PublicKey owner) {
        final List<AccountMeta> keys = new ArrayList<>();

        keys.add(new AccountMeta(source,false, true));
        keys.add(new AccountMeta(destination,false, true));
        keys.add(new AccountMeta(owner,true, false));

        byte[] transactionData = encodeTransferTokenInstructionData(
                amount
        );

        return createTransactionInstruction(
                PROGRAM_ID,
                keys,
                transactionData
        );
    }

    private static byte[] encodeTransferTokenInstructionData(long amount) {
        ByteBuffer result = ByteBuffer.allocate(9);
        result.order(ByteOrder.LITTLE_ENDIAN);

        result.put((byte) TRANSFER_METHOD_ID);
        result.putLong(amount);

        return result.array();
    }
}
