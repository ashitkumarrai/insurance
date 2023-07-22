package com.serivce.insurance.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.serivce.insurance.entity.User;
import com.serivce.insurance.repository.UserRepository;

@Component
public class CustomUserDetailsService implements UserDetailsService {
       @Autowired
    private UserRepository userRepository;
      @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
		if(!user.isPresent()) {
			throw new UsernameNotFoundException("User NOt Found, "+username);
		}
		return new UserPrincipal(user.get());
      
    }
}
