package dev.bruno.banking.testconfig;

import dev.bruno.banking.security.JwtTokenProvider;
import dev.bruno.banking.service.BalanceService;
import dev.bruno.banking.service.CustomUserDetailsService;
import dev.bruno.banking.service.ExchangeRateService;
import dev.bruno.banking.service.ExcelTemplateService;
import dev.bruno.banking.service.TransactionImportService;
import dev.bruno.banking.service.TransactionService;
import dev.bruno.banking.service.UserService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;

@TestConfiguration
public class ControllerTestConfig {

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return Mockito.mock(JwtTokenProvider.class);
    }

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
}
