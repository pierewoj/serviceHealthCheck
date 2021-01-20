package service.health.check.server;

import com.google.common.util.concurrent.AbstractScheduledService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import service.health.check.models.Address;
import service.health.check.models.Server;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.web.client.RestTemplate;
import service.health.check.models.Config;

public class App {
	private static final Server currentServer = new Server();
	private static final RestTemplate restTemplate = new RestTemplate();
	private static final String REST_CONFIG_URL = "http://localhost:8090/config/";

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
				testServerRegistryLogic(db);
				AddressToCheckPublisher publisher = new AddressToCheckPublisher();
				publisher.publishAddressesForChecking(db.getAll(Address.class));
			} catch (Exception e) {
				log.error("Exception thrown during run execution: {}", e.toString());
			}
		}

		@Override
		protected Scheduler scheduler() {
			return Scheduler.newFixedRateSchedule(0, 1, TimeUnit.SECONDS);
		}

		// TODO: remove this, create server registry class
		// TODO: do the heartbeats in a separate scheduler
		// TODO: add "garbage-collection" of servers that are inactive for a super long period of time
		//   to avoid table item count to grow super large
		private void testServerRegistryLogic(Database db) {
			currentServer.setLastHeartbeat(Instant.now());
			db.saveServer(currentServer);

			// servers that didn't perform a heartbeat for 10s are considered as inactive
			Instant maxServerAge = Instant.now().minusSeconds(Integer.parseInt(Objects.requireNonNull(
					restTemplate.getForObject(
							REST_CONFIG_URL +
									Config.ConfigName.SERVER_MAX_INACTIVE_TIME,
							Config.class)).getValue()));
			List<Server> servers = db.getAll(Server.class);
			List<Server> activeServers = servers.stream()
					.filter(s -> s.getLastHeartbeat().isAfter(maxServerAge))
					.collect(Collectors.toList());
			log.info("Currently there are {} servers in the DB and {} of them are active",
					servers.size(), activeServers.size());
		}
	}

	public static void main(String[] args) {
		// Each server should get a different ID; if a duplicate occurs (which is extremely unlikely)
		// both servers will be doing the same work. This will result in some of the targets getting
		// checked twice as often until one of the servers is restarted.
		currentServer.setId(UUID.randomUUID().toString());
		ScheduledExecutor executor = new ScheduledExecutor(new Database(), new AddressToCheckPublisher());
		executor.startAsync();
		executor.awaitTerminated();
	}
}
