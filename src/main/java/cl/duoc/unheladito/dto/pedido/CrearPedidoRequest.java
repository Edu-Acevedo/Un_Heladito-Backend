package cl.duoc.unheladito.dto.pedido;

import cl.duoc.unheladito.entity.EstadoPedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CrearPedidoRequest(

        @NotNull(message = "Los datos del cliente son obligatorios.")
        @Valid
        DatosClientePedidoRequest customer,

        @NotEmpty(message = "El pedido debe contener productos.")
        @Size(max = 30, message = "El pedido admite hasta 30 productos diferentes.")
        List<@NotNull @Valid ItemPedidoRequest> items,

        @NotNull(message = "Debes seleccionar un resultado de pago.")
        EstadoPedido payment

) {
}