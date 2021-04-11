package com.smurphydev.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "com.smurphydev")
@ConstructorBinding
@Getter
public class ConfigProperties {

  private final String consumerTopic;
  private final String producerTopic;

  public ConfigProperties(final String consumerTopic, final String producerTopic) {
    this.consumerTopic = consumerTopic;
    this.producerTopic = producerTopic;
  }
}
