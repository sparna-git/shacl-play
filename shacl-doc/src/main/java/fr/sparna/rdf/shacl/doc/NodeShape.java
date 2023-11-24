package fr.sparna.rdf.shacl.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.topbraid.shacl.vocabulary.SH;

public class NodeShape {

	private Resource nodeShape;
	
	
	protected List<PropertyShape> properties = new ArrayList<>();
	
	public NodeShape(Resource nodeShape) {
		this.nodeShape = nodeShape;
	}
	
	
	
	public String getRdfsLabelAsString(String lang) {
		return ModelRenderingUtils.render(this.getRdfsLabel(lang), true);
	}	
	
	public String getShortFormOrId() {
		if(this.nodeShape.isURIResource()) {
			return this.getNodeShape().getModel().shortForm(this.getNodeShape().getURI());
		} else {
			// return the blank node ID in that case
			return this.nodeShape.asResource().getId().getLabelString();
		}
	}
	
	public String getDisplayLabel(Model owlModel, String lang) {
		String result = ModelRenderingUtils.render(this.getSkosPrefLabel(lang), true);
		
		if(result == null) {
			result = ModelRenderingUtils.render(this.getRdfsLabel(lang), true);
		}				
		
		if(result == null && this.getShTargetClass() != null) {
			// otherwise if we have skos:prefLabel on the class, take it
			result = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(owlModel.getResource(this.getShTargetClass().getURI()), SKOS.prefLabel, lang), true);
		}
		
		if(result == null && this.getShTargetClass() != null) {
			// otherwise if we have rdfs:label on the class, take it
			result = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(owlModel.getResource(this.getShTargetClass().getURI()), RDFS.label, lang), true);
		}
		
		if(result == null) {
			result = this.getShortFormOrId();
		}
		
		return result;
	}
	
	public String getDisplayDescription(Model owlModel, String lang) {
		String result = ModelRenderingUtils.render(this.getSkosDefinition(lang), true);
		
		if(result == null) {
			result = ModelRenderingUtils.render(this.getRdfsComment(lang), true);
		}
		
		if(result == null && this.getShTargetClass() != null) {
			// otherwise if we have skos:definition on the class, take it
			result = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(owlModel.getResource(this.getShTargetClass().getURI()), SKOS.definition, lang), true);
		}
		
		if(result == null && this.getShTargetClass() != null) {
			// otherwise if we have rdfs:label on the class, take it
			result = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(owlModel.getResource(this.getShTargetClass().getURI()), RDFS.comment, lang), true);
		}
		
		return result;
	}
	
	
	
	public List<Literal> getRdfsComment(String lang) {
		return ModelReadingUtils.readLiteralInLang(nodeShape, RDFS.comment, lang);
	}
	
	public List<Literal> getRdfsLabel(String lang) {
		return ModelReadingUtils.readLiteralInLang(nodeShape, RDFS.label, lang);
	}
	
	public List<Literal> getSkosPrefLabel(String lang) {
		return ModelReadingUtils.readLiteralInLang(nodeShape, SKOS.prefLabel, lang);
	}
	
	public List<Literal> getSkosDefinition(String lang) {
		return ModelReadingUtils.readLiteralInLang(nodeShape, SKOS.definition, lang);
	}
	
	

	public Resource getNodeShape() {
		return nodeShape;
	}

	public List<PropertyShape> getProperties() {
		return properties;
	}

	public void setProperties(List<PropertyShape> properties) {
		this.properties = properties;
	}

	public Literal getShTargetShSelect() {
		return Optional.ofNullable(nodeShape.getPropertyResourceValue(SH.target)).map(
				r -> Optional.ofNullable(r.getProperty(SH.select)).map(l -> l.getLiteral()).orElse(null)
		).orElse(null);
	}

	public boolean isAClass() {
		return nodeShape.hasProperty(RDF.type, RDFS.Class);
	}

	public RDFNode getSkosExample() {
		return Optional.ofNullable(nodeShape.getProperty(SKOS.example)).map(s -> s.getObject()).orElse(null);
	}
	
	public Resource getShNodeKind() {	
		return Optional.ofNullable(nodeShape.getProperty(SH.nodeKind)).map(s -> s.getResource()).orElse(null);
	}

	public Boolean getShClosed() {
		return Optional.ofNullable(nodeShape.getProperty(SH.closed)).map(s -> Boolean.valueOf(s.getBoolean())).orElse(null);
	}

	public Double getShOrder() {
		return Optional.ofNullable(nodeShape.getProperty(SH.order)).map(s -> s.getDouble()).orElse(null);
	}

	public Literal getShPattern() {
		return Optional.ofNullable(nodeShape.getProperty(SH.pattern)).map(s -> s.getLiteral()).orElse(null);
	}

	public Resource getShTargetClass() {
		return Optional.ofNullable(nodeShape.getProperty(SH.targetClass)).map(s -> s.getResource()).orElse(null);
	}
	
	public List<Resource> getRdfsSubClassOf() {
		return nodeShape.listProperties(RDFS.subClassOf).toList().stream()
				.map(s -> s.getResource())
				.filter(r -> { return r.isURIResource() && !r.getURI().equals(OWL.Thing.getURI()); })
				.collect(Collectors.toList());
	}
	
	
}