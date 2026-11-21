package cl.duoc.unheladito.dto;

import cl.duoc.unheladito.entity.Producto;

public record ProductoResponse(
        String id,
        String name,
        String category,
        Integer price,
        String format,
        Integer stock,
        String allergens,
        boolean offer,
        String description,
        String image
){

    public static ProductoResponse fromEntity(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getCategoria().getNombre(),
                producto.getPrecio(),
                producto.getFormato(),
                producto.getStock(),
                producto.getAlergenos(),
                producto.isOferta(),
                producto.getDescripcion(),
                producto.getImagen()
        );
    }
}