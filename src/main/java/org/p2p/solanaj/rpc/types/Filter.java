package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class Filter {
        @Json(name = "memcmp")
        private Memcmp memcmp;

        public Filter() {
        }

        public Filter(Memcmp memcmp) {
            this.memcmp = memcmp;
        }
}