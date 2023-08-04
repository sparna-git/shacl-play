package fr.sparna.rdf.shacl.excel;

import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.XSD;

public class ComputeCell {


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

}
