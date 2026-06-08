package com.carddemo.repository;

import com.carddemo.model.Customer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByLastNameContainingIgnoreCase(String lastName);

    List<Customer> findByAddressStateCode(String stateCode);

    List<Customer> findBySsn(String ssn);
}
