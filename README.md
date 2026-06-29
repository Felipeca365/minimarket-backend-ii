# Minimarket Plus - Backend II

Proyecto backend desarrollado en Java con Spring Boot para la gestión de un sistema de minimarket. El sistema permite administrar productos, inventario, ventas, usuarios y roles, incorporando mecanismos de autenticación y control de acceso mediante Spring Security.

Este proyecto corresponde a la actividad sumativa de la Semana 6 de Desarrollo Backend II, enfocada en la aplicación de autenticación en microservicios y la validación de su funcionamiento mediante pruebas unitarias con JUnit, Mockito y JaCoCo.

## Tecnologías utilizadas

* Java 17
* Spring Boot 3.4.1
* Spring Web
* Spring Data JPA
* Spring Security
* H2 Database
* Maven
* JUnit 5
* Mockito
* Spring Security Test
* JaCoCo

## Módulos principales del sistema

El backend está organizado en los siguientes módulos:

* **Productos:** permite listar, crear, actualizar y eliminar productos.
* **Inventario:** permite registrar y consultar movimientos de inventario.
* **Ventas:** permite registrar y consultar ventas.
* **Usuarios:** permite registrar, consultar, actualizar y eliminar usuarios.
* **Roles:** permite asignar permisos diferenciados a los usuarios.
* **Seguridad:** define reglas de autenticación y autorización para proteger endpoints según el rol del usuario.

## Roles implementados

El sistema considera tres tipos principales de usuario:

| Rol     | Permisos principales                                           |
| ------- | -------------------------------------------------------------- |
| ADMIN   | Administrar productos, usuarios, inventario y consultar ventas |
| CAJERO  | Gestionar inventario y generar ventas                          |
| CLIENTE | Acceso limitado a operaciones autorizadas                      |

## Reglas de seguridad aplicadas

La configuración de seguridad restringe operaciones críticas según el rol del usuario:

| Endpoint             | Método            | Acceso permitido    |
| -------------------- | ----------------- | ------------------- |
| `/api/productos/**`  | GET               | Usuario autenticado |
| `/api/productos/**`  | POST, PUT, DELETE | ADMIN               |
| `/api/inventario/**` | Todos             | ADMIN, CAJERO       |
| `/api/ventas/**`     | GET               | ADMIN, CAJERO       |
| `/api/ventas/**`     | POST              | CAJERO              |
| `/api/usuarios/**`   | GET, PUT, DELETE  | ADMIN               |
| `/api/usuarios/**`   | POST              | Público             |

Además, se configuraron respuestas diferenciadas para accesos no autorizados:

* **401 Unauthorized:** usuario no autenticado.
* **403 Forbidden:** usuario autenticado, pero sin permisos suficientes.

## Estructura del proyecto

```text
src
├── main
│   ├── java
│   │   └── com.minimarket
│   │       ├── controller
│   │       ├── entity
│   │       ├── repository
│   │       ├── security
│   │       │   ├── config
│   │       │   ├── model
│   │       │   ├── service
│   │       │   └── util
│   │       ├── service
│   │       └── service.impl
│   └── resources
└── test
    └── java
        └── com.minimarket
            ├── controller
            ├── security
            └── service.impl
```

## Pruebas unitarias implementadas

Se desarrollaron pruebas unitarias para validar el comportamiento de los módulos principales y las reglas de seguridad del backend.

### Pruebas de controladores

* `ProductoControllerTest`
* `InventarioControllerTest`
* `VentaControllerTest`
* `UsuarioControllerTest`

Estas pruebas validan:

* Acceso permitido según rol.
* Bloqueo de accesos no autenticados.
* Bloqueo de usuarios sin permisos.
* Respuestas HTTP esperadas: `200 OK`, `204 No Content`, `401 Unauthorized`, `403 Forbidden` y `404 Not Found`.
* Correcta llamada a los servicios mediante Mockito.

### Pruebas de servicios

* `ProductoServiceImplTest`
* `InventarioServiceImplTest`
* `VentaServiceImplTest`
* `UsuarioServiceImplTest`

Estas pruebas validan:

* Listado de registros.
* Búsqueda por ID.
* Guardado de entidades.
* Eliminación de registros.
* Casos donde no existen datos.
* Encriptación de contraseña en el servicio de usuarios.

### Pruebas de seguridad

* `CustomUserDetailsServiceTest`
* `JwtUtilTest`

Estas pruebas validan:

* Carga correcta de un usuario existente.
* Excepción ante usuario inexistente.
* Instanciación de la clase utilitaria JWT.

## Resultado de ejecución de pruebas

La ejecución final de pruebas se realizó mediante Maven:

```bash
mvn clean test
```

Resultado obtenido:

```text
Tests run: 65
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

Esto confirma que las pruebas unitarias se ejecutaron correctamente y que no se detectaron errores en los escenarios validados.

## Cobertura de código con JaCoCo

Se configuró JaCoCo para generar reportes de cobertura automáticamente durante la ejecución de pruebas.

Comando utilizado:

```bash
mvn clean test
```

Ruta del reporte generado:

```text
target/site/jacoco/index.html
```

Resultado final de cobertura:

```text
Cobertura total: 71%
```

Cobertura destacada por paquete:

| Paquete                           | Cobertura |
| --------------------------------- | --------: |
| `com.minimarket.security.config`  |      100% |
| `com.minimarket.security.service` |      100% |
| `com.minimarket.security.util`    |      100% |
| `com.minimarket.security.model`   |       73% |
| `com.minimarket.service.impl`     |       61% |
| `com.minimarket.entity`           |       63% |
| `com.minimarket.controller`       |       46% |
| Total del proyecto                |       71% |

## Comandos principales

Compilar el proyecto:

```bash
mvn clean install
```

Ejecutar pruebas:

```bash
mvn clean test
```

Ejecutar la aplicación:

```bash
mvn spring-boot:run
```

Abrir reporte JaCoCo en Windows:

```text
target\site\jacoco\index.html
```

## Evidencias sugeridas para el informe

Para respaldar la ejecución del proyecto, se recomienda incluir las siguientes capturas:

1. Ejecución final de pruebas con `65 Tests run`, `0 Failures`, `0 Errors`.
2. Reporte JaCoCo final con `71%` de cobertura.
3. Estructura del proyecto en VS Code.
4. Archivo `SecurityConfig.java` con reglas de autorización.
5. Pruebas unitarias creadas en `src/test/java`.

## Mejoras implementadas

Durante el desarrollo de la actividad se realizaron las siguientes mejoras:

* Configuración de reglas de seguridad por rol.
* Restricción de endpoints críticos.
* Implementación de pruebas con usuarios simulados mediante `@WithMockUser`.
* Validación de respuestas `401` y `403`.
* Incorporación de JaCoCo para medir cobertura.
* Creación de pruebas para controladores, servicios y autenticación.
* Aumento progresivo de cobertura desde 37% inicial hasta 71% final.

## Recomendaciones futuras

Como mejoras futuras se propone:

* Implementar lógica completa en `JwtUtil` para generación y validación real de tokens JWT.
* Agregar pruebas de integración con base de datos H2.
* Mejorar la lógica de negocio en ventas, validando stock antes de confirmar una venta.
* Agregar validaciones con Bean Validation en entidades como Producto, Inventario, Venta y Usuario.
* Incorporar GitHub Actions para ejecutar pruebas automáticamente en cada push.
* Aumentar la cobertura de controladores sobre el 70%.

## Autor

Felipe Cabrera
Asignatura: Desarrollo Backend II
Proyecto: Minimarket Plus
Semana 6 - Pruebas unitarias, autenticación y cobertura
