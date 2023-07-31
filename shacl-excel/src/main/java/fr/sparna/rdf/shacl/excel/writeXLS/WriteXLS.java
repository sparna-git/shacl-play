package fr.sparna.rdf.shacl.excel.writeXLS;

import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Resource;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import fr.sparna.rdf.shacl.excel.ShaclOntologyReader;
import fr.sparna.rdf.shacl.excel.model.ModelStructure;
import fr.sparna.rdf.shacl.excel.model.PropertyShapeTemplate;
import fr.sparna.rdf.shacl.excel.model.ShaclOntology;

public class WriteXLS {
	
	public XSSFWorkbook processWorkBook(Map<String, String> Prefixes,List<Resource> ontology ,List<ModelStructure> dataset){
	
		//Blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook(); 
		
		// Declaration of font type
		XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontName("Arial");
        
        // Create a CellStyle for the font
        XSSFCellStyle headerFontBoltStyle = workbook.createCellStyle();        
        headerFontBoltStyle.setFont(headerFont);
        
        // Style Color 
		XSSFCellStyle rowStyle = workbook.createCellStyle();
		rowStyle.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
		rowStyle.setFillPattern( FillPatternType.SOLID_FOREGROUND);
	
		
        // Sheet for prefix
        XSSFSheet sheet_prefix = workbook.createSheet("prefix");
        sheet_prefix = sheet_prefix(sheet_prefix, Prefixes);
        
        // for all Shape
        XSSFSheet sheet;
        for (ModelStructure outputData : dataset) {
			
        	sheet = workbook.createSheet(outputData.getNameSheet());
        	
        	// column size
        	sheet.setDefaultColumnWidth(70);
        	
        	// write OWL
        	if (!ontology.isEmpty()) {
        		ShaclOntologyReader owlReader = new ShaclOntologyReader();
        		List<ShaclOntology> owl = owlReader.readOWL(ontology);
        		
    			String uriShape = "";
    			XSSFRow rowOWL = null; 
    			for (ShaclOntology owlonto : owl) {
    				rowOWL = sheet.createRow(0);

    				if (uriShape != owlonto.getShapeUri()) {
    					Cell cellURI = rowOWL.createCell(0);
    					Cell cellValueURI = rowOWL.createCell(1);

    					cellURI.setCellValue("Shapes URI");
    					cellValueURI.setCellValue(owlonto.getShapeUri());
    				}
    			}
    			
    			int rowIdClass = 1;
    			for (ShaclOntology owlonto : owl) {
    				rowOWL = sheet.createRow(rowIdClass++);

    				Cell cellProperty = rowOWL.createCell(0);
    				Cell cellValue = rowOWL.createCell(1);

    				cellProperty.setCellValue(owlonto.getOwlProperty());
    				cellValue.setCellValue(owlonto.getOwlValue());
    			}
        	}
        	
        	//  Write output 
        	sheet = writer_in_sheet(workbook,headerFont,rowStyle,sheet,outputData);
						 
		}        
		
		return workbook;
	}
	
	
	public static XSSFCellStyle styleColor(XSSFCellStyle styleColor,Short color,FillPatternType fpattern) {
		
		styleColor.setFillBackgroundColor(IndexedColors.AQUA.getIndex());
        styleColor.setFillPattern(FillPatternType.BIG_SPOTS);
		
		return styleColor;
	}
	
	
	public static XSSFSheet sheet_prefix(XSSFSheet sheet, Map<String, String> data) {
		
		//Iterate over data and write to sheet
		// Create Row
		XSSFRow row_prefix;
		// Write Prefixes
		int rowprefix = 0;
		for (Map.Entry<String, String> onePrefix : data.entrySet()) {
			row_prefix = sheet.createRow(rowprefix++);

			Cell cellP = row_prefix.createCell(0);
			Cell cellPrefix = row_prefix.createCell(1);
			Cell cellNameSpace = row_prefix.createCell(2);

			cellP.setCellValue("PREFIX");
			cellPrefix.setCellValue(onePrefix.getKey());
			cellNameSpace.setCellValue(onePrefix.getValue());			
		}
		
		return sheet;
	}
	
	
	public static XSSFSheet writer_in_sheet(XSSFWorkbook wb,XSSFFont HeaderfontStyle,XSSFCellStyle colorStyle ,XSSFSheet sheet,ModelStructure dataset) {
	
		
		Integer nRow = sheet.getLastRowNum()+3;
		// write Columns
		Integer nCell_desc = 0;
    	XSSFRow row_desc = sheet.createRow(nRow++);
    	XSSFCellStyle styleDescription = wb.createCellStyle();
    	
    	for (PropertyShapeTemplate cols : dataset.getColumns()) {
    		XSSFCell cell_desc = row_desc.createCell(nCell_desc);            	
    		cell_desc.setCellValue(cols.getSh_description());
    		//styleDescription.setAlignment(null);
    		nCell_desc++;
		}
    	
    	Integer nCell_name = 0;
    	XSSFRow row_name = sheet.createRow(nRow++);
    	for (PropertyShapeTemplate cols : dataset.getColumns()) {
    		XSSFCell cell_name = row_name.createCell(nCell_name);
        	cell_name.setCellValue(cols.getSh_name().toString());
        	nCell_name++;
		}
    	
    	XSSFRow row_path = sheet.createRow(nRow++);
    	//row_path.setRowStyle(colorStyle);
    	Integer nCell_path = 0;
    	for (PropertyShapeTemplate cols : dataset.getColumns()) {
    		XSSFCell cell_path = row_path.createCell(nCell_path);
        	cell_path.setCellValue(cols.getSh_path().toString());
        	cell_path.setCellStyle(colorStyle);
        	colorStyle.setAlignment(HorizontalAlignment.CENTER);
        	HeaderfontStyle.setColor(IndexedColors.WHITE.getIndex());
        	colorStyle.setFont(HeaderfontStyle);
        	
        	nCell_path++;
		}
		
    	// Write in excel
    	//All dataSet
		XSSFRow rowDataSet;
		Integer nCellData = row_path.getRowNum()+1;
		for (int line = 0; line < dataset.getOutputData().size(); line++) {
			rowDataSet = sheet.createRow(nCellData++);
			
			for (int i = 0; i < dataset.getOutputData().get(line).length; i++) {
				Cell CellData = rowDataSet.createCell(i);
				CellData.setCellValue(dataset.getOutputData().get(line)[i]);
			}
		}
				
		
		return sheet;
	}
	
}
