package tn.esprit.microservicerectification.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwtToken = extractTokenFromRequest(request);

        if (jwtToken != null && !jwtToken.isEmpty()) {
            try {
                String username = JwtUtils.extractUsername(jwtToken);
                String role = JwtUtils.extractRole(jwtToken);

                System.out.println("JWT Debug - Username: " + username + ", Role: " + role);

                if (username != null && JwtUtils.validateToken(jwtToken) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Create authorities based on the role from JWT
                    // Convert role to match @PreAuthorize expectations: "enseignant" -> "ROLE_ENSEIGNANT"
                    String normalizedRole = role.toUpperCase().replace(" ", "_");
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + normalizedRole);

                    System.out.println("JWT Debug - Creating authority: " + authority.getAuthority());

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, null, Collections.singletonList(authority));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    System.out.println("JWT Debug - Authentication set successfully for user: " + username);
                }
            } catch (Exception e) {
                // Log the error and continue without authentication
                System.err.println("JWT processing error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
