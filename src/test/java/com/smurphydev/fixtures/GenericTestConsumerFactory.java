package com.smurphydev.fixtures;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
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

  public GenericTestConsumer<K, V> getTestConsumerWithAutoConfig(
      String groupId, String topic, int partitionCount) {

    Map<String, Object> config = new HashMap<>(consumerFactory.getConfigurationProperties());
    config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    consumerFactory.updateConfigs(config);

    return getTestConsumer(consumerFactory, topic, partitionCount);
  }

  public GenericTestConsumer<K, V> getTestConsumerWithConfig(
      Map<String, Object> config, String topic, int partitionCount) {
    DefaultKafkaConsumerFactory<K, V> consumerFactoryWithConfig =
        new DefaultKafkaConsumerFactory<>(config);

    return getTestConsumer(consumerFactoryWithConfig, topic, partitionCount);
  }

  private GenericTestConsumer<K, V> getTestConsumer(
      ConsumerFactory<K, V> consumerFactory, String topic, int partitionCount) {
    ContainerProperties containerProperties = new ContainerProperties(topic);
    KafkaMessageListenerContainer<K, V> container =
        new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

    return new GenericTestConsumer<>(container, partitionCount);
  }
}
