package com.transaction_ms.dto;

import lombok.Data;

@Data
public class TransferRequestDto {
    private String originAccountNumber;
    private String destinationAccountNumber;
    private Double amount;
}