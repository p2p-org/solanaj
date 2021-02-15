package org.p2p.solanaj.programs;

import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.AccountMeta;
import org.p2p.solanaj.core.TransactionInstruction;
import org.p2p.solanaj.serum.Market;
import org.p2p.solanaj.serum.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for creating Serum v2 {@link TransactionInstruction}s
 */
public class SerumProgram extends Program {

    public static TransactionInstruction placeOrder(Account account, Market market, Order order) {
         /*
            See: https://github.com/project-serum/serum-ts/blob/e51e3d9af0ab7026155b76a1824cea6507fc7ef7/packages/serum/src/instructions.js#L118
          */
        /*
        const keys = [
          { pubkey: market, isSigner: false, isWritable: true },
          { pubkey: openOrders, isSigner: false, isWritable: true },
          { pubkey: requestQueue, isSigner: false, isWritable: true },
          { pubkey: payer, isSigner: false, isWritable: true },
          { pubkey: owner, isSigner: true, isWritable: false },
          { pubkey: baseVault, isSigner: false, isWritable: true },
          { pubkey: quoteVault, isSigner: false, isWritable: true },
          { pubkey: TOKEN_PROGRAM_ID, isSigner: false, isWritable: false },
          { pubkey: SYSVAR_RENT_PUBKEY, isSigner: false, isWritable: false },
        ];
        */
        final AccountMeta marketKey = new AccountMeta(market.getOwnAddress(), false, true);
        //final AccountMeta openOrders = new AccountMeta(null, false, true)
        final AccountMeta requestQueue = new AccountMeta(market.getRequestQueue(), false, true);
        final AccountMeta payer = new AccountMeta(market.getRequestQueue(), false, true);
        final AccountMeta owner = new AccountMeta(market.getRequestQueue(), true, false);


        final List<AccountMeta> keys = new ArrayList<>();
        keys.add(marketKey);
        keys.add(requestQueue);
        keys.add(payer);
        keys.add(owner);

        return createTransactionInstruction(null, keys, null);
    }

}
