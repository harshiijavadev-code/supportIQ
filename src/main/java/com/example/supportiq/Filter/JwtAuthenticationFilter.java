package com.example.supportiq.Filter;

import com.example.supportiq.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Step 1: Get the Authorization header
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Step 2: Check if header exists and starts with "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Remove "Bearer " prefix
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // Token is invalid or expired
                System.err.println("JWT Token validation error: " + e.getMessage());
            }
        }

        // Step 3: If token is valid and user is not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Validate token
            if (jwtUtil.validateToken(jwt, username)) {

                // Extract role from token
                String role = jwtUtil.extractRole(jwt);

                // Create authentication object
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Step 4: Continue with the request
        filterChain.doFilter(request, response);
    }
}