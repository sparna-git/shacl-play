package fr.sparna.rdf.xls2rdf.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SwaggerUICustom extends SwaggerIndexPageTransformer {

    public SwaggerUICustom(SwaggerUiConfigProperties swaggerUiConfig, SwaggerUiOAuthProperties swaggerUiOAuthProperties, SwaggerWelcomeCommon swaggerWelcomeCommon, ObjectMapperProvider objectMapperProvider) {
        super(swaggerUiConfig, swaggerUiOAuthProperties, swaggerWelcomeCommon, objectMapperProvider);
    }

    @Override
    public Resource transform(HttpServletRequest request, Resource resource, ResourceTransformerChain transformerChain) throws IOException {

        if (resource.toString().contains("swagger-ui.css")) {
            final InputStream is = resource.getInputStream();
            String css = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            css += """
/* =========================================================
   CUSTOM SWAGGER UI THEME OVERRIDES
   ========================================================= */

/* Masquage de la top bar et du sélecteur de serveur */
.swagger-ui .topbar,
.swagger-ui .scheme-container {
    display: none;
}
                    """;
            return new TransformedResource(resource, css.getBytes(StandardCharsets.UTF_8));
        }
        return super.transform(request, resource, transformerChain);
    }
}