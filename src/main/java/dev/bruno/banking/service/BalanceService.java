package dev.bruno.banking.service;

import dev.bruno.banking.dto.BalanceResponseDTO;
import dev.bruno.banking.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final WebClient webClient;

    @Value("${mock.api.balance.url}")
    private String balanceUrl;

    public List<BalanceResponseDTO> getBalances(String userEmail) {
        try {
            return webClient.get()
                    .uri(balanceUrl)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<BalanceResponseDTO>>() {})
                    .block();
        } catch (Exception e) {
            throw new BusinessException("Failed to retrieve balance data for user: " + userEmail, e);
        }
    }
}
