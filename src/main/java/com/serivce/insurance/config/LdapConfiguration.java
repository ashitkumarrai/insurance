package com.serivce.insurance.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;


@Configuration
public class LdapConfiguration {

  @Autowired
  private UserDetailsService userDetailsService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(rmr -> rmr
        .requestMatchers("/admin/**").hasRole("admin")
        .requestMatchers("/**").authenticated())
        .httpBasic();

    return http.build();
  }

  @Autowired
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .ldapAuthentication()
        .userDnPatterns("uid={0},ou=people")
        .groupSearchBase("ou=groups")
        .contextSource()
        .url("ldap://localhost:8389/dc=springframework,dc=org")
        .and()
        .passwordCompare()
        .passwordEncoder(new BCryptPasswordEncoder())
        .passwordAttribute("userPassword");
  }

  @Bean
  public UserDetailsContextMapper userDetailsContextMapper() {
    return new LdapUserDetailsMapper() {
      @Override
      public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
          Collection<? extends GrantedAuthority> authorities) {
        UserDetails details = userDetailsService.loadUserByUsername(username);
        return details;
      }
    };

  }
}

