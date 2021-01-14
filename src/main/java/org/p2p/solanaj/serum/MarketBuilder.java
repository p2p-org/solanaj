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
        
        // Deserialize the bid order book. This is just proof of concept - will be moved into classes.
        // If orderbook.dat exists, use it.
//        byte[] data = new byte[0];
//
////                try {
////                    data = Files.readAllBytes(Paths.get("orderbook.dat"));
////                } catch (IOException e) {
////                    // e.printStackTrace();
////                }
//
//        if (data.length == 0) {
//            AccountInfo bidAccount = client.getApi().getAccountInfo(market.getBids());
//            data = Base64.getDecoder().decode(bidAccount.getValue().getData().get(0));
//        }
//
//        OrderBook bidOrderBook = OrderBook.readOrderBook(data);
//        market.setBidOrderBook(bidOrderBook);
//
//        System.out.println("BTC/USDC Bids Orderbook");
//        bidOrderBook.getSlab().getSlabNodes().stream().sorted(Comparator.comparingLong(value -> {
//            if (value instanceof SlabLeafNode) {
//                return ((SlabLeafNode) value).getPrice();
//            }
//            return 0;
//        }).reversed()).forEach(slabNode -> {
//            if (slabNode instanceof SlabLeafNode) {
//                SlabLeafNode slabLeafNode = (SlabLeafNode)slabNode;
//                System.out.println("Order: Bid " + slabLeafNode.getQuantity()/10000.0 + " BTC/USDC at $" + slabLeafNode.getPrice()/10);
//            }
//        });


        return new Market();
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
