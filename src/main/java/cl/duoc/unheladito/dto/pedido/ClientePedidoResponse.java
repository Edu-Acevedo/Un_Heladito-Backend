package cl.duoc.unheladito.dto.pedido;

public record ClientePedidoResponse(
        String name,
        String email,
        String street,
        String region,
        String commune,
        String delivery,
        String notes
) {
}