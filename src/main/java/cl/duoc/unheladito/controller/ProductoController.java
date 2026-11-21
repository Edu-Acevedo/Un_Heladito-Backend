package cl.duoc.unheladito.controller;

import java.net.URI;
import java.util.List;

import cl.duoc.unheladito.dto.ProductoRequest;
import cl.duoc.unheladito.dto.ProductoResponse;
import cl.duoc.unheladito.entity.Producto;
import cl.duoc.unheladito.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Gestión del catálogo de Un Heladito")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }
    @GetMapping
    @Operation(summary = "Listar productos con filtros y ordenamiento")
    public ResponseEntity<List<ProductoResponse>> listar(

            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "recommended") String sort

    ) {
        List<ProductoResponse> productos =
                productoService.listar(category, search, sort)
                        .stream()
                        .map(ProductoResponse::fromEntity)
                        .toList();

        return ResponseEntity.ok(productos);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Obtener el detalle de un producto")
    public ResponseEntity<ProductoResponse> buscar(
            @PathVariable String id
    ) {
        Producto producto = productoService.buscarPorId(id);
        return ResponseEntity.ok(
                ProductoResponse.fromEntity(producto)
        );
    }


    @PostMapping
    @Operation(summary = "Registrar un nuevo producto")
    public ResponseEntity<ProductoResponse> crear(
            @Valid @RequestBody ProductoRequest request
    ) {
        Producto producto = convertirAEntidad(request);
        Producto creado = productoService.crear(
                producto,
                request.category()
        );
        URI ubicacion = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getId())
                .toUri();
        return ResponseEntity
                .created(ubicacion)
                .body(ProductoResponse.fromEntity(creado));
    }


    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto")
    public ResponseEntity<ProductoResponse> actualizar(
            @PathVariable String id,
            @Valid @RequestBody ProductoRequest request
    ) {
        Producto datos = convertirAEntidad(request);
        Producto actualizado = productoService.actualizar(
                id,
                datos,
                request.category()
        );
        return ResponseEntity.ok(
                ProductoResponse.fromEntity(actualizado)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto")
    public ResponseEntity<Void> eliminar(
            @PathVariable String id
    ) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private Producto convertirAEntidad(ProductoRequest request) {

        Producto producto = new Producto();
        producto.setId(request.id());
        producto.setNombre(request.name().trim());
        producto.setPrecio(request.price());
        producto.setFormato(request.format().trim());
        producto.setStock(request.stock());
        producto.setAlergenos(
                request.allergens() == null
                        ? ""
                        : request.allergens().trim()
        );
        producto.setOferta(Boolean.TRUE.equals(request.offer()));
        producto.setDescripcion(request.description().trim());
        producto.setImagen(
                request.image() == null
                        ? ""
                        : request.image().trim()
        );
        return producto;
    }
}
