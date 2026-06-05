package fr.sparna.rdf.shacl.shaclplay.draw.service;

import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramGenerator;
import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramOutput;
import fr.sparna.rdf.shacl.diagram.plantuml.PlantUmlHtmlSerializer;
import fr.sparna.rdf.shacl.diagram.plantuml.PlantUmlPngSerializer;
import fr.sparna.rdf.shacl.diagram.plantuml.PlantUmlSvgSerializer;
import fr.sparna.rdf.shacl.shaclplay.draw.DrawFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class DrawService {

    public ResponseEntity<ByteArrayResource> doOutputDiagram(
            Model shapesModel,
            String filename,
            DrawFormat drawFormat,
            boolean hideProperties) throws IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        String extension = ".".concat(drawFormat.getExtension());
        MediaType mediaType = MediaType.parseMediaType(drawFormat.getMimeType());

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

        if(diagrams.size() == 1) {
            String plantUmlString = diagrams.get(0).getPlantUmlString();
            // always set appropriate content type
            switch(drawFormat) {
                case PNG -> {
                    // display a png file, generate from PlantUml
                    PlantUmlPngSerializer pngSerializer = new PlantUmlPngSerializer();
                    pngSerializer.serialize(plantUmlString, buffer);
                }
                case SVG -> {
                    PlantUmlSvgSerializer svgSerializer = new PlantUmlSvgSerializer();
                    svgSerializer.serializeInSVG(plantUmlString, buffer);
                }
                case TXT -> {
                    buffer.write(plantUmlString.getBytes(StandardCharsets.UTF_8));
                }
                case HTML -> {
                    PlantUmlHtmlSerializer htmlSerializer = new PlantUmlHtmlSerializer();
                    htmlSerializer.serialize(diagrams, buffer);
                }
            }
        } else {
            switch(drawFormat) {
                case PNG -> {}
                case SVG -> {}
                case TXT -> {
                    // create a zip
                    mediaType = MediaType.parseMediaType("application/zip");
                    extension = ".zip";

                    ZipOutputStream zos = new ZipOutputStream(buffer, StandardCharsets.UTF_8);
                    zos.setLevel(9);

                    for (PlantUmlDiagramOutput oneDiagram : diagrams) {
                        String uri = oneDiagram.getDiagramUri();
                        String localPart;
                        if(uri.indexOf('#') > -1) {
                            localPart = uri.substring(uri.lastIndexOf('#')+1);
                        } else {
                            localPart = uri.substring(uri.lastIndexOf('/')+1);
                        }

                        if(drawFormat == DrawFormat.TXT) {
                            String entryName = URLEncoder.encode(localPart, StandardCharsets.UTF_8) + ".txt";
                            zos.putNextEntry(new ZipEntry(entryName));
                            zos.write(oneDiagram.getPlantUmlString().getBytes(StandardCharsets.UTF_8));
                            zos.closeEntry();
                        } else {
                            String entryName = URLEncoder.encode(localPart, StandardCharsets.UTF_8) + "." + drawFormat.getExtension();
                            zos.putNextEntry(new ZipEntry(entryName));
                            SourceStringReader reader = new SourceStringReader(oneDiagram.getPlantUmlString());
                            // either SVG or PNG, cannot be HTML or TXT
                            reader.generateImage(zos, new FileFormatOption(drawFormat.getPlantUmlFileFormat()));
                            zos.closeEntry();
                        }

                    }
                    zos.flush();
                    zos.close();
                }
                case HTML -> {
                    PlantUmlHtmlSerializer htmlSerializer = new PlantUmlHtmlSerializer();
                    htmlSerializer.serialize(diagrams, buffer);
                }
            }
        }
        return transformToResponseEntity(filename, mediaType, extension, buffer.toByteArray(), ContentDisposition.inline());
    }

    private ResponseEntity<ByteArrayResource> transformToResponseEntity(String filename, MediaType mediaType, String extension, byte[] file, ContentDisposition.Builder builder) {
        HttpHeaders header = new HttpHeaders();
        header.setContentDisposition(builder.filename(filename.concat(extension) , StandardCharsets.UTF_8).build());
        header.setContentType(mediaType);
        return new ResponseEntity<>(new ByteArrayResource(file), header, HttpStatus.CREATED);
    }
}
