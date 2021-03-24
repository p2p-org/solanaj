package org.p2p.solanaj.rpc;

import java.util.*;

import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.Transaction;
import org.p2p.solanaj.rpc.types.ConfigObjects.*;
import org.p2p.solanaj.rpc.types.AccountInfo;
import org.p2p.solanaj.rpc.types.ConfirmedTransaction;
import org.p2p.solanaj.rpc.types.ProgramAccount;
import org.p2p.solanaj.rpc.types.RecentBlockhash;
import org.p2p.solanaj.rpc.types.RpcSendTransactionConfig;
import org.p2p.solanaj.rpc.types.SignatureInformation;
import org.p2p.solanaj.rpc.types.RpcResultTypes.ValueLong;
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
        parameterMap.put("commitment", "max");
        params.add(parameterMap);

        return client.call("getRecentBlockhash", params, RecentBlockhash.class).getRecentBlockhash();
    }

    public String sendTransaction(Transaction transaction, Account signer, String recentBlockHash) throws RpcException {
        return sendTransaction(transaction, Collections.singletonList(signer), recentBlockHash);
    }

    public String sendTransaction(Transaction transaction, Account signer) throws RpcException {
        return sendTransaction(transaction, Collections.singletonList(signer), null);
    }

    public String sendTransaction(Transaction transaction, List<Account> signers, String recentBlockHash) throws RpcException {
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<ProgramAccount> getProgramAccounts(PublicKey account, long offset, String bytes) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(account.toString());

        List<Object> filters = new ArrayList<Object>();
        filters.add(new Filter(new Memcmp(offset, bytes)));

        ProgramAccountConfig programAccountConfig = new ProgramAccountConfig(filters);
        params.add(programAccountConfig);

        List<AbstractMap> rawResult = client.call("getProgramAccounts", params, List.class);

        List<ProgramAccount> result = new ArrayList<ProgramAccount>();
        for (AbstractMap item : rawResult) {
            result.add(new ProgramAccount(item));
        }

        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<ProgramAccount> getProgramAccounts(PublicKey account, List<Memcmp> memcmpList, int dataSize) throws RpcException {
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

}
