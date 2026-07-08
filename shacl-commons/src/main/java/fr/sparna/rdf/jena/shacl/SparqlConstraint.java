package fr.sparna.rdf.jena.shacl;

import java.util.List;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;

import fr.sparna.rdf.jena.ModelReadingUtils;
import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.vocabularies.DCT;
import fr.sparna.rdf.vocabularies.SH;

public class SparqlConstraint {
   
    protected Resource resource;
 
    public SparqlConstraint(Resource resource) {  
	    this.resource = resource;		
	}

	/**
	 * @return the underlying Resource
	 */
	public Resource getResource() {
		return resource;
	}

    public String getDescription(String lang) {
        return ModelRenderingUtils.render(this.getDctDescription(lang), true);
    }

	/**
	 * @return The rdfs:label list in the provided language, or an empty list if none is present
	 */
	public List<Literal> getDctDescription(String lang) {
		return ModelReadingUtils.readLiteralInLang(resource, DCT.Description, lang);
	}

    public String getSelect() {
        return ModelRenderingUtils.render(ModelReadingUtils.readLiteral(resource, SH.select));
    }

}
