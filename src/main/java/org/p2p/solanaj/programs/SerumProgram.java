package org.p2p.solanaj.programs;

import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.AccountMeta;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.TransactionInstruction;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.ConfigObjects;
import org.p2p.solanaj.rpc.types.ProgramAccount;
import org.p2p.solanaj.serum.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import static org.p2p.solanaj.serum.SerumUtils.OWN_ADDRESS_OFFSET;

/**
 * Class for creating Serum v3v {@link TransactionInstruction}s
 */
public class SerumProgram extends Program {

    private static final PublicKey TOKEN_PROGRAM_ID = new PublicKey("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA");
    private static final PublicKey SYSVAR_RENT_PUBKEY = new PublicKey("SysvarRent111111111111111111111111111111111");
    private static final PublicKey SERUM_PROGRAM_ID_V3 = new PublicKey("9xQeWvG816bUx9EPjHmaT23yvVM2ZWbrrpZb9PusVFin");

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static TransactionInstruction placeOrder(RpcClient client, Account account, Account payer, Account openOrders, Market market, Order order) {
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
        System.out.println("marketKey = " + marketKey.getPublicKey().toBase58());

        // pubkey: openOrders (+ findOpenOrdersAccountForOwner)
        final PublicKey openOrdersAccount = findOpenOrdersAccountForOwner(client, market.getOwnAddress(), account.getPublicKey());

        // if null, create open orders account
        if (openOrdersAccount == null) {
            // TODO - create openOrders account
            final Account newOpenOrders = new Account();
        }


        final AccountMeta openOrdersKey = new AccountMeta(openOrders.getPublicKey(), false, true);

        // temp: use passed-in open orders account instead of trying to find it (for now, proof of concept stage)
        System.out.println("openOrdersKey = " + openOrdersKey.getPublicKey().toBase58());

        // pubkey: requestQueue
        final AccountMeta requestQueueKey = new AccountMeta(market.getRequestQueue(), false, true);
        System.out.println("requestQueueKey = " + requestQueueKey.getPublicKey().toBase58());

        // pubkey: eventQueue
        final AccountMeta eventQueueKey = new AccountMeta(market.getEventQueue(), false, true);
        System.out.println("eventQueueKey = " + eventQueueKey.getPublicKey().toBase58());

        // pubkey: bids
        final AccountMeta bidsKey = new AccountMeta(market.getBids(), false, true);
        System.out.println("bidsKey = " + bidsKey.getPublicKey().toBase58());

        // pubkey: asks
        final AccountMeta asksKey = new AccountMeta(market.getAsks(), false, true);
        System.out.println("asksKey = " + asksKey.getPublicKey().toBase58());

        // pubkey: payer
        // TODO - fix this to create a new account each time - and has an initialize instruction after the createaccount instruction
        final AccountMeta payerKey = new AccountMeta(payer.getPublicKey(), true, true);
        System.out.println("payerKey = " + payer.getPublicKey().toBase58());

        // pubkey: owner
        final AccountMeta ownerKey = new AccountMeta(account.getPublicKey(), true, false);
        System.out.println("ownerKey = " + ownerKey.getPublicKey().toBase58());

        // pubkey: baseVault
        final AccountMeta baseVaultKey = new AccountMeta(market.getBaseVault(), false, true);
        System.out.println("baseVaultKey = " + baseVaultKey.getPublicKey().toBase58());

        // pubkey: quoteVault
        final AccountMeta quoteVaultKey = new AccountMeta(market.getQuoteVault(), false, true);
        System.out.println("quoteVaultKey = " + quoteVaultKey.getPublicKey().toBase58());

        // pubkey: TOKEN_PROGRAM_ID
        final AccountMeta tokenProgramIdKey = new AccountMeta(TOKEN_PROGRAM_ID, false, false);
        System.out.println("tokenProgramIdKey = " + tokenProgramIdKey.getPublicKey().toBase58());

        // pubkey: SYSVAR_RENT_PUBKEY
        final AccountMeta sysvarRentKey = new AccountMeta(SYSVAR_RENT_PUBKEY, false, false);
        System.out.println("sysvarRentKey = " + sysvarRentKey.getPublicKey().toBase58());

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
                null
        ); // TODO

        /*
        return new TransactionInstruction({
        keys,
        programId,
        data: encodeInstruction({
            newOrderV3: {
            side,
                limitPrice,
                maxBaseQuantity,
                maxQuoteQuantity,
                selfTradeBehavior,
                orderType,
                clientId,
                limit: 65535,
            },
        }),
        */

        return createTransactionInstruction(SERUM_PROGRAM_ID_V3, keys, transactionData);
    }

    // Using some constant data for testing at the moment
    // Going to be testing a Post-Only sell order of 1 MAPS at the MAPS/USDC market
    public static byte[] buildNewOrderv3InstructionData(byte[] instruction) {
        ByteBuffer result = ByteBuffer.allocate(51);
        result.order(ByteOrder.LITTLE_ENDIAN);

        // Constant used to indicate newOrderv3
        SerumUtils.writeNewOrderStructLayout(result);

        // Order side (buy/sell) - enum
        SerumUtils.writeSideLayout(result, SideLayout.SELL);

        // Limit price - uint64
        SerumUtils.writeLimitPrice(result, 13370000L);

        // maxBaseQuantity - uint64
        SerumUtils.writeMaxBaseQuantity(result, 1L);

        // maxQuoteQuantity - uint64
        SerumUtils.writeMaxQuoteQuantity(result, 1337000000L);

        // selfTradeBehaviorLayout - selfTradeBehaviorLayout (serum-ts) - 4 bytes for a 1 byte enum
        SerumUtils.writeSelfTradeBehavior(result, SelfTradeBehaviorLayout.DECREMENT_TAKE);

        // orderType - orderTypeLayout (enum)
        SerumUtils.writeOrderType(result, OrderTypeLayout.POST_ONLY);

        // clientId - uint64
        SerumUtils.writeClientId(result, 0L);

        // "limit" - uint16 - might always be static equal to 65535
        SerumUtils.writeLimit(result);

        byte[] arrayResult = result.array();
        System.out.println("newOrderv3 instruction hex = " + bytesToHex(arrayResult));

        return arrayResult;
    }

    private static PublicKey findOpenOrdersAccountForOwner(RpcClient client, PublicKey marketAddress, PublicKey ownerAddress) {
        /*
        const filters = [
          {
            memcmp: {
              offset: this.getLayout(programId).offsetOf('market'),
              bytes: marketAddress.toBase58(),
            },
          },
          {
            memcmp: {
              offset: this.getLayout(programId).offsetOf('owner'),
              bytes: ownerAddress.toBase58(),
            },
          },
          {
            dataSize: this.getLayout(programId).span,
          },
        ];
        const accounts = await getFilteredProgramAccounts(
          connection,
          programId,
          filters,
        );
        return accounts.map(({ publicKey, accountInfo }) =>
          OpenOrders.fromAccountInfo(publicKey, accountInfo, programId),
        );
         */

        // TODO - handle dataSize filter - can ignore for now as it is super negligible. actually it's not
        int dataSize = 3228;

        List<ProgramAccount> programAccounts = null;

        ConfigObjects.Memcmp marketFilter = new ConfigObjects.Memcmp(OWN_ADDRESS_OFFSET, marketAddress.toBase58());
        ConfigObjects.Memcmp ownerFilter = new ConfigObjects.Memcmp(45, ownerAddress.toBase58()); // TODO remove magic number

        List<ConfigObjects.Memcmp> memcmpList = List.of(marketFilter, ownerFilter);

        try {
            programAccounts = client.getApi().getProgramAccounts(SERUM_PROGRAM_ID_V3, memcmpList, dataSize);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        System.out.println("findOpenOrdersAccountForOwner:");
        if (programAccounts != null) {
            programAccounts.forEach(programAccount -> {
                System.out.println("Account = " + programAccount.getAccount().getData());
                System.out.println("Pubkey = " + programAccount.getPubkey());
            });
        }

        // TODO - handle failed lookup more cleaner than null
        String base58Pubkey = null;
        if (programAccounts != null) {
            base58Pubkey = programAccounts.stream().map(ProgramAccount::getPubkey).findFirst().orElse(null);
        }

        if (base58Pubkey == null) {
            return null;
        }

        return new PublicKey(base58Pubkey);
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

}
