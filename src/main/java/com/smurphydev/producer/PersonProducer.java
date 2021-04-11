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

  private final KafkaTemplate<String, Person> personTemplate;
  private final ConfigProperties config;

  @Autowired
  public PersonProducer(
      final KafkaTemplate<String, Person> personTemplate, final ConfigProperties config) {
    this.personTemplate = personTemplate;
    this.config = config;
  }

  public void sendMessage(final Person person) {
    String key = UUID.randomUUID().toString();
    ProducerRecord<String, Person> record =
        new ProducerRecord<>(config.getProducerTopic(), key, person);
    personTemplate.send(record);
  }
}
