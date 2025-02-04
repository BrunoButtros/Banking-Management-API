package dev.bruno.banking.service;

import dev.bruno.banking.dto.BalanceResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class MockBalanceService {

    private final WebClient webClient;

    @Value("${mock.api.balance.url}")
    private String mockApiUrl;

    public MockBalanceService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public BalanceResponseDTO getBalance() {
        return webClient
                .get()
                .uri(mockApiUrl)
                .retrieve()
                .bodyToMono(BalanceResponseDTO.class)
                .block();
    }
}
