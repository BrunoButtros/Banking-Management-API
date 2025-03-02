package dev.bruno.banking.controller;

import dev.bruno.banking.dto.BalanceResponseDTO;
import dev.bruno.banking.service.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BalanceControllerUnitTest {

    private MockMvc mockMvc;

    @InjectMocks
    private BalanceController balanceController;

    @Mock
    private BalanceService balanceService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(balanceController).build();
    }

    @Test
    @WithMockUser(username = "teste@exemplo.com")
    public void testGetBalances() throws Exception {
        String userEmail = "teste@exemplo.com";
        BalanceResponseDTO dto = new BalanceResponseDTO();
        List<BalanceResponseDTO> responseList = List.of(dto);

        when(balanceService.getBalances(userEmail)).thenReturn(responseList);

        mockMvc.perform(get("/balance").param("userEmail", userEmail))
                .andExpect(status().isOk());
    }
}