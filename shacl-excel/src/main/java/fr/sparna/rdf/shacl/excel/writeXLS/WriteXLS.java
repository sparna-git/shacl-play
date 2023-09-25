package fr.sparna.rdf.shacl.excel.writeXLS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFBorderFormatting;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import fr.sparna.rdf.shacl.excel.model.ColumnSpecification;
import fr.sparna.rdf.shacl.excel.model.Sheet;

public class WriteXLS {
	
	public XSSFWorkbook generateWorkbook(
			Map<String, String> prefixes,
			List<Sheet> sheets
	){
	
		//Blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook(); 
		
		// Sheet for prefix
        XSSFSheet sheet_prefix = workbook.createSheet("Prefixes");
        sheet_prefix = sheet_prefix(sheet_prefix, prefixes);
        
        // for all Shape
        for (Sheet sheetData : sheets) {        	
        	//  Write output 
        	writeSheet(workbook,sheetData);
		}        
		
		return workbook;
	}
	
	
	public static XSSFCellStyle createCellStyleWithBackgroundColor(XSSFWorkbook workbook,Color color) {		
		XSSFCellStyle sColor = workbook.createCellStyle();		
		sColor.setFillForegroundColor(color);
		sColor.setFillPattern(FillPatternType.SOLID_FOREGROUND);		
		return sColor;
	}
	
	public static XSSFFont createFont(
			XSSFWorkbook workbook,
			String word_fontname,
			boolean bold,
			boolean italic,
			short color
	) {		
		XSSFFont font = workbook.createFont();		
		font.setBold(bold);
		font.setItalic(italic);
		if(word_fontname != null) {
			font.setFontName(word_fontname);
		}
		if(color > 0) {
			font.setColor(color);
		}
		return font;
	}
	
	public static XSSFSheet sheet_ontology(XSSFSheet sheet, String b1Uri, List<String[]> headervalues) {
		
		XSSFRow row_ontology = sheet.createRow(0);
		Cell cellURI = row_ontology.createCell(0);
		Cell cellValueURI = row_ontology.createCell(1);
		cellURI.setCellValue("Class/shape URI");
		cellValueURI.setCellValue(b1Uri);
		
		if(headervalues != null) {
			int rowIdClass = 1;
			for (String[] aPair : headervalues) {
				XSSFRow row = sheet.createRow(rowIdClass++);
				row.createCell(0).setCellValue(aPair[0]);
				row.createCell(1).setCellValue(aPair[1]);
			}
		}
		
		return sheet;
	}
	
	public static XSSFSheet sheet_prefix(XSSFSheet sheet, Map<String, String> data) {
		
		//Iterate over data and write to sheet
		// Create Row
		XSSFRow row_prefix;
		
		// sort prefixes according to key
		List<String> sortedKeys = new ArrayList<>(data.keySet());
		Collections.sort(sortedKeys);
		
		// Write Prefixes
		int rowprefix = 0;
		for (String key : sortedKeys) {
			row_prefix = sheet.createRow(rowprefix++);

			row_prefix.createCell(0).setCellValue("PREFIX");;
			row_prefix.createCell(1).setCellValue(key);
			row_prefix.createCell(2).setCellValue(data.get(key));		
		}
		
		return sheet;
	}
		
	public static XSSFSheet writeSheet(XSSFWorkbook workbook, Sheet sheetData) {
	
		XSSFSheet xlsSheet = workbook.createSheet(sheetData.getName());
    	// column size
    	xlsSheet.setDefaultColumnWidth(40);
    	
    	// write OWL
    	xlsSheet = sheet_ontology(xlsSheet,sheetData.getB1Uri(),sheetData.getHeaderValues());
		
		// useful to create custom colors below
		IndexedColorMap colorMap = workbook.getStylesSource().getIndexedColors();
    	
    	
		// Get the las row in the sheet
    	Integer nRow = xlsSheet.getLastRowNum()+3;
	    
		// write Columns 
    	
    	// for description row
		Integer nCell_desc = 0;
    	XSSFRow row_desc = xlsSheet.createRow(nRow++);
    	row_desc.setHeight((short) 1300);
	
    	
    	// Column Description
    	XSSFColor MEDIUM_GREEN = new XSSFColor(new java.awt.Color(169,208,141), colorMap);
		XSSFCellStyle style_Description = createCellStyleWithBackgroundColor(workbook, MEDIUM_GREEN);
    	style_Description.setAlignment(HorizontalAlignment.JUSTIFY);
    	
    	XSSFFont descFont = createFont(workbook, null, false, true, IndexedColors.GREY_80_PERCENT.getIndex());
    	
    	for (ColumnSpecification cols : sheetData.getColumns()) {
    		XSSFCell cell_desc = row_desc.createCell(nCell_desc);       	
    		cell_desc.setCellValue(cols.getDescription());
    		
    		// Style
    		cell_desc.setCellStyle(style_Description);
    		style_Description.setFont(descFont);
    		
        	nCell_desc++;
		}
    	
    	// Column Label
    	Integer nCell_name = 0;
    	XSSFRow row_name = xlsSheet.createRow(nRow++);
    	
    	XSSFColor MEDIUM_LIGHT_GREEN = new XSSFColor(new java.awt.Color(215,227,188), colorMap);
		XSSFCellStyle labelCellStyle = createCellStyleWithBackgroundColor(workbook, MEDIUM_LIGHT_GREEN);
		labelCellStyle.setAlignment(HorizontalAlignment.CENTER);
    	
    	XSSFFont labelFont = createFont(workbook, null, true, false, IndexedColors.GREY_80_PERCENT.getIndex());
    	
    	for (ColumnSpecification cols : sheetData.getColumns()) {
    		XSSFCell cell_name = row_name.createCell(nCell_name);
        	cell_name.setCellValue(cols.getLabel());
        	cell_name.setCellStyle(labelCellStyle);
        	labelCellStyle.setFont(labelFont);
        	
        	nCell_name++;
		}
    	
    	
    	
    	XSSFRow row_path = xlsSheet.createRow(nRow++);        
    	Integer nCell_path = 0;
    	
        // Config color for each column Header for path
		XSSFColor LIGHT_GREEN = new XSSFColor(new java.awt.Color(235,241,222), colorMap);
		XSSFCellStyle headerCellStyle = createCellStyleWithBackgroundColor(workbook, LIGHT_GREEN);		
    	headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
    	headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
    	
    	XSSFFont headerFont = createFont(workbook, null, false, false, IndexedColors.GREY_80_PERCENT.getIndex());
    	
    	for (ColumnSpecification cols : sheetData.getColumns()) {
    		XSSFCell cell_path = row_path.createCell(nCell_path);
        	
    		cell_path.setCellValue(cols.getHeaderString());
        	// Style
        	cell_path.setCellStyle(headerCellStyle);
        	headerCellStyle.setFont(headerFont);
        	
        	nCell_path++;
		}
		
    	// Write in excel all dataSet
		XSSFRow rowDataSet;
		Integer nCellData = row_path.getRowNum()+1;
		for (int line = 0; line < sheetData.getOutputData().size(); line++) {
			rowDataSet = xlsSheet.createRow(nCellData++);
			
			for (int i = 0; i < sheetData.getOutputData().get(line).length; i++) {
				String s = sheetData.getOutputData().get(line)[i];
				Cell cell = rowDataSet.createCell(i);
				cell.setCellValue(s);
				
				// if we have a long string, wrap the cell
				if(!s.startsWith("http") && !(s.startsWith("(") && s.endsWith(")")) && s.length() > 50) {
					XSSFCellStyle style = workbook.createCellStyle();
					style.setWrapText(true);
					cell.setCellStyle(style);
				}
			}
		}
		
		return xlsSheet;
	}
	
}
