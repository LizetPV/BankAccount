package com.transactionms.dto;

import lombok.Data;

@Data
public class DepositRequestDto {
    private String accountNumber;
    private Double amount;
}
