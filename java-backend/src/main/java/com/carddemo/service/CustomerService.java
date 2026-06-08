package com.carddemo.service;

import com.carddemo.exception.BusinessValidationException;
import com.carddemo.exception.ResourceNotFoundException;
import com.carddemo.model.Customer;
import com.carddemo.repository.CustomerRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Customer management extracted from COACTUPC.cbl.
 * The original COBOL program accessed customer data through the
 * Card XREF → Customer VSAM lookup chain. Customer fields were
 * validated inline within the 4,236-line account update program.
 *
 * This service also supports data import (CBIMPORT.cbl) and
 * export (CBEXPORT.cbl) operations for the customer file.
 */
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Page<Customer> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }

    public List<Customer> searchByLastName(String lastName) {
        return customerRepository.findByLastNameContainingIgnoreCase(lastName);
    }

    public List<Customer> findByState(String stateCode) {
        return customerRepository.findByAddressStateCode(stateCode);
    }

    @Transactional
    public Customer createCustomer(Customer customer) {
        if (customer.getCustId() == null) {
            throw new BusinessValidationException("Customer ID is required");
        }
        if (customerRepository.existsById(customer.getCustId())) {
            throw new BusinessValidationException(
                    "Customer already exists: " + customer.getCustId());
        }
        validateCustomer(customer);
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer updateCustomer(Long id, Customer updated) {
        Customer existing = findById(id);

        if (updated.getFirstName() != null) existing.setFirstName(updated.getFirstName());
        if (updated.getMiddleName() != null) existing.setMiddleName(updated.getMiddleName());
        if (updated.getLastName() != null) existing.setLastName(updated.getLastName());
        if (updated.getAddressLine1() != null) existing.setAddressLine1(updated.getAddressLine1());
        if (updated.getAddressLine2() != null) existing.setAddressLine2(updated.getAddressLine2());
        if (updated.getAddressLine3() != null) existing.setAddressLine3(updated.getAddressLine3());
        if (updated.getAddressStateCode() != null) existing.setAddressStateCode(updated.getAddressStateCode());
        if (updated.getAddressCountryCode() != null) existing.setAddressCountryCode(updated.getAddressCountryCode());
        if (updated.getAddressZip() != null) existing.setAddressZip(updated.getAddressZip());
        if (updated.getPhoneNumber1() != null) existing.setPhoneNumber1(updated.getPhoneNumber1());
        if (updated.getPhoneNumber2() != null) existing.setPhoneNumber2(updated.getPhoneNumber2());
        if (updated.getGovernmentIssuedId() != null) existing.setGovernmentIssuedId(updated.getGovernmentIssuedId());
        if (updated.getDateOfBirth() != null) existing.setDateOfBirth(updated.getDateOfBirth());
        if (updated.getEftAccountId() != null) existing.setEftAccountId(updated.getEftAccountId());
        if (updated.getFicoCreditScore() != null) existing.setFicoCreditScore(updated.getFicoCreditScore());

        return customerRepository.save(existing);
    }

    private void validateCustomer(Customer customer) {
        if (customer.getFirstName() == null || customer.getFirstName().isBlank()) {
            throw new BusinessValidationException("First name is required");
        }
        if (customer.getLastName() == null || customer.getLastName().isBlank()) {
            throw new BusinessValidationException("Last name is required");
        }
        if (customer.getFicoCreditScore() != null
                && (customer.getFicoCreditScore() < 300 || customer.getFicoCreditScore() > 850)) {
            throw new BusinessValidationException(
                    "FICO score must be between 300 and 850");
        }
    }
}
