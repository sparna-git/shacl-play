package fr.sparna.rdf.shacl.app.analyze;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Analyzes an input dataset against a provided SHACL file, and counts the number of instances/occurrences of each "
		+ "NodeShapes / PropertyShapes in the dataset. Outputs a void:Dataset entity with partitions holding the count.")
public class ArgumentsAnalyze {
	
	@Parameter(
			names = { "-e", "--endpoint" },
			description = "URL of SPARQL endpoint to analyze (e.g. https://dbpedia.org/sparql). Either endpoint or input needs to be specified."
	)
	private String endpoint;
	
	@Parameter(
			names = { "-i", "--input" },
			description = "Input data file to analyse. This can be repeated for multiple input files, and can point to a directory. Either endpoint or input needs to be specified. Excel files are also supported using xsls2rdf.",
			variableArity = true
	)
	private List<File> input;
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Path where the statistics model will be written. The format of the file is determined based"
					+ " on the file extension : '*.ttl, *.rdf, *.n3, *.nq, *.nt, *.trig, *.jsonld' ",
			required = true
	)
	private File output;

	@Parameter(
			names = { "-s", "--shapes" },
			description = "Path to the shapes file(s) against which the input data should be analyzed, or URL of the shapes file. Excel files are also supported using xsls2rdf.",
			variableArity = true,
			required = true
	)
	private List<String> shapes;
	
	@Parameter(
			names = { "-os", "--outputShapes" },
			description = "Path where the shapes graph will be written. The shapes graph will be enhanced by the statistical analysis process, with sh:in constraints when a few values have been found i nthe statistics. This is optional: if not provided, shapes will not be written back. The format of the file is determined based"
					+ " on the file extension : '*.ttl, *.rdf, *.n3, *.nq, *.nt, *.trig, *.jsonld' ",
			required = false
	)
	private File outputShapes;

	@Parameter(
			names = { "-sub", "--subClassOf" },
			description = "Use rdf:type/rdfs:subClassOf* (which is the correct SHACL semantic) when counting instances instead of just rdf:type",
			required = false
	)
	private Boolean subClassOf=false;


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

	public File getOutputShapes() {
		return outputShapes;
	}

	public void setOutputShapes(File outputShapes) {
		this.outputShapes = outputShapes;
	}

	public List<String> getShapes() {
		return shapes;
	}

	public void setShapes(List<String> shapes) {
		this.shapes = shapes;
	}

	public Boolean getSubClassOf() {
		return subClassOf;
	}

	public void setSubClassOf(Boolean subClassOf) {
		this.subClassOf = subClassOf;
	}	
	
}