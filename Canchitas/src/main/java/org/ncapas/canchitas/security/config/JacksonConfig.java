package org.ncapas.canchitas.security.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.*;

import java.time.LocalTime;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer localTimeEsModule() {
        return builder -> {
            SimpleModule m = new SimpleModule();
            m.addDeserializer(LocalTime.class, new LocalTimeEsDeserializer());
            m.addSerializer(LocalTime.class,  new LocalTimeEsSerializer());
            builder.modules(m);
        };
    }
}
