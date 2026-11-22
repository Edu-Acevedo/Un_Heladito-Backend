package cl.duoc.unheladito.service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import cl.duoc.unheladito.dto.admin.UsuarioAdminResponse;
import cl.duoc.unheladito.entity.RolUsuario;
import cl.duoc.unheladito.entity.Usuario;
import cl.duoc.unheladito.exception.OperacionNoPermitidaException;
import cl.duoc.unheladito.exception.RecursoNoEncontradoException;
import cl.duoc.unheladito.repository.RefreshTokenRepository;
import cl.duoc.unheladito.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public AdminUsuarioService(
            UsuarioRepository usuarioRepository,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public List<UsuarioAdminResponse> listar() {

        return usuarioRepository
                .findAllByOrderByFechaCreacionDesc()
                .stream()
                .map(UsuarioAdminResponse::fromEntity)
                .toList();
    }

    @Transactional
    public UsuarioAdminResponse cambiarRol(
            Long id,
            RolUsuario nuevoRol,
            String emailAdministrador
    ) {

        Usuario administrador = obtenerAdministrador(emailAdministrador);
        Usuario usuario = obtenerUsuario(id);

        if (administrador.getId().equals(usuario.getId())) {
            throw new OperacionNoPermitidaException(
                    "No puedes modificar tu propio rol."
            );
        }

        boolean estaPerdiendoRolAdmin =
                usuario.isActivo()
                        && usuario.getRol() == RolUsuario.ADMINISTRADOR
                        && nuevoRol != RolUsuario.ADMINISTRADOR;

        if (estaPerdiendoRolAdmin
                && usuarioRepository.countByRolAndActivoTrue(
                RolUsuario.ADMINISTRADOR
        ) <= 1) {

            throw new OperacionNoPermitidaException(
                    "No puedes quitar el rol al último administrador activo."
            );
        }

        usuario.cambiarRol(nuevoRol);

        Usuario actualizado = usuarioRepository.save(usuario);

        refreshTokenRepository.revocarTodosDeUsuario(
                id,
                Instant.now()
        );

        return UsuarioAdminResponse.fromEntity(actualizado);
    }

    @Transactional
    public UsuarioAdminResponse cambiarEstado(
            Long id,
            boolean activo,
            String emailAdministrador
    ) {

        Usuario administrador = obtenerAdministrador(emailAdministrador);
        Usuario usuario = obtenerUsuario(id);

        if (administrador.getId().equals(usuario.getId())) {
            throw new OperacionNoPermitidaException(
                    "No puedes desactivar tu propia cuenta."
            );
        }

        boolean estaDesactivandoAdmin =
                usuario.isActivo()
                        && !activo
                        && usuario.getRol() == RolUsuario.ADMINISTRADOR;

        if (estaDesactivandoAdmin
                && usuarioRepository.countByRolAndActivoTrue(
                RolUsuario.ADMINISTRADOR
        ) <= 1) {

            throw new OperacionNoPermitidaException(
                    "No puedes desactivar al último administrador activo."
            );
        }

        if (activo) {
            usuario.activar();
        } else {
            usuario.desactivar();

            refreshTokenRepository.revocarTodosDeUsuario(
                    id,
                    Instant.now()
            );
        }

        Usuario actualizado = usuarioRepository.save(usuario);

        return UsuarioAdminResponse.fromEntity(actualizado);
    }

    private Usuario obtenerAdministrador(String email) {

        Usuario usuario = usuarioRepository
                .findByEmailIgnoreCase(normalizarEmail(email))
                .orElseThrow(() ->
                        new OperacionNoPermitidaException(
                                "No se pudo verificar al administrador."
                        )
                );

        if (!usuario.isActivo()
                || usuario.getRol() != RolUsuario.ADMINISTRADOR) {

            throw new OperacionNoPermitidaException(
                    "No tienes permisos para administrar usuarios."
            );
        }

        return usuario;
    }

    private Usuario obtenerUsuario(Long id) {

        return usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe el usuario con ID " + id
                        )
                );
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}