package dev.bruno.banking.service;

import dev.bruno.banking.dto.BalanceResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final WebClient webClient;

    public BalanceResponseDTO getBalance() {
        String url = "https://run.mocky.io/v3/d0614c1b-7de4-493b-a0be-222e8fdf55b7";

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(BalanceResponseDTO.class)
                .block(); // Transforma a resposta assíncrona em síncrona
    }
}
