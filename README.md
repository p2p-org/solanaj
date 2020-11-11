# solanaj
![solanaj](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/536cba58-1bca-4990-8bc3-616dd54f4206/1200x628_java_%281%29.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20201111%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20201111T121416Z&X-Amz-Expires=86400&X-Amz-Signature=257cf3ea6f0bc6a2012214f8771d92b363339b4806e947f8f3bc73c9ac98e018&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%221200x628_java_%281%29.jpg%22)

Solana blockchain client, written in pure Java.
Solanaj is an API for integrating with Solnan blockchain using the [Solana RPC API](https://docs.solana.com/apps/jsonrpc-api)

## Requirements
- Java 7+

## Dependencies
- bitcoinj
- OkHttp
- Moshi

### Example

##### Transfer lamports

```java
RpcClient client = new RpcClient(Cluster.TESTNET);

PublicKey fromPublicKey = new PublicKey("QqCCvshxtqMAL2CVALqiJB7uEeE5mjSPsseQdDzsRUo");
PublicKey toPublickKey = new PublicKey("GrDMoeqMLFjeXQ24H56S1RLgT4R76jsuWCd6SvXyGPQ5");
int lamports = 3000;

Account signer = new Account(secret_key);

Transaction transaction = new Transaction();
transaction.addInstruction(SystemProgram.transfer(fromPublicKey, toPublickKey, lamports));

String signature = client.getApi().sendTransaction(transaction, signer);
```

##### Get balance

```java
RpcClient client = new RpcClient(Cluster.TESTNET);

long balance = client.getApi().getBalance(new PublicKey("QqCCvshxtqMAL2CVALqiJB7uEeE5mjSPsseQdDzsRUo"));
```

## Contribution

Welcome to contribute, feel free to change and open a PR.


## License

Solanaj is available under the MIT license. See the LICENSE file for more info.
