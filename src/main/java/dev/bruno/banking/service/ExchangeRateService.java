package dev.bruno.banking.service;

import dev.bruno.banking.dto.ExchangeConversionResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final WebClient webClient;

    private static final String API_URL = "https://api.exchangerate.host/convert";
    public ExchangeConversionResponseDTO convertCurrency(String from, String to, Double amount) {
        String apiKey = System.getenv("EXCHANGERATE_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("Variável de ambiente EXCHANGERATE_API_KEY não encontrada.");
        }

        String url = String.format("%s?access_key=%s&from=%s&to=%s&amount=%s",
                API_URL, apiKey, from, to, amount);

        try {
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(ExchangeConversionResponseDTO.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao buscar dados de câmbio", e);
        }
    }

    public String convertAndFormat(String from, String to, Double amount) {
        ExchangeConversionResponseDTO dto = convertCurrency(from, to, amount);
        if (dto == null || !dto.isSuccess() || dto.getQuery() == null || dto.getInfo() == null) {
            return "Falha na conversão.";
        }
        return String.format("Valor original: %.2f %s – Valor convertido: %.2f %s (Taxa: %.5f)",
                dto.getQuery().getAmount(),
                dto.getQuery().getFrom(),
                dto.getResult(),
                dto.getQuery().getTo(),
                dto.getInfo().getRate());
    }
}
