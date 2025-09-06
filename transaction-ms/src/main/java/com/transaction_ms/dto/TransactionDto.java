package com.transaction_ms.dto;

import com.transaction_ms.repository.model.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TransactionDto {
    private String id;
    private TransactionType type;
    private Double amount;
    private Instant date;
    private String originAccountNumber;
    private String destinationAccountNumber;
}
