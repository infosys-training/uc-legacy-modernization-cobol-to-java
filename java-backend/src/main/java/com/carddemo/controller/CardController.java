package com.carddemo.controller;

import com.carddemo.model.Card;
import com.carddemo.model.CardXref;
import com.carddemo.service.CardService;
import java.util.List;
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
 * REST API for Credit Card management.
 * Replaces CICS programs COCRDLIC (list), COCRDSLC (view), COCRDUPC (update).
 */
@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public Page<Card> listCards(Pageable pageable) {
        return cardService.findAll(pageable);
    }

    @GetMapping("/{cardNumber}")
    public Card getCard(@PathVariable String cardNumber) {
        return cardService.findByCardNumber(cardNumber);
    }

    @GetMapping("/by-account/{accountId}")
    public Page<Card> getCardsByAccount(@PathVariable Long accountId,
                                        Pageable pageable) {
        return cardService.findByAccountId(accountId, pageable);
    }

    @GetMapping("/xref/by-account/{accountId}")
    public List<CardXref> getXrefsByAccount(@PathVariable Long accountId) {
        return cardService.findXrefsByAccountId(accountId);
    }

    @GetMapping("/xref/by-customer/{customerId}")
    public List<CardXref> getXrefsByCustomer(@PathVariable Long customerId) {
        return cardService.findXrefsByCustomerId(customerId);
    }

    @PostMapping
    public ResponseEntity<Card> createCard(@RequestBody Card card,
                                           @RequestParam Long customerId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cardService.createCard(card, customerId));
    }

    @PutMapping("/{cardNumber}")
    public Card updateCard(@PathVariable String cardNumber,
                           @RequestBody Card card) {
        return cardService.updateCard(cardNumber, card);
    }
}
