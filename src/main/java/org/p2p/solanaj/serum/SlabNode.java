package org.p2p.solanaj.serum;

public abstract class SlabNode {

    public SlabNode() {
    }

    // first 4 bytes
    private int tag;
    // bytes 5-72
    private byte[] blob;

    // TODO add getters for variants of the blob or make this an interface

    /**
     * returns the variant of this slabnode. 5 possible values [uninitialized (0), innerNode(1), leafNode(2), freeNode(3), lastFreeNode(4));
     * @return variant of the slabNode
     */
    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public byte[] getBlob() {
        return blob;
    }

    public void setBlob(byte[] blob) {
        this.blob = blob;
    }
}
