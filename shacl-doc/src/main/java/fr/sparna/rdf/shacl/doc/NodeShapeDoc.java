package fr.sparna.rdf.shacl.doc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.jena.shacl.NodeShape;
import fr.sparna.rdf.jena.shacl.PropertyShape;
import fr.sparna.rdf.jena.shacl.ShOrderComparator;



public class NodeShapeDoc extends NodeShape  {

	private Resource nodeShape;
	
	
	protected List<PropertyShape> propertiesDoc = new ArrayList<>();
	
	public NodeShapeDoc(Resource nodeShape) {
		super(nodeShape);
		this.nodeShape = nodeShape;
	}
	
	public String getRdfsLabelAsString(String lang) {
		return ModelRenderingUtils.render(super.getRdfsLabel(lang), true);
	}	
	
	public String getURIOrId() {
		if(super.getShape().isURIResource()) {
			return super.getShape().getURI();
		} else {
			// returns the blank node ID in that case
			return super.getShape().asResource().getId().getLabelString();
		}
	}
	
	public List<PropertyShape> getPropertiesDoc() {
		return propertiesDoc;
	}

	public void setPropertiesDoc(List<PropertyShape> propertiesDoc) {
		this.propertiesDoc = propertiesDoc;
	}

	public Float getShOrderDoc() {
		return this.getShOrder().orElse(null);
	}
	
	public List<Resource> getShTargetClassRdfsSubclassOfInverseOfShTargetClass() {
		Set<Resource> result = new HashSet<Resource>();
		List<Resource> targetClasses = super.getAllTargetedClasses();
		if(targetClasses != null) {
			
			List<Resource> subClassesOf = targetClasses.stream().flatMap( t -> NodeShape.getRdfsSubClassOfOf(t).stream()).collect(Collectors.toList());
			
			if(subClassesOf != null && subClassesOf.size() > 0) {
				for (Resource aSuperClass : subClassesOf) {
					List<Resource> shapeWithThisTarget = nodeShape.getModel().listStatements(null, SH.targetClass, aSuperClass).toList().stream().map(s -> s.getSubject()).collect(Collectors.toList());
					for (Resource aShapeWithSuperClassAsTarget : shapeWithThisTarget) {
						result.add(aShapeWithSuperClassAsTarget);
					}
				}
			}
		}
		
		return new ArrayList<Resource>(result);
	}
	
	/**
	 * Returns a list containing :
	 * 1. the shapes that this one is subClassOf 
	 * 2. plus the shapes that target a class, which the class that this shape target is a subClassOf.
	 * 3. plus a shape that is referenced by this shape by a sh:node
	 * 
	 * @return
	 */
	public List<Resource> getSuperShapes() {
		Set<Resource> superShapes = new HashSet<Resource>();
		superShapes.addAll(this.getRdfsSubClassOf());
		superShapes.addAll(this.getShTargetClassRdfsSubclassOfInverseOfShTargetClass());
		superShapes.addAll(this.getShNodeAsList());
		return new ArrayList<Resource>(superShapes);
	}
	
	public List<Depiction> getFoafDepiction() {
		
		//List<Statement> depic = nodeShape.listProperties(FOAF.depiction).toList();
		List<Resource> depictionsResources = new ArrayList<>();

		for (Resource aDepiction : super.getDepiction()) {
			if(
				aDepiction.getURI() != null
				&&
				(
					aDepiction.getURI().contains(".jpg")
					||
					aDepiction.getURI().contains(".png")
				)
			)
			depictionsResources.add(aDepiction);					
		}
		
		depictionsResources.sort(new ShOrderComparator());
		
		List<Depiction> depictions = new ArrayList<>();
		for (Resource r : depictionsResources) {
			Depiction aDepiction = new Depiction();
			aDepiction.setSrc(r.getURI());
			aDepiction.setShorder(ShOrderComparator.getShOrderOf(r));
			
			// dcterms:title
			Optional.ofNullable(r.getProperty(DCTerms.title)).map(s -> s.getString()).ifPresent(title -> aDepiction.setTitle(title));
			
			// dcterms:description
			Optional.ofNullable(r.getProperty(DCTerms.description)).map(s -> s.getString()).ifPresent(title -> aDepiction.setDescription(title));
			depictions.add(aDepiction);
		}
		
		return depictions;	
	}

	
}





