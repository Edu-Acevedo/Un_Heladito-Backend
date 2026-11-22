package cl.duoc.unheladito.dto.admin;

import java.time.LocalDateTime;
import cl.duoc.unheladito.entity.Usuario;

public record UsuarioAdminResponse(
        Long id,
        String name,
        String email,
        String role,
        boolean active,
        LocalDateTime createdAt
) {

    public static UsuarioAdminResponse fromEntity(Usuario usuario) {
        return new UsuarioAdminResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().getEtiqueta(),
                usuario.isActivo(),
                usuario.getFechaCreacion()
        );
    }
}