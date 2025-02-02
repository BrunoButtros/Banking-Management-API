package dev.bruno.banking.service;

import dev.bruno.banking.dto.ExchangeConversionResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExchangeRateServiceTest {

    @Test
    void testConvertCurrency() {
        WebClient webClient = WebClient.create();
        ExchangeRateService exchangeRateService = new ExchangeRateService(webClient);

        ExchangeConversionResponseDTO response = exchangeRateService.convertCurrency("USD", "EUR", 100.0);

        assertNotNull(response);
        assertNotNull(response.getQuery());
        assertNotNull(response.getResult());

        System.out.println("Conversion Result: " + response.getResult());
    }
}
