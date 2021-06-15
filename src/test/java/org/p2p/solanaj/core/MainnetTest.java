package org.p2p.solanaj.core;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.p2p.solanaj.programs.MemoProgram;
import org.p2p.solanaj.programs.SystemProgram;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.*;
import org.p2p.solanaj.token.TokenManager;

import java.util.*;

import static org.junit.Assert.*;

public class MainnetTest extends AccountBasedTest {

    private final RpcClient client = new RpcClient(Cluster.MAINNET);
    public final TokenManager tokenManager = new TokenManager(client);

    private static final PublicKey USDC_TOKEN_MINT = new PublicKey("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v");
    private static final long LAMPORTS_PER_SOL = 1000000000L;


    @Before
    public void beforeMethod() throws InterruptedException {
        // Prevent RPCPool rate limit
        Thread.sleep(100L);
    }

    @Test
    public void getAccountInfoBase64() throws RpcException {
        // Get account Info
        final AccountInfo accountInfo = client.getApi().getAccountInfo(PublicKey.valueOf("So11111111111111111111111111111111111111112"));
        final double balance = (double) accountInfo.getValue().getLamports()/ LAMPORTS_PER_SOL;

        // Account data list
        final List<String> accountData = accountInfo.getValue().getData();

        // Verify "base64" string in accountData
        assertTrue(accountData.stream().anyMatch(s -> s.equalsIgnoreCase("base64")));
        assertTrue(balance > 0);
    }

    @Test
    public void getAccountInfoBase58() throws RpcException {
        // Get account Info
        final AccountInfo accountInfo = client.getApi().getAccountInfo(PublicKey.valueOf("So11111111111111111111111111111111111111112"), Map.of("encoding", "base58"));
        final double balance = (double) accountInfo.getValue().getLamports()/ LAMPORTS_PER_SOL;

        // Account data list
        final List<String> accountData = accountInfo.getValue().getData();

        // Verify "base64" string in accountData
        assertTrue(accountData.stream().anyMatch(s -> s.equalsIgnoreCase("base58")));
        assertTrue(balance > 0);
    }

    @Test
    public void getAccountInfoRootCommitment() {
        try {
            // Get account Info
            final AccountInfo accountInfo = client.getApi().getAccountInfo(PublicKey.valueOf("So11111111111111111111111111111111111111112"), Map.of("commitment", "root"));
            final double balance = (double) accountInfo.getValue().getLamports()/ LAMPORTS_PER_SOL;

            // Verify any balance
            assertTrue(balance > 0);
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAccountInfoJsonParsed() {
        try {
            final SplTokenAccountInfo accountInfo = client.getApi().getSplTokenAccountInfo(
                    PublicKey.valueOf("8tnpAECxAT9nHBqR1Ba494Ar5dQMPGhL31MmPJz1zZvY")
            );

            assertTrue(
                    accountInfo.getValue().getData().getProgram().equalsIgnoreCase("spl-token")
            );

        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calls sendTransaction with a call to the Memo program included.
     */
    @Test
    @Ignore
    public void transactionMemoTest() {
        final int lamports = 1337;
        final PublicKey destination = solDestination;

        // Create account from private key
        final Account feePayer = testAccount;

        final Transaction transaction = new Transaction();
        transaction.addInstruction(
                SystemProgram.transfer(
                        feePayer.getPublicKey(),
                        destination,
                        lamports
                )
        );

        // Add instruction to write memo
        transaction.addInstruction(
                MemoProgram.writeUtf8(feePayer.getPublicKey(),"Hello from SolanaJ :)")
        );

        // Call sendTransaction
        String result = null;
        try {
            result = client.getApi().sendTransaction(transaction, feePayer);
            LOGGER.info("Result = " + result);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        assertNotNull(result);
    }

    @Test
    public void getBlockCommitmentTest() {
        // Block 5 used for testing - matches docs
        long block = 5;

        try {
            final BlockCommitment blockCommitment = client.getApi().getBlockCommitment(block);

            LOGGER.info(String.format("block = %d, totalStake = %d", block, blockCommitment.getTotalStake()));

            assertNotNull(blockCommitment);
            assertTrue(blockCommitment.getTotalStake() > 0);
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getBlockHeightTest() {
        try {
            long blockHeight = client.getApi().getBlockHeight();
            LOGGER.info(String.format("Block height = %d", blockHeight));
            assertTrue(blockHeight > 0);
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getBlockProductionTest() throws RpcException {
        BlockProduction blockProduction = client.getApi().getBlockProduction(0, 0, null);
        LOGGER.info(String.format("Block height = %s", blockProduction.getValue()));
        assertNotNull(blockProduction);
    }

    @Test
    public void minimumLedgerSlotTest() throws RpcException {
        long minimumLedgerSlot = client.getApi().minimumLedgerSlot();
        LOGGER.info(String.format("minimumLedgerSlot = %d", minimumLedgerSlot));
        assertTrue(minimumLedgerSlot > 0);
    }

    @Test
    public void getVersionTest() throws RpcException {
        SolanaVersion version = client.getApi().getVersion();
        LOGGER.info(
                String.format(
                        "solana-core: %s, feature-set: %s",
                        version.getSolanaCore(),
                        version.getFeatureSet()
                )
        );
        assertNotNull(version);
        assertNotNull(version.getSolanaCore());
    }


    @Test
    public void getClusterNodesTest() {
        try {
            final List<ClusterNode> clusterNodes = client.getApi().getClusterNodes();

            // Make sure we got some nodes
            assertNotNull(clusterNodes);
            assertTrue(clusterNodes.size() > 0);

            // Output the nodes
            LOGGER.info("Cluster Nodes:");
            clusterNodes.forEach(clusterNode -> {
                LOGGER.info(clusterNode.toString());
            });
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getEpochInfoTest() {
        try {
            final EpochInfo epochInfo = client.getApi().getEpochInfo();
            assertNotNull(epochInfo);

            LOGGER.info(epochInfo.toString());

            // Validate the returned data
            assertTrue(epochInfo.getAbsoluteSlot() > 0);
            assertTrue(epochInfo.getEpoch() > 0);
            assertTrue(epochInfo.getSlotsInEpoch() > 0);
            assertTrue(epochInfo.getBlockHeight() > 0);
            assertTrue(epochInfo.getSlotIndex() > 0);
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getEpochScheduleTest() throws RpcException {
        final EpochSchedule epochSchedule = client.getApi().getEpochSchedule();
        assertNotNull(epochSchedule);

        LOGGER.info(epochSchedule.toString());

        // Validate the returned data
        assertTrue(epochSchedule.getSlotsPerEpoch() > 0);
        assertTrue(epochSchedule.getLeaderScheduleSlotOffset() > 0);
    }

    @Test
    public void getInflationRateTest() throws RpcException {
        InflationRate inflationRate = client.getApi().getInflationRate();
        LOGGER.info(inflationRate.toString());

        //validate the returned data
        assertNotNull(inflationRate);
        assertTrue(inflationRate.getEpoch() > 0);
        assertTrue(inflationRate.getFoundation() >= 0);
        assertTrue(inflationRate.getValidator() >= 0);
        assertTrue(inflationRate.getTotal() >= 0);
        assertEquals(inflationRate.getTotal(), inflationRate.getFoundation() + inflationRate.getValidator(), 0.0);
    }

    @Test
    public void getInflationGovernorTest() throws RpcException {
        InflationGovernor inflationGovernor = client.getApi().getInflationGovernor();
        LOGGER.info(inflationGovernor.toString());

        //validate the returned data
        assertNotNull(inflationGovernor);
        assertTrue(inflationGovernor.getInitial() > 0);
        assertTrue(inflationGovernor.getTerminal() > 0);
        assertTrue(inflationGovernor.getTaper() > 0);
        assertTrue(inflationGovernor.getFoundation() >= 0);
        assertTrue(inflationGovernor.getFoundationTerm() >= 0);
    }

    @Test
    public void getInflationRewardTest() throws RpcException {
        List<InflationReward> inflationRewards = client.getApi().getInflationReward(
                Arrays.asList(
                        PublicKey.valueOf("H8VT3V6EDiYiQqmeDgqZJf4Tt76Qe6WZjPhighAGPL5T"),
                        PublicKey.valueOf("BsXUTPFf5b82ptLGfDVXhAPmGk1ZwTirWA2aQrBq4zBW")
                ),
                155L,
                null);

        LOGGER.info(inflationRewards.toString());

        //validate the returned data
        assertNotNull(inflationRewards);
        assertEquals(2, inflationRewards.size());
        for (InflationReward inflationReward : inflationRewards) {
            assertEquals(155, inflationReward.getEpoch(), 0);
            assertTrue(inflationReward.getAmount() > 0);
            assertTrue(inflationReward.getEffectiveSlot() > 0);
            assertTrue(inflationReward.getPostBalance() > 0);
        }

    }

    @Test
    public void getSlotTest() throws RpcException {
        long slot = client.getApi().getSlot();
        LOGGER.info(String.format("Current slot = %d", slot));
        assertTrue(slot > 0);
    }

    @Test
    public void getSlotLeaderTest() throws RpcException {
        PublicKey slotLeader = client.getApi().getSlotLeader();
        LOGGER.info(String.format("Current slot leader = %s", slotLeader));
        assertNotNull(slotLeader);
    }

    @Test
    public void getSlotLeadersTest() throws RpcException {
        long limit = 5;
        long currentSlot = client.getApi().getSlot();
        List<PublicKey> slotLeaders = client.getApi().getSlotLeaders(currentSlot, limit);
        slotLeaders.forEach(slotLeader ->
                LOGGER.info(slotLeader.toString())
        );

        assertNotNull(slotLeaders);
        assertEquals(limit, slotLeaders.size());
    }

    @Test
    public void getSnapshotSlotTest() throws RpcException {
        long snapshotSlot = client.getApi().getSnapshotSlot();
        LOGGER.info(String.format("Snapshot slot = %d", snapshotSlot));
        assertTrue(snapshotSlot > 0);
    }

    @Test
    public void getMaxShredInsertSlotTest() throws RpcException {
        long maxShredInsertSlot = client.getApi().getMaxShredInsertSlot();
        LOGGER.info(String.format("Max slot after shred insert = %d", maxShredInsertSlot));
        assertTrue(maxShredInsertSlot > 0);
    }

    @Test
    public void getIdentityTest() throws RpcException {
        PublicKey identity = client.getApi().getIdentity();
        LOGGER.info(String.format("Identity of the current node = %s", identity));
        assertNotNull(identity);
    }

    @Test
    public void getSupplyTest() throws RpcException {
        Supply supply = client.getApi().getSupply();
        LOGGER.info(supply.toString());

        //validate the returned data
        assertNotNull(supply);
        assertTrue(supply.getValue().getTotal() > 0);
        assertTrue(supply.getValue().getCirculating() > 0);
        assertTrue(supply.getValue().getNonCirculating() > 0);
        assertEquals(supply.getValue().getTotal(), supply.getValue().getCirculating() + supply.getValue().getNonCirculating());
        assertTrue(supply.getValue().getNonCirculatingAccounts().size() > 0);
    }

    @Test
    public void getFirstAvailableBlockTest() throws RpcException {
        long firstAvailableBlock = client.getApi().getFirstAvailableBlock();
        LOGGER.info(String.format("First available block in the ledger = %d", firstAvailableBlock));
        assertTrue(firstAvailableBlock >= 0);
    }

    @Test
    public void getGenesisHashTest() throws RpcException {
        String genesisHash = client.getApi().getGenesisHash();
        LOGGER.info(String.format("Genesis hash = %s", genesisHash));
        assertNotNull(genesisHash);
    }

    @Test
    public void getTransactionCountTest() throws RpcException {
        long transactionCount = client.getApi().getTransactionCount();
        assertTrue(transactionCount > 0);
    }

    @Test
    @Ignore
    public void getFeeCalculatorForBlockhashTest() throws RpcException, InterruptedException {
        String recentBlockHash = client.getApi().getRecentBlockhash();
        Thread.sleep(20000L);
        FeeCalculatorInfo feeCalculatorInfo = client.getApi().getFeeCalculatorForBlockhash(recentBlockHash);
        LOGGER.info(feeCalculatorInfo.getValue().getFeeCalculator().toString());

        assertNotNull(feeCalculatorInfo);
        assertTrue(feeCalculatorInfo.getValue().getFeeCalculator().getLamportsPerSignature() > 0);
    }

    @Test
    public void getFeesRateGovernorTest() throws RpcException {
        FeeRateGovernorInfo feeRateGovernorInfo = client.getApi().getFeeRateGovernor();
        LOGGER.info(feeRateGovernorInfo.getValue().getFeeRateGovernor().toString());

        assertNotNull(feeRateGovernorInfo);
        assertTrue(feeRateGovernorInfo.getValue().getFeeRateGovernor().getBurnPercent() > 0);
        assertTrue(feeRateGovernorInfo.getValue().getFeeRateGovernor().getMaxLamportsPerSignature() > 0);
        assertTrue(feeRateGovernorInfo.getValue().getFeeRateGovernor().getMinLamportsPerSignature() > 0);
        assertTrue(feeRateGovernorInfo.getValue().getFeeRateGovernor().getTargetLamportsPerSignature() >= 0);
        assertTrue(feeRateGovernorInfo.getValue().getFeeRateGovernor().getTargetSignaturesPerSlot() >= 0);
    }

    @Test
    public void getFeesInfoTest() throws RpcException {
        FeesInfo feesInfo = client.getApi().getFees();
        LOGGER.info(feesInfo.toString());

        assertNotNull(feesInfo);
        assertNotEquals("", feesInfo.getValue().getBlockhash());
        assertTrue(feesInfo.getValue().getFeeCalculator().getLamportsPerSignature() > 0);
        assertTrue(feesInfo.getValue().getLastValidSlot() > 0);
    }

    @Test
    public void getMaxRetransmitSlotTest() throws RpcException {
        long maxRetransmitSlot = client.getApi().getMaxRetransmitSlot();
        assertTrue(maxRetransmitSlot > 0);
    }

    @Ignore
    @Test
    public void simulateTransactionTest() throws RpcException {
        String transaction = "ASdDdWBaKXVRA+6flVFiZokic9gK0+r1JWgwGg/GJAkLSreYrGF4rbTCXNJvyut6K6hupJtm72GztLbWNmRF1Q4BAAEDBhrZ0FOHFUhTft4+JhhJo9+3/QL6vHWyI8jkatuFPQzrerzQ2HXrwm2hsYGjM5s+8qMWlbt6vbxngnO8rc3lqgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAy+KIwZmU8DLmYglP3bPzrlpDaKkGu6VIJJwTOYQmRfUBAgIAAQwCAAAAuAsAAAAAAAA=";
        List<PublicKey> addresses = List.of(PublicKey.valueOf("QqCCvshxtqMAL2CVALqiJB7uEeE5mjSPsseQdDzsRUo"));
        SimulatedTransaction simulatedTransaction = client.getApi().simulateTransaction(transaction, addresses);
        assertTrue(simulatedTransaction.getValue().getLogs().size() > 0);
    }

    @Test
    @Ignore
    public void sendTokenTest() {
        final PublicKey source = usdcSource; // Private key's USDC token account
        final PublicKey destination = usdcDestination; // Destination's USDC account
        final int tokenAmount = 10; // 0.000100 USDC

        // Create account from private key
        final Account owner = testAccount;

        // "10" = 0.0000001 (or similar)
        final String txId = tokenManager.transfer(
                owner,
                source,
                destination,
                USDC_TOKEN_MINT,
                tokenAmount
        );

        assertNotNull(txId);
    }

    @Test
    @Ignore
    public void transferCheckedTest() {
        final PublicKey source = usdcSource; // Private key's USDC token account
        final PublicKey destination = solDestination;

        /*
            amount = "0.0001" usdc
            amount = 100
            decimals = 6
         */

        final long tokenAmount = 100;
        final byte decimals = 6;

        // Create account from private key
        final Account owner = testAccount;

        final String txId = tokenManager.transferCheckedToSolAddress(
                owner,
                source,
                destination,
                USDC_TOKEN_MINT,
                tokenAmount,
                decimals
        );

        // TODO - actually verify something
        assertNotNull(txId);
    }

    @Test
    @Ignore
    public void initializeAccountTest() {
        final Account owner = testAccount;
        final Account newAccount = new Account();
        final String txId = tokenManager.initializeAccount(
                newAccount,
                USDC_TOKEN_MINT,
                owner
        );

        // TODO - actually verify something
        assertNotNull(txId);
        System.out.println(testAccount.getPublicKey().toBase58());
    }

    @Test
    @Ignore
    public void getConfirmedTransactionTest() throws RpcException {
        String txId = "46VcVPoecvVASnX9vHEZLA8JMS6BVXhvMMhqtGBcn9eg4bHehK6uA2icuTjwjWLZxwfxdT2z1CqYxCHHvjorvWDi";
        ConfirmedTransaction confirmedTransaction = client.getApi().getConfirmedTransaction(txId);

        if (confirmedTransaction != null) {
            LOGGER.info(String.format("Tx: %s", confirmedTransaction));
        }
    }

    @Test
    public void getConfirmedBlockTest() throws RpcException {
        ConfirmedBlock block = this.client.getApi().getConfirmedBlock(74953539);
        assertEquals(74953538, block.getParentSlot());
    }

    @Ignore
    @Test
    public void getBlockTest() throws RpcException {
        Block block = this.client.getApi().getBlock(74953539);
        assertEquals("74953539", block.getBlockHeight());
    }
}