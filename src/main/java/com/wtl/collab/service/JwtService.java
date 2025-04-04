package com.wtl.collab.service;


import com.wtl.collab.exception.ExpiredJwt;
import com.wtl.collab.exception.ResourceNotFound;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwtSecret}")
    private String secretKey;

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

//    public JwtService() {
//        try {
//            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
//            SecretKey sk = keyGen.generateKey();
//            secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
//
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//    }


    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        String token =  Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration((new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 15 )))
                .and()
                .signWith(key())
                .compact();
        System.out.println(token);
        return token;

    }

//    public static SecretKey getKey() {
//        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
//    }

    public String getUsernameFromToken(String token) throws Exception{
        return extractClaims(token, Claims::getSubject);
    }

    public boolean validateToken(String token, UserDetails userDetails) throws Exception{
        final String userName = getUsernameFromToken(token);

        if(isTokenExpired(token)) throw new ExpiredJwt("JWT has expired, login again");
        return (userName.equals(userDetails.getUsername()));

    }

    private <T> T extractClaims(String token, Function<Claims, T> claimResolver) throws Exception {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) throws Exception{
            return Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
    }

    private boolean isTokenExpired(String token) throws Exception{
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) throws Exception {
        return extractClaims(token, Claims::getExpiration);
    }
}
