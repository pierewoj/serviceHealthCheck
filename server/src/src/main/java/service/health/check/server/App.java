package service.health.check.server;

public class App {

	public App() {
	}

	public static void main(String[] args) {
		Database db = new Database();
		AddressToCheckPublisher publisher = new AddressToCheckPublisher();
		ServiceRegistry reg = new ServiceRegistry(db);

		PublishingScheduler publishingScheduler = new PublishingScheduler(db, publisher);
		HeartbeatScheduler heartbeatScheduler = new HeartbeatScheduler(reg);

		heartbeatScheduler.startAsync();
		publishingScheduler.startAsync();
		publishingScheduler.awaitTerminated();
		heartbeatScheduler.awaitTerminated();
	}
}
