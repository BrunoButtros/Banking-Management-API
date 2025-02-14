package dev.bruno.banking.service;

import dev.bruno.banking.exception.BusinessException;
import dev.bruno.banking.model.Transaction;
import dev.bruno.banking.model.TransactionType;
import dev.bruno.banking.model.User;
import dev.bruno.banking.repository.TransactionRepository;
import dev.bruno.banking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionImportService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<Transaction> importTransactions(InputStream excelFile, Long userId) {
        List<Transaction> transactions = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(excelFile)) {
            Sheet sheet = workbook.getSheetAt(0);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException("User not found with ID: " + userId));

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                Cell firstCell = row.getCell(0);
                if (firstCell == null || firstCell.getCellType() == CellType.BLANK) break;

                try {
                    BigDecimal amount = readAmount(row.getCell(0));
                    String description = readDescription(row.getCell(1));
                    TransactionType type = readTransactionType(row.getCell(2));
                    LocalDateTime date = readDate(row.getCell(3));
                    if (date.isAfter(LocalDateTime.now())) {
                        throw new BusinessException("Transaction date cannot be in the future: " + date);
                    }
                    Transaction transaction = new Transaction();
                    transaction.setAmount(amount);
                    transaction.setDescription(description);
                    transaction.setType(type);
                    transaction.setDate(date);
                    transaction.setUser(user);
                    transactions.add(transaction);
                } catch (Exception e) {
                    throw new BusinessException("Error processing row " + row.getRowNum() + ": " + e.getMessage(), e);
                }
            }
            transactionRepository.saveAll(transactions);
        } catch (Exception e) {
            throw new BusinessException("Error processing the Excel file: " + e.getMessage(), e);
        }
        return transactions;
    }

    private BigDecimal readAmount(Cell amountCell) {
        if (amountCell != null) {
            if (amountCell.getCellType() == CellType.NUMERIC) {
                return BigDecimal.valueOf(amountCell.getNumericCellValue());
            } else if (amountCell.getCellType() == CellType.STRING) {
                String amountString = amountCell.getStringCellValue();
                try {
                    return new BigDecimal(amountString.replace(",", "."));
                } catch (NumberFormatException e) {
                    throw new BusinessException("Invalid value for 'Amount'.", e);
                }
            }
        }
        throw new BusinessException("Amount value not found.");
    }

    private String readDescription(Cell descriptionCell) {
        if (descriptionCell != null && descriptionCell.getCellType() == CellType.STRING) {
            String description = descriptionCell.getStringCellValue().trim();
            if (!description.isEmpty()) return description;
        }
        throw new BusinessException("Invalid value for 'Description'.");
    }

    private TransactionType readTransactionType(Cell typeCell) {
        if (typeCell != null && typeCell.getCellType() == CellType.STRING) {
            return TransactionType.fromValue(typeCell.getStringCellValue());
        }
        throw new BusinessException("Invalid value for 'Type'.");
    }

    private LocalDateTime readDate(Cell dateCell) {
        if (dateCell != null) {
            if (dateCell.getCellType() == CellType.STRING) {
                String dateString = dateCell.getStringCellValue();
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    return LocalDateTime.parse(dateString, formatter);
                } catch (Exception e) {
                    throw new BusinessException("Error parsing date (String): " + dateString, e);
                }
            } else if (dateCell.getCellType() == CellType.NUMERIC) {
                java.util.Date excelDate = dateCell.getDateCellValue();
                return excelDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            }
        }
        throw new BusinessException("Date not found for transaction.");
    }
}
