package com.smurphydev.fixtures;

import java.util.Map;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

public class GenericTestProducer<K, V> {

  private final KafkaTemplate<K, V> template;

  public GenericTestProducer(final Map<String, Object> config) {
    DefaultKafkaProducerFactory<K, V> producerFactory = new DefaultKafkaProducerFactory<>(config);
    this.template = new KafkaTemplate<>(producerFactory);
  }

  public ProducerRecord<K, V> sendMessage(final String topic, final K key, final V value) {
    ProducerRecord<K, V> record = new ProducerRecord<>(topic, key, value);
    template.send(record);

    return record;
  }
}
