package com.fixedtransaction.app.models.documents;

import com.fixedtransaction.app.models.dto.FixedTerm;
import com.fixedtransaction.app.models.dto.TypeTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
@Data
@Builder
@Document("TransactionFixedTerm")
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    private String id;
    @NotNull
    private FixedTerm fixedTerm;
    @NotNull
    private String transactionCode;
    @NotNull
    private TypeTransaction typeTransaction;
    @NotNull
    private Double transactionAmount;
    private Double commissionAmount;
    private LocalDateTime transactionDate;
}
