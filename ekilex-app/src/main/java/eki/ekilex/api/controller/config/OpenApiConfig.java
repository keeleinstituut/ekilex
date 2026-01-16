package eki.ekilex.api.controller.config;

import eki.ekilex.api.util.ApiDescriptionLoader;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Ekilex API",
                version = "1.0",
                description = "See description loaded from external api-description.md file",
                contact = @Contact(
                        name = "Ekilex user support",
                        email = "kasutajatugi@ekilex.ee"
                //Deprecated content, needs reviewing
                //        url = "https://keeleinstituut.github.io/ekilex/"
                )
        ),
        servers = {@Server(
                url = "https://ekilex.ee",
                description = "Base url"
        )
                /*,
                @Server(
                        url = "http://localhost:5555",
                        description = "Local development environment"
                )*/
        },
        security = @SecurityRequirement(
                name = "apiKeyAuth" // Refers to the name of `@SecurityScheme`
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

    /**
     * Group for public endpoints accessible with only API key authentication.
     * These endpoints perform read-only operations and return reference data.
     */
    @Bean
    public GroupedOpenApi publicApiGroup() {
        return GroupedOpenApi.builder()
                .group("1-public-endpoints")
                .displayName("1. Public API (API Key Only)")
                .pathsToMatch(
                        "/api/**"
                )
                .pathsToExclude(
                        "/**/term-meaning/**",
                        "/**/source/**",
                        "/**/freeform/**",
                        "/**/lex-word/**",
                        "/**/freq_corp/**",
                        "/**/public_word/{datasetCode}/{tagName}",
                        "/**/create",
                        "/**/update",
                        "/**/delete",
                        "/**/save",
                        "/**/join"
                )
                .addOpenApiCustomiser(customiseOpenApi())
                .build();
    }

    /**
     * Group for all endpoints, including the ones with private operations requiring special authorization.
     */
    @Bean
    public GroupedOpenApi protectedApiGroup() {
        return GroupedOpenApi.builder()
                .group("2-all-endpoints")
                .displayName("2. Private API (Admin/CRUD Roles Required)")
                .pathsToMatch(
                        "/api/**"
                )
                .addOpenApiCustomiser(customiseOpenApi())
                .build();
    }

    /**
     * Customises the OpenAPI specification with the contents of the description file.
     * This customiser is reused across multiple API groups to avoid duplication.
     */
    private OpenApiCustomiser customiseOpenApi() {
        String description = ApiDescriptionLoader.loadApiDescription();
        return openApi -> openApi.getInfo().setDescription(description);
    }
}