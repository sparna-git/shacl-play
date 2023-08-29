package fr.sparna.rdf.shacl.app.excel;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Convert the fichier RDF/XML, TTL to excel file. ")
public class ArgumentsExcel {

	@Parameter(
			names = { "-i", "--input" },
			description = "Template File " + "The format of the file is determined based"
					+ " on the file extension : '*.ttl, *.rdf, *.n3, *.nq, *.nt, *.trig, *.jsonld' ",
			required = true,
			variableArity = true
	)
	private String inputTemplate;
	
	@Parameter(
			names = { "-s", "--source" },
			description = "Source File " + "The format of the file is determined based"
					+ " on the file extension : '*.ttl, *.rdf, *.n3, *.nq, *.nt, *.trig, *.jsonld' ",
			required = true,
			variableArity = true
	)
	private String inputSource;
	
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Path to an output file. The format of the file is determined based"
					+ " on the file extension : '*.ttl, *.rdf, *.n3, *.nq, *.nt, *.trig, *.jsonld' ",
			required = true,
			variableArity = true
	)
	private List<File> output;
	
	
	public String getInputTemplate() {
		return inputTemplate;
	}

	public void setInputTemplate(String inputTemplate) {
		this.inputTemplate = inputTemplate;
	}

	public String getInputSource() {
		return inputSource;
	}

	public void setInputSource(String inputSource) {
		this.inputSource = inputSource;
	}

	public List<File> getOutput() {
		return output;
	}

	public void setOutput(List<File> output) {
		this.output = output;
	}		
}