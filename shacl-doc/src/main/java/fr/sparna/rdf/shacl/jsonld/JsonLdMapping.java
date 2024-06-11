package fr.sparna.rdf.shacl.jsonld;

public class JsonLdMapping {

	protected String term;
	protected String id;
	protected String type;
	
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
	
	private boolean isOnlyIdMapping() {
		return type == null;
	}

	public void write(StringBuffer buffer) {
		if(isOnlyIdMapping()) {
			//buffer.append("\""+term+"\""+": "+"\""+id+"\"");
			buffer.append("\""+term+"\""+": "+id);
		} else {
			
			if (id.contains("@container")) {
				buffer.append("\""+term+"\""+": "+"{\"@id\""+":"+id);
			} else {
				buffer.append("\""+term+"\""+": "+"{\"@id\""+":"+id);
				if (type != null ) { 				
					buffer.append(", \"@type\""+":"+"\""+type+"\"");
				}
			}
			
			//buffer.append("\""+term+"\""+": "+"{\"@id\":\""+id+"\"");
			/*
			if (type != null ) { 				
				buffer.append(", \"@type\""+":"+"\""+type+"\"");
			}
			*/
			buffer.append("}");
		}
		
	}
	
	
}
