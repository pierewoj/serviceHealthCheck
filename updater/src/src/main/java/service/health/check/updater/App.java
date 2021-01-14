package service.health.check.updater;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.health.check.messages.AddressToCheck;
import service.health.check.messages.CheckedAddress;
import service.health.check.messages.Config;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class App {

	public App() {
	}

	public static void main(String[] args)
			throws IOException, TimeoutException {
		Logger logger = LoggerFactory.getLogger(App.class);
		ConnectionFactory factory = new ConnectionFactory();
		Connection connection = factory.newConnection(Address.parseAddresses(
				Config.RABBITMQ_CONNECTION_ADDRESS));
		Channel channel = connection.createChannel();

		channel.queueDeclare(Config.CHECKED_ADDRESSES_QUEUE, true, false, false, null);

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				doWork(logger, body);
			}
		};
		channel.basicConsume(Config.CHECKED_ADDRESSES_QUEUE, true, consumer);
	}

	private static void doWork(Logger logger, byte[] body) throws JsonProcessingException {
		String messageJson = new String(body);
		final ObjectMapper mapper = new ObjectMapper();
		CheckedAddress checkedAddress = mapper.readValue(messageJson,
				new TypeReference<CheckedAddress>() {
				});
		logger.info("Updater - Message received: " + messageJson);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			logger.error("Updater - ERROR: " + e.getMessage());
		}
		logger.info("Updater - Work done! " + messageJson);
	}

}
