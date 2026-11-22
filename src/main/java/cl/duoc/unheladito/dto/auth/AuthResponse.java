package cl.duoc.unheladito.dto.auth;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UsuarioResponse user
) {
}