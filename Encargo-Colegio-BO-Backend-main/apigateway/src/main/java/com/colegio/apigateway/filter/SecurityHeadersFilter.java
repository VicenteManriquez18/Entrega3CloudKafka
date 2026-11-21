package com.colegio.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            HttpHeaders headers = exchange.getResponse().getHeaders();

            // Prevenir Clickjacking
            if (!headers.containsKey("X-Frame-Options")) {
                headers.add("X-Frame-Options", "DENY");
            }

            // Prevenir MIME type sniffing
            if (!headers.containsKey("X-Content-Type-Options")) {
                headers.add("X-Content-Type-Options", "nosniff");
            }

            // Protección XSS adicional para navegadores
            if (!headers.containsKey("X-XSS-Protection")) {
                headers.add("X-XSS-Protection", "1; mode=block");
            }

            // Strict-Transport-Security (HSTS) - Forzar HTTPS por 1 año
            if (!headers.containsKey("Strict-Transport-Security")) {
                headers.add("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
            }

            // Content-Security-Policy (CSP) restrictiva
            if (!headers.containsKey("Content-Security-Policy")) {
                headers.add("Content-Security-Policy",
                        "default-src 'self'; " +
                                "script-src 'self' 'unsafe-inline'; " +
                                "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
                                "font-src 'self' https://fonts.gstatic.com data:; " +
                                "img-src 'self' data: https:; " +
                                "connect-src 'self' http://localhost:* ws://localhost:*; " +
                                "frame-ancestors 'none';");
            }

            // Referrer Policy
            if (!headers.containsKey("Referrer-Policy")) {
                headers.add("Referrer-Policy", "strict-origin-when-cross-origin");
            }

            // Permissions Policy
            if (!headers.containsKey("Permissions-Policy")) {
                headers.add("Permissions-Policy", "camera=(), microphone=(), geolocation=(), payment=()");
            }

            // Ocultar servidor backend
            headers.remove("Server");
        }));
    }

    @Override
    public int getOrder() {
        // Ejecución con alta prioridad en la cadena de filtros
        return Ordered.LOWEST_PRECEDENCE;
    }
}
