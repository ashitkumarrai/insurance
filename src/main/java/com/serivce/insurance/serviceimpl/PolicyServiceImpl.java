package com.serivce.insurance.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.serivce.insurance.entity.Customer;
import com.serivce.insurance.entity.Policy;
import com.serivce.insurance.entity.User;
import com.serivce.insurance.exceptionhandler.BlankMandatoryFieldException;
import com.serivce.insurance.exceptionhandler.RecordNotFoundException;
import com.serivce.insurance.payload.PolicyCreationForm;
import com.serivce.insurance.repository.CustomerRepository;
import com.serivce.insurance.repository.PolicyRepository;
import com.serivce.insurance.repository.UserRepository;
import com.serivce.insurance.service.PolicyService;
import com.serivce.insurance.util.SecurityUtils;

import jakarta.validation.Valid;

@Service
public class PolicyServiceImpl implements PolicyService{
      @Autowired
      PolicyRepository policyRepository;
    
      @Autowired
      CustomerRepository customerRepository;

      @Autowired
      UserRepository userRepository;

     

    @Override
    public Policy createPolicy(Long customerId, PolicyCreationForm policyForm)
            throws BlankMandatoryFieldException, RecordNotFoundException {
               

        
          
       

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RecordNotFoundException("customer of id " + customerId + " is not found in db"));
String username = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new RecordNotFoundException("user not logged in"));
                User createdByUser = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user not found db"));
        Policy policy = Policy.builder()
                        .createdByUser(createdByUser)
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

        String fullName = customer.getFullName();
                
        if ((!isNotBlank(customer.getImageUrl())) || customer.getImageUrl().equalsIgnoreCase("null")
                || customer.getImageUrl().equalsIgnoreCase("string")) {
               
                    throw new BlankMandatoryFieldException(
                            "customer of username " + fullName + " customer profile image not uploaded, complete the profile first");
                }
                else if ((!isNotBlank(customer.getIdentitydocumentUrl()))
                        || customer.getIdentitydocumentUrl().equalsIgnoreCase("null")
                        || customer.getIdentitydocumentUrl().equalsIgnoreCase("string")) {
                    throw new BlankMandatoryFieldException(
                            "customer: " + fullName
                                    + "identity document is is not uploaded, complete the profile first");
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
    return policyRepository.findByCustomerFullNameIgnoreCase(username);
              
}

@Override
public Policy partialUpdatePolicy(Long id, @Valid PolicyCreationForm policy) throws RecordNotFoundException {
    
    throw new UnsupportedOperationException("Unimplemented method 'partialUpdatePolicy'");
}

}
