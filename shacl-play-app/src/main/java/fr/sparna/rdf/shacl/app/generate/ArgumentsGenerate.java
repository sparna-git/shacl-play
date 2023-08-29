package fr.sparna.rdf.shacl.app.generate;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Validates some input RDF data against the provided SHACL file, and writes the output in one or more output file. The format "
		+ "of the output file(s) is determined based on the file extension.")
public class ArgumentsGenerate {

	@Parameter(
			names = { "-i", "--input" },
			description = "URL of Service Sparql (e.g. https//) or File",
			required = true,
			variableArity = true
	)
	private String input;
	
	
	@Parameter(
			names = { "-f", "--format" },
			description = "The format of the file is determined based:"
						+ "TURTLE"
						+ "RDF/XML"
						+ "N-Triples"
						+ "N-Quads"
						+ "N3"
						+ "TriG"
						+ "Json-LD"
						,
			required = true,
			variableArity = true
			)
	private String format;
	
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Path to an output file. The format of the file is determined based"
					+ " on the file extension : '*.ttl, *.rdf, *.n3, *.nq, *.nt, *.trig, *.jsonld' ",
			required = true,
			variableArity = true
	)
	private List<File> output;
	
	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}
	
	public String getformat() {
		return format;
	}

	public void setformat(String format) {
		this.format = format;
	}

	public List<File> getOutput() {
		return output;
	}

	public void setOutput(List<File> output) {
		this.output = output;
	}		
}