package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import org.p2p.solanaj.core.PublicKey;

import java.util.AbstractMap;

public class ClusterNode {

    public ClusterNode() {
    }

    public ClusterNode(PublicKey pubkey, String gossip, String tpu, String rpc, String version) {
        this.pubkey = pubkey;
        this.gossip = gossip;
        this.tpu = tpu;
        this.rpc = rpc;
        this.version = version;
    }

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

    public PublicKey getPubkey() {
        return pubkey;
    }

    public void setPubkey(PublicKey pubkey) {
        this.pubkey = pubkey;
    }

    public String getGossip() {
        return gossip;
    }

    public void setGossip(String gossip) {
        this.gossip = gossip;
    }

    public String getTpu() {
        return tpu;
    }

    public void setTpu(String tpu) {
        this.tpu = tpu;
    }

    public String getRpc() {
        return rpc;
    }

    public void setRpc(String rpc) {
        this.rpc = rpc;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "ClusterNode{" +
                "pubkey=" + pubkey +
                ", gossip='" + gossip + '\'' +
                ", tpu='" + tpu + '\'' +
                ", rpc='" + rpc + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
