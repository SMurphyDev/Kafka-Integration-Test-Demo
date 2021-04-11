package com.smurphydev.fixtures;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.utils.ContainerTestUtils;

public class GenericTestConsumer<T> {
  private final BlockingQueue<ConsumerRecord<String, T>> consumerRecords;
  private final KafkaMessageListenerContainer<String, T> container;
  private final int partitionCount;

  public GenericTestConsumer(
      KafkaMessageListenerContainer<String, T> container, int partitionCount) {
    consumerRecords = new LinkedBlockingQueue<>();
    this.container = container;
    this.partitionCount = partitionCount;
  }

  public ConsumerRecord<String, T> getNextRecord() throws InterruptedException {
    return consumerRecords.poll(10, TimeUnit.SECONDS);
  }

  public void start() throws IllegalStateException {
    container.setupMessageListener(
        (MessageListener<String, T>)
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
