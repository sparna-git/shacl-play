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
import fr.sparna.rdf.shacl.doc.ShaclBoxReader;
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

		List<Resource> nodeShapes = shaclGraph.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();

		// 1. Lire toutes les classes
		ArrayList<ShaclBox> Shaclvalue = new ArrayList<>();
		ShaclBoxReader reader = new ShaclBoxReader(lang);
		for (Resource nodeShape : nodeShapes) {			
			ShaclBox dbShacl = reader.read(nodeShape);
			Shaclvalue.add(dbShacl);
		}
		
		// sort node shapes
		Shaclvalue.sort((ShaclBox ns1, ShaclBox ns2) -> {
			if(ns1.getShOrder() != null) {
				if(ns2.getShOrder() != null) {
					return ns1.getShOrder() - ns2.getShOrder();
				} else {
					return -1;
				}
			} else {
				if(ns2.getShOrder() != null) {
					return 1;
				} else {
					// both sh:order are null, try with sh:name
					if(ns1.getNameshape() != null) {
						if(ns2.getNameshape() != null) {
							return ns1.getNameshape().compareTo(ns2.getNameshape());
						} else {
							return -1;
						}
					} else {
						if(ns2.getNameshape() != null) {
							return 1;
						} else {
							// both sh:name are null, try with URI
							return ns1.getNodeShape().toString().compareTo(ns2.getNodeShape().toString());
						}
					}
				}
			}
		});

		// 2. Lire les propriétés
		for (ShaclBox aBox : Shaclvalue) {
			aBox.setShacl_value(reader.readProperties(aBox.getNodeShape(), Shaclvalue));
		}

		// Lire les proprietes et récuperer les prefix a utilisé
		List<String> aPrefix = new ArrayList<>();
		for (ShaclBox aBox : Shaclvalue) {
			List<ShaclPrefix> prefixes = reader.readPropertiesPrefix(aBox.getNodeShape());
		    for(ShaclPrefix shprefix : prefixes) {
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
		List<NamespaceSections> namespaceSections = new ArrayList<>();
		HashSet<String> aPrefixsh = new HashSet(aPrefix);
		// Recuperation de prefix et Namespace
		Map<String, String> allPrefixes = shaclGraph.getNsPrefixMap();
		// Les prefix et namespace
		for (String aPrefixp : aPrefixsh) {
			for (Map.Entry<String, String> me : allPrefixes.entrySet()) {				
				if (me.getKey().equals(aPrefixp)) {		
					NamespaceSections tPrefix = new NamespaceSections();
					tPrefix.setOutput_prefix(me.getKey());
					tPrefix.setOutput_namespace(me.getValue());
					namespaceSections.add(tPrefix);
					break;
				}				
			}
		}
		
		
		
		
		
		
		//

		shapesDocumentation.setShnamespace(namespaceSections);
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
				for (ShaclProperty propriete : datanodeshape.getShacl_value()) {
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
