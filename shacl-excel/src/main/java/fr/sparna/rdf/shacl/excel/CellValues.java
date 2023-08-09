package fr.sparna.rdf.shacl.excel;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.XSD;

import fr.sparna.rdf.shacl.excel.model.ColumnSpecification;

public class CellValues {
	
	public static String statementsToCellValue(ColumnSpecification columnSpec, List<Statement> statements) {
		return statements.stream().map(s -> toCellValue(s.getObject(), columnSpec)).collect(Collectors.joining(", "));
	}
	
	public static String toCellValue(RDFNode node, ColumnSpecification columnSpec) {
		if(node.isURIResource()) {
			return node.getModel().shortForm(node.asResource().getURI());
		} else if(node.canAs(RDFList.class)) {
			return(toCellValue(node.as(RDFList.class), columnSpec));
		} else if(node.isAnon()) {
			return toCellValueAnon(node.asResource(), columnSpec);
		} else if(node.isLiteral()) {
			return(toCellValue(node.asLiteral(), columnSpec));
		} else {
			System.out.println("Unknown value to print "+node.toString());
			return "";
		}
	}
	
	public static String toCellValue(Literal l, ColumnSpecification columnSpec) {
		if((l.getDatatypeURI() == null) || (columnSpec.getDatatypeUri() == null) || (l.getDatatypeURI().equals(columnSpec.getDatatypeUri()))) {
			return l.getLexicalForm();
		} else {
			return l.getLexicalForm()+"^^"+l.getModel().shortForm(l.getDatatypeURI());
		}
	}
	
	public static String toCellValueAnon(Resource r, ColumnSpecification columnSpec) {
		return "["+r.listProperties().toList().stream().map(s -> toCellValueAnon_statement(s, columnSpec)).collect(Collectors.joining("; "))+"]";
	}
	
	public static String toCellValueAnon_statement(Statement statementOnAnonymousResource, ColumnSpecification columnSpec) {
		return statementOnAnonymousResource.getModel().shortForm(statementOnAnonymousResource.getPredicate().getURI())+" "+toCellValue(statementOnAnonymousResource.getObject(), columnSpec);
	}
	
	public static String toCellValue(RDFList list, ColumnSpecification columnSpec) {
		return "("+list.asJavaList().stream().map(node -> toCellValue(node, columnSpec)).collect(Collectors.joining(" "))+")";
	}

}
