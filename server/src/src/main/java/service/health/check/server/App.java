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
import java.util.List;
import java.util.concurrent.TimeoutException;

import service.health.check.messages.Config;
import service.health.check.models.HibernateUtil;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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

		EntityManager entityManager = HibernateUtil.getEntityManagerFactory()
				.createEntityManager();

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<service.health.check.models.Address> criteriaQuery = criteriaBuilder.createQuery(
				service.health.check.models.Address.class);
		Root<service.health.check.models.Address> root = criteriaQuery.from(
				service.health.check.models.Address.class);
		criteriaQuery.select(root);
		List<service.health.check.models.Address> addresses = entityManager.createQuery(
				criteriaQuery).getResultList();

		for (service.health.check.models.Address address : addresses) {
			AddressToCheck googleAddress = new AddressToCheck(address.getHost(),
					address.getPort());
			final ObjectMapper mapper = new ObjectMapper();
			String messageJson = mapper.writeValueAsString(googleAddress);
			channel.basicPublish("", Config.ADDRESSES_TO_CHECK_QUEUE,
					MessageProperties.PERSISTENT_TEXT_PLAIN,
					messageJson.getBytes());
			logger.info("Server - Message sent: " + messageJson);
		}

		channel.close();
		connection.close();
	}

}
