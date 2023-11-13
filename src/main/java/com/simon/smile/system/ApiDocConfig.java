package com.simon.smile.system;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Simon",
                        email = "simon@smial.com",
                        url = "https://www.smile.project.cn"
                ),
                description = "OpenAPI documentation with Spring Security",
                title = "API Document - Smile",
                version = "v1.0",
                license = @License(
                        name = "MIT",
                        url = "https://mit-license.org/"
                ),
                termsOfService = "Terms of service"
        ),
        servers = {
                @Server(
                        description = "Develop ENV",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Production ENV",
                        url = "https://www.smile.project.cn"
                ),
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT authorization header using the Bearer scheme. Example: \"Authorization: Bearer {token}\"",
        scheme = "Bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class ApiDocConfig {
}
