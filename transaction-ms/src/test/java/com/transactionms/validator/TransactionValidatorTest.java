package com.transactionms.validator;

import com.transactionms.exceptions.InvalidTransactionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class TransactionValidatorTest {

    private final TransactionValidator validator = new TransactionValidator();
    private static final String ACC_VALID = "ACC-12345";
    private static final String ACC_OTHER = "ACC-98765";

    // =================================================================
    // TESTS PARA validateAmount
    // =================================================================

    @ParameterizedTest(name = "Monto válido: {0}")
    @ValueSource(doubles = {0.01, 100.0, 10000.55})
    void givenPositiveAmount_whenValidateAmount_thenNoException(Double amount) {
        assertDoesNotThrow(() -> validator.validateAmount(amount));
    }

    @ParameterizedTest(name = "Monto inválido (cero o negativo): {0}")
    @ValueSource(doubles = {0.0, -10.0, -0.0001})
    void givenZeroOrNegativeAmount_whenValidateAmount_thenThrowsException(Double amount) {
        InvalidTransactionException e = assertThrows(
                InvalidTransactionException.class,
                () -> validator.validateAmount(amount));
        assertEquals("El monto debe ser mayor a 0", e.getMessage());
    }

    @Test
    void givenNullAmount_whenValidateAmount_thenThrowsException() {
        InvalidTransactionException e = assertThrows(
                InvalidTransactionException.class,
                () -> validator.validateAmount(null));
        assertEquals("El monto no puede ser nulo", e.getMessage());
    }

    // =================================================================
    // TESTS PARA validateAccountNumber
    // =================================================================

    @Test
    void givenValidAccountNumber_whenValidateAccountNumber_thenNoException() {
        assertDoesNotThrow(() -> validator.validateAccountNumber(ACC_VALID));
    }

    @ParameterizedTest(name = "Cuenta inválida: \"{0}\"")
    @ValueSource(strings = {"", " "})
    void givenEmptyOrBlankAccountNumber_whenValidateAccountNumber_thenThrowsException(String account) {
        InvalidTransactionException e = assertThrows(
                InvalidTransactionException.class,
                () -> validator.validateAccountNumber(account));
        assertEquals("El número de cuenta no puede estar vacío", e.getMessage());
    }

    @Test
    void givenNullAccountNumber_whenValidateAccountNumber_thenThrowsException() {
        InvalidTransactionException e = assertThrows(
                InvalidTransactionException.class,
                () -> validator.validateAccountNumber(null));
        assertEquals("El número de cuenta no puede estar vacío", e.getMessage());
    }

    // =================================================================
    // TESTS PARA validateDeposit & validateWithdraw
    // (Estos solo llaman a los métodos base, se prueban los casos combinados)
    // =================================================================

    @Test
    void givenValidData_whenValidateDeposit_thenNoException() {
        assertDoesNotThrow(() -> validator.validateDeposit(ACC_VALID, 10.0));
    }

    @Test
    void givenInvalidData_whenValidateWithdraw_thenThrowsAmountException() {
        assertThrows(InvalidTransactionException.class, () -> validator.validateWithdraw(ACC_VALID, 0.0));
    }

    // =================================================================
    // TESTS PARA validateTransfer
    // =================================================================

    @Test
    void givenValidAccountsAndAmount_whenValidateTransfer_thenNoException() {
        assertDoesNotThrow(() -> validator.validateTransfer(ACC_VALID, ACC_OTHER, 1.0));
    }

    @Test
    void givenSameOriginAndDestination_whenValidateTransfer_thenThrowsException() {
        InvalidTransactionException e = assertThrows(
                InvalidTransactionException.class,
                () -> validator.validateTransfer(ACC_VALID, ACC_VALID, 10.0));
        assertEquals("La cuenta origen y destino no pueden ser iguales", e.getMessage());
    }

    @Test
    void givenInvalidOriginAccount_whenValidateTransfer_thenThrowsException() {
        assertThrows(InvalidTransactionException.class, () -> validator.validateTransfer(null, ACC_OTHER, 1.0));
    }
}