package cl.duoc.unheladito.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProductoRequest(

        @Size(max = 20, message = "El código admite hasta 20 caracteres")
        String id,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre admite hasta 120 caracteres")
        String name,

        @NotBlank(message = "La categoría es obligatoria")
        String category,

        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser mayor que cero")
        Integer price,

        @NotBlank(message = "El formato es obligatorio")
        @Size(max = 40, message = "El formato admite hasta 40 caracteres")
        String format,

        @NotNull(message = "El stock es obligatorio")
        @PositiveOrZero(message = "El stock no puede ser negativo")
        Integer stock,

        @Size(max = 255, message = "Los alérgenos admiten hasta 255 caracteres")
        String allergens,

        @NotNull(message = "Debes indicar si el producto está en oferta")
        Boolean offer,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 2000, message = "La descripción admite hasta 2000 caracteres")
        String description,

        @Size(max = 500, message = "La imagen admite hasta 500 caracteres")
        String image

) {
}
