package com.carddemo.repository;

import com.carddemo.model.Account;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.acctId = :id")
    Optional<Account> findByIdForUpdate(@Param("id") Long id);

    List<Account> findByActiveStatus(String status);

    Page<Account> findByActiveStatus(String status, Pageable pageable);

    List<Account> findByGroupId(String groupId);
}
