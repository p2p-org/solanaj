package org.p2p.solanaj.serum;

import org.p2p.solanaj.utils.ByteUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
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
 *     u32('bumpIndex'),
 *     zeros(4), // Consider slabs with more than 2^32 nodes to be invalid
 *
 *     // Linked list of unused nodes
 *     u32('freeListLen'),
 *     zeros(4),
 *     u32('freeListHead'),
 *
 *     u32('root'),
 *
 *     u32('leafCount'),
 *     zeros(4),
 *   ],
 *   'header',
 * );
 */
public class Slab {

    private int bumpIndex;

    public static Slab readOrderBookSlab(byte[] data) {
        final Slab slab = new Slab();

        int bumpIndex = slab.readBumpIndex(data);
        slab.setBumpIndex(bumpIndex);

        return slab;
    }

    public int getBumpIndex() {
        return bumpIndex;
    }

    public void setBumpIndex(int bumpIndex) {
        this.bumpIndex = bumpIndex;
    }

    private int readBumpIndex(byte[] data) {
        final byte[] bumpIndexBytes = ByteUtils.readBytes(data, 5, 4);
        
        return readInt32(bumpIndexBytes);
    }
    
    public int readInt32(byte[] data) {
        // convert 4 bytes into an int.

        //  create a byte buffer and wrap the array
        ByteBuffer bb = ByteBuffer.wrap(data);

        //  if the file uses little endian as apposed to network
        //  (big endian, Java's native) format,
        //  then set the byte order of the ByteBuffer
        bb.order(ByteOrder.BIG_ENDIAN);

        //  read your integers using ByteBuffer's getInt().
        //  four bytes converted into an integer!
        System.out.println(bb.getInt(0));

        return bb.getInt(0);
    }

}
