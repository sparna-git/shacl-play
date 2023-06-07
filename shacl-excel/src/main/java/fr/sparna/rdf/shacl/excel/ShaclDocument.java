package fr.sparna.rdf.shacl.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.topbraid.shacl.vocabulary.SH;

public class ShaclDocument {

	public List<ShaclClasses> readDocument(Model shaclGraphTemplate, Model shaclGraph) throws IOException {

		// read graph for the building the recovery all the head columns
		List<Resource> nodeShapeTemplate = shaclGraphTemplate.listResourcesWithProperty(RDF.type, SH.NodeShape)
				.toList();

		// read everything typed as NodeShape
		List<Resource> nodeShapes = shaclGraph.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();

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

		//
		XslTemplateReader shaclReadColumns = new XslTemplateReader();
		List<XslTemplate> templateColumns = new ArrayList<>();
		for (Resource ns : nodeShapeTemplate) {
			List<Statement> ShProperty = ns.listProperties(SH.property).toList();
			// properties			
			for (Statement lproperty : ShProperty) {
				RDFNode rdfNode = lproperty.getObject();
				Resource res = rdfNode.asResource();
				templateColumns.add(shaclReadColumns.read(res));
			}
		}
		
		templateColumns.stream().sorted((a,b) -> {
			if(a.getSh_order() != 0) {
				if(b.getSh_order() != 0) {
					return a.getSh_order();
				} else {
					return -1;
				}
			}
			else {
				if (b.getSh_order() == 0) {
					return 1;
				} else {
					return a.getSh_order();
				}
			}
		});
		

		// Get OWL
		List<Resource> ontology = shaclGraph.listResourcesWithProperty(RDF.type, OWL.Ontology).toList();
		ShaclOntologyReader owlReader = new ShaclOntologyReader();
		List<ShaclOntology> owl = owlReader.readOWL(ontology);

		// Get Prefix
		Prefixes pf = new Prefixes();
		List<NamespaceSection> NameSpacesectionPrefix = pf.prefixes(nodeShapes, shaclGraph);

		// Class
		ShaclClassesReader shaclRead = new ShaclClassesReader();
		List<ShaclClasses> shClasses = new ArrayList<>();
		for (Resource res : nodeShapes) {
			shClasses.add(shaclRead.read(res, nodeShapes));
		}

		shClasses.stream().sorted((c1, c2) -> {
			if (!c1.getNodeShape().isAnon()) {
				if (!c2.getNodeShape().isAnon()) {
					return c1.getNodeShape().toString().compareTo(c2.getNodeShape().toString());
				} else {
					return -1;
				}
			} else {
				if (!c2.getNodeShape().isAnon()) {
					return 1;
				} else {
					return c2.getNodeShape().toString().compareTo(c1.getNodeShape().toString());
				}
			}
		});

		List<String> columns = new ArrayList<>();
		shClasses.stream().forEach(n -> {

			for (ShapesValues p : n.getShapes()) {
				columns.add(p.getNameShapes());
			}

		});

		// Sort

		// Title or Header in template
		List<String> HeaderColumns = columns.stream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream()
				.filter(e -> e.getValue() == 1).map(s -> s.getKey()).collect(Collectors.toList());

		
		
		
		
		
		// Blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook();

		// Create a blank sheet for prefixes
		XSSFSheet sheetPrefix = workbook.createSheet("prefixes");

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
		XSSFSheet sheetClasses = workbook.createSheet("classes");
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
		XSSFRow rowTemplateText;
		rowTemplateText = sheetClasses.createRow(rowIdClass + 3);
		Cell cellTemplate = rowTemplateText.createCell(0);
		cellTemplate.setCellValue(
				"This sheet specifies the NodeShape with their targets, that is the sets of entities being validated");
		
		
		// Header
		rowIdClass++;
		
		XSSFRow rowDescriptionClassColumn;
		XSSFRow rowNameClassColumn;
		XSSFRow rowShapeClassColumn;
		rowDescriptionClassColumn = sheetClasses.createRow(rowIdClass++);
		rowNameClassColumn = sheetClasses.createRow(rowIdClass++);
		rowShapeClassColumn = sheetClasses.createRow(rowIdClass++);
		Integer nCell = 0;		
		// write class columns
		for (XslTemplate r : templateColumns) {
			Cell cellDesc = rowDescriptionClassColumn.createCell(nCell);
			cellDesc.setCellValue(r.getSh_description());
			Cell cellName = rowNameClassColumn.createCell(nCell);
			cellName.setCellValue(r.getSh_name());
			Cell cellShape = rowShapeClassColumn.createCell(nCell);
			cellShape.setCellValue(r.getSh_path());
			nCell++;
		}
		
		/*
		List<String> nameHeader = templateColumns.stream().map(n -> n.getSh_name()).collect(Collectors.toList());
		for (String tHeader : nameHeader) {
			Cell cellHeader = rowTemplateText.createCell(nCell);
			nCell++;			
			cellHeader.setCellValue(tHeader);
		}
		
		// write Shape columns
		List<String> shapeHeader = templateColumns.stream().map(n -> n.get).collect(Collectors.toList());
		for (String tHeader : nameHeader) {
			Cell cellHeader = rowTemplateText.createCell(nCell);
			nCell++;			
			cellHeader.setCellValue(tHeader);
		}
		*/

		// This section is for classes all configurated

		File currDir = new File(".");
		String path = currDir.getAbsolutePath();
		String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";

		FileOutputStream outputStream = new FileOutputStream(fileLocation);
		workbook.write(outputStream);
		workbook.close();

		return shClasses;
	}
}
