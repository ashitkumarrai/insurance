package com.serivce.insurance.controller;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import com.serivce.insurance.entity.User;
import com.serivce.insurance.exceptionhandler.RecordNotFoundException;
import com.serivce.insurance.payload.UserCreationForm;
import com.serivce.insurance.payload.UserFindAllData;
import com.serivce.insurance.payload.UserUpdate;
import com.serivce.insurance.repository.CustomerRepository;
import com.serivce.insurance.repository.UserRepository;
import com.serivce.insurance.serviceimpl.UserService;
import com.serivce.insurance.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;


@RestController
@Log4j2
public class UserController {


    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    CustomerRepository customerRepository;

  

    
     @Tag(name = "1. User(Agent,Promoter,etc) endpoints")
    @Operation(operationId = "createUser", responses = {
     @ApiResponse(responseCode = "401", description = "Unauthorized request" ),
    @ApiResponse(responseCode = "403", description = "Forbidden request"),
            @ApiResponse(responseCode = "201", description = "created sucessfully") }, description = "register user", summary = "CREATE/REGISTER user")
    @PostMapping("/register/user")
public ResponseEntity<Map<String, String>> createUser(@RequestBody @Valid UserCreationForm user)
           throws URISyntaxException,NoSuchAlgorithmException, javax.naming.NamingException, NamingException {

       User result = userService.createUser(user);
       Map<String, String> hasMap = new HashMap<>();
       hasMap.put("response", "created sucessfully!");
       hasMap.put("URI", new URI("/user?id=" + result.getId()).toString());

       return new ResponseEntity<>(hasMap, HttpStatus.CREATED);

   }


@SecurityRequirement(name="Authorization")
@GetMapping("/users")
@Tag(name = "2. Admin endpoints")
    @Operation(operationId = "getAllUsers", responses = {
     @ApiResponse(responseCode = "401", description = "Unauthorized request" ),
    @ApiResponse(responseCode = "403", description = "Forbidden request"),
    @ApiResponse(responseCode = "200", description = "sucessfull") },description = "get all user",summary = "GET ALL USERS")
    public ResponseEntity<UserFindAllData> getAllUsers(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order) {

        Pageable paging = null;
        if (order.contains("asc")) {
            paging = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        } else {
            paging = PageRequest.of(page, size, Sort.by(sortBy).descending());
        }

        Page<User> pagedResult = userService.findAll(paging);

       

        
        
    UserFindAllData response = new UserFindAllData();
    response.setUser(pagedResult.getContent());
    response.setPage(pagedResult.getNumber());
    response.setPageSize(pagedResult.getSize());
    response.setTotalElements(pagedResult.getTotalElements());
    response.setTotalPages(pagedResult.getTotalPages());

  

        return ResponseEntity.ok().body(response);
    }

    @SecurityRequirement(name = "Authorization")
    @Tag(name = "1. User(Agent,Promoter,etc) endpoints")
         @Operation(operationId = "getAllUsers", responses = {
     @ApiResponse(responseCode = "401", description = "Unauthorized request" ),
    @ApiResponse(responseCode = "403", description = "Forbidden request"),
    @ApiResponse(responseCode = "200", description = "sucessfull") },description = "get user id or usename",summary = "GET USERS BY ID OR userFullName OR username")
    @GetMapping("/user")
    public ResponseEntity<Object> getUserById(@RequestParam(defaultValue = "0") Long userId,
            @RequestParam(defaultValue = "default") String userFullName,
            @RequestParam(defaultValue = "default") String username) throws RecordNotFoundException {

        if (userId != 0l) {
            log.info("request param id catch in getuser by id method");
            return ResponseEntity.ok(userService.findById(userId));

        }

        else if (!userFullName.equals("default")) {
            return ResponseEntity.ok(userRepository.findByFullNameIgnoreCase(userFullName));

        } else if (!username.equals("defualt")) {
            return ResponseEntity.ok(userRepository.findByUsername(username));
        }

        return ResponseEntity.ok(userService.findById(userId));

    }
    
    @SecurityRequirement(name = "Authorization")
    @Tag(name = "1. User(Agent,Promoter,etc) endpoints")
         @Operation(operationId = "getAllUsers", responses = {
     @ApiResponse(responseCode = "401", description = "Unauthorized request" ),
    @ApiResponse(responseCode = "403", description = "Forbidden request"),
    @ApiResponse(responseCode = "200", description = "sucessfull") },description = "SHOW LIST OF ALL CUSTOMERS CREATED BY THIS USER",summary = "GET CUSTOMERS CREATED BY THIS USER")
    @GetMapping("/user/createdCustomer")
    public ResponseEntity<List<Customer>> getCustomersByUserId() throws RecordNotFoundException {

        String username = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new RecordNotFoundException("user not logged in"));

        Optional<User> userr = userRepository.findByUsername(username);
        if (!userr.isPresent()) {
            throw new RecordNotFoundException("logged in User not found");

        }

        return ResponseEntity.ok(customerRepository.findByCreatedByUserId(userr.get().getId()));

    }

       
    @SecurityRequirement(name = "Authorization")
    @Tag(name="2. Admin endpoints")
    @DeleteMapping("/user/delete/{id}")
    
    @Operation(operationId = "deleteUser", responses = {
     @ApiResponse(responseCode = "401", description = "Unauthorized request" ),
    @ApiResponse(responseCode = "403", description = "Forbidden request"),
    @ApiResponse(responseCode = "204", description = "deleted sucessfully") },description = " delete user by id",summary = "DELETE USER BY ID")

    public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws RecordNotFoundException {

        userService.deleteById(id);

        return ResponseEntity
                .noContent()
                .build();

    }
    
    @SecurityRequirement(name = "Authorization")
          @Operation(operationId = "partialUpdateUser", responses = {
     @ApiResponse(responseCode = "401", description = "Unauthorized request" ),
    @ApiResponse(responseCode = "403", description = "Forbidden request"),
    @ApiResponse(responseCode = "201", description = "sucessfull") },description = "udate user ",summary = "UPDATE  USER")
       @PatchMapping("/user/{id}")
       @Tag(name = "1. User(Agent,Promoter,etc) endpoints") 
    public ResponseEntity<Map<String, String>> partialUpdateUser(
            @RequestBody @Valid UserUpdate user, @PathVariable Long userId) throws URISyntaxException, RecordNotFoundException {
              
        User result = userService.partialUpdateUser(userId, user);

        Map<String, String> hasMap = new HashMap<>();
        hasMap.put("response", "updated sucessfully!");

        hasMap.put("URI", new URI("/user?id=" + result.getId()).toString());

        return ResponseEntity.created(new URI("/user?id=" + result.getId())).body(hasMap);

    }

     


}

