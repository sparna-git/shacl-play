package fr.sparna.rdf.shacl.shaclplay.draw;

import net.sourceforge.plantuml.FileFormat;

public enum DrawFormat {

    SVG("image/svg+xml", FileFormat.SVG, "svg"),
    PNG("image/png", FileFormat.PNG, "png"),
    // html page that will contain the SVG diagrams
    HTML("text/html", null, "html"),
    TXT("text/plain", null, "txt");

    private final String mimeType;
    private final FileFormat plantUmlFileFormat;
    private final String extension;

    private DrawFormat(String mimeType, FileFormat plantUmlFileFormat, String extension) {
        this.mimeType = mimeType;
        this.plantUmlFileFormat = plantUmlFileFormat;
        this.extension = extension;
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
}
