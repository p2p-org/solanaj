package org.p2p.solanaj.rpc;

import java.util.*;
import java.util.stream.Collectors;

import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.Transaction;
import org.p2p.solanaj.rpc.types.*;
import org.p2p.solanaj.rpc.types.ConfigObjects.*;
import org.p2p.solanaj.rpc.types.RpcResultTypes.ValueLong;
import org.p2p.solanaj.rpc.types.RpcSendTransactionConfig.Encoding;
import org.p2p.solanaj.ws.SubscriptionWebSocketClient;
import org.p2p.solanaj.ws.listeners.NotificationEventListener;

public class RpcApi {
    private RpcClient client;

    public RpcApi(RpcClient client) {
        this.client = client;
    }

    public String getRecentBlockhash() throws RpcException {
        List<Object> params = new ArrayList<>();
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("commitment", "processed");
        params.add(parameterMap);

        return client.call("getRecentBlockhash", params, RecentBlockhash.class).getRecentBlockhash();
    }

    public String sendTransaction(Transaction transaction, Account signer, String recentBlockHash) throws
            RpcException {
        return sendTransaction(transaction, Collections.singletonList(signer), recentBlockHash);
    }

    public String sendTransaction(Transaction transaction, Account signer) throws RpcException {
        return sendTransaction(transaction, Collections.singletonList(signer), null);
    }

    public String sendTransaction(Transaction transaction, List<Account> signers, String recentBlockHash)
            throws RpcException {
        if (recentBlockHash == null) {
            recentBlockHash = getRecentBlockhash();
        }
        transaction.setRecentBlockHash(recentBlockHash);
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
        String signature = sendTransaction(transaction, signers, null);

        SubscriptionWebSocketClient subClient = SubscriptionWebSocketClient.getInstance(client.getEndpoint());
        subClient.signatureSubscribe(signature, listener);
    }

    public long getBalance(PublicKey account) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(account.toString());

        return client.call("getBalance", params, ValueLong.class).getValue();
    }

    public ConfirmedTransaction getConfirmedTransaction(String signature) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(signature);
        // TODO jsonParsed, base58, base64
        // the default encoding is JSON
        // params.add("json");

        return client.call("getConfirmedTransaction", params, ConfirmedTransaction.class);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<SignatureInformation> getConfirmedSignaturesForAddress2(PublicKey account, int limit)
            throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(account.toString());
        params.add(new ConfirmedSignFAddr2(limit));

        List<AbstractMap> rawResult = client.call("getConfirmedSignaturesForAddress2", params, List.class);

        List<SignatureInformation> result = new ArrayList<SignatureInformation>();
        for (AbstractMap item : rawResult) {
            result.add(new SignatureInformation(item));
        }

        return result;
    }

    public List<ProgramAccount> getProgramAccounts(PublicKey account, long offset, String bytes) throws RpcException {
        List<Object> filters = new ArrayList<Object>();
        filters.add(new Filter(new Memcmp(offset, bytes)));

        ProgramAccountConfig programAccountConfig = new ProgramAccountConfig(filters);
        return getProgramAccounts(account, programAccountConfig);
    }

    public List<ProgramAccount> getProgramAccounts(PublicKey account) throws RpcException {
        return getProgramAccounts(account, new ProgramAccountConfig(Encoding.base64));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<ProgramAccount> getProgramAccounts(PublicKey account, ProgramAccountConfig programAccountConfig)
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<ProgramAccount> getProgramAccounts(PublicKey account, List<Memcmp> memcmpList, int dataSize)
            throws RpcException {
        List<Object> params = new ArrayList<>();

        params.add(account.toString());

        List<Object> filters = new ArrayList<>();
        memcmpList.forEach(memcmp -> {
            filters.add(new Filter(memcmp));
        });

        filters.add(new DataSize(dataSize));

        ProgramAccountConfig programAccountConfig = new ProgramAccountConfig(filters);
        params.add(programAccountConfig);

        List<AbstractMap> rawResult = client.call("getProgramAccounts", params, List.class);

        List<ProgramAccount> result = new ArrayList<>();
        for (AbstractMap item : rawResult) {
            result.add(new ProgramAccount(item));
        }

        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<ProgramAccount> getProgramAccounts(PublicKey account, List<Memcmp> memcmpList) throws RpcException {
        List<Object> params = new ArrayList<>();

        params.add(account.toString());

        List<Object> filters = new ArrayList<>();
        memcmpList.forEach(memcmp -> {
            filters.add(new Filter(memcmp));
        });

        ProgramAccountConfig programAccountConfig = new ProgramAccountConfig(filters);
        params.add(programAccountConfig);

        List<AbstractMap> rawResult = client.call("getProgramAccounts", params, List.class);

        List<ProgramAccount> result = new ArrayList<>();
        for (AbstractMap item : rawResult) {
            result.add(new ProgramAccount(item));
        }

        return result;
    }

    public AccountInfo getAccountInfo(PublicKey account) throws RpcException {
        return getAccountInfo(account, new HashMap<>());
    }

    public AccountInfo getAccountInfo(PublicKey account, Map<String, Object> additionalParams) throws RpcException {
        List<Object> params = new ArrayList<>();

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("commitment", additionalParams.getOrDefault("commitment", "max"));
        parameterMap.put("encoding", additionalParams.getOrDefault("encoding", "base64"));

        // No default for dataSlice
        if (additionalParams.containsKey("dataSlice")) {
            parameterMap.put("dataSlice", additionalParams.get("dataSlice"));
        }

        params.add(account.toString());
        params.add(parameterMap);

        return client.call("getAccountInfo", params, AccountInfo.class);
    }

    public SplTokenAccountInfo getSplTokenAccountInfo(PublicKey account) throws RpcException {
        List<Object> params = new ArrayList<>();
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("encoding", "jsonParsed");

        params.add(account.toString());
        params.add(parameterMap);

        return client.call("getAccountInfo", params, SplTokenAccountInfo.class);
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

    /**
     * Seemingly deprecated on the official Solana API.
     *
     * @return
     * @throws RpcException
     */
    public long getBlockHeight() throws RpcException {
        return client.call("getBlockHeight", new ArrayList<>(), Long.class);
    }

    // TODO - implement the parameters - currently takes in none
    public BlockProduction getBlockProduction(long firstSlot, long lastSlot, PublicKey identity) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        return client.call("getBlockProduction", params, BlockProduction.class);
    }

    public Long minimumLedgerSlot() throws RpcException {
        return client.call("minimumLedgerSlot", new ArrayList<>(), Long.class);
    }

    public SolanaVersion getVersion() throws RpcException {
        return client.call("getVersion", new ArrayList<>(), SolanaVersion.class);
    }

    public String requestAirdrop(PublicKey address, long lamports) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(address.toString());
        params.add(lamports);

        return client.call("requestAirdrop", params, String.class);
    }

    public BlockCommitment getBlockCommitment(long block) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(block);

        return client.call("getBlockCommitment", params, BlockCommitment.class);
    }

    public FeeCalculatorInfo getFeeCalculatorForBlockhash(String blockhash) throws RpcException {
        List<Object> params = new ArrayList<Object>();
        params.add(blockhash);
        return client.call("getFeeCalculatorForBlockhash", params, FeeCalculatorInfo.class);
    }

    public FeeRateGovernorInfo getFeeRateGovernor() throws RpcException {
        return client.call("getFeeRateGovernor", new ArrayList<>(), FeeRateGovernorInfo.class);
    }

    public FeesInfo getFees() throws RpcException {
        return client.call("getFees", new ArrayList<>(), FeesInfo.class);
    }

    public long getTransactionCount() throws RpcException {
        return client.call("getTransactionCount", new ArrayList<>(), Long.class);
    }

    public long getMaxRetransmitSlot() throws RpcException {
        return client.call("getMaxRetransmitSlot", new ArrayList<>(), Long.class);
    }

    public SimulatedTransaction simulateTransaction(String transaction, List<PublicKey> addresses) throws RpcException {
        SimulateTransactionConfig simulateTransactionConfig = new SimulateTransactionConfig(Encoding.base64);
        simulateTransactionConfig.setAccounts(
                Map.of(
                        "encoding",
                        Encoding.base64,
                        "addresses",
                        addresses.stream().map(PublicKey::toBase58).collect(Collectors.toList()))
        );
        simulateTransactionConfig.setReplaceRecentBlockhash(true);

        List<Object> params = new ArrayList<>();
        params.add(transaction);
        params.add(simulateTransactionConfig);

        SimulatedTransaction simulatedTransaction = client.call(
                "simulateTransaction",
                params,
                SimulatedTransaction.class
        );

        return simulatedTransaction;
    }


    public List<ClusterNode> getClusterNodes() throws RpcException {
        List<Object> params = new ArrayList<Object>();

        // TODO - fix uncasted type stuff
        List<AbstractMap> rawResult = client.call("getClusterNodes", params, List.class);

        List<ClusterNode> result = new ArrayList<>();
        for (AbstractMap item : rawResult) {
            result.add(new ClusterNode(item));
        }

        return result;
    }

    /**
     * Returns identity and transaction information about a confirmed block in the ledger
     * DEPRECATED: use getBlock instead
     */
    @Deprecated
    public ConfirmedBlock getConfirmedBlock(int slot) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(slot);
        params.add(new ConfirmedBlockConfig());

        return client.call("getConfirmedBlock", params, ConfirmedBlock.class);
    }


    /**
     * Returns information about the current epoch
     * @return
     * @throws RpcException
     */
    public EpochInfo getEpochInfo() throws RpcException {
        List<Object> params = new ArrayList<Object>();

        return client.call("getEpochInfo", params, EpochInfo.class);
    }

    public EpochSchedule getEpochSchedule() throws RpcException {
        List<Object> params = new ArrayList<Object>();

        return client.call("getEpochSchedule", params, EpochSchedule.class);
    }

    public PublicKey getTokenAccountsByOwner(PublicKey owner, PublicKey tokenMint) throws RpcException {
        List<Object> params = new ArrayList<>();
        params.add(owner.toBase58());

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("mint", tokenMint.toBase58());
        params.add(parameterMap);

        Map<String, Object> rawResult = client.call("getTokenAccountsByOwner", params, Map.class);

        PublicKey tokenAccountKey;

        try {
            String base58 = (String) ((Map) ((List) rawResult.get("value")).get(0)).get("pubkey");
            tokenAccountKey = new PublicKey(base58);

        } catch (Exception ex) {
            throw new RpcException("unable to get token account by owner");
        }

        return tokenAccountKey;
    }

    public InflationRate getInflationRate() throws RpcException {
        return client.call("getInflationRate", new ArrayList<>(), InflationRate.class);
    }

    public InflationGovernor getInflationGovernor() throws RpcException {
        return client.call("getInflationGovernor", new ArrayList<>(), InflationGovernor.class);
    }

    public List<InflationReward> getInflationReward(List<PublicKey> addresses) throws RpcException {
        return getInflationReward(addresses, null, null);
    }

    public List<InflationReward> getInflationReward(List<PublicKey> addresses, Long epoch, String commitment)
            throws RpcException {
        List<Object> params = new ArrayList<>();

        params.add(addresses.stream().map(PublicKey::toString).collect(Collectors.toList()));

        if (null != epoch) {
            RpcEpochConfig rpcEpochConfig;
            if (null == commitment) {
                rpcEpochConfig = new RpcEpochConfig(epoch);
            } else {
                rpcEpochConfig = new RpcEpochConfig(epoch, commitment);
            }
            params.add(rpcEpochConfig);
        }

        List<AbstractMap> rawResult = client.call("getInflationReward", params, List.class);

        List<InflationReward> result = new ArrayList<>();
        for (AbstractMap item : rawResult) {
            result.add(new InflationReward(item));
        }

        return result;
    }

    public long getSlot() throws RpcException {
        return client.call("getSlot", new ArrayList<>(), Long.class);
    }

    public PublicKey getSlotLeader() throws RpcException {
        return new PublicKey(client.call("getSlotLeader", new ArrayList<>(), String.class));
    }

    public List<PublicKey> getSlotLeaders(long startSlot, long limit) throws RpcException {
        List<Object> params = new ArrayList<>();

        params.add(startSlot);
        params.add(limit);

        List<String> rawResult = client.call("getSlotLeaders", params, List.class);

        List<PublicKey> result = new ArrayList<>();
        for (String item : rawResult) {
            result.add(new PublicKey(item));
        }

        return result;
    }

    public long getSnapshotSlot() throws RpcException {
        return client.call("getSnapshotSlot", new ArrayList<>(), Long.class);
    }

    public long getMaxShredInsertSlot() throws RpcException {
        return client.call("getMaxShredInsertSlot", new ArrayList<>(), Long.class);
    }

    public PublicKey getIdentity() throws RpcException {
        Map<String, Object> rawResult = client.call("getIdentity", new ArrayList<>(), Map.class);

        PublicKey identity;
        try {
            String base58 = (String) rawResult.get("identity");
            identity = new PublicKey(base58);

        } catch (Exception ex) {
            throw new RpcException("unable to get identity");
        }

        return identity;
    }

    public Supply getSupply() throws RpcException {
        return client.call("getSupply", new ArrayList<>(), Supply.class);
    }

    public long getFirstAvailableBlock() throws RpcException {
        return client.call("getFirstAvailableBlock", new ArrayList<>(), Long.class);
    }

    public String getGenesisHash() throws RpcException {
        return client.call("getGenesisHash", new ArrayList<>(), String.class);
    }

}
