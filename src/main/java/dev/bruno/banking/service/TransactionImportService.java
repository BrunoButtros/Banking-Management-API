package dev.bruno.banking.service;

import dev.bruno.banking.model.Transaction;
import dev.bruno.banking.model.TransactionType;
import dev.bruno.banking.model.User;
import dev.bruno.banking.repository.TransactionRepository;
import dev.bruno.banking.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionImportService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public List<Transaction> importTransactions(InputStream excelFile, Long userId) {
        List<Transaction> transactions = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(excelFile)) {
            Sheet sheet = workbook.getSheetAt(0);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com o ID: " + userId));

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // Ignora a linha de cabeçalho
                }

                // Verifica se a primeira célula da linha está vazia ou em branco
                Cell firstCell = row.getCell(0);
                if (firstCell == null || firstCell.getCellType() == CellType.BLANK) {
                    break; // Interrompe a leitura se a célula estiver vazia
                }

                try {
                    // Lê os dados das células
                    BigDecimal amount = readAmount(row.getCell(0));
                    String description = readDescription(row.getCell(1));
                    TransactionType type = readTransactionType(row.getCell(2));
                    LocalDateTime date = readDate(row.getCell(3));

                    if (date.isAfter(LocalDateTime.now())) {
                        throw new IllegalArgumentException("A data da transação não pode estar no futuro: " + date);
                    }

                    Transaction transaction = new Transaction();
                    transaction.setAmount(amount);
                    transaction.setDescription(description);
                    transaction.setType(type);
                    transaction.setDate(date);
                    transaction.setUser(user);

                    transactions.add(transaction);

                } catch (Exception e) {
                    throw new IllegalArgumentException("Erro ao processar a linha " + row.getRowNum() + ": " + e.getMessage());
                }
            }

            transactionRepository.saveAll(transactions);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar o arquivo Excel: " + e.getMessage());
        }

        return transactions;
    }

    private BigDecimal readAmount(Cell amountCell) {
        if (amountCell != null) {
            if (amountCell.getCellType() == CellType.NUMERIC) {
                return new BigDecimal(amountCell.getNumericCellValue());
            } else if (amountCell.getCellType() == CellType.STRING) {
                String amountString = amountCell.getStringCellValue();
                try {
                    return new BigDecimal(amountString.replace(",", "."));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Valor inválido para 'Amount'.");
                }
            }
        }
        throw new IllegalArgumentException("Valor não encontrado para 'Amount'.");
    }

    private String readDescription(Cell descriptionCell) {
        if (descriptionCell != null && descriptionCell.getCellType() == CellType.STRING) {
            String description = descriptionCell.getStringCellValue().trim();
            if (!description.isEmpty()) {
                return description;
            }
        }
        throw new IllegalArgumentException("Valor inválido para 'Description'.");
    }

    private TransactionType readTransactionType(Cell typeCell) {
        if (typeCell != null && typeCell.getCellType() == CellType.STRING) {
            return TransactionType.fromValue(typeCell.getStringCellValue());
        }
        throw new IllegalArgumentException("Valor inválido para 'Type'.");
    }

    private LocalDateTime readDate(Cell dateCell) {
        if (dateCell != null) {
            if (dateCell.getCellType() == CellType.STRING) {
                String dateString = dateCell.getStringCellValue();
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    return LocalDateTime.parse(dateString, formatter);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Erro ao parsear a data (String): " + dateString);
                }
            } else if (dateCell.getCellType() == CellType.NUMERIC) {
                java.util.Date excelDate = dateCell.getDateCellValue();
                return excelDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            }
        }
        throw new IllegalArgumentException("Data não encontrada para a transação.");
    }
}
