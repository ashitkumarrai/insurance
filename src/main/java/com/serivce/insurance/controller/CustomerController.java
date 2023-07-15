package com.serivce.insurance.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serivce.insurance.entity.Customer;
import com.serivce.insurance.entity.User;
import com.serivce.insurance.exceptionhandler.RecordNotFoundException;
import com.serivce.insurance.payload.CustomerCreationForm;
import com.serivce.insurance.payload.CustomerFindAllData;
import com.serivce.insurance.payload.CustomerUpdate;
import com.serivce.insurance.repository.UserRepository;
import com.serivce.insurance.service.CustomerService;
import com.serivce.insurance.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;


@RestController
@Log4j2

public class CustomerController {
    @Autowired
    CustomerService customerService;

    @Autowired
    UserRepository userRepository;

    @Tag(name = "1. Customer endpoints")
     
    @Operation(operationId = "createCustomer", responses = {
     @ApiResponse(responseCode = "401", description = "Unauthorized request" ),
    @ApiResponse(responseCode = "403", description = "Forbidden request"),
    @ApiResponse(responseCode = "201", description = "created sucessfully") },description = "register customer",summary = "CREATE/REGISTER customer")
    @PostMapping("/register/customer")
public ResponseEntity<Map<String, String>> createCustomer(@RequestBody @Valid CustomerCreationForm customer)
           throws URISyntaxException {

       Customer result = customerService.createCustomer(customer);
       Map<String, String> hasMap = new HashMap<>();
       hasMap.put("response", "created sucessfully!");
       hasMap.put("URI", new URI("/customer?id=" + result.getCustomerId()).toString());

       return new ResponseEntity<>(hasMap, HttpStatus.CREATED);

   }


@SecurityRequirement(name="securedApis")
@GetMapping("/customers")
@Tag(name = "2. Admin endpoints")
    @Operation(operationId = "getAllCustomers", responses = {
     @ApiResponse(responseCode = "401", description = "Unauthorized request" ),
    @ApiResponse(responseCode = "403", description = "Forbidden request"),
    @ApiResponse(responseCode = "200", description = "sucessfull") },description = "get all customer",summary = "GET ALL CUSTOMERS")
    public ResponseEntity<CustomerFindAllData> getAllCustomers(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order) {

        Pageable paging = null;
        if (order.contains("asc")) {
            paging = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        } else {
            paging = PageRequest.of(page, size, Sort.by(sortBy).descending());
        }

        Page<Customer> pagedResult = customerService.findAll(paging);

       

        
        
    CustomerFindAllData response = new CustomerFindAllData();
    response.setCustomer(pagedResult.getContent());
    response.setPage(pagedResult.getNumber());
    response.setPageSize(pagedResult.getSize());
    response.setTotalElements(pagedResult.getTotalElements());
    response.setTotalPages(pagedResult.getTotalPages());

  

        return ResponseEntity.ok().body(response);
    }

    @SecurityRequirement(name = "securedApis")

    @Tag(name = "1. Customer endpoints")
         @Operation(operationId = "getAllCustomers", responses = {
     @ApiResponse(responseCode = "401", description = "Unauthorized request" ),
    @ApiResponse(responseCode = "403", description = "Forbidden request"),
    @ApiResponse(responseCode = "200", description = "sucessfull") },description = "get customer id or usename",summary = "GET CUSTOMER BY ID OR USERNAME")
    @GetMapping("/customer")
    public Customer getCustomerById(@RequestParam(defaultValue = "0") Long id,
            @RequestParam(defaultValue = "default") String username) throws RecordNotFoundException {

        if (id != 0l) {
            log.info("request param id catch in getcustomer by id method");
            return customerService.findById(id);

        }
         
        if (!username.equals("default")) {
            return customerService.findByUsername(username);

        }
      

        return customerService.findById(id);

    }

        
    @SecurityRequirement(name = "securedApis")
    @Tag(name="1. Customer endpoints")
    @DeleteMapping("/customer/delete/{id}")
    
    @Operation(operationId = "deleteCustomer", responses = {
     @ApiResponse(responseCode = "401", description = "Unauthorized request" ),
    @ApiResponse(responseCode = "403", description = "Forbidden request"),
    @ApiResponse(responseCode = "204", description = "deleted sucessfully") },description = " delete customer by id",summary = "DELETE CUSTOMER BY ID")

    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) throws RecordNotFoundException {

        customerService.deleteById(id);

        return ResponseEntity
                .noContent()
                .build();

    }
    
    @SecurityRequirement(name = "securedApis")
          @Operation(operationId = "getAllCustomers", responses = {
     @ApiResponse(responseCode = "401", description = "Unauthorized request" ),
    @ApiResponse(responseCode = "403", description = "Forbidden request"),
    @ApiResponse(responseCode = "201", description = "sucessfull") },description = "udate customer ",summary = "UPDATE  CUSTOMER")
       @PatchMapping("/customer/{id}")
       @Tag(name="1. Customer endpoints")
    public ResponseEntity<Map<String, String>> partialUpdateCustomer(
            @RequestBody @Valid CustomerUpdate customer) throws URISyntaxException, RecordNotFoundException {
                String username = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new RecordNotFoundException("user not logged in"));
        Customer result = customerService.partialUpdateCustomer(username, customer);

        Map<String, String> hasMap = new HashMap<>();
        hasMap.put("response", "updated sucessfully!");

        hasMap.put("URI", new URI("/customer?id=" + result.getCustomerId()).toString());

        return ResponseEntity.created(new URI("/customer?id=" + result.getCustomerId())).body(hasMap);

    }

     @SecurityRequirement(name="securedApis")
     @GetMapping("/profile")
     @Tag(name = "1. Customer endpoints")
        @Operation(operationId = "getAllCustomers", responses = {
     @ApiResponse(responseCode = "401", description = "Unauthorized request" ),
    @ApiResponse(responseCode = "403", description = "Forbidden request"),
    @ApiResponse(responseCode = "200", description = "sucessfull") },description = "get logged in customer",summary = "GET PROFILE OF LOGGED ID CUSTOMER")
    public ResponseEntity<Object> getProfile()
            throws RecordNotFoundException {

        String username = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new RecordNotFoundException("user not logged in"));

        Optional<User> userr = userRepository.findByUsername(username);
        if (!userr.isPresent()) {
            throw new RecordNotFoundException("logged in User not found");

        }

        if (SecurityUtils.hasCurrentUserThisAuthority("customer")) {
            return new ResponseEntity<>(customerService.findByUsername(username)
                   , HttpStatus.OK);

        }

        else if (SecurityUtils.hasCurrentUserThisAuthority("admin")) {
            return new ResponseEntity<>(userRepository.findByUsername(username)
                    .orElseThrow(() -> new RecordNotFoundException("profile not found in db")), HttpStatus.OK);
        } 
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

    }


}
