package dev.bruno.banking.service;

import dev.bruno.banking.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExcelTemplateServiceTest {

    @Mock
    private Resource transactionTemplate;

    @InjectMocks
    private ExcelTemplateService excelTemplateService;

    @Test
    void testGetTransactionTemplate_Success() throws IOException {
        byte[] mockBytes = "ExcelFileContent".getBytes();
        InputStream inputStreamMock = new java.io.ByteArrayInputStream(mockBytes);
        when(transactionTemplate.getInputStream()).thenReturn(inputStreamMock);

        byte[] result = excelTemplateService.getTransactionTemplate();

        assertNotNull(result);
        assertArrayEquals(mockBytes, result);
        verify(transactionTemplate).getInputStream();
    }

    @Test
    void testGetTransactionTemplate_ThrowsBusinessException() throws IOException {
        // Dado
        when(transactionTemplate.getInputStream()).thenThrow(new IOException("File error"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> excelTemplateService.getTransactionTemplate());
        assertTrue(ex.getMessage().contains("Failed to load Excel template"));
    }
}
