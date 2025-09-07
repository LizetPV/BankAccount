package com.transactionms.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class TransactionEntity {
    @Id
    private String id;
    private String type; // "DEPOSIT" | "WITHDRAW" | "TRANSFER"
    private Double amount;
    private Instant date;
    private String originAccountNumber;
    private String destinationAccountNumber;
    private String notes;
}
