package com.koi.api.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {

    @Value("${spring.security.jwt.key}")
    String key;

    @Value("${spring.security.jwt.expire}")
    int expire;

    @Resource
    StringRedisTemplate template;

    public DecodedJWT resolveJwt(String headerToken) {
        String token = this.convertToken(headerToken);
        if(token == null) return null;
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            DecodedJWT verify = jwtVerifier.verify(token);
            // verify token is in blacklist
            if(this.isInvalidToken(verify.getId())){
                return null;
            }
            // if token is expired
            Date expireAt = verify.getExpiresAt();
            // if all condition is fine, return the jwt
            return new Date().after(expireAt) ? null : verify;
        }catch (JWTVerificationException e) {
            return null;
        }

    }

    public String createJwt(UserDetails userDetails, int id, String username) {
        Algorithm algorithm = Algorithm.HMAC256(key);
        Date expire = this.expireTime();
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("id", id)
                .withClaim("name", username)
                .withClaim("authorities", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(expire)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    public Date expireTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, expire * 24);
        return calendar.getTime();
    }

    private String convertToken(String headerToken) {
        if(headerToken == null || !headerToken.startsWith("Bearer ")){
            return null;
        }
        return headerToken.substring(7);
    }

    /**
     * To analysis User token
     */
    public UserDetails toUser(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        return User.withUsername(claims.get("name").asString())
                .password("******")
                .authorities(claims.get("authorities").asArray(String.class))
                .build();
    }

    public Integer toId(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        return claims.get("id").asInt();
    }

    //To make token invalid
    public boolean invalidJwt(String headerJwt){
        String token = this.convertToken(headerJwt);
        if(token == null) return false;
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        //
        try {
            DecodedJWT jwt = jwtVerifier.verify(token);
            String id = jwt.getId();
            return deleteToken(id, jwt.getExpiresAt());
        }catch (JWTVerificationException e){
            return false;
        }
    }

    private boolean deleteToken(String uuid, Date expireTime) {
        if(this.isInvalidToken(uuid)){
            return false;
        }
        Date now = new Date();
        // if user logout is using the expired token, below is -value, so need to
        // compare to 0
        long expire = Math.max(expireTime.getTime() - now.getTime(), 0);
        template.opsForValue().set(Const.JWT_BLACK_LIST + uuid, "", expire, TimeUnit.MILLISECONDS);
        return true;
    }

    /**
     * whether the token is expired
     */
    private boolean isInvalidToken (String uuid) {
        return Boolean.TRUE.equals(template.hasKey(Const.JWT_BLACK_LIST + uuid));
    }
}
