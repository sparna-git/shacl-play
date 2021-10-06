package fr.sparna.rdf.shacl.shacl2xsd;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

public class OntologyBoxRead {
	
	public OntologyBox readOntology(Model GraphModel) {
		
		OntologyBox p = new OntologyBox();
		p.setOntoImports(this.readOwlforImports(GraphModel));
		p.setOntoClass(this.readOwlforClass(GraphModel));
		p.setOntoOP(this.readOwlforOP(GraphModel));
		return p;
		
	}
	
	public List<OntologyImports> readOwlforImports(Model owl){
		
		List<OntologyImports> imp = new ArrayList<>();
		List<Resource> owlResource = owl.listResourcesWithProperty(RDF.type, OWL.Ontology).toList();
		
		for(Resource readOwl : owlResource) {
			OntologyImports owlimp = new OntologyImports();
			owlimp.setImportSchema(readOwl.getModel().shortForm(readOwl.getProperty(OWL.imports).getResource().getURI()));
			owlimp.setImportURI(readOwl.getProperty(OWL.imports).getResource().getURI());
			imp.add(owlimp);
		}		
		return imp;		
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
