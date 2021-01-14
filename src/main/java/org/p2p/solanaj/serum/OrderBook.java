package org.p2p.solanaj.serum;

/**
 * Decodes an Orderbook object from bytes.
 *
 * buffer_layout_1.blob(5),
 *     layout_1.accountFlagsLayout('accountFlags'),
 *     slab_1.SLAB_LAYOUT.replicate('slab'),
 *     buffer_layout_1.blob(7),
 *
 *
 *
 */
public class OrderBook {

    private AccountFlags accountFlags;
    private Slab slab;

    public static OrderBook readOrderBook(byte[] data) {
        final OrderBook orderBook = new OrderBook();

        final AccountFlags accountFlags = AccountFlags.readAccountFlags(data);
        orderBook.setAccountFlags(accountFlags);

        final Slab slab = Slab.readOrderBookSlab(data);
        orderBook.setSlab(slab);

        //System.out.println("bumpIndex = " + slab.getBumpIndex());

//        Path path = Paths.get("orderbook3.dat");
//        try {
//            Files.write(path, data);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return orderBook;

    }

    public Slab getSlab() {
        return slab;
    }

    public void setSlab(Slab slab) {
        this.slab = slab;
    }

    public AccountFlags getAccountFlags() {
        return accountFlags;
    }

    public void setAccountFlags(AccountFlags accountFlags) {
        this.accountFlags = accountFlags;
    }

}
