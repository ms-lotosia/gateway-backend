package com.lotosia.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Senior‑level route definition for the API gateway.
 * All downstream services receive the original JWT unchanged.
 * Each route has retry and circuit‑breaker policies applied.
 */
@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                // USER SERVICE -----------------------------------------------------
                .route("user-service", r -> r.path("/api/v1/users/**")
                        .uri("${USER_SERVICE_URL}")
                        .filters(f -> f
                                .retry(3)
                                .circuitBreaker(c -> c
                                        .setName("userCircuit")
                                        .setFallbackUri("forward:/fallback/user"))
                                .filter(new com.lotosia.gateway.filter.JwtForwardFilter())))
                // CATALOG BACKEND -------------------------------------------------
                .route("catalog-backend", r -> r.path("/api/v1/catalog/**")
                        .uri("${CATALOG_SERVICE_URL}")
                        .filters(f -> f
                                .retry(2)
                                .circuitBreaker(c -> c
                                        .setName("catalogCircuit")
                                        .setFallbackUri("forward:/fallback/catalog"))
                                .filter(new com.lotosia.gateway.filter.JwtForwardFilter())))
                // FAVORITE BACKEND ------------------------------------------------
                .route("favorite-backend", r -> r.path("/api/v1/favorites/**")
                        .uri("${FAVORITE_SERVICE_URL}")
                        .filters(f -> f
                                .retry(2)
                                .circuitBreaker(c -> c
                                        .setName("favoriteCircuit")
                                        .setFallbackUri("forward:/fallback/favorite"))
                                .filter(new com.lotosia.gateway.filter.JwtForwardFilter())))
                // CART BACKEND ----------------------------------------------------
                .route("cart-backend", r -> r.path("/api/v1/cart/**")
                        .uri("${CART_SERVICE_URL}")
                        .filters(f -> f
                                .retry(2)
                                .circuitBreaker(c -> c
                                        .setName("cartCircuit")
                                        .setFallbackUri("forward:/fallback/cart"))
                                .filter(new com.lotosia.gateway.filter.JwtForwardFilter())))
                // ORDER BACKEND ---------------------------------------------------
                .route("order-backend", r -> r.path("/api/v1/orders/**")
                        .uri("${ORDER_SERVICE_URL}")
                        .filters(f -> f
                                .retry(2)
                                .circuitBreaker(c -> c
                                        .setName("orderCircuit")
                                        .setFallbackUri("forward:/fallback/order"))
                                .filter(new com.lotosia.gateway.filter.JwtForwardFilter())))
                // ADMIN ROUTES (any path under /api/v1/admin) ---------------------
                .route("admin", r -> r.path("/api/v1/admin/**")
                        .uri("${ADMIN_SERVICE_URL}")
                        .filter(new com.lotosia.gateway.filter.JwtForwardFilter()))
                .build();
    }
}
