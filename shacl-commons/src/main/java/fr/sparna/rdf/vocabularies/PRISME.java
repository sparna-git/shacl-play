package fr.sparna.rdf.vocabularies;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

public class PRISME {

    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static final Model M_MODEL = ModelFactory.createDefaultModel();

    public static final String NS = "https://prisme.sparna.fr/shuix#";

    public static final Resource OrderingKeyRole = M_MODEL.createResource(NS + "OrderingKeyRole");

    public static final Resource LabelDescriptionViewer = M_MODEL.createResource(NS + "LabelDescriptionViewer");

    public static final Resource MetadataRole = M_MODEL.createResource(NS + "MetadataRole");

    static{
        M_MODEL.add(OrderingKeyRole, RDF.type, SHUI.PropertyRole);
        M_MODEL.add(MetadataRole, RDF.type, SHUI.PropertyRole);
    }

}
