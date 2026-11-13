package fr.sparna.rdf.vocabularies;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

public class PRISME {

    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static final Model M_MODEL = ModelFactory.createDefaultModel();

    public static final String NS = "https://prisme.sparna.fr/";

    public static final Resource OrderingKeyRole = M_MODEL.createResource(NS + "OrderingKeyRole");

    public static final Resource LabelDescriptionViewer = M_MODEL.createResource(NS + "LabelDescriptionViewer");

    static{
        M_MODEL.add(OrderingKeyRole, RDF.type, SHUI.PropertyRole);
    }

}
