package fr.sparna.rdf.shacl.app.jsonld;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Generates a JSON-LD context from a SHACL file.")
public class ArgumentsGenerateContext {

	@Parameter(
			names = { "-i", "--input" },
			description = "Path to a input Shapes file, or directory containing multiple files. Excel files are also supported using xsls2rdf.",
			required = true,
			variableArity = true
	)
	private List<File> input;

	@Parameter(
			names = { "-o", "--output" },
			description = "Path to an output file, with extension *.json",
			required = true
	)
	private File output;

	@Parameter(
		names = { "-W", "--watch" },
		description = "This option regenerate the output file when each time SHACL file is modified.",
		required = false
	)
	private Boolean watch = false;

	public Boolean getWatch() {
		return watch;
	}

	public void setWatch(Boolean watch) {
		this.watch = watch;
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
