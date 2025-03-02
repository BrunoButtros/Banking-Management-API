package dev.bruno.banking.controller;

import dev.bruno.banking.config.ControllerTestConfig;
import dev.bruno.banking.service.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateControllerTest {


    private MockMvc mockMvc;

    @Mock
    private ExchangeRateService exchangeRateService;

    @InjectMocks
    private ExchangeRateController exchangeRateController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(exchangeRateController).build();
    }

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
