package service.health.check.server;

import com.google.common.util.concurrent.AbstractScheduledService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import service.health.check.models.Address;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Slf4j
class PublishingScheduler extends AbstractScheduledService
{
    private final Database db;
    private final AddressToCheckPublisher pub;

    @Override
    protected void runOneIteration() {
        try {
            Database db = new Database();
            AddressToCheckPublisher publisher = new AddressToCheckPublisher();
            publisher.publishAddressesForChecking(db.getAll(Address.class));
        } catch (Exception e) {
            log.error("Exception thrown during run execution: {}", e.toString());
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, Config.PUBLISHER_RUN_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }
}
