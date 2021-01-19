package service.health.check.server;

import lombok.AllArgsConstructor;

import service.health.check.models.Address;
import service.health.check.server.sharding.HashRange;
import service.health.check.server.sharding.HashRing;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class AddressToCheckFetcher {
    private final Database db;
    private final ServiceRegistry registry;

    List<Address> fetchAddressesToCheck() {
        List<String> activeSrvs = registry.getActiveServers();
        String curSrv = registry.getCurrentServerId();

        List<HashRange> hashRanges = HashRing.getHashRangesForServer(curSrv, activeSrvs);

        return hashRanges.stream()
                .flatMap(rng -> db.getAddressesForHashRange(rng).stream())
                .collect(Collectors.toList());
    }
}
