package org.p2p.solanaj.serum;

import org.bitcoinj.core.Utils;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.utils.ByteUtils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import static org.bitcoinj.core.Utils.reverseBytes;

/**
 * export const ORDERBOOK_LAYOUT = struct([
 * blob(5), 0-4
 * accountFlagsLayout('accountFlags'), 5-12
 * SLAB_LAYOUT.replicate('slab'), 13 - ...
 * blob(7),
 * ]);
 * <p>
 * first 5 bytes = "serum", start at position 5
 * <p>
 * note: zero(4) = blob(4) = 4 bytes
 * <p>
 * export const SLAB_LAYOUT = struct([
 * SLAB_HEADER_LAYOUT,
 * seq(
 * SLAB_NODE_LAYOUT,
 * offset(
 * SLAB_HEADER_LAYOUT.layoutFor('bumpIndex'),
 * SLAB_HEADER_LAYOUT.offsetOf('bumpIndex') - SLAB_HEADER_LAYOUT.span,
 * ),
 * 'nodes',
 * ),
 * ]);
 * <p>
 * <p>
 * slab header layout:
 * const SLAB_HEADER_LAYOUT = struct(
 * [
 * // Number of modified slab nodes
 * u32('bumpIndex'), 13-16
 * zeros(4), // Consider slabs with more than 2^32 nodes to be invalid 17-20
 * <p>
 * // Linked list of unused nodes
 * u32('freeListLen'), 21-24
 * zeros(4), //25-28
 * u32('freeListHead'), 29-32
 * <p>
 * u32('root'), 33-36
 * <p>
 * u32('leafCount'), 37-40
 * zeros(4), 41-44.
 * ],
 * 'header',
 * );
 * <p>
 * 45 - 48 = ??? = tag and then 68 bytes of data, for N number of times, where N = offset(
 * *       SLAB_HEADER_LAYOUT.layoutFor('bumpIndex'),
 * *       SLAB_HEADER_LAYOUT.offsetOf('bumpIndex') - SLAB_HEADER_LAYOUT.span,
 * *     );
 * <p>
 * 45-48 = tag 1,
 * 49-116 = blob 1
 * 117-120 = tag 2
 * 121-188 = blob 2
 * 189-192 = tag 3
 * ...
 * <p>
 * const SLAB_NODE_LAYOUT = union(u32('tag'), blob(68), 'node');
 * SLAB_NODE_LAYOUT.addVariant(0, struct([]), 'uninitialized');
 * SLAB_NODE_LAYOUT.addVariant(
 * 1,
 * struct([
 * // Only the first prefixLen high-order bits of key are meaningful
 * u32('prefixLen'),
 * u128('key'),
 * seq(u32(), 2, 'children'),
 * ]),
 * 'innerNode',
 * );
 * <p>
 * ....
 */
// Takes in bytes following an AccountFlag object.
public class Slab {

    private static final int INT32_SIZE = 4;

    // Offsets. TODO put these in their own file
    // STARTS at 13, since accountflags from the orderbook struct ends there. TODO - refactor this into something sensible

    private static final int BUMP_INDEX_OFFSET = 13;
    private static final int FREE_LIST_LEN_OFFSET = 21;
    private static final int FREE_LIST_HEAD_OFFSET = 29;
    private static final int ROOT_OFFSET = 33;
    private static final int LEAF_COUNT_OFFSET = 37;
    private static final int SLAB_NODE_OFFSET = 45;

    private int bumpIndex;
    private int freeListLen;
    private int freeListHead; // memory address?
    private int root;
    private int leafCount;
    private ArrayList<SlabNode> slabNodes;

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

        // StabNode should be an interface
        ArrayList<SlabNode> slabNodes = new ArrayList<>();
        // read rest of the binary into slabnodebytes

        System.out.println("reading slabnode at offset 45");
        byte[] slabNodeBytes = ByteUtils.readBytes(data, SLAB_NODE_OFFSET, data.length - 45);

        // TODO - pass in the start of the slabNodes binary instead of start of entire binary
        slabNodes = slab.readSlabNodes(slabNodeBytes, bumpIndex);
        slab.setSlabNodes(slabNodes);


        // calculate number of leafs or whatever to iterate through and use a for loop to create the arraylist
        // slabNodes is backed by an array, which is all we want.
        // in this example, let's just get... 5 nodes for now. calculation on # of nodes to count tbd.
        // could also do subtraction + division based on the size of the data field.
        // probably better to use the hardcoded typescript algorithm (conversion TBD)
        System.out.println("");

        System.out.println("bumpIndex = " + bumpIndex);
        System.out.println("freeListLen = " + freeListLen);
        System.out.println("freeListHead = " + freeListHead);
        System.out.println("root = " + root);
        System.out.println("leafCount = " + leafCount);


        return slab;
    }

    /**
     * [tag 4 bytes][blob 68 bytes]
     * repeated for N times
     * todo- add parameter N to this call
     *
     * @param data
     * @return
     */
    private ArrayList<SlabNode> readSlabNodes(byte[] data, int bumpIndex) {
        ArrayList<SlabNode> slabNodes = new ArrayList<>();

        for (int i = 0; i < bumpIndex; i++) {
            System.out.println("Reading slabNode at offset = " + ((72 * i) + 45));
            slabNodes.add(readSlabNode(ByteUtils.readBytes(data, (72 * i), 72)));
        }

//        System.out.println("reading slabnode at offset 0+45");
//        readSlabNode(ByteUtils.readBytes(data, 0, 72));
//
//        System.out.println("reading slabnode at offset 72+45");
//        readSlabNode(ByteUtils.readBytes(data, 72, 72));
//
//        System.out.println("reading slabnode at offset 144+45");
//        readSlabNode(ByteUtils.readBytes(data, 144, 72));
//
//        System.out.println("reading slabnode at offset 216+45");
//        readSlabNode(ByteUtils.readBytes(data, 216, 72));
//
//        System.out.println("reading slabnode at offset 288+45");
//        readSlabNode(ByteUtils.readBytes(data, 288, 72));
//
//        System.out.println("reading slabnode at offset 360+45");
//        readSlabNode(ByteUtils.readBytes(data, 360, 72));

        // parse blob 1
        // parse innerNode
        // struct([
        // *     // Only the first prefixLen high-order bits of key are meaningful
        // *     u32('prefixLen'),
        // *     u128('key'),
        // *     seq(u32(), 2, 'children'),
        // *   ]),
        // Blob1 = blob data of slabNode


        System.out.println();

        return slabNodes;
    }

    public SlabNode readSlabNode(byte[] data) {
        int tag = readInt32(ByteUtils.readBytes(data, 0, INT32_SIZE));
        byte[] blob1 = ByteUtils.readBytes(data, 4, 68);
        SlabNode slabNode;

        if (tag == 0) {
            System.out.println("tag 0 detected: uninitialized");
            slabNode = null;
        } else if (tag == 1) {
            System.out.println("tag 1 detected: innernode");
            int prefixLen = readInt32(ByteUtils.readBytes(blob1, 0, INT32_SIZE));
            System.out.println("prefixLen = " + prefixLen);

            // Only the first prefixLen high-order bits of key are meaningful\
            int numBytesToRead = (int) Math.ceil(prefixLen / 4.00);
            System.out.println("size of key (in bytes) = " + numBytesToRead);

            byte[] key = ByteUtils.readBytes(blob1, 4, numBytesToRead);
            System.out.println("key = " + new String(key));

            int child1 = readInt32(ByteUtils.readBytes(blob1, 20, 4));
            System.out.println("child1 = " + child1);

            int child2 = readInt32(ByteUtils.readBytes(blob1, 24, 4));
            System.out.println("child2 = " + child2);

            slabNode = new SlabInnerNode(prefixLen, key, child1, child2);
        } else if (tag == 2) {
            System.out.println("tag 2 detected: leafnode");
            byte ownerSlot = ByteUtils.readBytes(blob1, 0, 1)[0];
            System.out.println("ownerSlot = " + ownerSlot);
            byte feeTier = ByteUtils.readBytes(blob1, 1, 1)[0];
            System.out.println("feeTier = " + feeTier);
            // 2 empty bytes

            // "(price, seqNum)"
            // key starts at byte 4, u128. u128 = 128 bits = 16 * 8
            byte[] key = ByteUtils.readBytes(blob1, 4, 16);
            System.out.println("key = " + new String(key));
            double price = ByteUtils.readUint64(key, 0).doubleValue();
            long seqNum = Utils.readInt64(key, 8);

            System.out.println("price = " + price);
            System.out.println("seqNum = " + seqNum);


            // Open orders account
            PublicKey owner = PublicKey.readPubkey(blob1, 20);
            System.out.println("owner = " + owner.toBase58());

            // In units of lot size
            long quantity = Utils.readInt64(blob1, 52);
            System.out.println("quantity = " + quantity);

            long clientOrderId = Utils.readInt64(blob1, 60);
            System.out.println("clientOrderId = " + clientOrderId);

            slabNode = new SlabLeafNode(ownerSlot, feeTier, key, owner, quantity, clientOrderId);
        } else if (tag == 3) {
            System.out.println("tag 3 detected: freenode");
            int next = readInt32(ByteUtils.readBytes(blob1, 0, 4));
            System.out.println("next = " + next);

            slabNode = new SlabInnerNode();
        } else if (tag == 4) {
            System.out.println("tag 4 detected: lastfreenode");
            slabNode = null;
        } else {
            throw new RuntimeException("unknown tag detected during slab deserialization = " + tag);
        }

        System.out.println();

        return slabNode;
    }

    private String getTagType(int tag) {
        if (tag == 1) {
            return "innerNode";
        } else {
            return "unknown";
        }
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

    public ArrayList<SlabNode> getSlabNodes() {
        return slabNodes;
    }

    public void setSlabNodes(ArrayList<SlabNode> slabNodes) {
        this.slabNodes = slabNodes;
    }
}
