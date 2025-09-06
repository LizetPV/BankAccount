package com.transaction_ms.dto;

import lombok.Data;

@Data
public class DepositRequestDto {
    private String accountNumber;
    private Double amount;
}
