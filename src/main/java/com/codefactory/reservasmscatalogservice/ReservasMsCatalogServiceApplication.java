package com.codefactory.reservasmscatalogservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "JWT token obtenido del endpoint /api/auth/login del Auth Service. Usar el campo 'accessToken' de la respuesta."
)
@OpenAPIDefinition(
    info = @Info(
        title = "Plataforma de Reservas de Servicios - MS-Catalog-Service",
        description = "Microservicio de catálogo para la plataforma de reservas de servicios. " +
                     "Proporciona funcionalidades de gestión de categorías y servicios ofertados por proveedores.",
        version = "1.0.0",
        contact = @Contact(
            name = "Equipo EAV04",
            email = "isabela.bedoya@udea.edu.co",
            url = "https://github.com/Isa-Bedoya-UdeA/Reservas-MS-Catalog-Service"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            description = "Servidor de Desarrollo",
            url = "http://localhost:8082"
        ),
        @Server(
            description = "Servidor de Producción",
            url = "https://reservas-ms-catalog-service.onrender.com"
        )
    },
    security = {
        @SecurityRequirement(name = "bearerAuth")
    }
)
public class ReservasMsCatalogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservasMsCatalogServiceApplication.class, args);
    }

}
