package eki.ekilex.api.controller.config;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Ekilex API",
                version = "1.0",
                description = "**Overview**  \n" +
                        "\n" +
                        "The [Ekilex API](https://github.com/keeleinstituut/ekilex) is a RESTful web service that " +
                        "provides programmatic access to the [Ekilex](https://ekilex.ee) " +
                        "(Estonian Dictionary and Terminology Database System) maintained by the " +
                        "[Estonian Language Institute](https://eki.ee) (Eesti Keele Instituut – EKI).  \n" +
                        "The API enables developers, researchers, and language professionals to query and retrieve " +
                        "lexical data such as words, definitions, meanings, and morphological information.\n" +
                        "\n" +
                        "**Main Purpose**  \n" +
                        "\n" +
                        "Ekilex serves as the central dictionary and terminology database system for the Estonian language, " +
                        "consolidating Estonian vocabulary and terminology into a unified environment.  \n" +
                        "It supports searching for words and terms with flexible wildcard patterns, retrieving " +
                        "detailed information about lexemes, meanings, word paradigms, and source references  \n" +
                        "\n" +
                        "The API facilitates:\n" +
                        "* **Lexical Data Access:** Search and retrieve comprehensive information about Estonian words, " +
                        "including their forms, definitions, usage examples, and linguistic properties  \n" +
                        "* **Terminology Management:** Access specialized terminology from over 150 domain-specific " +
                        "dictionaries and terminology databases  \n" +
                        "* **Multilingual data:** Datasets in Ekilex database include entries in multiple languages " +
                        "incl. english, russian, latvian, norwegian etc.  \n" +
                        "* **Linguistic Research:** Query morphological, etymological, and semantic relationships between words  \n" +
                        "* **Application Integration:** Enable third-party applications, language tools, and " +
                        "educational platforms to incorporate Estonian language data  \n" +
                        "\n" +
                        "\n" +
                        "The data managed through Ekilex is publicly accessible via **[Sõnaveeb](https://sonaveeb.ee) " +
                        "(WordWeb)**, Estonia's primary language portal.\n" +
                        "\n" +
                        "---\n" +
                        "<details>\n" +
                        "<summary><strong>Intended Audience</strong></summary>\n" +
                        "\n" +
                        "The Ekilex API is intended for:\n" +
                        "- **Lexicographers and terminologists** working on Estonian dictionaries and terminology databases  \n" +
                        "- **Linguists and researchers** conducting computational linguistics studies  \n" +
                        "- **Institutional partners** requiring programmatic access to authoritative Estonian language resources  \n" +
                        "- **Software developers** integrating Estonian language data into applications and digital tools  \n" +
                        "\n" +
                        "  Developer use cases: \n" +
                        "  - **Application integration**: Connect lexical resources to dictionaries, spell checkers, learning applications, translation systems, or text-processing tools  \n" +
                        "  - **NLP tasks**: Use data for morphological analysis, lemmatization, and semantic search  \n" +
                        "  - **Custom tool development**: Build applications for linguistic research or vocabulary training  \n" +
                        "  - **Automation**: Programmatically retrieve, update, and synchronize lexical data  \n" +
                        "  - **Open-source collaboration**: Extend functionality or adapt the API via the public codebase \n " +
                        "\n" +
                        "Each user receives a unique API key linked to their Ekilex account, with permissions aligned to their role and dataset access rights.\n" +
                        "</details>\n" +
                        "---\n" +
                        "<details>\n" +
                        "<summary><strong>Authentication & Authorization</strong></summary>\n" +
                        "\n" +
                        "### API Key Requirements\n" +
                        "\n" +
                        "**Obtaining an API Key:**\n" +
                        "1. Register and log in to your Ekilex user account at [ekilex.ee](https://ekilex.ee)  \n" +
                        "2. Navigate to your user profile page  \n" +
                        "3. Generate an API key (only one key per user; generating a new key replaces the existing one)\n" +
                        "\n" +
                        "**Authentication Header:**  \n" +
                        "Example:  \n " +
                        "  \n" +
                        "<code>ekilex-api-key: cd8c61505b17423282550623464fcace</code>  \n" +
                        "  \n" +
                        "Include this header in all API requests.  \n" +
                        "Since each API key is associated with a particular user, all data modification actions are logged by that name.  \n" +
                        "\n" +
                        "---\n" +
                        "\n" +
                        "### Permission Model\n" +
                        "* **Read Operations:** All authenticated users can query and retrieve data  \n" +
                        "* **CRUD Operations:** Creating, updating, and deleting data requires special permissions:  \n" +
                        "  * Admin rights, **or**  \n" +
                        "  * CRUD permissions granted by an administrator via the Ekilex permissions page  \n" +
                        "* **Dataset Permissions:** The same permission logic applies as in the Ekilex web interface  \n" +
                        "* **Action Logging:** All modifications are logged under the API key owner's username  \n" +
                        "</details>\n" +
                        "---\n" +
                        "\n" +
                        "<details>\n" +
                        "<summary><strong>Common Error Responses</strong></summary>\n" +
                        "\n" +
                        "The API returns structured error responses in JSON format. Below are the common error scenarios you may encounter:\n" +
                        "\n" +
                        "**400 - Bad Request**  \n" +
                        "The request contains invalid parameters or malformed input.\n" +
                        "\n" +
                        "\n" +
                        "**403 - Forbidden**  \n" +
                        "Authentication failed or user lacks permission to access the requested resource.\n" +
                        "\n" +
                        "\n" +
                        "**404 - Not Found**  \n" +
                        "The requested resource does not exist in the database.\n" +
                        "\n" +
                        "\n" +
                        "**Response Format**  \n" +
                        "All error responses include the following fields:\n" +
                        "\n" +
                        "| Field | Type | Description |\n" +
                        "| --- | --- | --- |\n" +
                        "| `status` | Integer | HTTP status code |\n" +
                        "| `error` | String | HTTP status name (e.g., \"Bad Request\", \"Forbidden\", \"Not Found\") |\n" +
                        "| `code` | String | Machine-readable error code for programmatic handling |\n" +
                        "| `message` | String | Human-readable error description |\n" +
                        "| `path` | String | Request path that caused the error |\n" +
                        "| `timestamp` | String | ISO 8601 timestamp when the error occurred |\n" +
                        "\n" +
                        "**Error Handling Best Practices**\n" +
                        "\n" +
                        "1. **Always check the HTTP status code** - Use it to determine error category  \n" +
                        "2. **Use the error `code` field** - Machine-readable codes enable programmatic error handling  \n" +
                        "3. **Log the `timestamp` and `path`** - Helps with debugging and support requests  \n" +
                        "4. **Handle 403 errors gracefully** - Check API key validity and user permissions:  \n" +
                        "   * Missing `ekilex-api-key` header → Add authentication header  \n" +
                        "   * Invalid API key → Verify key format and regenerate if necessary  \n" +
                        "   * Insufficient permissions → Contact administrator for CRUD role grant  \n" +
                        "5. **Retry strategy for transient errors** - 5xx errors are server issues and may be retried  \n" +
                        "\n" +
                        "</details>\n" +
                        "\n",
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
                .build();
    }

}