package com.minimarket.controller;

import com.minimarket.entity.Carrito;
import com.minimarket.service.CarritoService;

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
@RequestMapping("/api/carrito")
@Tag(
        name = "Carrito",
        description = "Operaciones para consultar, agregar, actualizar y eliminar productos del carrito"
)
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Operation(
            summary = "Listar productos del carrito",
            description = "Obtiene todos los registros almacenados actualmente en el carrito de compras."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Carrito obtenido correctamente"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario no autenticado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuario sin permisos para consultar el carrito",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Carrito>>> listarCarrito() {

        List<EntityModel<Carrito>> registros = carritoService.findAll()
                .stream()
                .map(this::crearModeloCarrito)
                .toList();

        CollectionModel<EntityModel<Carrito>> coleccion =
                CollectionModel.of(
                        registros,
                        linkTo(methodOn(CarritoController.class)
                                .listarCarrito())
                                .withSelfRel()
                );

        return ResponseEntity.ok(coleccion);
    }

    @Operation(
            summary = "Obtener registro del carrito por ID",
            description = "Busca un registro específico del carrito utilizando su identificador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Registro del carrito encontrado",
                    content = @Content(
                            schema = @Schema(implementation = Carrito.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario no autenticado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuario sin permisos para consultar el carrito",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Registro del carrito no encontrado",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Carrito>> obtenerCarritoPorId(

            @Parameter(
                    description = "Identificador único del registro del carrito",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {

        Carrito carrito = carritoService.findById(id);

        if (carrito == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(crearModeloCarrito(carrito));
    }

    @Operation(
            summary = "Agregar producto al carrito",
            description = "Registra un nuevo producto en el carrito de compras."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Producto agregado correctamente al carrito",
                    content = @Content(
                            schema = @Schema(implementation = Carrito.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o stock insuficiente",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario no autenticado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuario sin permisos para modificar el carrito",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<EntityModel<Carrito>> agregarProductoAlCarrito(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del producto que será agregado al carrito",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Carrito.class)
                    )
            )
            @RequestBody Carrito carrito) {

        Carrito carritoGuardado = carritoService.save(carrito);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(crearModeloCarrito(carritoGuardado));
    }

    @Operation(
            summary = "Actualizar registro del carrito",
            description = "Modifica los datos de un producto existente dentro del carrito."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Registro del carrito actualizado correctamente",
                    content = @Content(
                            schema = @Schema(implementation = Carrito.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o stock insuficiente",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario no autenticado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuario sin permisos para modificar el carrito",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Registro del carrito no encontrado",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Carrito>> actualizarCarrito(

            @Parameter(
                    description = "Identificador del registro que será actualizado",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos del registro del carrito",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Carrito.class)
                    )
            )
            @RequestBody Carrito carrito) {

        Carrito existente = carritoService.findById(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        carrito.setId(id);

        Carrito carritoActualizado = carritoService.save(carrito);

        return ResponseEntity.ok(crearModeloCarrito(carritoActualizado));
    }

    @Operation(
            summary = "Eliminar producto del carrito",
            description = "Elimina un registro del carrito utilizando su identificador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Producto eliminado correctamente del carrito"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario no autenticado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuario sin permisos para modificar el carrito",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Registro del carrito no encontrado",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProductoDelCarrito(

            @Parameter(
                    description = "Identificador del registro que será eliminado",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {

        Carrito carrito = carritoService.findById(id);

        if (carrito == null) {
            return ResponseEntity.notFound().build();
        }

        carritoService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private EntityModel<Carrito> crearModeloCarrito(Carrito carrito) {

        EntityModel<Carrito> modelo = EntityModel.of(carrito);

        modelo.add(
                linkTo(methodOn(CarritoController.class)
                        .obtenerCarritoPorId(carrito.getId()))
                        .withSelfRel()
        );

        modelo.add(
                linkTo(methodOn(CarritoController.class)
                        .listarCarrito())
                        .withRel("carrito")
        );

        modelo.add(
                linkTo(methodOn(CarritoController.class)
                        .actualizarCarrito(carrito.getId(), carrito))
                        .withRel("actualizar")
        );

        modelo.add(
                linkTo(methodOn(CarritoController.class)
                        .eliminarProductoDelCarrito(carrito.getId()))
                        .withRel("eliminar")
        );

        return modelo;
    }
}