package com.serivce.insurance.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.serivce.insurance.entity.Policy;
import com.serivce.insurance.exceptionhandler.BlankMandatoryFieldException;
import com.serivce.insurance.exceptionhandler.RecordNotFoundException;
import com.serivce.insurance.payload.PolicyCreationForm;

import jakarta.validation.Valid;

public interface PolicyService {

    Page<Policy> findAll(Pageable paging);

    Policy createPolicy(@Valid PolicyCreationForm policy) throws RecordNotFoundException, BlankMandatoryFieldException;


    Policy findByPolicyName(String policyName) throws RecordNotFoundException;

    void deleteById(Long id) throws RecordNotFoundException;

    Policy partialUpdatePolicy(Long id, @Valid PolicyCreationForm policy) throws RecordNotFoundException;

    List<Policy> findByPolicyCustomerName(String policyName) throws RecordNotFoundException;

    Policy findById(Long id) throws RecordNotFoundException;
    
}
