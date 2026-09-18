package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.FileFormat;

public enum DrawFormat {

    SVG("image/svg+xml", FileFormat.SVG, "svg", Collections.emptyList()),
    PNG("image/png", FileFormat.PNG, "png", Collections.emptyList()),
    // html page that will contain the SVG diagrams
    HTML("text/html", null, "html", Collections.emptyList()),
    TXT("text/plain", null, "txt", new ArrayList<>(List.of("iuml", "puml", "plantuml")));

    private final String mimeType;
    private final FileFormat plantUmlFileFormat;
    private final String extension;
    private final List<String> otherExtensions;

    private DrawFormat(String mimeType, FileFormat plantUmlFileFormat, String extension, List<String> otherExtensions) {
        this.mimeType = mimeType;
        this.plantUmlFileFormat = plantUmlFileFormat;
        this.extension = extension;
        this.otherExtensions = otherExtensions;
    }

    public static DrawFormat fromFileName(String filename) {
        for(DrawFormat format : DrawFormat.values()) {
            for(String ext : format.getAllExtensions()) {
                if(filename.endsWith(ext)) {
                    return format;
                }
            }
        }
        return null;
    }

    public String getMimeType(){
        return this.mimeType;
    }

    public FileFormat getPlantUmlFileFormat(){
        return this.plantUmlFileFormat;
    }

    public String getExtension(){
        return this.extension;
    }

    public List<String> getOtherExtensions() {
        return this.otherExtensions;
    }

    public List<String> getAllExtensions() {
        List<String> allExtensions = new ArrayList<>();
        allExtensions.add(this.extension);
        allExtensions.addAll(this.otherExtensions);
        return allExtensions;
    }
}
