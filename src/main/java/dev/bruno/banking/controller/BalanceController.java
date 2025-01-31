package dev.bruno.banking.controller;

import dev.bruno.banking.dto.BalanceResponseDTO;
import dev.bruno.banking.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping("/api/balance")
    public BalanceResponseDTO getBalance() {
        return balanceService.getBalance();
    }
}
