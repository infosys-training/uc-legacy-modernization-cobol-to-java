package com.carddemo.controller;

import com.carddemo.model.Transaction;
import com.carddemo.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for Transaction management.
 * Replaces CICS transaction codes CT00 (list), CT01 (view), CT02 (add).
 */
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public Page<Transaction> listTransactions(Pageable pageable) {
        return transactionService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Transaction getTransaction(@PathVariable String id) {
        return transactionService.findById(id);
    }

    @GetMapping("/by-card/{cardNumber}")
    public Page<Transaction> getByCard(@PathVariable String cardNumber,
                                       Pageable pageable) {
        return transactionService.findByCardNumber(cardNumber, pageable);
    }

    @GetMapping("/by-account/{accountId}")
    public Page<Transaction> getByAccount(@PathVariable Long accountId,
                                          Pageable pageable) {
        return transactionService.findByAccountId(accountId, pageable);
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(
            @RequestBody Transaction transaction) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.createTransaction(transaction));
    }
}
