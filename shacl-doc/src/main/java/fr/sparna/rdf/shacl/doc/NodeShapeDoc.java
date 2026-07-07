package fr.sparna.rdf.shacl.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;

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





