package service.health.check.server;

import com.google.common.util.concurrent.AbstractScheduledService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

public class App {
	public App() {
	}

	@AllArgsConstructor
	@Slf4j
	static class ScheduledExecutor extends AbstractScheduledService
	{
		private final Database db;
		private final AddressToCheckPublisher pub;

		@Override
		protected void runOneIteration() {
			try {
				Database db = new Database();
				AddressToCheckPublisher publisher = new AddressToCheckPublisher();
				publisher.publishAddressesForChecking(db.getAllAddresses());
			} catch (Exception e) {
				log.error("Exception thrown during run execution: {}", e.toString());
			}
		}

		@Override
		protected Scheduler scheduler() {
			return Scheduler.newFixedRateSchedule(0, 1, TimeUnit.SECONDS);
		}
	}

	public static void main(String[] args) {
		ScheduledExecutor executor = new ScheduledExecutor(new Database(), new AddressToCheckPublisher());
		executor.startAsync();
		executor.awaitTerminated();
	}
}
