package com.serivce.insurance.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class CustomSecurityConfiguration {

 
       @Bean
       protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
           http.authorizeHttpRequests(rmr -> rmr
                   .requestMatchers("/admin/**").hasRole("admin")
                   .requestMatchers("/swagger-ui/index.html").permitAll()
                   .requestMatchers("/**").authenticated()).sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                   .and().httpBasic();

    return http.build();
       }
        @Autowired
   public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception
    {
        authenticationManagerBuilder
                .ldapAuthentication()
                .contextSource().url("ldap://localhost:10389")
                .managerDn("uid=admin,ou=system").managerPassword("secret")
                .and()
                .userSearchBase("")
                .userSearchFilter("(uid={0})");
    }


}