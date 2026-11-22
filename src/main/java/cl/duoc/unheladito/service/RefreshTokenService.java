package cl.duoc.unheladito.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;
import cl.duoc.unheladito.entity.RefreshToken;
import cl.duoc.unheladito.entity.Usuario;
import cl.duoc.unheladito.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshTokenDays;

    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${app.security.refresh-token-days:7}")
            long refreshTokenDays
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenDays = refreshTokenDays;
    }

    @Transactional
    public String crear(Usuario usuario) {

        Instant ahora = Instant.now();

        String tokenOriginal = generarToken();
        String hash = calcularHash(tokenOriginal);

        String familia = UUID.randomUUID().toString();

        RefreshToken registro = new RefreshToken(
                usuario,
                hash,
                familia,
                ahora.plus(refreshTokenDays, ChronoUnit.DAYS)
        );

        refreshTokenRepository.save(registro);

        return tokenOriginal;
    }

    @Transactional
    public Optional<Rotacion> rotar(String tokenOriginal) {

        if (tokenOriginal == null
                || tokenOriginal.isBlank()
                || tokenOriginal.length() > 200) {
            return Optional.empty();
        }

        String hash = calcularHash(tokenOriginal);

        RefreshToken actual = refreshTokenRepository
                .buscarParaRenovar(hash)
                .orElse(null);

        if (actual == null) {
            return Optional.empty();
        }

        Instant ahora = Instant.now();

        if (actual.getRevocadoEn() != null) {

            refreshTokenRepository.revocarFamilia(
                    actual.getFamilyId(),
                    ahora
            );

            return Optional.empty();
        }

        // Una sesión vencida no puede renovarse.
        if (!actual.getExpiraEn().isAfter(ahora)) {

            refreshTokenRepository.revocarFamilia(
                    actual.getFamilyId(),
                    ahora
            );

            return Optional.empty();
        }

        if (!actual.getUsuario().isActivo()) {

            refreshTokenRepository.revocarFamilia(
                    actual.getFamilyId(),
                    ahora
            );

            return Optional.empty();
        }

        actual.revocar(ahora);
        refreshTokenRepository.save(actual);

        String nuevoToken = generarToken();

        RefreshToken nuevoRegistro = new RefreshToken(
                actual.getUsuario(),
                calcularHash(nuevoToken),
                actual.getFamilyId(),
                ahora.plus(refreshTokenDays, ChronoUnit.DAYS)
        );

        refreshTokenRepository.save(nuevoRegistro);

        return Optional.of(
                new Rotacion(actual.getUsuario(), nuevoToken)
        );
    }

    @Transactional
    public void revocar(String tokenOriginal) {

        if (tokenOriginal == null
                || tokenOriginal.isBlank()
                || tokenOriginal.length() > 200) {
            return;
        }

        String hash = calcularHash(tokenOriginal);

        RefreshToken token = refreshTokenRepository
                .buscarParaRenovar(hash)
                .orElse(null);

        if (token == null) {
            return;
        }

        refreshTokenRepository.revocarFamilia(
                token.getFamilyId(),
                Instant.now()
        );
    }

    private String generarToken() {

        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String calcularHash(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] resultado = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(resultado);

        } catch (NoSuchAlgorithmException ex) {

            throw new IllegalStateException(
                    "No se pudo calcular el hash del refresh token.",
                    ex
            );
        }
    }

    public record Rotacion(
            Usuario usuario,
            String tokenNuevo
    ) {
    }
}
