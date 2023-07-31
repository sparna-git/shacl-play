package fr.sparna.rdf.shacl.excel;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.XSD;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.excel.model.ColumnsHeader_Input;
import fr.sparna.rdf.shacl.excel.model.InputDataset;

public class InputDataReader {

	public List<InputDataset> read(Model shaclGraphTemplate, Model dataGraph) {
		
		
		List<Resource> nodeShapes = dataGraph.listResourcesWithProperty(SH.targetClass)
				.andThen(dataGraph.listResourcesWithProperty(SH.targetObjectsOf))
				.andThen(dataGraph.listResourcesWithProperty(SH.targetSubjectsOf))
				.toList();	
		
		
		// read everything typed as NodeShape
		List<InputDataset> Shapes_data = new ArrayList<>();
		for (Resource nodeShape : nodeShapes) {
			
			InputDataset shape = new InputDataset(nodeShape);

			List<Statement> classes = this.readClasses(nodeShape);
			if (classes.size() > 0) {
				shape.setClassesXSL(classes);
				// get all columns
				shape.setCol_classes(this.getColumns(classes));
			}

			List<Statement> properties_section = this.readProperties(nodeShape);
			if (properties_section.size() > 0) {
				shape.setPropertyXSL(properties_section);
				shape.setCol_properties(this.getColumns(properties_section));
			}
			Shapes_data.add(shape);
		}

		// Convert the result in map

		return Shapes_data;
	}

	public List<Statement> readClasses(Resource ns) {
		return ns.listProperties().toList();
	}

	
	public List<Statement> readProperties(Resource nodeShape) {
		return nodeShape.listProperties(SH.property).toList();

	}
	
	public List<ColumnsHeader_Input> getColumns(List<Statement> data) {

		List<ColumnsHeader_Input> columnsdata = new ArrayList<>();
		for (Statement statement : data) {
			ColumnsHeader_Input colData = new ColumnsHeader_Input();

			final String headerParameters = computeHeaderParametersForStatement(statement);

			boolean truevalue = columnsdata.stream()
					.filter(s -> s.getColumn_name().equals(statement.getPredicate().toString())
							&& s.getColumn_datatypeValue().equals(headerParameters))
					.findFirst().isPresent();

			if (!truevalue) {
				colData.setColumn_name(statement.getModel().shortForm(statement.getPredicate().getURI()));
				colData.setColumn_datatypeValue(headerParameters);
				columnsdata.add(colData);
			}
		}
		return columnsdata;
	}

	public static String computeHeaderParametersForStatement(Statement statement) {
		final String headerParameters;

		if (statement.getObject().isLiteral()) {
			if (!statement.getObject().asLiteral().getLanguage().isEmpty()) {
				headerParameters = "@" + statement.getObject().asLiteral().getLanguage();
			} else if (!statement.getObject().asLiteral().getDatatypeURI().equals(XSD.xstring.getURI())) {
				headerParameters = "^^"
						+ statement.getModel().shortForm(statement.getObject().asLiteral().getDatatypeURI());
			} else {
				headerParameters = "";
			}
		} else {
			headerParameters = "";
		}

		return headerParameters;
	}

	public static String computeCellValueForStatement(Statement statement) {
		if (statement.getObject().isResource()) {
			return statement.getModel().shortForm(statement.getObject().toString());
		} else {
			return statement.getObject().asLiteral().getLexicalForm();
		}
	}

	public static List<ColumnsHeader_Input> getFilterColumnsHeader(List<ColumnsHeader_Input> data) {

		List<ColumnsHeader_Input> columnsdata = new ArrayList<>();
		List<ColumnsHeader_Input> Columns_Properties = new ArrayList<>();
		for (ColumnsHeader_Input column_properties : data) {

			ColumnsHeader_Input colData = new ColumnsHeader_Input();

			boolean truevalue = Columns_Properties.stream()
					.filter(s -> s.getColumn_name().equals(column_properties.getColumn_name())
							&& s.getColumn_datatypeValue().equals(column_properties.getColumn_datatypeValue()))
					.findFirst().isPresent();

			if (!truevalue) {
				colData.setColumn_name(column_properties.getColumn_name());
				colData.setColumn_datatypeValue(column_properties.getColumn_datatypeValue());
				Columns_Properties.add(colData);
			}
		}
		return columnsdata;
	}

}
