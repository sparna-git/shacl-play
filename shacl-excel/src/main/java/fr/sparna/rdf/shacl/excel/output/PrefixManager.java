package fr.sparna.rdf.shacl.excel.output;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrefixManager {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private Map<String, String> prefixes = new HashMap<>();
	// prefixes explicitely declared in the file
	private Map<String, String> explicitelyDeclaredPrefixes = new HashMap<>();
	
		
	public void register(String prefix, String uri) {
		// store both in all prefixes and explicitely declared prefixes
		this.prefixes.put(prefix, uri);
		this.explicitelyDeclaredPrefixes.put(prefix, uri);
	}
	
	public void register(Map<String, String> map) {
		// store both in all prefixes and explicitely declared prefixes
		this.prefixes.putAll(map);
		this.explicitelyDeclaredPrefixes.putAll(map);
	}
	
	public String expand(String shortForm) {
		if(shortForm == null) {
			return null;
		}
		if(!shortForm.contains(":")) {
			return null;
		}
		
		if(shortForm.startsWith("http")) {
			return shortForm;
		}
		
		String namespace = prefixes.get(shortForm.substring(0, shortForm.indexOf(':')));
		
		// prefix not found
		if(namespace == null) {
			return null;
		}
		
		return namespace+shortForm.substring(shortForm.indexOf(':')+1);
	}
	
	public boolean usesKnownPrefix(String qname) {
		boolean result = false;
		if(qname.contains(":")) {
			String prefix = qname.substring(0, qname.indexOf(':'));
			result = prefixes.containsKey(prefix);
		}
		return result;
	}
	
	public String uri(String value, boolean fixHttp) {
		// if the value starts with http, return it directly
		if(value.startsWith("http") || value.startsWith("mailto")) {
			// trim the value to remove trailing whitespaces
			return value.trim();
		}
		// if the value uses a known prefix, expand it
		if(usesKnownPrefix(value)) {
			// always trim, trim, trim
			return expand(value).trim();
		}
		
		if(fixHttp) {
			// otherwise try to fix the URI by adding "http://" in front of the value (may happen when excel formats the value)
			try {
				String trimmedValue = value.trim();
				new URI("http://"+trimmedValue);
				return "http://"+trimmedValue;
			} catch (URISyntaxException e) {
				log.warn("Cannot fix URI by adding http://, problem is "+e.getMessage());
				return null;
			}
		} else {
			return null;
		}
	}
	
	public String getPrefixesTurtleHeader() {
		StringBuffer buffer = new StringBuffer();
		this.prefixes.entrySet().stream().forEach(e -> { 
			buffer.append("@prefix "+e.getKey()+":\t"+"<"+e.getValue()+"> ."+"\n");
		});
		return buffer.toString();
	}

	public Map<String, String> getPrefixes() {
		return prefixes;
	}

	/**
	 * Get the prefixes to be used in the output, by taking first the prefixes explicitely declared in the file
	 * and then, only if not already explicitely mapped, the default known prefixes.
	 * This garantees that e.g. "dcterms" is always used in the output if explicitely declared like that.
	 */
	public Map<String, String> getOutputPrefixes() {
		Map<String, String> outputPrefixes = new HashMap<>();
		outputPrefixes.putAll(this.explicitelyDeclaredPrefixes);
		this.prefixes.entrySet().forEach(e -> { 
			if(!outputPrefixes.containsValue(e.getValue())) {
				outputPrefixes.put(e.getKey(),e.getValue());
			}
		});
		return outputPrefixes;
	}
	
}
