package dev.bruno.banking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.bruno.banking.dto.TransactionRequestDTO;
import dev.bruno.banking.dto.TransactionResponseDTO;
import dev.bruno.banking.dto.TransactionSummaryDTO;
import dev.bruno.banking.dto.TransactionSummaryRequestDTO;
import dev.bruno.banking.model.TransactionType;
import dev.bruno.banking.testconfig.ControllerTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@Import(ControllerTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private dev.bruno.banking.service.TransactionService transactionService;

    @Autowired
    private dev.bruno.banking.service.TransactionImportService transactionImportService;

    @Autowired
    private dev.bruno.banking.service.ExcelTemplateService excelTemplateService;

    @Test
    @WithMockUser
    void testCreateTransaction() throws Exception {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAmount(BigDecimal.valueOf(100));
        request.setDescription("Test transaction");
        request.setType(TransactionType.DEPOSIT);
        request.setDate(LocalDateTime.now().minusHours(1));

        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setId(1L);
        response.setAmount(BigDecimal.valueOf(100));
        response.setDescription("Test transaction");
        response.setType(TransactionType.DEPOSIT);
        response.setDate(request.getDate());
        response.setUserName("Test User");

        when(transactionService.createTransaction(any(TransactionRequestDTO.class), any()))
                .thenReturn(response);

        mockMvc.perform(post("/transactions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userName").value("Test User"));
    }

    @Test
    @WithMockUser
    void testFindTransactionById() throws Exception {
        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setId(1L);
        response.setAmount(BigDecimal.valueOf(100));
        response.setDescription("Test transaction");
        response.setType(TransactionType.DEPOSIT);
        response.setDate(LocalDateTime.now().minusHours(1));
        response.setUserName("Test User");

        when(transactionService.findById(anyLong(), any())).thenReturn(response);

        mockMvc.perform(get("/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void testGetTransactionSummary() throws Exception {
        TransactionSummaryDTO summary = new TransactionSummaryDTO(TransactionType.DEPOSIT, BigDecimal.valueOf(100), 1);
        when(transactionService.getTransactionSummary(any(TransactionSummaryRequestDTO.class), any()))
                .thenReturn(new PageImpl<>(List.of(summary), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/transactions/summary")
                        .param("startDate", "2025-02-18T00:00:00")
                        .param("endDate", "2025-02-19T00:00:00")
                        .param("type", "deposit")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("deposit"));
    }

    @Test
    @WithMockUser
    void testUpdateTransaction() throws Exception {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAmount(BigDecimal.valueOf(200));
        request.setDescription("Updated transaction");
        request.setType(TransactionType.DEPOSIT);
        request.setDate(LocalDateTime.now().minusMinutes(30));

        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setId(1L);
        response.setAmount(BigDecimal.valueOf(200));
        response.setDescription("Updated transaction");
        response.setType(TransactionType.DEPOSIT);
        response.setDate(request.getDate());
        response.setUserName("Test User");

        when(transactionService.updateTransaction(eq(1L), any(TransactionRequestDTO.class), any()))
                .thenReturn(response);

        mockMvc.perform(put("/transactions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(200));
    }

    @Test
    @WithMockUser
    void testDeleteTransaction() throws Exception {
        doNothing().when(transactionService).deleteTransaction(eq(1L), any());

        mockMvc.perform(delete("/transactions/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void testDownloadTransactionTemplate() throws Exception {
        byte[] templateBytes = "fakeExcelContent".getBytes();
        when(excelTemplateService.getTransactionTemplate()).thenReturn(templateBytes);

        mockMvc.perform(get("/transactions/download-template"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("attachment; filename=Transaction_Template.xlsx")))
                .andExpect(content().bytes(templateBytes));
    }

    @Test
    @WithMockUser
    void testImportTransactions() throws Exception {
        when(transactionImportService.importTransactions(any(), eq(1L)))
                .thenReturn(List.of(new dev.bruno.banking.model.Transaction(), new dev.bruno.banking.model.Transaction()));

        org.springframework.mock.web.MockMultipartFile file =
                new org.springframework.mock.web.MockMultipartFile("file", "test.xlsx",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "fake content".getBytes());

        mockMvc.perform(multipart("/transactions/import")
                        .file(file)
                        .param("userId", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Total transactions imported: 2")));
    }
}
