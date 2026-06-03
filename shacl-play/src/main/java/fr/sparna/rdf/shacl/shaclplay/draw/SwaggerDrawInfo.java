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


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Parameters(
        {
                @Parameter(
                        name = "format",
                        description = "RAJOUTER DESCRIPTION",
                        schema = @Schema(
                                allowableValues = {"SVG", "PNG", "TXT", "HTML"},
                                defaultValue = "SVG"),
                        in = ParameterIn.QUERY),

                @Parameter(
                        name = "hideProperties",
                        description = "Don't display anything inside the boxes, just keep the arrows to show the skeleton of the diagram\n",
                        in = ParameterIn.QUERY),
        }
)
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
public @interface SwaggerDrawInfo {
}
