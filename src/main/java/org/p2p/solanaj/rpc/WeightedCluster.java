package org.p2p.solanaj.rpc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.p2p.solanaj.rpc.types.WeightedEndpoint;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeightedCluster {

    List<WeightedEndpoint> endpoints;

}
