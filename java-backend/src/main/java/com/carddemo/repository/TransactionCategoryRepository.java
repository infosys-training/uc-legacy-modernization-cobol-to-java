package com.carddemo.repository;

import com.carddemo.model.TransactionCategory;
import com.carddemo.model.TransactionCategory.TransactionCategoryId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionCategoryRepository
        extends JpaRepository<TransactionCategory, TransactionCategoryId> {

    List<TransactionCategory> findByIdTypeCode(String typeCode);
}
