package dev.bruno.banking.service;

import dev.bruno.banking.exception.BusinessException;
import dev.bruno.banking.model.Transaction;
import dev.bruno.banking.model.User;
import dev.bruno.banking.repository.TransactionRepository;
import dev.bruno.banking.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionImportServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionImportService transactionImportService;

    @Test
    void testImportTransactions_UserNotFound() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Amount");
            header.createCell(1).setCellValue("Description");
            header.createCell(2).setCellValue("Type");
            header.createCell(3).setCellValue("Date");
            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue(100.0);
            row.createCell(1).setCellValue("Test Transaction");
            row.createCell(2).setCellValue("deposit");
            row.createCell(3).setCellValue("2025-02-16 10:00");
            workbook.write(bos);
        }
        ByteArrayInputStream excelStream = new ByteArrayInputStream(bos.toByteArray());

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> transactionImportService.importTransactions(excelStream, 99L));
        assertTrue(ex.getMessage().contains("User not found with ID: 99"));
    }

    @Test
    void testImportTransactions_Success() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Amount");
            header.createCell(1).setCellValue("Description");
            header.createCell(2).setCellValue("Type");
            header.createCell(3).setCellValue("Date");
            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue(100.0);
            row.createCell(1).setCellValue("Test Transaction");
            row.createCell(2).setCellValue("deposit");
            row.createCell(3).setCellValue("2025-02-16 10:00");
            workbook.write(bos);
        }
        ByteArrayInputStream excelStream = new ByteArrayInputStream(bos.toByteArray());

        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(transactionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = transactionImportService.importTransactions(excelStream, 1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
