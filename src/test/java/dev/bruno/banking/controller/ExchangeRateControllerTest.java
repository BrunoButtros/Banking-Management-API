package dev.bruno.banking.controller;

import dev.bruno.banking.testconfig.ControllerTestConfig;
import dev.bruno.banking.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExchangeRateController.class)
@Import(ControllerTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Test
    void testConvertCurrency_Success() throws Exception {
        when(exchangeRateService.convertAndFormat(anyString(), anyString(), anyDouble()))
                .thenReturn("Converted: 100.00 BRL");

        mockMvc.perform(get("/convert")
                        .param("from", "USD")
                        .param("to", "BRL")
                        .param("amount", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Converted: 100.00 BRL")));
    }
}
