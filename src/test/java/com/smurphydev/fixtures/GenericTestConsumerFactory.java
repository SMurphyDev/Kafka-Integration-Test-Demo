package com.smurphydev.fixtures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class GenericTestConsumerFactory<K, V> {

  private final ConsumerFactory<K, V> consumerFactory;

  @Autowired
  public GenericTestConsumerFactory(final ConsumerFactory<K, V> consumerFactory) {
    this.consumerFactory = consumerFactory;
  }

  public GenericTestConsumer<K, V> getTestConsumer(
      String groupId, String topic, int partitionCount) {
    ContainerProperties containerProperties = new ContainerProperties(topic);
    containerProperties.setGroupId(groupId);
    KafkaMessageListenerContainer<K, V> container =
        new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

    return new GenericTestConsumer<>(container, partitionCount);
  }
}
