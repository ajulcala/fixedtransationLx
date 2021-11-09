package com.fixedtransaction.app.controllers;

import com.fixedtransaction.app.models.documents.Transaction;
import com.fixedtransaction.app.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@RefreshScope
@RestController
@RequestMapping("/transactionFixedTerm")
public class TransactionController {
    @Autowired
    TransactionService service;

    @GetMapping("list")
    public Flux<Transaction> findAll(){
        return service.findAll();
    }

    @GetMapping("/find/{id}")
    public Mono<Transaction> findById(@PathVariable String id){
        return service.findById(id);
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<Transaction>> create(@RequestBody Transaction transactionFixedTerm){

        return service.findFixedTermById(transactionFixedTerm.getFixedTerm().getId())
                .flatMap(fixedTerm -> service.countTransactions(transactionFixedTerm.getFixedTerm().getId(), transactionFixedTerm.getTypeTransaction())
                        .filter(count -> {
                            Integer limit = 0;
                            switch (transactionFixedTerm.getTypeTransaction()){
                                case DEPOSIT: limit = fixedTerm.getLimitDeposits(); break;
                                case DRAFT: limit = fixedTerm.getLimitDraft(); break;
                            }
                            return count < limit && fixedTerm.getAllowDateTransaction().equals(LocalDate.now());
                        })
                        .flatMap(count -> {
                            if(fixedTerm.getFreeTransactions() > count){
                                transactionFixedTerm.setCommissionAmount(0.0);
                                fixedTerm.setBalance(fixedTerm.getBalance() + transactionFixedTerm.getTransactionAmount());
                            }else{
                                transactionFixedTerm.setCommissionAmount(fixedTerm.getCommissionTransactions());
                                fixedTerm.setBalance(fixedTerm.getBalance() + transactionFixedTerm.getTransactionAmount() - fixedTerm.getCommissionTransactions());
                            }
                            return service.updateFixedTerm(fixedTerm)
                                    .flatMap(ftUpdate -> {
                                        transactionFixedTerm.setFixedTerm(ftUpdate);
                                        transactionFixedTerm.setTransactionDate(LocalDateTime.now());
                                        return service.create(transactionFixedTerm);
                                    });
                        })
                )
                .map(sat ->new ResponseEntity<>(sat , HttpStatus.CREATED) )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

    }

    @PutMapping("/update")
    public Mono<ResponseEntity<Transaction>> update(@RequestBody Transaction transactionFixedTerm) {

        return service.findFixedTermById(transactionFixedTerm.getFixedTerm().getId())
                .flatMap(sa ->{
                    return service.findById(transactionFixedTerm.getId())
                            .flatMap(sat ->{
                                switch (transactionFixedTerm.getTypeTransaction()) {
                                    case DEPOSIT: sa.setBalance(sa.getBalance() - sat.getTransactionAmount() );
                                        return service.updateFixedTerm(sa).flatMap(saUpdate -> {
                                            transactionFixedTerm.setFixedTerm(saUpdate);
                                            transactionFixedTerm.setTransactionDate(LocalDateTime.now());
                                            return service.update(transactionFixedTerm);
                                        });

                                    case DRAFT: sa.setBalance(sa.getBalance() + sat.getTransactionAmount() - transactionFixedTerm.getTransactionAmount());
                                        return service.updateFixedTerm(sa).flatMap(saUpdate ->{
                                            transactionFixedTerm.setFixedTerm(saUpdate);
                                            transactionFixedTerm.setTransactionDate(LocalDateTime.now());
                                            return service.update(transactionFixedTerm);
                                        });
                                    default: return Mono.empty();
                                }
                            });
                })
                .map(sat -> new ResponseEntity<>(sat, HttpStatus.CREATED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<String>> delete(@PathVariable String id) {
        return service.delete(id)
                .filter(deleteTransactionFixedTerm -> deleteTransactionFixedTerm)
                .map(deleteCustomer -> new ResponseEntity<>("Transaction Deleted", HttpStatus.ACCEPTED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
