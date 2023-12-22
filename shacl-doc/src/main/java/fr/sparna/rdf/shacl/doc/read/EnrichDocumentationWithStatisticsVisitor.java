package fr.sparna.rdf.shacl.doc.read;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.VOID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.doc.NodeShape;
import fr.sparna.rdf.shacl.doc.PropertyShape;
import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationSection;
import fr.sparna.rdf.shacl.generate.visitors.ShaclVisitorIfc;

public class EnrichDocumentationWithStatisticsVisitor implements ShaclVisitorIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private Model statisticsModel;
	private ShapesDocumentation documentation;
	
	public EnrichDocumentationWithStatisticsVisitor(Model statisticsModel, ShapesDocumentation documentation) {
		super();
		this.statisticsModel = statisticsModel;
		this.documentation = documentation;
	}

	@Override
	public void visitModel(Model model) {
		// nothing
	}

	@Override
	public void visitOntology(Resource ontology) {
		// nothing
	}

	@Override
	public void visitNodeShape(Resource aNodeShape) {
		// read corresponding class partition and find number of entities
		List<Resource> classPartitions = statisticsModel.listStatements(null, DCTerms.conformsTo, aNodeShape).mapWith(t-> t.getSubject()).toList();
		if(classPartitions.size() == 0) {
			log.debug("Cannot find corresponding class partition for "+aNodeShape);
		} else {
			if(classPartitions.size() > 1) {
				log.debug("More than one class partition found for "+aNodeShape);
			}
			Resource aClassPartition = classPartitions.get(0);
			
			// read number of entities 
			if(aClassPartition.hasProperty(VOID.entities)) {
				int instances = aClassPartition.getProperty(VOID.entities).getInt();
				
				// find corresponding section
				NodeShape ns = new NodeShape(aNodeShape);
				ShapesDocumentationSection section = this.documentation.findSectionByUriOrId(ns.getURIOrId());
				if(section != null) {
					section.setNumberOfTargets(instances);
				} else {
					log.warn("Cannot find documentation section with URI or ID "+ns.getURIOrId());
				}
			}
		}
	}

	@Override
	public void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape) {
		// read corresponding property partition
		List<Resource> propertyPartitions = statisticsModel.listStatements(null, DCTerms.conformsTo, aPropertyShape).mapWith(t-> t.getSubject()).toList();
		
		if(propertyPartitions.size() == 0) {
			log.debug("Cannot find corresponding property partition for "+aPropertyShape);
		} else {
			if(propertyPartitions.size() > 1) {
				log.debug("More than one property partition found for "+aPropertyShape);
			}
			Resource aPropertyPartition = propertyPartitions.get(0);
			
			// find corresponding section in doc
			NodeShape ns = new NodeShape(aNodeShape);
			ShapesDocumentationSection section = this.documentation.findSectionByUriOrId(ns.getURIOrId());
			if(section != null) {
				PropertyShape ps = new PropertyShape(aPropertyShape);
				PropertyShapeDocumentation propertySection = section.findPropertyShapeDocumentationSectionByUriOrId(ps.getURIOrId());
				
				if(propertySection != null) {
					// read number of triples and distinct subjects 
					if (aPropertyPartition.hasProperty(VOID.triples)) {
						int triples = aPropertyPartition.getProperty(VOID.triples).getObject().asLiteral().getInt();
						propertySection.setTriples(triples);
					}
					
					if (aPropertyPartition.hasProperty(VOID.distinctObjects)) {
						int distinctObjects = aPropertyPartition.getProperty(VOID.distinctObjects).getObject().asLiteral().getInt();
						propertySection.setDistinctObjects(distinctObjects);
					}
				}
			}
		}
	}

	@Override
	public void leaveModel(Model model) {
		// nothing
	}

}
