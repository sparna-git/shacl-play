package fr.sparna.rdf.shacl.excel; 

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.excel.model.ColumnsHeader_Input;
import fr.sparna.rdf.shacl.excel.model.InputValues;
import fr.sparna.rdf.shacl.excel.model.ShaclOntology;
import fr.sparna.rdf.shacl.excel.model.InputDataset;
import fr.sparna.rdf.shacl.excel.model.TemplateSparnatural;
import fr.sparna.rdf.shacl.excel.model.XslTemplate;

public class Generator {

	public List<InputDataset> readDocument(Model shaclGraphTemplate, Model shaclGraph) throws IOException {

		// read graph for the building the recovery all the head columns
		List<Resource> nodeShapeTemplate = shaclGraphTemplate.listResourcesWithProperty(RDF.type, SH.NodeShape)
				.toList();

		// also read everything object of an sh:node or sh:qualifiedValueShape, that
		// maybe does not have an explicit rdf:type sh:NodeShape
		List<RDFNode> nodesAndQualifedValueShapesValues = shaclGraphTemplate.listStatements(null, SH.node, (RDFNode) null)
				.andThen(shaclGraphTemplate.listStatements(null, SH.qualifiedValueShape, (RDFNode) null)).toList().stream()
				.map(s -> s.getObject()).collect(Collectors.toList());

		// add those to our list
		for (RDFNode n : nodesAndQualifedValueShapesValues) {
			if (n.isResource() && !nodeShapeTemplate.contains(n)) {
				nodeShapeTemplate.add(n.asResource());
			}
		}
		
		// Template		
		List<TemplateSparnatural> wTemplate = new ArrayList<>();		
		OutputTemplateReader shaclReadColumns = new OutputTemplateReader();
		for (Resource ns : nodeShapeTemplate) {
			TemplateSparnatural tmpSparnatural = new TemplateSparnatural(ns);
			
			if (ns.hasProperty(SH.order)) {
				tmpSparnatural.setSHOrder(ns.getProperty(SH.order).getInt());
			}
			
			List<XslTemplate> ColumnTemplate = new ArrayList<>();			
			List<Statement> TemplateProperty = ns.listProperties(SH.property).toList();
			for (Statement lproperty : TemplateProperty) {
				RDFNode rdfNode = lproperty.getObject();
				Resource res = rdfNode.asResource();
				ColumnTemplate.add(shaclReadColumns.read(res));
			}
			
			if (ColumnTemplate.size() > 0) {
				tmpSparnatural.setShapesTemplate(ColumnTemplate);
			}
			wTemplate.add(tmpSparnatural);
		}
		
	
		
		/*
		 * 
		 * Read all Inputs
		 * 
		 */
		List<InputDataset> Input_DataSet = new ArrayList<>();
		InputDataReader read_model_data = new InputDataReader(); 
		Input_DataSet.addAll(read_model_data.read(shaclGraph));
		
		
		/*
		 * Get all columns classes 
		 * 
		 */		
		List<ColumnsHeader_Input> all_columns_classes = new ArrayList<>();
		Input_DataSet.stream().forEach(c -> all_columns_classes.addAll(c.getCol_classes()));
		// Filter the list of columns
		Output_ColumnsHeader ColumnsHeaderClasses = new Output_ColumnsHeader();
		all_columns_classes.addAll(ColumnsHeaderClasses.readData(all_columns_classes));
		
		/*
		 * 
		 * Read all columns properties
		 * 
		 */
		Output_ColumnsHeader ColumnsHeaderProperties = new Output_ColumnsHeader();
		List<ColumnsHeader_Input> all_columns_properties = new ArrayList<>(); 
		Input_DataSet.stream().forEach(c -> all_columns_properties.addAll(c.getCol_properties()));
		all_columns_properties.addAll(ColumnsHeaderProperties.readData(all_columns_properties));
		
		// ************ Section of Conversion
		
		// Get a Shape for each type of statement (Classes and Properties) 
		TemplateSparnatural SheetClasses = wTemplate.stream().filter(f -> f.getSHOrder()==1).findFirst().get();
		TemplateSparnatural SheetProperties = wTemplate.stream().filter(f -> f.getSHOrder()==2).findFirst().get();
		
		// Get the columns for each Shape type
		List<XslTemplate> col_classes = SheetClasses.getShapesTemplate();
		List<XslTemplate> col_properties = SheetClasses.getShapesTemplate();	
		
		
		//
		List<ColumnsHeader_Input> Columns_Classes = new ArrayList<>();
		for (ColumnsHeader_Input column_class : all_columns_classes) {
			
			ColumnsHeader_Input colData = new ColumnsHeader_Input();
			
			boolean truevalue = Columns_Classes
					.stream()
					.filter(
							s -> s.getColumn_name().equals(column_class.getColumn_name())
								 &&
								 s.getColumn_datatypeValue().equals(column_class.getColumn_datatypeValue())
							)
					.findFirst()
					.isPresent();
			
			if (!truevalue) {
				colData.setColumn_name(column_class.getColumn_name());
				colData.setColumn_datatypeValue(column_class.getColumn_datatypeValue());
				Columns_Classes.add(colData);
			}			
		}
		
		ColumnsHeader cc = new ColumnsHeader();
		List<XslTemplate> columnsHeaderClasses = cc.build(col_classes,Columns_Classes);
		
		int nCols = columnsHeaderClasses.size();
		int nRows = Input_DataSet.size();
		String[][] tData = new String[nRows][nCols];
		int nCol = 0;
		int nRow = 0;
		for (InputDataset shapes : Input_DataSet) {
			
			// firs the store URI values
			tData[nRow][nCol] = shapes.getNodeShape().getModel().shortForm(shapes.getNodeShape().getURI());
			
			for (InputValues dataValues : shapes.getClassesXSL()) {
				String pred = dataValues.getDatatype() != null || dataValues.getDatatype() != "" ? dataValues.getPredicate()+dataValues.getDatatype():dataValues.getPredicate();
				int idxCol = 0;
				for (int i = 0; i < columnsHeaderClasses.size(); i++) {
					String path_name =columnsHeaderClasses.get(i).getSh_path().toString(); 
					if (path_name.equals(pred)) {
						idxCol = i;
						break;
					}
				}				
				tData[nRow][idxCol] = dataValues.getObject();				
			}
			nRow++;
		}		
		
		// group by all name of columns		
		List<ColumnsHeader_Input> Columns_Properties = new ArrayList<>();
		for (ColumnsHeader_Input column_properties : all_columns_properties) {
			
			ColumnsHeader_Input colData = new ColumnsHeader_Input();
			
			boolean truevalue = Columns_Properties
					.stream()
					.filter(
							s -> s.getColumn_name().equals(column_properties.getColumn_name())
								 &&
								 s.getColumn_datatypeValue().equals(column_properties.getColumn_datatypeValue())
							)
					.findFirst()
					.isPresent();
			
			if (!truevalue) {
				colData.setColumn_name(column_properties.getColumn_name());
				colData.setColumn_datatypeValue(column_properties.getColumn_datatypeValue());
				Columns_Properties.add(colData);
			}			
		}
		// 
		ColumnsHeader cp = new ColumnsHeader();
		List<XslTemplate> columnsHeaderProperties = cp.build(col_properties,Columns_Properties);
		
		int nCols_prop = columnsHeaderProperties.size();
		int nRowsProp = all_columns_properties.size();
		String[][] tDataProperties = new String[nRowsProp][nCols_prop];
		int nCol_prop = 0;
		int nRow_prop = 0;
		for (InputDataset shapes : Input_DataSet) {			
			for (InputValues dataValues : shapes.getPropertyXSL()) {
				String pred = "";
				if (dataValues.getPredicate().toString().equals("sh:path")) {
					pred = "URI";
				}else {
					pred = dataValues.getDatatype() != null || dataValues.getDatatype() != "" ? dataValues.getPredicate()+dataValues.getDatatype():dataValues.getPredicate();
				}			
				
				int idxCol = 0;
				for (int i = 0; i < columnsHeaderProperties.size(); i++) {
					String path_name =columnsHeaderProperties.get(i).getSh_path().toString(); 
					if (path_name.equals(pred)) {
						idxCol = i;
						break;
					}
				}
				tDataProperties[nRow_prop][idxCol] = dataValues.getObject();
			}
			nRow_prop++;
		}
		
		// ********** Write excel file
		//String NameFile = shaclGraph.get 
		
		
		// Blank workbook
		XSSFWorkbook workbookShacl = new XSSFWorkbook();
		
		// Style color in row
		XSSFColor rowColor = new XSSFColor(new java.awt.Color(43,150,150), null);
		XSSFCellStyle rowStyle = workbookShacl.createCellStyle();
		rowStyle.setFillBackgroundColor(rowColor);
		rowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		// Create a blank sheet for prefixes
		XSSFSheet sheetPrefix = workbookShacl.createSheet("prefixes");
		
		// Create Row
		XSSFRow row;

		// Write Prefixes
		int rowid = 0;
		for (Map.Entry<String, String> onePrefix : shaclGraph.getNsPrefixMap().entrySet()) {
			row = sheetPrefix.createRow(rowid++);

			Cell cellP = row.createCell(0);
			Cell cellPrefix = row.createCell(1);
			Cell cellNameSpace = row.createCell(2);

			cellP.setCellValue("PREFIX");
			cellPrefix.setCellValue(onePrefix.getKey());
			cellNameSpace.setCellValue(onePrefix.getValue());			
		}
		
		
		// Get OWL Template
		List<Resource> ontology = shaclGraphTemplate.add(shaclGraph).listResourcesWithProperty(RDF.type, OWL.Ontology).toList();
		ShaclOntologyReader owlReader = new ShaclOntologyReader();
		List<ShaclOntology> owl = owlReader.readOWL(ontology);
		
		// Create a blank sheet for OWL and Classes
		XSSFSheet sheetClasses = workbookShacl.createSheet("classes");
		// Create Row
		XSSFRow rowClasses;		
		
		String uriShape = "";
		for (ShaclOntology owlonto : owl) {
			rowClasses = sheetClasses.createRow(0);

			if (uriShape != owlonto.getShapeUri()) {
				Cell cellURI = rowClasses.createCell(0);
				Cell cellValueURI = rowClasses.createCell(1);

				cellURI.setCellValue("Shapes URI");
				cellValueURI.setCellValue(owlonto.getShapeUri());
			}
		}

		
		int rowIdClass = 1;
		for (ShaclOntology owlonto : owl) {
			rowClasses = sheetClasses.createRow(rowIdClass++);

			Cell cellProperty = rowClasses.createCell(0);
			Cell cellValue = rowClasses.createCell(1);

			cellProperty.setCellValue(owlonto.getOwlProperty());
			cellValue.setCellValue(owlonto.getOwlValue());
		}

		// Template
		rowIdClass++;
		XSSFRow rowTemplateText;
		rowTemplateText = sheetClasses.createRow(rowIdClass++);
		Cell cellTemplate = rowTemplateText.createCell(0);
		cellTemplate.setCellValue("This sheet specifies the NodeShape with their targets, that is the sets of entities being validated");
		cellTemplate.setCellStyle(rowStyle);
		rowTemplateText.getCell(0).setCellStyle(rowStyle);
		
		
		// Header
		rowIdClass++;
		XSSFRow rowDescriptionClassColumn;
		XSSFRow rowNameClassColumn;
		XSSFRow rowShapeClassColumn;
		rowDescriptionClassColumn = sheetClasses.createRow(rowIdClass++);
		rowNameClassColumn = sheetClasses.createRow(rowIdClass++);
		rowShapeClassColumn = sheetClasses.createRow(rowIdClass++);
		
		// Columns Header - Classes 
		Integer nCell = 0;
		for (XslTemplate r : columnsHeaderClasses) {
			Cell cellDesc = rowDescriptionClassColumn.createCell(nCell);
			cellDesc.setCellValue(r.getSh_description());
			Cell cellName = rowNameClassColumn.createCell(nCell);
			cellName.setCellValue(r.getSh_name());
			Cell cellShape = rowShapeClassColumn.createCell(nCell);
			cellShape.setCellValue(r.getSh_path());
			nCell++;
		}
		
		//All dataSet
		XSSFRow rowDataSet;
		Integer nCellData = 0;
		for (int line = 0; line < tData.length; line++) {
			rowDataSet = sheetClasses.createRow(rowIdClass+(line+1));
			for (int col = 0; col < tData[line].length; col++) {
				Cell CellData = rowDataSet.createCell(col);
				CellData.setCellValue(tData[line][col]);
			}
		}	
		
		
		
		/*
		 * 
		 * 
		 */
		// Create a blank sheet for Property
		XSSFSheet sheetProperties = workbookShacl.createSheet("properties");
		// Create Row
		XSSFRow rowProperties;
		
		rowProperties = sheetProperties.createRow(0);		
		Integer nCellProperties = 0;
		// Columns Header - Properties
		Cell ontoCell = rowProperties.createCell(nCellProperties++);
		ontoCell.setCellValue("Ontology IRI:");
		Cell onto_value = rowProperties.createCell(nCellProperties++);
		onto_value.setCellValue("www.XXXXXX.fr");
		
		// Columns Header - Properties
		// Header
		Integer nRowProp = 2;
		nCellProperties++;
		XSSFRow rowDescriptionPropColumn;
		XSSFRow rowNamePropColumn;
		XSSFRow rowShapePropColumn;
		rowDescriptionPropColumn = sheetProperties.createRow(nRowProp++);
		rowNamePropColumn = sheetProperties.createRow(nRowProp++);
		rowShapePropColumn = sheetProperties.createRow(nRowProp++);
		Integer nCellProp = 0;
		for (XslTemplate r : columnsHeaderProperties) {
			Cell cellDesc = rowDescriptionPropColumn.createCell(nCellProp);
			cellDesc.setCellValue(r.getSh_description());
			Cell cellName = rowNamePropColumn.createCell(nCellProp);
			cellName.setCellValue(r.getSh_name());
			Cell cellShape = rowShapePropColumn.createCell(nCellProp);
			cellShape.setCellValue(r.getSh_path());
			nCellProp++;
		}
		
		
		
		//All dataSet
		nRowProp++;
		XSSFRow rowDataSetProp;
		Integer nCellDataProp = 0;
		for (int line = 0; line < tDataProperties.length; line++) {
			rowDataSetProp = sheetProperties.createRow(nRowProp+(line+1));
			for (int col = 0; col < tDataProperties[line].length; col++) {
				Cell CellData = rowDataSetProp.createCell(col);
				CellData.setCellValue(tDataProperties[line][col]);
			}
		}
		
		// This section is for classes all configurated
		File currDir = new File(".");
		String path = currDir.getAbsolutePath();
		String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";

		FileOutputStream outputStream = new FileOutputStream(fileLocation);
		workbookShacl.write(outputStream);
		workbookShacl.close();
		
		InputDataReader shaclRead = new InputDataReader();
		List<InputDataset> shClasses = new ArrayList<>();

		return shClasses;
	}
}