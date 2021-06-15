package org.p2p.solanaj.rpc;

import org.junit.Ignore;
import org.junit.Test;
import org.p2p.solanaj.rpc.types.Block;

import static org.junit.Assert.assertEquals;

public class RpcApiTest {

    @Ignore
    @Test
    public void getBlockTest() throws RpcException {
        RpcClient client = new RpcClient(Cluster.MAINNET);
        Block block = client.getApi().getBlock(74953539);
        assertEquals(74953539, block.getBlockHeight());
    }
}
