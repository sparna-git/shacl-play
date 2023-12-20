package fr.sparna.rdf.shacl.doc.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartDataset {

	private String PropertyName;
	private List<ChartDatasetValues> datavalues = new ArrayList<>();
	private Map<String,Integer> mapChart = new HashMap<>();
	
	public Map<String, Integer> getMapChart() {
		return mapChart;
	}
	public void setMapChart(Map<String, Integer> mapChart) {
		this.mapChart = mapChart;
	}
	public String getPropertyName() {
		return PropertyName;
	}
	public void setPropertyName(String propertyName) {
		PropertyName = propertyName;
	}
	public List<ChartDatasetValues> getDatavalues() {
		return datavalues;
	}
	public void setDatavalues(List<ChartDatasetValues> datavalues) {
		this.datavalues = datavalues;
	}
	
	
}