package service.health.check.server;

import com.google.common.util.concurrent.AbstractScheduledService;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class App {
	public App() {
	}

	@AllArgsConstructor
	static class ScheduledExecutor extends AbstractScheduledService
	{
		private final Database db;
		private final AddressToCheckPublisher pub;

		@Override
		protected void runOneIteration() throws Exception {
			Database db = new Database();
			AddressToCheckPublisher publisher = new AddressToCheckPublisher();
			publisher.publishAddressesForChecking(db.getAllAddresses());
		}

		@Override
		protected Scheduler scheduler() {
			return Scheduler.newFixedRateSchedule(0, 1, TimeUnit.SECONDS);
		}
	}

	public static void main(String[] args)
			throws IOException, TimeoutException {
		ScheduledExecutor executor = new ScheduledExecutor(new Database(), new AddressToCheckPublisher());
		executor.startAsync();
		executor.awaitTerminated();
	}
}
