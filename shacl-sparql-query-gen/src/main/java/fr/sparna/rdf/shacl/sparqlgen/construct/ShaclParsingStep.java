package fr.sparna.rdf.shacl.sparqlgen.construct;

import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.PathParser;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.sparqlgen.shaclmodel.NodeShape;
import fr.sparna.rdf.shacl.sparqlgen.shaclmodel.PropertyShape;

/**
 * Represents one step in the parsing of the SHACL file. Each step is caracterised with :
 *   - The property shape that was followed
 *   - Whether the property shape was followed inverse or not (only inverse path are supported, not other kinds of paths
 *   - The node shape reached
 *   
 */
public class ShaclParsingStep {

	protected PropertyShape propertyShape;
	protected boolean reverse = false;
	protected NodeShape nodeShape;
	
	
	public ShaclParsingStep(PropertyShape propertyShape, boolean reverse, NodeShape nodeShape) {
		super();
		this.propertyShape = propertyShape;
		this.reverse = reverse;
		this.nodeShape = nodeShape;
	}
	
	/**
	 * Returns the variable name in the SPARQL query associated to this step
	 * @return
	 */
	public String getVarName() {
		/*
		if(this.getPropertyShape().isPathURI()) {
			return this.getPropertyShape().getPath().getLocalName();
		} else {
			// TODO : need to handle more SHACL property paths
			if(this.getPropertyShape().getPath().hasProperty(SH.inversePath)) {
				Resource prop = this.getPropertyShape().getPath().getPropertyResourceValue(SH.inversePath);
				return "inverse_"+prop.getLocalName();
			} else {
				throw new IllegalArgumentException("Cannot handle this sh:path. Can handle only simple property or sh:inversePath");
			}
		}*/
		return this.nodeShape.getNodeShapeResource().getLocalName();
		
	}
	
	/**
	 * Returns the SPARQL path for this step
	 * @return
	 */
	public Path getPath() {
		String path = (this.reverse)?"^<"+this.propertyShape.getPath().getPropertyResourceValue(SH.inversePath).getURI()+">":"<"+this.propertyShape.getPath().getURI()+">";
		return PathParser.parse(path,ModelFactory.createDefaultModel());
	}


	public PropertyShape getPropertyShape() {
		return propertyShape;
	}

	public void setPropertyShape(PropertyShape propertyShape) {
		this.propertyShape = propertyShape;
	}

	public boolean isReverse() {
		return reverse;
	}

	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}

	public NodeShape getNodeShape() {
		return nodeShape;
	}

	public void setNodeShape(NodeShape nodeShape) {
		this.nodeShape = nodeShape;
	}
	
}
