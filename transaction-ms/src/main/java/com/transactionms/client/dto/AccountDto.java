package com.transactionms.client.dto;

import lombok.Data;

@Data
public class AccountDto {
    private Long id;
    private String accountNumber;
    private Double balance;
    private String accountType; // "SAVINGS" | "CHECKING"
    private Long customerId;
}
