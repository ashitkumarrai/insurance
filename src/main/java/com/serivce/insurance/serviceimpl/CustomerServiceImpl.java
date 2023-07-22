package com.serivce.insurance.serviceimpl;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ldap.NamingException;
import org.springframework.stereotype.Service;
import com.serivce.insurance.entity.Customer;
import com.serivce.insurance.entity.User;
import com.serivce.insurance.exceptionhandler.RecordNotFoundException;
import com.serivce.insurance.payload.CustomerCreationForm;
import com.serivce.insurance.payload.CustomerUpdate;
import com.serivce.insurance.repository.CustomerRepository;
import com.serivce.insurance.service.CustomerService;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    PortalUserService portalUserService;

    @Override
    public Customer createCustomer(CustomerCreationForm customerForm) throws NamingException, NoSuchAlgorithmException, javax.naming.NamingException {

        User user = User.builder().username(customerForm.username())
              
                
                
                
                .grantedAuthorities(List.of("customer"))
                .build();

        Customer customer = Customer.builder()
                .user(user)
                .fullName(customerForm.fullName())
                .email(customerForm.email())
                .phone(customerForm.phone())
                .imageUrl(customerForm.imageUrl())
                .identitydocumentUrl(customerForm.identitydocumentUrl())
                .occupation(customerForm.occupation())
                .gender(customerForm.gender())
                .maritalStatus(customerForm.maritalStatus())
                .numberOfDependents(customerForm.numberOfDependents())
                .dateOfBirth(customerForm.dateOfBirth())
                .address(customerForm.address())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        Customer savedCustomer = customerRepository.save(customer);
        log.info("saved user in database");
        try{portalUserService.createCustomerInLdap(customerForm.username(), customerForm.password(), customerForm.fullName());}
        catch (Exception e) {
            customerRepository.delete(savedCustomer); //if customer not creating  in ldap then dont save in mysql also
            log.info("rollbacked the saved user from database because of the exception came in ldap");
            throw e;
        }

        return savedCustomer;

    }

    @Override
    public Page<Customer> findAll(Pageable paging) {
        return customerRepository.findAll(paging);
    }

    @Override
    public Customer findById(Long id) throws RecordNotFoundException {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("customer of id " + id + " is not found in db"));
    }

    @Override
    public void deleteById(Long id) throws RecordNotFoundException {
        findById(id);
        customerRepository.deleteById(id);
    }

   @Override
public Customer partialUpdateCustomer(String  username, CustomerUpdate customer) throws RecordNotFoundException {
    Customer existingCustomer = findByUsername(username);

    updateUserFields(existingCustomer.getUser(), customer);
    updateCustomerFields(existingCustomer, customer);

    existingCustomer.setUpdatedAt(Instant.now());

    return customerRepository.save(existingCustomer);
}

private void updateUserFields(User user, CustomerUpdate customer) {
    if (isNotBlank(customer.username())) {
        user.setUsername(customer.username());
    }
   

 

}

private void updateCustomerFields(Customer customer, CustomerUpdate customer2) {
    if (isNotBlank(customer2.fullName())) {
        customer.setFullName(customer2.fullName());
    }
       if (isNotBlank(customer2.email())) {
        customer.setEmail(customer2.email());
    }
    if (isNotBlank(customer2.phone())) {
        customer.setPhone(customer2.phone());
    }
    if (isNotBlank(customer2.imageUrl())) {
        customer.setImageUrl(customer2.imageUrl());
    }
    if (isNotBlank(customer2.identitydocumentUrl())) {
        customer.setIdentitydocumentUrl(customer2.identitydocumentUrl());
    }

    if (isNotBlank(customer2.occupation())) {
        customer.setOccupation(customer2.occupation());
    }
    if (customer2.gender() != null) {
        customer.setGender(customer2.gender());
    }
    if (customer2.maritalStatus() != null) {
        customer.setMaritalStatus(customer2.maritalStatus());
    }
    if (customer2.numberOfDependents() != null) {
        customer.setNumberOfDependents(customer2.numberOfDependents());
    }
    if (customer2.dateOfBirth() != null) {
        customer.setDateOfBirth(customer2.dateOfBirth());
    }
    if (customer2.address() != null) {
        customer.setAddress(customer2.address());
    }
}

private boolean isNotBlank(String value) {
    return value != null && !value.isEmpty();
}

@Override
public Customer findByUsername(String username) throws RecordNotFoundException {
     return customerRepository.findByUserUsernameIgnoreCase(username)
                .orElseThrow(() -> new RecordNotFoundException("customer of username " + username + " is not found in db"));
}

}
