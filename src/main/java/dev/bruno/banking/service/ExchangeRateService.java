package dev.bruno.banking.service;

import dev.bruno.banking.dto.ExchangeConversionResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final WebClient webClient;

    public ExchangeConversionResponseDTO convertCurrency(String from, String to, Double amount) {
        String apiKey = System.getenv("EXCHANGERATE_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("Variável de ambiente EXCHANGERATE_API_KEY não encontrada.");
        }
        String url = "https://api.exchangerate.host/convert?access_key=" + apiKey +
                "&from={from}&to={to}&amount={amount}";

        try {
            return webClient.get()
                    .uri(url, from, to, amount)
                    .retrieve()
                    .bodyToMono(ExchangeConversionResponseDTO.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao buscar dados de câmbio", e);
        }
    }
}
