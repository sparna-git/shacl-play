package fr.sparna.rdf.shacl.excel;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

public class ShapesReader {

	ConstraintValueReader cValue = new ConstraintValueReader();

	public List<Shapes> read(Model GraphData) {
		// read everything typed as NodeShape
		List<Resource> nodeShapes = GraphData.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();
		List<Shapes> Shapes_data = new ArrayList<>();
		for (Resource nodeShape : nodeShapes) {
			Shapes shape = new Shapes(nodeShape);
						
			List<ShapesValues> classes = this.readClasses(nodeShape);
			if (classes.size() > 0) {
				shape.setClassesXSL(classes);
				// get all columns
				shape.setCol_classes(this.getColumns(classes));
			}
			
			List<ShapesValues> properties_section = this.readProperties(nodeShape, Shapes_data);
			if (properties_section.size() > 0) {
				shape.setPropertyXSL(properties_section);
				shape.setCol_properties(this.getColumns(properties_section));
			}
			Shapes_data.add(shape);
		}
		return Shapes_data;
	}

	public List<ShapesValues> readClasses(Resource ns) {

		List<ShapesValues> lShapes = new ArrayList<>();

		List<Statement> spo = ns.listProperties().toList();
		for (Statement confSentence : spo) {

			String s = null;
			String p = null;
			String o = null;
			String dataType = "";

			String pred = confSentence.getModel().shortForm(confSentence.getPredicate().getURI().toString());
			if (!pred.equals("sh:property")) {
				if (confSentence.getObject().isResource()) {

					s = confSentence.getModel().shortForm(confSentence.getSubject().getURI().toString());
					p = confSentence.getModel().shortForm(confSentence.getPredicate().getURI().toString());
					o = confSentence.getModel().shortForm(confSentence.getObject().toString());

				} else if (confSentence.getObject().isLiteral()) {
					s = confSentence.getModel().shortForm(confSentence.getSubject().getURI().toString());
					p = confSentence.getModel().shortForm(confSentence.getPredicate().getURI().toString());
					o = confSentence.getModel().shortForm(confSentence.getObject().asLiteral().getLexicalForm());

					String data_type = confSentence.getObject().asLiteral().getDatatypeURI();
					String data_Language = confSentence.getObject().asLiteral().getLanguage();
					if (data_Language.isEmpty() && ((data_type.equals("http://www.w3.org/2001/XMLSchema#string"))
							|| (data_type.equals("http://www.w3.org/2001/XMLSchema#integer")))) {
						dataType = "^^"
								+ confSentence.getModel().shortForm(confSentence.getObject().asLiteral().getDatatypeURI());
					} else if (!data_Language.isEmpty()) {
						dataType = "@" + data_Language;
					}
				}
				ShapesValues spv = new ShapesValues();
				spv.setSubject(s);
				spv.setPredicate(p);
				spv.setObject(o);
				spv.setDatatype(dataType);

				lShapes.add(spv);
			}	
		}

		return lShapes;
	}

	public List<ShapesValues> readProperties(Resource nodeShape, List<Shapes> ShapesClasses) {

		List<ShapesValues> spProperties = new ArrayList<>();		
		List<Statement> propertyStatements = nodeShape.listProperties(SH.property).toList();
		for (Statement prop : propertyStatements) {			
			ShapesValues spValues = new ShapesValues();
			RDFNode obj = prop.getObject();
			
			if (obj.isResource()) {
				Resource object = obj.asResource();				
				List<Statement> listProperties = object.listProperties().toList();	
				List<ShapesValues> propList = new ArrayList<>();
				for (Statement propStatement : listProperties) {
					ShapesValues spValuesprop = new ShapesValues();
					
					spValuesprop.setSubject(propStatement.getModel().shortForm(propStatement.getSubject().getURI()));
					spValuesprop.setPredicate(propStatement.getModel().shortForm(propStatement.getPredicate().getURI()));
					spValuesprop.setObject(propStatement.getModel().shortForm(propStatement.getObject().toString()));
					
					String dataType = "";					
					if (propStatement.getObject().isLiteral()) {
						String data_type = propStatement.getObject().asLiteral().getDatatypeURI();
						String data_Language = propStatement.getObject().asLiteral().getLanguage();
						if (data_Language.isEmpty() && ((data_type.equals("http://www.w3.org/2001/XMLSchema#string"))
								|| (data_type.equals("http://www.w3.org/2001/XMLSchema#integer")))) {
							dataType = "^^" + propStatement.getModel().shortForm(propStatement.getObject().asLiteral().getDatatypeURI());
						} else if (!data_Language.isEmpty()) {
							dataType = "@" + data_Language;
						}
						spValuesprop.setDatatype(dataType);
					} else {
						spValuesprop.setDatatype("");
					}					
					propList.add(spValuesprop);
				}				
				spProperties.addAll(propList);
			}
		}			
		return spProperties;
	}
	
	public List<ColumnsData> getColumns(List<ShapesValues> data){
		
		List<ColumnsData> columnsdata = new ArrayList<>();
		for (ShapesValues val : data) {
			ColumnsData colData = new ColumnsData();
			
			boolean truevalue = columnsdata
					.stream()
					.filter(
							s -> s.getColumn_name().equals(val.getPredicate().toString())
								 &&
								 s.getColumn_datatypeValue().equals(val.getDatatype())
							)
					.findFirst()
					.isPresent();
			
			if (!truevalue) {
				colData.setColumn_name(val.getPredicate());
				colData.setColumn_datatypeValue(val.getDatatype());
				columnsdata.add(colData);
			}	
		}
		return columnsdata;
	}

	
}
