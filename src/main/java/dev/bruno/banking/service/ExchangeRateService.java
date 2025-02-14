package dev.bruno.banking.service;

import dev.bruno.banking.dto.ExchangeConversionResponseDTO;
import dev.bruno.banking.exception.ExchangeRateException;
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
            throw new ExchangeRateException("EXCHANGERATE_API_KEY environment variable not found.", null);
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
            throw new ExchangeRateException("Failed to fetch exchange rate data", e);
        }
    }

    public String convertAndFormat(String from, String to, Double amount) {
        ExchangeConversionResponseDTO dto = convertCurrency(from, to, amount);
        if (dto == null || !dto.isSuccess() || dto.getQuery() == null || dto.getInfo() == null) {
            return "Conversion failed.";
        }
        return String.format("Original amount: %.2f %s – Converted amount: %.2f %s (Rate: %.5f)",
                dto.getQuery().getAmount(),
                dto.getQuery().getFrom(),
                dto.getResult(),
                dto.getQuery().getTo(),
                dto.getInfo().getRate());
    }
}
