package com.mdleo.appLiterAlura.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class configuration {
    @Bean
    public ObjectMapper objectMapper() {
        return  new ObjectMapper();
    }
}