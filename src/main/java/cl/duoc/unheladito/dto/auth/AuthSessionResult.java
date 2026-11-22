package cl.duoc.unheladito.dto.auth;

public record AuthSessionResult(
        AuthResponse response,
        String refreshToken
) {
}