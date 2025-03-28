package dev.bruno.banking.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransactionTypeTest {

    private String depositValue;
    private String withdrawValue;
    private String pixValue;
    private String transferValue;

    @BeforeEach
    void setUp() {
        depositValue = "deposit";
        withdrawValue = "withdraw";
        pixValue = "pix";
        transferValue = "transfer";
    }

    @Test
    void testGetValue() {
        assertEquals(depositValue, TransactionType.DEPOSIT.getValue());
        assertEquals(withdrawValue, TransactionType.WITHDRAW.getValue());
        assertEquals(pixValue, TransactionType.PIX.getValue());
        assertEquals(transferValue, TransactionType.TRANSFER.getValue());
    }

    @Test
    void testToString() {
        assertEquals(depositValue, TransactionType.DEPOSIT.toString());
        assertEquals(withdrawValue, TransactionType.WITHDRAW.toString());
        assertEquals(pixValue, TransactionType.PIX.toString());
        assertEquals(transferValue, TransactionType.TRANSFER.toString());
    }

    @Test
    void testFromValue_ValidValues() {
        assertEquals(TransactionType.DEPOSIT, TransactionType.fromValue(depositValue));
        assertEquals(TransactionType.WITHDRAW, TransactionType.fromValue(withdrawValue));
        assertEquals(TransactionType.PIX, TransactionType.fromValue(pixValue));
        assertEquals(TransactionType.TRANSFER, TransactionType.fromValue(transferValue));
    }

    @Test
    void testFromValue_ValidValuesIgnoreCase() {
        assertEquals(TransactionType.DEPOSIT, TransactionType.fromValue("DEPOSIT"));
        assertEquals(TransactionType.WITHDRAW, TransactionType.fromValue("WiThDrAw"));
        assertEquals(TransactionType.PIX, TransactionType.fromValue("Pix"));
        assertEquals(TransactionType.TRANSFER, TransactionType.fromValue("TRANSFER"));
    }

    @Test
    void testFromValue_NullValue() {
        assertNull(TransactionType.fromValue(null));
    }

    @Test
    void testFromValue_InvalidValue() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> TransactionType.fromValue("invalid-type")
        );
        assertEquals("Invalid transaction type: invalid-type", exception.getMessage());
    }
}
