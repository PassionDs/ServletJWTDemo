package com.revature.service;

import com.revature.model.AuthenticatedUser;
import com.revature.model.UserDetails;
import com.revature.utils.CurrentUserThreadLocal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.InvalidKeyException;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

public class TokenServiceImpl implements TokenService {

    /**
     * token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 1天超时时间
     */
    private static final long TOKEN_EXPIRY = 1000 * 60 * 60 * 24 * 365;
    private static final TokenService INSTANCE = new TokenServiceImpl();
    private final Logger logger = LogManager.getLogger(getClass());
    private final KeyPair keyPair;

    /**
     * 是否使用jks文件开关
     */
    private static final boolean IS_USE_JKS = false;

    private TokenServiceImpl() {
        try {
            KeyStore keyStore = KeyStore.getInstance("jks");
            keyStore.load(TokenServiceImpl.class.getClassLoader().getResourceAsStream("jwt.jks"),
                    "Password123!".toCharArray());
            Key key = keyStore.getKey("mxt_jwt", "Password123!".toCharArray());
            if (key instanceof PrivateKey) {
                // Get the certificate
                Certificate cert = keyStore.getCertificate("mxt_jwt");

                // Get Public Key
                PublicKey pubKey = cert.getPublicKey();
                this.keyPair = new KeyPair(pubKey, (PrivateKey) key);
            } else {
                throw new IOException("Failed to read KeyStore");
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
            logger.error("Failed to generate KeyPair: {}", e);
            throw new RuntimeException(e);
        }
    }

    public static TokenService getInstance() {
        return INSTANCE;
    }

    @Override
    public String generateToken(UserDetails details) {
        if (details == null) {
            throw new IllegalArgumentException("User details must not be null");
        }
        final Date now = new Date();
        final String jti = UUID.randomUUID().toString();
        logger.info("Generating JWT for {}", details.getUsername());
        if (IS_USE_JKS) {
            return Jwts.builder().signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                    .setSubject(details.getUsername())
                    .claim("email", details.getEmail())
                    .claim("roles", String.join(",", details.getRoles()))
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + TOKEN_EXPIRY))
                    .setId(jti)
                    .compact();
        } else {
            try {
                return Jwts.builder().signWith(generalKey(), SignatureAlgorithm.HS256)
                        .setSubject(details.getUsername())
                        .claim("email", details.getEmail())
                        .claim("roles", String.join(",", details.getRoles()))
                        .setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + TOKEN_EXPIRY))
                        .setId(jti)
                        .compact();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Claims claims;
            if (IS_USE_JKS) {
                claims = Jwts.parser().setSigningKey(keyPair.getPublic()).parseClaimsJws(token.replace(TOKEN_PREFIX,
                        "")).getBody();
            } else {
                claims =
                        Jwts.parser().setSigningKey(generalKey()).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody();
            }
            CurrentUserThreadLocal.set(new AuthenticatedUser(claims.getSubject(), (String) claims.get("email"),
                    Arrays.stream(claims.get("roles").toString().split(",")).collect(Collectors.toList())));
            return true;
        } catch (Exception e) {
            logger.error("JWT validation failed at {}. Exception was {}",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")),
                    e.getClass().getName());
        }
        return false;
    }

    @Override
    public UserDetails getUserDetailsFromToken(String token) {
        if (token != null && token.startsWith(TOKEN_PREFIX)) {
            Claims claims;
            if (IS_USE_JKS) {
                claims = Jwts.parser().setSigningKey(keyPair.getPublic()).parseClaimsJws(token.replace
                        (TOKEN_PREFIX, "")).getBody();
            } else {
                claims = Jwts.parser().setSigningKey(generalKey()).parseClaimsJws(token.replace(TOKEN_PREFIX,
                        "")).getBody();
            }

            return new AuthenticatedUser(claims.getSubject(), (String) claims.get("email"), Arrays.stream(claims.get(
                    "roles").toString().split(",")).collect(Collectors.toList()));
        }
        return null;
    }

    @Override
    public String getTokenId(String token) {
        if (token != null && token.startsWith(TOKEN_PREFIX)) {
            Claims claims = Jwts.parser().setSigningKey(keyPair.getPublic()).parseClaimsJws(token.replace(TOKEN_PREFIX,
                    "")).getBody();
            return claims.getId();
        }
        return null;
    }

    private static final String KEY = "abcdefghijklmnopqrstuvwxyz12345678900987654321zyxwvutsrqponmlkjihgfedcba";

    /**
     * 由字符串生成加密key
     *
     * @return javax.crypto.SecretKey
     * @date 2021/8/11 18:57
     * @author di.mao
     */
    private static SecretKey generalKey() {
        try {
            byte[] encodedKey = Base64.decodeBase64(KEY);
            return new SecretKeySpec(encodedKey, 0, encodedKey.length, "HmacSHA256");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
