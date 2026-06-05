package fr.sparna.rdf.shacl.shaclplay.swagger;

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

html.dark-mode,
html.dark-mode body,
html.dark-mode .swagger-ui,
html.dark-mode .swagger-ui .wrapper,
html.dark-mode .swagger-ui .scheme-container,
html.dark-mode .swagger-ui .information-container {
    background: #ffffff !important;
    color: #3b4151 !important;
}

/* Texte global */
html.dark-mode .swagger-ui,
html.dark-mode .swagger-ui * {
    color: #3b4151 !important;
}

/* Garder les badges GET/POST/etc lisibles */
html.dark-mode .swagger-ui .opblock-summary-method {
    color: #ffffff !important;
}

/* Blocs d'opérations */
html.dark-mode .swagger-ui .opblock,
html.dark-mode .swagger-ui .opblock-body,
html.dark-mode .swagger-ui .opblock-section-header,
html.dark-mode .swagger-ui .opblock-description-wrapper,
html.dark-mode .swagger-ui .responses-wrapper,
html.dark-mode .swagger-ui .parameters-container {
    background: #ffffff !important;
}

/* Inputs */
html.dark-mode .swagger-ui input,
html.dark-mode .swagger-ui textarea,
html.dark-mode .swagger-ui select {
    background: #ffffff !important;
    color: #3b4151 !important;
    border-color: #d9d9d9 !important;
}

/* Code examples */
html.dark-mode .swagger-ui pre,
html.dark-mode .swagger-ui pre.microlight,
html.dark-mode .swagger-ui code,
html.dark-mode .swagger-ui .highlight-code,
html.dark-mode .swagger-ui .highlight-code pre {
    background: #f7f7f7 !important;
    color: #3b4151 !important;
}

/* Forcer la couleur du contenu des blocs de code */
html.dark-mode .swagger-ui pre *,
html.dark-mode .swagger-ui code *,
html.dark-mode .swagger-ui .highlight-code *,
html.dark-mode .swagger-ui .microlight * {
    color: #3b4151 !important;
    background: transparent !important;
}

/* Modèles / Schémas */
html.dark-mode .swagger-ui section.models,
html.dark-mode .swagger-ui section.models.is-open,
html.dark-mode .swagger-ui .model-box,
html.dark-mode .swagger-ui .model-container,
html.dark-mode .swagger-ui .model,
html.dark-mode .swagger-ui .json-schema-2020-12,
html.dark-mode .swagger-ui .json-schema-2020-12-body,
html.dark-mode .swagger-ui .json-schema-2020-12-json-viewer {
    background: #ffffff !important;
    color: #3b4151 !important;
}

/* Tout le contenu des schémas */
html.dark-mode .swagger-ui section.models *,
html.dark-mode .swagger-ui .model *,
html.dark-mode .swagger-ui .model-box *,
html.dark-mode .swagger-ui .json-schema-2020-12 *,
html.dark-mode .swagger-ui .json-schema-2020-12-json-viewer * {
    color: #3b4151 !important;
}

/* Tables */
html.dark-mode .swagger-ui table,
html.dark-mode .swagger-ui table tbody,
html.dark-mode .swagger-ui table tr,
html.dark-mode .swagger-ui table td,
html.dark-mode .swagger-ui table th {
    background: #ffffff !important;
    color: #3b4151 !important;
}

/* Onglets Example Value / Schema */
html.dark-mode .swagger-ui .tab,
html.dark-mode .swagger-ui .tab li,
html.dark-mode .swagger-ui .tablinks {
    background: #ffffff !important;
    color: #3b4151 !important;
}

html.dark-mode .swagger-ui .opblock pre.microlight,
html.dark-mode .swagger-ui .opblock .highlight-code pre.microlight,
html.dark-mode .swagger-ui .opblock-body pre.microlight,
html.dark-mode .swagger-ui .highlight-code > .microlight {
    background: #f7f7f7 !important;
    color: #3b4151 !important;
}

html.dark-mode .swagger-ui .opblock pre.microlight *,
html.dark-mode .swagger-ui .opblock .highlight-code pre.microlight *,
html.dark-mode .swagger-ui .opblock-body pre.microlight *,
html.dark-mode .swagger-ui .highlight-code > .microlight * {
    background: transparent !important;
    color: #3b4151 !important;
}

html.dark-mode .swagger-ui .json-schema-2020-12,
html.dark-mode .swagger-ui .json-schema-2020-12 button,
html.dark-mode .swagger-ui .json-schema-2020-12-body,
html.dark-mode .swagger-ui .json-schema-2020-12-head,
html.dark-mode .swagger-ui .json-schema-2020-12-json-viewer,
html.dark-mode .swagger-ui .model-box,
html.dark-mode .swagger-ui section.models .model-container {
    background: #ffffff !important;
    color: #3b4151 !important;
}

html.dark-mode .swagger-ui .json-schema-2020-12 *,
html.dark-mode .swagger-ui .model-box *,
html.dark-mode .swagger-ui section.models .model-container * {
    color: #3b4151 !important;
}

html.dark-mode .swagger-ui .json-schema-2020-12 button svg,
html.dark-mode .swagger-ui .models-control svg,
html.dark-mode .swagger-ui .model-toggle::after {
    fill: #3b4151 !important;
}

html.dark-mode .swagger-ui .info .title small {
    background: #3b4151 !important;
    color: white !important;
    border: 1px solid #3b4151 !important;
}

/* Badge OAS */
html.dark-mode .swagger-ui .info .title small.version-stamp {
    background: #49cc90 !important;
    color: white !important;
    border: 1px solid #49cc90 !important;
}

html.dark-mode .swagger-ui .info .title small pre {
    background: transparent !important;
    color: inherit !important;
}
                    """;
            return new TransformedResource(resource, css.getBytes(StandardCharsets.UTF_8));
        }
        return super.transform(request, resource, transformerChain);
    }
}