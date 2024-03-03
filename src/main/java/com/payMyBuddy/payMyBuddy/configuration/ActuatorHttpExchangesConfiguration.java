package com.payMyBuddy.payMyBuddy.configuration;

import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Allows Actuator httpexchanges
 * Replace Actuator httptrace because it is deprecated
 */
@Configuration
public class ActuatorHttpExchangesConfiguration {
    @Bean
    public HttpExchangeRepository httpExchangeRepository() {return new InMemoryHttpExchangeRepository();}
}
