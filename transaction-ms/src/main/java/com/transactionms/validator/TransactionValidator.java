package com.transactionms.validator;

import com.transactionms.exceptions.InvalidTransactionException;
import org.springframework.stereotype.Component;

/**
 * Validador centralizado para operaciones de transacciones.
 * Cumple con Single Responsibility Principle (SRP) - solo se encarga de validaciones.
 */
@Component
public class TransactionValidator {

    /**
     * Valida el monto para cualquier operación de transacción.
     * @param amount monto a validar
     * @throws InvalidTransactionException si el monto es inválido
     */
    public void validateAmount(Double amount) {
        if (amount == null) {
            throw new InvalidTransactionException("El monto no puede ser nulo");
        }
        if (amount <= 0) {
            throw new InvalidTransactionException("El monto debe ser mayor a 0");
        }
    }

    /**
     * Valida que el número de cuenta no sea nulo o vacío.
     * @param accountNumber número de cuenta a validar
     * @throws InvalidTransactionException si el número de cuenta es inválido
     */
    public void validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new InvalidTransactionException("El número de cuenta no puede estar vacío");
        }
    }

    /**
     * Valida los parámetros específicos para un depósito.
     * @param accountNumber cuenta destino
     * @param amount monto del depósito
     */
    public void validateDeposit(String accountNumber, Double amount) {
        validateAccountNumber(accountNumber);
        validateAmount(amount);
    }

    /**
     * Valida los parámetros específicos para un retiro.
     * @param accountNumber cuenta origen
     * @param amount monto del retiro
     */
    public void validateWithdraw(String accountNumber, Double amount) {
        validateAccountNumber(accountNumber);
        validateAmount(amount);
    }

    /**
     * Valida los parámetros específicos para una transferencia.
     * @param originAccountNumber cuenta origen
     * @param destinationAccountNumber cuenta destino
     * @param amount monto de la transferencia
     */
    public void validateTransfer(String originAccountNumber, String destinationAccountNumber, Double amount) {
        validateAccountNumber(originAccountNumber);
        validateAccountNumber(destinationAccountNumber);
        validateAmount(amount);
        
        if (originAccountNumber.equals(destinationAccountNumber)) {
            throw new InvalidTransactionException("La cuenta origen y destino no pueden ser iguales");
        }
    }
}