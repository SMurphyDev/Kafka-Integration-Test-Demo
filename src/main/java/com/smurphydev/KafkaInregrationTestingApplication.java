package com.smurphydev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.smurphydev.config")
public class KafkaInregrationTestingApplication {

  public static void main(String[] args) {
    SpringApplication.run(KafkaInregrationTestingApplication.class, args);
  }
}
