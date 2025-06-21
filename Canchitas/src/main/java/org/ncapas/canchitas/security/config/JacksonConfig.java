// src/main/java/org/ncapas/canchitas/security/config/JacksonConfig.java
package org.ncapas.canchitas.security.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            // 1) Creamos un módulo JSR-310 para java.time.*
            JavaTimeModule javaTimeModule = new JavaTimeModule();

            // — Registramos LocalDate con formato ISO yyyy-MM-dd
            DateTimeFormatter dateFmt = DateTimeFormatter.ISO_LOCAL_DATE;
            javaTimeModule.addDeserializer(
                    LocalDate.class,
                    new LocalDateDeserializer(dateFmt)
            );
            javaTimeModule.addSerializer(
                    LocalDate.class,
                    new LocalDateSerializer(dateFmt)
            );

            // — Registramos tu LocalTimeEsDeserializer/Serializer
            javaTimeModule.addDeserializer(
                    LocalTime.class,
                    new LocalTimeEsDeserializer()
            );
            javaTimeModule.addSerializer(
                    LocalTime.class,
                    new LocalTimeEsSerializer()
            );

            // 2) Insertamos el módulo y deshabilitamos timestamps
            builder
                    .modules(javaTimeModule)
                    .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }
}
