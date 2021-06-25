package org.p2p.solanaj.rpc;

import lombok.*;
import org.p2p.solanaj.rpc.types.WeightedEndpoint;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeightedCluster {

    List<WeightedEndpoint> endpoints;

}
