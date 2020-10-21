package fr.sparna.rdf.shacl.diagram;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

public class ShaclPlantUmlWriter {


	public String writeInPlantUml(Model shaclGraph) {

		List<Resource> nodeShapes = shaclGraph.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();

		ArrayList<PlantUmlBox> planumlvalue = new ArrayList<>();
		ArrayList<String> shaclnode = new ArrayList<>();
		for (Resource nodeShape : nodeShapes) {
			shaclnode.add(nodeShape.getLocalName());
			PlantUmlBox dbShacl = new PlantUmlBox(nodeShape);
			planumlvalue.add(dbShacl);

		} 

		String attribute = "";
		SourcePlantUml codeuml = new SourcePlantUml();
		List<String> sourceuml = new ArrayList<>();

		for (PlantUmlBox plantUmlBox : planumlvalue) {
			sourceuml.add("Class"+" "+"\""+plantUmlBox.getNameshape()+"\""+"\n");
			for (PlantUmlProperty plantUmlproperty : plantUmlBox.getProperties()) {

				codeuml.codeuml(plantUmlproperty,plantUmlBox.getNameshape());

				attribute = codeuml.getUml_shape()+" : "+codeuml.getUml_path()+" "+codeuml.getUml_datatype()+" "+codeuml.getUml_literal()+"\n";

				if (codeuml.getUml_nodekind()!="") {
					attribute = codeuml.getUml_nodekind();
				} 

				if (codeuml.getUml_pattern()!=null) {
					attribute = codeuml.getUml_pattern();
				} 

				if (codeuml.getUml_uniquelang()!=null) {
					attribute = codeuml.getUml_uniquelang();
				}

				if (codeuml.getUml_node() !=null) {
					attribute = codeuml.getUml_node();
				}

				if(codeuml.getUml_class_property() != null) {
					attribute = codeuml.getUml_class_property();

				}

				sourceuml.add(attribute);

			} 
		}

		String source = "@startuml\n";

		for (String code : sourceuml) {
			source += code;
		}       
		source += "@enduml\n";


		return source;
	}

}
