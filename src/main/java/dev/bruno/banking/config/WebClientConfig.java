package dev.bruno.banking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .filter(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
                    MediaType contentType = clientResponse.headers()
                            .contentType()
                            .orElse(MediaType.APPLICATION_JSON);

                    if (MediaType.valueOf("application/json;charset=UTF-8").includes(contentType)) {
                        return Mono.just(clientResponse.mutate()
                                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .build());
                    }
                    return Mono.just(clientResponse);
                }))
                .build();
    }
}
