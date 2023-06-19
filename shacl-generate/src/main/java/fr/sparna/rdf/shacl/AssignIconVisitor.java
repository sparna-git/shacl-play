package fr.sparna.rdf.shacl;

import java.util.Arrays;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.generate.ShaclGenerator;

public class AssignIconVisitor implements ShaclVisitorIfc {

	private static final Logger log = LoggerFactory.getLogger(ShaclGenerator.class);

	private String fontAwesomeStyle = "fa-duotone";
	private String iconAnnotation = "http://data.sparna.fr/ontologies/volipi#iconName";
	private Model model;
	
	private enum CLASS_ICON {
		
		// FOAF stuff
		FOAF_PERSON(FOAF.Person.getURI(), "fa-user"),
		FOAF_DOCUMENT(FOAF.Document.getURI(), "fa-file"),
		FOAF_ORGANIZATION(FOAF.Organization.getURI(), "fa-building"),
		FOAF_GROUP(FOAF.Group.getURI(), "fa-users"),
		
		// EDM stuff
		EDM_AGGREGATION(EDM.AGGREGATION, "fa-object-group"),
		EDM_PROXY(EDM.PROXY, "fa-arrows-to-circle"),
		EDM_WEB_RESOURCE(EDM.WEB_RESOURCE, "fa-photo-film-music"),
		EDM_COLLECTION(EDM.COLLECTION, "fa-box-open-full"),
		EDM_PROVIDED_CHO(EDM.PROVIDED_CHO, "fa-palette");
		
		private String uri;
		private String iconName;
		private CLASS_ICON(String uri, String iconName) {
			this.uri = uri;
			this.iconName = iconName;
		}
		
		public static CLASS_ICON findByUri(String uri) {
			for(CLASS_ICON entry : Arrays.asList(CLASS_ICON.values())) {
				if(entry.uri.equals(uri)) {
					return entry;
				}
			}
			return null;
		}
	}
	
	public AssignIconVisitor() {
	}
	
	@Override
	public void visitModel(Model model) {
		this.model = model;
		
		// add volipi namespace
		model.setNsPrefix("volipi", "http://data.sparna.fr/ontologies/volipi#");
	}

	@Override
	public void visitOntology(Resource ontology) {

	}

	@Override
	public void visitNodeShape(Resource aNodeShape) {
		// read target class
		// use a toList to avoid ConcurrentModificationException
		aNodeShape.listProperties(SHACLM.targetClass).toList().stream().forEach(s -> {
			Resource c = s.getResource();
			CLASS_ICON entry = CLASS_ICON.findByUri(c.getURI());
			if(entry != null) {
				// assign icon to NodeShape
				String iconName = this.fontAwesomeStyle+" "+entry.iconName;
				log.debug("Assigned icon to NodeShape "+aNodeShape.getURI()+" : '"+iconName+"'");
				aNodeShape.addProperty(aNodeShape.getModel().createProperty(iconAnnotation), iconName);
			}
		});
	}

	@Override
	public void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape) {

	}

	@Override
	public void leaveModel(Model model) {
		
	}

}
