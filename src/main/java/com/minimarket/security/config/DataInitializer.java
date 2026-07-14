package com.minimarket.security.config;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner cargarUsuariosIniciales(
            RolRepository rolRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            Rol rolAdmin = obtenerOCrearRol("ADMIN", rolRepository);
            Rol rolCajero = obtenerOCrearRol("CAJERO", rolRepository);
            Rol rolCliente = obtenerOCrearRol("CLIENTE", rolRepository);

            crearUsuarioSiNoExiste(
                    "admin",
                    "admin123",
                    rolAdmin,
                    usuarioRepository,
                    passwordEncoder
            );

            crearUsuarioSiNoExiste(
                    "cajero",
                    "cajero123",
                    rolCajero,
                    usuarioRepository,
                    passwordEncoder
            );

            crearUsuarioSiNoExiste(
                    "cliente",
                    "cliente123",
                    rolCliente,
                    usuarioRepository,
                    passwordEncoder
            );
        };
    }

    private Rol obtenerOCrearRol(
            String nombre,
            RolRepository rolRepository) {

        return rolRepository.findByNombre(nombre)
                .orElseGet(() -> {
                    Rol rol = new Rol();
                    rol.setNombre(nombre);
                    return rolRepository.save(rol);
                });
    }

    private void crearUsuarioSiNoExiste(
            String username,
            String password,
            Rol rol,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        if (usuarioRepository.findByUsername(username).isEmpty()) {

            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setPassword(passwordEncoder.encode(password));
            usuario.setRoles(Set.of(rol));

            usuarioRepository.save(usuario);
        }
    }
}