package cl.duoc.unheladito.dto.pedido;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DatosClientePedidoRequest(

        @NotBlank(message = "El nombre del cliente es obligatorio.")
        @Size(max = 100)
        String name,

        @NotBlank(message = "El correo es obligatorio.")
        @Email(message = "El correo no tiene un formato válido.")
        @Size(max = 150)
        String email,

        @NotBlank(message = "La dirección es obligatoria.")
        @Size(max = 200)
        String street,

        @NotBlank(message = "La región es obligatoria.")
        @Size(max = 100)
        String region,

        @NotBlank(message = "La comuna es obligatoria.")
        @Size(max = 100)
        String commune,

        @NotBlank(message = "La opción de entrega es obligatoria.")
        @Size(max = 80)
        String delivery,

        @Size(max = 1000)
        String notes

) {
}
