package com.carddemo.service;

import com.carddemo.exception.BusinessValidationException;
import com.carddemo.exception.ResourceNotFoundException;
import com.carddemo.model.Account;
import com.carddemo.model.CardXref;
import com.carddemo.model.Transaction;
import com.carddemo.repository.AccountRepository;
import com.carddemo.repository.CardXrefRepository;
import com.carddemo.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Replaces COBOL programs:
 *   COTRN00C.cbl (Transaction List — 699 LOC) — paginated browse with STARTBR/READNEXT
 *   COTRN01C.cbl (Transaction View — 330 LOC) — single transaction detail
 *   COTRN02C.cbl (Transaction Add — 783 LOC)  — add new transaction with validation
 *   CBTRN02C.cbl (Batch: Post Transactions — 731 LOC) — daily transaction posting
 *
 * The add-transaction logic from COTRN02C validates:
 *   - Card number exists in XREF file
 *   - Account linked to card is active
 *   - Transaction amount does not exceed credit limit
 *   - Required fields are present (card num, type, amount)
 *
 * The batch posting logic from CBTRN02C updates account balances
 * after processing each transaction.
 */
@Service
public class TransactionService {

    private static final DateTimeFormatter ID_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS");
    private static final java.util.concurrent.atomic.AtomicInteger SEQ =
            new java.util.concurrent.atomic.AtomicInteger(0);

    private final TransactionRepository transactionRepository;
    private final CardXrefRepository cardXrefRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              CardXrefRepository cardXrefRepository,
                              AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.cardXrefRepository = cardXrefRepository;
        this.accountRepository = accountRepository;
    }

    public Page<Transaction> findAll(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    public Transaction findById(String id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));
    }

    public Page<Transaction> findByCardNumber(String cardNumber, Pageable pageable) {
        return transactionRepository.findByCardNumber(cardNumber, pageable);
    }

    public Page<Transaction> findByAccountId(Long accountId, Pageable pageable) {
        return transactionRepository.findByAccountId(accountId, pageable);
    }

    /**
     * Replicates COTRN02C — Add a transaction.
     * Validates the card number against XREF, checks the account is active,
     * verifies the amount is within the credit limit, then posts the
     * transaction and updates the account balance (replicating CBTRN02C batch logic).
     */
    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        validateTransaction(transaction);

        if (transaction.getTransactionId() == null || transaction.getTransactionId().isBlank()) {
            transaction.setTransactionId(generateTransactionId());
        }
        if (transaction.getOriginTimestamp() == null) {
            transaction.setOriginTimestamp(LocalDateTime.now());
        }

        CardXref xref = cardXrefRepository.findById(transaction.getCardNumber())
                .orElseThrow(() -> new BusinessValidationException(
                        "Card number not found in cross-reference: " + transaction.getCardNumber()));

        Account account = accountRepository.findById(xref.getAccountId())
                .orElseThrow(() -> new BusinessValidationException(
                        "Account not found for card: " + transaction.getCardNumber()));

        if (!"Y".equals(account.getActiveStatus())) {
            throw new BusinessValidationException(
                    "Account is not active: " + account.getAcctId());
        }

        BigDecimal newBalance = account.getCurrentBalance().add(transaction.getAmount());
        if (newBalance.compareTo(account.getCreditLimit()) > 0) {
            throw new BusinessValidationException(
                    "Transaction would exceed credit limit. Current balance: "
                    + account.getCurrentBalance() + ", limit: " + account.getCreditLimit());
        }

        account.setCurrentBalance(newBalance);
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            account.setCurrentCycleDebit(
                    account.getCurrentCycleDebit().add(transaction.getAmount()));
        } else {
            account.setCurrentCycleCredit(
                    account.getCurrentCycleCredit().add(transaction.getAmount().abs()));
        }

        transaction.setProcessedTimestamp(LocalDateTime.now());
        accountRepository.save(account);
        return transactionRepository.save(transaction);
    }

    private void validateTransaction(Transaction t) {
        if (t.getCardNumber() == null || t.getCardNumber().isBlank()) {
            throw new BusinessValidationException("Card number is required");
        }
        if (t.getTypeCode() == null || t.getTypeCode().isBlank()) {
            throw new BusinessValidationException("Transaction type code is required");
        }
        if (t.getAmount() == null) {
            throw new BusinessValidationException("Transaction amount is required");
        }
    }

    private String generateTransactionId() {
        String ts = LocalDateTime.now().format(ID_FORMAT);
        int seq = SEQ.getAndIncrement() % 10000;
        return String.format("%s%04d", ts.substring(0, Math.min(ts.length(), 12)), seq);
    }
}
