package fr.sparna.rdf.shacl.doc;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;

import fr.sparna.rdf.jena.ModelReadingUtils;
import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.jena.shacl.PropertyShape;

public class PropertyShapeDoc extends PropertyShape {

	private Resource resource;

	
	public PropertyShapeDoc(Resource resource) {
		super(resource);
		this.resource = resource;
	}
	
	public String getURIOrId() {
		if(this.resource.isURIResource()) {
			return this.resource.getURI();
		} else {
			// returns the blank node ID in that case
			return this.resource.getId().toString();
		}
	}
	
	/**
	 * Returns the display string of the path (using prefixes)
	 * @return
	 */
	public String getShPathAsString() {
		// always ask for the rendering of the path using prefixes
		return ModelRenderingUtils.renderSparqlPropertyPath(this.getShPath(), true);
	}
	
	public String getShNameAsString(String lang) {
		return ModelRenderingUtils.render(super.getShName(lang), true);
	}

	/**
	 * Returns the key to be used for sorting in the properties table
	 * 
	 * @param owlModel
	 * @param lang
	 * @return
	 */
	public String getSortOrderKey(Model owlModel, String lang) {
		if(this.getShOrderAsLiteral().isPresent()) {
			return this.getShOrderAsLiteral().map( o -> o.getDouble()).toString();
		} else if(!this.getDisplayLabel(owlModel, lang).equals("")) {
			return this.getDisplayLabel(owlModel, lang);
		} else if(this.getShPath().isURIResource()) {
			// otherwise use the property URI for sorting
			return ModelRenderingUtils.render(this.getShPath(), true);
		} else {
			// otherwise use the path
			return this.getShPath().toString();
		}
	}
	
}
