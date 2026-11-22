package cl.duoc.unheladito.dto.pedido;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        String id,
        ClientePedidoResponse customer,
        List<ItemPedidoResponse> items,
        Long total,
        String status,
        LocalDateTime createdAt
) {
}
