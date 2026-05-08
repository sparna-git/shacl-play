package fr.sparna.rdf.shacl.doc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.jena.ModelReadingUtils;
import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.jena.shacl.NodeShape;
import fr.sparna.rdf.jena.shacl.ShOrderComparator;
import fr.sparna.rdf.jena.shacl.ShapesGraph;

public class NodeShapeDoc extends NodeShape  {

	private Resource nodeShape;
	
	
	protected List<PropertyShapeDoc> propertiesDoc = new ArrayList<>();
	
	public NodeShapeDoc(Resource nodeShape) {
		super(nodeShape);
		this.nodeShape = nodeShape;
	}
	
	public String getRdfsLabelAsString(String lang) {
		return ModelRenderingUtils.render(super.getRdfsLabel(lang), true);
	}	
	
	public String getURIOrId() {
		if(super.getNodeShape().isURIResource()) {
			return super.getNodeShape().getURI();
		} else {
			// returns the blank node ID in that case
			return super.getNodeShape().asResource().getId().getLabelString();
		}
	}
	
	public String getDisplayDescription(Model owlModel, String lang) {
		String result = ModelRenderingUtils.render(super.getSkosDefinition(lang), true);
		
		if(result == null) {
			result = ModelRenderingUtils.render(super.getRdfsComment(lang), true);
		}
		
		if(result == null && super.getAllTargetedClasses().size() > 0) {
			// otherwise if we have skos:definition on the class, take it
			for (Resource t : super.getAllTargetedClasses()) {
				String res = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(owlModel.getResource(t.getURI()), SKOS.definition, lang), true);
			    if (res != null) {
			    	result.join(",",res);
			    }
			}
			
		}
		
		if(result == null && super.getAllTargetedClasses().size() > 0) {
			// otherwise if we have rdfs:comment on the class, take it
			for (Resource t : super.getAllTargetedClasses()) {
				String res = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(owlModel.getResource(t.getURI()), RDFS.comment, lang), true);
			    if (res != null) {
			    	result.join(",",res);
			    }
			}
		}
		
		return result;
	}
	
	public List<PropertyShapeDoc> getPropertiesDoc() {
		return propertiesDoc;
	}

	public void setPropertiesDoc(List<PropertyShapeDoc> propertiesDoc) {
		this.propertiesDoc = propertiesDoc;
	}

	public Float getShOrderDoc() {
		return this.getShOrder().orElse(null);
	}
	
	public List<Resource> getRdfsSubClassOf() {
		return this.getRdfsSubClassOfOf(nodeShape);
	}
	
	public static List<Resource> getRdfsSubClassOfOf(Resource resource) {
		return resource.listProperties(RDFS.subClassOf).toList().stream()
				.map(s -> s.getResource())
				.filter(r -> { return r.isURIResource() && !r.getURI().equals(OWL.Thing.getURI()); })
				.collect(Collectors.toList());
	}
	
	public List<Resource> getShTargetClassRdfsSubclassOfInverseOfShTargetClass() {
		Set<Resource> result = new HashSet<Resource>();
		List<Resource> targetClass = super.getAllTargetedClasses();
		if(targetClass != null) {
			
			List<Resource> subClassesOf = new ArrayList<>();
			for (Resource r : targetClass) {
				subClassesOf.addAll(this.getRdfsSubClassOfOf(r));
			}
			
			//List<Resource> subClassesOf = NodeShape.getRdfsSubClassOfOf(targetClass);
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
		List<Resource> superShapes = new ArrayList<Resource>();
		superShapes.addAll(this.getRdfsSubClassOf());
		superShapes.addAll(this.getShTargetClassRdfsSubclassOfInverseOfShTargetClass());
		superShapes.addAll(this.getShNodeAsList());
		return superShapes;
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

	public List<UsageDoc> getUsage() {

		ShapesGraph shapesGraphUsage = new ShapesGraph(this.getNodeShape().getModel());
		// Get all Resources
		List<Resource> ListOfPropertiesByUsage =  shapesGraphUsage.getResourceByUsage(this.nodeShape);
		//
		List<UsageDoc> nsUsageAsList = new ArrayList<>();
		if (ListOfPropertiesByUsage.size() > 0) {
			for (Resource r : ListOfPropertiesByUsage) {
				if (!r.isAnon() && r.isResource()) {
					List<Resource> nResourceFound = shapesGraphUsage.findNodeShapeByProperty(r);
					//System.out.println("Number of Resouces: " + nResourceFound.size());
					for(Resource rFound : nResourceFound) {
						NodeShapeDoc nsUsage = new NodeShapeDoc(rFound);						
						PropertyShapeDoc psDocUsage = new PropertyShapeDoc(r);
						boolean nsExist = nsUsageAsList.stream().filter( nsList -> nsList.getNodeShape().getNodeShape().getURI().equals(nsUsage.getNodeShape().getURI())).findFirst().isPresent();
						if (!nsExist) {
							UsageDoc usDoc = new UsageDoc();
							usDoc.setNodeShape(nsUsage);
							List<PropertyShapeDoc> psList = new ArrayList<>();
							psList.add(psDocUsage);
							usDoc.setProperties(psList);
							nsUsageAsList.add(usDoc);
						} else {
							Integer nCount = 0;
							for (UsageDoc nsResource : nsUsageAsList) {
								if (nsResource.getNodeShape().getNodeShape().getURI().equals(nsUsage.getNodeShape().getURI())) {
									List<PropertyShapeDoc> psList = nsResource.getProperties();
									boolean nsExistProperty = psList.stream().filter( pp -> pp.getPropertyShape().getURI().equals(psDocUsage.getPropertyShape().getURI()) ).findFirst().isPresent();
									if (!nsExistProperty) {
										List<PropertyShapeDoc> p = nsResource.getProperties();
										p.add(psDocUsage);
										nsResource.setProperties(p);
										nsUsageAsList.set(nCount, nsResource);
									}
								}
								nCount++;
							}							
						}

					}
				}
			}
		}
		
		return nsUsageAsList;

	}
}