package com.carddemo.repository;

import com.carddemo.model.CardXref;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardXrefRepository extends JpaRepository<CardXref, String> {

    List<CardXref> findByAccountId(Long accountId);

    List<CardXref> findByCustomerId(Long customerId);
}
