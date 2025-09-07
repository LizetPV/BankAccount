package com.transactionms.dto;

import com.transactionms.repository.model.Transaction;
import com.transactionms.repository.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {
    private String id;
    private TransactionType type;
    private String accountFrom;
    private String accountTo;
    private Double amount;
    private Instant date;

    // 🔹 Conversión de Entity → DTO
    public static TransactionDto fromEntity(Transaction tx) {
        return TransactionDto.builder()
                .id(tx.getId())
                .type(tx.getType())
                .accountFrom(tx.getAccountFrom())
                .accountTo(tx.getAccountTo())
                .amount(tx.getAmount())
                .date(tx.getDate())
                .build();
    }
}
