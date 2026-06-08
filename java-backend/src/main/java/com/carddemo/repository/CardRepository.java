package com.carddemo.repository;

import com.carddemo.model.Card;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, String> {

    List<Card> findByAccountId(Long accountId);

    Page<Card> findByAccountId(Long accountId, Pageable pageable);

    List<Card> findByActiveStatus(String status);
}
