package dev.bruno.banking.config;

import dev.bruno.banking.security.JwtTokenProvider;
import dev.bruno.banking.service.*;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;

@TestConfiguration
public class ControllerTestConfig {



    @Bean
    public CustomUserDetailsService customUserDetailsService() {
        return Mockito.mock(CustomUserDetailsService.class);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return Mockito.mock(AuthenticationManager.class);
    }

    @Bean
    public TransactionService transactionService() {
        return Mockito.mock(TransactionService.class);
    }

    @Bean
    public TransactionImportService transactionImportService() {
        return Mockito.mock(TransactionImportService.class);
    }

    @Bean
    public ExcelTemplateService excelTemplateService() {
        return Mockito.mock(ExcelTemplateService.class);
    }

    @Bean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }

    @Bean
    public BalanceService balanceService() {
        return Mockito.mock(BalanceService.class);
    }

    @Bean
    public ExchangeRateService exchangeRateService() {
        return Mockito.mock(ExchangeRateService.class);
    }

    @Bean
    public ReportService reportService() {
        return Mockito.mock(ReportService.class);
    }
}
