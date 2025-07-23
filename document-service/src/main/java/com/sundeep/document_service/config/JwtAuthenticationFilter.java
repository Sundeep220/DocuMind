package com.sundeep.document_service.config;


import com.sundeep.document_service.constants.JwtConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtHeader = request.getHeader(JwtConstants.JWT_HEADER);
        if(jwtHeader != null){
            String jwtToken = jwtHeader.substring(7);
            try{
                SecretKey secretKey = Keys.hmacShaKeyFor(JwtConstants.SECRET_KEY.getBytes());
                Claims claims = Jwts.parser().setSigningKey(secretKey).build().parseSignedClaims(jwtToken).getPayload();
                String email = String.valueOf(claims.get("email"));
                String authorities = String.valueOf(claims.get("authorities"));
//                List<GrantedAuthority> grantedAuthories = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
                List<GrantedAuthority> grantedAuthories = Arrays.stream(authorities.split(","))
                        .filter(auth -> !auth.isBlank())
                        .map(auth -> {
                            // Ensure ROLE_ prefix (if not present)
                            if (auth.startsWith("ROLE_")) {
                                return new SimpleGrantedAuthority(auth);
                            } else {
                                return new SimpleGrantedAuthority("ROLE_" + auth);
                            }
                        })
                        .collect(Collectors.toList());
                Authentication authObj = new UsernamePasswordAuthenticationToken(email, null, grantedAuthories);
                SecurityContextHolder.getContext().setAuthentication(authObj);
            }
            catch (Exception e){
                throw new BadCredentialsException("Invalid token");
            }
        }
        filterChain.doFilter(request, response);
    }
}
