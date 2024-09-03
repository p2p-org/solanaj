package org.p2p.solanaj.core;

import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TokenTest {

    @Test
    public void getTokenWithinBlocksWithLimitTest() {

        final RpcClient client = new RpcClient(Cluster.DEVNET);
        final String tokenProgramId = "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA"; // NFT Program ID
        // TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA
        try {
            int blockNumberStart = 126446590;
            int limit = 3;

            for (int i = 0; i < 1; i++) { // scan over pages increase loop, for unit testing only loop once
                // Get Blocks with Limit
                ArrayList<Double> blocks = (ArrayList<Double>) client.getApi().getBlocksWithLimit(blockNumberStart, limit);
                System.out.println(blocks);
                assertEquals(limit, blocks.size());

                for (Double blockId : blocks) {
                    String blockNumber = new BigDecimal(blockId.toString()).stripTrailingZeros().toPlainString();
                    // System.out.println(blockNumber);

                    Block block = client.getApi().getBlock((long) Double.parseDouble(blockNumber));

                    for (Transaction tx : block.getTransactions()) {
                        for (TransactionInstruction txi : tx.getMessage().getInstructions()) {

                            System.out.println(txi.getProgramId().toString());
                            if (txi.getProgramId().toString().equals(tokenProgramId)) {
                                System.out.println("NFT Found : " + txi.getProgramId());
                            }
                        }
                    }
                }
                blockNumberStart = blockNumberStart + limit;
            }
        } catch (RpcException e) {
            e.printStackTrace();
            fail("Unexpected RPC Exception " + e.getMessage());
        }
    }


}
