package fr.sparna.rdf.shacl.app.jsonSchema;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Generate a JSON schema from a SHACL specification")
public class ArgumentsJsonSchema {

	@Parameter(
			names = { "-i", "--input" },
			description = "Path to a input Shapes file, or directory containing multiple files.",
			required = true,
			variableArity = true
	)
	private List<File> input;
	
	@Parameter(
			names = { "-u", "--uris" },
			description = "Node Shape or list of node shape URIs",
			required = true
	)
	private List<String> nodeShapes;

	@Parameter(
			names = { "-o", "--output" },
			description = "Path to an output file, with extension *.json",
			required = true
	)
	private File output;
	
	public List<File> getInput() {
		return input;
	}

	public void setInput(List<File> input) {
		this.input = input;
	}
	
	public List<String> getNodeShapes() {
		return nodeShapes;
	}

	public void setNodeShapes(List<String> nodeShapes) {
		this.nodeShapes = nodeShapes;
	}

	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = output;
	}
	
}
