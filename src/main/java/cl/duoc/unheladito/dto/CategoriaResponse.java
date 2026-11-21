package cl.duoc.unheladito.dto;

import cl.duoc.unheladito.entity.Categoria;

public record CategoriaResponse(
        Long id,
        String name
) {
    public static CategoriaResponse fromEntity(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNombre()
        );
    }
}
