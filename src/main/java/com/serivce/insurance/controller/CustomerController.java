package com.serivce.insurance.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.NamingException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serivce.insurance.entity.Customer;
import com.serivce.insurance.exceptionhandler.RecordNotFoundException;
import com.serivce.insurance.payload.CustomerCreationForm;
import com.serivce.insurance.payload.CustomerFindAllData;
import com.serivce.insurance.payload.CustomerUpdate;
import com.serivce.insurance.repository.CustomerRepository;
import com.serivce.insurance.repository.UserRepository;
import com.serivce.insurance.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;


@RestController
@Log4j2
  @Tag(name = "1. User(Agent,Promoter,etc) endpoints")
public class CustomerController {
    @Autowired
    CustomerService customerService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CustomerRepository customerRepository;

  
     @SecurityRequirement(name="Authorization")
    @Operation(operationId = "createCustomer", responses = {
     @ApiResponse(responseCode = "401", description = "Unauthorized request" ),
    @ApiResponse(responseCode = "403", description = "Forbidden request"),
            @ApiResponse(responseCode = "201", description = "created sucessfully") }, description = "register /Onboard customer(users like agent or promoters will create customers)", summary = "Onboard/REGISTER customer")
    @PostMapping("/create/customer")
public ResponseEntity<Map<String, String>> createCustomer(@RequestBody @Valid CustomerCreationForm customer)
           throws URISyntaxException,NoSuchAlgorithmException, javax.naming.NamingException, NamingException, RecordNotFoundException {

       Customer result = customerService.createCustomer(customer);
       Map<String, String> hasMap = new HashMap<>();
       hasMap.put("response", "created sucessfully!");
       hasMap.put("URI", new URI("/customer?id=" + result.getCustomerId()).toString());

       return new ResponseEntity<>(hasMap, HttpStatus.CREATED);

   }


@SecurityRequirement(name="Authorization")
@GetMapping("/customers")
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

    @SecurityRequirement(name = "Authorization")

         @Operation(operationId = "getAllCustomers", responses = {
     @ApiResponse(responseCode = "401", description = "Unauthorized request" ),
    @ApiResponse(responseCode = "403", description = "Forbidden request"),
    @ApiResponse(responseCode = "200", description = "sucessfull") },description = "get customer id or usename",summary = "GET CUSTOMERS BY ID OR Customer FUll Name")
    @GetMapping("/customer")
    public ResponseEntity<Object> getCustomerById(@RequestParam(defaultValue = "0") Long customerId,
            @RequestParam(defaultValue = "default") String customerFullName) throws RecordNotFoundException {

        if (customerId != 0l) {
            log.info("request param id catch in getcustomer by id method");
            return ResponseEntity.ok(customerService.findById(customerId));

        }
         
        if (!customerFullName.equals("default")) {
            return ResponseEntity.ok(customerRepository.findByFullNameIgnoreCase(customerFullName));

        }
      

        return ResponseEntity.ok(customerService.findById(customerId));

    }

        
    @SecurityRequirement(name = "Authorization")
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
    
    @SecurityRequirement(name = "Authorization")
          @Operation(operationId = "partialUpdateCustomer", responses = {
     @ApiResponse(responseCode = "401", description = "Unauthorized request" ),
    @ApiResponse(responseCode = "403", description = "Forbidden request"),
    @ApiResponse(responseCode = "201", description = "sucessfull") },description = "udate customer ",summary = "UPDATE  CUSTOMER")
       @PatchMapping("/customer/{customerId}")
    public ResponseEntity<Map<String, String>> partialUpdateCustomer(@PathVariable Long customerId,
            @RequestBody @Valid CustomerUpdate customer ) throws URISyntaxException, RecordNotFoundException {
              
        Customer result = customerService.partialUpdateCustomer(customerId, customer);

        Map<String, String> hasMap = new HashMap<>();
        hasMap.put("response", "updated sucessfully!");
        hasMap.put("URI", new URI("/customer/" + result.getCustomerId()).toString());

        return ResponseEntity.created(new URI("/customer/" + result.getCustomerId())).body(hasMap);

    }

     


}
