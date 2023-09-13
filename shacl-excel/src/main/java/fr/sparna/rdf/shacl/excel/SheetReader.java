package fr.sparna.rdf.shacl.excel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.excel.model.ColumnSpecification;
import fr.sparna.rdf.shacl.excel.model.NodeShape;
import fr.sparna.rdf.shacl.excel.model.PropertyShape;
import fr.sparna.rdf.shacl.excel.model.Sheet;

public class SheetReader {

	/**
	 * Reads the content of the sheets to be printed
	 */
	public List<Sheet> read(List<NodeShape> nodeShapes, Model dataGraph, String language){
		
		List<Sheet> sheets = new ArrayList<>();
		
		// sort node shapes
		nodeShapes.sort((a, b) -> {
			if (a.getSHOrder() != null) {
				if (b.getSHOrder() != null) {
					return a.getSHOrder().compareTo(b.getSHOrder());
				} else {
					return -1;
				}
			} else {
				if (b.getSHOrder() != null) {
					return 1;
				} else {
					return a.getNodeShape().getURI().compareTo(b.getNodeShape().getURI());
				}
			}
		});
		
		// then iterate on node shapes
		for (NodeShape aNodeShape : nodeShapes) {
		
			Sheet modelStructure = new Sheet(aNodeShape);
			
			// 1. get a name for the sheet			
			if(aNodeShape.getSHTargetClass() != null) {
				// if there is a targetClass, take it
				modelStructure.setName(aNodeShape.getSHTargetClass().getLocalName());
			} else {
				// otherwise use the NodeShape URI
				modelStructure.setName(aNodeShape.getNodeShape().getLocalName());
			} 			
 			
 			// 2. resolve target
 			List<Resource> nodeShapeTarget = resolveTarget(aNodeShape, dataGraph); 			
						
			// 3. Build column specifications
 			List<ColumnSpecification> columnSpecifications = buildColumnSpecifications(
 					aNodeShape.getPropertyShapes(),
 					nodeShapeTarget,
 					language,
 					false
 			);
 			modelStructure.setColumns(columnSpecifications);
 			
			// 4. Generate values in the table	
			List<String[]> outputData = fillColumns(nodeShapeTarget,columnSpecifications);
			
			// 5. Sort the table, by default on the first column in the data, then by URI
			outputData.sort((String[] o1, String[] o2) -> {
				if(o1.length == 1) {
					return o1[0].compareToIgnoreCase(o2[0]);
				}
				
				int firstColumnCompare = o1[1].compareTo(o2[1]);
				if(firstColumnCompare == 0) {
					return o1[0].compareToIgnoreCase(o2[0]);
				} else {
					return firstColumnCompare;
				}
			});
			
			modelStructure.setOutputData(outputData);	
			
			// add to list of sheets
			sheets.add(modelStructure);
		}		
		return sheets;
	}
	
	/**
	 * @return a list of resources from the data graph corresponding to the target of the shape
	 */
	public static List<Resource> resolveTarget(NodeShape nodeShape, Model dataGraph) {
		List<Resource> targets = new ArrayList<Resource>();
		
		if(nodeShape.getSHTargetClass() != null) {
			// find all resources with this class
			targets.addAll(dataGraph.listResourcesWithProperty(RDF.type, nodeShape.getSHTargetClass()).toList());
		}
		
		if(nodeShape.getSHTargetObjectOf() != null) {
			// find all resources being objects of this property
			targets.addAll(dataGraph.listObjectsOfProperty(dataGraph.createProperty(nodeShape.getSHTargetObjectOf().getURI())).toList().stream().map(n -> n.asResource()).collect(Collectors.toList()));
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
			List<PropertyShape> propertyShapes,
			List<Resource> targets,
			String language,
			boolean addMissingColumns
	){
		List<ColumnSpecification> list_of_columns = new ArrayList<>();
		
		// 1. Add the URI column
		ColumnSpecification uriColumn = new ColumnSpecification("URI", "URI identifier", "URI of the entity. This column can use prefixes known in this spreadsheet");		
		list_of_columns.add(uriColumn);
		
		// sort property shapes
		propertyShapes.sort((a,b) -> {
			if (b.getOrder() != null) {
				if (a.getOrder() != null) {
					return a.getOrder().compareTo(b.getOrder());
				} else {
					return -1;								
				}
			} else {
				if (a.getOrder() == null) {
					return 1;
				} else {
					return a.getName(language).compareTo(b.getName(language));
				}
			}
		});
		
		// 2. Build columns from each property shapes
		for (PropertyShape pShape : propertyShapes) {
			
			// find a language
			List<String> LanguageConf = readShLanguageIn(pShape);
			if (LanguageConf.size() > 0) {
				for (String dataLanguage : LanguageConf) {
					list_of_columns.add(new ColumnSpecification(pShape, language,dataLanguage));
				}
			} else {
				list_of_columns.add(new ColumnSpecification(pShape, language,null));
			}
			
		}
		
		// 3. add new columns if necessary
		if(addMissingColumns) {
			for (Resource r : targets) {
				// find the properties
				List<Statement> pred_data = r.listProperties().toList();
				
				for (Statement sts_pred : pred_data) {
					ColumnSpecification colSpec = new ColumnSpecification(sts_pred);
					
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
    					statements = aTarget.getModel().listStatements(
    							aTarget,
    							aTarget.getModel().createProperty(aColumnSpec.getPropertyUri()),
    							(RDFNode)null
    					)
    					.filterKeep(buildStatementPredicate(aColumnSpec)).toList();
    				} else {
    					statements = aTarget.getModel().listStatements(
    							null,
    							aTarget.getModel().createProperty(aColumnSpec.getPropertyUri()),
    							aTarget
    					)
        				.filterKeep(buildStatementPredicate(aColumnSpec)).toList();
    				}    				
    				
    				// 2. print them
    				arrColumn[i] = CellValues.statementsToCellValue(aColumnSpec, statements);
    			}
				
			}
    		arrNode.add(arrColumn);
		}
		
		// sort the rows by the "URI" column
		int uriColumnIndex = columnSpecifications.indexOf(columnSpecifications.stream().filter(cs -> cs.getHeaderString().equals("URI")).findAny().get());
		arrNode.sort((lineA, lineB) -> {
			return lineA[uriColumnIndex].compareToIgnoreCase(lineB[uriColumnIndex]);
		});
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
	

	public static List<String> readShLanguageIn(PropertyShape r) {
		
		
		List<String> l_lang = new ArrayList<>();
		if (r.getPropertyShape().hasProperty(SH.languageIn)) {
			Resource list = r.getPropertyShape().getProperty(SH.languageIn).getList().asResource();		
		    RDFList rdfList = list.as(RDFList.class);
		    ExtendedIterator<RDFNode> items = rdfList.iterator();
		    while ( items.hasNext() ) {
		    	RDFNode item = items.next();
		    	l_lang.add(item.toString());
		    }
		    
		}
		return l_lang;
	}

}
