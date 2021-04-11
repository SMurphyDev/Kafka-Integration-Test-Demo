package com.smurphydev.fixtures;

import java.util.UUID;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class GenericTestProducer<T> {

  private final KafkaTemplate<String, T> template;

  @Autowired
  public GenericTestProducer(final KafkaTemplate<String, T> template) {
    this.template = template;
  }

  public ProducerRecord<String, T> sendMessage(final String topic, final T message) {
    String key = UUID.randomUUID().toString();
    ProducerRecord<String, T> record = new ProducerRecord<>(topic, key, message);
    template.send(record);

    return record;
  }
}
