package cl.duoc.unheladito.dto.admin;

import cl.duoc.unheladito.entity.RolUsuario;
import jakarta.validation.constraints.NotNull;

public record CambiarRolRequest(

        @NotNull(message = "El rol es obligatorio")
        RolUsuario role

) {
}