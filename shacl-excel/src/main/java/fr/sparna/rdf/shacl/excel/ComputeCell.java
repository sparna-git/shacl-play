package fr.sparna.rdf.shacl.excel;

import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.XSD;

import fr.sparna.rdf.shacl.excel.model.ColumnSpecification;

public class ComputeCell {
	
	public static ColumnSpecification computeColumnSpecificationForStatement(Statement statement) {
		final ColumnSpecification spec = new ColumnSpecification(statement.getPredicate().getURI());

		if (statement.getObject().isLiteral()) {
			if (!statement.getObject().asLiteral().getLanguage().isEmpty()) {
				spec.setLanguage(statement.getObject().asLiteral().getLanguage());
			} else if (!statement.getObject().asLiteral().getDatatypeURI().equals(XSD.xstring.getURI())) {
				spec.setDatatypeUri(statement.getObject().asLiteral().getDatatypeURI());
			} 
		}
		
		spec.recomputeHeaderString(statement.getModel());

		return spec;
	}
	
	public static String computeHeaderDatatypeForStatement(Statement statement) {
		String headerDatatype = "";

		if (statement.getObject().isLiteral()) {
			if (!statement.getObject().asLiteral().getDatatypeURI().equals(XSD.xstring.getURI())) {
				headerDatatype = statement.getObject().asLiteral().getDatatypeURI();
			}
		}

		return headerDatatype;
	}

	public static String computeCellValueForStatement(Statement statement) {
		if (statement.getObject().isResource()) {
			return statement.getModel().shortForm(statement.getObject().toString());
		} else {
			return statement.getObject().asLiteral().getLexicalForm();
		}
	}

}
