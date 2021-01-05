package org.p2p.solanaj.serum;

import org.p2p.solanaj.utils.ByteUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * first 5 bytes = "serum", start at position 5
 * zero(4) = blob(4) = 4 bytes
 *
 * export const SLAB_LAYOUT = struct([
 *   SLAB_HEADER_LAYOUT,
 *   seq(
 *     SLAB_NODE_LAYOUT,
 *     offset(
 *       SLAB_HEADER_LAYOUT.layoutFor('bumpIndex'),
 *       SLAB_HEADER_LAYOUT.offsetOf('bumpIndex') - SLAB_HEADER_LAYOUT.span,
 *     ),
 *     'nodes',
 *   ),
 * ]);
 *
 *
 * slab header layout:
 * const SLAB_HEADER_LAYOUT = struct(
 *   [
 *     // Number of modified slab nodes
 *     u32('bumpIndex'), 5-9
 *     zeros(4), // Consider slabs with more than 2^32 nodes to be invalid 10-14
 *
 *     // Linked list of unused nodes
 *     u32('freeListLen'), 15-19
 *     zeros(4), //20-24
 *     u32('freeListHead'), 25-29
 *
 *     u32('root'), 30-34
 *
 *     u32('leafCount'), 35-39
 *     zeros(4),
 *   ],
 *   'header',
 * );
 *
 * const SLAB_NODE_LAYOUT = union(u32('tag'), blob(68), 'node');
 * SLAB_NODE_LAYOUT.addVariant(0, struct([]), 'uninitialized');
 * SLAB_NODE_LAYOUT.addVariant(
 *   1,
 *   struct([
 *     // Only the first prefixLen high-order bits of key are meaningful
 *     u32('prefixLen'),
 *     u128('key'),
 *     seq(u32(), 2, 'children'),
 *   ]),
 *   'innerNode',
 * );
 *
 * ....
 */
// Takes in bytes following an AccountFlag object.
public class Slab {

    private static final int INT32_SIZE = 4;

    // Offsets. TODO put these in their own file
    // STARTS at 13, since accountflags from the orderbook struct ends there. TODO - refactor this into something sensible

    private static final int BUMP_INDEX_OFFSET = 5 + 13;
    private static final int FREE_LIST_LEN_OFFSET = 15 + 13;
    private static final int FREE_LIST_HEAD_OFFSET = 20 + 13;
    private static final int ROOT_OFFSET = 25 + 13;
    private static final int LEAF_COUNT_OFFSET = 30 + 13;

    private int bumpIndex;
    private int freeListLen;
    private int freeListHead; // memory address?
    private int root;
    private int leafCount;

    public static Slab readOrderBookSlab(byte[] data) {
        final Slab slab = new Slab();

        int bumpIndex = slab.readBumpIndex(data);
        slab.setBumpIndex(bumpIndex);

        int freeListLen = slab.readFreeListLen(data);
        slab.setFreeListLen(freeListLen);

        int freeListHead = slab.readFreeListHead(data);
        slab.setFreeListHead(freeListHead);

        int root = slab.readRoot(data);
        slab.setRoot(root);

        int leafCount = slab.readLeafcount(data);
        slab.setLeafCount(leafCount);


        System.out.println("bumpIndex = " + bumpIndex);
        System.out.println("freeListLen = " + freeListLen);
        System.out.println("freeListHead = " + freeListHead);
        System.out.println("root = " + root);
        System.out.println("leafCount = " + leafCount);



        return slab;
    }

    public int getLeafCount() {
        return leafCount;
    }

    public void setLeafCount(int leafCount) {
        this.leafCount = leafCount;
    }

    private int readLeafcount(byte[] data) {
        final byte[] bumpIndexBytes = ByteUtils.readBytes(data, LEAF_COUNT_OFFSET, INT32_SIZE);

        return readInt32(bumpIndexBytes);
    }

    public int getRoot() {
        return root;
    }

    public void setRoot(int root) {
        this.root = root;
    }

    private int readRoot(byte[] data) {
        final byte[] bumpIndexBytes = ByteUtils.readBytes(data, ROOT_OFFSET, INT32_SIZE);

        return readInt32(bumpIndexBytes);
    }

    public int getFreeListHead() {
        return freeListHead;
    }

    public void setFreeListHead(int freeListHead) {
        this.freeListHead = freeListHead;
    }

    private int readFreeListHead(byte[] data) {
        final byte[] bumpIndexBytes = ByteUtils.readBytes(data, FREE_LIST_HEAD_OFFSET, INT32_SIZE);

        return readInt32(bumpIndexBytes);
    }

    private int readFreeListLen(byte[] data) {
        final byte[] bumpIndexBytes = ByteUtils.readBytes(data, FREE_LIST_LEN_OFFSET, INT32_SIZE);

        return readInt32(bumpIndexBytes);
    }

    public int getFreeListLen() {
        return freeListLen;
    }

    public void setFreeListLen(int freeListLen) {
        this.freeListLen = freeListLen;
    }

    public int getBumpIndex() {
        return bumpIndex;
    }

    public void setBumpIndex(int bumpIndex) {
        this.bumpIndex = bumpIndex;
    }

    private int readBumpIndex(byte[] data) {
        final byte[] bumpIndexBytes = ByteUtils.readBytes(data, BUMP_INDEX_OFFSET, INT32_SIZE);
        
        return readInt32(bumpIndexBytes);
    }
    
    public int readInt32(byte[] data) {
        // convert 4 bytes into an int.

        //  create a byte buffer and wrap the array
        ByteBuffer bb = ByteBuffer.wrap(data);

        //  if the file uses little endian as apposed to network
        //  (big endian, Java's native) format,
        //  then set the byte order of the ByteBuffer
        bb.order(ByteOrder.LITTLE_ENDIAN);

        //  read your integers using ByteBuffer's getInt().
        //  four bytes converted into an integer!
        return bb.getInt(0);
    }

}
