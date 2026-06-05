package fr.sparna.rdf.shacl.shaclplay.doc;


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
                        name = "includeDiagram",
                        description = "Check if you want the full UML diagram to be included in the generated documentation. Not all structure of Shapes file can produce a nice UML diagram. Individual section diagrams are controlled by another parameter.",
                        in = ParameterIn.QUERY),

                @Parameter(
                        name = "sectionDiagram",
                        description = "Check if you want a small UML diagram to be included in each section of the generated documentation.",
                        in = ParameterIn.QUERY),

                @Parameter(
                        name = "hideProperties",
                        description = "Hide datatype properties in the generated diagrams."),

                @Parameter(
                        name = "format",
                        description = "Format of the generated documentation.",
                        schema = @Schema(allowableValues = {"HTML", "PDF", "XML", "HTML_RESPEC"}, defaultValue = "HTML_RESPEC"),
                        in = ParameterIn.QUERY),

                @Parameter(
                        name = "inputLogo",
                        description = "A logo to be included at the top of the generated documentation. The logo must be accessible at a URL. SVG is not supported if printed in PDF.",
                        in = ParameterIn.QUERY),

                @Parameter(
                        name = "language",
                        description = "The 2-letters language code of the labels and notes to be read from the SHACL file.",
                        in = ParameterIn.QUERY)

        }
)
@ApiResponses(
        {
                @ApiResponse(responseCode = "201",
                        description = "The documentation has been created successfully.",
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
                        description = "Something went wrong during the documentation process. Please verify your URL, your local file or your SHACL rules;",
                        content = {@Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = RestExceptionRenderer.class)
                        )
                        })
        })
public @interface SwaggerDocInfo {
}
