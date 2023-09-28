package com.example.translator.Config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class Config {

    @Bean
    public Validator getValidator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }

}
