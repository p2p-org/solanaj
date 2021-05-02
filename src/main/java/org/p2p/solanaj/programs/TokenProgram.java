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
    private static final PublicKey SYSVAR_RENT_PUBKEY = new PublicKey("SysvarRent111111111111111111111111111111111");

    private static final int TRANSFER_METHOD_ID = 3;
    private static final int TRANSFER_CHECKED_METHOD_ID = 12;

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

    public static TransactionInstruction transferChecked(PublicKey source, PublicKey destination, long amount, byte decimals, PublicKey owner, PublicKey tokenMint) {
        final List<AccountMeta> keys = new ArrayList<>();

        keys.add(new AccountMeta(source,false, true));
        // index 1 = token mint (https://docs.rs/spl-token/3.1.0/spl_token/instruction/enum.TokenInstruction.html#variant.TransferChecked)
        keys.add(new AccountMeta(tokenMint, false, false));
        keys.add(new AccountMeta(destination,false, true));
        keys.add(new AccountMeta(owner,true, false));

        byte[] transactionData = encodeTransferCheckedTokenInstructionData(
                amount,
                decimals
        );

        return createTransactionInstruction(
                PROGRAM_ID,
                keys,
                transactionData
        );
    }

    public static TransactionInstruction initializeAccount(final PublicKey account, final PublicKey mint, final PublicKey owner) {
        final List<AccountMeta> keys = new ArrayList<>();

        keys.add(new AccountMeta(account,false, true));
        keys.add(new AccountMeta(mint, false, false));
        keys.add(new AccountMeta(owner,false, true));
        keys.add(new AccountMeta(SYSVAR_RENT_PUBKEY,false, false));

        return createTransactionInstruction(
                PROGRAM_ID,
                keys,
                null
        );
    }

    private static byte[] encodeTransferTokenInstructionData(long amount) {
        ByteBuffer result = ByteBuffer.allocate(9);
        result.order(ByteOrder.LITTLE_ENDIAN);

        result.put((byte) TRANSFER_METHOD_ID);
        result.putLong(amount);

        return result.array();
    }

    private static byte[] encodeTransferCheckedTokenInstructionData(long amount, byte decimals) {
        ByteBuffer result = ByteBuffer.allocate(10);
        result.order(ByteOrder.LITTLE_ENDIAN);

        result.put((byte) TRANSFER_CHECKED_METHOD_ID);
        result.putLong(amount);
        result.put(decimals);

        return result.array();
    }
}
