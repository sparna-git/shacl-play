package fr.sparna.rdf.shacl.excel.model;

import java.util.List;

import fr.sparna.rdf.shacl.excel.SheetReader;

/**
 * Represents a header in the output excel file. The header contains a description (optional), a name (optional), 
 * and the header line itself (mandatory)
 * 
 * @author thomas
 *
 */
public class SheetColumnHeader {

	protected String description;
	protected String name;
	protected String header;
	
	public SheetColumnHeader(PropertyShapeTemplate template, List<ColumnsInputDatatype> columns_data_header) {
		this.description = template.getSh_description();
		this.name = template.getSh_name();
		this.header = SheetReader.buildHeaderString(template, columns_data_header);
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	

}
