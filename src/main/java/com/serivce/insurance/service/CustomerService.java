package com.serivce.insurance.service;


import java.security.NoSuchAlgorithmException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ldap.NamingException;

import com.serivce.insurance.entity.Customer;
import com.serivce.insurance.exceptionhandler.RecordNotFoundException;
import com.serivce.insurance.payload.CustomerCreationForm;
import com.serivce.insurance.payload.CustomerUpdate;


public interface CustomerService {

    Customer createCustomer(CustomerCreationForm customer) throws NamingException, NoSuchAlgorithmException, javax.naming.NamingException, RecordNotFoundException;

    Page<Customer> findAll(Pageable paging);

    Customer findById(Long id) throws RecordNotFoundException;

    void deleteById(Long id) throws RecordNotFoundException;

    Customer partialUpdateCustomer(Long customerId, CustomerUpdate customer) throws RecordNotFoundException;

 
   
    
}
