package fr.sparna.rdf.shacl.excel; 

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

public class Generator {

	public List<Shapes> readDocument(Model shaclGraphTemplate, Model shaclGraph) throws IOException {

		// read graph for the building the recovery all the head columns
		List<Resource> nodeShapeTemplate = shaclGraphTemplate.listResourcesWithProperty(RDF.type, SH.NodeShape)
				.toList();

		// read everything typed as NodeShape
		List<Resource> nodeShapes = shaclGraph.listResourcesWithProperty(RDF.type, OWL.Class).toList();
		
		// also read everything object of an sh:node or sh:qualifiedValueShape, that
		// maybe does not have an explicit rdf:type sh:NodeShape
		List<RDFNode> nodesAndQualifedValueShapesValues = shaclGraph.listStatements(null, SH.node, (RDFNode) null)
				.andThen(shaclGraph.listStatements(null, SH.qualifiedValueShape, (RDFNode) null)).toList().stream()
				.map(s -> s.getObject()).collect(Collectors.toList());

		// add those to our list
		for (RDFNode n : nodesAndQualifedValueShapesValues) {
			if (n.isResource() && !nodeShapes.contains(n)) {
				nodeShapes.add(n.asResource());
			}
		}
		
		// Template
		
		List<Shapes> wTemplate = new ArrayList<>();
		XslTemplateReader shaclReadColumns = new XslTemplateReader();
		// Write Columns for Classes and Properties
		for (Resource ns : nodeShapeTemplate) {
			
			Shapes shClass = new Shapes(ns);
			
			if (ns.hasProperty(SH.order)) {
				shClass.setSHOrder(ns.getProperty(SH.order).getInt());
			}
			
			List<XslTemplate> col = new ArrayList<>();
			
			List<Statement> ShProperty = ns.listProperties(SH.property).toList();
			
			
			// Read all nodeShape OWL.Class			
			for (Statement lproperty : ShProperty) {
				RDFNode rdfNode = lproperty.getObject();
				Resource res = rdfNode.asResource();
				col.add(shaclReadColumns.read(res));
			}	
			
			if (col.size() > 0) {
				shClass.setShapesTemplate(col);
			}
			wTemplate.add(shClass);
		}
		
		/*
		 * 
		 * Read all Shape
		 * 
		 */
		List<Shapes> wDataSet = new ArrayList<>();
		for (Resource nsData : nodeShapes) {
			ShapesReader spRead = new ShapesReader();
			wDataSet.add(spRead.read(nsData, nodeShapes));
		}
		
		// Delete this logbook
		for (Shapes shapes : wDataSet) {
			for (ShapesValues shapes2 : shapes.getShapes()) {
				System.out.println(shapes2.getSubject().toString()+" - "+shapes2.getPredicate()+" - "+shapes2.getObject());
			}
		}
		
		/*
		 * Get all section for each shape and only return the columns for build the template xsl
		 * 
		 */
		List<ShapesValues> shValues = new ArrayList<>();
		wDataSet
			.stream()
			.forEach(s -> shValues.addAll(s.getShapes()));
		
		List<ColumnsData> columnsdata = new ArrayList<>();
		for (ShapesValues val : shValues) {
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
		
		// ************ Section of Conversion
		
		// Get OWL Template
		List<Resource> ontology = shaclGraphTemplate.add(shaclGraph).listResourcesWithProperty(RDF.type, OWL.Ontology).toList();
		ShaclOntologyReader owlReader = new ShaclOntologyReader();
		List<ShaclOntology> owl = owlReader.readOWL(ontology);

		// Get Prefix Template
		Prefixes pf = new Prefixes();
		List<NamespaceSection> NameSpacesectionPrefix = pf.prefixes(nodeShapes, shaclGraphTemplate.add(shaclGraph));		
		
		
		// Get a Shape for each type of statement (Classes and Properties) 
		Shapes SheetClasses = wTemplate.stream().filter(f -> f.getSHOrder()==1).findFirst().get();
		Shapes SheetProperties = wTemplate.stream().filter(f -> f.getSHOrder()==2).findFirst().get();
		
		// Get the columns for each Shape type
		List<XslTemplate> col_classes = SheetClasses.getShapesTemplate();
		List<XslTemplate> col_properties = SheetClasses.getShapesTemplate();		
		
		
		CellColumns cc = new CellColumns();
		List<XslTemplate> columnsHeader = cc.build(col_classes,columnsdata); //, shValuesClass);
		
		int nCols = columnsHeader.size();
		int nRows = wDataSet.size();
		String[][] tData = new String[nRows][nCols];
		int nCol = 0;
		int nRow = 0;
		for (Shapes shapes : wDataSet) {
			
			// firs the store URI values
			tData[nRow][nCol] = shapes.getNodeShape().getModel().shortForm(shapes.getNodeShape().getURI());
			
			for (ShapesValues dataValues : shapes.getShapes()) {				
				 
				String pred = dataValues.getDatatype() != null || dataValues.getDatatype() != "" ? dataValues.getPredicate()+dataValues.getDatatype():dataValues.getPredicate();
				int idxCol = 0;
				for (int i = 0; i < columnsHeader.size(); i++) {
					String path_name =columnsHeader.get(i).getSh_path().toString(); 
					if (path_name.equals(pred)) {
						idxCol = i;
						break;
					}
				}
				
				tData[nRow][idxCol] = dataValues.getObject();
				
			}
			nRow++;
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
		for (NamespaceSection prefixes : NameSpacesectionPrefix) {
			row = sheetPrefix.createRow(rowid++);

			Cell cellP = row.createCell(0);
			Cell cellPrefix = row.createCell(1);
			Cell cellNameSpace = row.createCell(2);

			cellP.setCellValue("PREFIX");
			cellPrefix.setCellValue(prefixes.getprefix());
			cellNameSpace.setCellValue(prefixes.getnamespace());
			
		}		
		
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
		//rowTemplateText.setRowStyle(rowStyle);
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
		//templateColumns.sort(Comparator.comparing(XslTemplate::getSh_order).thenComparing(XslTemplate::getSh_name));
		
		for (XslTemplate r : columnsHeader) {
			Cell cellDesc = rowDescriptionClassColumn.createCell(nCell);
			cellDesc.setCellValue(r.getSh_description());
			Cell cellName = rowNameClassColumn.createCell(nCell);
			cellName.setCellValue(r.getSh_name());
			Cell cellShape = rowShapeClassColumn.createCell(nCell);
			cellShape.setCellValue(r.getSh_path());
			nCell++;
		}
		
		List<String> headerColumns = columnsHeader.stream().map(m -> m.getSh_path()).collect(Collectors.toList());
		
		
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
		
		
		// This section is for classes all configurated
		File currDir = new File(".");
		String path = currDir.getAbsolutePath();
		String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";

		FileOutputStream outputStream = new FileOutputStream(fileLocation);
		workbookShacl.write(outputStream);
		workbookShacl.close();
		
		ShapesReader shaclRead = new ShapesReader();
		List<Shapes> shClasses = new ArrayList<>();

		return shClasses;
	}
}
