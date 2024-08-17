package org.ecommerce.user.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.ecommerce.user.dto.UserResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTokenUtil {

    private static final String SECRET_KEY = "wubbalubbadubdubwubbalubbadubdubwubbalubbadubdub";
    private static final long EXPIRATION_TIME_MS = 7 * 24 * 60 * 60 * 1000; // 7 days

    public static String generateToken(UserResponseBody user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("role",user.getRole())
                .claim("name",user.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static Integer getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

        return Integer.parseInt(claims.getSubject());
    }

    public static Map<String,Object> getDataFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

        Map<String,Object> tokenData= new HashMap<>();
        tokenData.put("role", claims.get("role",String.class));
        tokenData.put("name",claims.get("name",String.class));

        return tokenData;
    }

}
