package com.carddemo.repository;

import com.carddemo.model.Account;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByActiveStatus(String status);

    Page<Account> findByActiveStatus(String status, Pageable pageable);

    List<Account> findByGroupId(String groupId);
}
