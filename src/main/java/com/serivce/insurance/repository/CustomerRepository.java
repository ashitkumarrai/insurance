package com.serivce.insurance.repository;

import com.serivce.insurance.entity.Customer;
import com.serivce.insurance.entity.User;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import org.springframework.stereotype.Repository;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>{
    Page<Customer> findAll(Pageable pageable);
    public Optional<User> findByEmail(String email);
    Optional<Customer> findByUserUsernameIgnoreCase(String username);
}
