package fr.sparna.rdf.shacl.app.summary;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;

public class ArgumentsGenerateSummary {

	@Parameter(
			names = { "-i", "--input" },
			description = "Path to local RDF file containing validation report",
			required = true,
			variableArity = true
	)
	private List<File> input;

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

}
