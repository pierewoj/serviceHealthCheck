package service.health.check.worker;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;

import lombok.extern.slf4j.Slf4j;
import service.health.check.messages.AddressToCheck;
import service.health.check.messages.CheckedAddress;
import service.health.check.messages.Config;

@Slf4j
public class CheckAddressMessageConsumer extends DefaultConsumer {

    // dependencies
    private final AddressChecker addressChecker;
    private final ObjectMapper mapper;

    public CheckAddressMessageConsumer(Channel channel) {
        super(channel);
        this.addressChecker = new AddressChecker();
        this.mapper = new ObjectMapper();
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                               byte[] body) throws IOException {
        String messageBody = new String(body);
        AddressToCheck addressToCheck = extractAddress(messageBody);
        CheckedAddress checkedAddress = addressChecker.checkAddress(addressToCheck);
        returnCheckedAddressToQueue(checkedAddress);
        log.info("Worker - Work done! " + messageBody);
    }

    private AddressToCheck extractAddress(String messageBody) throws JsonProcessingException {
        log.info("Worker - Message received: " + messageBody);
        return mapper.readValue(messageBody, AddressToCheck.class);
    }

    private void returnCheckedAddressToQueue(CheckedAddress checkedAddress)
            throws IOException {
        String messageCheckAddressJson = mapper.writeValueAsString(checkedAddress);
        getChannel().queueDeclare(Config.CHECKED_ADDRESSES_QUEUE, true, false, false, null);
        getChannel().basicPublish("", Config.CHECKED_ADDRESSES_QUEUE, MessageProperties.PERSISTENT_TEXT_PLAIN,
                                  messageCheckAddressJson.getBytes());
        log.info("Worker - Message sent: " + messageCheckAddressJson);
    }
}
