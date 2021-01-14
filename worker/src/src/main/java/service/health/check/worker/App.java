package service.health.check.worker;

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
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.health.check.messages.AddressToCheck;
import service.health.check.messages.CheckedAddress;
import service.health.check.messages.Config;
import java.util.Random;

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

		channel.queueDeclare(Config.ADDRESSES_TO_CHECK_QUEUE, true, false, false, null);

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				doWork(logger, channel, body);
			}
		};
		channel.basicConsume(Config.ADDRESSES_TO_CHECK_QUEUE, true, consumer);
	}

	private static void doWork(Logger logger, Channel channel, byte[] body) throws IOException {
		String messageJson = new String(body);
		logger.info("Worker - Message received: " + messageJson);

		final ObjectMapper mapper = new ObjectMapper();
		AddressToCheck addressToCheck = mapper.readValue(messageJson,
				new TypeReference<AddressToCheck>() {
				});

		Random rand = new Random();
		CheckedAddress checkedAddress = new CheckedAddress(addressToCheck.getHost(), addressToCheck.getPort(), rand.nextInt(2) > 0);
		channel.queueDeclare(Config.CHECKED_ADDRESSES_QUEUE, true, false, false, null);
		String messageCheckAddressJson = mapper.writeValueAsString(checkedAddress);
		channel.basicPublish("", Config.CHECKED_ADDRESSES_QUEUE, MessageProperties.PERSISTENT_TEXT_PLAIN, messageCheckAddressJson.getBytes());
		logger.info("Worker - Message sent: " + messageCheckAddressJson);

		logger.info("Worker - Work done! " + messageJson);
	}

}
