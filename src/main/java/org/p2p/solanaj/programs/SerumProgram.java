package org.p2p.solanaj.programs;

import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.AccountMeta;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.TransactionInstruction;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.ConfigObjects;
import org.p2p.solanaj.rpc.types.ProgramAccount;
import org.p2p.solanaj.serum.Market;
import org.p2p.solanaj.serum.Order;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.p2p.solanaj.serum.SerumUtils.OWN_ADDRESS_OFFSET;

/**
 * Class for creating Serum v2 {@link TransactionInstruction}s
 */
public class SerumProgram extends Program {

    public static TransactionInstruction placeOrder(RpcClient client, Account account, Market market, Order order) {
         /*
            See: https://github.com/project-serum/serum-ts/blob/e51e3d9af0ab7026155b76a1824cea6507fc7ef7/packages/serum/src/instructions.js#L118
          */
        /*
        const keys = [
          { pubkey: market, isSigner: false, isWritable: true },
          { pubkey: openOrders, isSigner: false, isWritable: true },
          { pubkey: requestQueue, isSigner: false, isWritable: true },
          { pubkey: eventQueue, isSigner: false, isWritable: true },
          { pubkey: bids, isSigner: false, isWritable: true },
          { pubkey: asks, isSigner: false, isWritable: true },
          { pubkey: payer, isSigner: false, isWritable: true },
          { pubkey: owner, isSigner: true, isWritable: false },
          { pubkey: baseVault, isSigner: false, isWritable: true },
          { pubkey: quoteVault, isSigner: false, isWritable: true },
          { pubkey: TOKEN_PROGRAM_ID, isSigner: false, isWritable: false },
          { pubkey: SYSVAR_RENT_PUBKEY, isSigner: false, isWritable: false },
        ];
        if (feeDiscountPubkey) {
          keys.push({
            pubkey: feeDiscountPubkey,
            isSigner: false,
            isWritable: false,
          });
        }
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
        final PublicKey ownerAddress = account.getPublicKey();
        final AccountMeta marketKey = new AccountMeta(market.getOwnAddress(), false, true);
        // final AccountMeta openOrders = new AccountMeta(market.get)

        // findOpenOrdersAccountForOwner
        final PublicKey openOrdersAccount = findOpenOrdersAccountForOwner(client, market.getOwnAddress(), ownerAddress, market.getBaseMint(), market);


        //final AccountMeta openOrders = new AccountMeta(null, false, true)
        final AccountMeta requestQueue = new AccountMeta(market.getRequestQueue(), false, true);
        //final AccountMeta payer = new AccountMeta(market.(), false, true);
        final AccountMeta owner = new AccountMeta(market.getRequestQueue(), true, false);


        final List<AccountMeta> keys = new ArrayList<>();
        keys.add(marketKey);
        keys.add(requestQueue);
        //keys.add(payer);
        keys.add(owner);

        return createTransactionInstruction(market.getOwnAddress(), keys, new byte[]{});
    }

    private static PublicKey findOpenOrdersAccountForOwner(RpcClient client, PublicKey marketAddress, PublicKey ownerAddress, PublicKey programId, Market market) {
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

        // TODO - handle dataSize filter - can ignore for now as it is super negligible.

        List<ProgramAccount> programAccounts = null;

        ConfigObjects.Memcmp marketFilter = new ConfigObjects.Memcmp(OWN_ADDRESS_OFFSET, marketAddress.toBase58());
        ConfigObjects.Memcmp ownerFilter = new ConfigObjects.Memcmp(OWN_ADDRESS_OFFSET, ownerAddress.toBase58());

        List<ConfigObjects.Memcmp> memcmpList = List.of(marketFilter, ownerFilter);

        try {
            programAccounts = client.getApi().getProgramAccounts(ownerAddress, memcmpList);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        if (programAccounts != null) {
            programAccounts.forEach(programAccount -> {
                System.out.println("Account = " + programAccount.getAccount().getData());
            });
        }

        return null;
    }

}
