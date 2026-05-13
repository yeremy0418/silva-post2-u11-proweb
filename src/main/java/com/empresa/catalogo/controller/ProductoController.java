package com.empresa.catalogo.controller;

import com.empresa.catalogo.dto.ProductoRequestDTO;
import com.empresa.catalogo.dto.ProductoResponseDTO;
import com.empresa.catalogo.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de productos.
 * Aplica SRP: solo maneja peticiones HTTP y delega la lógica al Service.
 * Aplica DIP: depende de la interfaz ProductoService, no de su implementación.
 */
@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Operaciones CRUD del catálogo")
public class ProductoController {

    private final ProductoService service;

    /**
     * Inyección por constructor — buena práctica SOLID.
     */
    public ProductoController(ProductoService service) {
        this.service = service;
    }

    /**
     * POST /api/productos
     * Crea un nuevo producto. Requiere body JSON válido según ProductoRequestDTO.
     * @Valid activa las validaciones de Bean Validation (@NotBlank, @Positive).
     */
    @Operation(summary = "Crear un nuevo producto")
    @ApiResponse(responseCode = "201", description = "Producto creado")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crear(
            @Valid @RequestBody ProductoRequestDTO dto) {
        ProductoResponseDTO respuesta = service.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    /**
     * GET /api/productos
     * Lista todos los productos activos.
     */
    @Operation(summary = "Listar productos activos")
    @ApiResponse(responseCode = "200", description = "Lista de productos activos")
    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarActivos());
    }

    /**
     * GET /api/productos/{id}
     * Retorna un producto por su id. Si no existe, el GlobalExceptionHandler
     * intercepta la RecursoNoEncontradoException y retorna 404.
     */
    @Operation(summary = "Obtener producto por ID")
    @ApiResponse(responseCode = "200", description = "Producto encontrado")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> buscarPorId(
            @Parameter(description = "ID del producto", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /**
     * DELETE /api/productos/{id}
     * Elimina un producto por su id. Retorna 204 No Content si tiene éxito.
     */
    @Operation(summary = "Eliminar producto por ID")
    @ApiResponse(responseCode = "204", description = "Producto eliminado")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
