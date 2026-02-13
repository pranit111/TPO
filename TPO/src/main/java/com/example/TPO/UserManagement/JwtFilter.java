package com.example.TPO.UserManagement;

import com.example.TPO.UserManagement.Service.JWTService;
import com.example.TPO.UserManagement.Service.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenExtractor tokenExtractor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extract token from cookie or Authorization header
        String token = tokenExtractor.extractToken(request);
        String username = null;

        // Extract username from token — catch invalid/expired/mismatched signatures gracefully
        if (token != null) {
            try {
                username = jwtService.extractUser(token);
            } catch (Exception e) {
                // Token is invalid (e.g. server restarted with new signing key, token expired, tampered).
                // Proceed as unauthenticated — do not block the request.
                username = null;
            }
        }

        // Validate token and set authentication context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.validateToken(token, userDetails)) {
                    // Set authentication in security context
                    jwtService.setAuthenticationContext(token, userDetails);
                }
            } catch (Exception e) {
                // Validation failed — proceed as unauthenticated
            }
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
