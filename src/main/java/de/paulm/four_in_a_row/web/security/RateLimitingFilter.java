package de.paulm.four_in_a_row.web.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import de.paulm.four_in_a_row.web.exceptions.RateLimitExceededException;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final HandlerExceptionResolver resolver;

    public RateLimitingFilter(RateLimitService rateLimitService,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.rateLimitService = rateLimitService;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Identifikation per IP-Adresse
        String ip = request.getRemoteAddr();
        Bucket bucket = rateLimitService.resolveBucket(ip);

        if (bucket.tryConsume(1)) {
            // Token vorhanden -> Anfrage darf passieren
            filterChain.doFilter(request, response);
        } else {
            // Limit erreicht -> Wir "werfen" eine Exception an den Resolver
            resolver.resolveException(request, response, null,
                    new RateLimitExceededException("Too many requests - slow down!"));
        }
    }
}
