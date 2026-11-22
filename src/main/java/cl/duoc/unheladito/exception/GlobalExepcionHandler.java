package cl.duoc.unheladito.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import cl.duoc.unheladito.dto.ErrorApi;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import cl.duoc.unheladito.exception.RefreshTokenInvalidoException;
import cl.duoc.unheladito.exception.OrigenNoPermitidoException;
import cl.duoc.unheladito.exception.OperacionNoPermitidaException;

import javax.naming.AuthenticationException;

@RestControllerAdvice
public class GlobalExepcionHandler {

    // 404
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorApi> noEncontrado(
            RecursoNoEncontradoException ex
    ) {
        return responder(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                Map.of()
        );
    }

    // 409
    @ExceptionHandler(DatosDuplicadosException.class)
    public ResponseEntity<ErrorApi> datosDuplicados(
            DatosDuplicadosException ex
    ) {
        return responder(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                Map.of()
        );
    }

    // 400
    @ExceptionHandler(DatosInvalidosException.class)
    public ResponseEntity<ErrorApi> datosInvalidos(
            DatosInvalidosException ex
    ) {
        return responder(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                Map.of()
        );
    }

    // 409
    @ExceptionHandler(CategoriaEnUsoException.class)
    public ResponseEntity<ErrorApi> categoriaEnUso(
            CategoriaEnUsoException ex
    ) {
        return responder(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                Map.of()
        );
    }

    // 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorApi> validacion(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(
                    error.getField(),
                    error.getDefaultMessage()
            );
        }
        return responder(
                HttpStatus.BAD_REQUEST,
                "Revisa los datos ingresados.",
                campos
        );
    }

    // 400
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorApi> tipoIncorrecto(
            MethodArgumentTypeMismatchException ex
    ) {
        return responder(
                HttpStatus.BAD_REQUEST,
                "El valor ingresado no tiene un formato válido.",
                Map.of()
        );
    }

    // 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorApi> jsonIncorrecto(
            HttpMessageNotReadableException ex
    ) {
        return responder(
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud no contiene un JSON válido.",
                Map.of()
        );
    }

    // 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorApi> integridad(
            DataIntegrityViolationException ex
    ) {
        return responder(
                HttpStatus.CONFLICT,
                "La operación no puede realizarse porque existen datos relacionados o duplicados.",
                Map.of()
        );
    }

    private ResponseEntity<ErrorApi> responder(
            HttpStatus estado,
            String mensaje,
            Map<String, String> campos
    ) {

        ErrorApi error = new ErrorApi(
                estado.value(),
                mensaje,
                campos
        );

        return ResponseEntity.status(estado).body(error);
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorApi> autenticacionFallida(
            AuthenticationException ex
    ) {

        return responder(
                HttpStatus.UNAUTHORIZED,
                "Correo o contraseña incorrectos.",
                Map.of()
        );
    }
    @ExceptionHandler(RefreshTokenInvalidoException.class)
    public ResponseEntity<ErrorApi> refreshTokenInvalido(
            RefreshTokenInvalidoException ex
    ) {

        return responder(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                Map.of()
        );
    }
    @ExceptionHandler(OrigenNoPermitidoException.class)
    public ResponseEntity<ErrorApi> origenNoPermitido(
            OrigenNoPermitidoException ex
    ) {

        return responder(
                HttpStatus.FORBIDDEN,
                ex.getMessage(),
                Map.of()
        );
    }
    @ExceptionHandler(OperacionNoPermitidaException.class)
    public ResponseEntity<ErrorApi> operacionNoPermitida(
            OperacionNoPermitidaException ex
    ) {

        return responder(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                Map.of()
        );
    }
}
