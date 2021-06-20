package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class DataSize {
        @Json(name = "dataSize")
        private int dataSize;

        public DataSize() {
        }

        public DataSize(int dataSize) {
            this.dataSize = dataSize;
        }
}