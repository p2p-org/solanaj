package org.p2p.solanaj.programs;

import org.p2p.solanaj.core.TransactionInstruction;

public class SerumProgram extends Program {

    public static TransactionInstruction placeOrder() {
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

        return createTransactionInstruction(null, null, null);
    }

}
