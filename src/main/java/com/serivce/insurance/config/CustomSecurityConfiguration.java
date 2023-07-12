package com.serivce.insurance.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import com.serivce.insurance.serviceimpl.LdapUserAuthoritiesPopulator;

import jakarta.servlet.http.HttpServletResponse;


@Configuration
@EnableWebSecurity
public class CustomSecurityConfiguration {

    @Autowired
    private Environment env;
    
    @Autowired
    private LdapUserAuthoritiesPopulator ldapUserAuthoritiesPopulator;

 
       @Bean
       protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
           http.authorizeHttpRequests(rmr -> rmr
                   .requestMatchers("/admin/**").hasRole("admin")
                   .requestMatchers("/swagger-ui/index.html").permitAll()
                   .requestMatchers("/**").authenticated()).sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                   .and().httpBasic();
        
      
   



    return http.build();
       }

        
    @Bean
    LdapAuthenticationProvider ldapAuthenticationProvider() {
        System.out.println("hellllllllllllo");
        return new LdapAuthenticationProvider(authenticator(), ldapUserAuthoritiesPopulator);
    }

    @Bean
    BindAuthenticator authenticator() {
        String searchBase = "dc=insurance,dc=com";
        String filter = env.getProperty("ldap.search.filter");
        assert filter != null;
        System.out.println(filter);

        FilterBasedLdapUserSearch search = new FilterBasedLdapUserSearch(searchBase, filter, contextSource());
        
        BindAuthenticator authenticator = new BindAuthenticator(contextSource());
        authenticator.setUserSearch(search);

        return authenticator;
    }
   @Bean
    public DefaultSpringSecurityContextSource contextSource() {
        DefaultSpringSecurityContextSource defSecCntx = new DefaultSpringSecurityContextSource(env.getProperty("spring.ldap.urls") + "/" + env.getProperty("spring.ldap.base"));
        defSecCntx.setUserDn(env.getProperty("ldap.managerDn") + "," + env.getProperty("spring.ldap.base"));
        defSecCntx.setPassword(env.getProperty("spring.ldap.password"));
        return defSecCntx;
    }


}