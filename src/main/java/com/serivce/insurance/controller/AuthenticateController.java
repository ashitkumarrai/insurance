package com.serivce.insurance.controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.serivce.insurance.exceptionhandler.UserBadCredentialsException;
import com.serivce.insurance.exceptionhandler.UserDisabledException;
import com.serivce.insurance.payload.JwtRequest;
import com.serivce.insurance.payload.JwtResponse;
import com.serivce.insurance.serviceimpl.PortalUserService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthenticateController {
    private final PortalUserService portalUserService;

    @PostMapping("/authenticate")
    public ResponseEntity<JwtResponse> authenticate(@RequestBody @NonNull JwtRequest authRequest)
            throws UserDisabledException, UserBadCredentialsException {
                try {
            log.info("Authentication request for user {} received!", authRequest.getUsername());
            return ResponseEntity.ok(portalUserService.authenticateUser(authRequest.getUsername(), authRequest.getPassword()));
            } catch (DisabledException e) {
			//for disabled user
			throw new UserDisabledException("USER_DISABLED");
		} catch (BadCredentialsException e) {
			// for INVALID_CREDENTIALS
			throw new UserBadCredentialsException("INVALID_CREDENTIALS");
		}
    }
}
