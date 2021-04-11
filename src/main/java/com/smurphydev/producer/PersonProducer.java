package com.smurphydev.producer;

import com.smurphydev.config.ConfigProperties;
import com.smurphydev.dto.Person;
import java.util.UUID;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PersonProducer {

  private final KafkaTemplate<UUID, Person> personTemplate;
  private final ConfigProperties config;

  @Autowired
  public PersonProducer(
      final KafkaTemplate<UUID, Person> personTemplate, final ConfigProperties config) {
    this.personTemplate = personTemplate;
    this.config = config;
  }

  public void sendMessage(final Person person) {
    ProducerRecord<UUID, Person> record =
        new ProducerRecord<>(config.getProducerTopic(), person.getId(), person);
    personTemplate.send(record);
  }
}
