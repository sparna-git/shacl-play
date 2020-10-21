package fr.sparna.rdf.shacl.diagram;


import java.io.FileInputStream;
import java.util.List;
import java.util.ArrayList;

//import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.model.Model;   /* Creation et manipulation de RDF */
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.engine.Constraint;
import org.topbraid.shacl.vocabulary.SH;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
		// TODO Auto-generated method stub
		String shaclFile = args[0];
		Model shaclGraph = ModelFactory.createDefaultModel();
		
		shaclGraph.read(new FileInputStream(shaclFile), RDF.uri, FileUtils.guessLang(shaclFile, "RDF/XML"));
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
			
			if(codeuml.getUml_class() != null) {
				attribute = codeuml.getUml_class();
				sourceuml.add(attribute);
				attribute = codeuml.getUml_class_property();
				
			}
			
			sourceuml.add(attribute);
			
	     } 
       }
       
       OutFileUml outfile = new OutFileUml();
       String source = "@startuml\n";
       for (String code : sourceuml) {
    	   source += code;
       }
       source += "hide circle\n";
        source += "@enduml\n";
       
       outfile.outfileuml(source, "out_uml");
       
       
       OutFileSVGUml fileplantuml = new OutFileSVGUml();
       fileplantuml.outfilesvguml(source);
       
	}
}