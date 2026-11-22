package cl.duoc.unheladito.exception;

public class RefreshTokenInvalidoException extends RuntimeException {

    public RefreshTokenInvalidoException() {
        super("La sesión expiró o dejó de ser válida. Inicia sesión nuevamente.");
    }
}
