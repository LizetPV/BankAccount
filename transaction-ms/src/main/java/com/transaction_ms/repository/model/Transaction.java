package com.transaction_ms.repository.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id; // ObjectId o transactionId
    private TransactionType type;
    private Double amount;
    private String accountFrom; // nullable for deposit
    private String accountTo;   // nullable for withdraw
    private Instant date;
    private String notes;
}
