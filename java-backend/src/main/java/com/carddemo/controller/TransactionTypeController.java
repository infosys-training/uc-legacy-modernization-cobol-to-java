package com.carddemo.controller;

import com.carddemo.model.TransactionCategory;
import com.carddemo.model.TransactionType;
import com.carddemo.service.TransactionTypeService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for Transaction Type and Category management.
 * Replaces CICS programs COTRTLIC (list, DB2 cursors) and COTRTUPC (update/delete, DB2).
 */
@RestController
@RequestMapping("/api/v1/transaction-types")
public class TransactionTypeController {

    private final TransactionTypeService service;

    public TransactionTypeController(TransactionTypeService service) {
        this.service = service;
    }

    @GetMapping
    public List<TransactionType> listTypes() {
        return service.findAllTypes();
    }

    @GetMapping("/{typeCode}")
    public TransactionType getType(@PathVariable String typeCode) {
        return service.findTypeByCode(typeCode);
    }

    @PostMapping
    public ResponseEntity<TransactionType> createType(
            @RequestBody TransactionType type) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createType(type));
    }

    @PutMapping("/{typeCode}")
    public TransactionType updateType(@PathVariable String typeCode,
                                      @RequestBody TransactionType type) {
        return service.updateType(typeCode, type);
    }

    @DeleteMapping("/{typeCode}")
    public ResponseEntity<Void> deleteType(@PathVariable String typeCode) {
        service.deleteType(typeCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{typeCode}/categories")
    public List<TransactionCategory> listCategories(
            @PathVariable String typeCode) {
        return service.findCategoriesByType(typeCode);
    }

    @PostMapping("/{typeCode}/categories")
    public ResponseEntity<TransactionCategory> createCategory(
            @PathVariable String typeCode,
            @RequestBody TransactionCategory category) {
        if (category.getId() == null) {
            category.setId(new TransactionCategory.TransactionCategoryId());
        }
        category.getId().setTypeCode(typeCode);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createCategory(category));
    }

    @DeleteMapping("/{typeCode}/categories/{categoryCode}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable String typeCode,
            @PathVariable Integer categoryCode) {
        service.deleteCategory(typeCode, categoryCode);
        return ResponseEntity.noContent().build();
    }
}
