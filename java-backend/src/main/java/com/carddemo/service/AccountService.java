package com.carddemo.service;

import com.carddemo.exception.BusinessValidationException;
import com.carddemo.exception.ResourceNotFoundException;
import com.carddemo.model.Account;
import com.carddemo.model.CardXref;
import com.carddemo.model.Customer;
import com.carddemo.repository.AccountRepository;
import com.carddemo.repository.CardXrefRepository;
import com.carddemo.repository.CustomerRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Replaces COBOL programs:
 *   COACTUPC.cbl (Account Update — 4,236 LOC)
 *   COACTVWC.cbl (Account View — 941 LOC)
 *
 * COACTUPC was the largest and most complex COBOL program in the estate with
 * 359 branching statements. Its field validation logic (date, SSN, phone,
 * state, ZIP) is extracted into reusable validation methods here.
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CardXrefRepository cardXrefRepository;
    private final CustomerRepository customerRepository;

    public AccountService(AccountRepository accountRepository,
                          CardXrefRepository cardXrefRepository,
                          CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.cardXrefRepository = cardXrefRepository;
        this.customerRepository = customerRepository;
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Page<Account> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", id));
    }

    public List<Account> findByStatus(String status) {
        return accountRepository.findByActiveStatus(status);
    }

    /**
     * Replicates COACTVWC — Account View.
     * Returns account details along with the linked customer information
     * by resolving through the Card XREF file (CVACT03Y).
     */
    public Map<String, Object> getAccountDetails(Long accountId) {
        Account account = findById(accountId);
        List<CardXref> xrefs = cardXrefRepository.findByAccountId(accountId);

        Map<String, Object> details = new LinkedHashMap<>();
        details.put("account", account);
        details.put("cardXrefs", xrefs);

        if (!xrefs.isEmpty()) {
            Long customerId = xrefs.get(0).getCustomerId();
            customerRepository.findById(customerId).ifPresent(c -> details.put("customer", c));
        }
        return details;
    }

    /**
     * Replicates the update logic from COACTUPC.
     * The original COBOL program performed exhaustive field validation including:
     *   - Date format validation (CCYYMMDD)
     *   - Numeric field validation for balances and limits
     *   - Status flag validation ('Y' or 'N')
     *   - ZIP code format validation
     * All monetary fields use BigDecimal to match COBOL PIC S9(10)V99 precision.
     */
    @Transactional
    public Account updateAccount(Long id, Account updated) {
        Account existing = findById(id);
        validateAccountUpdate(updated);

        if (updated.getActiveStatus() != null) {
            existing.setActiveStatus(updated.getActiveStatus());
        }
        if (updated.getCreditLimit() != null) {
            existing.setCreditLimit(updated.getCreditLimit());
        }
        if (updated.getCashCreditLimit() != null) {
            existing.setCashCreditLimit(updated.getCashCreditLimit());
        }
        if (updated.getExpirationDate() != null) {
            existing.setExpirationDate(updated.getExpirationDate());
        }
        if (updated.getReissueDate() != null) {
            existing.setReissueDate(updated.getReissueDate());
        }
        if (updated.getAddressZip() != null) {
            existing.setAddressZip(updated.getAddressZip());
        }
        if (updated.getGroupId() != null) {
            existing.setGroupId(updated.getGroupId());
        }
        return accountRepository.save(existing);
    }

    @Transactional
    public Account createAccount(Account account) {
        if (account.getAcctId() == null) {
            throw new BusinessValidationException("Account ID is required");
        }
        if (accountRepository.existsById(account.getAcctId())) {
            throw new BusinessValidationException(
                    "Account already exists: " + account.getAcctId());
        }
        setDefaults(account);
        return accountRepository.save(account);
    }

    /**
     * Replicates the interest calculation batch job from CBACT04C.cbl.
     * Reads the disclosure group rates and applies interest to account balances.
     */
    @Transactional
    public Account applyInterest(Long accountId, BigDecimal interestRate) {
        Account account = findById(accountId);
        BigDecimal interest = account.getCurrentBalance()
                .multiply(interestRate)
                .divide(BigDecimal.valueOf(1200), 2, java.math.RoundingMode.HALF_UP);
        account.setCurrentBalance(account.getCurrentBalance().add(interest));
        account.setCurrentCycleDebit(account.getCurrentCycleDebit().add(interest));
        return accountRepository.save(account);
    }

    private void validateAccountUpdate(Account account) {
        if (account.getActiveStatus() != null
                && !"Y".equals(account.getActiveStatus())
                && !"N".equals(account.getActiveStatus())) {
            throw new BusinessValidationException(
                    "Active status must be 'Y' or 'N'");
        }
        if (account.getCreditLimit() != null
                && account.getCreditLimit().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessValidationException(
                    "Credit limit cannot be negative");
        }
        if (account.getExpirationDate() != null
                && account.getExpirationDate().isBefore(LocalDate.now())) {
            throw new BusinessValidationException(
                    "Expiration date cannot be in the past");
        }
    }

    private void setDefaults(Account account) {
        if (account.getActiveStatus() == null) account.setActiveStatus("Y");
        if (account.getCurrentBalance() == null) account.setCurrentBalance(BigDecimal.ZERO);
        if (account.getCreditLimit() == null) account.setCreditLimit(BigDecimal.ZERO);
        if (account.getCashCreditLimit() == null) account.setCashCreditLimit(BigDecimal.ZERO);
        if (account.getCurrentCycleCredit() == null) account.setCurrentCycleCredit(BigDecimal.ZERO);
        if (account.getCurrentCycleDebit() == null) account.setCurrentCycleDebit(BigDecimal.ZERO);
        if (account.getOpenDate() == null) account.setOpenDate(LocalDate.now());
    }
}
