package fr.sparna.rdf.shacl.doc.read;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.doc.ShaclBox;
import fr.sparna.rdf.shacl.doc.ShaclProperty;
import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationSection;

public class ShapesDocumentationModelReader implements ShapesDocumentationReaderIfc {

	@Override
	public ShapesDocumentation readShapesDocumentation(InputStream input, String fileName) {

		Model shaclGraph = ModelFactory.createDefaultModel();
		shaclGraph.read(input, RDF.uri, FileUtils.guessLang(fileName, "RDF/XML"));
		ShapesDocumentation shapesDocumentation = new ShapesDocumentation();

		// HERE : READ Model and populate shapesDocumentation

		List<Resource> nodeShapes = shaclGraph.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();
		// 1. Lire toutes les classes
		ArrayList<ShaclBox> Shaclvalue = new ArrayList<>();
		for (Resource nodeShape : nodeShapes) {
			ShaclBox dbShacl = new ShaclBox(nodeShape);
			Shaclvalue.add(dbShacl);
		}

		// 2. Lire les propriétés
		for (ShaclBox aBox : Shaclvalue) {
			aBox.readProperties(aBox.getNodeShape(), Shaclvalue);			
		}
		
		shapesDocumentation.setTitle("Tableau d'information Technique");
		List<ShapesDocumentationSection> ListTitle = new ArrayList<>();
		String pattern_node_nodeshape = null;
		for(ShaclBox petition : Shaclvalue) {
			if(petition.getNametargetclass() != null) {
				ShapesDocumentationSection sectionPrincipal = new ShapesDocumentationSection();
				sectionPrincipal.setTitle(petition.getNameshape());
				sectionPrincipal.setPattern(petition.getShaclpatternNodeShape());
				sectionPrincipal.setComments(petition.getRdfsComment());
				List<PropertyShapeDocumentation> ListPropriete = new ArrayList<>();
				for(ShaclProperty propriete : petition.getProperties()) {
					// Récuperation du pattern si le node est une NodeShape 
					if (propriete.getnode() != null) {
						for (ShaclBox pattern_other_nodeshape : Shaclvalue) {
							if(propriete.getnode().contains(pattern_other_nodeshape.getNameshape())) {
								pattern_node_nodeshape = pattern_other_nodeshape.getShaclpatternNodeShape();
								break;
							}
						}
					}
					
					//
					PropertyShapeDocumentation proprieteDoc = new PropertyShapeDocumentation();
					//proprieteDoc.setOutput_propriete();
					proprieteDoc.setOutput_uri(propriete.getpath());
					proprieteDoc.setOutput_valeur_attendus(propriete.getclass(), 
														   propriete.getnode(), propriete.getclass_property(),
														   propriete.getdatatype(), propriete.getnodeKind(),propriete.getpath());
					proprieteDoc.setOutput_patterns(propriete.getpattern(),petition.getShaclpatternNodeShape(),pattern_node_nodeshape,
													propriete.getclass(), 
													propriete.getnode(), propriete.getclass_property(),
													propriete.getdatatype(), propriete.getnodeKind(),propriete.getpath());
					proprieteDoc.setOutput_Cardinalite(propriete.getcardinality());
					proprieteDoc.setOutput_description(propriete.getdescription(),petition.getRdfsComment());
					
					//section1.setPropertySections(Arrays.asList(new PropertyShapeDocumentation[] { propDoc1 }));
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
