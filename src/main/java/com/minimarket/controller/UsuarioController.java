package com.minimarket.controller;

import com.minimarket.entity.Usuario;
import com.minimarket.service.UsuarioService;

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
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/usuarios")
@Tag(
        name = "Usuarios",
        description = "Operaciones para registrar, consultar, actualizar y eliminar usuarios"
)
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(
            summary = "Listar usuarios",
            description = "Obtiene todos los usuarios registrados. Requiere rol ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuarios obtenidos correctamente"
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
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Usuario>>> listarUsuarios() {

        List<EntityModel<Usuario>> usuarios = usuarioService.findAll()
                .stream()
                .map(this::crearModeloUsuario)
                .toList();

        CollectionModel<EntityModel<Usuario>> coleccion =
                CollectionModel.of(
                        usuarios,
                        linkTo(methodOn(UsuarioController.class)
                                .listarUsuarios())
                                .withSelfRel()
                );

        return ResponseEntity.ok(coleccion);
    }

    @Operation(
            summary = "Obtener usuario por ID",
            description = "Busca un usuario utilizando su identificador único."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado",
                    content = @Content(
                            schema = @Schema(implementation = Usuario.class)
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
                    description = "Usuario no encontrado",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> obtenerUsuarioPorId(

            @Parameter(
                    description = "Identificador único del usuario",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {

        Optional<Usuario> usuario = usuarioService.findById(id);

        return usuario
                .map(valor -> ResponseEntity.ok(crearModeloUsuario(valor)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Registrar usuario",
            description = "Crea un nuevo usuario con sus roles asociados."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario registrado correctamente",
                    content = @Content(
                            schema = @Schema(implementation = Usuario.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o nombre de usuario duplicado",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<EntityModel<Usuario>> guardarUsuario(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del usuario que será registrado",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Usuario.class)
                    )
            )
            @RequestBody Usuario usuario) {

        Usuario usuarioGuardado = usuarioService.save(usuario);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(crearModeloUsuario(usuarioGuardado));
    }

    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza los datos de un usuario existente. Requiere rol ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario actualizado correctamente",
                    content = @Content(
                            schema = @Schema(implementation = Usuario.class)
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
                    description = "Usuario no encontrado",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> actualizarUsuario(

            @Parameter(
                    description = "Identificador del usuario que será actualizado",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,

            @RequestBody Usuario usuario) {

        Optional<Usuario> usuarioExistente = usuarioService.findById(id);

        if (usuarioExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        usuario.setId(id);

        Usuario usuarioActualizado = usuarioService.save(usuario);

        return ResponseEntity.ok(crearModeloUsuario(usuarioActualizado));
    }

    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario mediante su identificador. Requiere rol ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuario eliminado correctamente"
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
                    description = "Usuario no encontrado",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(

            @Parameter(
                    description = "Identificador del usuario que será eliminado",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {

        Optional<Usuario> usuario = usuarioService.findById(id);

        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        usuarioService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private EntityModel<Usuario> crearModeloUsuario(Usuario usuario) {

        EntityModel<Usuario> modelo = EntityModel.of(usuario);

        modelo.add(
                linkTo(methodOn(UsuarioController.class)
                        .obtenerUsuarioPorId(usuario.getId()))
                        .withSelfRel()
        );

        modelo.add(
                linkTo(methodOn(UsuarioController.class)
                        .listarUsuarios())
                        .withRel("usuarios")
        );

        modelo.add(
                linkTo(methodOn(UsuarioController.class)
                        .actualizarUsuario(usuario.getId(), usuario))
                        .withRel("actualizar")
        );

        modelo.add(
                linkTo(methodOn(UsuarioController.class)
                        .eliminarUsuario(usuario.getId()))
                        .withRel("eliminar")
        );

        return modelo;
    }
}