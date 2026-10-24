package com.lotosia.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Forward the incoming JWT (Authorization header or a cookie named "jwt")
 * unchanged to downstream services. This filter is applied on every route
 * defined in {@link com.lotosia.gateway.config.GatewayRoutesConfig}.
 */
public class JwtForwardFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtForwardFilter.class);
    private static final String AUTHORIZATION = HttpHeaders.AUTHORIZATION;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = extractToken(exchange);
        if (token != null) {
            // Add Bearer token to the outgoing request headers
            exchange = exchange.mutate()
                    .request(req -> req.headers(headers -> headers.setBearerAuth(token)))
                    .build();
            log.debug("Forwarded JWT for {} {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI());
        } else {
            log.warn("No JWT found for request {} {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI());
        }
        return chain.filter(exchange);
    }

    private String extractToken(ServerWebExchange exchange) {
        // Prefer Authorization header
        String header = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        // Fallback to cookie named "jwt"
        if (exchange.getRequest().getCookies().containsKey("jwt")) {
            return exchange.getRequest().getCookies().getFirst("jwt").getValue();
        }
        return null;
    }
}
