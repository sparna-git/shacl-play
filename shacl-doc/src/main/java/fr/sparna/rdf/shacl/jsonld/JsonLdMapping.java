package fr.sparna.rdf.shacl.jsonld;

public class JsonLdMapping {

	protected String term;
	protected String id;
	protected String type;
	protected String container;
	protected boolean inverse;
	
	public JsonLdMapping(String term, String id) {
		super();
		this.term = term;
		this.id = id;
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
		return (type == null) && (container == null);
	}

	public void write(StringBuffer buffer) {
		if(isOnlyIdMapping()) {
			buffer.append("\""+term+"\""+": "+"\""+id+"\"");
		} else {
			
			if (!this.inverse) {
				buffer.append("\""+term+"\""+": "+"{\"@id\":\""+id+"\"");
			} else {
				buffer.append("\""+term+"\""+": "+"{\"@reverse\":\""+id+"\"");
			}
			
			if (type != null ) { 				
				buffer.append(", \"@type\""+":"+"\""+type+"\"");
			}
			if (container != null ) { 				
				buffer.append(", \"@container\""+":"+"\""+container+"\"");
			}
			buffer.append("}");
		}		
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

	public boolean getInverse() {
		return inverse;
	}

	public void setInverse(boolean inverse) {
		this.inverse = inverse;
	}	
}
