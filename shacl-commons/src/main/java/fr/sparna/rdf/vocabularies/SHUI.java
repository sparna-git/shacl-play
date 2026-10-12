package fr.sparna.rdf.vocabularies;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;

public class SHUI {

    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static final Model M_MODEL = ModelFactory.createDefaultModel();

	public static final String NS = "http://www.w3.org/ns/shacl-ui#";

    public static final Resource BlankNodeViewer = M_MODEL.createResource(NS + "BlankNodeViewer");

    public static final Resource DetailsViewer = M_MODEL.createResource(NS + "DetailsViewer");

    public static final Resource HyperlinkViewer = M_MODEL.createResource(NS + "HyperlinkViewer");

    public static final Resource IRIViewer = M_MODEL.createResource(NS + "IRIViewer");

    public static final Resource LabelViewer = M_MODEL.createResource(NS + "LabelViewer");

    public static final Resource LiteralViewer = M_MODEL.createResource(NS + "LiteralViewer");
	
    public static final Property propertyRole = M_MODEL.createProperty( NS + "propertyRole" ); // <-- Ici c'est un prédicate propertyRole

    public static final Resource PropertyRole = M_MODEL.createResource(NS +  "PropertyRole"); // <-- Ici c'est une class de type PropertyRole

    public static final Property viewer = M_MODEL.createProperty( NS + "viewer" );

    public static final Resource LabelRole = M_MODEL.createResource( NS + "LabelRole" );
	
    public static final Resource IDRole = M_MODEL.createResource( NS + "IDRole" );

    public static final Resource DescriptionRole = M_MODEL.createResource( NS + "DescriptionRole" );

    public static final Resource IconRole = M_MODEL.createResource( NS + "IconRole" );

    public static final Resource DepictionRole = M_MODEL.createResource( NS + "DepictionRole" );

    public static final Resource KeyInfoRole = M_MODEL.createResource( NS + "KeyInfoRole" );
    
    static { 
        M_MODEL.add(propertyRole, OWL.equivalentProperty, DASH.propertyRole);
        M_MODEL.add(LabelRole, OWL.sameAs, DASH.LabelRole);
        M_MODEL.add(IDRole, OWL.sameAs, DASH.IDRole);
        M_MODEL.add(DescriptionRole, OWL.sameAs, DASH.DescriptionRole);
        M_MODEL.add(IconRole, OWL.sameAs, DASH.IconRole);
        M_MODEL.add(DepictionRole, OWL.sameAs, DASH.DepictionRole);
        M_MODEL.add(KeyInfoRole, OWL.sameAs, DASH.KeyInfoRole);
    }

    public static final Resource getSameDashRole(Resource shuiRole) {
    	return M_MODEL.listStatements(shuiRole, OWL.sameAs, (Resource)null).toList().stream().map(s -> s.getObject().asResource()).findFirst().orElse(null);
    }
	
}
