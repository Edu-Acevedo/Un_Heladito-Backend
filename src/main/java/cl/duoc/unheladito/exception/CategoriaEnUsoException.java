package cl.duoc.unheladito.exception;

public class CategoriaEnUsoException extends RuntimeException {

    public CategoriaEnUsoException(String mensaje) {
        super(mensaje);
    }
}