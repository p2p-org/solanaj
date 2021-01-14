package org.p2p.solanaj.serum;

import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.AccountInfo;

import java.util.Base64;
import java.util.Comparator;
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

    public void setRetrieveOrderbooks(boolean retrieveOrderbooks) {
        this.retrieveOrderbooks = retrieveOrderbooks;
    }

    public Market build() {
        Market market = new Market();
        //market.setOwnAddress(publicKey);

        // Get Data + read order books

        // Get account Info
        String base64AccountInfo = getAccountData();

        // Read market
        if (base64AccountInfo == null) {
            throw new RuntimeException("Unable to read account data");
        }

        market = Market.readMarket(base64AccountInfo.getBytes());

        // Get Order books
        if (retrieveOrderbooks) {
            byte[] data;
            AccountInfo bidAccount = null;

            try {
                bidAccount = client.getApi().getAccountInfo(market.getBids());
            } catch (RpcException e) {
                e.printStackTrace();
            }

            data = Base64.getDecoder().decode(bidAccount.getValue().getData().get(0));

            OrderBook bidOrderBook = OrderBook.readOrderBook(data);
            market.setBidOrderBook(bidOrderBook);
        }

        return market;
    }

    private String getAccountData() {
        AccountInfo accountInfo = null;
        try {
            accountInfo = client.getApi().getAccountInfo(publicKey);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        final List<String> accountData = accountInfo.getValue().getData();
        final String base64Data = accountData.get(0);

        return base64Data;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public MarketBuilder setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
        return this;
    }
}
