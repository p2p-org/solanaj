package org.p2p.solanaj.rpc;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.Transaction;
import org.p2p.solanaj.rpc.types.RecentBlockhash;
import org.p2p.solanaj.rpc.types.RpcSendTransactionConfig;

public class RpcApi {
    private RpcClient client;

    public RpcApi(RpcClient client) {
        this.client = client;
    }

    public String getRecentBlockhash() throws RpcException {
        return client.call("getRecentBlockhash", null, RecentBlockhash.class).getRecentBlockhash();
    }

    public String sendTransaction(Transaction transaction, Account signer) throws RpcException {
        String recentBlockhash = getRecentBlockhash();
        transaction.setRecentBlockHash(recentBlockhash);
        transaction.sign(signer);
        byte[] serializedTransaction = transaction.serialize();

        String base64Trx = Base64.getEncoder().encodeToString(serializedTransaction);

        List<Object> params = new ArrayList<Object>();

        params.add(base64Trx);

        JsonAdapter<RpcSendTransactionConfig> rpcConfig = new Moshi.Builder().build()
                .adapter(RpcSendTransactionConfig.class);
        RpcSendTransactionConfig config = new RpcSendTransactionConfig();

        params.add(rpcConfig.toJson(config));

        return client.call("sendTransaction", params, String.class);
    }
}
