package com.lotosia.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Foundation security configuration for the API gateway.
 * <p>
 * - Disables CSRF (stateless JSON APIs).
 * - Public access to health and all actuator endpoints.
 * - All other requests require a valid JWT bearer token (validated by downstream services).
 * - Enables method-level security annotations (e.g., @PreAuthorize) for reactive handlers.
 */
@Configuration
@EnableMethodSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/health", "/actuator/**").permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt())
                .build();
    }
}
