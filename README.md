# Spring Kafka Integration Test Example

The purpose of this project is to demonstrate the basics of spring-kafka integration testing with embedded kafka. I intend it to be educational, for myself mostly. If you're brave enough to use this strategy in production do let me know how it works out!

## The Application

The entry point into the app is the PersonConsumer class. It is a bog standard KafkaListner which relies on the auto configured consumer factory. It reads json from the *in-person* topic, logs the message, and the writes to *out-person*. It expects json based on the Person dto in the format of:

```json
{
    "id": "959e366f-6103-4be6-9d37-152407ac533e",
    "firstname": "Stephen",
    "lastname": "Murphy",
    "age": 186
}
```

That's it, read from one topic, log the message, and write to the next topic.

## The Integration Test

This is where the fun is. The first thing I do is pull in the config properties. This allows me to retrieve the topics I've configured in the application. Then I pull in `GenericTestProducer` & `GenericConsumerFactory`. I'll talk more about them below, but for now all you need to know is they read and write to my topics from the test. Then I spin up an `EmbeddedKafkaBroker` on port 9092, the same port my producer and listener are configured on in the application.properties file. You'll notice the `@DirtiesContext` annotation which ensures we have a fresh broker for each test.

### The Set Up

Due to `@DirtiesContext` giving me a new broker instance each run I need to create a new instance of `GenericTestConsumer`, which is configured per my application.properties file. If you dig into the code you'll see it relies on the auto configured `ConsumerFactory` for its set up.

### The Tear Down

This is straight forward, I close the listner and set `personConsumer` & `personProducer` to null so the garbage collector can clean them up.

### The Test

I create a person object and use `GenericTestProducer` to write to *in-person*, the topic our application consumer is listening on. At this point `PersonConsumer` reads the message, logs it, and writes to *out-person*. The topic `GenericTestConsumer` is listening on! This lets me retreive the message and compare it to the origanal input `Person`.

### The GenericTestProducerFactory

This class handles the configuration for `GenericTestProducer`. It gives us two advantages:

1. I can choose whether to depend on an auto configured `ConsumerFactory` or to create my test consumer or create a new one based on a provided config map.
2. I can easily create a `GenericTestConsumer` any key and value types.

### The GenericTestProducer

This class is just a thin wrapper around `KafkaTemplate` and creates a `ProducerRecord<K, T>`. This could part of the application logic replacing `PersonConsumer`. In an actual production app I would have more business logic in `PersonConsumer`, so I decided to keep them separate. That and reusing business logic to drive tests is the kind of coupling that can bite you in the ass down the line.

### The GenericTestConsumerFactory

The `GenericTestConsumerFactory` allows me to create a `KafkaMessageListnerContainer` relying on the auto configured `ConsumerFactory` for it's base configuration, with one exception: groupId. It is important for your test consumer and application consumer to be in different consumer groups as by default consumers are assigned one per group/topic/partition.

The `KafkaMessageListnerContainer` is the piece which actually listens on the topic we are testing. On it's own though its not very useful. I only configure it here, in the `GenericTestConsumer` I've built the additional machinery which makes this valuable.

Like the `GenericTestProducerFactory`, it gives me options for configuring the consumer.

### The GenericTestConsumer

The `GenericTestConsumer` takes `KafkaMessageListnerContainer` passed in from the `GenericTestConsumerFactory` and adds some extra goodies which will let us read topics from our test. In the constructor I set up a `BlockingQueue` which I use to hold messages recieved by the listener. A `BlockingQueue` is a much better choice than a standard `List` type here becuase the head of the queue may be empty when I initially try to read it, because our data must make a round trip through two kafka topics and my application. With this type I can block until the expected record is present.

In the `start()` method I set up a lister callback on `KafkaMessageListnerContainer` which will populate the `BlockingQueue` as the listener recieves records from kafka. Then I wait until all partitions on the topic are assigned to my listner.

Finally `getNextRecord()` polls the queue for the records recieved, waiting up to 10 seconds before failing with an exception.

### Further Thoughts

In general, my initial strategy tightly couples the configuration of the application listener & producer to their test counterparts. It works because there is exactly one kafka broker and one serialization format. It would not work if I was reading JSON and writing avro, for example.

As a result I've add the `KafkaIntegrationWithExplicitConfigTest` which covers exactly the same test scenario, but shows how to provide explicit configurations for the test consumers/producers.
