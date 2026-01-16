package eki.ekilex.api.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class ApiDescriptionLoader {

    public static String loadDescription(String resourcePath) {
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            return new String(Files.readAllBytes(Paths.get(resource.getURI())), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load API description from " + resourcePath, e);
        }
    }

    public static String loadApiDescription() {
        return loadDescription("api-description.md");
    }
}