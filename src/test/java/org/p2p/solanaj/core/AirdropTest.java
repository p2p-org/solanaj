package org.p2p.solanaj.core;

import org.junit.Test;
import org.p2p.solanaj.token.TokenManager;

import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

/**
 * Test to iterate a list of SOL addresses and sent a fraction of a penny to.
 * This illustrates the ability to Airdrop a given token directly to SOl addresses.
 */
public class AirdropTest extends AccountBasedTest {
    private static final PublicKey USDC_TOKEN_MINT = new PublicKey("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v");
    private static final Logger LOGGER = Logger.getLogger(AirdropTest.class.getName());

    private final PublicKey publicKey = new PublicKey("skynetDj29GH6o6bAqoixCpDuYtWqi1rm8ZNx1hB3vq");
    private final PublicKey source = new PublicKey("A71WvME6ZhR4SFG3Ara7zQK5qdRSB97jwTVmB3sr7XiN");
    public final TokenManager tokenManager = new TokenManager();

    // List of recipients - ETL a file into this
    private final List<PublicKey> recipients = List.of(publicKey);

    private static final long AIRDROP_AMOUNT = 100;
    private static final byte AIRDROP_DECIMALS = 6;

    @Test
    public void airdropTest() {
        // Send airdrop
        recipients.forEach(recipient -> {
            tokenManager.transferCheckedToSolAddress(
                    testAccount,
                    source,
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
