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

	public void startNewSection() {
		sections.add(new ArrayList<JsonLdMapping>());
		currentSection = sections.get(sections.size()-1);
	}
	
	public void add(JsonLdMapping mapping) {
		if(this.containsTerm(mapping.term)) {
			throw new IllegalArgumentException("The context already contains a mapping for the term '"+mapping.term+"'");
		}
		
		this.currentSection.add(mapping);
	}

	/**
	 * Check if the context already contains a mapping for the given term
	 * @param term
	 * @return
	 */
	public boolean containsTerm(String term) {
		for (List<JsonLdMapping> section : sections) {
			for (JsonLdMapping m : section) {
				if(m.term.equals(term)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void write(StringBuffer buffer) {
		String indent = "";
		for (int i = 0; i < this.indent; i++) {
			indent += " ";		
		}
		
		buffer.append("{\n");
		this.writeInner(buffer, indent, '\n');
		buffer.append("}");
	}

	public void writeInner(StringBuffer buffer, String indent, char newLineChar) {
		
		buffer.append("  \"@context\": {"+newLineChar);
		
		for (List<JsonLdMapping> section : sections) {
			// sort the section content
			section.sort((m1, m2) -> m1.term.compareTo(m2.term));
			for (JsonLdMapping m : section) {
				buffer.append(indent);
				m.write(buffer);
				buffer.append(","+newLineChar);
			}
			buffer.append(newLineChar);
			buffer.append(newLineChar);
		}
		// remove last , and line breaks
		buffer.delete(buffer.length()-3-1, buffer.length());
		buffer.append(newLineChar);
		buffer.append("  }"+newLineChar);
	}


}
