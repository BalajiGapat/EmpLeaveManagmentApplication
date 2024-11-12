package com.rak.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.rak.entity.Role;
import com.rak.security.user.SecurityUser;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

// Step: 2
@Component
@Slf4j
public class JwtUtils 
{
	@Value("${auth.token.jwtSecret}")
	private String jwtSecret;
	
	@Value("${auth.token.expirationinMils}")
	private int jwtExpirationMs;
	
	public String generateJwtTokenForUser(Authentication authentication)
	{
		SecurityUser user= (SecurityUser) authentication.getPrincipal();
		Set<Role> roleSet=user
							.getAuthorities()
							.stream()
							.map(authority->new Role(authority.getAuthority()))
							.collect(Collectors.toSet());

		String token= Jwts
				.builder()
				.subject(user.getUsername())
				.claim("roleSet", roleSet)
				.issuedAt(new Date())
				.expiration(new Date( new Date().getTime()+jwtExpirationMs ))
				.signWith(secretKey())
				.compact();
		
		log.info("Generated JWT token for user: {}", user.getUsername()); // Log token generation
		
		return token;
	}
	
	public Key secretKey()
	{
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}
	
	public String getUsernameFromToken(String token)
	{
		String username= Jwts
				.parser()
				.verifyWith((SecretKey) secretKey())
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
		
		log.info("Extracted username from token: {}", username); // Log username extraction
        return username;
	}
	
	public boolean validateToken(String token)
	{
		try
		{
			Jwts
				.parser()
				.verifyWith((SecretKey) secretKey())
				.build()
				.parse(token);
			log.info("JWT token is validated...!!!"); // Log successful validation
            return true;
		}
		catch(MalformedJwtException ex)
		{
			log.error("invalid jwt token: {} ",ex.getMessage());
		}
		catch(ExpiredJwtException ex)
		{
			log.error("Expired jwt token: {} ", ex.getMessage());
		}
		catch(UnsupportedJwtException ex)
		{
			log.error("This token is not supported: {} ", ex.getMessage());
		}
		catch(IllegalArgumentException ex)
		{
			log.error("No claims found: {} ", ex.getMessage());
		}
		
		return false;
	}
	
	
}
