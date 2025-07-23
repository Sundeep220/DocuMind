package com.sundeep.user.user_service.util;

import com.sundeep.user.user_service.constants.JwtConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

public class JwtUtil {
    static SecretKey secretKey = Keys.hmacShaKeyFor(JwtConstants.SECRET_KEY.getBytes());
    private String jwtSecret = "dasfhasdkljfhadjklhflajkhfdklasjdlkahkjfghjghjghjgkjhhfgj";
    private long jwtExpirationMs = 86400000L;

    public static String generateTokenAuthentication(Authentication authentication) {
        //TODO: implement this method to generate token for authentication)
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities(); //TODO: get authorities from authentication>
        String roles = populateAuthorities(authorities);
        String jwtToken = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JwtConstants.EXPIRATION_TIME))
                .claim("email", authentication.getName())
                .claim("authorities", roles)
                .signWith(secretKey).compact();
        return jwtToken;
    }
    private static String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
//        StringBuilder sb = new StringBuilder();
//        for (GrantedAuthority authority : authorities) {
//            // TODO: add role to sb
//            sb.append(authority.getAuthority()).append(",");
//        }
//        return sb.toString();
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                .collect(Collectors.joining(","));
    }

    public static String getEmailFromToken(String token) {
        return String.valueOf(Jwts.parser().setSigningKey(secretKey).build().parseSignedClaims(token.substring(7)).getPayload().get("email"));
    }

}