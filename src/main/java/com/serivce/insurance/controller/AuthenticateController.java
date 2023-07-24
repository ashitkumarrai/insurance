package com.serivce.insurance.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.serivce.insurance.entity.User;
import com.serivce.insurance.exceptionhandler.RecordNotFoundException;
import com.serivce.insurance.exceptionhandler.UserBadCredentialsException;
import com.serivce.insurance.exceptionhandler.UserDisabledException;
import com.serivce.insurance.payload.JwtRequest;
import com.serivce.insurance.payload.JwtResponse;
import com.serivce.insurance.repository.UserRepository;
import com.serivce.insurance.serviceimpl.LdapService;
import com.serivce.insurance.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthenticateController {
    private final LdapService portalUserService;
    private final UserRepository userRepository;

    @PostMapping("/authenticate")
    public ResponseEntity<JwtResponse> authenticate(@RequestBody @NonNull JwtRequest authRequest)
            throws UserDisabledException, UserBadCredentialsException {
        try {
            log.info("Authentication request for user {} received!", authRequest.getUsername());
            if (!userRepository.existsByUsername(authRequest.getUsername())) {
                throw new UsernameNotFoundException(
                        "username: " + authRequest.getUsername() + " not found in database");
            }
            return ResponseEntity
                    .ok(portalUserService.authenticateUser(authRequest.getUsername(), authRequest.getPassword()));
        } catch (DisabledException e) {
            // for disabled user
            throw new UserDisabledException("USER_DISABLED");
        } catch (BadCredentialsException e) {
            // for INVALID_CREDENTIALS
            throw new UserBadCredentialsException("Invalid Credentials");
        }

    }

    @SecurityRequirement(name = "Authorization")
    @GetMapping("/currentLoggedProfile")
    @Operation(operationId = "getAllCustomers", responses = {
            @ApiResponse(responseCode = "401", description = "Unauthorized request"),
            @ApiResponse(responseCode = "403", description = "Forbidden request"),
            @ApiResponse(responseCode = "200", description = "sucessfull") }, description = "get logged in customer", summary = "GET PROFILE OF LOGGED ID USER(can be Agent or Promoter)")
    public ResponseEntity<Object> getProfile()
            throws RecordNotFoundException {

        String username = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new RecordNotFoundException("user not logged in"));

        Optional<User> userr = userRepository.findByUsername(username);
        if (!userr.isPresent()) {
            throw new RecordNotFoundException("logged in User not found");

        }

        return new ResponseEntity<>(userRepository.findByUsername(username)
                .orElseThrow(() -> new RecordNotFoundException("profile not found in db")), HttpStatus.OK);

    }

}
