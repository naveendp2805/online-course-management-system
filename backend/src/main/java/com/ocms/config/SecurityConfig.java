package com.ocms.config;

import com.ocms.auth.filter.JwtAuthenticationFilter;
import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Allow internal error dispatches
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()

                        // Allow error endpoint
                        .requestMatchers("/error").permitAll()

                        // Public registration and login
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/users/register",
                                "/api/auth/login"
                        ).permitAll()

                        // Public course browsing
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/courses",
                                "/api/courses/**"
                        ).permitAll()

                        // Student endpoints
                        .requestMatchers("/api/student/**")
                        .hasRole("STUDENT")

                        // Instructor endpoints
                        .requestMatchers("/api/instructor/**")
                        .hasRole("INSTRUCTOR")

                        // Admin endpoints
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // Remaining endpoints require authentication
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
