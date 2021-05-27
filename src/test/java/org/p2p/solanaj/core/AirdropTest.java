package org.p2p.solanaj.core;

import org.junit.Ignore;
import org.junit.Test;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.token.TokenManager;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Test to iterate a list of SOL addresses and sent a fraction of a penny to.
 * This illustrates the ability to Airdrop a given token directly to SOl addresses.
 */
public class AirdropTest extends AccountBasedTest {
    private static final PublicKey USDC_TOKEN_MINT = new PublicKey("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v");

    private final PublicKey publicKey = solDestination;
    private final RpcClient client = new RpcClient(Cluster.TESTNET);
    public final TokenManager tokenManager = new TokenManager(client);

    // List of recipients - ETL a file into this
    private final List<PublicKey> recipients = List.of(publicKey);

    private static final long AIRDROP_AMOUNT = 100;
    private static final byte AIRDROP_DECIMALS = 6;

    @Test
    @Ignore
    public void airdropTest() {
        // Send airdrop
        recipients.forEach(recipient -> {
            tokenManager.transferCheckedToSolAddress(
                    testAccount,
                    usdcSource,
                    publicKey,
                    USDC_TOKEN_MINT,
                    AIRDROP_AMOUNT,
                    AIRDROP_DECIMALS
            );
            LOGGER.info("Airdropped tokens to " + recipient.toBase58());
        });

        assertTrue(true);
    }
}
