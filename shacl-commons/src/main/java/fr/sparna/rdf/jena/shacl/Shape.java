package fr.sparna.rdf.jena.shacl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.atlas.logging.Log;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;

import fr.sparna.rdf.jena.ModelReadingUtils;
import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.shacl.*;
import org.topbraid.shacl.vocabulary.SH;

public abstract class Shape {
	
	protected Resource shape;

	public Shape(Resource shape) {  
	    this.shape = shape;		
	}
	
	/**
	 * @return the underlying shape Resource
	 */
	public Resource getShape() {
		return shape;
	}

	public Optional<Resource> getShNode() {
		return ModelReadingUtils.getOptionalResource(shape, SH.node);
	}

	public Optional<Literal> getColor() {
		return ModelReadingUtils.getOptionalLiteral(shape, shape.getModel().createProperty(SHACL_PLAY.COLOR));
	}

	public RDFList getShOr() {
		if (shape.hasProperty(SH.or)) {
			return shape.getProperty(SH.or).getList(); 
		} else {
			return null;
		}		
	}

	public Optional<Resource> getShClass() {
		return ModelReadingUtils.getOptionalResource(shape, SH.class_);
	}

	public Optional<Resource> getShDatatype() {
		return ModelReadingUtils.getOptionalResource(this.shape, SH.datatype);
	}
	
	public Optional<Resource> getShNodeKind() {
		return ModelReadingUtils.getOptionalResource(this.shape, SH.nodeKind);
	}
	
	public Optional<Literal> getShPattern() {
		return ModelReadingUtils.getOptionalLiteral(this.shape, SH.pattern);
	}

	public Optional<Literal> getShOrder() {
		return ModelReadingUtils.getOptionalLiteral(this.shape, SH.order);
	}

	public Optional<Literal> getShMinLength() {
		return ModelReadingUtils.getOptionalLiteral(this.shape, SH.minLength);
	}
	
	public Optional<Literal> getShMaxLength() {
		return ModelReadingUtils.getOptionalLiteral(this.shape, SH.maxLength);
	}
	
	public Optional<Literal> getShMinInclusive() {
		return ModelReadingUtils.getOptionalLiteral(this.shape, SH.minInclusive);
	}
	
	public Optional<Literal> getShMaxInclusive() {
		return ModelReadingUtils.getOptionalLiteral(this.shape, SH.maxInclusive);
	}
	
	public Optional<Literal> getShMinExclusive() {
		return ModelReadingUtils.getOptionalLiteral(this.shape, SH.minExclusive);
	}
	
	public Optional<Literal> getShMaxExclusive() {
		return ModelReadingUtils.getOptionalLiteral(this.shape, SH.maxExclusive);
	}

	public Optional<Literal> getShDeactivated() {
		return ModelReadingUtils.getOptionalLiteral(this.shape,SH.deactivated);
	}

	public List<Literal> getExamples() {
		return ModelReadingUtils.readLiteral(shape, SKOS.example);
	}

	public boolean hasShOrShClassOrShNode() {
		return 
				(this.getShOrShClass() != null && this.getShOrShClass().size() > 0)
				|| 
				(this.getShOrShNode() != null && this.getShOrShNode().size() > 0)
		;
	}

	public List<Resource> getShOrShDatatype() {
		if (this.shape.hasProperty(SH.or)) {			
			List<Resource> values = ShOrReadingUtils.readShDatatypeInShOr(this.shape.getProperty(SH.or).getList());
			if(values.size() > 0) {
				return values;
			}
		}		
		return null;
	}
	
	public List<Resource> getShOrShNodeKind() {
		if (this.shape.hasProperty(SH.or)) {			
			List<Resource> values = ShOrReadingUtils.readShNodeKindInShOr(this.shape.getProperty(SH.or).getList());
			if(values.size() > 0) {
				return values;
			}
		}		
		return null;
	}
	
	public List<Resource> getShOrShClass() {
		if (this.shape.hasProperty(SH.or)) {			
			List<Resource> values = ShOrReadingUtils.readShClassInShOr(this.shape.getProperty(SH.or).getList());
			if(values.size() > 0) {
				return values;
			}
		}
		
		return null;
	}
	
	public List<Resource> getShOrShNode() {
		if (this.shape.hasProperty(SH.or)) {			
			List<Resource> values = ShOrReadingUtils.readShNodeInShOr(this.shape.getProperty(SH.or).getList());
			if(values.size() > 0) {
				return values;
			}
		}
		
		return null;
	}

	public boolean isDeactivated() {
		return this.getShDeactivated().isPresent() && this.getShDeactivated().get().getBoolean();
	}

	public Float getOrderFloat() {
		return this.getShOrder().map(node -> node.asLiteral().getFloat()).orElse(null);
	}

}