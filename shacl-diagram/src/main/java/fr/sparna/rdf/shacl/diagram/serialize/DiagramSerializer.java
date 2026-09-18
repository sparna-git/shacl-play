package fr.sparna.rdf.shacl.diagram.serialize;

import fr.sparna.rdf.shacl.diagram.DrawFormat;
import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramOutput;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class DiagramSerializer {

    public byte[] doOutputDiagram(
            List<PlantUmlDiagramOutput> diagrams,
            String filename,
            DrawFormat drawFormat
    ) throws IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

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
                case PNG, SVG, TXT -> {
                    // create a zip

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
        return buffer.toByteArray();
    }

}
