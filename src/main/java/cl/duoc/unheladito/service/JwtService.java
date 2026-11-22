package cl.duoc.unheladito.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import cl.duoc.unheladito.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final long accessTokenMinutes;

    public JwtService(
            JwtEncoder jwtEncoder,
            @Value("${app.security.jwt.issuer}") String issuer,
            @Value("${app.security.jwt.access-token-minutes}") long accessTokenMinutes
    ) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.accessTokenMinutes = accessTokenMinutes;
    }

    public String generarAccessToken(Usuario usuario) {

        Instant ahora = Instant.now();

        Instant expiracion = ahora.plus(
                accessTokenMinutes,
                ChronoUnit.MINUTES
        );

        JwsHeader encabezado = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(ahora)
                .expiresAt(expiracion)
                .subject(usuario.getEmail())
                .claim("userId", usuario.getId())
                .claim("name", usuario.getNombre())
                .claim("roles", List.of(usuario.getRol().name()))
                .build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(encabezado, claims)
        ).getTokenValue();
    }

    public long obtenerDuracionEnSegundos() {

        return ChronoUnit.MINUTES
                .getDuration()
                .multipliedBy(accessTokenMinutes)
                .getSeconds();
    }
}
