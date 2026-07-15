package fr.sparna.rdf.jena.shacl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;

import fr.sparna.rdf.jena.ModelReadingUtils;
import fr.sparna.rdf.vocabularies.*;

import org.topbraid.shacl.vocabulary.SH;

public abstract class Shape {
	
	protected Resource resource;

	public Shape(Resource resource) {  
	    this.resource = resource;		
	}

	public abstract String getDisplayLabel(Model owlModel, String lang);
	public abstract String getDisplayDescription(Model owlModel, String lang);
	public abstract String getDisplayColor();
	public abstract String getDisplayBackgroundColor();
	
	/**
	 * @return the underlying Resource
	 */
	public Resource getResource() {
		return resource;
	}

	public String getShortFormOrId() {
		if(this.resource != null && this.resource.isURIResource()) {
			return this.getResource().getModel().shortForm(this.getResource().getURI());
		} else {
			// returns the blank node ID in that case
			return this.resource.asResource().getId().getLabelString();
		}
	}

	/**
	 * @return the URI of the shape if it is a URI resource, or the blank node ID if it is a blank node
	 */
	public String getURIOrId() {
		if(this.resource.isURIResource()) {
			return this.resource.getURI();
		} else {
			// returns the blank node ID in that case
			return this.resource.getId().toString();
		}
	}

	/**
	 * @return the optional value of shacl-play:color. See also the entity-level getDisplayColor() method, which should be preferred
	 */
	public Optional<Literal> getShaclPlayColor() {
		return ModelReadingUtils.getOptionalLiteral(resource, resource.getModel().createProperty(SHACL_PLAY.COLOR));
	}

	/**
	 * @return the optional value of shacl-play:backgroundcolor. See also the entity-level getDisplayBackgroundColor() method, which should be preferred
	 */
	public Optional<Literal> getShaclPlayBackgroundColor() {
		return ModelReadingUtils.getOptionalLiteral(resource, resource.getModel().createProperty(SHACL_PLAY.BACKGROUNDCOLOR));
	}

	public Optional<Literal> getShaclPlayShortName() {
		return ModelReadingUtils.getOptionalLiteral(resource, resource.getModel().createProperty(SHACL_PLAY.SHORTNAME));
	}

	public Optional<Literal> getShaclPlayMain() {
		return ModelReadingUtils.getOptionalLiteral(resource, resource.getModel().createProperty(SHACL_PLAY.MAIN));
	}

	public RDFList getShOr() {
		if (resource.hasProperty(SH.or)) {
			return resource.getProperty(SH.or).getObject().as(RDFList.class); 
		} else {
			return null;
		}		
	}

	public Optional<Resource> getShGroup() {
		return ModelReadingUtils.getOptionalResource(resource, SH.group);
	}

	public Optional<Resource> getShNode() {
		return ModelReadingUtils.getOptionalResource(resource, SH.node);
	}

	public List<Resource> getShNodeAsList() {
		return ModelReadingUtils.readObjectAsResource(resource, SH.node);
	}

	public Optional<Resource> getShClass() {
		return ModelReadingUtils.getOptionalResource(resource, SH.class_);
	}

	public Optional<Resource> getShDatatype() {
		return ModelReadingUtils.getOptionalResource(this.resource, SH.datatype);
	}
	
	public Optional<Resource> getShNodeKind() {
		return ModelReadingUtils.getOptionalResource(this.resource, SH.nodeKind);
	}
	
	public Optional<Literal> getShPattern() {
		return ModelReadingUtils.getOptionalLiteral(this.resource, SH.pattern);
	}

	public Optional<Float> getShOrder() {
		return ModelReadingUtils.getOptionalLiteral(this.resource, SH.order).map(l -> l.getFloat());
	}

	public Optional<Literal> getShOrderAsLiteral() {
		return ModelReadingUtils.getOptionalLiteral(this.resource, SH.order);
	}

	public Optional<Integer> getShMinLength() {
		return ModelReadingUtils.getOptionalLiteral(this.resource, SH.minLength).map(l -> l.getInt());
	}
	
	public Optional<Integer> getShMaxLength() {
		return ModelReadingUtils.getOptionalLiteral(this.resource, SH.maxLength).map(l -> l.getInt());
	}
	
	public Optional<Literal> getShMinInclusive() {
		return ModelReadingUtils.getOptionalLiteral(this.resource, SH.minInclusive);
	}
	
	public Optional<Literal> getShMaxInclusive() {
		return ModelReadingUtils.getOptionalLiteral(this.resource, SH.maxInclusive);
	}
	
	public Optional<Literal> getShMinExclusive() {
		return ModelReadingUtils.getOptionalLiteral(this.resource, SH.minExclusive);
	}
	
	public Optional<Literal> getShMaxExclusive() {
		return ModelReadingUtils.getOptionalLiteral(this.resource, SH.maxExclusive);
	}

	public Optional<Boolean> getShDeactivated() {
		return ModelReadingUtils.getOptionalLiteral(this.resource,SH.deactivated).map(l -> l.getBoolean());
	}

	public List<Literal> getShLanguageIn() {
		Optional<Resource> list = ModelReadingUtils.getOptionalResource(this.resource, SH.languageIn);
		if(list.isPresent()) {
			List<RDFNode> languages = ModelReadingUtils.asJavaList(list.get());
			return languages.stream().filter(n -> n.isLiteral()).map(n -> n.asLiteral()).collect(Collectors.toList());
		} else {
			return new ArrayList<Literal>();
		}
	}

	public List<RDFNode> getSkosExample() {
		return ModelReadingUtils.readObjectAsResourceOrLiteral(resource, SKOS.example);
	}

	/**
	 * Returns true if the shape could be a literal.
  	 * This is the case if it has a datatype, or if it has a nodeKind of Literal,
  	 * or if it has a languageIn, or if it has minLength, maxLength, minInclusive,
  	 * maxInclusive, minExclusive, or maxExclusive.
	 * @return
	 */
	public boolean couldBeLiteral() {
		  return 
		  resource.hasProperty(SH.datatype)
		  ||
		  ( 
			resource.hasProperty(SH.nodeKind)
			&& 
	  		resource.getProperty(SH.nodeKind).getObject().asResource().equals(SH.Literal) 
		  )
		  ||
		  resource.hasProperty(SH.languageIn)
	  	  ||
		  resource.hasProperty(SH.minLength)
		  ||
		  resource.hasProperty(SH.maxLength)
	  	  ||
		  resource.hasProperty(SH.minInclusive)
		  ||
		  resource.hasProperty(SH.maxInclusive)
	  	  ||
		  resource.hasProperty(SH.minExclusive)
		  ||
		  resource.hasProperty(SH.maxExclusive);
	}

	public boolean hasShOrShClassOrShNode() {
		return 
				(this.getShOrShClass() != null && this.getShOrShClass().size() > 0)
				|| 
				(this.getShOrShNode() != null && this.getShOrShNode().size() > 0)
		;
	}

	public List<Resource> getShOrShDatatype() {
		if (this.resource.hasProperty(SH.or)) {			
			List<Resource> values = ShOrReadingUtils.readShDatatypeInShOr(this.resource.getProperty(SH.or).getList());
			if(values.size() > 0) {
				return values;
			}
		}		
		return null;
	}
	
	public List<Resource> getShOrShNodeKind() {
		if (this.resource.hasProperty(SH.or)) {			
			List<Resource> values = ShOrReadingUtils.readShNodeKindInShOr(this.resource.getProperty(SH.or).getList());
			if(values.size() > 0) {
				return values;
			}
		}		
		return null;
	}
	
	public List<Resource> getShOrShClass() {
		if (this.resource.hasProperty(SH.or)) {			
			List<Resource> values = ShOrReadingUtils.readShClassInShOr(this.resource.getProperty(SH.or).getList());
			if(values.size() > 0) {
				return values;
			}
		}
		
		return null;
	}
	
	public List<Resource> getShOrShNode() {
		if (this.resource.hasProperty(SH.or)) {			
			List<Resource> values = ShOrReadingUtils.readShNodeInShOr(this.resource.getProperty(SH.or).getList());
			if(values.size() > 0) {
				return values;
			}
		}
		
		return null;
	}

	public boolean isDeactivated() {
		return this.getShDeactivated().orElse(false);
	}

	public List<Resource> getPropertyRoles() {
		if (resource.hasProperty(DASH.propertyRole)) {
			List<Statement> propertyRoles = resource.listProperties(DASH.propertyRole).toList();
			return propertyRoles.stream().map(s -> s.getObject().asResource()).collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}
	
	public boolean isLabelRole() {
		return this.getPropertyRoles().stream().anyMatch(r -> r.getURI().equals(DASH.LabelRole.getURI()));
	}

	public List<Resource> getShRule() {
		if (resource.hasProperty(SH.rule)) {
			List<Statement> rules = resource.listProperties(SH.rule).toList();
			return rules.stream().map(s -> s.getObject().asResource()).collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * @return The rdfs:comment list in the provided language, or an empty list if none is present
	 */
	public List<Literal> getRdfsComment(String lang) {
		return ModelReadingUtils.readLiteralInLang(resource, RDFS.comment, lang);
	}
	
	/**
	 * @return The rdfs:label list in the provided language, or an empty list if none is present
	 */
	public List<Literal> getRdfsLabel(String lang) {
		return ModelReadingUtils.readLiteralInLang(resource, RDFS.label, lang);
	}

	/**
	 * @return The rdfs:label list in the provided language, or an empty list if none is present
	 */
	public List<Literal> getSchemaName(String lang) {
		return ModelReadingUtils.readLiteralInLang(resource, SCHEMA.name, lang);
	}

	/**
	 * @return The rdfs:label list in the provided language, or an empty list if none is present
	 */
	public List<Literal> getSchemaDescription(String lang) {
		return ModelReadingUtils.readLiteralInLang(resource, SCHEMA.description, lang);
	}

	/**
	 * @return The skos:prefLabel list in the provided language, or an empty list if none is present
	 */
	public List<Literal> getSkosPrefLabel(String lang) {
		return ModelReadingUtils.readLiteralInLang(resource, SKOS.prefLabel, lang);
	}
	
	/**
	 * @return The skos:definition list in the provided language, or an empty list if none is present
	 */
	public List<Literal> getSkosDefinition(String lang) {
		return ModelReadingUtils.readLiteralInLang(resource, SKOS.definition, lang);
	}	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((resource == null) ? 0 : resource.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Shape other = (Shape) obj;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		return true;
	}

	public static class ShOrderComparator implements Comparator<Resource> {		

		public ShOrderComparator() {
		}

		public static Double getShOrderOf(Resource r) {
			return Optional.ofNullable(r.getProperty(SH.order)).map(s -> s.getDouble()).orElse(null);
		}


		@Override
		public int compare(Resource r1, Resource r2) {
			if (getShOrderOf(r1) != null) {
				if (getShOrderOf(r2) != null) {
					return ((getShOrderOf(r1) - getShOrderOf(r2)) > 0)?1:-1;
				} else {
					return -1;
				}
			} else {
				if (getShOrderOf(r2) != null) {
					return 1;
				} else {
					return 1;
				}
			}
		}
		
	}

	public static class ShapeDisplayLabelComparator implements Comparator<Shape> {

		private Model owlGraph;
		private String lang;		

		public ShapeDisplayLabelComparator(Model owlGraph, String lang) {
			this.owlGraph = owlGraph;
			this.lang = lang;
		}

		@Override
		public int compare(Shape ns1, Shape ns2) {
			if (ns1.getShOrder().orElse(null) != null) {
				if (ns2.getShOrder().orElse(null) != null) {
					return ((ns1.getShOrder().get() - ns2.getShOrder().get()) > 0)?1:-1;
				} else {
					return -1;
				}
			} else {
				if (ns2.getShOrder().orElse(null) != null) {
					return 1;
				} else {
					// both sh:order are null, try with their display label
					return ns1.getDisplayLabel(owlGraph, lang).compareToIgnoreCase(ns2.getDisplayLabel(owlGraph, lang));
				}
			}
		}
		
	}

}