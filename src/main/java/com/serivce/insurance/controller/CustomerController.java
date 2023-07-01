package com.serivce.insurance.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.serivce.insurance.exceptionhandler.RecordNotFoundException;
import com.serivce.insurance.payload.CustomerCreationForm;
import com.serivce.insurance.payload.CustomerUpdate;
import com.serivce.insurance.service.CustomerService;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;


@RestController
@Log4j2
public class CustomerController {
    @Autowired
    CustomerService customerService;

    
   @PostMapping("/customer")
   public ResponseEntity<Map<String, String>> createCustomer(@RequestBody @Valid CustomerCreationForm customer)
           throws URISyntaxException {

       Customer result = customerService.createCustomer(customer);
       Map<String, String> hasMap = new HashMap<>();
       hasMap.put("response", "created sucessfully!");
       hasMap.put("URI", new URI("/customer?id=" + result.getCustomerId()).toString());

       return new ResponseEntity<>(hasMap, HttpStatus.CREATED);

   }



    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order, 
            @RequestParam(defaultValue = "true") boolean showActive) {

        Pageable paging = null;
        if (order.contains("asc")) {
            paging = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        } else {
            paging = PageRequest.of(page, size, Sort.by(sortBy).descending());
        }

        Page<Customer> pagedResult = customerService.findAll(paging);

        List<Customer> ansList = pagedResult.getContent();

        if (showActive) {
            ansList = ansList.stream().filter(e -> e.getUser().isEnabled()).collect(Collectors.toList());

        }
    

        return ResponseEntity.ok().body(ansList);
    }


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

        
    
    @DeleteMapping("/customer/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) throws RecordNotFoundException {

        customerService.deleteById(id);

        return ResponseEntity
                .noContent()
                .build();

    }
    

     @PatchMapping("/customer/{id}")
    public ResponseEntity<Map<String, String>> partialUpdateCustomer(
            @PathVariable("id") final Long id,
            @RequestBody @Valid CustomerUpdate customer) throws URISyntaxException, RecordNotFoundException {

        Customer result = customerService.partialUpdateCustomer(id, customer);

         Map<String, String> hasMap = new HashMap<>();
          hasMap.put("response", "updated sucessfully!");
       
          hasMap.put("URI", new URI("/customer?id=" + result.getCustomerId()).toString());

        return ResponseEntity.created(new URI("/customer?id=" + result.getCustomerId())).body(hasMap);

    }



}
