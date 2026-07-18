package com.minimarket.security.model;

import java.util.List;

/**
 * Respuesta del login: token JWT, username y roles del usuario
 * autenticado. El cliente (Postman, frontend, etc.) guarda el token
 * y lo reenvía en el header Authorization de las siguientes peticiones.
 */
public class LoginResponse {

    private String token;
    private String username;
    private List<String> roles;

    public LoginResponse(String token, String username, List<String> roles) {
        this.token = token;
        this.username = username;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
