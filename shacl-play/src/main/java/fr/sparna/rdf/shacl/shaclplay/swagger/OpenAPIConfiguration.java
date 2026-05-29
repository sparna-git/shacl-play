package fr.sparna.rdf.shacl.shaclplay.swagger;


import fr.sparna.rdf.xls2rdf.web.SwaggerUICustom;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@OpenAPIDefinition(
        info = @Info(
                title = "titre",
                summary = "Résumé api ...",
                description = "description api ...",
                version = "version api ...")
)
@Configuration
@EnableAutoConfiguration
public class OpenAPIConfiguration {

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
}
