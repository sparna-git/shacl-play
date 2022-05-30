package fr.sparna.rdf.shacl.app.shacl2sparql;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(
		commandDescription = "Generates SPARQL query for dataset generation based on a SHACL input file"
)
public class ArgumentsShacl2Sparql {
	
	@Parameter(
			names = { "-i", "--input" },
			description = "Path to the input SHACL file",
			required = true			
	)
	private File input;
	
	
	@Parameter(
			names = { "-to", "--targetsOverride" },
			description = "Path to an optional SHACL file overriding the targets of the main SHACL file. If provided, the sh:target predicates of the input file will be replaced by the sh:target from this file.",
			required = false			
	)
	private File targetsOverrideFile;
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Path to the output directory where SPARQL queries will be written",
			required = true
	)
	private File output;
	
	
	@Parameter(
			names = { "-t", "--type" },
			description = "Query type, \"normal\" or \"combine\"",
			required = true
	)
	private String type;
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public File getInput() {
		return input;
	}

	public void setInput(File input) {
		this.input = input;
	}

	public File getTargetsOverrideFile() {
		return targetsOverrideFile;
	}

	public void setTargetsOverrideFile(File targetsOverrideFile) {
		this.targetsOverrideFile = targetsOverrideFile;
	}

	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = output;
	}



	
	
}
