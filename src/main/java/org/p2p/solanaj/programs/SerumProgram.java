package org.p2p.solanaj.programs;

import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.AccountMeta;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.TransactionInstruction;
import org.p2p.solanaj.serum.*;
import org.p2p.solanaj.utils.ByteUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Class for creating Serum v3 {@link TransactionInstruction}s
 */
public class SerumProgram extends Program {


    private static final Logger LOGGER = Logger.getLogger(SerumProgram.class.getName());
    private static final PublicKey TOKEN_PROGRAM_ID = new PublicKey("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA");
    private static final PublicKey SYSVAR_RENT_PUBKEY = new PublicKey("SysvarRent111111111111111111111111111111111");

    public static TransactionInstruction consumeEvents(List<PublicKey> openOrdersAccounts, Account payerAccount, Market market) {
        List<AccountMeta> accountMetas = new ArrayList<>();

        // 0 fee payer + signer => your account
        accountMetas.add(new AccountMeta(payerAccount.getPublicKey(), true, true));

        // 1 - 5 = 5 open orders accounts
        accountMetas.addAll(openOrdersAccounts.stream()
                .map(publicKey -> new AccountMeta(publicKey, false, true)).collect(Collectors.toList()));

        // 6 = market's event queue
        accountMetas.add(new AccountMeta(market.getEventQueueKey(), false, true));


        // 7 dummy key (just me)

        accountMetas.add(new AccountMeta(market.getOwnAddress(), false, false));


        int limit = 5;
        byte[] transactionData = encodeConsumeEventsTransactionData(
                limit
        );

        return createTransactionInstruction(
                SerumUtils.SERUM_PROGRAM_ID_V3,
                accountMetas,
                transactionData
        );
    }

    private static byte[] encodeConsumeEventsTransactionData(int limit) {
        ByteBuffer result = ByteBuffer.allocate(3);
        result.order(ByteOrder.LITTLE_ENDIAN);

        result.put((byte) 4);
        result.putShort((short) limit);

        return result.array();
    }

    public static TransactionInstruction placeOrder(Account account, Account payer, PublicKey openOrders, Market market, Order order) {
        /*
        See: https://github.com/project-serum/serum-ts/blob/e51e3d9af0ab7026155b76a1824cea6507fc7ef7/packages/serum/src/instructions.js#L118
        */

        // TODO - handle feeDiscountPubkey
        /* (if (feeDiscountPubkey) {
            keys.push({
                    pubkey: feeDiscountPubkey,
                    isSigner: false,
                    isWritable: false,
            });
        }
        */
        /*
        open_order_accounts = self.find_open_orders_accounts_for_owner(owner.public_key())
        if not open_order_accounts:
        new_open_orders_account = Account()
        place_order_open_order_account = new_open_orders_account.public_key()
        mbfre_resp = self._conn.get_minimum_balance_for_rent_exemption(OPEN_ORDERS_LAYOUT.sizeof())
        balanced_needed = mbfre_resp["result"]
        transaction.add(
            make_create_account_instruction(
                owner_address=owner.public_key(),
                new_account_address=new_open_orders_account.public_key(),
                lamports=balanced_needed,
                program_id=self.state.program_id(),
            )
        )
        signers.append(new_open_orders_account)
         */

        // pubkey: market
        final AccountMeta marketKey = new AccountMeta(market.getOwnAddress(), false, true);

        // openOrders account retrieval/creation should be done in the manager
        final AccountMeta openOrdersKey = new AccountMeta(openOrders, false, true);

        // pubkey: requestQueue
        final AccountMeta requestQueueKey = new AccountMeta(market.getRequestQueue(), false, true);

        // pubkey: eventQueue
        final AccountMeta eventQueueKey = new AccountMeta(market.getEventQueueKey(), false, true);

        // pubkey: bids
        final AccountMeta bidsKey = new AccountMeta(market.getBids(), false, true);

        // pubkey: asks
        final AccountMeta asksKey = new AccountMeta(market.getAsks(), false, true);

        // pubkey: payer
        // TODO - fix this to create a new account each time - and has an initialize instruction after the createaccount instruction
        final AccountMeta payerKey = new AccountMeta(payer.getPublicKey(), true, true);

        // pubkey: owner
        final AccountMeta ownerKey = new AccountMeta(account.getPublicKey(), true, false);

        // pubkey: baseVault
        final AccountMeta baseVaultKey = new AccountMeta(market.getBaseVault(), false, true);

        // pubkey: quoteVault
        final AccountMeta quoteVaultKey = new AccountMeta(market.getQuoteVault(), false, true);

        // pubkey: TOKEN_PROGRAM_ID
        final AccountMeta tokenProgramIdKey = new AccountMeta(TOKEN_PROGRAM_ID, false, false);

        // pubkey: SYSVAR_RENT_PUBKEY
        final AccountMeta sysvarRentKey = new AccountMeta(SYSVAR_RENT_PUBKEY, false, false);

        final List<AccountMeta> keys = List.of(
                marketKey,
                openOrdersKey,
                requestQueueKey,
                eventQueueKey,
                bidsKey,
                asksKey,
                payerKey,
                ownerKey,
                baseVaultKey,
                quoteVaultKey,
                tokenProgramIdKey,
                sysvarRentKey
        );

        byte[] transactionData =  buildNewOrderv3InstructionData(
                order
        );

        return createTransactionInstruction(SerumUtils.SERUM_PROGRAM_ID_V3, keys, transactionData);
    }

    // Using some constant data for testing at the moment
    // Going to be testing a Post-Only sell order of 1 SOL at the SOL/USDC market
    public static byte[] buildNewOrderv3InstructionData(Order order) {
        ByteBuffer result = ByteBuffer.allocate(51);
        result.order(ByteOrder.LITTLE_ENDIAN);

        // Constant used to indicate newOrderv3
        SerumUtils.writeNewOrderStructLayout(result);

        // Order side (buy/sell) - enum
        SerumUtils.writeSideLayout(result, order.isBuy() ? SideLayout.BUY : SideLayout.SELL);

        // Limit price - uint64
        SerumUtils.writeLimitPrice(result, order.getPrice());

        // maxBaseQuantity - uint64 (for some reason 0.1 sol = 1L)
        SerumUtils.writeMaxBaseQuantity(result, order.getQuantity());

        // maxQuoteQuantity - uint64
        SerumUtils.writeMaxQuoteQuantity(result, order.getMaxQuoteQuantity());

        // selfTradeBehaviorLayout - selfTradeBehaviorLayout (serum-ts) - 4 bytes for a 1 byte enum
        SerumUtils.writeSelfTradeBehavior(result, order.getSelfTradeBehaviorLayout());

        // orderType - orderTypeLayout (enum)
        SerumUtils.writeOrderType(result, order.getOrderTypeLayout());

        // clientId - uint64
        SerumUtils.writeClientId(result, order.getClientId());

        // "limit" - uint16 - might always be static equal to 65535
        SerumUtils.writeLimit(result);

        byte[] arrayResult = result.array();
        LOGGER.info("newOrderV3 instruction hex = " + ByteUtils.bytesToHex(arrayResult));

        return arrayResult;
    }

}
