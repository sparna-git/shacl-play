package fr.sparna.rdf.shacl.app.shacl2sparql;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(
		commandDescription = "Generates SPARQL queries for dataset generation based on a SHACL input file"
)
public class ArgumentsShacl2Sparql {
	
	@Parameter(
			names = { "-i", "--input" },
			description = "Path to the input SHACL file. Excel files are also supported using xsls2rdf.",
			required = true			
	)
	private List<File> input;
	
	
	@Parameter(
			names = { "-to", "--targetsOverride" },
			description = "Path to an optional SHACL file overriding the targets of the main SHACL file. If provided, the sh:target predicates of the input file will be replaced by the sh:target from this file.",
			required = false			
	)
	private List<File> targetsOverrideFile;
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Path to the output directory where SPARQL queries will be written",
			required = true
	)
	private File output;
	
	
	@Parameter(
			names = { "-u", "--unionQuery" },
			description = "Set this parameter that the generated query should use a set of UNION clauses rather than one query per paths",
			required = false
	)
	private boolean unionQuery = false;
	

	public boolean isUnionQuery() {
		return unionQuery;
	}

	public void setUnionQuery(boolean unionQuery) {
		this.unionQuery = unionQuery;
	}

	public List<File> getInput() {
		return input;
	}

	public void setInput(List<File> input) {
		this.input = input;
	}

	public List<File> getTargetsOverrideFile() {
		return targetsOverrideFile;
	}

	public void setTargetsOverrideFile(List<File> targetsOverrideFile) {
		this.targetsOverrideFile = targetsOverrideFile;
	}

	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = output;
	}



	
	
}
