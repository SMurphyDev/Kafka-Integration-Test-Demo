package com.smurphydev;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.smurphydev.config.ConfigProperties;
import com.smurphydev.dto.Person;
import com.smurphydev.fixtures.GenericTestConsumer;
import com.smurphydev.fixtures.GenericTestConsumerFactory;
import com.smurphydev.fixtures.GenericTestProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
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
public class KafkaIntegrationTest {

  @Autowired private ConfigProperties config;

  @Autowired private GenericTestProducer<Person> personProducer;

  @Autowired private GenericTestConsumerFactory<Person> personConsumerFactory;

  @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;

  private GenericTestConsumer<Person> personConsumer;

  @BeforeEach
  public void setUp() {
    // We consume the producer topic as this is the topic our application writes to.
    personConsumer =
        personConsumerFactory.getTestConsumer(
            "integration-test",
            config.getProducerTopic(),
            embeddedKafkaBroker.getPartitionsPerTopic());
    personConsumer.start();
  }

  @AfterEach
  public void tearDown() {
    personConsumer.stop();
    personConsumer = null;
  }

  private Person createDummy() {
    Person person = new Person();
    person.setFirstname("Stephen");
    person.setLastname("Murphy");
    person.setAge(29);

    return person;
  }

  @Test
  public void kafkaIntegrationTest() throws InterruptedException {
    Person person = createDummy();

    // We produce to the consumer topic as this is the topic our application reads from
    ProducerRecord<String, Person> producerRecord =
        personProducer.sendMessage(config.getConsumerTopic(), person);
    ConsumerRecord<String, Person> consumerRecord = personConsumer.getNextRecord();

    assertEquals(producerRecord.value(), consumerRecord.value());
  }
}
