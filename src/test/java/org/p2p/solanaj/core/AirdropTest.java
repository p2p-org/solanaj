package org.p2p.solanaj.core;

import org.junit.Test;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.token.TokenManager;

import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

/**
 * Test to iterate a list of SOL addresses and sent a fraction of a penny to.
 * This illustrates the ability to Airdrop a given token directly to SOl addresses.
 */
public class AirdropTest extends AccountBasedTest {
    private static final Logger LOGGER = Logger.getLogger(AirdropTest.class.getName());
    private final RpcClient client = new RpcClient(Cluster.MAINNET);
    private final PublicKey publicKey = new PublicKey("skynetDj29GH6o6bAqoixCpDuYtWqi1rm8ZNx1hB3vq");
    public final TokenManager tokenManager = new TokenManager();
    private static final PublicKey USDC_TOKEN_MINT = new PublicKey("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v");

    @Test
    public void airdropTest() {




        assertTrue(true);
    }


}
