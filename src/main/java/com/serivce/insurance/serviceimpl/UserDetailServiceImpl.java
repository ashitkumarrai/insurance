package com.serivce.insurance.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.serivce.insurance.entity.User;
import com.serivce.insurance.repository.UserRepository;

import lombok.extern.log4j.Log4j2;

@Service

@Log4j2
public class UserDetailServiceImpl implements UserDetailsService{
   @Autowired
   private UserRepository ur;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("helooiosssiosss");
        log.info(username);
        Optional<User> user = ur.findByUsername(username);
        
         if (user.isPresent()) {
             
            return new UserDetailsImpl(user.get());
		}
       
        log.info("adfsasdfdafdasdd");
        return null;
        
        
    }
    
}