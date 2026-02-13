package com.example.TPO.UserManagement.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * Centralized utility for extracting JWT tokens from HTTP requests.
 * Checks the HttpOnly cookie first, then falls back to the Authorization header.
 * Use this across all controllers and services instead of duplicating extraction logic.
 */
@Component
public class TokenExtractor {

    private static final String COOKIE_NAME = "jwt_token";
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Extracts the raw JWT token from the request.
     * Priority: 1) HttpOnly cookie  2) Authorization header (Bearer token)
     *
     * @param request the incoming HTTP request
     * @return the raw JWT token string, or null if not found
     */
    public String extractToken(HttpServletRequest request) {
        // 1. Try extracting from HttpOnly cookie first
        String token = extractFromCookie(request);

        // 2. Fallback to Authorization header
        if (token == null) {
            token = extractFromHeader(request);
        }

        return token;
    }

    /**
     * Strips the "Bearer " prefix from an Authorization header value.
     * Safe to call even if the value doesn't have the prefix.
     *
     * @param authHeader the raw Authorization header value
     * @return the token without the "Bearer " prefix, or null if invalid
     */
    public String stripBearer(String authHeader) {
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length()).trim();
        }
        return authHeader;
    }

    /**
     * Extracts the JWT token from the HttpOnly cookie.
     */
    private String extractFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (COOKIE_NAME.equals(cookie.getName())) {
                    String value = cookie.getValue();
                    if (value != null && !value.trim().isEmpty()) {
                        return value;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Extracts the JWT token from the Authorization header.
     */
    private String extractFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length()).trim();
        }
        return null;
    }
}
