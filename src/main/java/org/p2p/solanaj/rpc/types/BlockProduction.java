package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

import java.util.List;
import java.util.Map;

public class BlockProduction {

    public static class BlockProductionRange {
        @Json(name = "firstSlot")
        private double firstSlot;

        @Json(name = "lastSlot")
        private double lastSlot;

        public double getFirstSlot() {
            return firstSlot;
        }

        public double getLastSlot() {
            return lastSlot;
        }
    }

    public static class BlockProductionValue {

        @Json(name = "byIdentity")
        private Map<String, List<Double>> byIdentity;

        public Map<String, List<Double>> getByIdentity() {
            return byIdentity;
        }

        @Json(name = "range")
        private BlockProductionRange blockProductionRange;

        public BlockProductionRange getBlockProductionRange() {
            return blockProductionRange;
        }
    }

    @Json(name = "value")
    private BlockProductionValue value;


    public BlockProduction() {
    }

    public BlockProductionValue getValue() {
        return value;
    }

    public void setValue(BlockProductionValue value) {
        this.value = value;
    }
}
