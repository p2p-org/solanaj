package org.p2p.solanaj.serum;

import org.bitcoinj.core.Utils;
import org.p2p.solanaj.core.PublicKey;

import java.nio.ByteBuffer;

/**
 * version 2 market offsets.
 *
 *   blob(5), 0-4
 *   accountFlagsLayout('accountFlags'), 5-12
 *   publicKeyLayout('ownAddress'), 13-44
 *   u64('vaultSignerNonce'), 45-52
 *   publicKeyLayout('baseMint'), 53-84
 *   publicKeyLayout('quoteMint'), 85-116
 *   publicKeyLayout('baseVault'), 117-148
 *   u64('baseDepositsTotal'), 149-156
 *   u64('baseFeesAccrued'), 157-164
 *   publicKeyLayout('quoteVault'), 165-196
 *
 *   u64('quoteDepositsTotal'), 197-204
 *   u64('quoteFeesAccrued'), 205-212
 *
 *   u64('quoteDustThreshold'), 213-220
 *
 *     private PublicKey requestQueue; 221-252
 *     private PublicKey eventQueue; 253-284
 *
 *     private PublicKey bids; 285-316
 *     private PublicKey asks; 317-348
 *
 *
 *     private long baseLotSize; 349-356
 *     private long quoteLotSize; 357-364
 *     private long feeRateBps; 365-372
 *     private long referrerRebatesAccrued 373-380;
 *
 *   ....
 *
 */
public class SerumUtils {

    public static final int OWN_ADDRESS_OFFSET = 13;
    private static final int VAULT_SIGNER_NONCE_OFFSET = 28;
    private static final int BASE_MINT_OFFSET = 53;
    private static final int QUOTE_MINT_OFFSET = 85;
    private static final int BASE_VAULT_OFFSET = 117;
    private static final int BASE_DEPOSITS_TOTAL_OFFSET = 149;
    private static final int BASE_FEES_ACCRUED_OFFSET = 157;
    private static final int QUOTE_VAULT_OFFSET = 165;
    private static final int QUOTE_DEPOSITS_TOTAL_OFFSET = 197;
    private static final int QUOTE_FEES_ACCRUED_OFFSET = 205;
    private static final int QUOTE_DUST_THRESHOLD_OFFSET = 213;
    private static final int REQUEST_QUEUE_OFFSET = 221;
    private static final int EVENT_QUEUE_OFFSET = 253;
    private static final int BIDS_OFFSET = 285;
    private static final int ASKS_OFFSET = 317;
    private static final int BASE_LOT_SIZE_OFFSET = 349;
    private static final int QUOTE_LOT_SIZE_OFFSET = 357;
    private static final int FEE_RATE_BPS_OFFSET = 365;
    private static final int REFERRER_REBATES_ACCRUED_OFFSET = 373;




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

    public static long readBaseFeesAccrued(byte[] bytes) {
        return Utils.readInt64(bytes, BASE_FEES_ACCRUED_OFFSET);
    }

    public static PublicKey readQuoteVaultOffset(byte[] bytes) {
        return PublicKey.readPubkey(bytes, QUOTE_VAULT_OFFSET);
    }

    public static long readQuoteDepositsTotal(byte[] bytes) {
        return Utils.readInt64(bytes, QUOTE_DEPOSITS_TOTAL_OFFSET);
    }

    public static long readQuoteFeesAccrued(byte[] bytes) {
        return Utils.readInt64(bytes, QUOTE_FEES_ACCRUED_OFFSET);
    }

    public static long readQuoteDustThreshold(byte[] bytes) {
        return Utils.readInt64(bytes, QUOTE_DUST_THRESHOLD_OFFSET);
    }

    public static PublicKey readRequestQueuePubkey(byte[] bytes) {
        return PublicKey.readPubkey(bytes, REQUEST_QUEUE_OFFSET);
    }

    public static PublicKey readEventQueuePubkey(byte[] bytes) {
        return PublicKey.readPubkey(bytes, EVENT_QUEUE_OFFSET);
    }

    public static PublicKey readBidsPubkey(byte[] bytes) {
        return PublicKey.readPubkey(bytes, BIDS_OFFSET);
    }

    public static PublicKey readAsksPubkey(byte[] bytes) {
        return PublicKey.readPubkey(bytes, ASKS_OFFSET);
    }

    public static long readBaseLotSize(byte[] bytes) {
        return Utils.readInt64(bytes, BASE_LOT_SIZE_OFFSET);
    }

    public static long readQuoteLotSize(byte[] bytes) {
        return Utils.readInt64(bytes, QUOTE_LOT_SIZE_OFFSET);
    }

    public static long readFeeRateBps(byte[] bytes) {
        return Utils.readInt64(bytes, FEE_RATE_BPS_OFFSET);
    }

    public static long readReferrerRebatesAccrued(byte[] bytes) {
        return Utils.readInt64(bytes, REFERRER_REBATES_ACCRUED_OFFSET);
    }

    public static void writeNewOrderStructLayout(ByteBuffer result) {
        int NEW_ORDER_STRUCT_LAYOUT = 10;
        result.put(1, (byte) NEW_ORDER_STRUCT_LAYOUT);
    }

    public static void writeSideLayout(ByteBuffer result, SideLayout sideLayout) {
        result.put(6, (byte) sideLayout.getValue());
    }
}