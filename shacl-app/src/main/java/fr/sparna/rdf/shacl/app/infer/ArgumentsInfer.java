package fr.sparna.rdf.shacl.app.infer;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Infer data based on the provided SHACL file containing rules, and writes the output in the given output file")
public class ArgumentsInfer {

	@Parameter(
			names = { "-i", "--input" },
			description = "Path to a local RDF file",
			required = true,
			variableArity = true
	)
	private List<File> input;
	
	@Parameter(
			names = { "-s", "--shapes" },
			description = "Path to an RDF file containing the shapes definitions to use",
			required = true
	)
	private File shapes;

	@Parameter(
			names = { "-o", "--output" },
			description = "Path to the output file",
			required = true
	)
	private File output;

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

	public File getShapes() {
		return shapes;
	}

	public void setShapes(File shapes) {
		this.shapes = shapes;
	}
	
}
