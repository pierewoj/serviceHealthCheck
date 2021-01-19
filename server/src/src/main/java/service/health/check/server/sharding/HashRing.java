package service.health.check.server.sharding;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static service.health.check.server.sharding.HashFunction.MAX_VALUE_EXCLUSIVE;
import static service.health.check.server.sharding.HashFunction.getHashValue;

public class HashRing {
    public static List<HashRange> getHashRangesForServer(String currentServerId, List<String> allActiveServers) {
        List<Long> hashRing = allActiveServers.stream()
                .flatMap(s -> getHashValuesForServer(s).stream())
                .sorted()
                .collect(Collectors.toList());

        Set<Long> hashValuesForCurrentServer = getHashValuesForServer(currentServerId);

        List<HashRange> result = new ArrayList<>();
        for(int i = 0; i < hashRing.size(); i++) {
            if (hashValuesForCurrentServer.contains(hashRing.get(i))) {
                if (i == hashRing.size() - 1) {
                    result.add(new HashRange(hashRing.get(i), MAX_VALUE_EXCLUSIVE));
                    result.add(new HashRange(HashFunction.MIN_VALUE_INCLUSIVE, hashRing.get(0)));
                } else {
                    result.add(new HashRange(hashRing.get(i), hashRing.get(i+1)));
                }
            }
        }
        return result;
    }

    private static Set<Long> getHashValuesForServer(String server) {
        // increasing this value should improve distribution between servers
        // but make db queries more expensive (more index range queries);
        // tested this with 3 servers and 10k targets and got following target count
        // per server: {s1=2842, s2=3860, s3=3298}
        int datapointsPerServer = 10;
        return IntStream.range(0, datapointsPerServer)
                .mapToObj(i -> getHashValue(server + "#" + i))
                .collect(Collectors.toSet());
    }
}
