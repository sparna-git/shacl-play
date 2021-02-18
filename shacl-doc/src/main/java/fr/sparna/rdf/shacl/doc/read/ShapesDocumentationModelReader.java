package fr.sparna.rdf.shacl.doc.read;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.diagram.ShaclPlantUmlWriter;
import fr.sparna.rdf.shacl.doc.ConstraintValueReader;
import fr.sparna.rdf.shacl.doc.ShaclBox;
import fr.sparna.rdf.shacl.doc.ShaclPrefix;
import fr.sparna.rdf.shacl.doc.ShaclProperty;
import fr.sparna.rdf.shacl.doc.model.NamespaceSections;
import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationSection;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class ShapesDocumentationModelReader implements ShapesDocumentationReaderIfc {

	@Override
	public ShapesDocumentation readShapesDocumentation(Model shaclGraph, Model owlGraph, String lang, String fileName) {

		// Recuperation de prefix et Namespace
		Map<String, String> map = shaclGraph.getNsPrefixMap();

		List<Resource> nodeShapes = shaclGraph.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();

		// 1. Lire toutes les classes
		ArrayList<ShaclBox> Shaclvalue = new ArrayList<>();
		for (Resource nodeShape : nodeShapes) {
			ShaclBox dbShacl = new ShaclBox(nodeShape, lang);
			Shaclvalue.add(dbShacl);
		}

		List aContent = new ArrayList<>();
		for (ShaclBox shOrder : Shaclvalue) {
			aContent.add(shOrder.getShOrder());
		}

		if (aContent.contains(0)) {
			Collections.sort(Shaclvalue, Comparator.comparing(ShaclBox::getNameshape));
		} else {
			Collections.sort(Shaclvalue, Comparator.comparing(ShaclBox::getShOrder));
		}

		// 2. Lire les propriétés
		for (ShaclBox aBox : Shaclvalue) {
			aBox.readProperties(aBox.getNodeShape(), Shaclvalue, lang);
		}

		// Lire les proprietes et récuperer les prefix a utilisé
		List<String> aPrefix = new ArrayList<>();
		for (ShaclBox aBox : Shaclvalue) {
		    aBox.readPropertiesPrefix(aBox.getNodeShape());
		    for(ShaclPrefix shprefix : aBox.getShacl_prefix()) {
		    	String value = null;
		    	if(shprefix.getPrefix_shClass() != null) {
		    		value = shprefix.getPrefix_shClass(); 
		    		aPrefix.add(value.split(":")[0]);
		    	}
		    	if(shprefix.getPrefix_shdatatype() != null) {
		    		value = shprefix.getPrefix_shdatatype();
		    		aPrefix.add(value.split(":")[0]);
		    	}
		    	if(shprefix.getPrefix_shhasvalue() != null) {
		    		value = shprefix.getPrefix_shhasvalue(); 
		    		aPrefix.add(value.split(":")[0]);
		    	}
		    	if(shprefix.getPrefix_shin() != null) {
		    		value = shprefix.getPrefix_shin(); 
		    		aPrefix.add(value.split(":")[0]);
		    	}
		    	if(shprefix.getPrefix_shpath() != null) {
		    		value = shprefix.getPrefix_shpath();
		    		aPrefix.add(value.split(":")[0]);
		    	}
		    	if(shprefix.getPrefix_shTargetClass() != null) {
		    		value = shprefix.getPrefix_shTargetClass();
		    		aPrefix.add(value.split(":")[0]);
		    	}
		    }
		}
		
		HashSet<String> aPrefixsh = new HashSet(aPrefix);
		// Lecture de OWL
		ConstraintValueReader ReadValue = new ConstraintValueReader();
		List<Resource> sOWL = shaclGraph.listResourcesWithProperty(RDF.type, OWL.Ontology).toList();
		String sOWLlabel = null;
		String sOWLComment = null;
		String sOWLDateModified = null;
		String sOWLVersionInfo = null;
		for (Resource rOntology : sOWL) {
			sOWLlabel = ReadValue.readValueconstraint(rOntology, RDFS.label, lang);
			sOWLComment = ReadValue.readValueconstraint(rOntology, RDFS.comment, lang);
		}		

		// Code XML
		List<ShapesDocumentationSection> ListTitle = new ArrayList<>();
		ShapesDocumentation shapesDocumentation = new ShapesDocumentation();
		shapesDocumentation.setTitle(sOWLlabel);
		shapesDocumentation.setCommentOntology(sOWLComment);
		shapesDocumentation.setVersionOntology(sOWLVersionInfo);
		String pattern_node_nodeshape = null;
		// for pour afficher l'information des prefix et namespace
		List<NamespaceSections> tPrefixPropriete = new ArrayList<>();
		// Les prefix et namespace
		for (String aPrefixp : aPrefixsh) {
			for (Map.Entry<String, String> me : map.entrySet()) {
				NamespaceSections tPrefix = new NamespaceSections();
				if (me.getKey().equals(aPrefixp)) {					
					tPrefix.setOutput_prefix(me.getKey());
					tPrefix.setOutput_namespace(me.getValue());
					tPrefixPropriete.add(tPrefix);
					break;
				}				
			}
		}
		
		
		
		
		
		
		//

		shapesDocumentation.setShnamespace(tPrefixPropriete);
		// Property NodeShape
		for (ShaclBox datanodeshape : Shaclvalue) {
			if (datanodeshape.getNametargetclass() != null) {
				//
				ShapesDocumentationSection sectionPrincipal = new ShapesDocumentationSection();
				//
				sectionPrincipal.setTitle(datanodeshape.getRdfslabel());
				sectionPrincipal.setdURI(datanodeshape.getNameshape());
				sectionPrincipal.setComments(datanodeshape.getRdfsComment());
				sectionPrincipal.setPatternNS(datanodeshape.getShpatternNodeShape());
				sectionPrincipal.setNodeKindNS(datanodeshape.getShnodeKind());
				sectionPrincipal.setCloseNS(datanodeshape.getShClose());
				// Information de l'Ontology

				List<PropertyShapeDocumentation> ListPropriete = new ArrayList<>();
				for (ShaclProperty propriete : datanodeshape.getProperties()) {
					// Récuperation du pattern si le node est une NodeShape
					if (propriete.getnode() != null) {
						for (ShaclBox pattern_other_nodeshape : Shaclvalue) {
							if (propriete.getnode().contains(pattern_other_nodeshape.getNameshape())) {
								pattern_node_nodeshape = pattern_other_nodeshape.getShpatternNodeShape();
								break;
							}
						}
					}
					//
					PropertyShapeDocumentation proprieteDoc = new PropertyShapeDocumentation();
					//
					proprieteDoc.setOutput_propriete(propriete.getname(), null);
					proprieteDoc.setOutput_uri(propriete.getpath());
					proprieteDoc.setOutput_valeur_attendus(propriete.getclass(), propriete.getnode(),
							propriete.getclass_property(), propriete.getdatatype(), propriete.getnodeKind(),
							propriete.getpath());
					proprieteDoc.setOutput_patterns(propriete.getpattern(), datanodeshape.getShpatternNodeShape(),
							pattern_node_nodeshape, propriete.getclass(), propriete.getnode(),
							propriete.getclass_property(), propriete.getdatatype(), propriete.getnodeKind(),
							propriete.getpath());
					proprieteDoc.setOutput_Cardinalite(propriete.getcardinality());
					proprieteDoc.setOutput_description(propriete.getdescription(), datanodeshape.getRdfsComment());
					proprieteDoc.setOutput_shin(propriete.getShin());
					proprieteDoc.setOutput_shvalue(propriete.getShValue());

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
