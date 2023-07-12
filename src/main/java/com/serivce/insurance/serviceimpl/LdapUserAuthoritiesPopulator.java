package com.serivce.insurance.serviceimpl;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Component;


@Component
public class LdapUserAuthoritiesPopulator implements LdapAuthoritiesPopulator {
    @Autowired
    private UserDetailServiceImpl userDetailsService;

    private final Log logger = LogFactory.getLog(getClass());

    @Override
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData,
            String username) {
        
             

        
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();

        
         authorities = userDetailsService.loadUserByUsername(username).getAuthorities();
          
          logger.info("LdapUserAuthoritiesPopulator --> " + authorities);
        
        return authorities;

    }

}