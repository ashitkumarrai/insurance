package com.serivce.insurance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.serivce.insurance.entity.Policy;
import org.springframework.stereotype.Repository;







@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    Page<Policy> findAll(Pageable pageable);
      @Query("SELECT p FROM Policy p WHERE p.policyName = :query")
    Optional<Policy> findByPolicyNameIgnoreCase(@Param("query")String query);

     
   

    @Query("SELECT p FROM Policy p WHERE p.customer.user.username = :query")
      List<Policy> findByCustomerUserUsernameIgnoreCase(@Param("query") String username);

    
}
