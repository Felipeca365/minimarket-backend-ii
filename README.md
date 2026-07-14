# 🛒 MiniMarket API

API REST desarrollada con **Spring Boot** para la gestión de un minimarket. El proyecto implementa documentación automática mediante **OpenAPI 3 (Swagger)**, navegación entre recursos con **Spring HATEOAS**, autenticación con **Spring Security**, pruebas unitarias con **JUnit y MockMvc** y persistencia utilizando **Spring Data JPA**.

---

# 📌 Características

- Gestión de Productos
- Gestión de Usuarios
- Gestión de Carrito de Compras
- Gestión de Inventario
- Gestión de Ventas
- Documentación automática con OpenAPI 3
- Navegación REST mediante HATEOAS
- Seguridad con Spring Security (Basic Authentication)
- Pruebas unitarias con JUnit 5 y MockMvc
- Cobertura de código con JaCoCo

---

# 🛠 Tecnologías utilizadas

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- Spring HATEOAS
- SpringDoc OpenAPI
- Maven
- H2 Database
- JUnit 5
- Mockito
- MockMvc
- JaCoCo

---

# 📂 Estructura del proyecto

```
src
 ├── main
 │   ├── controller
 │   ├── entity
 │   ├── repository
 │   ├── service
 │   ├── security
 │   └── config
 │
 └── test
     ├── controller
     ├── service
     └── security
```

---

# 🚀 Ejecución del proyecto

Clonar el repositorio

```bash
git clone https://github.com/Felipeca365/minimarket.git
```

Ingresar al proyecto

```bash
cd minimarket
```

Ejecutar

```bash
./mvnw spring-boot:run
```

o

```bash
mvn spring-boot:run
```

---

# 📖 Documentación OpenAPI

Una vez iniciada la aplicación, acceder a:

Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

Documento OpenAPI

```
http://localhost:8080/v3/api-docs
```

---

# 🔑 Autenticación

La API utiliza autenticación Basic mediante Spring Security.

Usuarios de prueba:

| Usuario | Rol |
|----------|-----|
| admin | ADMIN |
| cajero | CAJERO |
| cliente | CLIENTE |

---

# 📦 Endpoints principales

## Productos

- GET /api/productos
- GET /api/productos/{id}
- POST /api/productos
- PUT /api/productos/{id}
- DELETE /api/productos/{id}

## Usuarios

- GET /api/usuarios
- GET /api/usuarios/{id}
- POST /api/usuarios
- PUT /api/usuarios/{id}
- DELETE /api/usuarios/{id}

## Carrito

- GET /api/carrito
- GET /api/carrito/{id}
- POST /api/carrito
- PUT /api/carrito/{id}
- DELETE /api/carrito/{id}

## Inventario

- GET /api/inventario
- GET /api/inventario/{id}
- POST /api/inventario
- PUT /api/inventario/{id}
- DELETE /api/inventario/{id}

## Ventas

- GET /api/ventas
- GET /api/ventas/{id}
- POST /api/ventas

---

# 🔗 HATEOAS

Todos los recursos incluyen enlaces HATEOAS para facilitar la navegación entre endpoints.

Ejemplo:

```json
{
  "id": 2,
  "nombre": "Fideos 400 g",
  "_links": {
      "self": {
          "href": "/api/productos/2"
      },
      "productos": {
          "href": "/api/productos"
      },
      "actualizar": {
          "href": "/api/productos/2"
      },
      "eliminar": {
          "href": "/api/productos/2"
      }
  }
}
```

---

# 🧪 Pruebas

El proyecto incluye pruebas unitarias para:

- Controllers
- Services
- Seguridad
- Validación de Roles
- Endpoints REST

Resultado obtenido:

```
Tests run: 65
Failures: 0
Errors: 0
BUILD SUCCESS
```

---

# 📚 Funcionalidades implementadas

- CRUD de Productos
- CRUD de Usuarios
- CRUD de Carrito
- CRUD de Inventario
- Gestión de Ventas
- Seguridad por roles
- OpenAPI 3
- Swagger UI
- Spring HATEOAS
- Documentación automática
- Pruebas unitarias

---

# 👨‍💻 Autor

Felipe Cáceres

Analista Programador Computacional

Duoc UC

Proyecto desarrollado para la asignatura **Backend II**.
