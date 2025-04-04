package dev.bruno.banking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.bruno.banking.config.CustomUserDetails;
import dev.bruno.banking.dto.TransactionSummaryRequestDTO;
import dev.bruno.banking.model.User;
import dev.bruno.banking.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ReportControllerTest {

    private static final String FORMAT_PDF = "PDF";
    private static final String FORMAT_XLS = "XLS";

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(reportController)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();

        setAuthenticatedUser();
    }

    private void setAuthenticatedUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("password123");

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private TransactionSummaryRequestDTO createRequestDTO() {
        TransactionSummaryRequestDTO requestDTO = new TransactionSummaryRequestDTO();
        requestDTO.setStartDate(LocalDateTime.now().minusDays(1));
        requestDTO.setEndDate(LocalDateTime.now());
        requestDTO.setType("deposit");
        requestDTO.setPage(0);
        return requestDTO;
    }

    @Test
    public void testGenerateReport_PDF() throws Exception {
        TransactionSummaryRequestDTO requestDTO = createRequestDTO();
        byte[] expectedReport = new byte[0];

        when(reportService.generateReport(any(TransactionSummaryRequestDTO.class), any(UserDetails.class), eq(FORMAT_PDF)))
                .thenReturn(expectedReport);

        mockMvc.perform(post("/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("format", FORMAT_PDF)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=transaction-summary.pdf"));
    }

    @Test
    public void testGenerateReport_NonPdfFormat() throws Exception {
        TransactionSummaryRequestDTO requestDTO = createRequestDTO();
        byte[] expectedReport = new byte[0];

        when(reportService.generateReport(any(TransactionSummaryRequestDTO.class), any(UserDetails.class), eq(FORMAT_XLS)))
                .thenReturn(expectedReport);

        mockMvc.perform(post("/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("format", FORMAT_XLS)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=transaction-summary.xlsx"));
    }
}
