package cl.duoc.unheladito.controller;

import cl.duoc.unheladito.dto.EstadoApiResponse;
import cl.duoc.unheladito.service.EstadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/estado")
@Tag(name = "Estado", description = "Comprobaciones básicas de la API y MySQL")
public class EstadoController {

    private final EstadoService estadoService;

    public EstadoController(EstadoService estadoService) {
        this.estadoService = estadoService;
    }

    @GetMapping
    @Operation(summary = "Consultar estado de la API y MySQL")
    public ResponseEntity<EstadoApiResponse> consultarEstado() {
        EstadoApiResponse respuesta = estadoService.consultarEstado();
        HttpStatus estadoHttp = "operativa".equals(respuesta.estado())
                ? HttpStatus.OK
                : HttpStatus.SERVICE_UNAVAILABLE;

        return ResponseEntity.status(estadoHttp).body(respuesta);
    }
}
