package cl.duoc.unheladito.dto.pedido;

public record ItemPedidoResponse(
        String id,
        String name,
        Integer qty,
        Integer price,
        String format,
        String image,
        Long subtotal
) {
}