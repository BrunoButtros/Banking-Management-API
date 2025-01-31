package dev.bruno.banking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .jackson2JsonDecoder(new org.springframework.http.codec.json.Jackson2JsonDecoder(
                                new com.fasterxml.jackson.databind.ObjectMapper(),
                                MediaType.valueOf("text/json;charset=UTF-8"))))
                .build();

        return WebClient.builder()
                .exchangeStrategies(strategies)
                .build();
    }
}
