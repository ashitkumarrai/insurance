package com.serivce.insurance.config;


import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.serivce.insurance.serviceimpl.PortalUserService;
import com.serivce.insurance.serviceimpl.UserPrincipal;

import com.serivce.insurance.util.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
	@Value("${jwt.secret}")
	private String jwtSecret;
	@Autowired
	private  PortalUserService portalUserService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		
			Optional<String> token = JwtUtils.getTokenWithoutBearer(request);
			token.ifPresent(t -> {
				
					
						verifyAndAuthenticatePortalUser(request, t);
					
				 
			});
		
			
		
		filterChain.doFilter(request, response);
	}

	private void verifyAndAuthenticatePortalUser(HttpServletRequest request, String token)  {
		
	
				if (JwtUtils.verifyToken(token, jwtSecret)) {
					String username = JwtUtils.extractUsername(token, jwtSecret);
					UserPrincipal principal = (UserPrincipal) portalUserService.loadUserByUsername(username);

					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null,
							principal.getAuthorities());
					auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					SecurityContextHolder.getContext().setAuthentication(auth);
				
 
				
				
				

}
			
}
}
