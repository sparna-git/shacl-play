package fr.sparna.rdf.shacl.doc.read;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.PrefixMapBase;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.jenax.util.ExtraPrefixes;
import org.topbraid.shacl.util.PrefixUtil;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.doc.JenaUtil;
import fr.sparna.rdf.shacl.doc.ShaclBox;
import fr.sparna.rdf.shacl.doc.ShaclProperty;
import fr.sparna.rdf.shacl.doc.model.NamespaceSections;
import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationSection;

public class ShapesDocumentationModelReader implements ShapesDocumentationReaderIfc {

	@Override
	public ShapesDocumentation readShapesDocumentation(Model shaclGraph, Model owlGraph, String lang, String fileName) {

		// Recuperation de prefix et  Namespace
		Map<String, String> map = shaclGraph.getNsPrefixMap();		
		
		List<Resource> nodeShapes = shaclGraph.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();
		
		// 1. Lire toutes les classes
		ArrayList<ShaclBox> Shaclvalue = new ArrayList<>();
		for (Resource nodeShape : nodeShapes) {
			ShaclBox dbShacl = new ShaclBox(nodeShape, lang);
			Shaclvalue.add(dbShacl);
		}

		// 2. Lire les propriétés
		for (ShaclBox aBox : Shaclvalue) {
			aBox.readProperties(aBox.getNodeShape(), Shaclvalue, lang);
		}	
		
		
		// Lecture de OWL
		List<Resource> sOWL = shaclGraph.listResourcesWithProperty(RDF.type, OWL.Ontology).toList();
		String sOWLlabel = null;
		for (Resource readRDF : sOWL) {	
			sOWLlabel = readRDF.getProperty(RDFS.label).getString();
		}
		
		// Code XML
		List<ShapesDocumentationSection> ListTitle = new ArrayList<>();	
		ShapesDocumentation shapesDocumentation = new ShapesDocumentation();
		shapesDocumentation.setTitle(sOWLlabel);
		
		String pattern_node_nodeshape = null;
		// for pour afficher l'information des prefix et namespace
		List<NamespaceSections> tPrefixPropriete = new ArrayList<>();		
		//Les prefix et namespace
		for (Map.Entry<String, String> me : map.entrySet()) {
			NamespaceSections tPrefix = new NamespaceSections();	
			tPrefix.setOutput_prefix(me.getKey());
			tPrefix.setOutput_namespace(me.getValue());		
			tPrefixPropriete.add(tPrefix);
		}	
		shapesDocumentation.setShnamespace(tPrefixPropriete);
		
		
		// Property NodeShape
		for (ShaclBox datanodeshape : Shaclvalue) {
			if (datanodeshape.getNametargetclass() != null) {
				ShapesDocumentationSection sectionPrincipal = new ShapesDocumentationSection();
				sectionPrincipal.setTitle(datanodeshape.getRdfslabel());
				sectionPrincipal.setdURI(datanodeshape.getNameshape());
				sectionPrincipal.setComments(datanodeshape.getRdfsComment());
				
				List<PropertyShapeDocumentation> ListPropriete = new ArrayList<>();
				for (ShaclProperty propriete : datanodeshape.getProperties()) {
					// Récuperation du pattern si le node est une NodeShape
					if (propriete.getnode() != null) {
						for (ShaclBox pattern_other_nodeshape : Shaclvalue) {
							if (propriete.getnode().contains(pattern_other_nodeshape.getNameshape())) {
								pattern_node_nodeshape = pattern_other_nodeshape.getShaclpatternNodeShape();
								break;
							}
						}
					}
					//
					PropertyShapeDocumentation proprieteDoc = new PropertyShapeDocumentation();
					
					proprieteDoc.setOutput_language(propriete.getShLanguage());
					proprieteDoc.setOutput_propriete(propriete.getname(),null);
					proprieteDoc.setOutput_uri(propriete.getpath());
					proprieteDoc.setOutput_valeur_attendus(propriete.getclass(), propriete.getnode(),
							propriete.getclass_property(), propriete.getdatatype(), propriete.getnodeKind(),
							propriete.getpath());
					proprieteDoc.setOutput_patterns(propriete.getpattern(), datanodeshape.getShaclpatternNodeShape(),
							pattern_node_nodeshape, propriete.getclass(), propriete.getnode(),
							propriete.getclass_property(), propriete.getdatatype(), propriete.getnodeKind(),
							propriete.getpath());
					proprieteDoc.setOutput_Cardinalite(propriete.getcardinality());
					proprieteDoc.setOutput_description(propriete.getdescription(), datanodeshape.getRdfsComment());
					proprieteDoc.setOutput_shin(propriete.getShin());

					ListPropriete.add(proprieteDoc);
				}
				sectionPrincipal.setPropertySections(ListPropriete);
				ListTitle.add(sectionPrincipal);				
			}
			
		}
		shapesDocumentation.setSections(ListTitle);
		return shapesDocumentation;
	}

}
