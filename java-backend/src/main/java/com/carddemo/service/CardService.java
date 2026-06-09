package com.carddemo.service;

import com.carddemo.exception.BusinessValidationException;
import com.carddemo.exception.ResourceNotFoundException;
import com.carddemo.model.Card;
import com.carddemo.model.CardXref;
import com.carddemo.repository.AccountRepository;
import com.carddemo.repository.CardRepository;
import com.carddemo.repository.CardXrefRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Replaces COBOL programs:
 *   COCRDLIC.cbl (Card List — 1,459 LOC)  — paginated browse with STARTBR/READNEXT
 *   COCRDSLC.cbl (Card View — 887 LOC)    — single card detail lookup
 *   COCRDUPC.cbl (Card Update — 1,560 LOC) — update card attributes + XREF
 */
@Service
public class CardService {

    private final CardRepository cardRepository;
    private final CardXrefRepository cardXrefRepository;
    private final AccountRepository accountRepository;

    public CardService(CardRepository cardRepository,
                       CardXrefRepository cardXrefRepository,
                       AccountRepository accountRepository) {
        this.cardRepository = cardRepository;
        this.cardXrefRepository = cardXrefRepository;
        this.accountRepository = accountRepository;
    }

    public Page<Card> findAll(Pageable pageable) {
        return cardRepository.findAll(pageable);
    }

    public Card findByCardNumber(String cardNumber) {
        return cardRepository.findById(cardNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Card", cardNumber));
    }

    public Page<Card> findByAccountId(Long accountId, Pageable pageable) {
        return cardRepository.findByAccountId(accountId, pageable);
    }

    public List<CardXref> findXrefsByAccountId(Long accountId) {
        return cardXrefRepository.findByAccountId(accountId);
    }

    public List<CardXref> findXrefsByCustomerId(Long customerId) {
        return cardXrefRepository.findByCustomerId(customerId);
    }

    @Transactional
    public Card updateCard(String cardNumber, Card updated) {
        Card existing = findByCardNumber(cardNumber);

        if (updated.getActiveStatus() != null) {
            validateStatus(updated.getActiveStatus());
            existing.setActiveStatus(updated.getActiveStatus());
        }
        if (updated.getEmbossedName() != null) {
            existing.setEmbossedName(updated.getEmbossedName());
        }
        if (updated.getExpirationDate() != null) {
            existing.setExpirationDate(updated.getExpirationDate());
        }
        return cardRepository.save(existing);
    }

    @Transactional
    public Card createCard(Card card, Long customerId) {
        if (card.getCardNumber() == null || card.getCardNumber().isBlank()) {
            throw new BusinessValidationException("Card number is required");
        }
        if (cardRepository.existsById(card.getCardNumber())) {
            throw new BusinessValidationException(
                    "Card already exists: " + card.getCardNumber());
        }
        if (!accountRepository.existsById(card.getAccountId())) {
            throw new BusinessValidationException(
                    "Account does not exist: " + card.getAccountId());
        }
        if (card.getActiveStatus() == null) {
            card.setActiveStatus("Y");
        }
        Card saved = cardRepository.save(card);

        CardXref xref = new CardXref();
        xref.setCardNumber(card.getCardNumber());
        xref.setAccountId(card.getAccountId());
        xref.setCustomerId(customerId);
        cardXrefRepository.save(xref);

        return saved;
    }

    private void validateStatus(String status) {
        if (!"Y".equals(status) && !"N".equals(status)) {
            throw new BusinessValidationException(
                    "Active status must be 'Y' or 'N'");
        }
    }
}
