package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.p2p.solanaj.core.PublicKey;

import java.util.AbstractMap;

@Getter
@ToString
@NoArgsConstructor
public class ClusterNode {

    // Constructor for deserializing into List
    @SuppressWarnings({ "rawtypes" })
    public ClusterNode(AbstractMap pa) {
        this.pubkey = new PublicKey((String) pa.get("pubkey"));
        this.gossip = (String) pa.get("gossip");
        this.tpu = (String) pa.get("tpu");
        this.rpc = (String) pa.get("rpc");
        this.version = (String) pa.get("version");
    }

    @Json(name = "pubkey")
    private PublicKey pubkey;

    @Json(name = "gossip")
    private String gossip;

    @Json(name = "tpu")
    private String tpu;

    @Json(name = "rpc")
    private String rpc;

    @Json(name = "version")
    private String version;
}
