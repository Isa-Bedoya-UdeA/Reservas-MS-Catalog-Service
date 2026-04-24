# Pruebas de API - MS-Catalog-Service

Este documento contiene las pruebas de API para el microservicio de Catálogo de Servicios.

## Configuración Inicial

### Variables de Entorno

Para ejecutar las pruebas, asegúrate de configurar las siguientes variables en tu archivo `.env`:

```bash
# SPRING PROFILE
SPRING_PROFILE=dev

# DATABASE CONFIG - SUPABASE (Transaction Pooler - IPv4 compatible)
DB_URL=jdbc:postgresql://aws-1-us-west-2.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0
DB_USER=postgres.[TU-PROJECT-REF]
DB_PASSWORD=[TU-CONTRASEÑA-DE-SUPABASE]

# EXTERNAL SERVICES URLs
SERVICES_AUTH_URL=http://localhost:8081
```

### Configuración de JWT

El microservicio de Catálogo requiere autenticación JWT para operaciones que requieren roles específicos (ADMIN, PROVEEDOR). Debes obtener un token JWT del microservicio de Autenticación.

**Para obtener un token JWT:**
1. Inicia sesión en el Auth Service: `POST http://localhost:8081/api/auth/login`
2. Usa el `accessToken` retornado en el header `Authorization: Bearer [JWT_TOKEN]`

---

## Categorías

### 1. Obtener Todas las Categorías (Requiere ROLE_ADMIN)

**Nombre:** Get All Categories - Success
**URL:** `http://localhost:8082/api/catalog/categories`
**Método:** GET
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_ADMIN]
```
**Código esperado:** 200 OK
**Response esperado:**
```json
[
    {
        "id": "[UUID-CATEGORIA-1]",
        "nombreCategoria": "Belleza",
        "descripcion": "Servicios de belleza y cuidado personal",
        "activo": true
    },
    {
        "id": "[UUID-CATEGORIA-2]",
        "nombreCategoria": "Salud",
        "descripcion": "Servicios de salud y bienestar",
        "activo": true
    }
]
```

---

### 2. Obtener Categoría por ID (Público)

**Nombre:** Get Category by ID - Success
**URL:** `http://localhost:8082/api/catalog/categories/[UUID-CATEGORIA]`
**Método:** GET
**Headers:**
```
Content-Type: application/json
```
**Código esperado:** 200 OK
**Response esperado:**
```json
{
    "id": "[UUID-CATEGORIA]",
    "nombreCategoria": "Belleza",
    "descripcion": "Servicios de belleza y cuidado personal",
    "activo": true
}
```

---

### 3. Obtener Categorías Activas (Público)

**Nombre:** Get Active Categories - Success
**URL:** `http://localhost:8082/api/catalog/categories/active`
**Método:** GET
**Headers:**
```
Content-Type: application/json
```
**Código esperado:** 200 OK
**Response esperado:**
```json
[
    {
        "id": "[UUID-CATEGORIA-1]",
        "nombreCategoria": "Belleza",
        "descripcion": "Servicios de belleza y cuidado personal",
        "activo": true
    },
    {
        "id": "[UUID-CATEGORIA-2]",
        "nombreCategoria": "Salud",
        "descripcion": "Servicios de salud y bienestar",
        "activo": true
    }
]
```
**Nota:** Este endpoint retorna solo las categorías activas. Es usado por proveedores para seleccionar categorías al crear servicios.

---

### 4. Obtener Categoría No Existente (404)

**Nombre:** Get Category by ID - Not Found
**URL:** `http://localhost:8082/api/catalog/categories/00000000-0000-0000-0000-000000000000`
**Método:** GET
**Headers:**
```
Content-Type: application/json
```
**Código esperado:** 404 Not Found
**Response esperado:**
```json
{
    "timestamp": "2026-04-16T12:00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Categoría no encontrado con id: 00000000-0000-0000-0000-000000000000",
    "path": "/api/catalog/categories/00000000-0000-0000-0000-000000000000"
}
```

---

### 5. Obtener Categoría con ID Inválido (400)

**Nombre:** Get Category by ID - Invalid UUID
**URL:** `http://localhost:8082/api/catalog/categories/invalid-uuid`
**Método:** GET
**Headers:**
```
Content-Type: application/json
```
**Código esperado:** 400 Bad Request
**Response esperado:**
```json
{
    "timestamp": "2026-04-16T12:00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Invalid UUID format"
}
```

---

### 6. Crear Categoría (Requiere ROLE_ADMIN)

**Nombre:** Create Category - Success
**URL:** `http://localhost:8082/api/catalog/categories`
**Método:** POST
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_ADMIN]
```
**Body:**
```json
{
    "nombreCategoria": "Deportes",
    "descripcion": "Servicios deportivos y fitness"
}
```
**Código esperado:** 201 Created
**Response esperado:**
```json
{
    "id": "[UUID-NUEVA-CATEGORIA]",
    "nombreCategoria": "Deportes",
    "descripcion": "Servicios deportivos y fitness",
    "activo": true
}
```
**Nota:** El JWT_TOKEN debe corresponder a un usuario con rol ADMIN.

---

### 7. Crear Categoría con Datos Inválidos (400)

**Nombre:** Create Category - Invalid Data
**URL:** `http://localhost:8082/api/catalog/categories`
**Método:** POST
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_ADMIN]
```
**Body:**
```json
{
    "nombreCategoria": "  ",
    "descripcion": "abc"
}
```
**Código esperado:** 400 Bad Request
**Response esperado:**
```json
{
    "timestamp": "2026-04-16T12:00:00",
    "status": 400,
    "error": "Validation Error",
    "message": "Errores de validación en los datos de entrada",
    "validationErrors": {
        "nombreCategoria": "El nombre de la categoría es obligatorio"
    }
}
```

---

### 8. Crear Categoría sin Autorización (401)

**Nombre:** Create Category - Unauthorized
**URL:** `http://localhost:8082/api/catalog/categories`
**Método:** POST
**Headers:**
```
Content-Type: application/json
```
**Body:**
```json
{
    "nombreCategoria": "Deportes",
    "descripcion": "Servicios deportivos y fitness"
}
```
**Código esperado:** 401 Unauthorized
**Response esperado:**
```json
{
    "timestamp": "2026-04-16T12:00:00",
    "status": 401,
    "error": "Unauthorized",
    "message": "No estás autenticado"
}
```

---

### 8. Crear Categoría con Rol Incorrecto (403)

**Nombre:** Create Category - Forbidden (Wrong Role)
**URL:** `http://localhost:8082/api/catalog/categories`
**Método:** POST
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_CLIENTE]
```
**Body:**
```json
{
    "nombreCategoria": "Deportes",
    "descripcion": "Servicios deportivos y fitness"
}
```
**Código esperado:** 403 Forbidden
**Response esperado:**
```json
{
    "timestamp": "2026-04-16T12:00:00",
    "status": 403,
    "error": "Forbidden",
    "message": "No tienes permisos para realizar esta acción"
}
```

---

### 10. Actualizar Categoría (Requiere ROLE_ADMIN)

**Nombre:** Update Category - Success
**URL:** `http://localhost:8082/api/catalog/categories/[UUID-CATEGORIA]`
**Método:** PUT
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_ADMIN]
```
**Body:**
```json
{
    "nombreCategoria": "Belleza y Cuidado Personal",
    "descripcion": "Servicios profesionales de belleza y cuidado personal"
}
```
**Código esperado:** 200 OK
**Response esperado:**
```json
{
    "id": "[UUID-CATEGORIA]",
    "nombreCategoria": "Belleza y Cuidado Personal",
    "descripcion": "Servicios profesionales de belleza y cuidado personal",
    "activo": true
}
```

---

### 11. Actualizar Categoría No Existente (404)

**Nombre:** Update Category - Not Found
**URL:** `http://localhost:8082/api/catalog/categories/00000000-0000-0000-0000-000000000000`
**Método:** PUT
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_ADMIN]
```
**Body:**
```json
{
    "nombreCategoria": "Nombre Actualizado",
    "descripcion": "Descripción actualizada"
}
```
**Código esperado:** 404 Not Found
**Response esperado:**
```json
{
    "timestamp": "2026-04-16T12:00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Categoría no encontrado con id: 00000000-0000-0000-0000-000000000000"
}
```

---

### 12. Desactivar Categoría (Requiere ROLE_ADMIN)

**Nombre:** Deactivate Category - Success
**URL:** `http://localhost:8082/api/catalog/categories/[UUID-CATEGORIA]`
**Método:** DELETE
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_ADMIN]
```
**Código esperado:** 204 No Content
**Response esperado:** (vacío)

---

### 13. Desactivar Categoría No Existente (404)

**Nombre:** Deactivate Category - Not Found
**URL:** `http://localhost:8082/api/catalog/categories/00000000-0000-0000-0000-000000000000`
**Método:** DELETE
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_ADMIN]
```
**Código esperado:** 404 Not Found
**Response esperado:**
```json
{
    "timestamp": "2026-04-16T12:00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Categoría no encontrado con id: 00000000-0000-0000-0000-000000000000"
}
```

---

### 14. Activar Categoría (Requiere ROLE_ADMIN)

**Nombre:** Activate Category - Success
**URL:** `http://localhost:8082/api/catalog/categories/[UUID-CATEGORIA]/activate`
**Método:** PATCH
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_ADMIN]
```
**Código esperado:** 204 No Content
**Response esperado:** (vacío)

---

### 15. Activar Categoría No Existente (404)

**Nombre:** Activate Category - Not Found
**URL:** `http://localhost:8082/api/catalog/categories/00000000-0000-0000-0000-000000000000/activate`
**Método:** PATCH
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_ADMIN]
```
**Código esperado:** 404 Not Found
**Response esperado:**
```json
{
    "timestamp": "2026-04-16T12:00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Categoría no encontrado con id: 00000000-0000-0000-0000-000000000000"
}
```

---

## Servicios Ofertados

### 16. Crear Servicio Ofertado (Requiere ROLE_PROVEEDOR)

**Nombre:** Create Service Offering - Success
**URL:** `http://localhost:8082/api/catalog/services`
**Método:** POST
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Body:**
```json
{
    "nombreServicio": "Corte de Cabello",
    "descripcion": "Corte de cabello profesional",
    "duracionMinutos": 30,
    "precio": 25000,
    "capacidadMaxima": 1
}
```
**Código esperado:** 201 Created
**Response esperado:**
```json
{
    "id": "[UUID-NUEVO-SERVICIO]",
    "nombreServicio": "Corte de Cabello",
    "descripcion": "Corte de cabello profesional",
    "duracionMinutos": 30,
    "precio": 25000,
    "capacidadMaxima": 1,
    "activo": true,
    "idProveedor": "[UUID-PROVEEDOR]"
}
```
**Nota:** El `idProveedor` se obtiene automáticamente del JWT del proveedor autenticado.

---

### 17. Crear Servicio Ofertado con Capacidad Ilimitada (Requiere ROLE_PROVEEDOR)

**Nombre:** Create Service Offering - Unlimited Capacity
**URL:** `http://localhost:8082/api/catalog/services`
**Método:** POST
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Body:**
```json
{
    "nombreServicio": "Clase de Yoga",
    "descripcion": "Clase de yoga grupal",
    "duracionMinutos": 60,
    "precio": 15000,
    "capacidadMaxima": 0
}
```
**Código esperado:** 201 Created
**Response esperado:**
```json
{
    "id": "[UUID-NUEVO-SERVICIO]",
    "nombreServicio": "Clase de Yoga",
    "descripcion": "Clase de yoga grupal",
    "duracionMinutos": 60,
    "precio": 15000,
    "capacidadMaxima": 0,
    "activo": true,
    "idProveedor": "[UUID-PROVEEDOR]"
}
```
**Nota:** `capacidadMaxima = 0` representa capacidad ilimitada (sin límite).

---

### 18. Crear Servicio Ofertado con Datos Inválidos (400)

**Nombre:** Create Service Offering - Invalid Data
**URL:** `http://localhost:8082/api/catalog/services`
**Método:** POST
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Body:**
```json
{
    "nombreServicio": "  ",
    "descripcion": "abc",
    "duracionMinutos": -1,
    "precio": -100,
    "capacidadMaxima": -1
}
```
**Código esperado:** 400 Bad Request
**Response esperado:**
```json
{
    "timestamp": "2026-04-16T12:00:00",
    "status": 400,
    "error": "Validation Error",
    "message": "Errores de validación en los datos de entrada",
    "validationErrors": {
        "nombreServicio": "El nombre del servicio es obligatorio",
        "duracionMinutos": "La duración debe ser mayor a 0",
        "precio": "El precio no puede ser negativo",
        "capacidadMaxima": "La capacidad máxima debe ser al menos 0 (0 = sin límite)"
    }
}
```

---

### 19. Crear Servicio Ofertado sin Autorización (401)

**Nombre:** Create Service Offering - Unauthorized
**URL:** `http://localhost:8082/api/catalog/services`
**Método:** POST
**Headers:**
```
Content-Type: application/json
```
**Body:**
```json
{
    "nombreServicio": "Corte de Cabello",
    "descripcion": "Corte de cabello profesional",
    "duracionMinutos": 30,
    "precio": 25000,
    "capacidadMaxima": 1
}
```
**Código esperado:** 401 Unauthorized
**Response esperado:**
```json
{
    "timestamp": "2026-04-16T12:00:00",
    "status": 401,
    "error": "Unauthorized",
    "message": "No estás autenticado"
}
```

---

### 20. Crear Servicio Ofertado con Rol Incorrecto (403)

**Nombre:** Create Service Offering - Forbidden (Wrong Role)
**URL:** `http://localhost:8082/api/catalog/services`
**Método:** POST
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_CLIENTE]
```
**Body:**
```json
{
    "nombreServicio": "Corte de Cabello",
    "descripcion": "Corte de cabello profesional",
    "duracionMinutos": 30,
    "precio": 25000,
    "capacidadMaxima": 1
}
```
**Código esperado:** 403 Forbidden
**Response esperado:**
```json
{
    "timestamp": "2026-04-16T12:00:00",
    "status": 403,
    "error": "Forbidden",
    "message": "No tienes permisos para realizar esta acción"
}
```

---

### 21. Actualizar Servicio Ofertado (Requiere ROLE_PROVEEDOR)

**Nombre:** Update Service Offering - Success
**URL:** `http://localhost:8082/api/catalog/services/[UUID-SERVICIO]`
**Método:** PUT
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Body:**
```json
{
    "nombreServicio": "Corte de Cabello Premium",
    "precio": 30000
}
```
**Código esperado:** 200 OK
**Response esperado:**
```json
{
    "id": "[UUID-SERVICIO]",
    "nombreServicio": "Corte de Cabello Premium",
    "descripcion": "Corte de cabello profesional",
    "duracionMinutos": 30,
    "precio": 30000,
    "capacidadMaxima": 1,
    "activo": true,
    "idProveedor": "[UUID-PROVEEDOR]"
}
```
**Nota:** Solo el proveedor creador del servicio puede actualizarlo.

---

### 22. Actualizar Servicio Ofertado de Otro Proveedor (403)

**Nombre:** Update Service Offering - Forbidden (Not Owner)
**URL:** `http://localhost:8082/api/catalog/services/[UUID-SERVICIO-OTRO-PROVEEDOR]`
**Método:** PUT
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Body:**
```json
{
    "precio": 30000
}
```
**Código esperado:** 403 Forbidden
**Response esperado:**
```json
{
    "timestamp": "2026-04-16T12:00:00",
    "status": 403,
    "error": "Forbidden",
    "message": "No tiene permiso para modificar este servicio. Solo el proveedor creador puede modificarlo."
}
```

---

### 23. Desactivar Servicio Ofertado (Soft Delete) (Requiere ROLE_PROVEEDOR)

**Nombre:** Deactivate Service Offering - Success
**URL:** `http://localhost:8082/api/catalog/services/[UUID-SERVICIO]`
**Método:** DELETE
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Código esperado:** 204 No Content
**Response esperado:** (vacío)

---

### 24. Desactivar Servicio Ya Inactivo (409)

**Nombre:** Deactivate Service Offering - Already Inactive
**URL:** `http://localhost:8082/api/catalog/services/[UUID-SERVICIO-INACTIVO]`
**Método:** DELETE
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Código esperado:** 409 Conflict
**Response esperado:**
```json
{
    "timestamp": "2026-04-16T12:00:00",
    "status": 409,
    "error": "Conflict",
    "message": "El servicio ya está inactivo con id: [UUID-SERVICIO-INACTIVO]"
}
```

---

### 25. Eliminar Servicio Permanentemente (Hard Delete) (Requiere ROLE_ADMIN)

**Nombre:** Permanently Delete Service Offering - Success
**URL:** `http://localhost:8082/api/catalog/services/[UUID-SERVICIO]/permanent`
**Método:** DELETE
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_ADMIN]
```
**Código esperado:** 204 No Content
**Response esperado:** (vacío)

---

### 26. Listar Servicios del Proveedor (Requiere ROLE_PROVEEDOR)

**Nombre:** Get Services by Provider - Success
**URL:** `http://localhost:8082/api/catalog/services/provider`
**Método:** GET
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Código esperado:** 200 OK
**Response esperado:**
```json
[
    {
        "id": "[UUID-SERVICIO-1]",
        "nombreServicio": "Corte de Cabello",
        "descripcion": "Corte de cabello profesional",
        "duracionMinutos": 30,
        "precio": 25000,
        "capacidadMaxima": 1,
        "activo": true,
        "idProveedor": "[UUID-PROVEEDOR]"
    },
    {
        "id": "[UUID-SERVICIO-2]",
        "nombreServicio": "Manicura",
        "descripcion": "Manicura profesional",
        "duracionMinutos": 45,
        "precio": 20000,
        "capacidadMaxima": 2,
        "activo": true,
        "idProveedor": "[UUID-PROVEEDOR]"
    }
]
```

---

### 27. Listar Todos los Servicios Activos (Público)

**Nombre:** Get All Active Services - Success
**URL:** `http://localhost:8082/api/catalog/services/active`
**Método:** GET
**Headers:**
```
Content-Type: application/json
```
**Código esperado:** 200 OK
**Response esperado:**
```json
[
    {
        "id": "[UUID-SERVICIO-1]",
        "nombreServicio": "Corte de Cabello",
        "descripcion": "Corte de cabello profesional",
        "duracionMinutos": 30,
        "precio": 25000,
        "capacidadMaxima": 1,
        "activo": true,
        "idProveedor": "[UUID-PROVEEDOR-1]"
    },
    {
        "id": "[UUID-SERVICIO-2]",
        "nombreServicio": "Masaje",
        "descripcion": "Masaje relajante",
        "duracionMinutos": 60,
        "precio": 50000,
        "capacidadMaxima": 1,
        "activo": true,
        "idProveedor": "[UUID-PROVEEDOR-2]"
    }
]
```
**Nota:** Este endpoint es público y es usado por clientes para ver servicios disponibles.

---

### 28. Listar Servicios Activos por Categoría (Público)

**Nombre:** Get Active Services by Category - Success
**URL:** `http://localhost:8082/api/catalog/services/active/category/[UUID-CATEGORIA]`
**Método:** GET
**Headers:**
```
Content-Type: application/json
```
**Código esperado:** 200 OK
**Response esperado:**
```json
[
    {
        "id": "[UUID-SERVICIO-1]",
        "nombreServicio": "Corte de Cabello",
        "descripcion": "Corte de cabello profesional",
        "duracionMinutos": 30,
        "precio": 25000,
        "capacidadMaxima": 1,
        "activo": true,
        "idProveedor": "[UUID-PROVEEDOR-1]"
    },
    {
        "id": "[UUID-SERVICIO-2]",
        "nombreServicio": "Manicura",
        "descripcion": "Manicura profesional",
        "duracionMinutos": 45,
        "precio": 20000,
        "capacidadMaxima": 2,
        "activo": true,
        "idProveedor": "[UUID-PROVEEDOR-2]"
    }
]
```
**Nota:** Este endpoint retorna solo servicios activos de proveedores que pertenecen a la categoría especificada.

---

### 29. Listar Servicios Activos por Categoría Inactiva (404)

**Nombre:** Get Active Services by Category - Inactive Category
**URL:** `http://localhost:8082/api/catalog/services/active/category/[UUID-CATEGORIA-INACTIVA]`
**Método:** GET
**Headers:**
```
Content-Type: application/json
```
**Código esperado:** 409 Conflict
**Response esperado:**
```json
{
    "timestamp": "2026-04-16T12:00:00",
    "status": 409,
    "error": "Conflict",
    "message": "La categoría con id [UUID-CATEGORIA-INACTIVA] está inactiva"
}
```

---

## Health Check

### 30. Health Check

**Nombre:** Health Check - Success
**URL:** `http://localhost:8082/api/`
**Método:** GET
**Código esperado:** 200 OK
**Response esperado:**
```json
{
    "status": "UP"
}
```

---

### 31. Version Check

**Nombre:** Version Check - Success
**URL:** `http://localhost:8082/api/version`
**Método:** GET
**Código esperado:** 200 OK
**Response esperado:**
```json
{
    "version": "0.0.1-SNAPSHOT"
}
```

---

## Notas Importantes

### Obtención de Tokens JWT

Para obtener los tokens JWT necesarios para las pruebas que requieren autenticación:

1. **Token de Admin:**
   - Registra un usuario con rol ADMIN en el Auth Service
   - Inicia sesión: `POST http://localhost:8081/api/auth/login`
   - Usa el `accessToken` retornado

2. **Token de Proveedor:**
   - Registra un usuario con rol PROVEEDOR en el Auth Service
   - Inicia sesión: `POST http://localhost:8081/api/auth/login`
   - Usa el `accessToken` retornado

3. **Token de Cliente:**
   - Registra un usuario con rol CLIENTE en el Auth Service
   - Inicia sesión: `POST http://localhost:8081/api/auth/login`
   - Usa el `accessToken` retornado

### IDs de Prueba

Para las pruebas que requieren IDs específicos, puedes obtenerlos de:
- **Categorías:** Ejecuta primero la prueba #1 (Get All Categories) y usa los IDs retornados
- **Proveedores:** Registra un proveedor en el Auth Service y usa el ID retornado

### Orden de Ejecución Recomendado

Para una ejecución ordenada de las pruebas:
1. Primero ejecuta las pruebas públicas (1-4, 21-22)
2. Luego ejecuta las pruebas de creación de categorías (5-8)
3. Luego ejecuta las pruebas de actualización/desactivación/activación (9-14)
4. Finalmente ejecuta las pruebas de servicios ofertados (15-20)

### Precondiciones

Antes de ejecutar estas pruebas, asegúrate de:
1. Tener el Auth Service corriendo en `http://localhost:8081`
2. Tener el Catalog Service corriendo en `http://localhost:8082`
3. Tener usuarios registrados en el Auth Service con los roles necesarios (ADMIN, PROVEEDOR, CLIENTE)
4. Tener la base de datos configurada y accesible
