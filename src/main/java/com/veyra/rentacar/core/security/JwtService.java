package com.veyra.rentacar.core.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Service
public class JwtService {

    private final NimbusJwtDecoder jwtDecoder;
    private final NimbusJwtEncoder jwtEncoder;
    private final long expirationMs;

    // @Value ile alınan config değerlerinden SecretKey üretmek için
    // manuel constructor gerekli — @RequiredArgsConstructor bu durumu karşılamaz
    public JwtService(
            @Value("${veyra.security.jwt.secret-key}") String secretKey,
            @Value("${veyra.security.jwt.expiration-ms}") long expirationMs) {

        SecretKey key = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        this.jwtDecoder = NimbusJwtDecoder.withSecretKey(key).build();
        this.jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
        this.expirationMs = expirationMs;
    }

    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiresAt(now.plusMillis(expirationMs))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public String extractEmail(String token) {
        try {
            return jwtDecoder.decode(token).getSubject();
        } catch (org.springframework.security.oauth2.jwt.JwtException | IllegalArgumentException ex) {
            return null;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            var jwt = jwtDecoder.decode(token);
            String email = jwt.getSubject();
            Instant expiry = jwt.getExpiresAt();
            return email.equals(userDetails.getUsername())
                    && expiry != null
                    && expiry.isAfter(Instant.now());
        } catch (org.springframework.security.oauth2.jwt.JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
