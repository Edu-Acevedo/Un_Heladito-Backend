package cl.duoc.unheladito.dto;


public record EstadoApiResponse(
        String aplicacion,
        String estado,
        String baseDatos,
        String mensaje
) {
}
