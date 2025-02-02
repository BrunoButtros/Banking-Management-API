package dev.bruno.banking.controller;

import dev.bruno.banking.dto.ExchangeConversionResponseDTO;
import dev.bruno.banking.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping("/api/convert")
    public ExchangeConversionResponseDTO convertCurrency(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam Double amount) {
        return exchangeRateService.convertCurrency(from, to, amount);
    }
}
