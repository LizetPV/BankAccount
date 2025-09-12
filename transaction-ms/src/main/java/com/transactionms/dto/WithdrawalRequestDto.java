package com.transactionms.dto;

import lombok.Data;

@Data
public class WithdrawalRequestDto {
    private String accountNumber;
    private Double amount;
}