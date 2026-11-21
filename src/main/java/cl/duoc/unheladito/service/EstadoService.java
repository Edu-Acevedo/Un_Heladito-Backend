package cl.duoc.unheladito.service;

import cl.duoc.unheladito.dto.EstadoApiResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


@Service
public class EstadoService {

    private final JdbcTemplate jdbcTemplate;

    public EstadoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public EstadoApiResponse consultarEstado() {
        try {
            Integer resultado = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (resultado == null || resultado != 1) {
                return new EstadoApiResponse(
                        "Un Heladito API", "degradada", "sin respuesta",
                        "La API responde, pero MySQL no devolvió el resultado esperado."
                );
            }

            return new EstadoApiResponse(
                    "Un Heladito API", "operativa", "conectada",
                    "La API responde y la conexión con MySQL funciona."
            );
        } catch (DataAccessException exception) {
            return new EstadoApiResponse(
                    "Un Heladito API", "degradada", "desconectada",
                    "La API responde, pero no fue posible consultar MySQL. Revisa el servicio y las credenciales."
            );
        }
    }
}
