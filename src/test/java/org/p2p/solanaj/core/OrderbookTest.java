package org.p2p.solanaj.core;

import org.junit.Test;
import org.p2p.solanaj.serum.OrderBook;
import org.p2p.solanaj.serum.Slab;
import org.p2p.solanaj.serum.SlabLeafNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class OrderbookTest {

    @Test
    public void lqidUsdcTest() {
        byte[] data = new byte[0];

        try {
            data = Files.readAllBytes(Paths.get("src/test/resources/lqidusdc.bin"));  // LQID/USDC
        } catch (IOException e) {
            e.printStackTrace();
        }

        OrderBook bidOrderBook = OrderBook.readOrderBook(data);
        System.out.println(bidOrderBook.getAccountFlags().toString());
        Slab slab = bidOrderBook.getSlab();

        assertNotNull(slab);

        /* C:\apps\solanaj\lqidusdc.bin (1/12/2021 8:55:59 AM)
            StartOffset(d): 00001709, EndOffset(d): 00001724, Length(d): 00000016 */

        // this rawData = key bytes for a 477.080 quantity bid at 0.0510 cents

        byte[] rawData = {
                (byte)0xFC, (byte)0xFD, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                (byte)0xFF, (byte)0xFF, (byte)0x33, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00
        };


        slab.getSlabNodes().forEach(slabNode -> {
            if (slabNode instanceof SlabLeafNode) {
                SlabLeafNode slabLeafNode = (SlabLeafNode) slabNode;
                if (Arrays.equals(rawData, slabLeafNode.getKey())) {
                    System.out.println("Found the order");
                }
                System.out.println(slabNode);
                //System.out.println("Price = " + getPriceFromKey(slabLeafNode.getKey()));

            }
        });
    }
}
