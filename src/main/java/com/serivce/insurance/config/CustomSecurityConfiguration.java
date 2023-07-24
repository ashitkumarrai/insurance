package com.serivce.insurance.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;



@Configuration
@EnableWebSecurity
public class CustomSecurityConfiguration {
        
        @Autowired
  private FilterChainExceptionHandler filterChainExceptionHandler;


    
 final String[] WHITELIST_URL = {
    "/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/swagger-resources/configuration/ui",  "/swagger-resources/configuration/security","/swagger-ui/**","/v3/api-docs/**",
   
    "/register/**",
 
    "/media/**",
  
    
    "/show/**",
    "/authenticate"

};

       @Bean
       protected SecurityFilterChain filterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter)
               throws Exception {
           http

                   .csrf(csrf -> csrf.disable()).cors(cors -> cors.disable())

                   .authorizeHttpRequests(auth -> auth
                           .requestMatchers(WHITELIST_URL).permitAll()
                           .requestMatchers(HttpMethod.OPTIONS).permitAll()

                                           .requestMatchers(new AntPathRequestMatcher("/admin/**"))
                                           .hasAnyAuthority("admin")
                                           .requestMatchers(new AntPathRequestMatcher("/user/**"))
                                           .hasAnyAuthority("admin", "user")
                            .requestMatchers(new AntPathRequestMatcher("/policy/**")).hasAnyAuthority("admin","user")

                                           .requestMatchers(new AntPathRequestMatcher("/customers"))
                                           .hasAnyAuthority("admin", "user")
                           .requestMatchers(new AntPathRequestMatcher("/create/customer")).hasAnyAuthority("admin","user")
                                           .requestMatchers(new AntPathRequestMatcher("/customer/delete/**"))
                                           .hasAnyAuthority("admin", "user")
                                           .requestMatchers(new AntPathRequestMatcher("/user/delete/**"))
                                           .hasAnyAuthority("admin")
                           
                           .requestMatchers(new AntPathRequestMatcher("/customer/**"))
                                           .hasAnyAuthority("admin", "user")
                           .requestMatchers(new AntPathRequestMatcher("/users"))
                           .hasAnyAuthority("admin")
                           .anyRequest().authenticated())
                   .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                   .and()
                           .addFilterBefore(filterChainExceptionHandler, LogoutFilter.class);
                 
                   
           http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

           return http.build();
       }
       


}