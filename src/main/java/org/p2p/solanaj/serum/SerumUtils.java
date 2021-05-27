package org.p2p.solanaj.serum;

import org.bitcoinj.core.Utils;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.ConfigObjects;
import org.p2p.solanaj.rpc.types.ProgramAccount;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Logger;

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

    private static final Logger LOGGER = Logger.getLogger(SerumUtils.class.getName());

    private static final String PADDING = "serum";

    // Market
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

    // Token mint
    private static final int TOKEN_MINT_DECIMALS_OFFSET = 44;

    public static final PublicKey SERUM_PROGRAM_ID_V3 = new PublicKey("9xQeWvG816bUx9EPjHmaT23yvVM2ZWbrrpZb9PusVFin");
    public static final PublicKey WRAPPED_SOL_MINT = new PublicKey("So11111111111111111111111111111111111111112");

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
        result.put(5, (byte) sideLayout.getValue());
    }

    public static void writeLimitPrice(ByteBuffer result, long price) {
        result.putLong(9, price);
    }

    public static void writeMaxBaseQuantity(ByteBuffer result, long maxBaseQuantity) {
        // 9 + 8 bytes for the 64bit limit price = index 17 (or 16 indexed maybe)
        // lets verify with some real requests
        // looks good, used a quantity of 1, showing as 1
        result.putLong(17, maxBaseQuantity);
    }

    public static void writeMaxQuoteQuantity(ByteBuffer result, long maxQuoteQuantity) {
        // TODO - name these magic numbers with constants
        // TODO - figure out what this is, for now just write it
        result.putLong(25, maxQuoteQuantity);
    }


    // Only need to write the first byte since the enum is small
    public static void writeSelfTradeBehavior(ByteBuffer result, SelfTradeBehaviorLayout selfTradeBehavior) {
        result.put(33, (byte) selfTradeBehavior.getValue());
    }

    public static void writeOrderType(ByteBuffer result, OrderTypeLayout orderTypeLayout) {
        result.put(37, (byte) orderTypeLayout.getValue());
    }

    public static void writeClientId(ByteBuffer result, long clientId) {
        result.putLong(41, clientId);
    }

    public static void writeLimit(ByteBuffer result) {
        result.putShort(49, (short) 65535);
    }

    /**
     * Reads the decimals value from decoded account data of a given token mint
     *
     * Note: MINT_LAYOUT = struct([blob(44), u8('decimals'), blob(37)]);
     *
     * 0-43 = other data
     * index 44 = the single byte of decimals we want
     * 45-... = other data
     *
     * @param accountData decoded account data from the token mint
     * @return int containing the number of decimals in the token mint
     */
    public static byte readDecimalsFromTokenMintData(byte[] accountData) {
        // Read a SINGLE byte at offset 44
        byte result = accountData[TOKEN_MINT_DECIMALS_OFFSET];
        //LOGGER.info(String.format("Market decimals byte = %d", result));

        return result;
    }

    public static void validateSerumData(byte[] accountData) {
        for (int i = 0; i < 5; i++) {
            if (accountData[i] != PADDING.getBytes()[i]) {
                throw new RuntimeException("Invalid Event Queue data.");
            }
        }
    }

    public static double getBaseSplTokenMultiplier(byte baseDecimals) {
        return Math.pow(10, baseDecimals);
    }

    public static double getQuoteSplTokenMultiplier(byte quoteDecimals) {
        return Math.pow(10, quoteDecimals);
    }

    public static float priceLotsToNumber(long price, byte baseDecimals, byte quoteDecimals, long baseLotSize, long quoteLotSize) {
        double top = (price * quoteLotSize * getBaseSplTokenMultiplier(baseDecimals));
        double bottom = (baseLotSize * getQuoteSplTokenMultiplier(quoteDecimals));

        return (float) (top / bottom);
    }

    public static float priceNumberToLots(long price, byte quoteDecimals, long baseLotSize, byte baseDecimals, long quoteLotSize) {
        double top = (price * Math.pow(10, quoteDecimals) * baseLotSize);
        double bottom = Math.pow(10, baseDecimals) * quoteLotSize;
        return Math.round(top / bottom);
    }

    public static float baseSizeLotsToNumber(long size, long baseLotSize, long baseMultiplier) {
        double top = size * baseLotSize;
        return (float) (top / baseMultiplier);
    }

    public static float baseSizeNumberToLots(long size, byte baseDecimals, long baseLotSize) {
        double top = Math.round(size * Math.pow(10, baseDecimals));
        return (float) (top / baseLotSize);


    }

    public static PublicKey findOpenOrdersAccountForOwner(RpcClient client, PublicKey marketAddress, PublicKey ownerAddress) {
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
}