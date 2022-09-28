package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

import java.util.AbstractMap;

public class SignatureInformation {
    @Json(name = "err")
    private Object err;
    @Json(name = "memo")
    private Object memo;
    @Json(name = "signature")
    private String signature;
    @Json(name = "slot")
    private long slot;

    public SignatureInformation() {
    }

    @SuppressWarnings({"rawtypes"})
    public SignatureInformation(AbstractMap info) {
        this.err = info.get("err");
        this.memo = info.get("memo");
        this.signature = (String) info.get("signature");
        this.err = info.get("slot");
    }

    public Object getErr() {
        return err;
    }

    public Object getMemo() {
        return memo;
    }

    public String getSignature() {
        return signature;
    }

    public long getSlot() {
        return slot;
    }

}
