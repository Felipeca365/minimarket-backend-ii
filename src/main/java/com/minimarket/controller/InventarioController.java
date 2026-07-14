package com.minimarket.controller;

import com.minimarket.entity.Inventario;
import com.minimarket.service.InventarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/inventario")
@Tag(
        name = "Inventario",
        description = "Operaciones para registrar y consultar movimientos de inventario"
)
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Operation(
            summary = "Listar movimientos de inventario",
            description = "Obtiene todos los movimientos de entrada y salida registrados."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Movimientos obtenidos correctamente"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario no autenticado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuario sin permisos para consultar inventario",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Inventario>>>
            listarMovimientosDeInventario() {

        List<EntityModel<Inventario>> movimientos = inventarioService.findAll()
                .stream()
                .map(this::crearModeloInventario)
                .toList();

        CollectionModel<EntityModel<Inventario>> coleccion =
                CollectionModel.of(
                        movimientos,
                        linkTo(methodOn(InventarioController.class)
                                .listarMovimientosDeInventario())
                                .withSelfRel()
                );

        return ResponseEntity.ok(coleccion);
    }

    @Operation(
            summary = "Obtener movimiento por ID",
            description = "Busca un movimiento de inventario por su identificador único."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Movimiento encontrado",
                    content = @Content(
                            schema = @Schema(implementation = Inventario.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario no autenticado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuario sin permisos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Movimiento no encontrado",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Inventario>> obtenerMovimientoPorId(

            @Parameter(
                    description = "Identificador del movimiento de inventario",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {

        Inventario inventario = inventarioService.findById(id);

        if (inventario == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(crearModeloInventario(inventario));
    }

    @Operation(
            summary = "Registrar movimiento de inventario",
            description = "Registra una entrada o salida asociada a un producto."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Movimiento registrado correctamente",
                    content = @Content(
                            schema = @Schema(implementation = Inventario.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario no autenticado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuario sin permisos",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<EntityModel<Inventario>> registrarMovimiento(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del movimiento de inventario",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Inventario.class)
                    )
            )
            @RequestBody Inventario inventario) {

        Inventario inventarioGuardado = inventarioService.save(inventario);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(crearModeloInventario(inventarioGuardado));
    }

    @Operation(
            summary = "Actualizar movimiento",
            description = "Modifica un movimiento de inventario existente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Movimiento actualizado correctamente",
                    content = @Content(
                            schema = @Schema(implementation = Inventario.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario no autenticado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuario sin permisos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Movimiento no encontrado",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Inventario>> actualizarMovimiento(

            @Parameter(
                    description = "Identificador del movimiento",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,

            @RequestBody Inventario inventario) {

        Inventario existente = inventarioService.findById(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        inventario.setId(id);

        Inventario actualizado = inventarioService.save(inventario);

        return ResponseEntity.ok(crearModeloInventario(actualizado));
    }

    @Operation(
            summary = "Eliminar movimiento",
            description = "Elimina un movimiento de inventario mediante su ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Movimiento eliminado correctamente"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario no autenticado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuario sin permisos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Movimiento no encontrado",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(

            @Parameter(
                    description = "Identificador del movimiento que será eliminado",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {

        Inventario inventario = inventarioService.findById(id);

        if (inventario == null) {
            return ResponseEntity.notFound().build();
        }

        inventarioService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private EntityModel<Inventario> crearModeloInventario(
            Inventario inventario) {

        EntityModel<Inventario> modelo = EntityModel.of(inventario);

        modelo.add(
                linkTo(methodOn(InventarioController.class)
                        .obtenerMovimientoPorId(inventario.getId()))
                        .withSelfRel()
        );

        modelo.add(
                linkTo(methodOn(InventarioController.class)
                        .listarMovimientosDeInventario())
                        .withRel("inventario")
        );

        modelo.add(
                linkTo(methodOn(InventarioController.class)
                        .actualizarMovimiento(
                                inventario.getId(),
                                inventario
                        ))
                        .withRel("actualizar")
        );

        modelo.add(
                linkTo(methodOn(InventarioController.class)
                        .eliminarMovimiento(inventario.getId()))
                        .withRel("eliminar")
        );

        if (inventario.getProducto() != null
                && inventario.getProducto().getId() != null) {

            modelo.add(
                    linkTo(methodOn(ProductoController.class)
                            .obtenerProductoPorId(
                                    inventario.getProducto().getId()
                            ))
                            .withRel("producto")
            );
        }

        return modelo;
    }
}