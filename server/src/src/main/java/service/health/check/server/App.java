package service.health.check.server;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class App {
	public App() {
	}

	public static void main(String[] args)
			throws IOException, TimeoutException {
		Database db = new Database();
		AddressToCheckPublisher publisher = new AddressToCheckPublisher();
		publisher.publishAddressesForChecking(db.getAllAddresses());
	}
}
