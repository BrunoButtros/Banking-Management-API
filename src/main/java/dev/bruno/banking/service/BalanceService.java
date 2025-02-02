package dev.bruno.banking.service;

import dev.bruno.banking.dto.BalanceResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final WebClient webClient;

    public List<BalanceResponseDTO> getBalances(String userEmail) {
        String url = "https://run.mocky.io/v3/86410a28-e48b-4f12-91e3-043402dee555";

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<BalanceResponseDTO>>() {})
                .block();
    }
}
