package fr.sparna.rdf.shacl.excel.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.excel.InputDataReader;

public class ModelStructureReader {

	public List<ModelStructure> read(List<NodeShapeTemplate> dataSourceTemplate, Model dataGraph){
		
		List<ModelStructure> dataModel = new ArrayList<>();
		
		// 
		for (NodeShapeTemplate dataTemplate : dataSourceTemplate) {
		
			ModelStructure modelStructure = new ModelStructure();
			
			// 1. Get Name for sheet xls
 			String nameSheet = dataTemplate.getNodeShape().getModel().shortForm(dataTemplate.getNodeShape().getURI()).replace(':', '_');
 			modelStructure.setNameSheet(nameSheet);
 			
 			
 			// 2. Get Columns
 			List<PropertyShapeTemplate> colsHeaderTemplate = columns_xls(dataTemplate.getShapesTemplate()); 			
 			
 			//3. data
 			/*
 			 * for each statement, the next step is for properties write in model
 			 */
 			List<Statement> dataList = new ArrayList<>();
 			for (Statement nsStatement : dataGraph.listStatements().toList()) {
				if (nsStatement.getObject().equals(dataTemplate.getSHTargetClass())
					||	
					nsStatement.getObject().equals(dataTemplate.getSHTargetSubjectsOf())
					) {
					dataList.add(nsStatement);
				}
			}
 			
 			// if SH.targetObjectsOf is config, get all properties. 
 			if (dataTemplate.getSHTargetObjectOf() != null) {
 				Property SHProperty = dataGraph.createProperty(dataTemplate.getSHTargetObjectOf().getURI());
 				List<Resource> Shape = dataGraph.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();
 				for (Resource nsObject : Shape) {
 					
 					List<Statement> propertyStatements = nsObject.listProperties(SHProperty).toList();
 					for (Statement dataProperty : propertyStatements) {
 						dataList.add(dataProperty);
					}
				}
 			} 			
			modelStructure.setDataStatement(dataList);
						
			List<ColumnsHeader_Input> columns_data_header = columns_in_data_Header(colsHeaderTemplate,dataList);
			List<ColumnsHeader_Input> columns_config = columnswithDatatype(colsHeaderTemplate,dataList);
			
			// 
			List<String[]> outputData = new ArrayList<>(); 
			if (dataTemplate.getSHTargetClass() != null) {
				outputData = readTargetClass(dataList,columns_config);
			} else if (dataTemplate.getSHTargetSubjectsOf() != null) {
				
			} else if (dataTemplate.getSHTargetObjectOf() != null) {
				outputData = readTargetObjectOf(dataList, columns_config);
			}
			
			modelStructure.setOutputData(outputData);
        	
			// update Columns
			
			List<PropertyShapeTemplate> columnsHeader = updateColumnsHeader(colsHeaderTemplate,columns_data_header);
			modelStructure.setColumns(columnsHeader);
			
			
			dataModel.add(modelStructure);
		}		
		return dataModel;
	}
	
	
	public static List<PropertyShapeTemplate> columns_xls(List<PropertyShapeTemplate> columns){
		
		List<PropertyShapeTemplate> colsHeader = new ArrayList<>();
		
		Integer nOrder = 1;
		PropertyShapeTemplate tmpColumns = new PropertyShapeTemplate();
		tmpColumns.setSh_name("URI");
		tmpColumns.setSh_description("URI of the class. This column can use prefixes declared above in the header");
		tmpColumns.setSh_path("URI");
		tmpColumns.setSh_order(nOrder++);
		colsHeader.add(tmpColumns);
		for (PropertyShapeTemplate cols : columns) {
			PropertyShapeTemplate tmpTemplate = new PropertyShapeTemplate();
			
			tmpTemplate.setSh_name(cols.getSh_name());
			tmpTemplate.setSh_description(cols.getSh_description());
			tmpTemplate.setSh_path(cols.getSh_path());
			tmpTemplate.setSh_order(nOrder++);
			colsHeader.add(tmpTemplate);
		}
			
			/*
			if (ns.getSHTargetClass() != null) {
				PropertyShapeTemplate tmpTemplate = new PropertyShapeTemplate();
				tmpTemplate.setSh_name("targetCass");
				tmpTemplate.setSh_description("Set of entities validated by this Shape");
				tmpTemplate.setSh_path("sh:targetClass");
				tmpTemplate.setSh_order(nOrder++);
	 			colsHeader.add(tmpTemplate);
			}
			
			if (ns.getSHTargetSubjectsOf() != null) {
				PropertyShapeTemplate tmpTemplate = new PropertyShapeTemplate();
				tmpTemplate.setSh_name("TargetSubjectsOf");
				tmpTemplate.setSh_description("Set of entities validated by this Shape");
				tmpTemplate.setSh_path("sh:targetSubjectsOf");
				tmpTemplate.setSh_order(nOrder++);
	 			colsHeader.add(tmpColumns);
			}
			
			if (ns.getSHTargetSubjectsOf() != null) {
				PropertyShapeTemplate tmpTemplate = new PropertyShapeTemplate();
				tmpTemplate.setSh_name("TargetObjectOf()");
				tmpTemplate.setSh_description("Set of entities validated by this Shape");
				tmpTemplate.setSh_path("sh:targetObjectsOf");
				tmpTemplate.setSh_order(nOrder++);
	 			colsHeader.add(tmpColumns);
			}
			*/
			return colsHeader;
	
	}
	
	public static List<ColumnsHeader_Input> columns_in_data_Header(List<PropertyShapeTemplate> colsHeaderTemplate,List<Statement> statement){
		
		List<ColumnsHeader_Input> list_of_columns = new ArrayList<>();
		for (Statement sts : statement) {
				// fin the properties
				List<Statement> pred_data = sts.getSubject().asResource().listProperties().toList();
				for (PropertyShapeTemplate pred : colsHeaderTemplate) {
					for (Statement sts_pred : pred_data) {
						String node = sts_pred.getModel().shortForm(sts_pred.getPredicate().getURI());
						if (pred.getSh_path().equals(node)){
							
							String dataType = InputDataReader.computeHeaderParametersForStatement(sts_pred);
							boolean validate = list_of_columns
									.stream()
									.filter(v -> v.getColumn_name().equals(node)
											 &&
											 v.getColumn_datatypeValue().equals(dataType)
										)
								.findFirst()
								.isPresent();
							
							if (!validate) {
								ColumnsHeader_Input colData = new ColumnsHeader_Input();
	 							colData.setColumn_name(node);
	 							colData.setColumn_datatypeValue(dataType);
	 							list_of_columns.add(colData);
							}	
						}
				}
				
			}
		}
		
		return list_of_columns;
	}
	
	public static List<ColumnsHeader_Input> columnswithDatatype(List<PropertyShapeTemplate> colsHeaderTemplate,List<Statement> statement){
		
		List<ColumnsHeader_Input> list_of_columns = new ArrayList<>();
		
		for (PropertyShapeTemplate colTemplate : colsHeaderTemplate) {
			
			if (colTemplate.getSh_path().equals("URI")) {
				ColumnsHeader_Input colData = new ColumnsHeader_Input();
				colData.setColumn_name("URI");
				colData.setColumn_datatypeValue("");
				list_of_columns.add(colData);
			} else {
				ColumnsHeader_Input colData = new ColumnsHeader_Input();
				colData.setColumn_name(colTemplate.getSh_path());
				
				String dataType = "";
				for (Statement sts : statement) {
					// fin the properties
					List<Statement> pred_data = (sts.getObject().asResource().listProperties().toList().isEmpty()) ? 
												sts.getSubject().asResource().listProperties().toList() :
													sts.getObject().asResource().listProperties().toList();
					for (Statement sts_pred : pred_data) {
						String node = sts_pred.getModel().shortForm(sts_pred.getPredicate().getURI());
						if (colTemplate.getSh_path().equals(node)){
							dataType = InputDataReader.computeHeaderParametersForStatement(sts_pred);
						}
					}
				}
				
				colData.setColumn_datatypeValue(dataType);
				list_of_columns.add(colData);
			}
			
		}
		return list_of_columns;
	}
	

	
	public static List<PropertyShapeTemplate> updateColumnsHeader(List<PropertyShapeTemplate> columnsHeaderTemplate, List<ColumnsHeader_Input> columnsHeaderData) {
		
		List<PropertyShapeTemplate> columns = new ArrayList<>();
		
		for (PropertyShapeTemplate colHeaderTemplate : columnsHeaderTemplate) {
			for (ColumnsHeader_Input colHeaderData : columnsHeaderData) {
				if(colHeaderTemplate.getSh_path().equals(colHeaderData.getColumn_name())
					&&
					colHeaderData.getColumn_datatypeValue() != null
						) {
					
					List<ColumnsHeader_Input> nNameCol = columnsHeaderData
							.stream()
							.filter(p -> p.getColumn_name().equals(colHeaderData.getColumn_name()))
							.collect(Collectors.toList());
							
					
					if (nNameCol.size() < 2) {
						// Update the column with datatype.
						colHeaderTemplate.setSh_path(colHeaderData.getColumn_name()+colHeaderData.getColumn_datatypeValue());
					}
				}
			}
		}
		return columnsHeaderTemplate;
	}
	
	
	public static List<String[]> readTargetClass(List<Statement> data, List<ColumnsHeader_Input> colsHeaderTemplate) {
		List<String[]> arrNode = new ArrayList<>();
		
		for (Statement ns_output : data) {
			String[] arrColumn = new String[colsHeaderTemplate.size()];
    		if (ns_output.getPredicate().equals(RDF.type)) {
    			for (int i = 0; i < data.size(); i++) {    				
					String path_name = colsHeaderTemplate.get(i).getColumn_name().toString(); 
					if (path_name.equals("URI")) {
						arrColumn[i] = ns_output.getModel().shortForm(ns_output.getSubject().getURI());
						break;
					}
				}
    		}
    		
    		List<Statement> listProperties = ns_output.getSubject().listProperties().toList()
    					.stream()
    					.filter(f -> !f.getPredicate().equals(SH.property))
    					.collect(Collectors.toList());
    		for (int j = 0; j < colsHeaderTemplate.size(); j++) {
    			
    			String column_name = colsHeaderTemplate.get(j).getColumn_name();
    			String column_datatype = colsHeaderTemplate.get(j).getColumn_datatypeValue();
    			String value = "";
    			if (!column_name.equals("URI")) {
    				value = columnHeaderValue(column_name,column_datatype,listProperties);
    				if (!value.isEmpty()) {
        				arrColumn[j] = value;
        			}else {
        				arrColumn[j] = "";
        			}
    			}
			}
    		arrNode.add(arrColumn);
		}
		return arrNode;
	}	
	
	
	public static List<String[]> readTargetObjectOf(List<Statement> data, List<ColumnsHeader_Input> colsHeaderTemplate) {
		
		List<String[]> cols = new ArrayList<>();
		
		for (Statement statement : data) {		
			String[] arrColumn = new String[colsHeaderTemplate.size()];
			
			String header = statement.getModel().shortForm(statement.getObject().toString())+InputDataReader.computeHeaderParametersForStatement(statement);
    		if (statement.getPredicate().equals(SH.property)) {
    			for (int i = 0; i < colsHeaderTemplate.size(); i++) {    				
					String path_name = colsHeaderTemplate.get(i).getColumn_name();
					if (path_name.equals("URI")) {
						arrColumn[i] = header;
						break;
					}
				}
    		}		
    		
    		List<Statement> listProperties = statement.getObject().asResource().listProperties().toList();
    		for (int i = 0; i < colsHeaderTemplate.size(); i++) {
    			
    			
    			String column_name = colsHeaderTemplate.get(i).getColumn_name();
    			String column_datatype = colsHeaderTemplate.get(i).getColumn_datatypeValue();
    			String value = "";
    			if (column_name.equals("URI")) {
    				value = header;
    			} else if (column_name.equals("^property")) {
    				value = statement.getModel().shortForm(statement.getSubject().getURI());
    			}
    			else {
    				
    				value = columnHeaderValue(column_name,column_datatype,listProperties);
    			}
    			 
    			if (!value.isEmpty()){
    				arrColumn[i] = value;    				
    			} else {
    				arrColumn[i] = value;
    				
    			}
			}
    		cols.add(arrColumn);
    	}
		return cols;
	}
	
		
	public static String columnHeaderValue(String column_name,String column_datatype,List<Statement> ListPropertiesShape) {
		
		String value = "";
		for (Statement lprop : ListPropertiesShape) {
			String header_col = "";
			
			header_col = lprop.getModel().shortForm(lprop.getPredicate().getURI()); //+InputDataReader.computeHeaderParametersForStatement(lprop);
			
			if (column_name.equals(header_col)) {
				if (lprop.getPredicate().equals(SH.or)) {
					value = shOr(lprop.getSubject().asResource());					
				} else {
					String datatype = InputDataReader.computeHeaderParametersForStatement(lprop);
					if (column_datatype.equals(datatype)) {
						value = InputDataReader.computeCellValueForStatement(lprop);
					} else {
						value = "\""+InputDataReader.computeCellValueForStatement(lprop)+"\""+InputDataReader.computeHeaderParametersForStatement(lprop);
					}
					
				}
				
				break;
			}
		}
		return value;
	}
		
	public static String shOr (Resource constraint) {
		
		String valueOutput = "";
		Resource theOr = constraint.getProperty(SH.or).getResource();
		// now read all sh:node or sh:class inside
		List<RDFNode> rdfList = theOr.as( RDFList.class ).asJavaList();
		for (RDFNode node : rdfList) {
			if(node.canAs(Resource.class)) {
				Resource value = null;
				if (node.asResource().hasProperty(SH.node)) {
					value = node.asResource().getProperty(SH.node).getResource();
				} else if (node.asResource().hasProperty(SH.class_)) {
					value = node.asResource().getProperty(SH.class_).getResource();
				}
				
				if(value != null) {
					String output = "<"+value.getURI()+">";
					valueOutput += output+" ";
				}
			}	
		}
		
		if (valueOutput.length() > 0) {
			String fmt = "("+valueOutput+")";
			valueOutput = fmt;
		}
		
		return valueOutput;
	}
	
}
