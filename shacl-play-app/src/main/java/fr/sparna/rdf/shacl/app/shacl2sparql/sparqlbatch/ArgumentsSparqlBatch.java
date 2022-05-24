package fr.sparna.rdf.shacl.app.shacl2sparql.sparqlbatch;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(
		commandDescription = "Executes SPARQL queries in batch and outputs single output file"
)
public class ArgumentsSparqlBatch {
	
	@Parameter(
			names = { "-d", "--dir" },
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
			names = { "-c", "--conn" },
			description = "Server connection link",
			required = true
	)
	private String conn;
	
	@Parameter(
			names = { "-p", "--prefix" },
			description = "Path to the input SHACL file to read prefixes from. If not provided, no prefix declarations will be set in the output file",
			required = false
	)
	private File prefix;
	
	public File getPrefix() {
		return prefix;
	}

	public void setPrefix(File prefix) {
		this.prefix = prefix;
	}

	public String getConn() {
		return conn;
	}

	public void setConn(String conn) {
		this.conn = conn;
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
