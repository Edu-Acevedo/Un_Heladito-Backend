package cl.duoc.unheladito.controller;

import java.net.URI;
import java.util.List;
import cl.duoc.unheladito.dto.CategoriaRequest;
import cl.duoc.unheladito.dto.CategoriaResponse;
import cl.duoc.unheladito.entity.Categoria;
import cl.duoc.unheladito.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorías", description = "Gestión de categorías de productos")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las categorías")
    public ResponseEntity<List<CategoriaResponse>> listar() {
        List<CategoriaResponse> categorias =
                categoriaService.listar()
                        .stream()
                        .map(CategoriaResponse::fromEntity)
                        .toList();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar una categoría por ID")
    public ResponseEntity<CategoriaResponse> buscar(
            @PathVariable Long id
    ) {
        Categoria categoria = categoriaService.buscarPorId(id);
        return ResponseEntity.ok(
                CategoriaResponse.fromEntity(categoria)
        );
    }

    @PostMapping
    @Operation(summary = "Crear una categoría")
    @SecurityRequirement(name = "bearerAuth")

    public ResponseEntity<CategoriaResponse> crear(
            @Valid @RequestBody CategoriaRequest request
    ) {
        Categoria creada = categoriaService.crear(request.name());
        URI ubicacion = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creada.getId())
                .toUri();
        return ResponseEntity
                .created(ubicacion)
                .body(CategoriaResponse.fromEntity(creada));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Renombrar una categoría")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CategoriaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequest request

    ) {
        Categoria actualizada = categoriaService.actualizar(
                id,
                request.name()
        );
        return ResponseEntity.ok(
                CategoriaResponse.fromEntity(actualizada)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una categoría")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}