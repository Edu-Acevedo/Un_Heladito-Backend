package cl.duoc.unheladito.config;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import cl.duoc.unheladito.entity.RolUsuario;
import cl.duoc.unheladito.entity.Usuario;
import cl.duoc.unheladito.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminBootstrapRunner implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    private final boolean enabled;
    private final String nombre;
    private final String email;
    private final String password;

    public AdminBootstrapRunner(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap.admin.enabled:false}") boolean enabled,
            @Value("${app.bootstrap.admin.name:}") String nombre,
            @Value("${app.bootstrap.admin.email:}") String email,
            @Value("${app.bootstrap.admin.password:}") String password
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.enabled = enabled;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        if (!enabled) {
            return;
        }

        validarConfiguracion();

        String emailNormalizado =
                email.trim().toLowerCase(Locale.ROOT);

        var existente = usuarioRepository
                .findByEmailIgnoreCase(emailNormalizado);

        if (existente.isPresent()) {

            Usuario usuario = existente.get();

            if (usuario.getRol() != RolUsuario.ADMINISTRADOR
                    || !usuario.isActivo()) {

                throw new IllegalStateException(
                        "La cuenta administrativa existente no está activa "
                                + "o no posee el rol esperado."
                );
            }
            System.out.println(
                    "Administrador inicial ya existente: "
                            + emailNormalizado
            );

            return;
        }

        Usuario administrador = new Usuario(
                nombre.trim(),
                emailNormalizado,
                passwordEncoder.encode(password),
                RolUsuario.ADMINISTRADOR
        );

        usuarioRepository.save(administrador);

        System.out.println(
                "Administrador inicial creado correctamente: "
                        + emailNormalizado
        );
    }

    private void validarConfiguracion() {

        if (nombre == null || nombre.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()) {

            throw new IllegalStateException(
                    "Faltan variables de entorno para crear el administrador."
            );
        }

        if (password.length() < 12) {
            throw new IllegalStateException(
                    "La contraseña administrativa debe tener "
                            + "al menos 12 caracteres."
            );
        }

        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalStateException(
                    "La contraseña administrativa supera "
                            + "el límite permitido por BCrypt."
            );
        }
    }
}
