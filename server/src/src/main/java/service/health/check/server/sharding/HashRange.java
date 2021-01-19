package service.health.check.server.sharding;

import lombok.Value;

@Value
public class HashRange {
    private long fromInclusive;
    private long toExclusive;
}
