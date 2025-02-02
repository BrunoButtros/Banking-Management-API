package dev.bruno.banking.controller;

import dev.bruno.banking.dto.BalanceResponseDTO;
import dev.bruno.banking.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping("/api/balance")
    public List<BalanceResponseDTO> getBalances(@RequestParam String userEmail) {
        return balanceService.getBalances(userEmail);
    }
}
