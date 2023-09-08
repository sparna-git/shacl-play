package fr.sparna.rdf.shacl.app.generate;

import java.io.File;
import java.util.List;

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
}