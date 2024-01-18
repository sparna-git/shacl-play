package fr.sparna.rdf.shacl.doc;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.jena.ModelReadingUtils;
import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.jena.shacl.ShOrReadingUtils;

public class PropertyShape {

	private Resource resource;

	
	public PropertyShape(Resource resource) {
		super();
		this.resource = resource;
	}
	
	public Resource getResource() {
		return resource;
	}
	
	public String getURIOrId() {
		if(this.resource.isURIResource()) {
			return this.resource.getURI();
		} else {
			// returns the blank node ID in that case
			return this.resource.asResource().getId().getLabelString();
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
		return ModelRenderingUtils.render(this.getShName(lang), true);
	}
	
	public String getDisplayLabel(Model owlModel, String lang) {
		String result = ModelRenderingUtils.render(this.getShName(lang), true);
		
		if(result == null && this.getShPath().isURIResource() && owlModel != null) {
			// otherwise if we have skos:prefLabel on the property, take it
			result = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(owlModel.getResource(this.getShPath().getURI()), SKOS.prefLabel, lang), true);
		}
		
		if(result == null && this.getShPath().isURIResource() && owlModel != null) {
			// otherwise if we have rdfs:label on the property, take it
			result = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(owlModel.getResource(this.getShPath().getURI()), RDFS.label, lang), true);
		}
		
		// otherwise return empty string, never null (for sorting)
		if(result == null) {
			result = "";
		}
		
		return result;
	}
	
	public String getDisplayDescription(Model owlModel, String lang) {
		String result = ModelRenderingUtils.render(this.getShDescription(lang), true);
		
		if(result == null && this.getShPath().isURIResource()) {
			// otherwise if we have skos:definition on the property, take it
			result = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(owlModel.getResource(this.getShPath().getURI()), SKOS.definition, lang), true);
		}
		
		if(result == null && this.getShPath().isURIResource()) {
			// otherwise if we have rdfs:comment on the property, take it
			result = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(owlModel.getResource(this.getShPath().getURI()), RDFS.comment, lang), true);
		}
		
		return result;
	}

	public List<String> getShOr() {
		if (this.resource.hasProperty(SH.or)) {
			
			List<Resource> values = ShOrReadingUtils.readShClassAndShNodeAndShDatatypeAndShNodeKindInShOr(this.resource.getProperty(SH.or).getList());
			if(values.size() > 0) {
				return values.stream().map(r -> { return (r.isURIResource())?r.getModel().shortForm(r.getURI()):r.toString();}).collect(Collectors.toList());
			}			
		}
		
		return null;
	}
	
	public RDFNode getShHasValue() {
		return Optional.ofNullable(this.resource.getProperty(SH.hasValue)).map(s -> s.getObject()).orElse(null);
	}

	public Double getShOrder() {
		return Optional.ofNullable(this.resource.getProperty(SH.order)).map(s -> s.getDouble()).orElse(null);
	}

	public List<RDFNode> getShIn() {
		if (this.resource.hasProperty(SH.in)) {
			Resource list = this.resource.getProperty(SH.in).getList().asResource();
			return list.as(RDFList.class).asJavaList();
		} else {
			return null;
		}
	}

	public List<Literal> getShName(String lang) {
		return ModelReadingUtils.readLiteralInLang(this.resource, SH.name, lang);
	}

	public List<Literal> getShDescription(String lang) {
		return ModelReadingUtils.readLiteralInLang(this.resource, SH.description, lang);
	}

	public Resource getShPath() {
		return Optional.ofNullable(this.resource.getProperty(SH.path)).map(s -> s.getResource()).orElse(null);
	}

	public Resource getShDatatype() {
		return Optional.ofNullable(this.resource.getProperty(SH.datatype)).map(s -> s.getResource()).orElse(null);
	}

	public Resource getShNodeKind() {		
		return Optional.ofNullable(this.resource.getProperty(SH.nodeKind)).map(s -> s.getResource()).orElse(null);
	}
	
	public Integer getShMinCount() {
		return Optional.ofNullable(this.resource.getProperty(SH.minCount)).map(s -> Integer.parseInt(s.getString())).orElse(null);
	}
	
	public Integer getShMaxCount() {
		return Optional.ofNullable(this.resource.getProperty(SH.maxCount)).map(s -> Integer.parseInt(s.getString())).orElse(null);
	}

	public Literal getShPattern() {
		return Optional.ofNullable(this.resource.getProperty(SH.pattern)).map(s -> s.getLiteral()).orElse(null);
	}

	// TODO : devrait retourner un ShaclBox
	public Resource getShNode() {
		return Optional.ofNullable(this.resource.getProperty(SH.node)).map(s -> s.getResource()).orElse(null);
	}

	public Resource getShClass() {
		return Optional.ofNullable(this.resource.getProperty(SH.class_)).map(s -> s.getResource()).orElse(null);
	}
	
}
