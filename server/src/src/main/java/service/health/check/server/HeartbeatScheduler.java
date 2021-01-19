package service.health.check.server;

import com.google.common.util.concurrent.AbstractScheduledService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import service.health.check.server.sharding.HashRange;
import service.health.check.server.sharding.HashRing;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
class HeartbeatScheduler extends AbstractScheduledService
{
    private final ServiceRegistry registry;

    @Override
    protected void runOneIteration() {
        try {
            // TODO: add "garbage-collection" of servers that are inactive for a super long period of time
            //   to avoid table item count to grow super large
            registry.sendHeartbeat();

            log.info("There are {} active servers", registry.getActiveServers().size());
            List<HashRange> hashRanges = HashRing.getHashRangesForServer(registry.getCurrentServerId(),
                    registry.getActiveServers());
            for (HashRange range : hashRanges) {
                log.info("Assigned range: " + range);
            }
        } catch (Exception e) {
            log.error("Exception thrown during run execution: {}", e.toString());
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, Config.HEARTBEAT_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }
}