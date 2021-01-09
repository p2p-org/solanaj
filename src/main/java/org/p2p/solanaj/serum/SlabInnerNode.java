package org.p2p.solanaj.serum;

public class SlabInnerNode extends SlabNode {

    int prefixLen;
    byte[] key;
    int child1;
    int child2;

    public SlabInnerNode(int prefixLen, byte[] key, int child1, int child2) {
        this.prefixLen = prefixLen;
        this.key = key;
        this.child1 = child1;
        this.child2 = child2;
    }

    public int getPrefixLen() {
        return prefixLen;
    }

    public void setPrefixLen(int prefixLen) {
        this.prefixLen = prefixLen;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public int getChild1() {
        return child1;
    }

    public void setChild1(int child1) {
        this.child1 = child1;
    }

    public int getChild2() {
        return child2;
    }

    public void setChild2(int child2) {
        this.child2 = child2;
    }
}
