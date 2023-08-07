package fr.sparna.rdf.shacl.excel.writeXLS;

import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Resource;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import fr.sparna.rdf.shacl.excel.ShaclOntologyReader;
import fr.sparna.rdf.shacl.excel.model.Sheet;
import fr.sparna.rdf.shacl.excel.model.ColumnSpecification;
import fr.sparna.rdf.shacl.excel.model.PropertyShapeTemplate;
import fr.sparna.rdf.shacl.excel.model.ShaclOntology;

public class WriteXLS {
	
	public XSSFWorkbook generateWorkbook(Map<String, String> Prefixes,List<Resource> ontology ,List<Sheet> sheets){
	
		//Blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook(); 
		
		// Sheet for prefix
        XSSFSheet sheet_prefix = workbook.createSheet("prefix");
        sheet_prefix = sheet_prefix(sheet_prefix, Prefixes);
        
        // for all Shape
        for (Sheet sheetData : sheets) {        	
        	//  Write output 
        	writeSheet(workbook,ontology,sheetData);
		}        
		
		return workbook;
	}
	
	
	public static XSSFCellStyle styleColor(XSSFWorkbook workbook,Short color,FillPatternType fpattern) {
		
		XSSFCellStyle sColor = workbook.createCellStyle();
		
		sColor.setFillBackgroundColor(color);
		sColor.setFillPattern(fpattern);
		
		return sColor;
	}
	
	public static XSSFFont styleFont(XSSFWorkbook workbook,Boolean word_bold,String word_fontname) {
		
		XSSFFont font = workbook.createFont();
		
		font.setBold(word_bold);
		font.setFontName(word_fontname);
		
		return font;
	}
	
	
	public static XSSFSheet sheet_ontology(XSSFSheet sheet, List<Resource> ontology) {
		
		//
		XSSFRow row_ontology;
		
		ShaclOntologyReader owlReader = new ShaclOntologyReader();
		List<ShaclOntology> owl = owlReader.readOWL(ontology);
		
		String uriShape = "";		
		for (ShaclOntology owlonto : owl) {
			row_ontology = sheet.createRow(0);

			if (uriShape != owlonto.getShapeUri()) {
				Cell cellURI = row_ontology.createCell(0);
				Cell cellValueURI = row_ontology.createCell(1);

				cellURI.setCellValue("Shapes URI");
				cellValueURI.setCellValue(owlonto.getShapeUri());
			}
		}
		
		int rowIdClass = 1;
		for (ShaclOntology owlonto : owl) {
			row_ontology = sheet.createRow(rowIdClass++);

			Cell cellProperty = row_ontology.createCell(0);
			Cell cellValue = row_ontology.createCell(1);

			cellProperty.setCellValue(owlonto.getOwlProperty());
			cellValue.setCellValue(owlonto.getOwlValue());
		}
		
		
		
		
		return sheet;
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
		
	public static XSSFSheet writeSheet(XSSFWorkbook workbook, List<Resource> ontology, Sheet sheetData) {
	
		XSSFSheet xlsSheet = workbook.createSheet(sheetData.getNameSheet());
    	// column size
    	xlsSheet.setDefaultColumnWidth(40);
    	
    	// write OWL
    	if (!ontology.isEmpty()) {
    		xlsSheet = sheet_ontology(xlsSheet,ontology);
    	}
		
		// Declaration of font type
		XSSFFont headerFont = workbook.createFont();
		headerFont = styleFont(workbook,true,"Arial");
		headerFont.setColor(IndexedColors.WHITE.getIndex());
		
        // Config color for each column Header for path
		XSSFCellStyle style_path = workbook.createCellStyle();
    	style_path = styleColor(workbook, IndexedColors.DARK_RED.index,FillPatternType.THICK_BACKWARD_DIAG);
    	style_path.setAlignment(HorizontalAlignment.CENTER);
    	
    	// Column Description
    	XSSFCellStyle style_Description = workbook.createCellStyle();
    	style_Description = styleColor(workbook, IndexedColors.LIGHT_TURQUOISE1.index,FillPatternType.THICK_BACKWARD_DIAG);
    	style_Description.setAlignment(HorizontalAlignment.JUSTIFY);
		
		// Get the las row in the sheet
    	Integer nRow = xlsSheet.getLastRowNum()+3;
	    
		// write Columns 
    	
    	// for description row
		Integer nCell_desc = 0;
    	XSSFRow row_desc = xlsSheet.createRow(nRow++);
    	row_desc.setHeight((short) 1300);
    	XSSFCellStyle style_description_font = workbook.createCellStyle();    	
    	for (ColumnSpecification cols : sheetData.getColumns()) {
    		XSSFCell cell_desc = row_desc.createCell(nCell_desc);       	
    		cell_desc.setCellValue(cols.getDescription());
    		
    		// Style
    		cell_desc.setCellStyle(style_Description);
    		style_Description.setFont(headerFont);
    		style_Description.setWrapText(true);
    		headerFont.setItalic(true);
    		headerFont.setBold(false);
    		
        	nCell_desc++;
		}
    	
    	Integer nCell_name = 0;
    	XSSFRow row_name = xlsSheet.createRow(nRow++);
    	for (ColumnSpecification cols : sheetData.getColumns()) {
    		XSSFCell cell_name = row_name.createCell(nCell_name);
        	cell_name.setCellValue(cols.getLabel());
        	nCell_name++;
		}
    	
    	
    	
    	XSSFRow row_path = xlsSheet.createRow(nRow++);
    	// Create a CellStyle for the font
        
    	//XSSFCellStyle headerFonttStyle = workbook.createCellStyle();        
        //headerFonttStyle.setFont(headerFont);
        
    	Integer nCell_path = 0;
    	for (ColumnSpecification cols : sheetData.getColumns()) {
    		XSSFCell cell_path = row_path.createCell(nCell_path);
        	cell_path.setCellValue(cols.getHeaderString());
        	// Style
        	cell_path.setCellStyle(style_path);
        	style_path.setFont(headerFont);
        	
        	nCell_path++;
		}
		
    	// Write in excel
    	//All dataSet
		XSSFRow rowDataSet;
		Integer nCellData = row_path.getRowNum()+1;
		for (int line = 0; line < sheetData.getOutputData().size(); line++) {
			rowDataSet = xlsSheet.createRow(nCellData++);
			
			for (int i = 0; i < sheetData.getOutputData().get(line).length; i++) {
				Cell CellData = rowDataSet.createCell(i);
				CellData.setCellValue(sheetData.getOutputData().get(line)[i]);
			}
		}
		
		return xlsSheet;
	}
	
}
