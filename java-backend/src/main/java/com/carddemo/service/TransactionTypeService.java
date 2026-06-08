package com.carddemo.service;

import com.carddemo.exception.BusinessValidationException;
import com.carddemo.exception.ResourceNotFoundException;
import com.carddemo.model.TransactionCategory;
import com.carddemo.model.TransactionCategory.TransactionCategoryId;
import com.carddemo.model.TransactionType;
import com.carddemo.repository.TransactionCategoryRepository;
import com.carddemo.repository.TransactionTypeRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Replaces COBOL programs:
 *   COTRTLIC.cbl (Transaction Type List — DB2 cursor-based pagination)
 *   COTRTUPC.cbl (Transaction Type Update/Delete — DB2 with cascading deletes)
 *
 * These were the DB2-based programs and the most natural entry point
 * for database modernization since DB2 SQL maps closely to JPA.
 */
@Service
public class TransactionTypeService {

    private final TransactionTypeRepository typeRepository;
    private final TransactionCategoryRepository categoryRepository;

    public TransactionTypeService(TransactionTypeRepository typeRepository,
                                  TransactionCategoryRepository categoryRepository) {
        this.typeRepository = typeRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<TransactionType> findAllTypes() {
        return typeRepository.findAll();
    }

    public TransactionType findTypeByCode(String typeCode) {
        return typeRepository.findById(typeCode)
                .orElseThrow(() -> new ResourceNotFoundException("TransactionType", typeCode));
    }

    @Transactional
    public TransactionType createType(TransactionType type) {
        if (type.getTypeCode() == null || type.getTypeCode().length() != 2) {
            throw new BusinessValidationException(
                    "Type code must be exactly 2 characters");
        }
        if (typeRepository.existsById(type.getTypeCode())) {
            throw new BusinessValidationException(
                    "Transaction type already exists: " + type.getTypeCode());
        }
        return typeRepository.save(type);
    }

    @Transactional
    public TransactionType updateType(String typeCode, TransactionType updated) {
        TransactionType existing = findTypeByCode(typeCode);
        if (updated.getDescription() != null) {
            existing.setDescription(updated.getDescription());
        }
        return typeRepository.save(existing);
    }

    @Transactional
    public void deleteType(String typeCode) {
        if (!typeRepository.existsById(typeCode)) {
            throw new ResourceNotFoundException("TransactionType", typeCode);
        }
        List<TransactionCategory> categories = categoryRepository.findByIdTypeCode(typeCode);
        categoryRepository.deleteAll(categories);
        typeRepository.deleteById(typeCode);
    }

    public List<TransactionCategory> findCategoriesByType(String typeCode) {
        return categoryRepository.findByIdTypeCode(typeCode);
    }

    @Transactional
    public TransactionCategory createCategory(TransactionCategory category) {
        if (category.getId() == null) {
            throw new BusinessValidationException("Category ID (typeCode + categoryCode) is required");
        }
        if (categoryRepository.existsById(category.getId())) {
            throw new BusinessValidationException("Transaction category already exists");
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(String typeCode, Integer categoryCode) {
        TransactionCategoryId id = new TransactionCategoryId(typeCode, categoryCode);
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("TransactionCategory",
                    typeCode + "/" + categoryCode);
        }
        categoryRepository.deleteById(id);
    }
}
