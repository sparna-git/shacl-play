package fr.sparna.rdf.shacl.app.excel;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Generates an Excel table from a dataset and the description of the table structure in Excel, provided in SHACL.")
public class ArgumentsExcel {

	@Parameter(
			names = { "-t", "--template" },
			description = "Template SHACL file that defines the table structure.",
			required = true
	)
	private File template;
	
	@Parameter(
			names = { "-i", "--input" },
			description = "Input data file that will populate the table. This can be repeated for multiple input files, and can point to a directory.",
			required = true,
			variableArity = true
	)
	private List<File> input;
	
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Path to the Excel output file that will be generated",
			required = true
	)
	private File output;

	@Parameter(
			names = { "-l", "--language" },
			description = "Code of the language to use to read titles and descriptions from the template."
					+ "This is mandatory unless the template uses a single language, in which case an attempt will be made to guess that unique language",
			required = false
	)
	private String language;
	
	

	public List<File> getInput() {
		return input;
	}

	public void setInput(List<File> input) {
		this.input = input;
	}

	public File getTemplate() {
		return template;
	}

	public void setTemplate(File template) {
		this.template = template;
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
	
}