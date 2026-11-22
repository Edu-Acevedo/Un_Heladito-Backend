package cl.duoc.unheladito.controller;

import java.time.Duration;
import cl.duoc.unheladito.dto.auth.AuthResponse;
import cl.duoc.unheladito.dto.auth.AuthSessionResult;
import cl.duoc.unheladito.dto.auth.LoginRequest;
import cl.duoc.unheladito.dto.auth.RegistroRequest;
import cl.duoc.unheladito.dto.auth.UsuarioResponse;
import cl.duoc.unheladito.security.OriginValidator;
import cl.duoc.unheladito.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Gestión de usuarios y sesiones")
public class AuthController {

    private static final String REFRESH_COOKIE = "refreshToken";

    private final AuthService authService;
    private final OriginValidator originValidator;
    private final long refreshTokenDays;
    private final boolean secureCookie;

    public AuthController(
            AuthService authService,
            OriginValidator originValidator,
            @Value("${app.security.refresh-token-days:7}")
            long refreshTokenDays,
            @Value("${app.security.cookie.secure:false}")
            boolean secureCookie
    ) {

        this.authService = authService;
        this.originValidator = originValidator;
        this.refreshTokenDays = refreshTokenDays;
        this.secureCookie = secureCookie;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo cliente")
    public ResponseEntity<AuthResponse> registrar(
            @Valid @RequestBody RegistroRequest request,
            @RequestHeader(value = "Origin", required = false) String origin
    ) {

        originValidator.validar(origin);

        AuthSessionResult sesion = authService.registrar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(
                        HttpHeaders.SET_COOKIE,
                        crearCookieRefresh(sesion.refreshToken()).toString()
                )
                .body(sesion.response());
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<AuthResponse> iniciarSesion(
            @Valid @RequestBody LoginRequest request,
            @RequestHeader(value = "Origin", required = false) String origin
    ) {

        originValidator.validar(origin);

        AuthSessionResult sesion = authService.iniciarSesion(request);

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        crearCookieRefresh(sesion.refreshToken()).toString()
                )
                .body(sesion.response());
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar una sesión mediante refresh token")
    public ResponseEntity<AuthResponse> renovar(
            @CookieValue(
                    value = REFRESH_COOKIE,
                    required = false
            ) String refreshToken,
            @RequestHeader(value = "Origin", required = false) String origin
    ) {

        originValidator.validar(origin);

        AuthSessionResult sesion = authService.renovar(refreshToken);

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        crearCookieRefresh(sesion.refreshToken()).toString()
                )
                .body(sesion.response());
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión y revocar los refresh tokens")
    public ResponseEntity<Void> cerrarSesion(
            @CookieValue(
                    value = REFRESH_COOKIE,
                    required = false
            ) String refreshToken,
            @RequestHeader(value = "Origin", required = false) String origin
    ) {

        originValidator.validar(origin);

        authService.cerrarSesion(refreshToken);

        return ResponseEntity
                .noContent()
                .header(
                        HttpHeaders.SET_COOKIE,
                        eliminarCookieRefresh().toString()
                )
                .build();
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener el perfil del usuario autenticado")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UsuarioResponse> obtenerPerfil(
            @AuthenticationPrincipal Jwt jwt
    ) {

        UsuarioResponse usuario =
                authService.obtenerPerfil(jwt.getSubject());

        return ResponseEntity.ok(usuario);
    }

    private ResponseCookie crearCookieRefresh(String token) {

        return ResponseCookie
                .from(REFRESH_COOKIE, token)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(Duration.ofDays(refreshTokenDays))
                .build();
    }

    private ResponseCookie eliminarCookieRefresh() {

        return ResponseCookie
                .from(REFRESH_COOKIE, "")
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(Duration.ZERO)
                .build();
    }
}