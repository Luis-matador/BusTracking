package com.busTracking.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

     // Configuración de Jackson para manejar correctamente la serialización de fechas y horas en JSON.
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer addJavaTimeModule() {
        return builder -> builder.modules(new JavaTimeModule());
    }
}
