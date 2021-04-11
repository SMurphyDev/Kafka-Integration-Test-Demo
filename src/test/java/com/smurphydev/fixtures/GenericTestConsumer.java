package com.smurphydev.fixtures;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.utils.ContainerTestUtils;

public class GenericTestConsumer<K, V> {
  private final BlockingQueue<ConsumerRecord<K, V>> consumerRecords;
  private final KafkaMessageListenerContainer<K, V> container;
  private final int partitionCount;

  public GenericTestConsumer(KafkaMessageListenerContainer<K, V> container, int partitionCount) {
    consumerRecords = new LinkedBlockingQueue<>();
    this.container = container;
    this.partitionCount = partitionCount;
  }

  public ConsumerRecord<K, V> getNextRecord() throws InterruptedException {
    return consumerRecords.poll(10, TimeUnit.SECONDS);
  }

  public void start() throws IllegalStateException {
    container.setupMessageListener(
        (MessageListener<K, V>)
            record -> {
              consumerRecords.add(record);
            });
    container.start();
    ContainerTestUtils.waitForAssignment(container, partitionCount);
  }

  public void stop() {
    container.stop();
  }
}
