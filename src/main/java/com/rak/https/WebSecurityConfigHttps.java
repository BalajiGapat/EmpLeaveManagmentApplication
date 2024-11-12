//package com.rak.https;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class WebSecurityConfigHttps 
//{
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception 
//    {
//        http
//            // Enforce HTTPS by setting HSTS (HTTP Strict Transport Security)
//            .headers(headers -> headers
//                .httpStrictTransportSecurity(hsts -> hsts
//                    .includeSubDomains(true)
//                    .preload(true)
//                    .maxAgeInSeconds(31536000) // 1 year (standard max age for HSTS)
//                )
//            )
//            // Disable HTTP (allow only HTTPS)
//            .requiresChannel(channel -> channel
//                .anyRequest().requiresSecure() // Ensure all requests go over HTTPS
//            );
//
//        return http.build();
//    }
//}
