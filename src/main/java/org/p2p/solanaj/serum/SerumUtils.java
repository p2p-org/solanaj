package org.p2p.solanaj.serum;

import org.bitcoinj.core.Utils;
import org.p2p.solanaj.core.PublicKey;

/**
 *   blob(5), 0-4
 *
 *   accountFlagsLayout('accountFlags'), 5-12
 *
 *   publicKeyLayout('ownAddress'), 13-44
 *
 *   u64('vaultSignerNonce'), 45-52
 *
 *   publicKeyLayout('baseMint'), 53-84
 *   publicKeyLayout('quoteMint'), 85-116
 *
 *
 *
 *
 *   publicKeyLayout('baseVault'), 117-148
 *
 *
 *   u64('baseDepositsTotal'), 149-156
 *   u64('baseFeesAccrued'),
 *
 *   publicKeyLayout('quoteVault'),
 *   u64('quoteDepositsTotal'),
 *   u64('quoteFeesAccrued'),
 *
 *   u64('quoteDustThreshold'),
 *
 *   publicKeyLayout('requestQueue'),
 *   publicKeyLayout('eventQueue'),
 *
 *   ....
 *
 */
public class SerumUtils {

    private static final int OWN_ADDRESS_OFFSET = 13;
    private static final int VAULT_SIGNER_NONCE_OFFSET = 28;
    private static final int BASE_MINT_OFFSET = 53;
    private static final int QUOTE_MINT_OFFSET = 85;
    private static final int BASE_VAULT_OFFSET = 117;
    private static final int BASE_DEPOSITS_TOTAL_OFFSET = 149;

    public static PublicKey readOwnAddressPubkey(byte[] bytes) {
        return PublicKey.readPubkey(bytes, OWN_ADDRESS_OFFSET);
    }

    public static long readVaultSignerNonce(byte[] bytes) {
        return Utils.readInt64(bytes, VAULT_SIGNER_NONCE_OFFSET);
    }

    public static PublicKey readBaseMintPubkey(byte[] bytes) {
        return PublicKey.readPubkey(bytes, BASE_MINT_OFFSET);
    }

    public static PublicKey readQuoteMintPubkey(byte[] bytes) {
        return PublicKey.readPubkey(bytes, QUOTE_MINT_OFFSET);
    }

    public static PublicKey readBaseVaultPubkey(byte[] bytes) {
        return PublicKey.readPubkey(bytes, BASE_VAULT_OFFSET);
    }

    public static long readBaseDepositsTotal(byte[] bytes) {
        return Utils.readInt64(bytes, BASE_DEPOSITS_TOTAL_OFFSET);
    }
}