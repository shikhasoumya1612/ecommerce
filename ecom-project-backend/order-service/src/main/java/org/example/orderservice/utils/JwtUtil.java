package org.example.orderservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    private static final String SECRET_KEY = "wubbalubbadubdubwubbalubbadubdubwubbalubbadubdub";

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
        tokenData.put("role",claims.get("role",String.class));
        tokenData.put("name",claims.get("name",String.class));

        return tokenData;
    }

}
