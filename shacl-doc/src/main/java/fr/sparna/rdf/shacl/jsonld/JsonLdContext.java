package fr.sparna.rdf.shacl.jsonld;

import java.util.ArrayList;
import java.util.List;

public class JsonLdContext {

	protected List<List<JsonLdMapping>> sections = new ArrayList<>();
	
	private transient List<JsonLdMapping> currentSection;
	
	private int indent = 4;

	public JsonLdContext() {
		super();
		startNewSection();
	}

	protected void startNewSection() {
		sections.add(new ArrayList<JsonLdMapping>());
		currentSection = sections.get(sections.size()-1);
	}
	
	protected void add(JsonLdMapping mapping) {
		this.currentSection.add(mapping);
	}
	
	public void write(StringBuffer buffer) {
		String indent = "";
		for (int i = 0; i < this.indent; i++) {
			indent += " ";		
		}
		
		buffer.append("{\n");
		buffer.append("  \"@context\": {"+"\n");
		
		for (List<JsonLdMapping> section : sections) {
			for (JsonLdMapping m : section) {
				buffer.append(indent);
				m.write(buffer);
				buffer.append(",\n");
			}
			buffer.append("\n");
			buffer.append("\n");
		}
		// remove last , and line breaks
		buffer.delete(buffer.length()-3-1, buffer.length());
		buffer.append("\n");
		buffer.append("  }\n");
		buffer.append("}");
	}
	
}
