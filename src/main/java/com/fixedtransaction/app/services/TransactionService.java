package com.fixedtransaction.app.services;

import com.fixedtransaction.app.models.documents.Transaction;
import com.fixedtransaction.app.models.dto.FixedTerm;
import com.fixedtransaction.app.models.dto.TypeTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {
    Mono<Transaction> create(Transaction t);
    Flux<Transaction> findAll();
    Mono<Transaction> findById(String id);
    Mono<Transaction> update(Transaction t);
    Mono<Boolean> delete(String t);
    Mono<Long> countTransactions(String id, TypeTransaction typeTransaction);
    Mono<FixedTerm> findFixedTermById(String t);
    Mono<FixedTerm> updateFixedTerm(FixedTerm ft);
}
