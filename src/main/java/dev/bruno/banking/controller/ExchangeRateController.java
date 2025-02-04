package dev.bruno.banking.controller;

import dev.bruno.banking.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping("/convert")
    public String convertCurrency(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam Double amount) {
        return exchangeRateService.convertAndFormat(from, to, amount);
    }
}
