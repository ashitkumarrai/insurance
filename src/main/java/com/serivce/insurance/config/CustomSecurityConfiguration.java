package com.serivce.insurance.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.web.SecurityFilterChain;

import com.serivce.insurance.serviceimpl.LdapUserAuthoritiesPopulator;


@Configuration
@EnableWebSecurity
public class CustomSecurityConfiguration {

 @Autowired
    private LdapUserAuthoritiesPopulator ldapUserAuthoritiesPopulator;

       @Bean
       protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
           http.authorizeHttpRequests(rmr -> rmr
                   .requestMatchers("/admin/**").hasAnyAuthority("admin")
                   .requestMatchers("/customers").hasAnyAuthority("admin")
                   .requestMatchers("/swagger-ui/index.html").permitAll()
                   .requestMatchers("/**").authenticated()).sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                   .and().httpBasic();

    return http.build();
       }
//         @Autowired
//    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception
//     {
//         authenticationManagerBuilder
//                 .ldapAuthentication()
//                 .contextSource().url("ldap://localhost:10389")
//                 .managerDn("uid=admin,ou=system").managerPassword("secret")
//                 .and()
//                 .userSearchBase("")
//                 .userSearchFilter("(uid={0})");
//     }



 @Bean
    LdapAuthenticationProvider ldapAuthenticationProvider() {
        return new LdapAuthenticationProvider(authenticator(), ldapUserAuthoritiesPopulator);
    }

    @Bean
    BindAuthenticator authenticator() {
        String searchBase = "";
        String filter = "(uid={0})";
        assert filter != null;

        FilterBasedLdapUserSearch search = new FilterBasedLdapUserSearch(searchBase, filter, contextSource());

        BindAuthenticator authenticator = new BindAuthenticator(contextSource());
        authenticator.setUserSearch(search);

        return authenticator;
    }

    @Bean
    public DefaultSpringSecurityContextSource contextSource() {
        DefaultSpringSecurityContextSource defSecCntx = new DefaultSpringSecurityContextSource("ldap://localhost:10389");
        defSecCntx.setUserDn("uid=admin,ou=system");
        defSecCntx.setPassword("secret");
        return defSecCntx;
    }




}