package com.rak.security.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rak.security.user.SecurityUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Step: 3
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtOnceperRequestFilter extends OncePerRequestFilter
{
	private final JwtUtils jwtUtils;
	private final SecurityUserDetailsService securityUserDetailsService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException 
	{
		try
		{
			String jwt=parseJwt(request);
			log.info("jwt_token: {} ",jwt);
			
			if(jwt!=null && jwtUtils.validateToken(jwt))
			{
				String companyMailId=jwtUtils.getUsernameFromToken(jwt);
				log.info("Valid JWT User: {}", companyMailId); // Log valid JWT and associated user
				
				UserDetails userDetails=securityUserDetailsService.loadUserByUsername(companyMailId);
				
				UsernamePasswordAuthenticationToken authenitcation=new UsernamePasswordAuthenticationToken(companyMailId, null, userDetails.getAuthorities());
				authenitcation.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authenitcation);
				
				log.info("User authentication set in security context for user: {} whose roles are: {} ", companyMailId, userDetails.getAuthorities()); // Log when authentication is set
			}
		}
		catch(Exception ex)
		{
			log.error("cannot set user authentication: {} ", ex.getMessage());
		}
		
		filterChain.doFilter(request, response);
	}
	
	private String parseJwt(HttpServletRequest request)
	{
		String authorizationHeader=request.getHeader("Authorization");
		if(StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer "))
		{
			return authorizationHeader.substring(7);
		}
		return null;
	}
	
	
}
