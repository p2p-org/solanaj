package org.p2p.solanaj.serum;

import org.p2p.solanaj.core.PublicKey;

import java.util.Arrays;

public class SlabLeafNode extends SlabNode {

    private byte ownerSlot;
    private byte feeTier;
    private byte[] key;
    private PublicKey owner;
    private long quantity;
    private long clientOrderId;

    public SlabLeafNode(byte ownerSlot, byte feeTier, byte[] key, PublicKey owner, long quantity, long clientOrderId) {
        this.ownerSlot = ownerSlot;
        this.feeTier = feeTier;
        this.key = key;
        this.owner = owner;
        this.quantity = quantity;
        this.clientOrderId = clientOrderId;
    }

    public byte getOwnerSlot() {
        return ownerSlot;
    }

    public void setOwnerSlot(byte ownerSlot) {
        this.ownerSlot = ownerSlot;
    }

    public byte getFeeTier() {
        return feeTier;
    }

    public void setFeeTier(byte feeTier) {
        this.feeTier = feeTier;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public PublicKey getOwner() {
        return owner;
    }

    public void setOwner(PublicKey owner) {
        this.owner = owner;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getClientOrderId() {
        return clientOrderId;
    }

    public void setClientOrderId(long clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    @Override
    public String toString() {
        return "SlabLeafNode{" +
                "ownerSlot=" + ownerSlot +
                ", feeTier=" + feeTier +
                ", key=" + Arrays.toString(key) +
                ", owner=" + owner +
                ", quantity=" + quantity +
                ", clientOrderId=" + clientOrderId +
                '}';
    }
}
