package fr.sparna.rdf.shacl.jsonld;

public class JsonLdMapping {

	protected String term;
	protected String id;
	protected String type;
	protected String container;
	protected String language;
	protected boolean reverse = false;
	protected JsonLdContext innerContext;
	
	public JsonLdMapping(String term, String id) {
		super();
		this.term = term;
		this.id = id;
	}	

	public JsonLdMapping(String term, String id, boolean reverse) {
		super();
		this.term = term;
		this.id = id;
		this.reverse = reverse;
	}	
	
	public JsonLdMapping(String term, String id, String type) {
		super();
		this.term = term;
		this.id = id;
		this.type = type;
	}
	
	/**
	 * @return true if this mapping only has an id set, but no type and no container
	 */
	private boolean isOnlyIdMapping() {
		return (type == null) && (container == null) && (innerContext == null) && (language == null) && !reverse;
	}

	public void write(StringBuffer buffer) {
		if(isOnlyIdMapping()) {
			buffer.append("\""+term+"\""+": "+"\""+id+"\"");
		} else {			
			String idKey = reverse ? "@reverse" : "@id";
			buffer.append("\""+term+"\""+": "+"{\""+idKey+"\":\""+id+"\"");
			if (type != null ) { 				
				buffer.append(", \"@type\""+":"+"\""+type+"\"");
			}
			if (container != null ) { 				
				buffer.append(", \"@container\""+":"+"\""+container+"\"");
			}
			if (language != null ) { 				
				buffer.append(", \"@language\""+":"+"\""+language+"\"");
			}
			if (innerContext != null ) { 	
				buffer.append(", ");
				innerContext.writeInner(buffer, "", ' ');
			}
			buffer.append("}");
		}		
	}

	public String getTerm() {
		return term;
	}

	public JsonLdContext getInnerContext() {
		return innerContext;
	}

	public void setInnerContext(JsonLdContext innerContext) {
		this.innerContext = innerContext;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContainer() {
		return container;
	}

	public void setContainer(String container) {
		this.container = container;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	public boolean isReverse() {
		return reverse;
	}
	
	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}

	public void setId(String id) {
		this.id = id;
	}
}
