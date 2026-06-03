package fr.sparna.rdf.shacl.shaclplay.doc.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationModelReader;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationReaderIfc;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationXmlWriter;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationXsltRespecWriter;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationXsltShaclPlayWriter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class DocService {

    //public void doOutputDoc(
    //        Model shapesModel,
    //        boolean includeDiagram,
    //        boolean hideProperties,
    //        ShapesDocumentationWriterIfc.MODE mode,
    //        String urlLogo,
    //        String filename,
    //        String languageInput,
    //        boolean includeSectionDiagram,
    //        boolean filterUnusedNodeShapes,
    //        HttpServletResponse response
    //) throws IOException {
    //    response.setHeader("Content-Disposition", "inline; filename=\""+filename+".html\"");
//
    //    ShapesDocumentationReaderIfc reader = ShapesDocumentationModelReader.buildShapesDocumentationModelReader(
    //            includeDiagram,
    //            urlLogo,
    //            hideProperties,
    //            includeSectionDiagram,
    //            filterUnusedNodeShapes
    //    );
    //    ShapesDocumentation doc = reader.readShapesDocumentation(
    //            shapesModel,
    //            // OWL graph : empty
    //            ModelFactory.createDefaultModel(),
    //            languageInput
    //    );
//
    //    switch(mode) {
    //        case HTML : {
    //            response.setHeader("Content-Disposition", "inline; filename=\""+filename+".html\"");
    //            ShapesDocumentationWriterIfc writer = new ShapesDocumentationXsltShaclPlayWriter(ShapesDocumentationWriterIfc.MODE.HTML);
    //            response.setContentType("text/html");
//
    //            // response.setContentType("application/xhtml+xml");
    //            writer.writeDoc(doc, languageInput, response.getOutputStream());
    //            break;
    //        }
    //        case PDF : {
    //            response.setHeader("Content-Disposition", "inline; filename=\""+filename+".pdf\"");
    //            // 1. write Documentation structure to XML
    //            ShapesDocumentationWriterIfc writerHTML = new ShapesDocumentationXsltShaclPlayWriter(ShapesDocumentationWriterIfc.MODE.PDF);
    //            ByteArrayOutputStream htmlBytes = new ByteArrayOutputStream();
    //            writerHTML.writeDoc(doc,languageInput, htmlBytes);
//
    //            //read file html
    //            String htmlCode = new String(htmlBytes.toByteArray(),"UTF-8");
//
    //            // Convert
    //            response.setContentType("application/pdf");
    //            PdfRendererBuilder _builder = new PdfRendererBuilder();
    //            _builder.useFastMode();
    //            _builder.withHtmlContent(htmlCode,"https://shacl-play.sparna.fr/play");
    //            _builder.toStream(response.getOutputStream());
    //            _builder.testMode(false);
    //            _builder.run();
//
    //            break;
    //        }
    //        case XML : {
    //            response.setHeader("Content-Disposition", "inline; filename=\""+filename+".xml\"");
    //            ShapesDocumentationXmlWriter writeXML = new ShapesDocumentationXmlWriter();
    //            response.setContentType("application/xml");
    //            writeXML.write(doc, languageInput, response.getOutputStream());
//
    //            break;
    //        }
    //        case HTML_RESPEC : {
    //            response.setHeader("Content-Disposition", "inline; filename=\""+filename+".html\"");
    //            ShapesDocumentationWriterIfc writer = new ShapesDocumentationXsltRespecWriter(ShapesDocumentationWriterIfc.MODE.HTML);
    //            response.setContentType("text/html");
//
    //            // response.setContentType("application/xhtml+xml");
    //            writer.writeDoc(doc, languageInput, response.getOutputStream());
    //            break;
    //        }
    //    }
//
    //}

    public ResponseEntity<ByteArrayResource> doOutputDoc(
            Model shapesModel,
            boolean includeDiagram,
            boolean hideProperties,
            ShapesDocumentationWriterIfc.MODE format,
            String urlLogo,
            String fileName,
            String languageInput,
            boolean includeSectionDiagram,
            boolean filterUnusedNodeShapes
    ) throws IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        String extension = "";
        MediaType mediaType = null;
        ShapesDocumentationWriterIfc writer;

        ShapesDocumentationReaderIfc reader = ShapesDocumentationModelReader.buildShapesDocumentationModelReader(
                includeDiagram,
                urlLogo,
                hideProperties,
                includeSectionDiagram,
                filterUnusedNodeShapes
        );
        ShapesDocumentation doc = reader.readShapesDocumentation(
                shapesModel,
                // OWL graph : empty
                ModelFactory.createDefaultModel(),
                languageInput
        );

        switch (format){
            case HTML -> {
                mediaType = MediaType.TEXT_HTML;
                extension = ".html";
                writer = new ShapesDocumentationXsltShaclPlayWriter(ShapesDocumentationWriterIfc.MODE.PDF);
                // response.setContentType("application/xhtml+xml");
                writer.writeDoc(doc, languageInput, buffer);
            }

            case PDF -> {
                mediaType = MediaType.APPLICATION_PDF;
                extension = ".pdf";
                writer = new ShapesDocumentationXsltShaclPlayWriter(ShapesDocumentationWriterIfc.MODE.PDF);
                writer.writeDoc(doc,languageInput, buffer);

                //read file html
                String htmlCode = new String(buffer.toByteArray(),"UTF-8");

                // Convert
                PdfRendererBuilder _builder = new PdfRendererBuilder();
                _builder.useFastMode();
                _builder.withHtmlContent(htmlCode,"https://shacl-play.sparna.fr/play");
                buffer = new ByteArrayOutputStream();
                _builder.toStream(buffer);
                _builder.testMode(false);
                _builder.run();
            }

            case XML -> {
                mediaType = MediaType.TEXT_XML;
                extension = ".xml";
                writer = new ShapesDocumentationXmlWriter();
                writer.writeDoc(doc, languageInput, buffer);
            }

            case HTML_RESPEC -> {
                mediaType = MediaType.TEXT_HTML;
                extension = ".html";
                writer = new ShapesDocumentationXsltRespecWriter(ShapesDocumentationWriterIfc.MODE.HTML);
                // response.setContentType("application/xhtml+xml");
                writer.writeDoc(doc, languageInput, buffer);
            }
        }

        return transformToResponseEntity(fileName, mediaType, extension, buffer.toByteArray(), ContentDisposition.inline());
    }

    private ResponseEntity<ByteArrayResource> transformToResponseEntity(String filename, MediaType mediaType, String extension, byte[] file, ContentDisposition.Builder builder) {
        HttpHeaders header = new HttpHeaders();
        header.setContentDisposition(builder.filename(filename.concat(extension) , StandardCharsets.UTF_8).build());
        header.setContentType(mediaType);
        return new ResponseEntity<>(new ByteArrayResource(file), header, HttpStatus.CREATED);
    }
}
