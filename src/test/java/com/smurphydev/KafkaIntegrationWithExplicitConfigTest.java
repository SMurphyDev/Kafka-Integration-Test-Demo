package com.smurphydev;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.smurphydev.config.ConfigProperties;
import com.smurphydev.dto.Person;
import com.smurphydev.fixtures.GenericTestConsumer;
import com.smurphydev.fixtures.GenericTestProducer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(ports = {9092})
public class KafkaIntegrationWithExplicitConfigTest {

  @Autowired private ConfigProperties config;
  @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;

  private static Map<String, Object> personConsumerConfig;
  private GenericTestConsumer<UUID, Person> personConsumer;

  private static Map<String, Object> personProducerConfig;
  private GenericTestProducer<UUID, Person> personProducer;

  @BeforeAll
  public static void testConsumerAndProducerConfiguration() {
    // Test producer Config
    personProducerConfig = new HashMap<>();
    personProducerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    personProducerConfig.put(
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        org.apache.kafka.common.serialization.UUIDSerializer.class);
    personProducerConfig.put(
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        org.springframework.kafka.support.serializer.JsonSerializer.class);

    // Test consumer config
    personConsumerConfig = new HashMap<>();
    personConsumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    personConsumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "integration-test");
    personConsumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    personConsumerConfig.put(
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        org.apache.kafka.common.serialization.UUIDDeserializer.class);
    personConsumerConfig.put(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        org.springframework.kafka.support.serializer.JsonDeserializer.class);
    personConsumerConfig.put("spring.json.trusted.packages", "com.smurphydev.dto");
  }

  @BeforeEach
  public void setUp() {
    // Create producer
    personProducer = new GenericTestProducer<UUID, Person>(personProducerConfig);

    // Create Consumer
    personConsumer =
        new GenericTestConsumer<UUID, Person>(
            personConsumerConfig,
            config.getProducerTopic(),
            embeddedKafkaBroker.getPartitionsPerTopic());
    personConsumer.start();
  }

  @AfterEach
  public void tearDown() {
    personConsumer.stop();
    personConsumer = null;
    personProducer = null;
  }

  private Person createDummy() {
    Person person = new Person();
    person.setId(UUID.randomUUID());
    person.setFirstname("Stephen");
    person.setLastname("Murphy");
    person.setAge(29);

    return person;
  }

  @Test
  public void kafkaIntegrationTest() throws InterruptedException {
    Person person = createDummy();

    // We produce to the consumer topic as this is the topic our application reads from
    ProducerRecord<UUID, Person> producerRecord =
        personProducer.sendMessage(config.getConsumerTopic(), person.getId(), person);
    ConsumerRecord<UUID, Person> consumerRecord = personConsumer.getNextRecord();

    assertEquals(producerRecord.key(), producerRecord.key());
    assertEquals(producerRecord.value(), consumerRecord.value());
  }
}
