package service.health.check.server.sharding;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class HashRingTest {
    @Test
    public void testThatHashRangesAreAValidPartitionOfIntegerSet() {
        // Arrange
        List<String> srvs = ImmutableList.of("a", "b", "c");

        // Act
        List<HashRange> ranges = srvs.stream()
                .flatMap(srv -> HashRing.getHashRangesForServer(srv, srvs).stream())
                // sorted by range start
                .sorted(Comparator.comparingLong(HashRange::getFromInclusive))
                .collect(Collectors.toList());

        // Assert
        // verify that exclusive end of the range is equal to inclusive
        // start of next range
        int n = ranges.size();
        assertThat(ranges.get(0).getFromInclusive()).isEqualTo(HashFunction.MIN_VALUE_INCLUSIVE);
        for (int i = 0 ; i < n - 1 ; i++) {
            assertThat(ranges.get(i).getToExclusive())
                    .isEqualTo(ranges.get(i+1).getFromInclusive());
        }
        assertThat(ranges.get(n-1).getToExclusive()).isEqualTo(HashFunction.MAX_VALUE_EXCLUSIVE);
    }
}