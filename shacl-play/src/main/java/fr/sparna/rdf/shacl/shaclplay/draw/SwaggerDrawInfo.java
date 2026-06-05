package fr.sparna.rdf.shacl.shaclplay.draw;

import fr.sparna.rdf.shacl.shaclplay.exception.RestExceptionRenderer;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;


//@Target permet d'indiquer sur quel élément l'annotation peut être mise ex: METHOD, PARAMETER, TYPE ... @Target(METHOD) => l'annotation ne peut être mise que sur des méthodes.
@Target(ElementType.METHOD)
//@Retention(RUNTIME) permet que l'annotation soit conservée au runtime et accessible par l'API Reflection de Java : ex: Lire et récupérer le contenu d'une annotation sur une class, une méthode etc.
//Autrement dit elle est accessible en tant que meta donnée à l'exécution.
@Retention(RetentionPolicy.RUNTIME)
//@Documented permet qyue l'annotation apparaisse dans la Javadoc produite pour une class.
@Documented
//Conteneur à @Parameter, permet d'indiquer la documentation Swagger des paramètres d'un endpoint
@Parameters(
        {
                @Parameter(
                        name = "format",
                        description = "Format of the generated diagram. 'TXT' is for the raw PlantUML code.",
                        schema = @Schema(
                                allowableValues = {"SVG", "PNG", "TXT", "HTML"},
                                defaultValue = "SVG"),
                        in = ParameterIn.QUERY),

                @Parameter(
                        name = "hideProperties",
                        description = "Don't display anything inside the boxes, just keep the arrows to show the skeleton of the diagram.",
                        in = ParameterIn.QUERY),
        }
)
//Conteneur à @ApiResponse, permet de documenter les réponses qui peuvent être faite pour un endpoint
@ApiResponses(
        {
                @ApiResponse(responseCode = "201",
                        description = "The UML Diagram has been created successfully.",
                        content = {
                                @Content(mediaType = "text/html"),
                                @Content(mediaType = "text/xml"),
                                @Content(mediaType = "application/pdf")
                        }),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error.",
                        content = {@Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = RestExceptionRenderer.class)
                        )
                        }),
                @ApiResponse(
                        responseCode = "400",
                        description = "Something went wrong during the drawing process. Please verify your URL, your local file or your SHACL rules;",
                        content = {@Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = RestExceptionRenderer.class)
                        )
                        })
        })
//Annotation qui agrége les annotations précédentes
public @interface SwaggerDrawInfo {
}
