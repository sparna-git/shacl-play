package fr.sparna.rdf.shacl.doc.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Chart {

	private String title;
	
	@JacksonXmlElementWrapper(localName="items")
	@JacksonXmlProperty(localName = "item")
	private List<ChartDataItem> items = new ArrayList<>();
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<ChartDataItem> getItems() {
		return items;
	}
	public void setItems(List<ChartDataItem> items) {
		this.items = items;
	}
	
	
}