package cl.duoc.unheladito.security;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import cl.duoc.unheladito.exception.OrigenNoPermitidoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OriginValidator {
    private final Set<String> allowedOrigins;
    public OriginValidator(
            @Value("${app.cors.allowed-origins:http://localhost:5173,http://localhost:8081}")
            String origins
    ) {
        this.allowedOrigins = Arrays.stream(origins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .collect(Collectors.toUnmodifiableSet());
    }
    public void validar(String origin) {
        if (origin == null || !allowedOrigins.contains(origin)) {
            throw new OrigenNoPermitidoException();
        }
    }
}
