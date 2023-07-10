package com.serivce.insurance.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.serivce.insurance.entity.User;
import com.serivce.insurance.repository.UserRepository;
@Service
public class UserDetailServiceImpl implements UserDetailsService{
   @Autowired
   private UserRepository ur;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
         Optional<User> user = ur.findByUsername(username);
		if(!user.isPresent()) {
            return new UserDetailsImpl(user.get());
		}
       
            
        return null;
        
        
    }
    
}
