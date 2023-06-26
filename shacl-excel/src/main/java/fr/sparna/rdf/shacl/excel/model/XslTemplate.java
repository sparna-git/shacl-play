package fr.sparna.rdf.shacl.excel.model;

public class XslTemplate {

	protected String Sh_path;
	protected String Sh_description;
	protected String Sh_name;
	protected Integer Sh_order;
	protected String datatype;
	protected String Sh_UniqueLang;
	
	public String getSh_UniqueLang() {
		return Sh_UniqueLang;
	}
	public void setSh_UniqueLang(String sh_UniqueLang) {
		Sh_UniqueLang = sh_UniqueLang;
	}
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	public String getSh_path() {
		return Sh_path;
	}
	public void setSh_path(String sh_path) {
		Sh_path = sh_path;
	}
	public String getSh_description() {
		return Sh_description;
	}
	public void setSh_description(String sh_description) {
		Sh_description = sh_description;
	}
	public String getSh_name() {
		return Sh_name;
	}
	public void setSh_name(String sh_name) {
		Sh_name = sh_name;
	}
	public Integer getSh_order() {
		return Sh_order;
	}
	public void setSh_order(Integer sh_order) {
		Sh_order = sh_order;
	}
}
