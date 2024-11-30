package com.jeremieferreira.config.security;

import java.io.IOException;
import java.security.Key;
import java.sql.Date;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeremieferreira.feature.util.ApplicationError.Code;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter   {

	// We use auth manager to validate the user credentials
	private AuthenticationManager authManager;
	
	public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authManager) {
		this.authManager = authManager;
		
		// By default, UsernamePasswordAuthenticationFilter listens to "/login" path. 
		// In our case, we use "/auth". So, we need to override the defaults.
		this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(JwtConfig.URI, "POST"));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		try {
			// 1. Get credentials from request
			UserCredentials creds = new ObjectMapper().readValue(request.getInputStream(), UserCredentials.class);
			
			// 2. Create auth object (contains credentials) which will be used by auth manager
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
					creds.getUsername(), creds.getPassword(), Collections.emptyList());
			
			// 3. Authentication manager authenticate the user, and use UserDetialsServiceImpl::loadUserByUsername() method to load the user.
			return authManager.authenticate(authToken);
			
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	// Upon successful authentication, generate a token.
	// The 'auth' passed to successfulAuthentication() is the current authenticated user.
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		
		Long now = System.currentTimeMillis();
		
		Key key = Keys.hmacShaKeyFor(JwtConfig.SECRET.getBytes());
		
		String token = Jwts.builder()
			.setSubject(auth.getName())	
			// Convert to list of strings. 
			// This is important because it affects the way we get them back in the Gateway.
			.claim("authorities", auth.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
			.setIssuedAt(new Date(now))
			.setExpiration(new Date(now + JwtConfig.EXPIRATION * 1000))  // in milliseconds
			.signWith(key)
			//.signWith(SignatureAlgorithm.HS512, JwtConfig.SECRET.getBytes())
			.compact();
		
		// Add token to header
		//response.addHeader(jwtConfig.getHeader(), jwtConfig.getPrefix() + token);
		//request.getSession().setAttribute(UserService.SESSION_ATTRIBUTE_USER, auth.getPrincipal());
		
		response.addHeader("Content-Type", "application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write("{\"Authorization\": \"" + JwtConfig.PREFIX + token + "\"}");
		response.getWriter().flush();
		response.getWriter().close();
	}
	
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		response.addHeader("Content-Type", "application/json");
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.getWriter().write("{\"code\": \"" + Code.AUTHENTICATION_FAILED.name() + "\"}");
		response.getWriter().flush();
		response.getWriter().close();
	}
	
	// A (temporary) class just to represent the user credentials
	private static class UserCredentials {
	    private String username, password;
	    
	    public String getUsername() {
			return username;
		}
	    
	    public String getPassword() {
			return password;
		}
	}
}