package com.carddemo.repository;

import com.carddemo.model.UserSecurity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSecurityRepository extends JpaRepository<UserSecurity, String> {

    List<UserSecurity> findByUserType(String userType);
}
