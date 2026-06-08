package com.carddemo.repository;

import com.carddemo.model.DisclosureGroup;
import com.carddemo.model.DisclosureGroup.DisclosureGroupId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisclosureGroupRepository
        extends JpaRepository<DisclosureGroup, DisclosureGroupId> {

    List<DisclosureGroup> findByIdAccountGroupId(String accountGroupId);
}
