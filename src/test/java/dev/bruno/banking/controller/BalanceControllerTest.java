package dev.bruno.banking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.bruno.banking.dto.BalanceResponseDTO;
import dev.bruno.banking.testconfig.ControllerTestConfig;
import dev.bruno.banking.service.BalanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BalanceController.class)
@Import(ControllerTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class BalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BalanceService balanceService;

    @Test
    void testGetBalances() throws Exception {
        BalanceResponseDTO dto = new BalanceResponseDTO();
        dto.setBalance(100.0);
        dto.setCurrency("USD");
        when(balanceService.getBalances(anyString())).thenReturn(List.of(dto));

        mockMvc.perform(get("/balance")
                        .param("userEmail", "test@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].currency").value("USD"));
    }
}
