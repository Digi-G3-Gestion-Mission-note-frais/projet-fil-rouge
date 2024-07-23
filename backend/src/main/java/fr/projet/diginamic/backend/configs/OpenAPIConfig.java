package fr.projet.diginamic.backend.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(info = @Info(contact = @Contact(name = "Groupe 3 - Digi-G3-Gestion-Mission-note-frais", url = "https://github.com/Digi-G3-Gestion-Mission-note-frais/projet-fil-rouge"), title = "Fil Rouge API", version = "1.0", description = "API for managing missions"), servers = {
        @Server(description = "Local server", url = "http://localhost:8080")
})
@SecurityScheme(name = "bearerAuth", description = "JWT Authorization header using the Bearer scheme", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class OpenAPIConfig {

}
