package com.carddemo.repository;

import com.carddemo.model.Transaction;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findByCardNumber(String cardNumber);

    Page<Transaction> findByCardNumber(String cardNumber, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.cardNumber IN " +
           "(SELECT x.cardNumber FROM CardXref x WHERE x.accountId = :acctId)")
    Page<Transaction> findByAccountId(@Param("acctId") Long accountId, Pageable pageable);

    List<Transaction> findByTypeCode(String typeCode);
}
