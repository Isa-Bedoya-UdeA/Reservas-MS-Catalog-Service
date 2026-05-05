# Variables de Entorno - Reservas-MS-Catalog-Service

Este documento describe todas las variables de entorno necesarias para el despliegue del microservicio de Catálogo de Servicios.

## Archivo de Configuración

Copia el archivo `.env.example` a `.env` y configura los valores:

```bash
cp .env.example .env
```

## Variables Requeridas

### 1. Perfil de Spring

| Variable | Descripción | Valor por Defecto | Opciones |
|----------|-------------|-------------------|----------|
| `SPRING_PROFILE` | Perfil activo de Spring Boot | `dev` | `dev`, `test`, `prod` |

### 2. Configuración de Base de Datos (Supabase)

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DB_URL` | URL JDBC de PostgreSQL (Transaction Pooler IPv4) | `jdbc:postgresql://aws-1-us-west-2.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0` |
| `DB_USER` | Usuario de la base de datos | `postgres.[PROJECT-REF]` |
| `DB_PASSWORD` | Contraseña de Supabase | `[TU-CONTRASEÑA]` |

**Nota:** Se recomienda usar el Transaction Pooler de Supabase (puerto 6543) para compatibilidad con IPv4.

### 3. Configuración JWT

| Variable | Descripción | Recomendación |
|----------|-------------|---------------|
| `JWT_SECRET` | Secreto para firmar/validar tokens JWT | Generar con: `openssl rand -base64 64` |
| `JWT_EXPIRATION` | Tiempo de expiración del token en milisegundos | `86400000` (24 horas) |

⚠️ **IMPORTANTE:** El `JWT_SECRET` debe ser el **MISMO VALOR** en todos los microservicios para que la validación de tokens funcione correctamente.

### 4. URL del Frontend

| Variable | Descripción | Valor Local | Valor Producción |
|----------|-------------|-------------|------------------|
| `FRONTEND_URL` | URL base para enlaces | `http://localhost:3000` | `https://tu-dominio.com` |

### 5. Configuración de Email (Opcional)

El Catalog Service puede usar el email para notificaciones. Si se requiere:

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `EMAIL_USERNAME` | Dirección de correo Gmail | `tucorreo@gmail.com` |
| `EMAIL_PASSWORD` | App Password de Google | `xxxx xxxx xxxx xxxx` |

### 6. URLs de Servicios Externos

| Variable | Descripción | Puerto Local | Producción |
|----------|-------------|--------------|------------|
| `SERVICES_AUTH_URL` | URL del Auth Service para validación JWT | `http://localhost:8081` | `https://ms-auth-service.onrender.com` |

## Ejemplo Completo (.env)

```bash
# ======================
# SPRING PROFILE
# ======================
SPRING_PROFILE=dev

# ======================
# DATABASE CONFIG
# ======================
DB_URL=jdbc:postgresql://aws-1-us-west-2.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0
DB_USER=postgres.[TU-PROJECT-REF]
DB_PASSWORD=[TU-CONTRASEÑA-DE-SUPABASE]

# ======================
# JWT CONFIG (MISMO EN TODOS LOS MS)
# ======================
JWT_SECRET=[TU-JWT-SECRET-SEGURA]
JWT_EXPIRATION=86400000

# ======================
# EMAIL CONFIG (Opcional)
# ======================
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password

# ======================
# FRONTEND URL
# ======================
FRONTEND_URL=http://localhost:3000

# ======================
# EXTERNAL SERVICES URLs
# ======================
SERVICES_AUTH_URL=http://localhost:8081
```

## Despliegue en Producción (Render)

Cuando despliegues en Render:

1. Configura todas las variables en el dashboard de Render
2. Actualiza `SERVICES_AUTH_URL` a la URL de producción del Auth Service
3. Asegúrate de que el `JWT_SECRET` sea idéntico al del Auth Service

## Verificación

Para verificar la configuración:

```bash
# Ver el perfil activo
cat .env | grep SPRING_PROFILE

# Iniciar el servicio
mvn spring-boot:run
```

En los logs verás la validación de las variables configuradas.

## Notas Específicas del Catalog Service

- Este microservicio **depende del Auth Service** para validar tokens JWT
- La variable `SERVICES_AUTH_URL` es crítica para la autenticación
- Si el Auth Service no está disponible, las operaciones protegidas fallarán
