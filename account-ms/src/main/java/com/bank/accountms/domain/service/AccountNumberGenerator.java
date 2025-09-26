package com.bank.accountms.domain.service;

/** Contrato para generar números de cuenta (DIP/SRP). */
public interface AccountNumberGenerator {
    String next();
}
