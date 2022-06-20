package fr.sparna.rdf.shacl.app.shacl2sparql.sparqlbatch;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(
		commandDescription = "Executes a set of SPARQL CONSTRUCT queries in batch against a SPARQL endpoint and outputs output in a single file"
)
public class ArgumentsSparqlBatch {
	
	@Parameter(
			names = { "-q", "--queries" },
			description = "Path to the input directory containing SPARQL CONSTRUCT files",
			required = true			
	)
	private File dir;
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Path to the output directory where the result file will be written",
			required = true
	)
	private File output;
	
	@Parameter(
			names = { "-e", "--endpoint" },
			description = "SPARQL endpoint URL to which the queries will be sent",
			required = true
	)
	private String sparqlEndpoint;
	
	@Parameter(
			names = { "-p", "--prefixesFile" },
			description = "Path to the input RDF (SHACL) file to read prefixes from. If not provided, no prefix declarations will be set in the output file",
			required = false
	)
	private File prefix;
	
	public File getPrefix() {
		return prefix;
	}

	public void setPrefix(File prefix) {
		this.prefix = prefix;
	}

	public String getSparqlEndpoint() {
		return sparqlEndpoint;
	}

	public void setSparqlEndpoint(String sparqlEndpoint) {
		this.sparqlEndpoint = sparqlEndpoint;
	}

	public File getDir() {
		return dir;
	}

	public void setDir(File dir) {
		this.dir = dir;
	}

	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = output;
	}



	
	
}
