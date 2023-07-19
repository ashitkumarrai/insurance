package com.serivce.insurance.config;
import java.io.IOException;
import java.io.Serializable;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

	private static final long serialVersionUID = -7858869558953243875L;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {

		log.info("unauthorizedddd");

		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
	}
	@ExceptionHandler (value = {AccessDeniedException.class})
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException {
    // 401
    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Authentication Failed : " + accessDeniedException.getMessage());
  }
}
