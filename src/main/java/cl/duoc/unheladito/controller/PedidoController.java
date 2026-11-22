package cl.duoc.unheladito.controller;

import cl.duoc.unheladito.dto.pedido.CrearPedidoRequest;
import cl.duoc.unheladito.dto.pedido.PedidoResponse;
import cl.duoc.unheladito.service.PedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@Tag(
        name = "Pedidos",
        description = "Gestión de pedidos y compras de Un Heladito"
)
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo pedido")
    public ResponseEntity<PedidoResponse> crear(
            @Valid @RequestBody CrearPedidoRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {

        PedidoResponse pedido = pedidoService.crear(
                request,
                jwt.getSubject()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pedido);
    }

    @GetMapping("/mis-pedidos")
    @Operation(summary = "Consultar mis pedidos")
    public ResponseEntity<List<PedidoResponse>> listarMisPedidos(
            @AuthenticationPrincipal Jwt jwt
    ) {

        return ResponseEntity.ok(
                pedidoService.listarMisPedidos(jwt.getSubject())
        );
    }

    @GetMapping
    @Operation(summary = "Consultar todos los pedidos")
    public ResponseEntity<List<PedidoResponse>> listarTodos(
            @AuthenticationPrincipal Jwt jwt
    ) {

        return ResponseEntity.ok(
                pedidoService.listarTodos(jwt.getSubject())
        );
    }

    @GetMapping("/{codigo}")
    @Operation(summary = "Consultar un pedido por su código")
    public ResponseEntity<PedidoResponse> buscarPorCodigo(
            @PathVariable String codigo,
            @AuthenticationPrincipal Jwt jwt
    ) {

        return ResponseEntity.ok(
                pedidoService.buscarPorCodigo(
                        codigo,
                        jwt.getSubject()
                )
        );
    }
}