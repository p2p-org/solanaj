package org.p2p.solanaj.core;

import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class BlockTest {

    @Test
    public void getBlocksWithLimitTest() {

        final RpcClient client = new RpcClient(Cluster.DEVNET);

        try {
            int limit = 10;
            // Get Blocks with Limit
            ArrayList<Double> blocks = (ArrayList<Double>) client.getApi().getBlocksWithLimit(124449450, limit);

            assertEquals(limit, blocks.size());
        } catch (RpcException e) {
            e.printStackTrace();
            fail("Unexpected RPC Exception " + e.getMessage());
        }
    }

    @Test
    public void getBlockTest() {

        final RpcClient client = new RpcClient(Cluster.DEVNET);

        try {
            int limit = 10;
            // Gets specific block
            Block block = client.getApi().getBlock(124449450);

            assertNotNull(block);

        } catch (RpcException e) {
            e.printStackTrace();
            fail("Unexpected RPC Exception " + e.getMessage());
        }
    }

}
