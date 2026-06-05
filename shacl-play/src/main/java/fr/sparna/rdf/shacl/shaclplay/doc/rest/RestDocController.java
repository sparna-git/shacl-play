package fr.sparna.rdf.shacl.shaclplay.doc.rest;


import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalogService;
import fr.sparna.rdf.shacl.shaclplay.doc.SwaggerDocInfo;
import fr.sparna.rdf.shacl.shaclplay.doc.service.DocService;
import fr.sparna.rdf.shacl.shaclplay.exception.DocException;
import fr.sparna.rdf.shacl.shaclplay.exception.ExceptionManager;
import fr.sparna.rdf.shacl.shaclplay.exception.RestExceptionRenderer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

import static fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory.SOURCE_TYPE.FILE;

@Tag(name = "Documentation")
@RestController
@RequestMapping("/api")
public class RestDocController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestDocController.class);

    private final ShapesCatalogService catalogService;
    private final DocService docService;

    @Autowired
    public RestDocController(ShapesCatalogService catalogService, DocService docService){
        this.catalogService = catalogService;
        this.docService = docService;
    }

    /**
     *  GET REST END POINT
     */
    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping(
            value = "/doc",
            produces = {"text/html", "text/xml", "application/pdf"})
    @SwaggerDocInfo
    @Operation(summary = "Generates a human-readable documentation from a SHACL file URL.")
    public ResponseEntity<ByteArrayResource> getDoc(
            @Parameter(
                    name = "url",
                    description = "URL of a SHACL file. Format will be auto-detected based on the file extension (ex: .ttl, .rdf, .jsonld...).",
                    required = true,
                    examples = {
                        @ExampleObject(
                                name = "European Parliament ELI-EP Application Profile",
                                value = "https://europarl.github.io/eli-ep/3.0.0/eli-ep.shacl.ttl"
                        ),
                        @ExampleObject(
                                name = "SAPA Collections",
                                value = "https://shapes.performing-arts.ch/collections/sapa-collections.ttl"
                        ),
                        @ExampleObject(
                                name = "SemPER Ric-O Application Profile",
                                value = "https://sparna-git.github.io/semper/configs/ad31-shacl.ttl"
                        )
                    }
            )
            @RequestParam(value="url", required=true) String shapesUrl,
            // includeDiagram option
            @RequestParam(
                value="includeDiagram",
                required=false,
                defaultValue = "false"
            ) boolean includeDiagram,
            // includeDiagram option
            @RequestParam(value="sectionDiagram", required=false, defaultValue = "true") boolean sectionDiagram,
            // hide Properties
            @RequestParam(value="hideProperties", required=false, defaultValue = "false") boolean hideProperties,
            // List Option
            @RequestParam(value="format", required=false, defaultValue = "HTML_RESPEC") String clientFormat,
            // Logo Option
            @RequestParam(value="inputLogo", required=false) String urlLogo,
            // Language Option
            @RequestParam(value="language", required=false, defaultValue = "en") String language,
            // Filter Unused NodeShape
            @RequestParam(value="filterUnusedNodeShapes", required=false, defaultValue="true") boolean filterUnusedNodeShapes
    ){
        try {

            LOGGER.debug("docUrl(shapesUrl='{}')", shapesUrl);

            ShapesDocumentationWriterIfc.MODE format = ShapesDocumentationWriterIfc.MODE.valueOf(clientFormat.toUpperCase());
            Model model = ModelFactory.createDefaultModel();
            ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
            modelPopulator.populateModelFromUrl(model, shapesUrl);

            LOGGER.debug("Done Loading Shapes. Model contains {} triples.", model.size());

            return this.docService.doOutputDoc(
                        model,
                        // true to read diagram
                        includeDiagram,
                        hideProperties,
                    format,
                        urlLogo,
                        modelPopulator.getSourceName(),
                        language,
                        sectionDiagram,
                        filterUnusedNodeShapes
            );
        } catch (Exception e) {
            ExceptionManager.throwException(DocException.class, e.getMessage());
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     *  POST REST END POINT
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(
            value = "/doc",
            consumes = {"multipart/form-data"},
            produces = {"text/html", "text/xml", "application/pdf"}
    )
    @SwaggerDocInfo
    @Operation(summary = "Generates a human-readable documentation from a SHACL file uploaded as a multipart form data.")
    public ResponseEntity<ByteArrayResource> postDoc(
            @Parameter(
                    name = "inputShapeFile",
                    description = "The SHACL file to be read for generating the documentation. RDF format will be auto-detected based on the file extension (ex: .ttl, .rdf, .jsonld...).",
                    required = true
            )
            @RequestParam(value="inputShapeFile", required=true) List<MultipartFile> shapesFiles,
            // includeDiagram option
            @RequestParam(value="includeDiagram", required=false, defaultValue = "false") boolean includeDiagram,
            // includeDiagram option
            @RequestParam(value="sectionDiagram", required=false, defaultValue = "true") boolean sectionDiagram,
            // hide Properties
            @RequestParam(value="hideProperties", required=false, defaultValue = "false") boolean hideProperties,
            // List Option
            @RequestParam(value="format", required=false, defaultValue = "HTML_RESPEC") String clientFormat,
            // Logo Option
            @RequestParam(value="inputLogo", required=false) String urlLogo,
            // Language Option
            @RequestParam(value="language", required=false, defaultValue = "en") String language,
            // Filter Unused NodeShape
            @RequestParam(value="filterUnusedNodeShapes", required=false, defaultValue="true") boolean filterUnusedNodeShapes
    ){
        try {

            if (shapesFiles == null) LOGGER.debug("shapesFiles=null");
            else LOGGER.debug("shapesFiles.length={}", shapesFiles.size());

            Model shapesModel = ModelFactory.createDefaultModel();
            ShapesDocumentationWriterIfc.MODE format = ShapesDocumentationWriterIfc.MODE.valueOf(clientFormat.toUpperCase());
            ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());

            modelPopulator.populateModel(
                    shapesModel,
                    //ENUM DE ControllerModelFactory.SOURCE_TYPE.FILE
                    FILE,
                    null,
                    null,
                    shapesFiles,
                    null
            );

            LOGGER.debug("Done Loading Shapes. Model contains {} triples.", shapesModel.size());

            return this.docService.doOutputDoc(
                    shapesModel,
                    // true to read diagram
                    includeDiagram,
                    hideProperties,
                    format,
                    urlLogo,
                    modelPopulator.getSourceName(),
                    language,
                    sectionDiagram,
                    filterUnusedNodeShapes);
        } catch (Exception e) {
            ExceptionManager.throwException(DocException.class, e.getMessage());
        }
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(value = DocException.class, produces = "application/json")
    public ResponseEntity<RestExceptionRenderer> handleExceptionForRestDocController(DocException ex){
        return ExceptionManager.prepareRestExceptionRenderer(ex, HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }


}
