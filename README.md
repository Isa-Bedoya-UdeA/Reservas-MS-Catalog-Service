# Plataforma de Reservas de Servicios - MS-Catalog-Service

[![CI/CD Pipeline](https://github.com/Isa-Bedoya-UdeA/Reservas-MS-Catalog-Service/actions/workflows/build.yml/badge.svg)](https://github.com/Isa-Bedoya-UdeA/Reservas-MS-Catalog-Service/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Isa-Bedoya-UdeA_Reservas-MS-Catalog-Service&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Isa-Bedoya-UdeA_Reservas-MS-Catalog-Service)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Isa-Bedoya-UdeA_Reservas-MS-Catalog-Service&metric=bugs)](https://sonarcloud.io/summary/new_code?id=Isa-Bedoya-UdeA_Reservas-MS-Catalog-Service)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=Isa-Bedoya-UdeA_Reservas-MS-Catalog-Service&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=Isa-Bedoya-UdeA_Reservas-MS-Catalog-Service)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Isa-Bedoya-UdeA_Reservas-MS-Catalog-Service&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Isa-Bedoya-UdeA_Reservas-MS-Catalog-Service)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=Isa-Bedoya-UdeA_Reservas-MS-Catalog-Service&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=Isa-Bedoya-UdeA_Reservas-MS-Catalog-Service)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=Isa-Bedoya-UdeA_Reservas-MS-Catalog-Service&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=Isa-Bedoya-UdeA_Reservas-MS-Catalog-Service)

## Descripción

CodeF@ctory - Caso 15 - Plataforma de Reservas de Servicios - Microservicio de Catálogo de Servicios

## Responsabilidad

Gestión de servicios ofrecidos por proveedores

## Tecnologías

### Backend

* **Java 17**
* **Spring Boot 3.5.13**
* **Spring Security** (Autenticación y autorización)
* **Spring Data JPA** (Persistencia)
* **JWT** (JSON Web Tokens para autenticación)
* **MapStruct** (Mapeo entre entidades y DTOs)
* **Lombok** (Reducción de código boilerplate)
* **Maven** (Gestión de dependencias)

### Herramientas de Desarrollo

* **Git** (Control de versiones)
* **GitHub** (Repositorio remoto)
* **Postman** (Pruebas de APIs)
* **SonarCloud** (Análisis de calidad de código)

## Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

* **JDK 17** o superior
* **Maven 3.8+**
* **Oracle Database** o **PostgreSQL**
* **Git**

## Instalación

### 1. Clonar el Repositorio

```bash
git clone https://github.com/Isa-Bedoya-UdeA/Reservas-MS-Catalog-Service
cd Reservas-MS-Catalog-Service
```

### 2. Configurar la Base de Datos y Propiedades

### 3. Configurar JWT

### 4. Compilar el Proyecto

```bash
mvn clean install -U -DskipTests 
```

### 5. Ejecutar la Aplicación

```bash
# Opción 1:
mvn spring-boot:run

# Opción 2:
mvn clean spring-boot:run

# Si falla al ejecutar, lanzar este comando, y luego nuevamente run
taskkill /F /IM java.exe 2>$null
```

La aplicación estará disponible en: `http://localhost:8082`

## Estructura del Proyecto

## Endpoints Principales

* `GET /api/`: Health Check
* `GET /api/version`: Version Check

## Pruebas en Postman

> Importante: El `Content-Type` de las peticiones debe ser `application/json`.