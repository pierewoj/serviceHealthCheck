package service.health.check.server;

import com.google.common.util.concurrent.AbstractScheduledService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Slf4j
class PublishingScheduler extends AbstractScheduledService
{
    private final AddressToCheckFetcher addressToCheckFetcher;
    private final AddressToCheckPublisher pub;


    @Override
    protected void runOneIteration() {
        try {
            AddressToCheckPublisher publisher = new AddressToCheckPublisher();
            publisher.publishAddressesForChecking(
                    addressToCheckFetcher.fetchAddressesToCheck());
        } catch (Exception e) {
            log.error("Exception thrown during run execution: {}", e.toString());
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, Config.PUBLISHER_RUN_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }
}
