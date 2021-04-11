package com.smurphydev.consumer;

import com.smurphydev.config.ConfigProperties;
import com.smurphydev.dto.Person;
import com.smurphydev.producer.PersonProducer;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
class PersonConsumer {
  private final Logger logger;
  private final ConfigProperties config;
  private final PersonProducer personProducer;

  @Autowired
  public PersonConsumer(
      final Logger logger, final ConfigProperties config, final PersonProducer personProducer) {
    this.logger = logger;
    this.config = config;
    this.personProducer = personProducer;
  }

  @KafkaListener(topics = "${com.smurphydev.consumer-topic}")
  public void consumePerson(ConsumerRecord<UUID, Person> record) {
    StringBuilder message = new StringBuilder();
    message.append("Consumed [key : value] from topic ");
    message.append(config.getConsumerTopic());
    message.append("\n sending to topic ");
    message.append(config.getProducerTopic());
    message.append(" :\n[");
    message.append(record.key().toString());
    message.append(" : ");
    message.append(record.value().toString());
    message.append(" ]");

    logger.info(message.toString());

    personProducer.sendMessage(record.value());
  }
}
