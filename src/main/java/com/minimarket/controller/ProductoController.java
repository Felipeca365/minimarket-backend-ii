package com.minimarket.controller;

import com.minimarket.entity.Producto;
import com.minimarket.service.ProductoService;

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
@RequestMapping("/api/productos")
@Tag(
        name = "Productos",
        description = "Operaciones para consultar, crear, actualizar y eliminar productos"
)
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Operation(
            summary = "Listar todos los productos",
            description = "Obtiene la lista completa de productos registrados en Minimarket Plus."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Productos obtenidos correctamente"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario no autenticado",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Producto>>> listarProductos() {

        List<EntityModel<Producto>> productos = productoService.findAll()
                .stream()
                .map(this::crearModeloProducto)
                .toList();

        CollectionModel<EntityModel<Producto>> coleccion =
                CollectionModel.of(
                        productos,
                        linkTo(methodOn(ProductoController.class)
                                .listarProductos())
                                .withSelfRel()
                );

        return ResponseEntity.ok(coleccion);
    }

    @Operation(
            summary = "Obtener producto por ID",
            description = "Busca un producto utilizando su identificador único."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto encontrado",
                    content = @Content(
                            schema = @Schema(implementation = Producto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario no autenticado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Producto>> obtenerProductoPorId(

            @Parameter(
                    description = "Identificador único del producto",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {

        Producto producto = productoService.findById(id);

        if (producto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(crearModeloProducto(producto));
    }

    @Operation(
            summary = "Crear un producto",
            description = "Registra un nuevo producto. Esta operación requiere rol ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Producto creado correctamente",
                    content = @Content(
                            schema = @Schema(implementation = Producto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos del producto inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario no autenticado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuario sin permisos para crear productos",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<EntityModel<Producto>> guardarProducto(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del producto que será registrado",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Producto.class)
                    )
            )
            @RequestBody Producto producto) {

        Producto productoGuardado = productoService.save(producto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(crearModeloProducto(productoGuardado));
    }

    @Operation(
            summary = "Actualizar un producto",
            description = "Actualiza los datos de un producto existente. Requiere rol ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto actualizado correctamente",
                    content = @Content(
                            schema = @Schema(implementation = Producto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos del producto inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario no autenticado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuario sin permisos para actualizar productos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Producto>> actualizarProducto(

            @Parameter(
                    description = "Identificador del producto que será actualizado",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos del producto",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Producto.class)
                    )
            )
            @RequestBody Producto producto) {

        Producto productoExistente = productoService.findById(id);

        if (productoExistente == null) {
            return ResponseEntity.notFound().build();
        }

        producto.setId(id);

        Producto productoActualizado = productoService.save(producto);

        return ResponseEntity.ok(crearModeloProducto(productoActualizado));
    }

    @Operation(
            summary = "Eliminar un producto",
            description = "Elimina un producto mediante su ID. Requiere rol ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Producto eliminado correctamente"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario no autenticado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuario sin permisos para eliminar productos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(

            @Parameter(
                    description = "Identificador del producto que será eliminado",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {

        Producto producto = productoService.findById(id);

        if (producto == null) {
            return ResponseEntity.notFound().build();
        }

        productoService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private EntityModel<Producto> crearModeloProducto(Producto producto) {

        EntityModel<Producto> modelo = EntityModel.of(producto);

        modelo.add(
                linkTo(methodOn(ProductoController.class)
                        .obtenerProductoPorId(producto.getId()))
                        .withSelfRel()
        );

        modelo.add(
                linkTo(methodOn(ProductoController.class)
                        .listarProductos())
                        .withRel("productos")
        );

        modelo.add(
                linkTo(methodOn(ProductoController.class)
                        .actualizarProducto(producto.getId(), producto))
                        .withRel("actualizar")
        );

        modelo.add(
                linkTo(methodOn(ProductoController.class)
                        .eliminarProducto(producto.getId()))
                        .withRel("eliminar")
        );

        if (producto.getCategoria() != null
                && producto.getCategoria().getId() != null) {

            modelo.add(
                    linkTo(methodOn(CategoriaController.class)
                            .obtenerCategoriaPorId(
                                    producto.getCategoria().getId()
                            ))
                            .withRel("categoria")
            );
        }

        return modelo;
    }
}