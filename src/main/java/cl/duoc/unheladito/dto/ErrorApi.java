package cl.duoc.unheladito.dto;

import java.util.Map;

public record ErrorApi(
        int status,
        String mensaje,
        Map<String, String> campos
) {
}