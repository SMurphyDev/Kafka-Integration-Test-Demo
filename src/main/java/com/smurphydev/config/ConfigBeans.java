package com.smurphydev.config;

import static java.util.Optional.*;

import java.lang.reflect.Field;
import org.slf4j.*;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.MethodParameter;

@Configuration
public class ConfigBeans {

  @Bean
  @Scope("prototype")
  public Logger logger(final InjectionPoint ip) {
    return LoggerFactory.getLogger(
        of(ip.getMethodParameter())
            .<Class>map(MethodParameter::getContainingClass)
            .orElseGet(
                () ->
                    ofNullable(ip.getField())
                        .map(Field::getDeclaringClass)
                        .orElseThrow(IllegalArgumentException::new)));
  }
}
