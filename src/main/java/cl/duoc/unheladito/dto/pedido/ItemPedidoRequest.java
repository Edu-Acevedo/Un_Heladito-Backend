package cl.duoc.unheladito.dto.pedido;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ItemPedidoRequest(

        @NotBlank(message = "El código del producto es obligatorio.")
        @Size(max = 20)
        String id,

        @NotNull(message = "La cantidad es obligatoria.")
        @Min(value = 1, message = "La cantidad mínima es 1.")
        @Max(value = 100, message = "La cantidad máxima por producto es 100.")
        Integer qty

) {
}
