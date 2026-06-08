package com.carddemo.controller;

import com.carddemo.model.Account;
import com.carddemo.service.AccountService;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for Account management.
 * Replaces CICS transaction codes CC01 (COACTVWC) and CC02 (COACTUPC).
 */
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public Page<Account> listAccounts(Pageable pageable) {
        return accountService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Account getAccount(@PathVariable Long id) {
        return accountService.findById(id);
    }

    @GetMapping("/{id}/details")
    public Map<String, Object> getAccountDetails(@PathVariable Long id) {
        return accountService.getAccountDetails(id);
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.createAccount(account));
    }

    @PutMapping("/{id}")
    public Account updateAccount(@PathVariable Long id, @RequestBody Account account) {
        return accountService.updateAccount(id, account);
    }

    @PostMapping("/{id}/interest")
    public Account applyInterest(@PathVariable Long id,
                                 @RequestParam BigDecimal rate) {
        return accountService.applyInterest(id, rate);
    }
}
