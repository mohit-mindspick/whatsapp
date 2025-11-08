package com.assetneuron.whatsapp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI whatsAppOpenAPI() {
        Contact contact = new Contact();
        contact.setEmail("support@assetneuron.com");
        contact.setName("AssetNeuron Support");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("WhatsApp Management API")
                .version("1.0.0")
                .contact(contact)
                .description("API documentation for WhatsApp Management System. " +
                        "This API provides endpoints for managing WhatsApp messages, " +
                        "and related entities.")
                .license(mitLicense);

        // Define JWT Bearer token security scheme
        SecurityScheme jwtSecurityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Enter JWT token in the format: Bearer {token}");

        // Add security requirement (optional - makes it available globally)
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearer-jwt");

        return new OpenAPI()
                .info(info)
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", jwtSecurityScheme))
                .addSecurityItem(securityRequirement);
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            List<Server> servers = new ArrayList<>();

            // Try to get the actual request URL from the current request
            try {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    String scheme = request.getScheme(); // http or https
                    String serverName = request.getServerName(); // hostname or IP
                    int serverPort = request.getServerPort();
                    String requestContextPath = request.getContextPath();

                    // Build the server URL from the actual request
                    String serverUrl;
                    if ((scheme.equals("http") && serverPort == 80) ||
                            (scheme.equals("https") && serverPort == 443)) {
                        // Standard ports, omit port number
                        serverUrl = String.format("%s://%s%s", scheme, serverName, requestContextPath);
                    } else {
                        // Non-standard port, include it
                        serverUrl = String.format("%s://%s:%d%s", scheme, serverName, serverPort, requestContextPath);
                    }

                    Server currentServer = new Server();
                    currentServer.setUrl(serverUrl);
                    currentServer.setDescription("Development Server");
                    servers.add(currentServer);
                }
            } catch (Exception e) {
                // If we can't get the request context, fall back to default
                // This can happen during application startup or in certain contexts
            }
            openApi.setServers(servers);
        };
    }
}

