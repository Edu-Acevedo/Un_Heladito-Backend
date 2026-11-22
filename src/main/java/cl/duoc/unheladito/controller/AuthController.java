package cl.duoc.unheladito.controller;

import cl.duoc.unheladito.dto.auth.AuthResponse;
import cl.duoc.unheladito.dto.auth.LoginRequest;
import cl.duoc.unheladito.dto.auth.RegistroRequest;
import cl.duoc.unheladito.dto.auth.UsuarioResponse;
import cl.duoc.unheladito.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Registro e inicio de sesión")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo cliente")
    public ResponseEntity<AuthResponse> registrar(
            @Valid @RequestBody RegistroRequest request
    ) {
        AuthResponse respuesta = authService.registrar(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<AuthResponse> iniciarSesion(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse respuesta = authService.iniciarSesion(request);
        return ResponseEntity.ok(respuesta);
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
}