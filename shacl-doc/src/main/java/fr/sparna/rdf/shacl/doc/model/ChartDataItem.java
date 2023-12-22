package fr.sparna.rdf.shacl.doc.model;

public class ChartDataItem {

	private String label;
	private int value;	
	
	public ChartDataItem(String label, int value) {
		super();
		this.label = label;
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public int getValue() {
		return value;
	}
	
	
}