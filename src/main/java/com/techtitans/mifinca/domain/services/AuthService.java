package com.techtitans.mifinca.domain.services;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.techtitans.mifinca.domain.dtos.AccessTokenDTO;
import com.techtitans.mifinca.domain.dtos.AuthDTO;
import com.techtitans.mifinca.domain.entities.AccountEntity;
import com.techtitans.mifinca.domain.entities.Role;
import com.techtitans.mifinca.domain.entities.SessionEntity;
import com.techtitans.mifinca.domain.exceptions.ApiError;
import com.techtitans.mifinca.domain.exceptions.ApiException;
import com.techtitans.mifinca.repository.SessionRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.security.auth.message.AuthException;

public class AuthService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${security.refresh.expiration}")
    private long refreshExpiration;

    @Autowired
    private SessionRepository repo;

    public AccessTokenDTO createSession(AccountEntity account){
        UUID sesionToken = UUID.randomUUID();
        var session = SessionEntity.builder()
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusSeconds(refreshExpiration))
            .token(sesionToken)
            .user(account)
            .build();
        repo.save(session);

        String jwtAccess = buildJWTToken(account);
        return new AccessTokenDTO(jwtAccess, sesionToken.toString());
    }

    public AuthDTO extractJWTPayload(String token){
        final Claims jwtToken = Jwts.parser()
            .verifyWith(getKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
        
        var userId = UUID.fromString(jwtToken.getId());
        Role role = (Role) jwtToken.get("ROLE");
        if(jwtToken.getExpiration().before(new Date(System.currentTimeMillis()))){
            throw new ApiException(ApiError.EXPIRED_TOKEN);
        }
        return new AuthDTO(userId, role);

    }

    public AccessTokenDTO refreshAccessToken(String refreshToken){
        SessionEntity session = repo.findByToken(UUID.fromString(refreshToken)).orElse(null);
        if(session == null){
            throw new ApiException(ApiError.INVALID_TOKEN);
        }

        //f refresh token already expired
        if(session.getExpiresAt().isBefore(LocalDateTime.now())){
            repo.deleteById(session.getId());
            throw new ApiException(ApiError.EXPIRED_TOKEN);
        }
        //if everything was fine then we simply create thenew access token
        String accessToken = buildJWTToken(session.getUser());
        return new AccessTokenDTO(accessToken, session.getToken().toString());
    }

    private String buildJWTToken(AccountEntity account){
        return Jwts.builder()
            .id(account.getId().toString())
            .claims(getClaims(account))
            .subject(account.getEmail())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(getKey())
            .compact();
    }

    private SecretKey getKey(){
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    private Map<String, ?> getClaims(AccountEntity acc){
        return Map.of(
            "ROLE", acc.getRole()
        );
    }
}
