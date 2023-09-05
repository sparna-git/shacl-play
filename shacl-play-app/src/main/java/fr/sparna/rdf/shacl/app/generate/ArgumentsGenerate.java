package fr.sparna.rdf.shacl.app.generate;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Generates the SHACL profile of an input knowledge graph")
public class ArgumentsGenerate {

	@Parameter(
			names = { "-i", "--input" },
			description = "URL of SPARQL endpoint (e.g. https://dbpedia.org/sparql)",
			required = true
	)
	private String input;		
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Path where the SHACL file will be written. The format of the file is determined based"
					+ " on the file extension : '*.ttl, *.rdf, *.n3, *.nq, *.nt, *.trig, *.jsonld' ",
			required = true
	)
	private File output;
	
	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = output;
	}		
}