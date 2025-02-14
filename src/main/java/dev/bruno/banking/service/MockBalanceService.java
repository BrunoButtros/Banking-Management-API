package dev.bruno.banking.service;

import dev.bruno.banking.dto.BalanceResponseDTO;
import dev.bruno.banking.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class MockBalanceService {

    private final WebClient.Builder webClientBuilder;

    @Value("${mock.api.balance.url}")
    private String mockApiUrl;

    public BalanceResponseDTO getBalance() {
        WebClient webClient = webClientBuilder.build();
        try {
            return webClient.get()
                    .uri(mockApiUrl)
                    .retrieve()
                    .bodyToMono(BalanceResponseDTO.class)
                    .block();
        } catch (Exception e) {
            throw new BusinessException("Failed to retrieve mock balance", e);
        }
    }
}
