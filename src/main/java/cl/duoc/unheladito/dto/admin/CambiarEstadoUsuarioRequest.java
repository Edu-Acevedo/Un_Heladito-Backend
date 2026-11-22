package cl.duoc.unheladito.dto.admin;

import jakarta.validation.constraints.NotNull;

public record CambiarEstadoUsuarioRequest(

        @NotNull(message = "Debes indicar el estado de la cuenta")
        Boolean active

) {
}