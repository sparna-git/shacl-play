package fr.sparna.rdf.shacl.shacl2xsd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

public class OntologyBoxRead {
	
	public OntologyBox readOntology(Model GraphModel) {
		
		OntologyBox p = new OntologyBox();
		
		p.setXsdRootElement(this.readRootElement(GraphModel));
		p.setOntoImports(this.readOwlforImports(GraphModel));
		p.setOntoClass(this.readOwlforClass(GraphModel));
		p.setOntoOP(this.readOwlforOP(GraphModel));
		return p;
		
	}
	
	
	public String readRootElement(Model owl){
		String value = null;
		List<Resource> owlResource = owl.listResourcesWithProperty(RDF.type, OWL.Ontology).toList();
		for(Resource owlOntology : owlResource) {			
			if(owlOntology.hasProperty(owlOntology.getModel().createProperty("http://shacl-play.sparna.fr/ontology#xsdRootElement"))) {
				value = owlOntology.getProperty(owlOntology.getModel().createProperty("http://shacl-play.sparna.fr/ontology#xsdRootElement")).getObject().toString();			
				
			}			
		}			
		return value;
	}
	
	public List<OntologyImports> readOwlforImports(Model owl){
		// Create a HashMap object called capitalCities
	    Map<String,String> mapPrefix = owl.getNsPrefixMap();
	    Map<String,String> Ontoimport = new HashMap<String, String>();
		List<OntologyImports> imp = new ArrayList<>();
		List<Resource> owlResource = owl.listResourcesWithProperty(RDF.type, OWL.Ontology).toList();
		for(Resource readOwl : owlResource) {
			List<Statement> readImports = readOwl.listProperties(OWL.imports).toList();
			for(Statement src : readImports){
				for(String key : mapPrefix.keySet()) {
					if(src.getObject().toString().equals(mapPrefix.get(key))) {
						Ontoimport.put(key, src.getObject().toString());						
					}	
					
				}
				
			}
		}	
		
		List<OntologyImports> listowlimp = new ArrayList<>();
		if(Ontoimport.size() > 0) {
			System.out.println("Get Imports");
			for(Map.Entry m: Ontoimport.entrySet()){ 	
				System.out.println("Imports: "+m.getKey().toString()+" - "+m.getValue().toString());
				OntologyImports owlimp = new OntologyImports();				
				owlimp.setImportSchema(m.getKey().toString());
				owlimp.setImportURI(m.getValue().toString());
				listowlimp.add(owlimp);
			}
		}
		return listowlimp;		
	}
	
	
	public List<OntologyClass> readOwlforClass(Model owl){
		
		List<OntologyClass> imp = new ArrayList<>();
		List<Resource> owlResource = owl.listResourcesWithProperty(RDF.type, OWL.Class).toList();
		
		for(Resource readOwl : owlResource) {
			OntologyClass owlimp = new OntologyClass();
			
			owlimp.setClassName(readOwl.getLocalName());
			
			if(readOwl.hasProperty(RDFS.comment)) {
				owlimp.setCommentRDFS(readOwl.getProperty(RDFS.comment).getString());
			}
			
			if(readOwl.hasProperty(RDFS.subClassOf)) {
				owlimp.setSubClassOfRDFS(readOwl.getModel().shortForm(readOwl.getProperty(RDFS.subClassOf).getResource().getURI()));
			}
			
			imp.add(owlimp);
		}		
		return imp;		
	}
	
	public List<OntologyObjectProperty> readOwlforOP(Model owl){
		
		List<OntologyObjectProperty> imp = new ArrayList<>();
		List<Resource> owlResource = owl.listResourcesWithProperty(RDF.type, OWL.ObjectProperty).toList();
		
		for(Resource readOwl : owlResource) {
			OntologyObjectProperty owlimp = new OntologyObjectProperty();
			
			owlimp.setPropertyName(readOwl.getLocalName());
			
			if(readOwl.hasProperty(RDFS.comment)) {
				owlimp.setCommentRDFS(readOwl.getProperty(RDFS.comment).getString());
			}			
			
			imp.add(owlimp);
		}		
		return imp;		
	}

}
