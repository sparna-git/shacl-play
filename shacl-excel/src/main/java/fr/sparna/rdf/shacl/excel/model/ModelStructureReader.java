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
 			
 			//2. data
 			List<Statement> dataList_Target = new ArrayList<>();
 			for (Statement nsStatement : dataGraph.listStatements().toList()) {
				if (nsStatement.getObject().equals(dataTemplate.getSHTargetClass())
					||	
					nsStatement.getObject().equals(dataTemplate.getSHTargetSubjectsOf())
					) {
					dataList_Target.add(nsStatement);
				}
			}
 			
 			// if SH.targetObjectsOf get all properties. 
 			if (dataTemplate.getSHTargetObjectOf() != null) {
 				Property SHProperty = dataGraph.createProperty(dataTemplate.getSHTargetObjectOf().getURI());
 				List<Resource> Shape = dataGraph.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();
 				for (Resource nsObject : Shape) {
 					
 					List<Statement> propertyStatements = nsObject.listProperties(SHProperty).toList();
 					for (Statement dataProperty : propertyStatements) {
 						dataList_Target.add(dataProperty);
					}
				}
 			} 			
			modelStructure.setDataStatement(dataList_Target);
						
			// 3. Get Columns
 			List<ShapeTemplateHeaderColumn> colsHeaderTemplate = columns_xls(dataTemplate.getShapesTemplate());
			List<ColumnsInputDatatype> columns_data_header = columns_in_data_Header(colsHeaderTemplate,dataList_Target);
			
			
			
			// 4. Output Value
			List<ColumnsInputDatatype> columns_config = columnswithDatatype(colsHeaderTemplate,dataList_Target);
			List<String[]> outputData = new ArrayList<>(); 
			if (dataTemplate.getSHTargetClass() != null) {
				outputData = readTargetClass(dataList_Target,columns_config);
			} else if (dataTemplate.getSHTargetSubjectsOf() != null) {
				
			} else if (dataTemplate.getSHTargetObjectOf() != null) {
				outputData = readTargetObjectOf(dataList_Target, columns_config);
			}			
			modelStructure.setOutputData(outputData);
        	
			
			// update Columns with datatype
			List<ShapeTemplateHeaderColumn> columnsHeader = updateColumnsHeader(colsHeaderTemplate,columns_data_header);
			modelStructure.setColumns(columnsHeader);	
			
			dataModel.add(modelStructure);
		}		
		return dataModel;
	}
	
	
	public static List<ShapeTemplateHeaderColumn> columns_xls(List<ShapeTemplateHeaderColumn> columns){
		
		List<ShapeTemplateHeaderColumn> colsHeader = new ArrayList<>();
		
		Integer nOrder = 1;
		ShapeTemplateHeaderColumn tmpColumns = new ShapeTemplateHeaderColumn();
		tmpColumns.setSh_name("URI");
		tmpColumns.setSh_description("URI of the class. This column can use prefixes declared above in the header");
		tmpColumns.setSh_path("URI");
		tmpColumns.setSh_order(nOrder++);
		colsHeader.add(tmpColumns);
		for (ShapeTemplateHeaderColumn cols : columns) {
			ShapeTemplateHeaderColumn tmpTemplate = new ShapeTemplateHeaderColumn();
			
			tmpTemplate.setSh_name(cols.getSh_name());
			tmpTemplate.setSh_description(cols.getSh_description());
			tmpTemplate.setSh_path(cols.getSh_path());
			tmpTemplate.setSh_order(nOrder++);
			colsHeader.add(tmpTemplate);
		}	
		return colsHeader;	
	}
	
	public static List<ColumnsInputDatatype> columns_in_data_Header(List<ShapeTemplateHeaderColumn> colsHeaderTemplate,List<Statement> statement){
		
		List<ColumnsInputDatatype> list_of_columns = new ArrayList<>();
		for (Statement sts : statement) {
				// fin the properties
				List<Statement> pred_data = sts.getSubject().asResource().listProperties().toList();
				for (ShapeTemplateHeaderColumn pred : colsHeaderTemplate) {
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
								ColumnsInputDatatype colData = new ColumnsInputDatatype();
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
	
	public static List<ColumnsInputDatatype> columnswithDatatype(List<ShapeTemplateHeaderColumn> colsHeaderTemplate,List<Statement> statement){
		
		List<ColumnsInputDatatype> list_of_columns = new ArrayList<>();
		
		for (ShapeTemplateHeaderColumn colTemplate : colsHeaderTemplate) {
			
			if (colTemplate.getSh_path().equals("URI")) {
				ColumnsInputDatatype colData = new ColumnsInputDatatype();
				colData.setColumn_name("URI");
				colData.setColumn_datatypeValue("");
				list_of_columns.add(colData);
			} else {
				ColumnsInputDatatype colData = new ColumnsInputDatatype();
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
	
	public static List<ShapeTemplateHeaderColumn> updateColumnsHeader(List<ShapeTemplateHeaderColumn> columnsHeaderTemplate, List<ColumnsInputDatatype> columnsHeaderData) {
		
		List<ShapeTemplateHeaderColumn> columns = new ArrayList<>();
		
		for (ShapeTemplateHeaderColumn colHeaderTemplate : columnsHeaderTemplate) {
			for (ColumnsInputDatatype colHeaderData : columnsHeaderData) {
				if(colHeaderTemplate.getSh_path().equals(colHeaderData.getColumn_name())
					&&
					colHeaderData.getColumn_datatypeValue() != null
						) {
					
					List<ColumnsInputDatatype> nNameCol = columnsHeaderData
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
	
	public static List<String[]> readTargetClass(List<Statement> data, List<ColumnsInputDatatype> colsHeaderTemplate) {
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
    				value = outputValue(column_name,column_datatype,listProperties);
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
	
	public static List<String[]> readTargetObjectOf(List<Statement> data, List<ColumnsInputDatatype> colsHeaderTemplate) {
		
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
    				
    				value = outputValue(column_name,column_datatype,listProperties);
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
	
	public static String outputValue(String column_name,String column_datatype,List<Statement> ListPropertiesShape) {
		
		String value = "";
		for (Statement lprop : ListPropertiesShape) {
			String header_col = "";
			
			header_col = lprop.getModel().shortForm(lprop.getPredicate().getURI());
			
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
