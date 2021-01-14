package org.p2p.solanaj.serum;

import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.AccountInfo;

import java.util.Base64;
import java.util.List;

public class MarketBuilder {

    private final RpcClient client = new RpcClient(Cluster.MAINNET);
    private PublicKey publicKey;
    private boolean retrieveOrderbooks = false;

    public MarketBuilder setRetrieveOrderBooks(boolean retrieveOrderbooks) {
        this.retrieveOrderbooks = retrieveOrderbooks;
        return this;
    }

    public boolean isRetrieveOrderbooks() {
        return retrieveOrderbooks;
    }

    public Market build() {
        Market market = new Market();

        // Get account Info
        byte[] base64AccountInfo = getAccountData();

        // Read market
        if (base64AccountInfo == null) {
            throw new RuntimeException("Unable to read account data");
        }

        market = Market.readMarket(base64AccountInfo);

        // Get Order books
        if (retrieveOrderbooks) {
            byte[] base64BidOrderbook = getOrderbookData(market.getBids());

            OrderBook bidOrderBook = OrderBook.readOrderBook(base64BidOrderbook);
            market.setBidOrderBook(bidOrderBook);
        }

        return market;
    }

    private byte[] getAccountData() {
        AccountInfo accountInfo = null;
        try {
            accountInfo = client.getApi().getAccountInfo(publicKey);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        final List<String> accountData = accountInfo.getValue().getData();

        return Base64.getDecoder().decode(accountData.get(0));
    }

    private byte[] getOrderbookData(PublicKey publicKey) {
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
}
