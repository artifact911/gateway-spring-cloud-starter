package com.artifact.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
//@ConfigurationProperties("spring.kafka")
public class KafkaProperties {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
}
