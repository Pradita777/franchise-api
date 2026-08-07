package com.accenture.franchiseapi.config;

import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import java.util.HashMap;
import java.util.Map;


public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

  private static final String PROPERTY_SOURCE_NAME = "dotenv";
  private static final String PREFIX = "env.";

  @Override
  public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().ignoreIfMalformed().load();

    Map<String, Object> properties = new HashMap<>();
    for (DotenvEntry entry : dotenv.entries()) {
      properties.put(PREFIX + entry.getKey(), entry.getValue());
    }

    environment.getPropertySources().addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
  }
}