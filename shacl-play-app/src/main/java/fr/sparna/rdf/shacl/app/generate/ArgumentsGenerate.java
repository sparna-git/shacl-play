package fr.sparna.rdf.shacl.app.generate;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;


@Parameters(commandDescription = "Generates the SHACL profile of an input knowledge graph")
public class ArgumentsGenerate {
	
	@Parameter(
			names = { "-e", "--endpoint" },
			description = "URL of SPARQL endpoint to analyze (e.g. https://dbpedia.org/sparql). Either endpoint or input needs to be specified."
	)
	private String endpoint;
	
	@Parameter(
			names = { "-i", "--input" },
			description = "Input data file to analyse. This can be repeated for multiple input files, and can point to a directory. Either endpoint or input needs to be specified.",
			variableArity = true
	)
	private List<File> input;
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Path where the SHACL file will be written. The format of the file is determined based"
					+ " on the file extension : '*.ttl, *.rdf, *.n3, *.nq, *.nt, *.trig, *.jsonld' ",
			required = true
	)
	private File output;

	@Parameter(
			names = { "-p", "--prefix" },
			description = "Namespace prefixes to be added to the output shapes, in the form <key1>:<ns1> <key2>:<ns2> e.g. skos:http://www.w3.org/2004/02/skos/core# dct:http://purl.org/dc/terms/",
			variableArity = true
	)
	private List<String> prefixes;
	
	@Parameter(
			names = { "-inc", "--include" },
			description = "List of classes URI to be included in the analysis. Classes not in the list will not be analyzed.",
			variableArity = true
	)
	private List<String> includes;
	
	@Parameter(
			names = { "-exc", "--exclude" },
			description = "List of classes URI to be excluded from the analysis. If a class is in this list, it will not be analyzed",
			variableArity = true
	)
	private List<String> excludes;

	@Parameter(
		names = { "-nl", "--no-labels" },
		description = "Set this option to avoid generating sh:name and rdfs:label on the shapes. Defaults to false",
		required = false
	)
	private boolean noLabels = false;
	
	public Map<String, String> getAdditionnalPrefixes() {
		if(this.prefixes == null) {
			return null;
		}
		Map<String, String> result = new HashMap<String, String>();
		for (String aMappingString : this.prefixes) {
			result.put(aMappingString.substring(0,aMappingString.indexOf(':')),aMappingString.substring(aMappingString.indexOf(':')+1));
		}
		return result;
	}
	

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public List<File> getInput() {
		return input;
	}

	public void setInput(List<File> input) {
		this.input = input;
	}

	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = output;
	}

	public List<String> getPrefixes() {
		return prefixes;
	}

	public void setPrefixes(List<String> prefixes) {
		this.prefixes = prefixes;
	}

	public List<String> getIncludes() {
		return includes;
	}

	public List<String> getExcludes() {
		return excludes;
	}

	public boolean isNoLabels() {
		return noLabels;
	}

	public void setNoLabels(boolean noLabels) {
		this.noLabels = noLabels;
	}
	
}