package cl.duoc.unheladito.service;

import cl.duoc.unheladito.dto.auth.AuthResponse;
import cl.duoc.unheladito.dto.auth.LoginRequest;
import cl.duoc.unheladito.dto.auth.RegistroRequest;
import cl.duoc.unheladito.dto.auth.UsuarioResponse;
import cl.duoc.unheladito.entity.RolUsuario;
import cl.duoc.unheladito.entity.Usuario;
import cl.duoc.unheladito.exception.DatosDuplicadosException;
import cl.duoc.unheladito.exception.DatosInvalidosException;
import cl.duoc.unheladito.exception.RecursoNoEncontradoException;
import cl.duoc.unheladito.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse registrar(RegistroRequest request) {

        String nombre = request.name().trim();
        String email = normalizarEmail(request.email());
        String password = request.password();

        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new DatosDuplicadosException(
                    "Ese correo ya está registrado."
            );
        }

        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new DatosInvalidosException(
                    "La contraseña supera el límite permitido."
            );
        }

        Usuario usuario = new Usuario(
                nombre,
                email,
                passwordEncoder.encode(password),
                RolUsuario.CLIENTE
        );

        Usuario guardado = usuarioRepository.save(usuario);

        return crearRespuesta(guardado);
    }

    public AuthResponse iniciarSesion(LoginRequest request) {

        String email = normalizarEmail(request.email());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            request.password()
                    )
            );

        } catch (AuthenticationException ex) {
            throw new BadCredentialsException(
                    "Correo o contraseña incorrectos."
            );
        }

        Usuario usuario = usuarioRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Correo o contraseña incorrectos."
                        )
                );

        if (!usuario.isActivo()) {
            throw new BadCredentialsException(
                    "Correo o contraseña incorrectos."
            );
        }

        return crearRespuesta(usuario);
    }
    public UsuarioResponse obtenerPerfil(String email) {

        Usuario usuario = usuarioRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró el usuario autenticado."
                        )
                );

        if (!usuario.isActivo()) {
            throw new BadCredentialsException(
                    "La cuenta no está disponible."
            );
        }

        return UsuarioResponse.fromEntity(usuario);
    }
    private AuthResponse crearRespuesta(Usuario usuario) {

        String token = jwtService.generarAccessToken(usuario);

        UsuarioResponse perfil = UsuarioResponse.fromEntity(usuario);

        return new AuthResponse(
                token,
                "Bearer",
                jwtService.obtenerDuracionEnSegundos(),
                perfil
        );
    }
    private String normalizarEmail(String email) {

        return email.trim().toLowerCase(Locale.ROOT);
    }
}