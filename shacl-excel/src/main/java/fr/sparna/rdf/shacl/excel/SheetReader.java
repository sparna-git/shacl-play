package fr.sparna.rdf.shacl.excel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

import fr.sparna.rdf.shacl.excel.model.ColumnSpecification;
import fr.sparna.rdf.shacl.excel.model.NodeShapeTemplate;
import fr.sparna.rdf.shacl.excel.model.PropertyShapeTemplate;
import fr.sparna.rdf.shacl.excel.model.Sheet;

public class SheetReader {

	/**
	 * Reads the content of the sheets to be printed
	 */
	public List<Sheet> read(List<NodeShapeTemplate> dataSourceTemplate, Model dataGraph){
		
		List<Sheet> sheets = new ArrayList<>();
		
		// 
		for (NodeShapeTemplate dataTemplate : dataSourceTemplate) {
		
			Sheet modelStructure = new Sheet();
 			// keep original Model in the data structure, just in case
			modelStructure.setTemplateModel(dataGraph);
			
			// 1. Get Name for sheet xls
 			String nameSheet = dataTemplate.getNodeShape().getModel().shortForm(dataTemplate.getNodeShape().getURI()).replace(':', '_');
 			modelStructure.setNameSheet(nameSheet);
 			
 			// 2. resolve target
 			List<Resource> nodeShapeTarget = resolveTarget(dataTemplate, dataGraph);
						
			// 3. Build column specifications
 			List<ColumnSpecification> columnSpecifications = buildColumnSpecifications(dataTemplate.getShapesTemplate(),nodeShapeTarget, false);
 			modelStructure.setColumns(columnSpecifications);
 			
			// 4. Fill table with values		
			List<String[]> outputData = fillColumns(nodeShapeTarget,columnSpecifications);		
			modelStructure.setOutputData(outputData);	
			
			// add to list of sheets
			sheets.add(modelStructure);
		}		
		return sheets;
	}
	
	/**
	 * @return a list of resources from the data graph corresponding to the target of the shape
	 */
	public static List<Resource> resolveTarget(NodeShapeTemplate nodeShape, Model dataGraph) {
		List<Resource> targets = new ArrayList<Resource>();
		
		if(nodeShape.getSHTargetClass() != null) {
			// find all resources with this class
			targets.addAll(dataGraph.listResourcesWithProperty(RDF.type, nodeShape.getSHTargetClass()).toList());
		}
		
		if(nodeShape.getSHTargetObjectOf() != null) {
			// find all resources being objects of this property
			targets.addAll(dataGraph.listObjectsOfProperty(dataGraph.createProperty(nodeShape.getSHTargetObjectOf().getURI())).toList().stream().map(n -> n.asResource()).collect(Collectors.toList()));
			System.out.println("TargetOfBjectOf "+targets.size());
		}
		
		if(nodeShape.getSHTargetSubjectsOf() != null) {
			// find all resources being objects of this property
			targets.addAll(dataGraph.listSubjectsWithProperty(dataGraph.createProperty(nodeShape.getSHTargetSubjectsOf().getURI())).toList().stream().map(n -> n.asResource()).collect(Collectors.toList()));
		}
		
		return targets;
	}

	/**
	 * @return the column specifications of the table to write
	 */
	public static List<ColumnSpecification> buildColumnSpecifications(
			List<PropertyShapeTemplate> colsHeaderTemplate,
			List<Resource> targets,
			boolean addMissingColumns
	){
		List<ColumnSpecification> list_of_columns = new ArrayList<>();
		
		// 1. Add the URI column
		ColumnSpecification uriColumn = new ColumnSpecification("URI", "URI identifier", "URI of the entity. This column can use prefixes known in this spreadsheet");		
		list_of_columns.add(uriColumn);
		
		// 2. Build columns from each property shapes
		for (PropertyShapeTemplate pShape : colsHeaderTemplate) {
			list_of_columns.add(new ColumnSpecification(pShape));
		}
		
		// 3. add new columns if necessary
		if(addMissingColumns) {
			for (Resource r : targets) {
				// find the properties
				List<Statement> pred_data = r.listProperties().toList();
				
				for (Statement sts_pred : pred_data) {
					ColumnSpecification colSpec = ComputeCell.computeColumnSpecificationForStatement(sts_pred);
					
					if(!list_of_columns.contains(colSpec)) {
						list_of_columns.add(colSpec);
					}
				}
			}
		}
		
		return list_of_columns;
	}

	/**
	 * @return the list of column values to print in the table. Each entry corresponds to one line, each line is an array
	 * with as many values as the columns in the table
	 */
	public static List<String[]> fillColumns(List<Resource> targets, List<ColumnSpecification> columnSpecifications) {
		List<String[]> arrNode = new ArrayList<>();
		
		for (Resource aTarget : targets) {
			String[] arrColumn = new String[columnSpecifications.size()];
    		
			
    		for (int i = 0; i < columnSpecifications.size(); i++) {
    			ColumnSpecification aColumnSpec = columnSpecifications.get(i);
    			
    			if(aColumnSpec.getHeaderString().equals("URI")) {
    				arrColumn[i] = aTarget.getModel().shortForm(aTarget.getURI());
    			} else {
    				// 1. find the statements corresponding to column
    				List<Statement> statements;
    				if(!aColumnSpec.isInverse()) {
    					statements = aTarget.listProperties(
        						aTarget.getModel().createProperty(aColumnSpec.getPropertyUri())
        				).filterKeep(buildStatementPredicate(aColumnSpec)).toList();
    				} else {
    					statements = aTarget.getModel().listStatements(null, aTarget.getModel().createProperty(aColumnSpec.getPropertyUri()), aTarget)
        				.filterKeep(buildStatementPredicate(aColumnSpec)).toList();
    				}    				
    				
    				// 2. print them
    				arrColumn[i] = statementsToCellValue(aColumnSpec, statements);
    			}
				
			}
    		arrNode.add(arrColumn);
		}
		return arrNode;
	}	
	
	/**
	 * @return a Predicate to select only the statements corresponding to the column
	 */
	public static Predicate<Statement> buildStatementPredicate(ColumnSpecification spec) {
		return s -> {
			return 
					s.getPredicate().getURI().equals(spec.getPropertyUri())
					&&
					(
							spec.getDatatypeUri() == null
							||
							(spec.getDatatypeUri().equals(XSD.xstring.getURI()))
							||
							(
									s.getObject().isLiteral()
									&&
									s.getObject().asLiteral().getDatatypeURI().equals(spec.getDatatypeUri())
							)
					)
					&&
					(
							spec.getLanguage() == null
							||
							(
									s.getObject().isLiteral()
									&&
									s.getObject().asLiteral().getLanguage().equals(spec.getLanguage())
							)
					)
					;
		};
	}
	
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
