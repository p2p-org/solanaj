package org.p2p.solanaj.rpc.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeightedEndpoint {

    private String url;
    private Integer weight;

}
