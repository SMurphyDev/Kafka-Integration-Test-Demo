package com.smurphydev.fixtures;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;

public class GenericTestProducer<K, V> {

  private final KafkaTemplate<K, V> template;

  public GenericTestProducer(final KafkaTemplate<K, V> template) {
    this.template = template;
  }

  public ProducerRecord<K, V> sendMessage(final String topic, final K key, final V value) {
    ProducerRecord<K, V> record = new ProducerRecord<>(topic, key, value);
    template.send(record);

    return record;
  }
}
