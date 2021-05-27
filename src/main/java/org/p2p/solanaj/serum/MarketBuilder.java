package org.p2p.solanaj.serum;

import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.AccountInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Builds a {@link Market} object, which can have polled data including bid/ask {@link OrderBook}s
 */
public class MarketBuilder {

    private RpcClient client = new RpcClient(Cluster.MAINNET);
    private PublicKey publicKey;
    private boolean retrieveOrderbooks = false;
    private boolean retrieveEventQueue = false;
    private boolean built = false;
    private byte[] base64AccountInfo;
    private static final Logger LOGGER = Logger.getLogger(MarketBuilder.class.getName());

    private Map<PublicKey, Byte> decimalsCache = new ConcurrentHashMap<>();

    public MarketBuilder setRetrieveOrderBooks(boolean retrieveOrderbooks) {
        this.retrieveOrderbooks = retrieveOrderbooks;
        return this;
    }

    public MarketBuilder setClient(RpcClient client) {
        this.client = client;
        return this;
    }

    public boolean isRetrieveOrderbooks() {
        return retrieveOrderbooks;
    }

    public boolean isRetrieveEventQueue() {
        return retrieveEventQueue;
    }

    public MarketBuilder setRetrieveEventQueue(boolean retrieveEventQueue) {
        this.retrieveEventQueue = retrieveEventQueue;
        return this;
    }

    public Market build() {
        // Only lookup account info one time since it never changes (except for fees accrued, not important imo)
        if (!built) {
            //LOGGER.info("Retrieving market data once for " + publicKey.toBase58());
            base64AccountInfo = retrieveAccountData();
        } else {
            //LOGGER.info("Already built for " + publicKey.toBase58() + ", using cached data");
        }

        // Read market
        if (base64AccountInfo == null) {
            throw new RuntimeException("Unable to read account data");
        }

        Market market = Market.readMarket(base64AccountInfo);

        // Get Order books
        if (retrieveOrderbooks) {
            // Data from the token mints

            // first, check the cache for the byte. otherwise, make a request for it
            byte baseDecimals;
            byte quoteDecimals;

            if (decimalsCache.containsKey(market.getBaseMint())) {
                //LOGGER.info("Using cache to get decimal for " + market.getBaseMint().toBase58());
                baseDecimals = decimalsCache.get(market.getBaseMint());
            } else {
                //LOGGER.info("Looking up mint for " + market.getBaseMint().toBase58());
                baseDecimals = getMintDecimals(market.getBaseMint());
                decimalsCache.put(market.getBaseMint(), baseDecimals);
            }

            if (decimalsCache.containsKey(market.getQuoteMint())) {
                //LOGGER.info("Using cache to get decimal for " + market.getQuoteMint().toBase58());
                quoteDecimals = decimalsCache.get(market.getQuoteMint());
            } else {
                //LOGGER.info("Looking up mint for " + market.getQuoteMint().toBase58());
                quoteDecimals = getMintDecimals(market.getQuoteMint());
                decimalsCache.put(market.getQuoteMint(), quoteDecimals);
            }

            market.setBaseDecimals(baseDecimals);
            market.setQuoteDecimals(quoteDecimals);

            // TODO - multi-thread these
            // Data from the order books
            byte[] base64BidOrderbook = retrieveAccountData(market.getBids());
            byte[] base64AskOrderbook = retrieveAccountData(market.getAsks());

            // TODO - change/limit how we pass the decimals around
            // Currently giving them to everything for testing
            OrderBook bidOrderBook = OrderBook.readOrderBook(base64BidOrderbook);
            OrderBook askOrderBook = OrderBook.readOrderBook(base64AskOrderbook);

            bidOrderBook.setBaseDecimals(baseDecimals);
            bidOrderBook.setQuoteDecimals(quoteDecimals);
            askOrderBook.setBaseDecimals(baseDecimals);
            askOrderBook.setQuoteDecimals(quoteDecimals);

            bidOrderBook.setBaseLotSize(market.getBaseLotSize());
            bidOrderBook.setQuoteLotSize(market.getQuoteLotSize());
            askOrderBook.setBaseLotSize(market.getBaseLotSize());
            askOrderBook.setQuoteLotSize(market.getQuoteLotSize());

            market.setBidOrderBook(bidOrderBook);
            market.setAskOrderBook(askOrderBook);
        }

        if (retrieveEventQueue) {
            // retrieveEventQueue
            byte[] base64EventQueue = retrieveAccountData(market.getEventQueueKey());

//            try {
//                Files.write(Path.of("eventqueue.dat"), base64EventQueue);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            // first, check the cache for the byte. otherwise, make a request for it
            // TODO - unduplicate this code
            byte baseDecimals;
            byte quoteDecimals;

            if (decimalsCache.containsKey(market.getBaseMint())) {
                //LOGGER.info("Using cache to get decimal for " + market.getBaseMint().toBase58());
                baseDecimals = decimalsCache.get(market.getBaseMint());
            } else {
                //LOGGER.info("Looking up mint for " + market.getBaseMint().toBase58());
                baseDecimals = getMintDecimals(market.getBaseMint());
                decimalsCache.put(market.getBaseMint(), baseDecimals);
            }

            if (decimalsCache.containsKey(market.getQuoteMint())) {
                //LOGGER.info("Using cache to get decimal for " + market.getQuoteMint().toBase58());
                quoteDecimals = decimalsCache.get(market.getQuoteMint());
            } else {
                //LOGGER.info("Looking up mint for " + market.getQuoteMint().toBase58());
                quoteDecimals = getMintDecimals(market.getQuoteMint());
                decimalsCache.put(market.getQuoteMint(), quoteDecimals);
            }

            long baseLotSize = market.getBaseLotSize();
            long quoteLotSize = market.getQuoteLotSize();

            EventQueue eventQueue = EventQueue.readEventQueue(base64EventQueue, baseDecimals, quoteDecimals, baseLotSize, quoteLotSize);
            market.setEventQueue(eventQueue);
        }

        built = true;
        return market;
    }

    /**
     * Retrieves decimals for a given Token Mint's {@link PublicKey} from Solana account data.
     * @param tokenMint
     * @return
     */
    private byte getMintDecimals(PublicKey tokenMint) {
        if (tokenMint.equals(SerumUtils.WRAPPED_SOL_MINT)) {
            return 9;
        }

        // RPC call to get mint's account data into decoded bytes (already base64 decoded)
        byte[] accountData = retrieveAccountData(tokenMint);

        // Deserialize accountData into the MINT_LAYOUT enum
        byte decimals = SerumUtils.readDecimalsFromTokenMintData(accountData);

        return decimals;
    }

    private byte[] retrieveAccountData() {
        return retrieveAccountData(publicKey);
    }

    private byte[] retrieveAccountData(PublicKey publicKey) {
        AccountInfo orderBook = null;

        try {
            orderBook = client.getApi().getAccountInfo(publicKey);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        final List<String> accountData = orderBook.getValue().getData();

        return Base64.getDecoder().decode(accountData.get(0));
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public MarketBuilder setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public Market reload() {
        return build();
    }
}
