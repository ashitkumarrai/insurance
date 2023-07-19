package com.serivce.insurance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.serivce.insurance.exceptionhandler.UserBadCredentialsException;
import com.serivce.insurance.exceptionhandler.UserDisabledException;
import com.serivce.insurance.payload.JwtRequest;
import com.serivce.insurance.payload.JwtResponse;
import com.serivce.insurance.serviceimpl.UserDetailServiceImpl;
import com.serivce.insurance.util.JwtTokenUtil;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailServiceImpl userService;



    @PostMapping
    public ResponseEntity<Object> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest)
    {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final UserDetails userDetails = userService.loadUserByUsername(authenticationRequest.getUsername());


        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token,userDetails));
    }

    private void authenticate(String username, String password) throws UserBadCredentialsException,UserDisabledException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new UserDisabledException("USER_DISABLED");
        } catch (BadCredentialsException e) {
            throw new UserBadCredentialsException("INVALID_CREDENTIALS");
        }
    }
}
