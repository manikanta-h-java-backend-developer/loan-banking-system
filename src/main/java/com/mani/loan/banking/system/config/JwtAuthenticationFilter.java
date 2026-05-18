package com.mani.loan.banking.system.config;

import com.mani.loan.banking.system.service.CustomerUserDetailsService;
import com.mani.loan.banking.system.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomerUserDetailsService customerUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Step 1: Extract JWT from Authorization header
        String authHeader = request.getHeader("Authorization");

        // Step 2: Check If "Bearer " token exists
        if (authHeader == null && authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Skip authentication for this request
            return;
        }

        // Step 3: Extract the token (remove "Bearer " prefix)
        String token = authHeader.substring(7);

        // Step 4: Extract username from token
        String username = jwtUtil.extractUsername(token);

        // Step 5: Validate — only if user not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Step 6: Load user from DB
            UserDetails userDetails = customerUserDetailsService.loadUserByUsername(username);
            // Step 7: Validate token against user
            if (jwtUtil.validateToken(token, userDetails)) {
                // Step 8: Create authentication object
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                // Step 9: Attach request details
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Step 10: Store in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Step 11: Continue the filter chain
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // Skip JWT filter for public endpoints entirely
        return path.startsWith("/actuator") || path.startsWith("/v1/auth");
    }
}
