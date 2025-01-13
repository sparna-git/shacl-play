package fr.sparna.rdf.shacl.app.validate;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import fr.sparna.cli.SpaceSplitter;

@Parameters(commandDescription = "Validates some input RDF data against the provided SHACL file, and writes the output in one or more output file. The format "
		+ "of the output file(s) is determined based on the file extension.")
public class ArgumentsValidate {

	@Parameter(
			names = { "-i", "--input" },
			description = "Path to a local RDF file, or directory containing multiple RDF files. This can be repeated to read multiple input files (e.g. data files + ontology file + vocabulary file)",
			required = true,
			variableArity = true
	)
	private List<File> input;
	
	@Parameter(
			names = { "-s", "--shapes" },
			description = "Path to an RDF file or directory containing the shapes definitions to use, this can be repeated to merge multiple SHACL files.",
			required = true,
			variableArity = true
	)
	private List<File> shapes;
	
	@Parameter(
			names = { "-x", "--extra" },
			description = "Extra data to use for validation - but not part of validated data itself. Typically ontology file with subClassOf predicates. This can be repeated.",
			required = false,
			variableArity = true
	)
	private List<File> extra;

	@Parameter(
			names = { "-o", "--output" },
			description = "Path to an output file. This can be repeated to serialize the report in multiple output files. The format of the file is determined based"
					+ " on the file extension : '*.html' for the HTML report, '*-raw.html' for a raw listing of validation results, '*-summary.html' "
					+ " for a raw rendering of a summary of validation results, '*.ttl' or '*.rdf' for an RDF serialisation of the report, in Turtle or RDF/XML,"
					+ " '*.csv' for a CSV serilisation of the validation results \n ",
			required = true,
			variableArity = true
	)
	private List<File> output;
	
	@Parameter(
			names = { "-c", "--copyInput" },
			description = "Path to the file where the input should be copied to be examined"
	)
	private File copyInput;
	
	@Parameter(
			names = { "-cd", "--createDetails" },
			description = "Asks the SHACL validator to create details for OrComponent and AndComponents. Defaults to false."
	)
	private boolean createDetails = false;
	
	@Parameter(
		names = { "-ns", "--namespaces" },
			description = "Namespace prefixes, in the form <key1>,<ns1> <key2>,<ns2> e.g. skos,http://www.w3.org/2004/02/skos/core# dct,http://purl.org/dc/terms/",
			variableArity = true,
			splitter = SpaceSplitter.class
	)
	private List<String> namespaceMappingsStrings;

	public List<File> getInput() {
		return input;
	}

	public void setInput(List<File> input) {
		this.input = input;
	}

	public List<File> getOutput() {
		return output;
	}

	public void setOutput(List<File> output) {
		this.output = output;
	}

	public List<File> getShapes() {
		return shapes;
	}

	public void setShapes(List<File> shapes) {
		this.shapes = shapes;
	}

	public List<File> getExtra() {
		return extra;
	}

	public void setExtra(List<File> extra) {
		this.extra = extra;
	}

	public File getCopyInput() {
		return copyInput;
	}

	public void setCopyInput(File copyInput) {
		this.copyInput = copyInput;
	}

	public boolean isCreateDetails() {
		return createDetails;
	}

	public void setCreateDetails(boolean createDetails) {
		this.createDetails = createDetails;
	}
	
	public Map<String, String> getNamespaceMappings() {
		if(this.namespaceMappingsStrings == null) {
			return null;
		}
		Map<String, String> result = new HashMap<String, String>();
		for (String aMappingString : this.namespaceMappingsStrings) {
			result.put(aMappingString.split(",")[0],aMappingString.split(",")[1]);
		}
		return result;
	}
	
}
