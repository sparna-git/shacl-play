package fr.sparna.rdf.shacl.doc.model;
public class ChartDataset {

	private String PropertyName;
	private int NumberOfDistinct;
	
	public String getPropertyName() {
		return PropertyName;
	}
	public void setPropertyName(String propertyName) {
		PropertyName = propertyName;
	}
	public int getNumberOfDistinct() {
		return NumberOfDistinct;
	}
	public void setNumberOfDistinct(int numberOfDistinct) {
		NumberOfDistinct = numberOfDistinct;
	}
}