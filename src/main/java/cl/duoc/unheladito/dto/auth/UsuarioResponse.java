package cl.duoc.unheladito.dto.auth;

import cl.duoc.unheladito.entity.Usuario;

public record UsuarioResponse(
        Long id,
        String name,
        String email,
        String role
) {

    public static UsuarioResponse fromEntity(Usuario usuario) {

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().getEtiqueta()
        );
    }
}
