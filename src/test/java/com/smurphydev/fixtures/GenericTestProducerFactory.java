package com.smurphydev.fixtures;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class GenericTestProducerFactory<K, V> {

  @Autowired private final KafkaTemplate<K, V> kafkaTemplate;

  public GenericTestProducerFactory(final KafkaTemplate<K, V> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public GenericTestProducer<K, V> getTestProducerWithAutoConfig() {
    return new GenericTestProducer<>(kafkaTemplate);
  }

  public GenericTestProducer<K, V> getTestProducerWithConfig(Map<String, Object> config) {
    DefaultKafkaProducerFactory<K, V> producerFactory = new DefaultKafkaProducerFactory<>(config);
    KafkaTemplate<K, V> templateWithConfig = new KafkaTemplate<>(producerFactory);

    return new GenericTestProducer<>(templateWithConfig);
  }
}
