package com.fixedtransaction.app.models.dao;

import com.fixedtransaction.app.models.documents.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TransactionDao extends ReactiveMongoRepository<Transaction, String> {
    Flux<Transaction> findByFixedTermId(String id);
}
