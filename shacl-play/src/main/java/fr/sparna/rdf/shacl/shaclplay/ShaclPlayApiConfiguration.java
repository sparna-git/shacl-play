package fr.sparna.rdf.shacl.shaclplay;

import fr.sparna.rdf.shacl.shaclplay.swagger.SwaggerUICustom;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/*
 * Permet de définir les informations principales de la page API Swagger
 */
@OpenAPIDefinition(
        info = @Info(
                title = "SHACL Play REST API",
                summary = "This API allows you to call the various features of SHACL Play from your own application."
        )
)
/*
  Class de configuration pour Spring, elle est chargée par WEB-INF/spring/spring-servlet-xml#<context:component-scan base-package="fr.sparna.rdf.shacl" />
  Permet de rajouter des beans ou autre élément dans le contexte de Spring.
 */
@Configuration

/*
 * Permet d'activer l'auto-configuration avec Spring-boot: rajout de la dépendance dans le POM : spring-boot-autoconfigure
 * Spring-boot scan le classpath et cherche les dossiers META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports qui listent
 * les class de @Configuration à charger dans le contexte de Spring.
 * ex: springdoc-openapi-starter-webmvc

 * -> springdoc-openapi-starter-webmcv-api:2.8.1
 * -> springdoc-openapi-starter-webmcv-api:2.8.1.jar
 * -> META-INF
 * -> spring
 * -> org.springframework.boot.autoconfigure.AutoConfiguration.imports
 * org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration        <----- C'est une class de @Configuration springdoc qui va être chargée dans le contexte de Spring
 * org.springdoc.webmvc.core.configuration.MultipleOpenApiSupportConfiguration <----- C'est une autre class de @Configuration springdoc qui va être chargée dans le contexte de Spring
 */
@EnableAutoConfiguration
public class ShaclPlayApiConfiguration {

    /**
     * Permet de réecrire dans la page css de springdoc pour override des propriétés css.
     * voir {@link SwaggerUICustom}.
     */
    @Bean
    @Primary
    public SwaggerIndexPageTransformer swaggerIndexPageTransformer(
            SwaggerUiConfigProperties a,
            SwaggerUiOAuthProperties b,
            SwaggerWelcomeCommon c,
            ObjectMapperProvider d
    ){
        return new SwaggerUICustom(a,b,c,d);
    }

    /*
    Permet de customiser le rendu de la page swagger UNE FOIS la spécification faite par springdoc-open api
     */
    @Bean
    public OpenApiCustomizer customizer(ApplicationData data){
        return openApi -> {
            openApi.getInfo().setVersion(data.getBuildVersion());
        };
    }



}
