package fr.sparna.rdf.shacl.excel.writeXLS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.jena.shared.PrefixMapping;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import fr.sparna.rdf.shacl.excel.output.PrefixManager;
import picocli.CommandLine.Model;

public class WriteXLS {
	
	/**
	 * The prefixes declared in the file along with utility classes and default prefixes
	 */
	protected PrefixManager prefixManager = new PrefixManager();
	
	
	public List<Model> processWorkBook(Workbook workbook){
		
		List<Model> models = new ArrayList<>();
		
		
		
		
		
		
		return models;
	}
	
	
	/**
	 * Init the prefix manager with the prefixes declared in the Sheet
	 * @param sheet
	 */
	private void initPrefixManager(PrefixMapping prefixes) {
		// read the prefixes
		this.prefixManager.register((Map<String, String>) prefixes);
	}

}
