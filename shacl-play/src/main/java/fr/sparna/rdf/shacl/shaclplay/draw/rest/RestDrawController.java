package fr.sparna.rdf.shacl.shaclplay.draw.rest;


import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalogService;
import fr.sparna.rdf.shacl.shaclplay.draw.DrawFormat;
import fr.sparna.rdf.shacl.shaclplay.draw.SwaggerDrawInfo;
import fr.sparna.rdf.shacl.shaclplay.draw.service.DrawService;
import fr.sparna.rdf.shacl.shaclplay.exception.DrawException;
import fr.sparna.rdf.shacl.shaclplay.exception.ExceptionManager;
import fr.sparna.rdf.shacl.shaclplay.exception.RestExceptionRenderer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Draw")
@RestController
@RequestMapping("/api")
public class RestDrawController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestDrawController.class);

    private final ShapesCatalogService catalogService;
    private final DrawService drawService;

    @Autowired
    public RestDrawController(ShapesCatalogService catalogService, DrawService drawService){
        this.catalogService = catalogService;
        this.drawService = drawService;
    }


    /**
     *  GET REST END POINT
     */
    @SwaggerDrawInfo
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Generates one or more diagram from a SHACL file URL. Splitting multiple diagrams is based on the presence of foaf:depiction annotations on NodeShapes.")
    @GetMapping(
            value = "/draw",
            produces = {"image/svg+xml", "image/png", "text/html", "text/plain"})
    public ResponseEntity<ByteArrayResource> getDraw(
            @RequestParam(value="url", required=true) String shapesUrl,
            @RequestParam(value="format", required=false, defaultValue = "svg") String clientFormat,
            @RequestParam(value="hideProperties", required=false) boolean hideProperties
    ){
        try{

            LOGGER.debug("drawUrl(shapesUrl='{}')", shapesUrl);

            // read format
            DrawFormat drawFormat = DrawFormat.valueOf(clientFormat.toUpperCase());

            Model model = ModelFactory.createDefaultModel();
            ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
            modelPopulator.populateModelFromUrl(model, shapesUrl);

            LOGGER.debug("Done Loading Shapes. Model contains {} triples", model.size());

            return this.drawService.doOutputDiagram(
                    model,
                    modelPopulator.getSourceName(),
                    drawFormat,
                    hideProperties
            );

        }catch (Exception ex){
            ExceptionManager.throwException(DrawException.class, ex.getMessage());
        }
        return ResponseEntity.badRequest().build();
    }


    /**
     *  POST REST END POINT
     */
    @SwaggerDrawInfo
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Generates one or more diagram from a SHACL file uploaded as a multipart form data. Splitting multiple diagrams is based on the presence of foaf:depiction annotations on NodeShapes.")
    @PostMapping(
            value = "/draw",
            consumes = "multipart/form-data",
            produces = {"image/svg+xml", "image/png", "text/html", "text/plain"})
    public ResponseEntity<ByteArrayResource> postDraw(
            @Parameter(
                    name = "inputShapeFile",
                    description = "The SHACL file to be used for generating the diagram.",
                    required = true
            )
            @RequestParam(value="inputShapeFile", required=true) List<MultipartFile> shapesFiles,
            @RequestParam(value="hideProperties", required=false) boolean hideProperties,
            @RequestParam(value="format", required=false, defaultValue = "svg") String clientFormat
    ){
        try{
            // read format
            DrawFormat drawFormat = DrawFormat.valueOf(clientFormat.toUpperCase());
            // initialize shapes first
            if (shapesFiles == null) LOGGER.debug("shapesFiles=null");
            else LOGGER.debug("shapesFiles.length={}", shapesFiles.size());

            Model model = ModelFactory.createDefaultModel();
            ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
            modelPopulator.populateModel(
                    model,
                    ControllerModelFactory.SOURCE_TYPE.FILE,
                    null,
                    null,
                    shapesFiles,
                    null
            );

            LOGGER.debug("Done Loading Shapes. Model contains {} triples", model.size());

            return this.drawService.doOutputDiagram(
                model,
                modelPopulator.getSourceName(),
                drawFormat,
                hideProperties
            );

        }catch (Exception ex){
            ExceptionManager.throwException(DrawException.class, ex.getMessage());
        }
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(value = DrawException.class, produces = "application/json")
    public ResponseEntity<RestExceptionRenderer> handleExceptionForRestDrawController(DrawException ex){
        return ExceptionManager.prepareRestExceptionRenderer(ex, HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }
}
