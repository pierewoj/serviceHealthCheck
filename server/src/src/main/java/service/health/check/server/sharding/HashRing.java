package service.health.check.server.sharding;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HashRing {
    public List<HashRange> getHashRangesForServer(String currentServerId, List<String> allActiveServers) {
        List<Long> hashRing = allActiveServers.stream()
                .flatMap(s -> getHashValuesForServer(s).stream())
                .sorted()
                .collect(Collectors.toList());

        Set<Long> hashValuesForCurrentServer = getHashValuesForServer(currentServerId);

        List<HashRange> result = new ArrayList<>();
        for(int i = 0; i < hashRing.size(); i++) {
            if (hashValuesForCurrentServer.contains(hashRing.get(i))) {
                if (i == hashRing.size() - 1) {
                    result.add(new HashRange(hashRing.get(i), Long.MAX_VALUE));
                    result.add(new HashRange(Long.MIN_VALUE, hashRing.get(0)));
                } else {
                    result.add(new HashRange(hashRing.get(i), hashRing.get(i+1)));
                }
            }
        }
        return result;
    }

    private Set<Long> getHashValuesForServer(String server) {
        int datapointsPerServer = 3;
        return IntStream.range(0, datapointsPerServer)
                .mapToObj(i -> getHashValue(server + i))
                .collect(Collectors.toSet());
    }

    private long getHashValue(String s) {
        return Hashing.sha256().hashString(s, Charset.defaultCharset()).asLong();
    }
}
