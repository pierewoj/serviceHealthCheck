package service.health.check.worker;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;

import lombok.extern.slf4j.Slf4j;
import service.health.check.messages.Config;

@Slf4j
public class App {

    public App() {
    }

    public static void main(String[] args)
            throws IOException, TimeoutException {
        Connection queueConnection = getQueueConnection();
        Channel channel = queueConnection.createChannel();
        channel.queueDeclare(Config.ADDRESSES_TO_CHECK_QUEUE, true, false, false, null);
        Consumer consumer = new CheckAddressMessageConsumer(channel);
        channel.basicConsume(Config.ADDRESSES_TO_CHECK_QUEUE, true, consumer);
    }

    private static Connection getQueueConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        return factory.newConnection(Address.parseAddresses(Config.RABBITMQ_CONNECTION_ADDRESS));
    }
}
