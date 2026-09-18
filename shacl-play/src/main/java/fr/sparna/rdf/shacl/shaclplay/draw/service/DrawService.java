package fr.sparna.rdf.shacl.shaclplay.draw.service;

import fr.sparna.rdf.shacl.diagram.DrawFormat;
import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramGenerator;
import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramOutput;
import fr.sparna.rdf.shacl.diagram.serialize.DiagramSerializer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class DrawService {

    public ResponseEntity<ByteArrayResource> doOutputDiagram(
            Model shapesModel,
            String filename,
            DrawFormat drawFormat,
            boolean hideProperties) throws IOException {

        PlantUmlDiagramGenerator writer = new PlantUmlDiagramGenerator(
                // includes the subClassOf links in the generated diagram
                true,
                // don't generate hyperlinks
                false,
                // avoid arrows to empty boxes
                true,
                // hide Properties
                hideProperties,
                // a language for label and description reading
                "en"
        );

        List<PlantUmlDiagramOutput> diagrams = writer.generateDiagrams(
                shapesModel,
                // OWL Model
                ModelFactory.createDefaultModel()
        );

        byte[] output = new DiagramSerializer().doOutputDiagram(diagrams, filename, drawFormat);
        String extension = ".".concat(drawFormat.getExtension());
        MediaType mediaType = MediaType.parseMediaType(drawFormat.getMimeType());

        
        if(diagrams.size() == 1) {
            // nothing special
        } else {
            switch(drawFormat) {
                case PNG, SVG, TXT -> {
                    // create a zip
                    mediaType = MediaType.parseMediaType("application/zip");
                    extension = ".zip";
                }
            }
        }

        return transformToResponseEntity(filename, mediaType, extension, output, ContentDisposition.inline());
    }

    private ResponseEntity<ByteArrayResource> transformToResponseEntity(String filename, MediaType mediaType, String extension, byte[] file, ContentDisposition.Builder builder) {
        HttpHeaders header = new HttpHeaders();
        header.setContentDisposition(builder.filename(filename.concat(extension) , StandardCharsets.UTF_8).build());
        header.setContentType(mediaType);
        return new ResponseEntity<>(new ByteArrayResource(file), header, HttpStatus.CREATED);
    }
}
