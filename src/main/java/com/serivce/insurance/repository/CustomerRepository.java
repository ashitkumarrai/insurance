package com.serivce.insurance.repository;

import com.serivce.insurance.entity.Customer;
import com.serivce.insurance.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>{
    Page<Customer> findAll(Pageable pageable);

    public Optional<User> findByEmail(String email);
    
    @Query("SELECT p FROM Customer p WHERE LOWER(p.fullName) LIKE CONCAT('%', LOWER(:fullName), '%')")
      List<Customer> findByFullNameIgnoreCase(@Param("fullName") String fullName);

    List<Customer> findByCreatedByUserId(Long userId);
 
}
