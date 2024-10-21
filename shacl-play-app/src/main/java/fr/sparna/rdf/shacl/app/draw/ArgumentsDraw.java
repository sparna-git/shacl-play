package fr.sparna.rdf.shacl.app.draw;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;


@Parameters(commandDescription = "Draw a UML diagram from a SHACL file")
public class ArgumentsDraw {

	@Parameter(
			names = { "-i", "--input" },
			description = "Path to a input Shapes file, or directory containing multiple files.",
			required = true,
			variableArity = true
	)
	private List<File> input;

	@Parameter(
			names = { "-o", "--output" },
			description = "Path to an output file, with extension *.png or *.svg or *.pdf or *.iuml. Can be repeated to generate multiple outputs.",
			required = true,
			variableArity = true
	)
	private List<File> output;

	@Parameter(
			names = { "-s", "--sub" },
			description = "Set this option to include subClassOf links in the diagram, from rdfs:subClassOf assertions. Defaults to false",
			required = false
	)
	private boolean includeSubclasses = false;
	
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

	public boolean isIncludeSubclasses() {
		return includeSubclasses;
	}

	public void setIncludeSubclasses(boolean includeSubclasses) {
		this.includeSubclasses = includeSubclasses;
	}
	
}
