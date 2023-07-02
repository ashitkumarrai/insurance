package com.serivce.insurance.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.serivce.insurance.entity.Customer;
import com.serivce.insurance.entity.Policy;
import com.serivce.insurance.exceptionhandler.BlankMandatoryFieldException;
import com.serivce.insurance.exceptionhandler.RecordNotFoundException;
import com.serivce.insurance.payload.PolicyCreationForm;
import com.serivce.insurance.repository.CustomerRepository;
import com.serivce.insurance.repository.PolicyRepository;
import com.serivce.insurance.service.PolicyService;
import jakarta.validation.Valid;
import jakarta.validation.Valid;

@Service
public class PolicyServiceImpl implements PolicyService{
      @Autowired
      PolicyRepository policyRepository;
    
      @Autowired
      CustomerRepository customerRepository;

     

    @Override
    public Policy createPolicy(PolicyCreationForm policyForm) throws BlankMandatoryFieldException, RecordNotFoundException {


       

        Customer customer = customerRepository.findById(policyForm.customerId())
                .orElseThrow(() -> new RecordNotFoundException("customer of id " + policyForm.customerId() + " is not found in db"));

        Policy policy = Policy.builder()
                        .customer(customer)
                        .policyName(policyForm.policyName())
                        .policyType(policyForm.policyType())
                        .startDate(policyForm.startDate())
                        .duration(policyForm.duration())
                        .coverageAmount(policyForm.coverageAmount())
                        .coverageOptions(policyForm.coverageOptions())
                        .deductible(policyForm.deductible())
                        .beneficiaryName(policyForm.beneficiaryName())
                .beneficiaryRelationship(policyForm.beneficiaryRelationship()).build();

        if (!isNotBlank(customer.getImageUrl())) {
            throw new BlankMandatoryFieldException(
                    "customer of id " + policyForm.customerId() + " has not uploaded profile image, complete the profile first");
        }
        else if (!isNotBlank(customer.getIdentitydocumentUrl())) {
             throw new BlankMandatoryFieldException(
                    "customer of id " + policyForm.customerId() + " has not uploaded any identity document,complete the profile first");
        }
                

        return policyRepository.save(policy);

    }

    @Override
    public Page<Policy> findAll(Pageable paging) {
        return policyRepository.findAll(paging);
    }

    @Override
    public Policy findById(Long id) throws RecordNotFoundException {
        return policyRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("policy of id " + id + " is not found in db"));
    }

    @Override
    public void deleteById(Long id) throws RecordNotFoundException {
        findById(id);
        policyRepository.deleteById(id);
    }



private boolean isNotBlank(String value) {
    return value != null && !value.isEmpty();
}



@Override
public Policy findByPolicyName(String policyName) throws RecordNotFoundException {
    return policyRepository.findByPolicyNameIgnoreCase(policyName)
                .orElseThrow(() -> new RecordNotFoundException("policy of name " + policyName + " is not found in db"));
}

@Override
public List<Policy> findByPolicyCustomerName(String username){
    return policyRepository.findByCustomerUserUsernameIgnoreCase(username);
              
}

@Override
public Policy partialUpdatePolicy(Long id, @Valid PolicyCreationForm policy) throws RecordNotFoundException {
    
    throw new UnsupportedOperationException("Unimplemented method 'partialUpdatePolicy'");
}

}
