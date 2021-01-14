package service.health.check.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.health.check.messages.AddressToCheck;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import service.health.check.messages.Config;

public class App {

	public App() {
	}

	public static void main(String[] args)
			throws IOException, TimeoutException, InterruptedException {
		Logger logger = LoggerFactory.getLogger(App.class);
		ConnectionFactory factory = new ConnectionFactory();
		Connection connection = factory.newConnection(Address.parseAddresses(
				Config.RABBITMQ_CONNECTION_ADDRESS));
		Channel channel = connection.createChannel();

		channel.queueDeclare(Config.ADDRESSES_TO_CHECK_QUEUE, true, false, false, null);

		AddressToCheck googleAddress = new AddressToCheck("http://google.com", "80");
		final ObjectMapper mapper = new ObjectMapper();
		String messageJson = mapper.writeValueAsString(googleAddress);

		for (int i = 0; i < 10; i++) {
			channel.basicPublish("", Config.ADDRESSES_TO_CHECK_QUEUE, MessageProperties.PERSISTENT_TEXT_PLAIN, messageJson.getBytes());
			logger.info("Server - Message sent: " + messageJson);
			Thread.sleep(500);
		}

		channel.close();
		connection.close();
	}

}
