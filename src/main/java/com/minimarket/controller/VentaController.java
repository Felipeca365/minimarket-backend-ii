package com.minimarket.controller;

import com.minimarket.entity.Venta;
import com.minimarket.service.VentaService;

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
@RequestMapping("/api/ventas")
@Tag(
        name = "Ventas",
        description = "Operaciones para registrar y consultar ventas"
)
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Operation(
            summary = "Listar ventas",
            description = "Obtiene todas las ventas registradas en el sistema."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ventas obtenidas correctamente"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario no autenticado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuario sin permisos para consultar ventas",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Venta>>> listarVentas() {

        List<EntityModel<Venta>> ventas = ventaService.findAll()
                .stream()
                .map(this::crearModeloVenta)
                .toList();

        CollectionModel<EntityModel<Venta>> coleccion =
                CollectionModel.of(
                        ventas,
                        linkTo(methodOn(VentaController.class)
                                .listarVentas())
                                .withSelfRel()
                );

        return ResponseEntity.ok(coleccion);
    }

    @Operation(
            summary = "Obtener venta por ID",
            description = "Busca una venta utilizando su identificador único."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Venta encontrada",
                    content = @Content(
                            schema = @Schema(implementation = Venta.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario no autenticado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuario sin permisos para consultar ventas",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Venta no encontrada",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Venta>> obtenerVentaPorId(

            @Parameter(
                    description = "Identificador único de la venta",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {

        Venta venta = ventaService.findById(id);

        if (venta == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(crearModeloVenta(venta));
    }

    @Operation(
            summary = "Registrar una venta",
            description = "Registra una nueva venta con su usuario y detalles asociados. Requiere rol CAJERO."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Venta registrada correctamente",
                    content = @Content(
                            schema = @Schema(implementation = Venta.class)
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
                    description = "Usuario sin permisos para registrar ventas",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<EntityModel<Venta>> guardarVenta(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la venta y sus detalles",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Venta.class)
                    )
            )
            @RequestBody Venta venta) {

        /*
         * Asegura la relación bidireccional antes de guardar.
         */
        if (venta.getDetalles() != null) {
            venta.getDetalles().forEach(detalle -> detalle.setVenta(venta));
        }

        Venta ventaGuardada = ventaService.save(venta);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(crearModeloVenta(ventaGuardada));
    }

    private EntityModel<Venta> crearModeloVenta(Venta venta) {

        EntityModel<Venta> modelo = EntityModel.of(venta);

        modelo.add(
                linkTo(methodOn(VentaController.class)
                        .obtenerVentaPorId(venta.getId()))
                        .withSelfRel()
        );

        modelo.add(
                linkTo(methodOn(VentaController.class)
                        .listarVentas())
                        .withRel("ventas")
        );

        if (venta.getUsuario() != null
                && venta.getUsuario().getId() != null) {

            modelo.add(
                    linkTo(methodOn(UsuarioController.class)
                            .obtenerUsuarioPorId(
                                    venta.getUsuario().getId()
                            ))
                            .withRel("usuario")
            );
        }

        return modelo;
    }
}