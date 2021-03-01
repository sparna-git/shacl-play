package fr.sparna.rdf.shacl.app.doc;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import fr.sparna.cli.SpaceSplitter;

@Parameters(commandDescription = "Validates some input RDF data against the provided SHACL file, and writes the output in one or more output file. The format "
		+ "of the output file(s) is determined based on the file extension.")
public class ArgumentsDoc {

	@Parameter(
			names = { "-i", "--input" },
			description = "Path to a input Shapes file, or directory containing multiple files.",
			required = true,
			variableArity = true
	)
	private List<File> input;
	
	@Parameter(
			names = { "-w", "--owl" },
			description = "Path to a input OWL ontology file, or directory containing multiple files.",
			variableArity = true
	)
	private List<File> ontologies;

	@Parameter(
			names = { "-o", "--output" },
			description = "Path to an output file, with extension *.html",
			required = true
	)
	private File output;
	
	@Parameter(
			names = { "-l", "--language" },
			description = "Language code to generate documentation in, e.g. 'en', 'fr', etc.",
			required = true
	)
	private String language;

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

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<File> getOntologies() {
		return ontologies;
	}

	public void setOntologies(List<File> ontologies) {
		this.ontologies = ontologies;
	}
	
}
