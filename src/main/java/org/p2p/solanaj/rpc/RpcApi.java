package org.p2p.solanaj.rpc;

import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.Block;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.Transaction;
import org.p2p.solanaj.rpc.types.*;
import org.p2p.solanaj.ws.SubscriptionWebSocketClient;
import org.p2p.solanaj.ws.listeners.NotificationEventListener;

import java.util.*;

public class RpcApi {
    private final RpcClient client;

    public RpcApi(RpcClient client) {
        this.client = client;
    }

    @Deprecated
    public String getRecentBlockhash() throws RpcException {
        return client.call("getRecentBlockhash", null, RecentBlockhash.class).getRecentBlockhash();
    }

    public String sendTransaction(Transaction transaction, Account signer) throws RpcException {
        return sendTransaction(transaction, Arrays.asList(signer));
    }

    /**
     * JSON RPC API Reference
     * <p>
     * getAccountInfo - done
     * getBalance - done
     * getBlockTime - done
     * getMinimumBalanceForRentExemption - done
     * getProgramAccounts - done
     * requestAirdrop - done
     * sendTransaction - done
     * <p>
     * getBlock
     * getBlockHeight
     * getBlockProduction
     * getBlockCommitment
     * getBlocks
     * getBlocksWithLimit
     * getClusterNodes
     * getEpochInfo
     * getEpochSchedule
     * getFeeForMessage
     * getFirstAvailableBlock
     * getGenesisHash
     * getHealth
     * getHighestSnapshotSlot
     * getIdentity
     * getInflationGovernor
     * getInflationRate
     * getInflationReward
     * getLargestAccounts
     * getLatestBlockhash
     * getLeaderSchedule
     * getMaxRetransmitSlot
     * getMaxShredInsertSlot
     * getMultipleAccounts
     * getRecentPerformanceSamples
     * getSignaturesForAddress
     * getSignatureStatuses
     * getSlot
     * getSlotLeader
     * getSlotLeaders
     * getStakeActivation
     * getSupply
     * getTokenAccountBalance
     * getTokenAccountsByDelegate
     * getTokenAccountsByOwner
     * getTokenLargestAccounts
     * getTokenSupply
     * getTransaction
     * getTransactionCount
     * getVersion
     * getVoteAccounts
     * isBlockhashValid
     * minimumLedgerSlot
     * simulateTransaction
     * <p>
     * JSON RPC API Deprecated Methods
     * <p>
     * getConfirmedBlock
     * getConfirmedBlocks
     * getConfirmedBlocksWithLimit
     * getConfirmedSignaturesForAddress2 - used
     * getConfirmedTransaction - used
     * getFeeCalculatorForBlockhash
     * getFeeRateGovernor
     * getFees
     * getRecentBlockhash - used
     * getSnapshotSlot
     */


    public String sendTransaction(Transaction transaction, List<Account> signers) throws RpcException {
        String recentBlockhash = getRecentBlockhash();
        transaction.setRecentBlockHash(recentBlockhash);
        transaction.sign(signers);
        byte[] serializedTransaction = transaction.serialize();

        String base64Trx = Base64.getEncoder().encodeToString(serializedTransaction);

        List<Object> params = new ArrayList<Object>();

        params.add(base64Trx);
        params.add(new RpcSendTransactionConfig());

        return client.call("sendTransaction", params, String.class);
    }

    public void sendAndConfirmTransaction(Transaction transaction, List<Account> signers,
                                          NotificationEventListener listener) throws RpcException {
        String signature = sendTransaction(transaction, signers);

        SubscriptionWebSocketClient subClient = SubscriptionWebSocketClient.getInstance(client.getEndpoint());
        subClient.signatureSubscribe(signature, listener);
    }

    public long getBalance(PublicKey account) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(account.toString());

        return client.call("getBalance", params, RpcResultTypes.ValueLong.class).getValue();
    }

    @Deprecated
    public ConfirmedTransaction getConfirmedTransaction(String signature) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(signature);
        // TODO jsonParsed, base58, base64
        // the default encoding is JSON
        // params.add("json");

        return client.call("getConfirmedTransaction", params, ConfirmedTransaction.class);
    }

    @Deprecated
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<SignatureInformation> getConfirmedSignaturesForAddress2(PublicKey account, int limit)
            throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(account.toString());
        params.add(new ConfigObjects.ConfirmedSignFAddr2(limit));

        List<AbstractMap> rawResult = client.call("getConfirmedSignaturesForAddress2", params, List.class);

        List<SignatureInformation> result = new ArrayList<SignatureInformation>();
        for (AbstractMap item : rawResult) {
            result.add(new SignatureInformation(item));
        }

        return result;
    }

    public List<ProgramAccount> getProgramAccounts(PublicKey account, long offset, String bytes) throws RpcException {
        List<Object> filters = new ArrayList<Object>();
        filters.add(new ConfigObjects.Filter(new ConfigObjects.Memcmp(offset, bytes)));

        ConfigObjects.ProgramAccountConfig programAccountConfig = new ConfigObjects.ProgramAccountConfig(filters);
        return getProgramAccounts(account, programAccountConfig);
    }

    public List<ProgramAccount> getProgramAccounts(PublicKey account) throws RpcException {
        return getProgramAccounts(account, new ConfigObjects.ProgramAccountConfig(RpcSendTransactionConfig.Encoding.base64));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<ProgramAccount> getProgramAccounts(PublicKey account, ConfigObjects.ProgramAccountConfig programAccountConfig)
            throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(account.toString());

        if (programAccountConfig != null) {
            params.add(programAccountConfig);
        }

        List<AbstractMap> rawResult = client.call("getProgramAccounts", params, List.class);

        List<ProgramAccount> result = new ArrayList<ProgramAccount>();
        for (AbstractMap item : rawResult) {
            result.add(new ProgramAccount(item));
        }

        return result;
    }

    public AccountInfo getAccountInfo(PublicKey account) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(account.toString());
        params.add(new RpcSendTransactionConfig());

        return client.call("getAccountInfo", params, AccountInfo.class);
    }

    public long getMinimumBalanceForRentExemption(long dataLength) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(dataLength);

        return client.call("getMinimumBalanceForRentExemption", params, Long.class);
    }

    public long getBlockTime(long block) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(block);

        return client.call("getBlockTime", params, Long.class);
    }

    public String requestAirdrop(PublicKey address, long lamports) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(address.toString());
        params.add(lamports);

        return client.call("requestAirdrop", params, String.class);
    }

    public Block getBlock(long block) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(block);

        return client.call("getBlock", params, Block.class);
    }

    public String getBlockHeight() throws RpcException {
        List<Object> params = new ArrayList<Object>();

        return client.call("getBlockHeight", params, String.class);
    }

    public String getBlockProduction(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getBlockProduction", params, String.class);
    }

    public String getBlockCommitment(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getBlockCommitment", params, String.class);
    }

    public String getBlocks(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getBlocks", params, String.class);
    }

    public List<Double> getBlocksWithLimit(long block, long limit) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(block);
        params.add(limit);

        List<Double> rawResult = client.call("getBlocksWithLimit", params, List.class);

        return rawResult;
    }

    public String getClusterNodes(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getClusterNodes", params, String.class);
    }

    public String getEpochInfo(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getEpochInfo", params, String.class);
    }

    public String getEpochSchedule(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getEpochSchedule", params, String.class);
    }

    public String getFeeForMessage(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getFeeForMessage", params, String.class);
    }

    public String getFirstAvailableBlock(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");

//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getFirstAvailableBlock", params, String.class);
    }

    public String getGenesisHash(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");

//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getGenesisHash", params, String.class);
    }

    public String getHealth() throws RpcException {
        List<Object> params = new ArrayList<Object>();

        return client.call("getHealth", params, String.class);
    }

    public String getHighestSnapshotSlot(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getHighestSnapshotSlot", params, String.class);
    }

    public String getIdentity(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getIdentity", params, String.class);
    }

    public String getInflationGovernor(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getInflationGovernor", params, String.class);
    }

    public String getInflationRate(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getInflationRate", params, String.class);
    }

    public String getInflationReward(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getInflationReward", params, String.class);
    }

    public String getLargestAccounts(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getLargestAccounts", params, String.class);
    }

    public String getLatestBlockhash(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getLatestBlockhash", params, String.class);
    }

    public String getLeaderSchedule(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getLeaderSchedule", params, String.class);
    }

    public String getMaxRetransmitSlot(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getMaxRetransmitSlot", params, String.class);
    }

    public String getMaxShredInsertSlot(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getMaxShredInsertSlot", params, String.class);
    }

    public String getMultipleAccounts(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getMultipleAccounts", params, String.class);
    }

    public String getRecentPerformanceSamples(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getRecentPerformanceSamples", params, String.class);
    }

    public String getSignaturesForAddress(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getSignaturesForAddress", params, String.class);
    }

    public String getSignatureStatuses(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getSignatureStatuses", params, String.class);
    }

    public String getSlot(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getSlot", params, String.class);
    }

    public String getSlotLeader(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getSlotLeader", params, String.class);
    }

    public String getSlotLeaders(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getSlotLeaders", params, String.class);
    }

    public String getStakeActivation(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getStakeActivation", params, String.class);
    }

    public String getSupply(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getSupply", params, String.class);
    }

    public String getTokenAccountBalance(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getTokenAccountBalance", params, String.class);
    }

    public String getTokenAccountsByDelegate(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getTokenAccountsByDelegate", params, String.class);
    }

    public String getTokenAccountsByOwner(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getTokenAccountsByOwner", params, String.class);
    }

    public String getTokenLargestAccounts(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getTokenLargestAccounts", params, String.class);
    }

    public String getTokenSupply(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getTokenSupply", params, String.class);
    }

    public String getTransaction(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getTransaction", params, String.class);
    }

    public String getTransactionCount(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getTransactionCount", params, String.class);
    }

    public String getVersion(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getVersion", params, String.class);
    }

    public String getVoteAccounts(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("getVoteAccounts", params, String.class);
    }

    public String isBlockhashValid(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("isBlockhashValid", params, String.class);
    }

    public String minimumLedgerSlot(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("minimumLedgerSlot", params, String.class);
    }

    public String simulateTransaction(PublicKey address, long lamports) throws RpcException {
        throw new UnsupportedOperationException("Not implemented yet");
//        List<Object> params = new ArrayList<Object>();
//
//        params.add(address.toString());
//        params.add(lamports);
//
//        return client.call("simulateTransaction", params, String.class);
    }

}
