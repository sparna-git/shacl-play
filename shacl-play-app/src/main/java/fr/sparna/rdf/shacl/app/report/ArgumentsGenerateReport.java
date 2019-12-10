package fr.sparna.rdf.shacl.app.report;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Serialize an RDF validation report into one or more output files. The format of the output files is determined"
		+ "based on the file extension.")
public class ArgumentsGenerateReport {

	@Parameter(
			names = { "-i", "--input" },
			description = "Path to local RDF file or directory containing validation report. This can be repeated to read multiple input files.",
			required = true,
			variableArity = true
	)
	private List<File> input;

	@Parameter(
			names = { "-s", "--shapes" },
			description = "Path to local RDF file or directory containing shapes. This can be repeated to read multiple files.",
			variableArity = true
	)
	private List<File> shapes;
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Path to the output files (possibly multiple)",
			required = true,
			variableArity = true
	)
	private List<File> output;

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

}
