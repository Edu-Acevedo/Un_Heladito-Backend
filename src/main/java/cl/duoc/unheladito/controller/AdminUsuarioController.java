package cl.duoc.unheladito.controller;

import java.util.List;
import cl.duoc.unheladito.dto.admin.CambiarEstadoUsuarioRequest;
import cl.duoc.unheladito.dto.admin.CambiarRolRequest;
import cl.duoc.unheladito.dto.admin.UsuarioAdminResponse;
import cl.duoc.unheladito.service.AdminUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/usuarios")
@Tag(
        name = "Administración de usuarios",
        description = "Gestión de cuentas y permisos"
)
@SecurityRequirement(name = "bearerAuth")
public class AdminUsuarioController {

    private final AdminUsuarioService adminUsuarioService;

    public AdminUsuarioController(
            AdminUsuarioService adminUsuarioService
    ) {
        this.adminUsuarioService = adminUsuarioService;
    }

    @GetMapping
    @Operation(summary = "Listar usuarios registrados")
    public ResponseEntity<List<UsuarioAdminResponse>> listar() {

        return ResponseEntity.ok(
                adminUsuarioService.listar()
        );
    }
    @PatchMapping("/{id}/rol")
    @Operation(summary = "Cambiar el rol de un usuario")
    public ResponseEntity<UsuarioAdminResponse> cambiarRol(
            @PathVariable Long id,
            @Valid @RequestBody CambiarRolRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {

        UsuarioAdminResponse actualizado =
                adminUsuarioService.cambiarRol(
                        id,
                        request.role(),
                        jwt.getSubject()
                );

        return ResponseEntity.ok(actualizado);
    }
    @PatchMapping("/{id}/estado")
    @Operation(summary = "Activar o desactivar una cuenta")
    public ResponseEntity<UsuarioAdminResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoUsuarioRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {

        UsuarioAdminResponse actualizado =
                adminUsuarioService.cambiarEstado(
                        id,
                        request.active(),
                        jwt.getSubject()
                );

        return ResponseEntity.ok(actualizado);
    }
}