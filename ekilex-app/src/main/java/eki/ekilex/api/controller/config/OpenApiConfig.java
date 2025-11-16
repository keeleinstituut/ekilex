package eki.ekilex.api.controller.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                title = "Ekilex API",
                version = "1.0",
                description = "Technical documentation of the Ekilex API",
                termsOfService = "https://example.com/terms",
                contact = @Contact(
                        name = "Ekilex user support",
                        email = "kasutajatugi@ekilex.ee",
                        url = "https://keeleinstituut.github.io/ekilex/"
                ),
                license = @License(
                        name = "Name of MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {@Server(
                url = "https://ekilex.ee",
                description = "Base url"
        ),
        @Server(
                url = "http://localhost:5555",
                description = "Local development environment"
        )},
        security = @SecurityRequirement(
                name = "apiKeyAuth" // Viitab `@SecurityScheme` nimele
        ),
        externalDocs = @ExternalDocumentation(
                description = "Ekilex API documentation in Github Wiki",
                url = "https://github.com/keeleinstituut/ekilex/wiki/"
        )
)
@SecurityScheme(
        name = "apiKeyAuth",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "ekilex-api-key"
)
@Configuration
public class OpenApiConfig {

}