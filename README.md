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

This is where the fun is. The first thing I do is pull in the config properties. This allows me to retrieve the topics I've configured in the application. Then I pull in `GenericTestProducer` & `GenericTestConsumer`. I'll talk more about them below, but for now all you need to know is they read and write to my topics from the test. Then I spin up an `EmbeddedKafkaBroker` on port 9092, the same port my producer and listener are configured on in the application.properties file. You'll notice the `@DirtiesContext` annotation which ensures I have a fresh broker for each test.

### The Set Up

Due to `@DirtiesContext` giving me a new broker instance each run I need to create a new instance of `GenericTestConsumer` & `GenericTestProducer`. Both are explicitly configured in a `@BeforeAll` annotated method. This keeps the configuration of my testing consumers & producers independent from the configuration for the application under test.

### The Tear Down

This is straight forward, I close the consumer and set `personConsumer` & `personProducer` to null so the garbage collector can clean them up.

### The Test

I create a person object and use `GenericTestProducer` to write to *in-person*, the topic my application consumer is listening on. At this point `PersonConsumer` reads the message, logs it, and writes to *out-person*. The topic `GenericTestConsumer` is listening on! This lets me retreive the message and compare it to the origanal input `Person`.

### The GenericTestProducer

This class is just a thin wrapper around `KafkaTemplate` and creates a `ProducerRecord<K, T>`. This could part of the application logic replacing `PersonConsumer`. In an actual production app I would have more business logic in `PersonConsumer`, so I decided to keep them separate. That and reusing business logic to drive tests is the kind of coupling that can really bite you in the ass down the line.

### The GenericTestConsumer

The `GenericTestConsumer` allows me to create and configure a `KafkaMessageListnerContainer` which listens on the topic I am testing. Next I set up a `BlockingQueue` which I use to hold messages recieved by the listener. A `BlockingQueue` is a much better choice than a standard `List` type here becuase the head of the queue may be empty when I initially try to read it, because my data must make a round trip through two kafka topics and my application. With this type I can block until the expected record is present.

In the `start()` method I set up a lister callback on `KafkaMessageListnerContainer` which will populate the `BlockingQueue` as the listener recieves records from kafka. Then I wait until all partitions on the topic are assigned to my listner.

Finally `getNextRecord()` polls the queue for the records recieved, waiting up to 10 seconds before failing with an exception.
