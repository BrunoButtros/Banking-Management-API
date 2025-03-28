package dev.bruno.banking.service;

import dev.bruno.banking.exception.BusinessException;
import dev.bruno.banking.model.Transaction;
import dev.bruno.banking.model.TransactionType;
import dev.bruno.banking.model.User;
import dev.bruno.banking.repository.TransactionRepository;
import dev.bruno.banking.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionImportServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionImportService transactionImportService;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setId(1L);
        validUser.setName("Test User");
    }

    private <T> T invokePrivate(String methodName, Class<?>[] paramTypes, Object... args) throws Exception {
        Method method = TransactionImportService.class.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        try {
            return (T) method.invoke(transactionImportService, args);
        } catch (InvocationTargetException e) {
            throw (Exception) e.getTargetException();
        }
    }

    private ByteArrayInputStream createExcelFile(Object amount, Object description, Object type, Object date) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Amount");
            header.createCell(1).setCellValue("Description");
            header.createCell(2).setCellValue("Type");
            header.createCell(3).setCellValue("Date");

            Row row = sheet.createRow(1);
            // Amount
            if (amount != null) {
                if (amount instanceof Number) {
                    row.createCell(0).setCellValue(((Number) amount).doubleValue());
                } else {
                    row.createCell(0).setCellValue(amount.toString());
                }
            } else {
                row.createCell(0, CellType.BLANK);
            }
            // Description
            if (description != null) {
                row.createCell(1).setCellValue(description.toString());
            } else {
                row.createCell(1, CellType.BLANK);
            }
            // Type
            if (type != null) {
                row.createCell(2).setCellValue(type.toString());
            } else {
                row.createCell(2, CellType.BLANK);
            }
            // Date
            if (date != null) {
                if (date instanceof Number) {
                    row.createCell(3).setCellValue(((Number) date).doubleValue());
                } else {
                    row.createCell(3).setCellValue(date.toString());
                }
            } else {
                row.createCell(3, CellType.BLANK);
            }
            workbook.write(bos);
        }
        return new ByteArrayInputStream(bos.toByteArray());
    }


    @Test
    void testImportTransactions_UserNotFound() throws Exception {
        ByteArrayInputStream excel = createExcelFile(100.0, "Valid Desc", "deposit", "2025-02-16 10:00");
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () ->
                transactionImportService.importTransactions(excel, 99L)
        );
        assertTrue(ex.getMessage().contains("User not found with ID: 99"));
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void testImportTransactions_Success() throws Exception {
        ByteArrayInputStream excel = createExcelFile(100.0, "Test Transaction", "deposit", "2025-02-16 10:00");
        when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));
        when(transactionRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        List<Transaction> result = transactionImportService.importTransactions(excel, 1L);
        assertNotNull(result);
        assertEquals(1, result.size());
        Transaction t = result.get(0);
        assertEquals(new BigDecimal("100.0"), t.getAmount());
        assertEquals("Test Transaction", t.getDescription());
        assertEquals(TransactionType.DEPOSIT, t.getType());
    }

    @Test
    void testImportTransactions_DateInFuture() throws Exception {
        String futureDate = LocalDateTime.now().plusDays(5)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        ByteArrayInputStream excel = createExcelFile(100.0, "Desc", "deposit", futureDate);
        when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                transactionImportService.importTransactions(excel, 1L)
        );
        assertTrue(ex.getMessage().contains("Transaction date cannot be in the future"));
    }

    @Test
    void testImportTransactions_RowProcessingError() throws Exception {
        ByteArrayInputStream excel = createExcelFile("ABC", "Desc", "deposit", "2025-02-16 10:00");
        when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                transactionImportService.importTransactions(excel, 1L)
        );
        assertTrue(ex.getMessage().contains("Error processing row 1:"));
    }

    @Test
    void testImportTransactions_ErrorProcessingFile() {
        ByteArrayInputStream invalidStream = new ByteArrayInputStream(new byte[]{0, 1, 2});
        BusinessException ex = assertThrows(BusinessException.class, () ->
                transactionImportService.importTransactions(invalidStream, 1L)
        );
        assertTrue(ex.getMessage().contains("Error processing the Excel file"));
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void testReadAmount_Numeric() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Cell cell = workbook.createSheet().createRow(0).createCell(0);
        cell.setCellValue(150.5);
        BigDecimal result = invokePrivate("readAmount", new Class<?>[]{Cell.class}, cell);
        assertEquals(new BigDecimal("150.5"), result);
        workbook.close();
    }

    @Test
    void testReadAmount_StringValid() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Cell cell = workbook.createSheet().createRow(0).createCell(0);
        cell.setCellValue("200,75");
        BigDecimal result = invokePrivate("readAmount", new Class<?>[]{Cell.class}, cell);
        assertEquals(new BigDecimal("200.75"), result);
        workbook.close();
    }

    @Test
    void testReadAmount_StringInvalid() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Cell cell = workbook.createSheet().createRow(0).createCell(0);
        cell.setCellValue("XYZ");
        BusinessException ex = assertThrows(BusinessException.class, () ->
                invokePrivate("readAmount", new Class<?>[]{Cell.class}, cell)
        );
        assertTrue(ex.getMessage().contains("Invalid value for 'Amount'"));
        workbook.close();
    }

    @Test
    void testReadAmount_NullCell() {
        BusinessException ex = assertThrows(BusinessException.class, () ->
                invokePrivate("readAmount", new Class<?>[]{Cell.class}, new Object[]{null})
        );
        assertTrue(ex.getMessage().contains("Amount value not found"));
    }


    @Test
    void testReadDescription_Valid() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Cell cell = workbook.createSheet().createRow(0).createCell(0);
        cell.setCellValue("  Valid Description  ");
        String result = invokePrivate("readDescription", new Class<?>[]{Cell.class}, cell);
        assertEquals("Valid Description", result);
        workbook.close();
    }

    @Test
    void testReadDescription_Blank() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Cell cell = workbook.createSheet().createRow(0).createCell(0);
        cell.setCellValue("   ");
        BusinessException ex = assertThrows(BusinessException.class, () ->
                invokePrivate("readDescription", new Class<?>[]{Cell.class}, cell)
        );
        assertTrue(ex.getMessage().contains("Invalid value for 'Description'"));
        workbook.close();
    }

    @Test
    void testReadTransactionType_Valid() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Cell cell = workbook.createSheet().createRow(0).createCell(0);
        cell.setCellValue("deposit");
        TransactionType result = invokePrivate("readTransactionType", new Class<?>[]{Cell.class}, cell);
        assertEquals(TransactionType.DEPOSIT, result);
        workbook.close();
    }

    @Test
    void testReadTransactionType_Invalid() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Cell cell = workbook.createSheet().createRow(0).createCell(0);
        cell.setCellValue("unknown");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                invokePrivate("readTransactionType", new Class<?>[]{Cell.class}, cell)
        );
        assertTrue(ex.getMessage().contains("Invalid transaction type"));
        workbook.close();
    }

    @Test
    void testReadTransactionType_NullCell() {
        BusinessException ex = assertThrows(BusinessException.class, () ->
                invokePrivate("readTransactionType", new Class<?>[]{Cell.class}, new Object[]{null})
        );
        assertTrue(ex.getMessage().contains("Invalid value for 'Type'"));
    }

    @Test
    void testReadDate_StringValid() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Cell cell = workbook.createSheet().createRow(0).createCell(0);
        cell.setCellValue("2024-03-15 14:30");
        LocalDateTime result = invokePrivate("readDate", new Class<?>[]{Cell.class}, cell);
        assertEquals(LocalDateTime.of(2024, 3, 15, 14, 30), result);
        workbook.close();
    }

    @Test
    void testReadDate_StringInvalid() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Cell cell = workbook.createSheet().createRow(0).createCell(0);
        cell.setCellValue("bad-date");
        BusinessException ex = assertThrows(BusinessException.class, () ->
                invokePrivate("readDate", new Class<?>[]{Cell.class}, cell)
        );
        assertTrue(ex.getMessage().contains("Error parsing date (String)"));
        workbook.close();
    }

    @Test
    void testReadDate_Numeric() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(2023, java.util.Calendar.JANUARY, 1, 0, 0, 0);
        cell.setCellValue(cal.getTime());
        LocalDateTime result = invokePrivate("readDate", new Class<?>[]{Cell.class}, cell);
        assertEquals(LocalDateTime.of(2023, 1, 1, 0, 0), result.truncatedTo(ChronoUnit.SECONDS));
        workbook.close();
    }

    @Test
    void testReadDate_NullCell() {
        BusinessException ex = assertThrows(BusinessException.class, () ->
                invokePrivate("readDate", new Class<?>[]{Cell.class}, new Object[]{null})
        );
        assertTrue(ex.getMessage().contains("Date not found for transaction"));
    }
}
